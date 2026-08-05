# Verification micro-benchmarks

Small JBMC probe programs for the model library, checking model
CORRECTNESS (each probe encodes its expected verdict in the method
name: `_t` = VERIFICATION SUCCESSFUL, `_f` = VERIFICATION FAILED)
and measuring verification TIME per probe.

Use them to compare model variants -- before/after a model change, or
two competing implementations of the same class:

    ./run.sh <jbmc> <core-models.jar> <cprover-api.jar>

The runner exits non-zero on any verdict mismatch, so it can serve as
a CI gate once a JBMC binary is available to the workflow.

Every `_f` probe guards against vacuity: if a model change makes
paths spuriously infeasible (e.g. an over-eager `CProver.assume`), the
false property "verifies" and the mismatch is flagged.

Probes deliberately exercise the interaction surface that has bitten
before: boxed-primitive `<clinit>` (any autoboxing routes through
`Class.getPrimitiveClass`), enhanced-for iteration, map views, null
keys/values, capacity growth, and nondet (symbolic) collection
parameters.
