/*
 * Read-only Map.Entry used by AxiomaticEntryIterator.
 * Standalone (not depending on core-models.jar's
 * org.cprover.CProverMapEntry) so the axiomatic jar can be
 * used without core-models on the classpath.
 */
package org.cprover;

import java.util.Map;

public final class AxiomaticMapEntry<K, V> implements Map.Entry<K, V> {

    private final K key;
    private final V value;

    public AxiomaticMapEntry(K key, V value) {
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
    public V setValue(V v) {
        throw new UnsupportedOperationException(
                "AxiomaticMapEntry is read-only");
    }
}
