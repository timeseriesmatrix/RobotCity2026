#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SOURCE_DIR="${1:-$ROOT_DIR/migration/build/flamepos-source-latest}"

if [[ ! -d "$SOURCE_DIR" ]]; then
  echo "Source directory not found: $SOURCE_DIR"
  echo "Run migration/scripts/generate_flamepos_source_tree.sh first."
  exit 1
fi

if ! command -v javac >/dev/null 2>&1; then
  echo "javac is required to rebuild. Install a JDK and retry."
  exit 2
fi

if ! command -v mvn >/dev/null 2>&1; then
  echo "mvn is required to rebuild. Install Maven and retry."
  exit 3
fi

echo "Building from: $SOURCE_DIR"
cd "$SOURCE_DIR"

mvn -DskipTests package

BUILT_JAR="$(find target -maxdepth 1 -type f -name 'flamepos*.jar' ! -name '*sources*' ! -name '*javadoc*' | head -n 1)"

if [[ -z "$BUILT_JAR" ]]; then
  echo "Build finished, but no flamepos jar found under target/."
  exit 4
fi

cp -f "$BUILT_JAR" "$ROOT_DIR/flamepos.jar"
echo "Copied rebuilt jar to: $ROOT_DIR/flamepos.jar"
