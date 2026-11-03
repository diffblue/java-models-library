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

import java.lang.invoke.VarHandle;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

/**
 * An object reference that may be updated atomically.  See the {@link
 * VarHandle} specification for descriptions of the properties of
 * atomic accesses.
 * @since 1.5
 * @author Doug Lea
 * @param <V> The type of object referred to by this reference
 */
public class AtomicReference<V> implements java.io.Serializable {
    private static final long serialVersionUID = -1848883965231344442L;

    /**
     * @cprover.fullSupport Every method below operates on this field
     *   directly instead of going through a VarHandle: see
     *   {@link AtomicLong#value} for why.
     */
    @SuppressWarnings("serial") // Conditionally serializable
    private volatile V value;

    /**
     * Creates a new AtomicReference with the given initial value.
     *
     * @param initialValue the initial value
     */
    public AtomicReference(V initialValue) {
        value = initialValue;
    }

    /**
     * Creates a new AtomicReference with null initial value.
     */
    public AtomicReference() {
    }

    /**
     * Returns the current value,
     * with memory effects as specified by {@link VarHandle#getVolatile}.
     *
     * @return the current value
     */
    public final V get() {
        return value;
    }

    /**
     * Sets the value to {@code newValue},
     * with memory effects as specified by {@link VarHandle#setVolatile}.
     *
     * @param newValue the new value
     */
    public final void set(V newValue) {
        value = newValue;
    }

    /**
     * Sets the value to {@code newValue},
     * with memory effects as specified by {@link VarHandle#setRelease}.
     *
     * @param newValue the new value
     * @since 1.6
     */
    /**
     * @cprover.fullSupport See {@link #value}.
     */
    public final void lazySet(V newValue) {
        value = newValue;
    }

    /**
     * Atomically sets the value to {@code newValue}
     * if the current value {@code == expectedValue},
     * with memory effects as specified by {@link VarHandle#compareAndSet}.
     *
     * @param expectedValue the expected value
     * @param newValue the new value
     * @return {@code true} if successful. False return indicates that
     * the actual value was not equal to the expected value.
     */
    /**
     * @cprover.fullSupport See {@link #value}.
     */
    public final boolean compareAndSet(V expectedValue, V newValue) {
        if (value == expectedValue) {
            value = newValue;
            return true;
        }
        return false;
    }

    /**
     * Possibly atomically sets the value to {@code newValue}
     * if the current value {@code == expectedValue},
     * with memory effects as specified by {@link VarHandle#weakCompareAndSetPlain}.
     *
     * @deprecated This method has plain memory effects but the method
     * name implies volatile memory effects (see methods such as
     * {@link #compareAndExchange} and {@link #compareAndSet}).  To avoid
     * confusion over plain or volatile memory effects it is recommended that
     * the method {@link #weakCompareAndSetPlain} be used instead.
     *
     * @param expectedValue the expected value
     * @param newValue the new value
     * @return {@code true} if successful
     * @see #weakCompareAndSetPlain
     */
    /**
     * @cprover.fullSupport See {@link #value}.
     */
    @Deprecated(since="9")
    public final boolean weakCompareAndSet(V expectedValue, V newValue) {
        return compareAndSet(expectedValue, newValue);
    }

    /**
     * Possibly atomically sets the value to {@code newValue}
     * if the current value {@code == expectedValue},
     * with memory effects as specified by {@link VarHandle#weakCompareAndSetPlain}.
     *
     * @param expectedValue the expected value
     * @param newValue the new value
     * @return {@code true} if successful
     * @since 9
     */
    /**
     * @cprover.fullSupport See {@link #value}.
     */
    public final boolean weakCompareAndSetPlain(V expectedValue, V newValue) {
        return compareAndSet(expectedValue, newValue);
    }

    /**
     * Atomically sets the value to {@code newValue} and returns the old value,
     * with memory effects as specified by {@link VarHandle#getAndSet}.
     *
     * @param newValue the new value
     * @return the previous value
     */
    /**
     * @cprover.fullSupport See {@link #value}.
     */
    public final V getAndSet(V newValue) {
        V prev = value;
        value = newValue;
        return prev;
    }

