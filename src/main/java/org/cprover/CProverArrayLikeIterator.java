package org.cprover;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Generic snapshot-style iterator over an {@code Object[]}-backed
 * collection. See class javadoc below for the design rationale.
 *
 * Implementation note on field initialization. The fields
 * {@code data}, {@code size}, and {@code idx} are initialized
 * via direct assignment in {@code init(...)}, NOT via constructor
 * parameters. This avoids a JBMC --validate-trace abort observed
 * on jbmc/regression/jbmc/symex_complexity/loopBlacklist when the
 * iterator was allocated via {@code new CProverArrayLikeIterator<>
 * (data, size)} as a constructor expression: the constructor's
 * SSA equation produced a nil-typed RHS that
 * {@code java_trace_validation.cpp:check_rhs_assumptions} couldn't
 * classify. Allocating an empty instance with the no-arg
 * constructor and then calling {@code init(...)} produces a flat
 * sequence of three field writes that the trace validator
 * accepts.
 *
 * <p>Intended usage: a collection model that stores its elements in
 * a contiguous {@code Object[]} (an ArrayList/Vector/ArrayDeque/
 * Stack-style model) implements {@code iterator()} as:
 *
 * <pre>{@code
 *   public Iterator<E> iterator() {
 *       CProverArrayLikeIterator<E> it = new CProverArrayLikeIterator<>();
 *       it.init(elementData, size);
 *       return it;
 *   }
 * }</pre>
 *
 * <p><b>Snapshot semantics.</b> {@code init} captures the
 * collection's storage array and logical size at iterator-creation
 * time. Subsequent mutations of the underlying collection are not
 * observed by the iterator: hasNext() compares against the snapshot
 * size, and next() reads from the snapshot array.
 *
 * <p><b>Why a top-level class, not an inner class?</b> A non-static
 * inner class carries an implicit {@code this$0} reference back to
 * the enclosing collection. JBMC's lazy class-loading then sees a
 * cycle (Collection ↔ Iterator) when nondet-init allocates a
 * parameter of an abstract collection type. A top-level class with
 * no back-reference breaks the cycle.
 *
 * <p><b>What this iterator does NOT model:</b>
 * <ul>
 *   <li>{@code remove()} — calls {@link CProver#notModelled()}.</li>
 *   <li>Fail-fast behaviour. JDK ArrayList iterators throw CME on
 *       structural modification; we don't model that.</li>
 * </ul>
 *
 * <p><b>Why snapshot semantics are the right default.</b> Most
 * verification targets iterate read-only (sums, first-match
 * searches, building secondary structures), so CME never fires in
 * practice. Contracts can state the collection/iterator
 * relationship explicitly when a target genuinely mutates during
 * iteration: the snapshot gives the verifier a stable view to
 * assert against.
 *
 * <p><b>Iteration order.</b> Array-backed lists iterate in index
 * order, matching {@code get(i)}. Hash-based collections iterate in
 * storage order; the JDK explicitly leaves their iteration order
 * unspecified, so storage order is conformant. (Insertion-ordered
 * collections such as {@code LinkedHashMap} specify an order and
 * need their models to maintain storage in insertion order for this
 * iterator to be conformant.)
 *
 * <p><b>Future path: fail-fast (CME) modelling.</b> Targets that
 * genuinely require {@code ConcurrentModificationException} can
 * extend this design with a {@code modCount} field on the
 * collection and an {@code expectedModCount} snapshot here;
 * {@code hasNext()}/{@code next()} would compare and throw on
 * mismatch. That requires every mutator to bump {@code modCount}
 * and inflates the symbolic equation by one int per collection plus
 * one check per iterator step, so it should be opt-in (e.g. a JBMC
 * flag), not the default.
 *
 * <p><b>Future path: {@code Iterator.remove()}.</b> A mutable
 * variant needs a back-reference to the collection in addition to
 * the snapshot, with {@code remove()} writing through to the
 * collection's storage (and the snapshot, for consistency). An
 * explicit field back-reference on a top-level class is safe: the
 * lazy-class-loading cycle described above is specific to the
 * implicit {@code this$0} of non-static inner classes.
 *
 * @param <E> element type produced by {@code next()}.
 */
public final class CProverArrayLikeIterator<E> implements Iterator<E> {

    /** Snapshot of the backing array. Set by {@link #init}. */
    Object[] data;

    /** Snapshot of the collection's logical size. Set by {@link #init}. */
    int size;

    /** Cursor into {@code data}; advances on every {@code next()}. */
    int idx;

    public CProverArrayLikeIterator() {
        // No-arg ctor by design; see class javadoc. Fields are
        // populated by init(...) before the iterator is exposed
        // to the caller.
    }

    /**
     * Populate the snapshot fields. Must be called exactly once,
     * before any other method, by the JDK collection model
     * inside its {@code iterator()} body.
     */
    public void init(Object[] data, int size) {
        CProver.assume(data != null);
        CProver.assume(size >= 0);
        CProver.assume(size <= data.length);
        this.data = data;
        this.size = size;
        this.idx = 0;
    }

    @Override
    public boolean hasNext() {
        return idx < size;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E next() {
        // JDK contract: iterating past the end throws
        // NoSuchElementException. Modelling the throw (rather than
        // reading the storage tail, or assume()-ing the precondition,
        // which would soundly-invisibly prune the very executions where
        // target code over-iterates) keeps caller bugs observable with
        // the correct exception type.
        if (idx >= size) {
            throw new NoSuchElementException();
        }
        int i = idx;
        idx = i + 1;
        return (E) data[i];
    }

    @Override
    public void remove() {
        CProver.notModelled();
    }
}

