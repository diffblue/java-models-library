package org.cprover;

import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;

public final class AxiomaticKeySetView<K> extends AbstractSet<K> {

    private final HashMap<K, ?> map;

    public AxiomaticKeySetView(HashMap<K, ?> m) {
        this.map = m;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<K> iterator() {
        // Same skolemization pattern as AxiomaticEntryIterator,
        // but yielding only keys.
        return new Iterator<K>() {
            @Override
            public boolean hasNext() {
                return CProver.nondetBoolean();
            }

            @Override
            public K next() {
                K key = (K) CProver.nondetWithoutNullForNotModelled();
                CProver.assume(map.containsKey(key));
                return key;
            }
        };
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(Object o) {
        return map.containsKey((K) o);
    }
}