    /**
     * Atomically updates (with memory effects as specified by {@link
     * VarHandle#compareAndSet}) the current value with the results of
     * applying the given function, returning the previous value. The
     * function should be side-effect-free, since it may be re-applied
     * when attempted updates fail due to contention among threads.
     *
     * @param updateFunction a side-effect-free function
     * @return the previous value
     * @since 1.8
     */
    public final V getAndUpdate(UnaryOperator<V> updateFunction) {
        V prev = get(), next = null;
        for (boolean haveNext = false;;) {
            if (!haveNext)
                next = updateFunction.apply(prev);
            if (weakCompareAndSetVolatile(prev, next))
                return prev;
            haveNext = (prev == (prev = get()));
        }
    }

    /**
     * Atomically updates (with memory effects as specified by {@link
     * VarHandle#compareAndSet}) the current value with the results of
     * applying the given function, returning the updated value. The
     * function should be side-effect-free, since it may be re-applied
     * when attempted updates fail due to contention among threads.
     *
     * @param updateFunction a side-effect-free function
     * @return the updated value
     * @since 1.8
     */
    public final V updateAndGet(UnaryOperator<V> updateFunction) {
        V prev = get(), next = null;
        for (boolean haveNext = false;;) {
            if (!haveNext)
                next = updateFunction.apply(prev);
            if (weakCompareAndSetVolatile(prev, next))
                return next;
            haveNext = (prev == (prev = get()));
        }
    }

    /**
     * Atomically updates (with memory effects as specified by {@link
     * VarHandle#compareAndSet}) the current value with the results of
     * applying the given function to the current and given values,
     * returning the previous value. The function should be
     * side-effect-free, since it may be re-applied when attempted
     * updates fail due to contention among threads.  The function is
     * applied with the current value as its first argument, and the
     * given update as the second argument.
     *
     * @param x the update value
     * @param accumulatorFunction a side-effect-free function of two arguments
     * @return the previous value
     * @since 1.8
     */
    public final V getAndAccumulate(V x,
                                    BinaryOperator<V> accumulatorFunction) {
        V prev = get(), next = null;
        for (boolean haveNext = false;;) {
            if (!haveNext)
                next = accumulatorFunction.apply(prev, x);
            if (weakCompareAndSetVolatile(prev, next))
                return prev;
            haveNext = (prev == (prev = get()));
        }
    }

    /**
     * Atomically updates (with memory effects as specified by {@link
     * VarHandle#compareAndSet}) the current value with the results of
     * applying the given function to the current and given values,
     * returning the updated value. The function should be
     * side-effect-free, since it may be re-applied when attempted
     * updates fail due to contention among threads.  The function is
     * applied with the current value as its first argument, and the
     * given update as the second argument.
     *
     * @param x the update value
     * @param accumulatorFunction a side-effect-free function of two arguments
     * @return the updated value
     * @since 1.8
     */
    public final V accumulateAndGet(V x,
                                    BinaryOperator<V> accumulatorFunction) {
        V prev = get(), next = null;
        for (boolean haveNext = false;;) {
            if (!haveNext)
                next = accumulatorFunction.apply(prev, x);
            if (weakCompareAndSetVolatile(prev, next))
                return next;
            haveNext = (prev == (prev = get()));
        }
    }

    /**
     * Returns the String representation of the current value.
     * @return the String representation of the current value
     */
    public String toString() {
        return String.valueOf(get());
    }

    // jdk9

    /**
     * Returns the current value, with memory semantics of reading as
     * if the variable was declared non-{@code volatile}.
     *
     * @return the value
     * @since 9
     */
    /**
     * @cprover.fullSupport See {@link #value}.
     */
    public final V getPlain() {
        return value;
    }

    /**
     * Sets the value to {@code newValue}, with memory semantics
     * of setting as if the variable was declared non-{@code volatile}
     * and non-{@code final}.
     *
     * @param newValue the new value
     * @since 9
     */
    /**
     * @cprover.fullSupport See {@link #value}.
     */
    public final void setPlain(V newValue) {
        value = newValue;
    }

    /**
     * Returns the current value,
     * with memory effects as specified by {@link VarHandle#getOpaque}.
     *
     * @return the value
     * @since 9
     */
    /**
     * @cprover.fullSupport See {@link #value}.
     */
    public final V getOpaque() {
        return value;
    }

