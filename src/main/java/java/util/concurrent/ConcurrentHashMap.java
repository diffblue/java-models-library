package java.util.concurrent;

import java.util.HashMap;
import java.util.Map;

/// Models lib: fakeout, no need for any of CHM's atomicity stuff so just use a plain ol'
/// HashMap.
public class ConcurrentHashMap<K, V> extends HashMap<K, V> implements ConcurrentMap<K, V> {

    public ConcurrentHashMap() {
        super();
    }

    public ConcurrentHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    public ConcurrentHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public ConcurrentHashMap(int initialCapacity, float loadFactor, int concurrencyLevel) {
        super(initialCapacity, loadFactor);
    }

    public ConcurrentHashMap(Map<? extends K,? extends V> m) {
        super(m);
    }

}
