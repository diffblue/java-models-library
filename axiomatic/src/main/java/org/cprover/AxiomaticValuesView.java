package org.cprover;

import java.util.AbstractCollection;
import java.util.HashMap;
import java.util.Iterator;

public final class AxiomaticValuesView<V> extends AbstractCollection<V> {

    private final HashMap<?, V> map;

    public AxiomaticValuesView(HashMap<?, V> m) {
        this.map = m;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<V> iterator() {
        return new Iterator<V>() {
            @Override
            public boolean hasNext() {
                return CProver.nondetBoolean();
            }

            @Override
            public V next() {
                // Skolemize: pick a nondet KEY in the map,
                // return its corresponding value. We don't
                // expose the key publicly; callers see only
                // the value.
                Object key = CProver.nondetWithoutNullForNotModelled();
                CProver.assume(((HashMap<Object, V>) map).containsKey(key));
                return ((HashMap<Object, V>) map).get(key);
            }
        };
    }

    @Override
    public int size() {
        return map.size();
    }
}