    /**
     * Sets the value to {@code newValue},
     * with memory effects as specified by {@link VarHandle#setOpaque}.
     *
     * @param newValue the new value
     * @since 9
     */
    /**
     * @cprover.fullSupport See {@link #value}.
     */
    public final void setOpaque(V newValue) {
        value = newValue;
    }

    /**
     * Returns the current value,
     * with memory effects as specified by {@link VarHandle#getAcquire}.
     *
     * @return the value
     * @since 9
     */
    /**
     * @cprover.fullSupport See {@link #value}.
     */
    public final V getAcquire() {
        return value;
    }

    /**
     * Sets the value to {@code newValue},
     * with memory effects as specified by {@link VarHandle#setRelease}.
     *
     * @param newValue the new value
     * @since 9
     */
    /**
     * @cprover.fullSupport See {@link #value}.
     */
    public final void setRelease(V newValue) {
        value = newValue;
    }

    /**
     * Atomically sets the value to {@code newValue} if the current value,
     * referred to as the <em>witness value</em>, {@code == expectedValue},
     * with memory effects as specified by
     * {@link VarHandle#compareAndExchange}.
     *
     * @param expectedValue the expected value
     * @param newValue the new value
     * @return the <em>witness value</em>, which will be the same as the
     * expected value if successful
     * @since 9
     */
    /**
     * @cprover.fullSupport See {@link #value}.
     */
    public final V compareAndExchange(V expectedValue, V newValue) {
        V prev = value;
        if (prev == expectedValue) {
            value = newValue;
        }
        return prev;
    }

    /**
     * Atomically sets the value to {@code newValue} if the current value,
     * referred to as the <em>witness value</em>, {@code == expectedValue},
     * with memory effects as specified by
     * {@link VarHandle#compareAndExchangeAcquire}.
     *
     * @param expectedValue the expected value
     * @param newValue the new value
     * @return the <em>witness value</em>, which will be the same as the
     * expected value if successful
     * @since 9
     */
    /**
     * @cprover.fullSupport See {@link #value}.
     */
    public final V compareAndExchangeAcquire(V expectedValue, V newValue) {
        return compareAndExchange(expectedValue, newValue);
    }

    /**
     * Atomically sets the value to {@code newValue} if the current value,
     * referred to as the <em>witness value</em>, {@code == expectedValue},
     * with memory effects as specified by
     * {@link VarHandle#compareAndExchangeRelease}.
     *
     * @param expectedValue the expected value
     * @param newValue the new value
     * @return the <em>witness value</em>, which will be the same as the
     * expected value if successful
     * @since 9
     */
    /**
     * @cprover.fullSupport See {@link #value}.
     */
    public final V compareAndExchangeRelease(V expectedValue, V newValue) {
        return compareAndExchange(expectedValue, newValue);
    }

    /**
     * Possibly atomically sets the value to {@code newValue}
     * if the current value {@code == expectedValue},
     * with memory effects as specified by
     * {@link VarHandle#weakCompareAndSet}.
     *
     * @param expectedValue the expected value
     * @param newValue the new value
     * @return {@code true} if successful
     * @since 9
     */
    /**
     * @cprover.fullSupport See {@link #value}.
     */
    public final boolean weakCompareAndSetVolatile(V expectedValue, V newValue) {
        return compareAndSet(expectedValue, newValue);
    }

    /**
     * Possibly atomically sets the value to {@code newValue}
     * if the current value {@code == expectedValue},
     * with memory effects as specified by
     * {@link VarHandle#weakCompareAndSetAcquire}.
     *
     * @param expectedValue the expected value
     * @param newValue the new value
     * @return {@code true} if successful
     * @since 9
     */
    /**
     * @cprover.fullSupport See {@link #value}.
     */
    public final boolean weakCompareAndSetAcquire(V expectedValue, V newValue) {
        return compareAndSet(expectedValue, newValue);
    }

    /**
     * Possibly atomically sets the value to {@code newValue}
     * if the current value {@code == expectedValue},
     * with memory effects as specified by
     * {@link VarHandle#weakCompareAndSetRelease}.
     *
     * @param expectedValue the expected value
     * @param newValue the new value
     * @return {@code true} if successful
     * @since 9
     */
    /**
     * @cprover.fullSupport See {@link #value}.
     */
    public final boolean weakCompareAndSetRelease(V expectedValue, V newValue) {
        return compareAndSet(expectedValue, newValue);
    }

}
