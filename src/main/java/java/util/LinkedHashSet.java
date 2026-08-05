/*
 * Copyright (c) 2000, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java.util;

/**
 * JBMC model of java.util.LinkedHashSet.
 *
 * <p>DIFFBLUE MODEL LIBRARY The HashSet model stores its elements as a
 * duplicate-free {@code Object[]} prefix in which {@code add} appends and
 * {@code remove} shifts left -- i.e. it is ALREADY insertion-ordered, and
 * its iterator walks storage order. LinkedHashSet, whose contract is
 * exactly "iteration in insertion order", is therefore behaviourally the
 * HashSet model; this subclass only provides the JDK type identity
 * (instanceof LinkedHashSet, overload resolution) and constructors.
 */
public class LinkedHashSet<E> extends HashSet<E> {

    private static final long serialVersionUID = -2851667679971038690L;

    public LinkedHashSet() {
        super();
    }

    public LinkedHashSet(int initialCapacity) {
        super(initialCapacity);
    }

    public LinkedHashSet(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public LinkedHashSet(Collection<? extends E> c) {
        super();
        addAll(c);
    }
}
