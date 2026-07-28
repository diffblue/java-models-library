/*
 * Skolemizing iterator for axiomatic HashMap.entrySet().
 *
 * Each {@code next()} yields a synthetic
 * {@code Map.Entry<K, V>} whose key is fresh nondet and whose
 * value is the result of {@code map.get(key)} — but with a
 * `CProver.assume(map.containsKey(key))` so the SAT solver
 * can only return entries that the map "has".
 *
 * {@code hasNext()} returns a fresh nondet boolean each call,
 * so JBMC's BMC explores all loop iterations from 0 to the
 * unwind bound. A property
 *
 *   for (Map.Entry e : map.entrySet()) {{ assert P(e); }}
 *
 * is then checked under each iteration count: the loop body
 * must hold for every consistent (key, value) pair the map
 * contains. This is the standard skolemization of
 * forall-over-entries that BMC can verify modulo unwind
 * depth.
 *
 * Without this iterator, the for-each pattern hits an
 * UnsupportedOperationException from the throwing entrySet()
 * — see the class javadoc on
 * {@link java.util.HashMap#entrySet()} (axiomatic version).
 */
package org.cprover;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class AxiomaticEntryIterator<K, V>
        implements Iterator<Map.Entry<K, V>> {

    private HashMap<K, V> map;

    public AxiomaticEntryIterator(HashMap<K, V> m) {
        this.map = m;
    }

    @Override
    public boolean hasNext() {
        return CProver.nondetBoolean();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map.Entry<K, V> next() {
        K key = (K) CProver.nondetWithoutNullForNotModelled();
        // Skolemization: only return entries the map actually
        // contains. Under axiomatic encoding this is
        // _kv[pack(map, key_value)] != NULL.
        CProver.assume(map.containsKey(key));
        V value = map.get(key);
        return new AxiomaticMapEntry<>(key, value);
    }

    @Override
    public void remove() {
        CProver.notModelled();
    }
}
