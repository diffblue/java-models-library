/*
 * Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
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
  * Some of the methods defined in this file will be replaced in CBMC, so their
  * definition will be overwritten.
  * These methods are marked as "Handled internally by CBMC".
  */

package java.lang;

import org.cprover.CProver;
import org.cprover.CProverString;

/**
 * A mutable sequence of characters.  This class provides an API compatible
 * with {@code StringBuffer}, but with no guarantee of synchronization.
 * This class is designed for use as a drop-in replacement for
 * {@code StringBuffer} in places where the string buffer was being
 * used by a single thread (as is generally the case).   Where possible,
 * it is recommended that this class be used in preference to
 * {@code StringBuffer} as it will be faster under most implementations.
 *
 * <p>The principal operations on a {@code StringBuilder} are the
 * {@code append} and {@code insert} methods, which are
 * overloaded so as to accept data of any type. Each effectively
 * converts a given datum to a string and then appends or inserts the
 * characters of that string to the string builder. The
 * {@code append} method always adds these characters at the end
 * of the builder; the {@code insert} method adds the characters at
 * a specified point.
 * <p>
 * For example, if {@code z} refers to a string builder object
 * whose current contents are "{@code start}", then
 * the method call {@code z.append("le")} would cause the string
 * builder to contain "{@code startle}", whereas
 * {@code z.insert(4, "le")} would alter the string builder to
 * contain "{@code starlet}".
 * <p>
 * In general, if sb refers to an instance of a {@code StringBuilder},
 * then {@code sb.append(x)} has the same effect as
 * {@code sb.insert(sb.length(), x)}.
 * <p>
 * Every string builder has a capacity. As long as the length of the
 * character sequence contained in the string builder does not exceed
 * the capacity, it is not necessary to allocate a new internal
 * buffer. If the internal buffer overflows, it is automatically made larger.
 *
 * <p>Instances of {@code StringBuilder} are not safe for
 * use by multiple threads. If such synchronization is required then it is
 * recommended that {@link java.lang.StringBuffer} be used.
 *
 * <p>Unless otherwise noted, passing a {@code null} argument to a constructor
 * or method in this class will cause a {@link NullPointerException} to be
 * thrown.
 *
 * @author      Michael McCloskey
 * @see         java.lang.StringBuffer
 * @see         java.lang.String
 * @since       1.5
 *
 * @diffblue.limitedSupport
 * Most methods are not supported yet.
 * The methods that are supported have limited support as they do not handle
 * exceptions.
 */
