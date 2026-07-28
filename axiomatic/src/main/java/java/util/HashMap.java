/*
 * Axiomatic JBMC model of java.util.HashMap.
 *
 * Trades the parallel-arrays + linear-scan implementation in
 * core-models.jar for a single hash-indexed Object[]. CBMC's
 * array theory translates the get / put operations into single
 * select / store SMT terms, eliminating the inner unwind loops
 * that dominate verification time for Map-heavy lemmas.
 *
 *   put(k, v):    kv[h(k)] = v;  if was null, sz++
 *   get(k):       return kv[h(k)]
 *   containsKey:  return kv[h(k)] != null
 *
 * where h(k) = (k == null ? 0 : k.hashCode()) & MASK.
 *
 * <h2>Limitations</h2>
 *
 * <ul>
 *   <li>{@code null} values are indistinguishable from
 *       "no entry". Lemmas that legitimately bind a key to
 *       {@code null} should not enable
 *       {@code --axiomatic-collections}.
 *   <li>Hash collisions silently overwrite. CAPACITY = 1024
 *       makes collisions vanishingly rare for the Integer- and
 *       small-pair-keyed maps in the lemma corpus.
 *   <li>Iteration order is unspecified.
 *   <li>{@link #equals(Object)} and {@link #hashCode()} are
 *       opaque ({@code CProver.notModelled}).
 *   <li>{@link #keySet()}, {@link #values()},
 *       {@link #entrySet()} return opaque nondet views.
 * </ul>
 *
 * <h2>Design rationale</h2>
 *
 * The class deliberately AVOIDS any explicit init loop. The
 * {@code kv} array is allocated by JBMC's
 * {@link remove_java_new} pass as a single zero-initialised
 * struct ASSIGN — no per-slot loop is emitted. Adding an
 * explicit fill loop here would force CBMC to unroll
 * {@code CAPACITY} times, defeating the speedup.
 */
package java.util;

import org.cprover.AxiomaticEntrySetView;
import org.cprover.AxiomaticKeySetView;
import org.cprover.AxiomaticValuesView;
import org.cprover.CProver;

public class HashMap<K, V> implements Map<K, V> {

    /**
     * Hash-table capacity. Power of two so {@code & MASK} is
     * a valid modulus. Set high enough that collisions are
     * effectively impossible for typed keys with good hashCode
     * distributions. Raising this number does NOT cost unwinds
     * in CBMC's array theory; the array is symbolic.
     */
    static final int CAPACITY = 1024;
    static final int MASK = CAPACITY - 1;

    /**
     * The hash table. Naturally null-initialised by JBMC's
     * {@code java_new_array} lowering — no explicit fill loop.
     */
    Object[] kv;

    /** Mapping count. Maintained explicitly by put / remove. */
    int sz;

    private void cproverInvariant() {
        CProver.assume(this.kv != null);
        CProver.assume(this.kv.length == CAPACITY);
        CProver.assume(this.sz >= 0);
        CProver.assume(this.sz <= CAPACITY);
    }

    public HashMap() {
        this.kv = new Object[CAPACITY];
        this.sz = 0;
    }

    public HashMap(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException(
                    "Illegal initial capacity: " + initialCapacity);
        }
        // initialCapacity is ignored; CAPACITY is constant.
        this.kv = new Object[CAPACITY];
        this.sz = 0;
    }

    public HashMap(int initialCapacity, float loadFactor) {
        this(initialCapacity);
    }

    public HashMap(Map<? extends K, ? extends V> m) {
        this.kv = new Object[CAPACITY];
        if (m instanceof HashMap) {
            @SuppressWarnings("unchecked")
            HashMap<? extends K, ? extends V> hm =
                (HashMap<? extends K, ? extends V>) m;
            CProver.assume(hm.kv != null);
            CProver.assume(hm.kv.length == CAPACITY);
            // Whole-array assignment: CBMC array theory
            // collapses this to a single SMT array term.
            this.kv = hm.kv;
            this.sz = hm.sz;
        } else {
            int n = m.size();
            CProver.assume(n >= 0 && n <= CAPACITY);
            this.sz = n;
        }
    }

    /** Hash a key to a slot index. */
    private static int slot(Object key) {
        return (key == null ? 0 : key.hashCode()) & MASK;
    }

    @Override
    public int size() {
        cproverInvariant();
        return this.sz;
    }

    @Override
    public boolean isEmpty() {
        cproverInvariant();
        return this.sz == 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public V get(Object key) {
        cproverInvariant();
        return (V) this.kv[slot(key)];
    }

    @Override
    public boolean containsKey(Object key) {
        cproverInvariant();
        return this.kv[slot(key)] != null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public V put(K key, V value) {
        cproverInvariant();
        int i = slot(key);
        Object old = this.kv[i];
        this.kv[i] = value;
        if (old == null) {
            this.sz = this.sz + 1;
            return null;
        }
        return (V) old;
    }

    @SuppressWarnings("unchecked")
    @Override
    public V remove(Object key) {
        cproverInvariant();
        int i = slot(key);
        Object old = this.kv[i];
        if (old == null) {
            return null;
        }
        this.kv[i] = null;
        this.sz = this.sz - 1;
        return (V) old;
    }

    @Override
    public void clear() {
        cproverInvariant();
        // Allocate a fresh null-initialised array. Avoids an
        // explicit fill loop (see Design rationale).
        this.kv = new Object[CAPACITY];
        this.sz = 0;
    }

    @Override
    public boolean containsValue(Object value) {
        cproverInvariant();
        // The only method whose cost scales with CAPACITY.
        // Avoid in hot specs.
        for (int i = 0; i < CAPACITY; i++) {
            Object v = this.kv[i];
            if (v == null) {
                continue;
            }
            if (value == null ? false : value.equals(v)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        CProver.notModelled();
    }

    @Override
    public Set<K> keySet() {
        // The skolemizing iterator yields fresh nondet keys
        // constrained to be in the map. We wrap it in a
        // ghost HashSet view; the only useful method on the
        // returned Set is iterator(), which delegates here.
        // If a user code touches other Set methods, they get
        // the axiomatic HashSet's behaviour applied to a
        // separate ghost set with no contents.
        return new AxiomaticKeySetView<K>(this);
    }

    @Override
    public Collection<V> values() {
        return new AxiomaticValuesView<V>(this);
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return new AxiomaticEntrySetView<K, V>(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Map)) return false;
        CProver.notModelled();
        return false;
    }

    @Override
    public int hashCode() {
        CProver.notModelled();
        return 0;
    }

    @Override
    public String toString() {
        return "HashMap(axiomatic)";
    }
}
