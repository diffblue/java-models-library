/*
 * Copyright (c) 2003, 2022, Oracle and/or its affiliates. All rights reserved.
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

package java.util.regex;

/**
 * Unchecked exception thrown to indicate a syntax error in a
 * regular-expression pattern.
 *
 * DIFFBLUE MODEL LIBRARY the JDK's class (the same in Java 8 to 25 but for
 * the line separator, which is that of the platform: either here)
 *
 * @since 1.4
 */
public class PatternSyntaxException
    extends IllegalArgumentException
{
    private static final long serialVersionUID = -3864639126226059218L;

    private final String desc;
    private final String pattern;
    private final int index;

    public PatternSyntaxException(String desc, String regex, int index) {
        this.desc = desc;
        this.pattern = regex;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String getDescription() {
        return desc;
    }

    public String getPattern() {
        return pattern;
    }

    public String getMessage() {
        // System.lineSeparator(): "\n" or "\r\n"
        String nl = org.cprover.CProver.nondetBoolean() ? "\n" : "\r\n";
        StringBuilder sb = new StringBuilder();
        sb.append(desc);
        if (index >= 0) {
            sb.append(" near index ");
            sb.append(index);
        }
        sb.append(nl);
        sb.append(pattern);
        if (index >= 0 && pattern != null && index < pattern.length()) {
            sb.append(nl);
            // for (int i = 0; i < index; i++) sb.append(' ');
            // DIFFBLUE MODEL LIBRARY (as many steps as the pattern has
            // chars, whatever the index)
            for (int i = 0; i < pattern.length(); i++) {
                if (i < index) {
                    sb.append(' ');
                }
            }
            sb.append('^');
        }
        return sb.toString();
    }

    // DIFFBLUE MODEL LIBRARY
    // See the toString() of java.lang.IllegalArgumentException.
    @Override
    public String toString() {
        return "java.util.regex.PatternSyntaxException: " + getMessage();
    }
}
