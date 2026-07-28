/*
 * Set view returned by {@code HashMap.entrySet()} under the
 * axiomatic encoding. The only method the for-each idiom
 * touches is {@code iterator()}; everything else delegates
 * to nondet or throws.
 */
package org.cprover;

import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class AxiomaticEntrySetView<K, V>
        extends AbstractSet<Map.Entry<K, V>> {

    private final HashMap<K, V> map;

    public AxiomaticEntrySetView(HashMap<K, V> m) {
        this.map = m;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        return new AxiomaticEntryIterator<>(map);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean contains(Object o) {
        // Defer to map.containsKey when the operand is an
        // Entry. Otherwise nondet.
        if(o instanceof Map.Entry<?, ?>) {
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
            return map.containsKey(e.getKey());
        }
        return CProver.nondetBoolean();
    }
}
