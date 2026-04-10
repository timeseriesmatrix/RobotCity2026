# FLAMEPOS Full Migration Plan (com.flamepos)

This project snapshot is binary-first (`flamepos.jar` + extracted `.class` files).  
To create a real `com.flamepos` package tree, we must reconstruct a buildable source tree and then rebuild.

## Current status

1. Branding text migration completed in runtime resources/UI files.
2. Automated source reconstruction script added:
   - `migration/scripts/generate_flamepos_source_tree.sh`
3. Automated rebuild script added:
   - `migration/scripts/rebuild_flamepos.sh`

## Phase 1: Generate source tree (completed tooling)

Run:

```bash
bash migration/scripts/generate_flamepos_source_tree.sh
```

What it does:

1. Decompiles `flamepos.jar` with CFR (`migration/tools/cfr-0.151.jar`).
2. Combines decompiled `.java` with non-class resources from `_jar_extract`.
3. Renames package path from `com/floreantpos` to `com/flamepos`.
4. Applies global token migration:
   - `FLOREANT -> FLAME`
   - `Floreant -> Flame`
   - `floreant -> flame`
5. Writes source project to:
   - `migration/build/flamepos-source-<timestamp>`
   - `migration/build/flamepos-source-latest` (symlink)

## Phase 2: Build prerequisites

Required tools:

1. JDK with `javac`
2. Maven (`mvn`)

Check:

```bash
javac -version
mvn -version
```

## Phase 3: Rebuild

Run:

```bash
bash migration/scripts/rebuild_flamepos.sh
```

Expected output:

1. Maven package build in generated source directory
2. Rebuilt jar copied to:
   - `flamepos.jar`

## Phase 4: Runtime smoke test

1. Update launcher scripts to use `flamepos.jar`.
2. Start application.
3. Validate:
   - Login screen text/branding
   - Database connection and entity mapping startup
   - Receipt printing
   - Report generation

## Phase 5: Hardening after first successful build

1. Replace decompiled sources with canonical upstream sources if available.
2. Split UI branding migration from internal identifiers where needed.
3. Add CI checks:
   - compile
   - startup smoke tests
   - database connectivity check
