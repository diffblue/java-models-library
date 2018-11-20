/*
 * Copyright (c) 2003, 2016, Oracle and/or its affiliates. All rights reserved.
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
 * DIFFBLUE MODEL LIBRARY
 * At the moment, the methods defined in this class are not called anywhere
 * because the classes that extend this class override each method without
 * calling methods from this class.
 *
 * This file is only present to maintain the original inheritance. However, it
 * would make sense to implement some of the modelled methods here instead of
 * in the StringBuilder and StringBuffer classes, in the case these methods
 * are modelled in a similar way.
 */

package java.lang;

import org.cprover.CProver;

abstract class AbstractStringBuilder implements Appendable, CharSequence {

    AbstractStringBuilder() {}

    AbstractStringBuilder(int capacity) {}

    @Override
    public int length() {
        return CProver.nondetInt();
    }

    public int capacity() {
        CProver.notModelled();
        return CProver.nondetInt();
    }

    public void ensureCapacity(int minimumCapacity) {}

    public void trimToSize() {}

    public void setLength(int newLength) {}

    @Override
    public char charAt(int index) {
        return 'c';
    }

    public int codePointAt(int index) {
        return CProver.nondetInt();
    }

    public int codePointBefore(int index) {
        return CProver.nondetInt();
    }

    public int codePointCount(int beginIndex, int endIndex) {
        return CProver.nondetInt();
    }

    public int offsetByCodePoints(int index, int codePointOffset) {
        return CProver.nondetInt();
    }

    public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
      CProver.notModelled();
    }

    public void setCharAt(int index, char ch) {}

    public AbstractStringBuilder append(Object obj) {
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder append(String str) {
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder append(StringBuffer sb) {
        return CProver.nondetWithNullForNotModelled();
    }

    AbstractStringBuilder append(AbstractStringBuilder asb) {
        return CProver.nondetWithNullForNotModelled();
    }

    @Override
    public AbstractStringBuilder append(CharSequence s) {
        return CProver.nondetWithNullForNotModelled();
    }

    @Override
    public AbstractStringBuilder append(CharSequence s, int start, int end) {
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder append(char[] str) {
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder append(char str[], int offset, int len) {
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder append(boolean b) {
        return CProver.nondetWithNullForNotModelled();
    }

    @Override
    public AbstractStringBuilder append(char c) {
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder append(int i) {
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder append(long l) {
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder append(float f) {
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder append(double d) {
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder delete(int start, int end) {
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder appendCodePoint(int codePoint) {
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder deleteCharAt(int index) {
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder replace(int start, int end, String str) {
        return CProver.nondetWithNullForNotModelled();
    }

    public String substring(int start) {
        return CProver.nondetWithNullForNotModelled();
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return CProver.nondetWithNullForNotModelled();
    }

    public String substring(int start, int end) {
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder insert(int index, char[] str, int offset,
                                               int len) {
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder insert(int offset, Object obj) {
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder insert(int offset, String str) {
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder insert(int offset, char[] str) {
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder insert(int dstOffset, CharSequence s) {
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder insert(int dstOffset, CharSequence s,
                                               int start, int end) {
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder insert(int offset, boolean b) {
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder insert(int offset, char c) {
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder insert(int offset, int i) {
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder insert(int offset, long l) {
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder insert(int offset, float f) {
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder insert(int offset, double d) {
        return CProver.nondetWithNullForNotModelled();
    }

    public int indexOf(String str) {
        return CProver.nondetInt();
    }

    public int indexOf(String str, int fromIndex) {
        return CProver.nondetInt();
    }

    public int lastIndexOf(String str) {
        return CProver.nondetInt();
    }

    public int lastIndexOf(String str, int fromIndex) {
        return CProver.nondetInt();
    }

    public AbstractStringBuilder reverse() {
        return CProver.nondetWithNullForNotModelled();
    }

    @Override
    public abstract String toString();

    // Method should not be used in the models
    // final char[] getValue();
}
