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

import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;
import org.cprover.CProver;

/**
 * DIFFBLUE MODEL LIBRARY: a {@code int} value in a plain field, whose
 * compound operations are atomic ({@code CProver.atomicBegin()}). The memory
 * order of the access modes is not modelled (all are sequentially consistent).
 */
public class AtomicInteger extends Number implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private volatile int value;

    public AtomicInteger(int initialValue) {
        value = initialValue;
    }

    public AtomicInteger() {
    }

    public final int get() { return value; }

    public final void set(int newValue) { value = newValue; }

    public final void lazySet(int newValue) { value = newValue; }

    public final int getAndSet(int newValue) {
        CProver.atomicBegin();
        int old = value;
        value = newValue;
        CProver.atomicEnd();
        return old;
    }

    public final boolean compareAndSet(int expectedValue, int newValue) {
        CProver.atomicBegin();
        boolean ok = value == expectedValue;
        if (ok) value = newValue;
        CProver.atomicEnd();
        return ok;
    }

    public final boolean weakCompareAndSet(int expectedValue, int newValue) {
        return compareAndSet(expectedValue, newValue);
    }

    public final boolean weakCompareAndSetPlain(int expectedValue, int newValue) {
        return compareAndSet(expectedValue, newValue);
    }

    public final boolean weakCompareAndSetVolatile(int expectedValue, int newValue) {
        return compareAndSet(expectedValue, newValue);
    }

    public final boolean weakCompareAndSetAcquire(int expectedValue, int newValue) {
        return compareAndSet(expectedValue, newValue);
    }

    public final boolean weakCompareAndSetRelease(int expectedValue, int newValue) {
        return compareAndSet(expectedValue, newValue);
    }

    public final int compareAndExchange(int expectedValue, int newValue) {
        CProver.atomicBegin();
        int witness = value;
        if (witness == expectedValue) value = newValue;
        CProver.atomicEnd();
        return witness;
    }

    public final int compareAndExchangeAcquire(int expectedValue, int newValue) {
        return compareAndExchange(expectedValue, newValue);
    }

    public final int compareAndExchangeRelease(int expectedValue, int newValue) {
        return compareAndExchange(expectedValue, newValue);
    }

    public final int getAndIncrement() { return getAndAdd(1); }

    public final int getAndDecrement() { return getAndAdd(-1); }

    public final int getAndAdd(int delta) {
        CProver.atomicBegin();
        int old = value;
        value = (int) (old + delta);
        CProver.atomicEnd();
        return old;
    }

    public final int incrementAndGet() { return addAndGet(1); }

    public final int decrementAndGet() { return addAndGet(-1); }

    public final int addAndGet(int delta) {
        CProver.atomicBegin();
        int next = (int) (value + delta);
        value = next;
        CProver.atomicEnd();
        return next;
    }

    public final int getAndUpdate(IntUnaryOperator updateFunction) {
        int prev = get(), next = 0;
        for (boolean haveNext = false;;) {
            if (!haveNext)
                next = updateFunction.applyAsInt(prev);
            if (compareAndSet(prev, next))
                return prev;
            haveNext = (prev == (prev = get()));
        }
    }

    public final int updateAndGet(IntUnaryOperator updateFunction) {
        int prev = get(), next = 0;
        for (boolean haveNext = false;;) {
            if (!haveNext)
                next = updateFunction.applyAsInt(prev);
            if (compareAndSet(prev, next))
                return next;
            haveNext = (prev == (prev = get()));
        }
    }

    public final int getAndAccumulate(int x, IntBinaryOperator accumulatorFunction) {
        int prev = get(), next = 0;
        for (boolean haveNext = false;;) {
            if (!haveNext)
                next = accumulatorFunction.applyAsInt(prev, x);
            if (compareAndSet(prev, next))
                return prev;
            haveNext = (prev == (prev = get()));
        }
    }

    public final int accumulateAndGet(int x, IntBinaryOperator accumulatorFunction) {
        int prev = get(), next = 0;
        for (boolean haveNext = false;;) {
            if (!haveNext)
                next = accumulatorFunction.applyAsInt(prev, x);
            if (compareAndSet(prev, next))
                return next;
            haveNext = (prev == (prev = get()));
        }
    }

    public String toString() {
        return Integer.toString(get());
    }

    public int intValue() { return (int) get(); }

    public long longValue() { return (long) get(); }

    public float floatValue() { return (float) get(); }

    public double doubleValue() { return (double) get(); }

    public final int getPlain() { return value; }

    public final void setPlain(int newValue) { value = newValue; }

    public final int getOpaque() { return value; }

    public final void setOpaque(int newValue) { value = newValue; }

    public final int getAcquire() { return value; }

    public final void setRelease(int newValue) { value = newValue; }
}
