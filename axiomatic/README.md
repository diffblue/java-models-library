# Axiomatic Models Library — opt-in JBMC collections

This module provides an alternative implementation of
`java.util.HashMap`, `java.util.HashSet`, and `java.util.LinkedHashMap`
for use with JBMC. Compared to `core-models.jar`, these implementations
are **axiomatic**: a HashMap is a single direct-mapped `Object[CAPACITY]`
table (`h(k) = hashCode & MASK`) with `CProver.assume`-based invariants,
designed so that lemma-style verification over large or symbolic maps
avoids the core models' per-entry storage and scan loops.

## Selection mechanism (validated)

Put `axiomatic-models.jar` **before** `core-models.jar` on the JBMC
classpath; JBMC's class loader resolves each class to its first
occurrence, so the axiomatic classes shadow the core ones:

```bash
jbmc --function MyClass.lemma \
  -cp target/classes:axiomatic-models.jar:core-models.jar:cprover-api.jar \
  MyClass
```

Verified against a stock JBMC with the sibling
`verification-benchmarks/run.sh` (which accepts a colon-joined models
path, so a core-vs-axiomatic comparison is one command per jar order).

## Semantic trade-offs — READ BEFORE USE

These models deliberately deviate from the JDK in ways that are fine
for their intended niche (contracts/lemma verification over maps used
as key-value stores) and WRONG for general-purpose verification:

* **Null values are not representable**: `put(k, null)` is
  indistinguishable from absence (`containsKey` tests `kv[slot] != null`).
  Code storing null values gets wrong definite answers.
* **Hash collisions silently overwrite**: two present keys with
  `hashCode() & MASK` equal corrupt each other. Wrong definite answers,
  not sound nondeterminism.
* **Views are ghost objects**: `keySet()`/`values()`/`entrySet()`
  return skolemizing views whose iterators yield nondet elements
  constrained to be in the map. Properties over concrete iteration
  contents or order can become VACUOUSLY provable — the sibling
  benchmark suite demonstrates a false property "verifying" this way
  (`OrderProbes.removeReorders_f`). Do not use these models when the
  target code iterates and the property depends on the iteration.
* **LinkedHashMap does not model its specified iteration order.**

For general-purpose verification use `core-models.jar` alone; its
HashMap/HashSet/LinkedHashMap are JDK-conformant (including null
values, collision handling, and iteration order).