public final class StringBuilder
    extends AbstractStringBuilder
    implements java.io.Serializable, CharSequence
{
    static final long serialVersionUID = 4383685877147921099L;

    // DIFFBLUE MODEL LIBRARY
    // The model follows AbstractStringBuilder of Java 8 to 25: its checks,
    // their exceptions and messages (CProverChecks), and "null" for null
    // arguments. The string solver does the rest: the methods marked
    // "Handled internally by CBMC" and the private cprover* methods are
    // replaced by JBMC (java_string_library_preprocess).

    /** Handled internally by CBMC: the empty sequence. */
    public StringBuilder() {
    }

    /** The empty sequence, after the check of the capacity. */
    public StringBuilder(int capacity) {
        this();
        if (capacity < 0) {
            // (Java allocates an array of the capacity)
            throw new NegativeArraySizeException(
                CProverChecks.negativeSizeMessage(capacity));
        }
    }

    /** Handled internally by CBMC: a copy of {@code str}. */
    public StringBuilder(String str) {
    }

    public StringBuilder(CharSequence seq) {
        this(seq.toString());
    }

    public int compareTo(StringBuilder another) {
        return toString().compareTo(another.toString());
    }

    /** Handled internally by CBMC. */
    public int length() {
        return CProver.nondetInt();
    }

    public int capacity() {
        // (any capacity that holds the sequence)
        int capacity = CProver.nondetInt();
        CProver.assume(capacity >= length());
        return capacity;
    }

    public void ensureCapacity(int minimumCapacity) {
    }

    public void trimToSize() {
    }

    public void setLength(int newLength) {
        if (newLength < 0) {
            throw new StringIndexOutOfBoundsException(newLength);
        }
        cproverSetLength(newLength);
    }

    public char charAt(int index) {
        if (index < 0 || index >= length()) {
            if (CProverChecks.java8()) {
                throw new StringIndexOutOfBoundsException(index);
            }
            CProverChecks.checkIndex(index, length());
        }
        return cproverCharAt(index);
    }

    public int codePointAt(int index) {
        if (index < 0 || index >= length()) {
            if (CProverChecks.java8()) {
                throw new StringIndexOutOfBoundsException(index);
            }
            CProverChecks.checkIndex(index, length());
        }
        return cproverCodePointAt(index);
    }

    public int codePointBefore(int index) {
        int i = index - 1;
        if (i < 0 || i >= length()) {
            if (CProverRuntime.version() <= 17) {
                throw new StringIndexOutOfBoundsException(index);
            }
            CProverChecks.checkIndex(i, length());
        }
        return cproverCodePointBefore(index);
    }

    public int codePointCount(int beginIndex, int endIndex) {
        if (beginIndex < 0 || endIndex > length() || beginIndex > endIndex) {
            if (CProverRuntime.version() <= 17) {
                throw new IndexOutOfBoundsException();
            }
            throw new IndexOutOfBoundsException(CProverChecks.rangeMessage(
                beginIndex, endIndex, length()));
        }
        return cproverCodePointCount(beginIndex, endIndex);
    }

    public int offsetByCodePoints(int index, int codePointOffset) {
        if (index < 0 || index > length()) {
            throw new IndexOutOfBoundsException();
        }
        return toString().offsetByCodePoints(index, codePointOffset);
    }

    public void getChars(int srcBegin, int srcEnd, char[] dst,
                              int dstBegin) {
        int count = length();
        if (CProverChecks.java8()) {
            if (srcBegin < 0) {
                throw new StringIndexOutOfBoundsException(srcBegin);
            }
            if ((srcEnd < 0) || (srcEnd > count)) {
                throw new StringIndexOutOfBoundsException(srcEnd);
            }
            if (srcBegin > srcEnd) {
                throw new StringIndexOutOfBoundsException(
                    "srcBegin > srcEnd");
            }
            // (System.arraycopy)
            int n = srcEnd - srcBegin;
            if (dstBegin < 0 || dstBegin > dst.length - n) {
                throw new ArrayIndexOutOfBoundsException();
            }
        } else {
            CProverChecks.checkRangeSIOOBE(srcBegin, srcEnd, count);
            int n = srcEnd - srcBegin;
            CProverChecks.checkRange(dstBegin, dstBegin + n, dst.length);
        }
        for (int i = srcBegin; i < srcEnd; i++) {
            dst[dstBegin + i - srcBegin] = cproverCharAt(i);
        }
    }

    public void setCharAt(int index, char ch) {
        if (index < 0 || index >= length()) {
            if (CProverChecks.java8()) {
                throw new StringIndexOutOfBoundsException(index);
            }
            CProverChecks.checkIndex(index, length());
        }
        cproverSetCharAt(index, ch);
    }

    public StringBuilder append(Object obj) {
        return append(String.valueOf(obj));
    }

    /** Handled internally by CBMC (a null str appends "null"). */
    public StringBuilder append(String str) {
        return CProver.nondetWithoutNullForNotModelled();
    }

    /** Handled internally by CBMC (a null sb appends "null"). */
    public StringBuilder append(StringBuffer sb) {
        return CProver.nondetWithoutNullForNotModelled();
    }

    public StringBuilder append(CharSequence s) {
        if (s == null) {
            return append("null");
        }
        return append(s.toString());
    }

    public StringBuilder append(CharSequence s, int start, int end) {
        // (a null s stands for "null", in a call of its own, so that s
        // keeps a single target where it is not null)
        if (s == null) {
            return append("null", start, end);
        }
        if (CProverChecks.java8()) {
            if ((start < 0) || (start > end) || (end > s.length())) {
                throw new IndexOutOfBoundsException(
                    "start " + start + ", end " + end + ", s.length() "
                    + s.length());
            }
        } else {
            CProverChecks.checkRange(start, end, s.length());
        }
        return append(CProverString.substring(s.toString(), start, end));
    }

    public StringBuilder append(char[] str) {
        return append(CProverString.ofCharArray(str, 0, str.length));
    }

    public StringBuilder append(char[] str, int offset, int len) {
        int end = offset + len;
        if (CProverChecks.java8()) {
            // (System.arraycopy)
            if (offset < 0 || len < 0 || offset > str.length - len) {
                throw new ArrayIndexOutOfBoundsException();
            }
        } else {
            CProverChecks.checkRange(offset, end, str.length);
        }
        return append(CProverString.ofCharArray(str, offset, len));
    }

    public StringBuilder append(boolean b) {
        if (b) {
            return append("true");
        }
        return append("false");
    }

    /** Handled internally by CBMC. */
    public StringBuilder append(char c) {
        return CProver.nondetWithoutNullForNotModelled();
    }

    public StringBuilder append(int i) {
        return append(String.valueOf(i));
    }

    public StringBuilder appendCodePoint(int codePoint) {
        if (!Character.isValidCodePoint(codePoint)) {
            throw CProverChecks.invalidCodePoint(codePoint);
        }
        return cproverAppendCodePoint(codePoint);
    }

    public StringBuilder append(long lng) {
        return append(String.valueOf(lng));
    }

    public StringBuilder append(float f) {
        return append(String.valueOf(f));
    }

    public StringBuilder append(double d) {
        return append(String.valueOf(d));
    }

    public StringBuilder delete(int start, int end) {
        int count = length();
        if (CProverChecks.java8()) {
            if (start < 0) {
                throw new StringIndexOutOfBoundsException(start);
            }
            if (end > count) {
                end = count;
            }
            if (start > end) {
                throw new StringIndexOutOfBoundsException();
            }
        } else {
            if (end > count) {
                end = count;
            }
            CProverChecks.checkRangeSIOOBE(start, end, count);
        }
        return cproverDelete(start, end);
    }

    public StringBuilder deleteCharAt(int index) {
        if (index < 0 || index >= length()) {
            if (CProverChecks.java8()) {
                throw new StringIndexOutOfBoundsException(index);
            }
            CProverChecks.checkIndex(index, length());
        }
        return cproverDeleteCharAt(index);
    }

    public StringBuilder replace(int start, int end, String str) {
        int count = length();
        if (CProverChecks.java8()) {
            if (start < 0) {
                throw new StringIndexOutOfBoundsException(start);
            }
            if (start > count) {
                throw new StringIndexOutOfBoundsException("start > length()");
            }
            if (start > end) {
                throw new StringIndexOutOfBoundsException("start > end");
            }
            if (end > count) {
                end = count;
            }
        } else {
            if (end > count) {
                end = count;
            }
            CProverChecks.checkRangeSIOOBE(start, end, count);
        }
        // (a NullPointerException for a null str)
        int len = str.length();
        cproverDelete(start, end);
        return cproverInsert(start, str);
    }

    public String substring(int start) {
        return substring(start, length());
    }

    public CharSequence subSequence(int start, int end) {
        return substring(start, end);
    }

    public String substring(int start, int end) {
        int count = length();
        if (CProverChecks.java8()) {
            if (start < 0) {
                throw new StringIndexOutOfBoundsException(start);
            }
            if (end > count) {
                throw new StringIndexOutOfBoundsException(end);
            }
            if (start > end) {
                throw new StringIndexOutOfBoundsException(end - start);
            }
        } else {
            CProverChecks.checkRangeSIOOBE(start, end, count);
        }
        return cproverSubstring(start, end);
    }

    /** The check of an offset of an insert of {@code CharSequence}s. */
    private void checkInsertOffset(int offset) {
        if (offset < 0 || offset > length()) {
            if (CProverChecks.java8()) {
                throw new StringIndexOutOfBoundsException(offset);
            }
            CProverChecks.checkOffset(offset, length());
        }
    }

    public StringBuilder insert(int index, char[] str, int offset, int len) {
        if (CProverChecks.java8()) {
            if ((index < 0) || (index > length())) {
                throw new StringIndexOutOfBoundsException(index);
            }
            if ((offset < 0) || (len < 0) || (offset > str.length - len)) {
                throw new StringIndexOutOfBoundsException(
                    "offset " + offset + ", len " + len + ", str.length "
                    + str.length);
            }
        } else {
            checkInsertOffset(index);
            CProverChecks.checkRangeSIOOBE(offset, offset + len, str.length);
        }
        return cproverInsert(index, CProverString.ofCharArray(str, offset, len));
    }

    public StringBuilder insert(int offset, Object obj) {
        return insert(offset, String.valueOf(obj));
    }

    public StringBuilder insert(int offset, String str) {
        checkInsertOffset(offset);
        if (str == null) {
            return cproverInsert(offset, "null");
        }
        return cproverInsert(offset, str);
    }

    public StringBuilder insert(int offset, char[] str) {
        checkInsertOffset(offset);
        return cproverInsert(offset, CProverString.ofCharArray(str, 0, str.length));
    }

    public StringBuilder insert(int dstOffset, CharSequence s) {
        if (s == null) {
            return insert(dstOffset, "null");
        }
        if (s instanceof String) {
            return insert(dstOffset, (String) s);
        }
        return insert(dstOffset, s, 0, s.length());
    }

    public StringBuilder insert(int dstOffset, CharSequence s, int start,
                              int end) {
        if (s == null) {
            return insert(dstOffset, "null", start, end);
        }
        if (CProverChecks.java8()) {
            if ((dstOffset < 0) || (dstOffset > length())) {
                throw new IndexOutOfBoundsException("dstOffset " + dstOffset);
            }
            if ((start < 0) || (end < 0) || (start > end)
                || (end > s.length())) {
                throw new IndexOutOfBoundsException(
                    "start " + start + ", end " + end + ", s.length() "
                    + s.length());
            }
        } else {
            CProverChecks.checkOffset(dstOffset, length());
            CProverChecks.checkRange(start, end, s.length());
        }
        return cproverInsert(
            dstOffset, CProverString.substring(s.toString(), start, end));
    }

    public StringBuilder insert(int offset, boolean b) {
        return insert(offset, String.valueOf(b));
    }

    public StringBuilder insert(int offset, char c) {
        if (offset < 0 || offset > length()) {
            if (CProverChecks.java8()) {
                // (System.arraycopy)
                throw new ArrayIndexOutOfBoundsException();
            }
            CProverChecks.checkOffset(offset, length());
        }
        return cproverInsert(offset, String.valueOf(c));
    }

    public StringBuilder insert(int offset, int i) {
        return insert(offset, String.valueOf(i));
    }

    public StringBuilder insert(int offset, long l) {
        return insert(offset, String.valueOf(l));
    }

    public StringBuilder insert(int offset, float f) {
        return insert(offset, String.valueOf(f));
    }

    public StringBuilder insert(int offset, double d) {
        return insert(offset, String.valueOf(d));
    }

    public int indexOf(String str) {
        return indexOf(str, 0);
    }

    public int indexOf(String str, int fromIndex) {
        // (a NullPointerException for a null str)
        return toString().indexOf(str.toString(), fromIndex);
    }

    public int lastIndexOf(String str) {
        return lastIndexOf(str, length());
    }

    public int lastIndexOf(String str, int fromIndex) {
        return toString().lastIndexOf(str.toString(), fromIndex);
    }

    public StringBuilder reverse() {
        // (the surrogate pairs keep their order, as in Java)
        String s = toString();
        int size = s.length();
        cproverDelete(0, size);
        int i = size - 1;
        while (i >= 0) {
            char c = CProverString.charAt(s, i);
            if (Character.isLowSurrogate(c) && i > 0
                && Character.isHighSurrogate(CProverString.charAt(s, i - 1))) {
                append(CProverString.charAt(s, i - 1));
                append(c);
                i -= 2;
            } else {
                append(c);
                i -= 1;
            }
        }
        return this;
    }

    /** Handled internally by CBMC: a copy of the sequence. */
    public String toString() {
        return CProver.nondetWithoutNullForNotModelled();
    }

    public java.util.stream.IntStream chars() {
        return toString().chars();
    }

    public java.util.stream.IntStream codePoints() {
        return toString().codePoints();
    }

    // The operations of the string solver (handled internally by CBMC).

    private char cproverCharAt(int index) {
        return CProver.nondetChar();
    }

    private int cproverCodePointAt(int index) {
        return CProver.nondetInt();
    }

    private int cproverCodePointBefore(int index) {
        return CProver.nondetInt();
    }

    private int cproverCodePointCount(int beginIndex, int endIndex) {
        return CProver.nondetInt();
    }

    private String cproverSubstring(int start, int end) {
        return CProver.nondetWithoutNullForNotModelled();
    }

    private StringBuilder cproverAppendCodePoint(int codePoint) {
        return CProver.nondetWithoutNullForNotModelled();
    }

    private StringBuilder cproverInsert(int offset, String str) {
        return CProver.nondetWithoutNullForNotModelled();
    }

    private StringBuilder cproverDelete(int start, int end) {
        return CProver.nondetWithoutNullForNotModelled();
    }

    private StringBuilder cproverDeleteCharAt(int index) {
        return CProver.nondetWithoutNullForNotModelled();
    }

    private void cproverSetCharAt(int index, char ch) {
    }

    private void cproverSetLength(int newLength) {
    }
}
