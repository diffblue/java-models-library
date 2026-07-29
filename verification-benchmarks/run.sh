#!/bin/bash
# Verification micro-benchmarks for the model library.
#
# Runs each probe method under JBMC against a given core-models.jar and
# checks the verdict against the expectation encoded in the method name
# (suffix _t = VERIFICATION SUCCESSFUL, _f = VERIFICATION FAILED), timing
# each run. Use to compare model variants (e.g. before/after a model
# change) for correctness AND verification performance.
#
# Usage: ./run.sh <path-to-jbmc> <path-to-core-models.jar> <path-to-cprover-api.jar>
set -u
JBMC=$1; MODELS=$2; API=$3
DIR=$(cd "$(dirname "$0")" && pwd)
WORK=$(mktemp -d)
trap 'rm -rf "$WORK"' EXIT
cp "$DIR"/src/*.java "$WORK"
(cd "$WORK" && javac --release 11 *.java)
fail=0
for cls in $(cd "$DIR/src" && ls *.java | sed 's/\.java//'); do
  for p in $(grep -oE "public static void [a-zA-Z0-9_]+_[tf]\(" "$DIR/src/$cls.java" \
             | sed 's/public static void //; s/($//; s/(//'); do
    exp=$([ "${p: -1}" = "t" ] && echo SUCCESSFUL || echo FAILED)
    S=$(date +%s.%N)
    R=$(cd "$WORK" && timeout 120 "$JBMC" "$cls" --function "$cls.$p" \
          --cp "$WORK:$MODELS:$API" --unwind 16 --max-nondet-array-length 4 2>&1 \
        | grep -aoE 'VERIFICATION (SUCCESSFUL|FAILED)' | tail -1 | awk '{print $2}')
    E=$(date +%s.%N)
    MARK=ok
    [ "${R:-TIMEOUT}" = "$exp" ] || { MARK="MISMATCH(expected $exp)"; fail=1; }
    printf "%-12s %-22s %-11s %-24s %6.1fs\n" \
      "$cls" "$p" "${R:-TIMEOUT}" "$MARK" "$(echo "$E - $S" | bc)"
  done
done
exit $fail
