#!/usr/bin/env bash
set -euo pipefail

SCRIPT_NAME="$(basename "$0")"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"

REMOTE_HOST="${REMOTE_HOST:-192.168.1.186}"
REMOTE_PORT="${REMOTE_PORT:-5432}"
REMOTE_DB="${REMOTE_DB:-pos01wnf}"
REMOTE_USER="${REMOTE_USER:-liam}"
REMOTE_PGPASSWORD="${REMOTE_PGPASSWORD:-Agent47@113078}"

LOCAL_DB="${LOCAL_DB:-pos01wnf}"
LOCAL_ROLE="${LOCAL_ROLE:-liam}"
LOCAL_APP_PASSWORD="${LOCAL_APP_PASSWORD:-Knight@113078}"
LOCAL_VERIFY_HOST="${LOCAL_VERIFY_HOST:-192.168.1.18}"
LOCAL_VERIFY_PORT="${LOCAL_VERIFY_PORT:-5432}"

BACKUP_DIR="${BACKUP_DIR:-$HOME/db-backups}"
PG18_ROOT="${PG18_ROOT:-$HOME/opt/pgsql18/extract}"
PG18_DEBS_DIR="${PG18_DEBS_DIR:-$HOME/opt/pgsql18/debs}"
PG18_DIST="${PG18_DIST:-noble-pgdg}"
PG18_INDEX_URL="${PG18_INDEX_URL:-https://apt.postgresql.org/pub/repos/apt/dists/${PG18_DIST}/main/binary-amd64/Packages.gz}"
PG18_BASE_URL="${PG18_BASE_URL:-https://apt.postgresql.org/pub/repos/apt}"
PG18_LIB_DIR="${PG18_ROOT}/usr/lib/x86_64-linux-gnu"
PG18_BIN_DIR="${PG18_ROOT}/usr/lib/postgresql/18/bin"

usage() {
  cat <<EOF
Usage: ${SCRIPT_NAME} [--help]

Refresh this server's local PostgreSQL database from the live POS database.

Default source:
  ${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_PORT}/${REMOTE_DB}

Default local target:
  ${LOCAL_DB} on this server

What the script does:
  1. Back up the current local ${LOCAL_DB} to ${BACKUP_DIR}
  2. Download PostgreSQL 18 client tools locally if they are missing
  3. Dump ${REMOTE_DB} from ${REMOTE_HOST}
  4. Drop and recreate the local ${LOCAL_DB}
  5. Restore the fresh dump into the local server
  6. Reset the local ${LOCAL_ROLE} password for flamepos
  7. Verify the restored database and network login

Environment overrides:
  REMOTE_HOST, REMOTE_PORT, REMOTE_DB, REMOTE_USER, REMOTE_PGPASSWORD
  LOCAL_DB, LOCAL_ROLE, LOCAL_APP_PASSWORD, LOCAL_VERIFY_HOST, LOCAL_VERIFY_PORT
  BACKUP_DIR, PG18_ROOT, PG18_DEBS_DIR, PG18_DIST, PG18_INDEX_URL, PG18_BASE_URL

Examples:
  ${SCRIPT_NAME}
  REMOTE_PGPASSWORD='new-password' ${SCRIPT_NAME}
  LOCAL_VERIFY_HOST='100.81.119.65' ${SCRIPT_NAME}
EOF
}

log() {
  printf '[%s] %s\n' "$(date '+%F %T')" "$*"
}

warn() {
  printf '[%s] WARNING: %s\n' "$(date '+%F %T')" "$*" >&2
}

die() {
  printf '[%s] ERROR: %s\n' "$(date '+%F %T')" "$*" >&2
  exit 1
}

require_cmd() {
  command -v "$1" >/dev/null 2>&1 || die "missing required command: $1"
}

remote_conninfo() {
  printf 'host=%s port=%s dbname=%s user=%s sslmode=disable' \
    "$REMOTE_HOST" "$REMOTE_PORT" "$REMOTE_DB" "$REMOTE_USER"
}

database_exists() {
  psql -d postgres -Atqc "select 1 from pg_database where datname = '${1}'" | grep -q '^1$'
}

pg18_package_filename() {
  local package_name="$1"

  wget -qO- "$PG18_INDEX_URL" \
    | gzip -d \
    | awk -v package_name="$package_name" '
      $0 == "Package: " package_name { found = 1; next }
      found && /^Filename: / { print $2; exit }
      found && /^Package: / { exit 1 }
    '
}

ensure_pg18_client() {
  if [[ -x "${PG18_BIN_DIR}/pg_dump" && -x "${PG18_BIN_DIR}/pg_restore" ]]; then
    return
  fi

  require_cmd wget
  require_cmd gzip
  require_cmd dpkg-deb

  mkdir -p "$PG18_DEBS_DIR" "$PG18_ROOT"

  local package_name filename package_path target_path
  for package_name in postgresql-client-common libpq5 postgresql-client-18; do
    filename="$(pg18_package_filename "$package_name")"
    [[ -n "$filename" ]] || die "could not resolve package metadata for ${package_name}"
    package_path="${PG18_BASE_URL}/${filename}"
    target_path="${PG18_DEBS_DIR}/$(basename "$filename")"
    if [[ ! -f "$target_path" ]]; then
      log "Downloading $(basename "$filename")"
      wget -q -O "$target_path" "$package_path"
    fi
    dpkg-deb -x "$target_path" "$PG18_ROOT"
  done

  [[ -x "${PG18_BIN_DIR}/pg_dump" ]] || die "PostgreSQL 18 pg_dump is still unavailable after bootstrap"
}

