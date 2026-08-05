/*
 * Skolemizing iterator for axiomatic HashSet.iterator().
 *
 * Same idea as {@link AxiomaticEntryIterator}: each call to
 * {@code next()} returns a fresh nondet element constrained
 * to be in the underlying set; {@code hasNext()} returns a
 * fresh nondet boolean so BMC explores all iteration counts
 * from 0 to the unwind bound.
 */
package org.cprover;

import java.util.HashSet;
import java.util.Iterator;

public final class AxiomaticSetIterator<E> implements Iterator<E> {

    private HashSet<E> set;

    public AxiomaticSetIterator(HashSet<E> s) {
        this.set = s;
    }

    @Override
    public boolean hasNext() {
        return CProver.nondetBoolean();
    }

    @Override
    @SuppressWarnings("unchecked")
    public E next() {
        E elem = (E) CProver.nondetWithoutNullForNotModelled();
        CProver.assume(set.contains(elem));
        return elem;
    }

    @Override
    public void remove() {
        CProver.notModelled();
    }
}
