/*
 * Copyright (c) 1997, 2022, Oracle and/or its affiliates. All rights reserved.
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

package java.security;

/**
 * DIFFBLUE MODEL LIBRARY: there is no security manager, so the actions are
 * simply run (the protection domains, access control contexts and the
 * stack inspection of the JDK are not modelled).
 *
 * @cprover.limitedSupport The access control contexts are not modelled:
 *   getContext() returns null and the contexts that are given are ignored.
 */
@SuppressWarnings("removal")
public final class AccessController {

    private AccessController() { }

    public static <T> T doPrivileged(PrivilegedAction<T> action) {
        return action.run();
    }

    public static <T> T doPrivilegedWithCombiner(PrivilegedAction<T> action) {
        return action.run();
    }

    public static <T> T doPrivileged(PrivilegedAction<T> action,
                                     AccessControlContext context) {
        return action.run();
    }

    public static <T> T doPrivileged(PrivilegedAction<T> action,
                                     AccessControlContext context,
                                     Permission... perms) {
        return action.run();
    }

    public static <T> T doPrivilegedWithCombiner(PrivilegedAction<T> action,
                                                 AccessControlContext context,
                                                 Permission... perms) {
        return action.run();
    }

    public static <T> T doPrivileged(PrivilegedExceptionAction<T> action)
        throws PrivilegedActionException {
        try {
            return action.run();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new PrivilegedActionException(e);
        }
    }

    public static <T> T doPrivilegedWithCombiner(PrivilegedExceptionAction<T> action)
        throws PrivilegedActionException {
        return doPrivileged(action);
    }

    public static <T> T doPrivileged(PrivilegedExceptionAction<T> action,
                                     AccessControlContext context)
        throws PrivilegedActionException {
        return doPrivileged(action);
    }

    public static <T> T doPrivileged(PrivilegedExceptionAction<T> action,
                                     AccessControlContext context,
                                     Permission... perms)
        throws PrivilegedActionException {
        return doPrivileged(action);
    }

    public static <T> T doPrivilegedWithCombiner(PrivilegedExceptionAction<T> action,
                                                 AccessControlContext context,
                                                 Permission... perms)
        throws PrivilegedActionException {
        return doPrivileged(action);
    }

    static AccessControlContext getInheritedAccessControlContext() { return null; }

    public static AccessControlContext getContext() {
        // there is no security manager: no restrictions (a null context)
        return null;
    }

    public static void checkPermission(Permission perm)
        throws AccessControlException {
        // no security manager: every permission is granted
    }
}
