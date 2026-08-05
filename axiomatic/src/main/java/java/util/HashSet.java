/*
 * Axiomatic JBMC model of java.util.HashSet.
 *
 * Trades the parallel-array + linear-scan implementation in
 * core-models.jar for a single hash-indexed boolean[] (encoded
 * as Object[] to avoid a primitive-array specialisation).
 *
 *   add(e):       set kv[h(e)] = PRESENT; if was absent, sz++
 *   contains(o):  return kv[h(o)] == PRESENT
 *   remove(o):    set kv[h(o)] = ABSENT; if was present, sz--
 *
 * where h(o) = (o == null ? 0 : o.hashCode()) & MASK.
 *
 * Each operation is O(1). Compare with core-models, where add()
 * + contains() + remove() each linear-scan up to size entries.
 *
 * Limitations:
 *   - Hash collisions silently merge entries.
 *   - Iteration order is not specified.
 *   - equals()/hashCode() are opaque (CProver.notModelled).
 */
package java.util;

import org.cprover.AxiomaticSetIterator;
import org.cprover.CProver;

public class HashSet<E> extends AbstractSet<E>
        implements Set<E>, Cloneable, java.io.Serializable {

    private static final long serialVersionUID = -5024744406713321676L;

    static final int CAPACITY = 1024;
    static final int MASK = CAPACITY - 1;

    /** Per-slot occupancy. ABSENT = false, PRESENT = true. */
    boolean[] kv;

    /** Ghost size kept in sync. */
    int sz;

    private void cproverInvariant() {
        CProver.assume(this.kv != null);
        CProver.assume(this.kv.length == CAPACITY);
        CProver.assume(this.sz >= 0);
        CProver.assume(this.sz <= CAPACITY);
    }

    private void initEmpty() {
        this.kv = new boolean[CAPACITY];
        // boolean[] is zero-initialised (false) by JBMC's
        // java_new_array lowering — no explicit fill loop.
        this.sz = 0;
    }

    public HashSet() {
        initEmpty();
    }

    public HashSet(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException(
                    "Illegal Capacity: " + initialCapacity);
        }
        // initialCapacity is ignored; CAPACITY is constant.
        initEmpty();
    }

    public HashSet(int initialCapacity, float loadFactor) {
        this(initialCapacity);
    }

    public HashSet(Collection<? extends E> c) {
        initEmpty();
        if (c instanceof HashSet) {
            HashSet<? extends E> hs = (HashSet<? extends E>) c;
            CProver.assume(hs.kv != null);
            CProver.assume(hs.kv.length == CAPACITY);
            for (int i = 0; i < CAPACITY; i++) {
                this.kv[i] = hs.kv[i];
            }
            this.sz = hs.sz;
        } else {
            int n = c.size();
            CProver.assume(n >= 0 && n <= CAPACITY);
            this.sz = n;
        }
    }

    /** Hash an element to a slot index. */
    private static int slot(Object o) {
        return (o == null ? 0 : o.hashCode()) & MASK;
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

    @Override
    public boolean contains(Object o) {
        cproverInvariant();
        return this.kv[slot(o)];
    }

    @Override
    public boolean add(E e) {
        cproverInvariant();
        int i = slot(e);
        if (this.kv[i]) {
            return false;
        }
        this.kv[i] = true;
        this.sz = this.sz + 1;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        cproverInvariant();
        int i = slot(o);
        if (!this.kv[i]) {
            return false;
        }
        this.kv[i] = false;
        this.sz = this.sz - 1;
        return true;
    }

    @Override
    public void clear() {
        cproverInvariant();
        // Allocate a fresh false-initialised array. Avoids an
        // explicit fill loop.
        this.kv = new boolean[CAPACITY];
        this.sz = 0;
    }

    @Override
    public Iterator<E> iterator() {
        // Skolemizing iterator: hasNext() returns nondet,
        // next() returns a nondet element constrained to be
        // in this set (`set.contains(elem)`). JBMC's BMC
        // explores all loop iteration counts up to the unwind
        // bound; the loop body must hold for every consistent
        // element the set contains.
        return new AxiomaticSetIterator<E>(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Set)) return false;
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
        return "HashSet(axiomatic)";
    }
}
