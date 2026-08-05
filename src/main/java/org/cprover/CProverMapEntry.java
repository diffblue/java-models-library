package org.cprover;

import java.util.Map;

/**
 * Snapshot {@link Map.Entry} implementation for map models.
 *
 * <p>Intended usage: a map model that stores keys and values in
 * parallel {@code Object[]} arrays and needs {@code entrySet()} to
 * expose a {@code Set<Map.Entry<K, V>>} constructs one entry per
 * (keys[i], values[i]) pair. That gives the model a deterministic,
 * iterable view without depending on the JDK's
 * {@code AbstractMap.SimpleEntry} (whose body, like everything
 * else in {@code AbstractMap}, JBMC stubs out).
 *
 * <p>This entry is deliberately {@code final} and read-only:
 * {@code setValue} calls {@link CProver#notModelled()}. Most
 * production map iteration doesn't mutate via the entry; a target
 * that needs {@code setValue} calls for a separate mutable entry
 * class holding a write-through reference to the map's storage,
 * mirroring the mutable-iterator path described in
 * {@link CProverArrayLikeIterator}.
 *
 * @param <K> key type.
 * @param <V> value type.
 */
public final class CProverMapEntry<K, V> implements Map.Entry<K, V> {

    private final K key;
    private final V value;

    public CProverMapEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V newValue) {
        // Snapshot entries don't write back to the underlying
        // map. Targets that need entry-driven mutation can
        // subclass; the common read-only iteration case is fine.
        CProver.notModelled();
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Map.Entry)) {
            return false;
        }
        Map.Entry<?, ?> other = (Map.Entry<?, ?>) o;
        Object ok = other.getKey();
        Object ov = other.getValue();
        boolean keyEq = (key == null) ? (ok == null) : key.equals(ok);
        boolean valEq = (value == null) ? (ov == null) : value.equals(ov);
        return keyEq && valEq;
    }

    @Override
    public int hashCode() {
        // Java's Map.Entry contract: XOR of key.hashCode() and
        // value.hashCode(), with null hashCode treated as 0.
        int kh = (key == null) ? 0 : key.hashCode();
        int vh = (value == null) ? 0 : value.hashCode();
        return kh ^ vh;
    }
}
