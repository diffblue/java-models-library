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

/**
 * A {@code boolean} value that may be updated atomically. See the
 * {@link VarHandle} specification for descriptions of the properties
 * of atomic accesses. An {@code AtomicBoolean} is used in
 * applications such as atomically updated flags, and cannot be used
 * as a replacement for a {@link java.lang.Boolean}.
 *
 * @since 1.5
 * @author Doug Lea
 */
public class AtomicBoolean implements java.io.Serializable {
    private static final long serialVersionUID = 4654671469794556979L;

    /**
     * @cprover.fullSupport Every method below operates on this field
     *   directly instead of going through a VarHandle: see
     *   {@link AtomicLong#value} for why (the same reasoning applies -
     *   a VarHandle's get/compareAndSet etc. cannot be modelled as
     *   reflecting the specific field it was created for any more
     *   faithfully than Unsafe's offset-based accessors can).
     */
    private volatile int value;

    /**
     * Creates a new {@code AtomicBoolean} with the given initial value.
     *
     * @param initialValue the initial value
     */
    public AtomicBoolean(boolean initialValue) {
        if (initialValue)
            value = 1;
    }

    /**
     * Creates a new {@code AtomicBoolean} with initial value {@code false}.
     */
    public AtomicBoolean() {
    }

    /**
     * Returns the current value,
     * with memory effects as specified by {@link VarHandle#getVolatile}.
     *
     * @return the current value
     */
    public final boolean get() {
        return value != 0;
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
    public final boolean compareAndSet(boolean expectedValue, boolean newValue) {
        int expected = expectedValue ? 1 : 0;
        if (value == expected) {
            value = newValue ? 1 : 0;
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
    public boolean weakCompareAndSet(boolean expectedValue, boolean newValue) {
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
    public boolean weakCompareAndSetPlain(boolean expectedValue, boolean newValue) {
        return compareAndSet(expectedValue, newValue);
    }

    /**
     * Sets the value to {@code newValue},
     * with memory effects as specified by {@link VarHandle#setVolatile}.
     *
     * @param newValue the new value
     */
    public final void set(boolean newValue) {
        value = newValue ? 1 : 0;
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
    public final void lazySet(boolean newValue) {
        value = newValue ? 1 : 0;
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
    public final boolean getAndSet(boolean newValue) {
        int prev = value;
        value = newValue ? 1 : 0;
        return prev != 0;
    }

    /**
     * Returns the String representation of the current value.
     * @return the String representation of the current value
     */
    public String toString() {
        return Boolean.toString(get());
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
    public final boolean getPlain() {
        return value != 0;
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
    public final void setPlain(boolean newValue) {
        value = newValue ? 1 : 0;
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
    public final boolean getOpaque() {
        return value != 0;
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
    public final void setOpaque(boolean newValue) {
        value = newValue ? 1 : 0;
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
    public final boolean getAcquire() {
        return value != 0;
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
    public final void setRelease(boolean newValue) {
        value = newValue ? 1 : 0;
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
    public final boolean compareAndExchange(boolean expectedValue, boolean newValue) {
        int prev = value;
        if (prev == (expectedValue ? 1 : 0)) {
            value = newValue ? 1 : 0;
        }
        return prev != 0;
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
    public final boolean compareAndExchangeAcquire(boolean expectedValue, boolean newValue) {
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
    public final boolean compareAndExchangeRelease(boolean expectedValue, boolean newValue) {
        return compareAndExchange(expectedValue, newValue);
    }

    /**
     * Possibly atomically sets the value to {@code newValue} if the current
     * value {@code == expectedValue},
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
    public final boolean weakCompareAndSetVolatile(boolean expectedValue, boolean newValue) {
        return compareAndSet(expectedValue, newValue);
    }

    /**
     * Possibly atomically sets the value to {@code newValue} if the current
     * value {@code == expectedValue},
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
    public final boolean weakCompareAndSetAcquire(boolean expectedValue, boolean newValue) {
        return compareAndSet(expectedValue, newValue);
    }

    /**
     * Possibly atomically sets the value to {@code newValue} if the current
     * value {@code == expectedValue},
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
    public final boolean weakCompareAndSetRelease(boolean expectedValue, boolean newValue) {
        return compareAndSet(expectedValue, newValue);
    }

}
