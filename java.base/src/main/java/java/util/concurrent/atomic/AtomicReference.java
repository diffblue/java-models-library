/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * This file is available under and governed by the GNU General Public
 * License version 2 only, as published by the Free Software Foundation.
 * However, the following notice accompanied the original version of this
 * file:
 *
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */

package java.util.concurrent.atomic;

import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import org.cprover.CProver;

/**
 * DIFFBLUE MODEL LIBRARY: a reference in a plain field, whose compound
 * operations are atomic ({@code CProver.atomicBegin()}). The memory order of
 * the access modes is not modelled (all are sequentially consistent).
 */
public class AtomicReference<V> implements java.io.Serializable {
    private static final long serialVersionUID = -1848883965231344442L;

    private volatile V value;

    public AtomicReference(V initialValue) {
        value = initialValue;
    }

    public AtomicReference() {
    }

    public final V get() { return value; }

    public final void set(V newValue) { value = newValue; }

    public final void lazySet(V newValue) { value = newValue; }

    public final boolean compareAndSet(V expectedValue, V newValue) {
        CProver.atomicBegin();
        boolean ok = value == expectedValue;
        if (ok) value = newValue;
        CProver.atomicEnd();
        return ok;
    }

    public final boolean weakCompareAndSet(V expectedValue, V newValue) {
        return compareAndSet(expectedValue, newValue);
    }

    public final boolean weakCompareAndSetPlain(V expectedValue, V newValue) {
        return compareAndSet(expectedValue, newValue);
    }

    public final boolean weakCompareAndSetVolatile(V expectedValue, V newValue) {
        return compareAndSet(expectedValue, newValue);
    }

    public final boolean weakCompareAndSetAcquire(V expectedValue, V newValue) {
        return compareAndSet(expectedValue, newValue);
    }

    public final boolean weakCompareAndSetRelease(V expectedValue, V newValue) {
        return compareAndSet(expectedValue, newValue);
    }

    public final V getAndSet(V newValue) {
        CProver.atomicBegin();
        V old = value;
        value = newValue;
        CProver.atomicEnd();
        return old;
    }

    public final V getAndUpdate(UnaryOperator<V> updateFunction) {
        V prev = get(), next = null;
        for (boolean haveNext = false;;) {
            if (!haveNext)
                next = updateFunction.apply(prev);
            if (compareAndSet(prev, next))
                return prev;
            haveNext = (prev == (prev = get()));
        }
    }

    public final V updateAndGet(UnaryOperator<V> updateFunction) {
        V prev = get(), next = null;
        for (boolean haveNext = false;;) {
            if (!haveNext)
                next = updateFunction.apply(prev);
            if (compareAndSet(prev, next))
                return next;
            haveNext = (prev == (prev = get()));
        }
    }

    public final V getAndAccumulate(V x, BinaryOperator<V> accumulatorFunction) {
        V prev = get(), next = null;
        for (boolean haveNext = false;;) {
            if (!haveNext)
                next = accumulatorFunction.apply(prev, x);
            if (compareAndSet(prev, next))
                return prev;
            haveNext = (prev == (prev = get()));
        }
    }

    public final V accumulateAndGet(V x, BinaryOperator<V> accumulatorFunction) {
        V prev = get(), next = null;
        for (boolean haveNext = false;;) {
            if (!haveNext)
                next = accumulatorFunction.apply(prev, x);
            if (compareAndSet(prev, next))
                return next;
            haveNext = (prev == (prev = get()));
        }
    }

    public String toString() {
        return String.valueOf(get());
    }

    public final V getPlain() { return value; }

    public final void setPlain(V newValue) { value = newValue; }

    public final V getOpaque() { return value; }

    public final void setOpaque(V newValue) { value = newValue; }

    public final V getAcquire() { return value; }

    public final void setRelease(V newValue) { value = newValue; }

    public final V compareAndExchange(V expectedValue, V newValue) {
        CProver.atomicBegin();
        V witness = value;
        if (witness == expectedValue) value = newValue;
        CProver.atomicEnd();
        return witness;
    }

    public final V compareAndExchangeAcquire(V expectedValue, V newValue) {
        return compareAndExchange(expectedValue, newValue);
    }

    public final V compareAndExchangeRelease(V expectedValue, V newValue) {
        return compareAndExchange(expectedValue, newValue);
    }
}