fingerprint_sql() {
  cat <<'SQL'
select count(*) from information_schema.tables where table_schema='public';
select md5(string_agg(table_name||':'||row_count, '|' order by table_name))
from (
  select table_name,
         (xpath('/row/c/text()', query_to_xml(format('select count(*) as c from %I.%I', table_schema, table_name), false, true, '')))[1]::text::bigint as row_count
  from information_schema.tables
  where table_schema='public' and table_type='BASE TABLE'
) s;
select label||'|'||row_count
from (
  select 1 as sort_key, 'ticket' as label, count(*) as row_count from ticket
  union all
  select 2 as sort_key, 'ticket_item' as label, count(*) as row_count from ticket_item
  union all
  select 3 as sort_key, 'transactions' as label, count(*) as row_count from transactions
  union all
  select 4 as sort_key, 'action_history' as label, count(*) as row_count from action_history
) key_counts
order by sort_key;
SQL
}

main() {
  if [[ "${1:-}" == "--help" || "${1:-}" == "-h" ]]; then
    usage
    exit 0
  fi

  if [[ $# -gt 0 ]]; then
    die "unknown argument: $1"
  fi

  umask 077

  require_cmd psql
  require_cmd pg_dump
  require_cmd dropdb
  require_cmd createdb
  require_cmd sed
  require_cmd awk
  require_cmd grep
  require_cmd cmp

  ensure_pg18_client

  mkdir -p "$BACKUP_DIR"

  local timestamp local_backup remote_dump restore_log remote_fp_file local_fp_file
  timestamp="$(date +%Y%m%d-%H%M%S)"
  local_backup="${BACKUP_DIR}/${LOCAL_DB}-local-before-sync-${timestamp}.dump"
  remote_dump="${BACKUP_DIR}/${LOCAL_DB}-remote-${REMOTE_HOST}-${timestamp}.dump"
  restore_log="${BACKUP_DIR}/${LOCAL_DB}-restore-${timestamp}.log"
  remote_fp_file="${BACKUP_DIR}/${LOCAL_DB}-remote-fingerprint-${timestamp}.txt"
  local_fp_file="${BACKUP_DIR}/${LOCAL_DB}-local-fingerprint-${timestamp}.txt"

  if database_exists "$LOCAL_DB"; then
    log "Backing up local ${LOCAL_DB} to ${local_backup}"
    pg_dump -Fc -d "$LOCAL_DB" -f "$local_backup"
  else
    warn "Local database ${LOCAL_DB} does not exist yet, skipping pre-sync backup"
  fi

  log "Dumping ${REMOTE_DB} from ${REMOTE_HOST}:${REMOTE_PORT} to ${remote_dump}"
  LD_LIBRARY_PATH="$PG18_LIB_DIR" \
    PGPASSWORD="$REMOTE_PGPASSWORD" \
    "${PG18_BIN_DIR}/pg_dump" \
      -Fc \
      -h "$REMOTE_HOST" \
      -p "$REMOTE_PORT" \
      -U "$REMOTE_USER" \
      -d "$REMOTE_DB" \
      -f "$remote_dump"

  log "Refreshing local ${LOCAL_DB}"
  psql -d postgres -v ON_ERROR_STOP=1 -c \
    "select pg_terminate_backend(pid) from pg_stat_activity where datname='${LOCAL_DB}' and pid <> pg_backend_pid();"
  dropdb --if-exists "$LOCAL_DB"
  createdb -O "$LOCAL_ROLE" -E UTF8 -T template0 --lc-collate='en_US.UTF-8' --lc-ctype='en_US.UTF-8' "$LOCAL_DB"

  LD_LIBRARY_PATH="$PG18_LIB_DIR" \
    "${PG18_BIN_DIR}/pg_restore" -f - "$remote_dump" \
    | sed '/^SET transaction_timeout = 0;$/d' \
    | psql -v ON_ERROR_STOP=1 -d "$LOCAL_DB" >"$restore_log"

  log "Ensuring local PostgreSQL role password matches flamepos config"
  psql -d postgres -v ON_ERROR_STOP=1 -c \
    "ALTER ROLE \"${LOCAL_ROLE}\" PASSWORD '${LOCAL_APP_PASSWORD}';"

  log "Collecting post-restore fingerprint from local ${LOCAL_DB}"
  psql -d "$LOCAL_DB" -Atqc "$(fingerprint_sql)" >"$local_fp_file"

  log "Collecting current fingerprint from live source ${REMOTE_HOST}"
  PGPASSWORD="$REMOTE_PGPASSWORD" \
    psql "$(remote_conninfo)" -Atqc "$(fingerprint_sql)" >"$remote_fp_file"

  log "Verifying flamepos-style network login to ${LOCAL_VERIFY_HOST}:${LOCAL_VERIFY_PORT}"
  PGPASSWORD="$LOCAL_APP_PASSWORD" \
    psql "host=${LOCAL_VERIFY_HOST} port=${LOCAL_VERIFY_PORT} dbname=${LOCAL_DB} user=${LOCAL_ROLE} sslmode=disable" \
      -v ON_ERROR_STOP=1 \
      -Atqc "select current_database(), current_user, inet_server_addr(), inet_client_addr();"

  if cmp -s "$local_fp_file" "$remote_fp_file"; then
    log "Sync verified: local and current remote fingerprints match"
  else
    warn "Local restore completed, but current remote counts differ from local."
    warn "This usually means the live POS database changed again after or during the dump."
    warn "Compare these files if you need detail:"
    warn "  local:  ${local_fp_file}"
    warn "  remote: ${remote_fp_file}"
  fi

  log "Done"
  log "Local backup: ${local_backup}"
  log "Remote dump:  ${remote_dump}"
  log "Restore log:  ${restore_log}"
  log "Set flamepos on 192.168.1.186 to jdbc:postgresql://192.168.1.18:5432/${LOCAL_DB}"
}

main "$@"
