/*
 * Axiomatic JBMC model of java.util.LinkedHashMap.
 *
 * Inherits HashMap's axiomatic encoding. Insertion-order
 * preservation is NOT modelled — see "Limitations" below.
 */
package java.util;

public class LinkedHashMap<K, V> extends HashMap<K, V>
        implements Map<K, V> {

    private static final long serialVersionUID = 3801124242820219131L;

    public LinkedHashMap() {
        super();
    }

    public LinkedHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    public LinkedHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity);
    }

    /**
     * <strong>Limitation</strong>: the axiomatic encoding does
     * not preserve insertion order or implement access-order
     * (LRU) semantics. {@code accessOrder} is silently ignored.
     * Lemmas whose correctness depends on iteration order MUST
     * NOT use this model — fall back to core-models.jar by
     * leaving {@code --axiomatic-collections} off.
     */
    public LinkedHashMap(
            int initialCapacity, float loadFactor, boolean accessOrder) {
        super(initialCapacity);
    }

    public LinkedHashMap(Map<? extends K, ? extends V> m) {
        super(m);
    }
}
