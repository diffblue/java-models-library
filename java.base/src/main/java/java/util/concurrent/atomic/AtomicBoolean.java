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

import org.cprover.CProver;

/**
 * DIFFBLUE MODEL LIBRARY: a {@code boolean} value in a plain field, whose
 * compound operations are atomic ({@code CProver.atomicBegin()}). The memory
 * order of the access modes is not modelled (all are sequentially consistent).
 */
public class AtomicBoolean implements java.io.Serializable {
    private static final long serialVersionUID = 4654671469794556979L;

    private volatile boolean value;

    public AtomicBoolean(boolean initialValue) {
        value = initialValue;
    }

    public AtomicBoolean() {
    }

    public final boolean get() { return value; }

    public final boolean compareAndSet(boolean expectedValue, boolean newValue) {
        CProver.atomicBegin();
        boolean ok = value == expectedValue;
        if (ok) value = newValue;
        CProver.atomicEnd();
        return ok;
    }

    public boolean weakCompareAndSet(boolean expectedValue, boolean newValue) {
        return compareAndSet(expectedValue, newValue);
    }

    public boolean weakCompareAndSetPlain(boolean expectedValue, boolean newValue) {
        return compareAndSet(expectedValue, newValue);
    }

    public final void set(boolean newValue) { value = newValue; }

    public final void lazySet(boolean newValue) { value = newValue; }

    public final boolean getAndSet(boolean newValue) {
        CProver.atomicBegin();
        boolean old = value;
        value = newValue;
        CProver.atomicEnd();
        return old;
    }

    public String toString() {
        return Boolean.toString(get());
    }

    public final boolean getPlain() { return value; }

    public final void setPlain(boolean newValue) { value = newValue; }

    public final boolean getOpaque() { return value; }

    public final void setOpaque(boolean newValue) { value = newValue; }

    public final boolean getAcquire() { return value; }

    public final void setRelease(boolean newValue) { value = newValue; }

    public final boolean compareAndExchange(boolean expectedValue, boolean newValue) {
        CProver.atomicBegin();
        boolean witness = value;
        if (witness == expectedValue) value = newValue;
        CProver.atomicEnd();
        return witness;
    }

    public final boolean compareAndExchangeAcquire(boolean expectedValue, boolean newValue) {
        return compareAndExchange(expectedValue, newValue);
    }

    public final boolean compareAndExchangeRelease(boolean expectedValue, boolean newValue) {
        return compareAndExchange(expectedValue, newValue);
    }

    public boolean weakCompareAndSetVolatile(boolean expectedValue, boolean newValue) {
        return compareAndSet(expectedValue, newValue);
    }

    public boolean weakCompareAndSetAcquire(boolean expectedValue, boolean newValue) {
        return compareAndSet(expectedValue, newValue);
    }

    public boolean weakCompareAndSetRelease(boolean expectedValue, boolean newValue) {
        return compareAndSet(expectedValue, newValue);
    }
}
