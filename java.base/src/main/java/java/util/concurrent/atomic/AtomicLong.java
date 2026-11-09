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

import java.util.function.LongBinaryOperator;
import java.util.function.LongUnaryOperator;
import org.cprover.CProver;

/**
 * DIFFBLUE MODEL LIBRARY: a {@code long} value in a plain field, whose
 * compound operations are atomic ({@code CProver.atomicBegin()}). The memory
 * order of the access modes is not modelled (all are sequentially consistent).
 */
public class AtomicLong extends Number implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    // used by AtomicLongFieldUpdater
    static final boolean VM_SUPPORTS_LONG_CAS = true;

    private volatile long value;

    public AtomicLong(long initialValue) {
        value = initialValue;
    }

    public AtomicLong() {
    }

    public final long get() { return value; }

    public final void set(long newValue) { value = newValue; }

    public final void lazySet(long newValue) { value = newValue; }

    public final long getAndSet(long newValue) {
        CProver.atomicBegin();
        long old = value;
        value = newValue;
        CProver.atomicEnd();
        return old;
    }

    public final boolean compareAndSet(long expectedValue, long newValue) {
        CProver.atomicBegin();
        boolean ok = value == expectedValue;
        if (ok) value = newValue;
        CProver.atomicEnd();
        return ok;
    }

    public final boolean weakCompareAndSet(long expectedValue, long newValue) {
        return compareAndSet(expectedValue, newValue);
    }

    public final boolean weakCompareAndSetPlain(long expectedValue, long newValue) {
        return compareAndSet(expectedValue, newValue);
    }

    public final boolean weakCompareAndSetVolatile(long expectedValue, long newValue) {
        return compareAndSet(expectedValue, newValue);
    }

    public final boolean weakCompareAndSetAcquire(long expectedValue, long newValue) {
        return compareAndSet(expectedValue, newValue);
    }

    public final boolean weakCompareAndSetRelease(long expectedValue, long newValue) {
        return compareAndSet(expectedValue, newValue);
    }

    public final long compareAndExchange(long expectedValue, long newValue) {
        CProver.atomicBegin();
        long witness = value;
        if (witness == expectedValue) value = newValue;
        CProver.atomicEnd();
        return witness;
    }

    public final long compareAndExchangeAcquire(long expectedValue, long newValue) {
        return compareAndExchange(expectedValue, newValue);
    }

    public final long compareAndExchangeRelease(long expectedValue, long newValue) {
        return compareAndExchange(expectedValue, newValue);
    }

    public final long getAndIncrement() { return getAndAdd(1); }

    public final long getAndDecrement() { return getAndAdd(-1); }

    public final long getAndAdd(long delta) {
        CProver.atomicBegin();
        long old = value;
        value = (long) (old + delta);
        CProver.atomicEnd();
        return old;
    }

    public final long incrementAndGet() { return addAndGet(1); }

    public final long decrementAndGet() { return addAndGet(-1); }

    public final long addAndGet(long delta) {
        CProver.atomicBegin();
        long next = (long) (value + delta);
        value = next;
        CProver.atomicEnd();
        return next;
    }

    public final long getAndUpdate(LongUnaryOperator updateFunction) {
        long prev = get(), next = 0;
        for (boolean haveNext = false;;) {
            if (!haveNext)
                next = updateFunction.applyAsLong(prev);
            if (compareAndSet(prev, next))
                return prev;
            haveNext = (prev == (prev = get()));
        }
    }

    public final long updateAndGet(LongUnaryOperator updateFunction) {
        long prev = get(), next = 0;
        for (boolean haveNext = false;;) {
            if (!haveNext)
                next = updateFunction.applyAsLong(prev);
            if (compareAndSet(prev, next))
                return next;
            haveNext = (prev == (prev = get()));
        }
    }

    public final long getAndAccumulate(long x, LongBinaryOperator accumulatorFunction) {
        long prev = get(), next = 0;
        for (boolean haveNext = false;;) {
            if (!haveNext)
                next = accumulatorFunction.applyAsLong(prev, x);
            if (compareAndSet(prev, next))
                return prev;
            haveNext = (prev == (prev = get()));
        }
    }

    public final long accumulateAndGet(long x, LongBinaryOperator accumulatorFunction) {
        long prev = get(), next = 0;
        for (boolean haveNext = false;;) {
            if (!haveNext)
                next = accumulatorFunction.applyAsLong(prev, x);
            if (compareAndSet(prev, next))
                return next;
            haveNext = (prev == (prev = get()));
        }
    }

    public String toString() {
        return Long.toString(get());
    }

    public int intValue() { return (int) get(); }

    public long longValue() { return (long) get(); }

    public float floatValue() { return (float) get(); }

    public double doubleValue() { return (double) get(); }

    public final long getPlain() { return value; }

    public final void setPlain(long newValue) { value = newValue; }

    public final long getOpaque() { return value; }

    public final void setOpaque(long newValue) { value = newValue; }

    public final long getAcquire() { return value; }

    public final void setRelease(long newValue) { value = newValue; }
}
