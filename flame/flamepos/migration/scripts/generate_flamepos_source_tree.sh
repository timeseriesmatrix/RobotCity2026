#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
TOOLS_DIR="$ROOT_DIR/migration/tools"
WORK_DIR="$ROOT_DIR/migration/work"
BUILD_DIR="$ROOT_DIR/migration/build"
JAR_PATH="$ROOT_DIR/flamepos.jar"
CFR_JAR="$TOOLS_DIR/cfr-0.151.jar"

if [[ ! -f "$JAR_PATH" ]]; then
  echo "Missing $JAR_PATH"
  exit 1
fi

mkdir -p "$TOOLS_DIR" "$WORK_DIR" "$BUILD_DIR"

if [[ ! -f "$CFR_JAR" ]]; then
  echo "Downloading CFR decompiler..."
  curl -fL --retry 3 --retry-delay 2 \
    -o "$CFR_JAR" \
    "https://repo1.maven.org/maven2/org/benf/cfr/0.151/cfr-0.151.jar"
fi

TS="$(date +%Y%m%d_%H%M%S)"
DECOMP_DIR="$WORK_DIR/decompiled-src-$TS"
OUT_DIR="$BUILD_DIR/flamepos-source-$TS"

mkdir -p "$DECOMP_DIR" "$OUT_DIR/src" "$OUT_DIR/resources/META-INF"

echo "Decompiling flamepos.jar -> $DECOMP_DIR"
java -jar "$CFR_JAR" "$JAR_PATH" \
  --outputdir "$DECOMP_DIR" \
  --caseinsensitivefs true \
  --silent true

echo "Copying non-class resources from _jar_extract"
if command -v rsync >/dev/null 2>&1; then
  rsync -a --exclude='*.class' "$ROOT_DIR/_jar_extract/" "$OUT_DIR/src/"
  rsync -a "$DECOMP_DIR/" "$OUT_DIR/src/"
else
  cp -a "$ROOT_DIR/_jar_extract/." "$OUT_DIR/src/"
  find "$OUT_DIR/src" -type f -name '*.class' -delete
  cp -a "$DECOMP_DIR/." "$OUT_DIR/src/"
fi

if [[ -d "$OUT_DIR/src/com/floreantpos" ]]; then
  mv "$OUT_DIR/src/com/floreantpos" "$OUT_DIR/src/com/flamepos"
fi

if [[ -d "$OUT_DIR/src/META-INF/maven/org.floreantpos/floreantpos" ]]; then
  mkdir -p "$OUT_DIR/src/META-INF/maven/org.flamepos"
  mv "$OUT_DIR/src/META-INF/maven/org.floreantpos/floreantpos" \
     "$OUT_DIR/src/META-INF/maven/org.flamepos/flamepos"
fi

echo "Restoring build descriptors"
unzip -p "$JAR_PATH" META-INF/maven/org.floreantpos/floreantpos/pom.xml > "$OUT_DIR/pom.xml"
cp "$ROOT_DIR/_jar_extract/META-INF/mvn-assembly.xml" "$OUT_DIR/resources/META-INF/mvn-assembly.xml"

cat > "$OUT_DIR/flamepos.bat" <<'BAT'
@echo off

java -jar flamepos.jar
BAT

cat > "$OUT_DIR/FlamePOS-Setup.bat" <<'BAT'
@echo off

java -cp flamepos.jar com.flamepos.main.SetUpWindow
BAT

echo "Rebranding FLOREANT -> FLAME in source/resources"
while IFS= read -r -d '' FILE; do
  perl -0pi -e 's/FLOREANT/FLAME/g; s/Floreant/Flame/g; s/floreant/flame/g' "$FILE"
done < <(
  find "$OUT_DIR" -type f \
    \( -name '*.java' -o -name '*.xml' -o -name '*.properties' -o -name '*.form' -o -name '*.mf' -o -name '*.MF' -o -name '*.bat' -o -name '*.txt' -o -name '*.jrxml' -o -name '*.hbm.xml' \) \
    -print0
)

ln -sfn "$OUT_DIR" "$BUILD_DIR/flamepos-source-latest"

JAVA_COUNT="$(find "$OUT_DIR/src" -type f -name '*.java' | wc -l | tr -d ' ')"
LEGACY_COUNT="$( (rg -o -S 'com\\.floreantpos|org\\.floreantpos|floreantpos' "$OUT_DIR" 2>/dev/null || true) | wc -l | tr -d ' ')"

echo
echo "Generated source tree: $OUT_DIR"
echo "Java sources: $JAVA_COUNT"
echo "Remaining legacy tokens: $LEGACY_COUNT"
echo "Latest symlink: $BUILD_DIR/flamepos-source-latest"
