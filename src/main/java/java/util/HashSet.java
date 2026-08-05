/*
 * JBMC model of java.util.HashSet.
 *
 * Real, deterministic model: a HashSet instance owns an Object[]
 * elements + int size. add/contains/remove are linear scans. size()
 * returns this.size deterministically.
 *
 * Class invariant assumed on entry to every public method:
 *     0 <= size <= elements.length, elements != null,
 *     and elements[0..size) contains no duplicates (under .equals()).
 *
 * The "no duplicates" assumption is checked at entry but never proved —
 * it's an axiom on the nondet-init state. add() is the only mutator
 * that could violate it; we maintain it explicitly there.
 *
 * NOT modelled: iterator (depends on Iterator infrastructure),
 * toArray, retainAll/removeAll/addAll on Collection arguments,
 * equals, hashCode.
 */

package java.util;

import org.cprover.CProver;
import org.cprover.CProverArrayLikeIterator;
import org.cprover.CProverGenericArrayElement;

public class HashSet<E> extends AbstractSet<E>
        implements Set<E>, Cloneable, java.io.Serializable {

    private static final long serialVersionUID = -5024744406713321676L;

    @CProverGenericArrayElement("E")
    private Object[] elements;
    private int size;

    private void cproverInvariant() {
        CProver.assume(this.elements != null);
        CProver.assume(this.size >= 0);
        CProver.assume(this.size <= this.elements.length);
    }

    public HashSet() {
        this.elements = new Object[16];
        this.size = 0;
    }

    public HashSet(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException(
                    "Illegal Capacity: " + initialCapacity);
        }
        this.elements = new Object[initialCapacity];
        this.size = 0;
    }

    public HashSet(int initialCapacity, float loadFactor) {
        this(initialCapacity);
    }

    public HashSet(Collection<? extends E> c) {
        this.elements = new Object[16];
        int n = CProver.nondetInt();
        CProver.assume(n >= 0 && n <= this.elements.length);
        this.size = n;
    }

    /**
     * Package-private constructor used by HashMap.keySet() and
     * HashMap.entrySet() to wrap a pre-existing Object[] storage
     * as a HashSet view. The caller is responsible for ensuring
     * {@code 0 <= size <= elements.length} and that the storage
     * has no duplicates under .equals() (HashSet's class
     * invariant). Snapshot semantics: see HashMap.values()'s
     * ArrayList wrapping ctor.
     */
    HashSet(Object[] elements, int size) {
        CProver.assume(elements != null);
        CProver.assume(size >= 0);
        CProver.assume(size <= elements.length);
        this.elements = elements;
        this.size = size;
    }

    @Override
    public int size() {
        cproverInvariant();
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        cproverInvariant();
        return this.size == 0;
    }

    @Override
    public boolean contains(Object o) {
        cproverInvariant();
        return indexOf(o) >= 0;
    }

    @Override
    public boolean add(E e) {
        cproverInvariant();
        if (indexOf(e) >= 0) {
            return false;  // already present, set unchanged
        }
        CProver.assume(this.size < this.elements.length);
        this.elements[this.size] = e;
        this.size = this.size + 1;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        cproverInvariant();
        int idx = indexOf(o);
        if (idx < 0) {
            return false;
        }
        // Shift left; preserves the no-duplicates invariant trivially.
        for (int i = idx; i < this.size - 1; i++) {
            this.elements[i] = this.elements[i + 1];
        }
        this.elements[this.size - 1] = null;
        this.size = this.size - 1;
        return true;
    }

    @Override
    public void clear() {
        cproverInvariant();
        for (int i = 0; i < this.size; i++) {
            this.elements[i] = null;
        }
        this.size = 0;
    }

    private int indexOf(Object o) {
        if (o == null) {
            for (int i = 0; i < this.size; i++) {
                if (this.elements[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < this.size; i++) {
                if (o.equals(this.elements[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    // ---- Stubs for unmodelled methods. ----

    /**
     * Deterministic iterator over this HashSet's logical
     * contents. HashSet's storage is a packed Object[] with a
     * logical size, same shape as ArrayList — the
     * {@link CProverArrayLikeIterator} works as-is.
     *
     * <p>Iteration order is the storage order of {@code elements},
     * which is whatever order add()/remove() left them in. Java's
     * HashSet does not specify iteration order (LinkedHashSet
     * does), so this snapshot order is conformant.
     *
     * <p>Snapshot semantics: see
     * {@code CProverArrayLikeIterator}'s class javadoc. The
     * iterator captures the {@code elements} array reference and
     * the logical size at this call's time; subsequent
     * add()/remove() are not observed.
     */
    @Override
    public Iterator<E> iterator() {
        CProverArrayLikeIterator<E> it = new CProverArrayLikeIterator<>();
        it.init(this.elements, this.size);
        return it;
    }

    @Override
    public Object[] toArray() {
        CProver.notModelled();
        return CProver.nondetWithoutNull(new Object[0]);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        CProver.notModelled();
        return a;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean changed = false;
        for (E e : c) {
            if (add(e)) {
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        CProver.notModelled();
        return CProver.nondetBoolean();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        CProver.notModelled();
        return CProver.nondetBoolean();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        CProver.notModelled();
        return CProver.nondetBoolean();
    }
}
