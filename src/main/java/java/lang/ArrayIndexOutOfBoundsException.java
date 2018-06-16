/*
 * Copyright (c) 1994, 2008, Oracle and/or its affiliates. All rights reserved.
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

package java.lang;

public
class ArrayIndexOutOfBoundsException extends IndexOutOfBoundsException {
    private static final long serialVersionUID = -5116101128118950844L;

    public ArrayIndexOutOfBoundsException() {
        super();
    }

    public ArrayIndexOutOfBoundsException(int index) {
        super("Array index out of range: " + index);
    }

    public ArrayIndexOutOfBoundsException(String s) {
        super(s);
    }
    
    // DIFFBLUE MODEL LIBRARY
    // While Object.getClass() is not modelled, we can get the same
    // functionality by adding one toString() method per subclass of
    // Throwable.
    @Override
    public String toString() {
        String message = getLocalizedMessage();
        return (message != null)
            ? ("java.lang.ArrayIndexOutOfBoundsException: " + message)
            : "java.lang.ArrayIndexOutOfBoundsException";
    }
}
