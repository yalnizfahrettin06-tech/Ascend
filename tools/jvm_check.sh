#!/usr/bin/env bash
# Compiles the Android-free domain layer with plain kotlinc and runs its unit tests.
# Useful where the Android SDK cannot be downloaded. Usage: tools/jvm_check.sh <kotlin-home> <work-dir>
set -euo pipefail
KC="$1/bin/kotlinc"; W="$2"; ROOT="$(cd "$(dirname "$0")/.." && pwd)"
SRC="$ROOT/app/src/main/java/com/yalnizfahrettin/azim"
mkdir -p "$W/stub" "$W/out" "$W/lib"
for a in junit/junit/4.13.2/junit-4.13.2.jar org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar; do
  [ -f "$W/lib/$(basename $a)" ] || curl -sSfo "$W/lib/$(basename $a)" "https://repo1.maven.org/maven2/$a"
done
{ echo "package com.yalnizfahrettin.azim"; echo "object R { object drawable {"; i=1
  for f in "$ROOT"/app/src/main/res/drawable-nodpi/* "$ROOT"/app/src/main/res/drawable/*; do n=$(basename "$f"); echo "  const val ${n%%.*} = $i"; i=$((i+1)); done
  echo "} }"; } > "$W/stub/R.kt"
{ echo "package com.yalnizfahrettin.azim.ui"; echo "import com.yalnizfahrettin.azim.R"
  sed -n '/^enum class AtmosferGrubu/,/^@Composable/p' "$SRC/ui/Atmosfer.kt" | sed '$d'; } > "$W/stub/Atmosfer.kt"
DATA="KlasikVerisi Access AnaTemalar Diller LocaleCatalog LocaleDe LocaleFr LocaleIt LocalePt LocaleRu Languages IcerikVerisi Kategoriler Sozler UserState UserActions Policies ShortSeries RestartSeries DisciplineSeries JourneyCopy"
FILES=""; for d in $DATA; do FILES="$FILES $SRC/data/$d.kt"; done
CP="$W/lib/junit-4.13.2.jar:$W/lib/hamcrest-core-1.3.jar"
sed '/codecRoundTrips/,/^    }$/d; /emptyStoreUsesDevice/,/^    }$/d; /mutablePreferencesOf/d' "$ROOT/app/src/test/java/com/yalnizfahrettin/azim/UserStateTest.kt" > "$W/stub/UserStateTest.kt"
"$KC" -nowarn -cp "$CP" -d "$W/out" $FILES "$W"/stub/*.kt "$ROOT/app/src/test/java/com/yalnizfahrettin/azim/ReminderPolicyTest.kt"
java -cp "$W/out:$CP:$1/lib/kotlin-stdlib.jar" org.junit.runner.JUnitCore com.yalnizfahrettin.azim.ReminderPolicyTest com.yalnizfahrettin.azim.UserStateTest
