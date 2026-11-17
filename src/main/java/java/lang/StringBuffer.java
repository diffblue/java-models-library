/*
 * Copyright (c) 1994, 2013, Oracle and/or its affiliates. All rights reserved.
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

// import java.util.Arrays;
import org.cprover.CProver;
import org.cprover.CProverString;

/**
 * A thread-safe, mutable sequence of characters.
 * A string buffer is like a {@link String}, but can be modified. At any
 * point in time it contains some particular sequence of characters, but
 * the length and content of the sequence can be changed through certain
 * method calls.
 * <p>
 * String buffers are safe for use by multiple threads. The methods
 * are synchronized where necessary so that all the operations on any
 * particular instance behave as if they occur in some serial order
 * that is consistent with the order of the method calls made by each of
 * the individual threads involved.
 * <p>
 * The principal operations on a {@code StringBuffer} are the
 * {@code append} and {@code insert} methods, which are
 * overloaded so as to accept data of any type. Each effectively
 * converts a given datum to a string and then appends or inserts the
 * characters of that string to the string buffer. The
 * {@code append} method always adds these characters at the end
 * of the buffer; the {@code insert} method adds the characters at
 * a specified point.
 * <p>
 * For example, if {@code z} refers to a string buffer object
 * whose current contents are {@code "start"}, then
 * the method call {@code z.append("le")} would cause the string
 * buffer to contain {@code "startle"}, whereas
 * {@code z.insert(4, "le")} would alter the string buffer to
 * contain {@code "starlet"}.
 * <p>
 * In general, if sb refers to an instance of a {@code StringBuffer},
 * then {@code sb.append(x)} has the same effect as
 * {@code sb.insert(sb.length(), x)}.
 * <p>
 * Whenever an operation occurs involving a source sequence (such as
 * appending or inserting from a source sequence), this class synchronizes
 * only on the string buffer performing the operation, not on the source.
 * Note that while {@code StringBuffer} is designed to be safe to use
 * concurrently from multiple threads, if the constructor or the
 * {@code append} or {@code insert} operation is passed a source sequence
 * that is shared across threads, the calling code must ensure
 * that the operation has a consistent and unchanging view of the source
 * sequence for the duration of the operation.
 * This could be satisfied by the caller holding a lock during the
 * operation's call, by using an immutable source sequence, or by not
 * sharing the source sequence across threads.
 * <p>
 * Every string buffer has a capacity. As long as the length of the
 * character sequence contained in the string buffer does not exceed
 * the capacity, it is not necessary to allocate a new internal
 * buffer array. If the internal buffer overflows, it is
 * automatically made larger.
 * <p>
 * Unless otherwise noted, passing a {@code null} argument to a constructor
 * or method in this class will cause a {@link NullPointerException} to be
 * thrown.
 * <p>
 * As of  release JDK 5, this class has been supplemented with an equivalent
 * class designed for use by a single thread, {@link StringBuilder}.  The
 * {@code StringBuilder} class should generally be used in preference to
 * this one, as it supports all of the same operations but it is faster, as
 * it performs no synchronization.
 *
 * @author      Arthur van Hoff
 * @see     java.lang.StringBuilder
 * @see     java.lang.String
 * @since   JDK1.0
 *
 * @diffblue.limitedSupport
 * Most methods are not supported yet.
 * The methods that are supported have limited support as they do not handle
 * exceptions.
 */
 public final class StringBuffer
    extends AbstractStringBuilder
    implements java.io.Serializable, CharSequence
{
    static final long serialVersionUID = 3388685877147921107L;

    // DIFFBLUE MODEL LIBRARY
    // The model follows AbstractStringBuilder of Java 8 to 25: its checks,
    // their exceptions and messages (CProverChecks), and "null" for null
    // arguments. The string solver does the rest: the methods marked
    // "Handled internally by CBMC" and the private cprover* methods are
    // replaced by JBMC (java_string_library_preprocess).

    /** Handled internally by CBMC: the empty sequence. */
    public StringBuffer() {
    }

    /** The empty sequence, after the check of the capacity. */
    public StringBuffer(int capacity) {
        this();
        if (capacity < 0) {
            // (Java allocates an array of the capacity)
            throw new NegativeArraySizeException(
                CProverChecks.negativeSizeMessage(capacity));
        }
    }

    /** Handled internally by CBMC: a copy of {@code str}. */
    public StringBuffer(String str) {
    }

    public StringBuffer(CharSequence seq) {
        this(seq.toString());
    }

    public synchronized int compareTo(StringBuffer another) {
        return toString().compareTo(another.toString());
    }

    /** Handled internally by CBMC. */
    public synchronized int length() {
        return CProver.nondetInt();
    }

    public synchronized int capacity() {
        // (any capacity that holds the sequence)
        int capacity = CProver.nondetInt();
        CProver.assume(capacity >= length());
        return capacity;
    }

    public synchronized void ensureCapacity(int minimumCapacity) {
    }

    public synchronized void trimToSize() {
    }

    public synchronized void setLength(int newLength) {
        if (newLength < 0) {
            throw new StringIndexOutOfBoundsException(newLength);
        }
        cproverSetLength(newLength);
    }

    public synchronized char charAt(int index) {
        if (index < 0 || index >= length()) {
            if (CProverChecks.java8()) {
                throw new StringIndexOutOfBoundsException(index);
            }
            CProverChecks.checkIndex(index, length());
        }
        return cproverCharAt(index);
    }

    public synchronized int codePointAt(int index) {
        if (index < 0 || index >= length()) {
            if (CProverChecks.java8()) {
                throw new StringIndexOutOfBoundsException(index);
            }
            CProverChecks.checkIndex(index, length());
        }
        return cproverCodePointAt(index);
    }

    public synchronized int codePointBefore(int index) {
        int i = index - 1;
        if (i < 0 || i >= length()) {
            if (CProverRuntime.version() <= 17) {
                throw new StringIndexOutOfBoundsException(index);
            }
            CProverChecks.checkIndex(i, length());
        }
        return cproverCodePointBefore(index);
    }

    public synchronized int codePointCount(int beginIndex, int endIndex) {
        if (beginIndex < 0 || endIndex > length() || beginIndex > endIndex) {
            if (CProverRuntime.version() <= 17) {
                throw new IndexOutOfBoundsException();
            }
            throw CProverChecks.index(CProverChecks.RANGE, beginIndex,
                endIndex, length());
        }
        return cproverCodePointCount(beginIndex, endIndex);
    }

    public synchronized int offsetByCodePoints(int index, int codePointOffset) {
        if (index < 0 || index > length()) {
            throw new IndexOutOfBoundsException();
        }
        return toString().offsetByCodePoints(index, codePointOffset);
    }

    public synchronized void getChars(int srcBegin, int srcEnd, char[] dst,
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

    public synchronized void setCharAt(int index, char ch) {
        if (index < 0 || index >= length()) {
            if (CProverChecks.java8()) {
                throw new StringIndexOutOfBoundsException(index);
            }
            CProverChecks.checkIndex(index, length());
        }
        cproverSetCharAt(index, ch);
    }

    public synchronized StringBuffer append(Object obj) {
        return append(String.valueOf(obj));
    }

    /** Handled internally by CBMC (a null str appends "null"). */
    public synchronized StringBuffer append(String str) {
        return CProver.nondetWithoutNullForNotModelled();
    }

    /** Handled internally by CBMC (a null sb appends "null"). */
    public synchronized StringBuffer append(StringBuffer sb) {
        return CProver.nondetWithoutNullForNotModelled();
    }

    public synchronized StringBuffer append(CharSequence s) {
        if (s == null) {
            return append("null");
        }
        return append(s.toString());
    }

    public synchronized StringBuffer append(CharSequence s, int start, int end) {
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

    public synchronized StringBuffer append(char[] str) {
        return append(CProverString.ofCharArray(str, 0, str.length));
    }

    public synchronized StringBuffer append(char[] str, int offset, int len) {
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

    public synchronized StringBuffer append(boolean b) {
        if (b) {
            return append("true");
        }
        return append("false");
    }

    /** Handled internally by CBMC. */
    public synchronized StringBuffer append(char c) {
        return CProver.nondetWithoutNullForNotModelled();
    }

    public synchronized StringBuffer append(int i) {
        return append(String.valueOf(i));
    }

    public synchronized StringBuffer appendCodePoint(int codePoint) {
        if (!Character.isValidCodePoint(codePoint)) {
            throw CProverChecks.invalidCodePoint(codePoint);
        }
        return cproverAppendCodePoint(codePoint);
    }

    public synchronized StringBuffer append(long lng) {
        return append(String.valueOf(lng));
    }

    public synchronized StringBuffer append(float f) {
        return append(String.valueOf(f));
    }

    public synchronized StringBuffer append(double d) {
        return append(String.valueOf(d));
    }

    public synchronized StringBuffer delete(int start, int end) {
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

    public synchronized StringBuffer deleteCharAt(int index) {
        if (index < 0 || index >= length()) {
            if (CProverChecks.java8()) {
                throw new StringIndexOutOfBoundsException(index);
            }
            CProverChecks.checkIndex(index, length());
        }
        return cproverDeleteCharAt(index);
    }

    public synchronized StringBuffer replace(int start, int end, String str) {
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

    public synchronized String substring(int start) {
        return substring(start, length());
    }

    public synchronized CharSequence subSequence(int start, int end) {
        return substring(start, end);
    }

    public synchronized String substring(int start, int end) {
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

    public synchronized StringBuffer insert(int index, char[] str, int offset, int len) {
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

    public synchronized StringBuffer insert(int offset, Object obj) {
        return insert(offset, String.valueOf(obj));
    }

    public synchronized StringBuffer insert(int offset, String str) {
        checkInsertOffset(offset);
        if (str == null) {
            return cproverInsert(offset, "null");
        }
        return cproverInsert(offset, str);
    }

    public synchronized StringBuffer insert(int offset, char[] str) {
        checkInsertOffset(offset);
        return cproverInsert(offset, CProverString.ofCharArray(str, 0, str.length));
    }

    public StringBuffer insert(int dstOffset, CharSequence s) {
        if (s == null) {
            return insert(dstOffset, "null");
        }
        if (s instanceof String) {
            return insert(dstOffset, (String) s);
        }
        return insert(dstOffset, s, 0, s.length());
    }

    public synchronized StringBuffer insert(int dstOffset, CharSequence s, int start,
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

    public StringBuffer insert(int offset, boolean b) {
        return insert(offset, String.valueOf(b));
    }

    public synchronized StringBuffer insert(int offset, char c) {
        if (offset < 0 || offset > length()) {
            if (CProverChecks.java8()) {
                // (System.arraycopy)
                throw new ArrayIndexOutOfBoundsException();
            }
            CProverChecks.checkOffset(offset, length());
        }
        return cproverInsert(offset, String.valueOf(c));
    }

    public StringBuffer insert(int offset, int i) {
        return insert(offset, String.valueOf(i));
    }

    public StringBuffer insert(int offset, long l) {
        return insert(offset, String.valueOf(l));
    }

    public StringBuffer insert(int offset, float f) {
        return insert(offset, String.valueOf(f));
    }

    public StringBuffer insert(int offset, double d) {
        return insert(offset, String.valueOf(d));
    }

    public int indexOf(String str) {
        return indexOf(str, 0);
    }

    public synchronized int indexOf(String str, int fromIndex) {
        // (a NullPointerException for a null str)
        return toString().indexOf(str.toString(), fromIndex);
    }

    public int lastIndexOf(String str) {
        return lastIndexOf(str, length());
    }

    public synchronized int lastIndexOf(String str, int fromIndex) {
        return toString().lastIndexOf(str.toString(), fromIndex);
    }

    public synchronized StringBuffer reverse() {
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
    public synchronized String toString() {
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

    private StringBuffer cproverAppendCodePoint(int codePoint) {
        return CProver.nondetWithoutNullForNotModelled();
    }

    private StringBuffer cproverInsert(int offset, String str) {
        return CProver.nondetWithoutNullForNotModelled();
    }

    private StringBuffer cproverDelete(int start, int end) {
        return CProver.nondetWithoutNullForNotModelled();
    }

    private StringBuffer cproverDeleteCharAt(int index) {
        return CProver.nondetWithoutNullForNotModelled();
    }

    private void cproverSetCharAt(int index, char ch) {
    }

    private void cproverSetLength(int newLength) {
    }
}
