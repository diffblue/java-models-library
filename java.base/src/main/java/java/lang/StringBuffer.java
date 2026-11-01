/*
 * Copyright (c) 1994, 2023, Oracle and/or its affiliates. All rights reserved.
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

import org.cprover.CProver;
import org.cprover.CProverString;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serial;
import java.io.Serializable;
import java.io.StreamCorruptedException;

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
 * @apiNote
 * {@code StringBuffer} implements {@code Comparable} but does not override
 * {@link Object#equals equals}. Thus, the natural ordering of {@code StringBuffer}
 * is inconsistent with equals. Care should be exercised if {@code StringBuffer}
 * objects are used as keys in a {@code SortedMap} or elements in a {@code SortedSet}.
 * See {@link Comparable}, {@link java.util.SortedMap SortedMap}, or
 * {@link java.util.SortedSet SortedSet} for more information.
 *
 * @author      Arthur van Hoff
 * @see     java.lang.StringBuilder
 * @see     java.lang.String
 * @since   1.0
 *
 * @cprover.limitedSupport
 * Unlike the real class, several methods here must avoid the real
 * inherited {@code super.xxx()} logic and instead be composed directly
 * from {@code org.cprover.CProverString} helpers or other already-correct
 * methods on this class: the real bodies delegate to {@link
 * AbstractStringBuilder}, which cannot be made correct here (see its
 * class Javadoc). The dropped {@code toStringCache} field/bookkeeping is
 * moot either way since {@link #toString} is always replaced wholesale by
 * JBMC's string-refinement.
 */
 public final class StringBuffer
    extends AbstractStringBuilder
    implements Appendable, Serializable, Comparable<StringBuffer>, CharSequence
{

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    @Serial
    static final long serialVersionUID = 3388685877147921107L;

    /**
     * Constructs a string buffer with no characters in it and an
     * initial capacity of 16 characters.
     */
    public StringBuffer() {
        super(16);
    }

    /**
     * Constructs a string buffer with no characters in it and
     * the specified initial capacity.
     *
     * @param      capacity  the initial capacity.
     * @throws     NegativeArraySizeException  if the {@code capacity}
     *             argument is less than {@code 0}.
     *
     * @cprover.limitedSupport {@code capacity} is a pure pre-allocation
     *   performance hint with no observable effect, so it is discarded:
     *   this delegates to the no-arg constructor, which is directly
     *   intercepted (unlike this one).
     */
    public StringBuffer(int capacity) {
        this();
    }

    /**
     * Constructs a string buffer initialized to the contents of the
     * specified string. The initial capacity of the string buffer is
     * {@code 16} plus the length of the string argument.
     *
     * @param   str   the initial contents of the buffer.
     */
    public StringBuffer(String str) {
        super(str);
    }

    /**
     * Constructs a string buffer that contains the same characters
     * as the specified {@code CharSequence}. The initial capacity of
     * the string buffer is {@code 16} plus the length of the
     * {@code CharSequence} argument.
     *
     * @param      seq   the sequence to copy.
     * @since 1.5
     *
     * @cprover.fullSupport Delegates to the {@code String}-arg constructor,
     *   which is directly intercepted (unlike this one).
     */
    public StringBuffer(CharSequence seq) {
        this(seq == null ? "null" : seq.toString());
    }

    /**
     * Compares two {@code StringBuffer} instances lexicographically. This method
     * follows the same rules for lexicographical comparison as defined in the
     * {@linkplain java.lang.CharSequence#compare(java.lang.CharSequence,
     * java.lang.CharSequence)  CharSequence.compare(this, another)} method.
     *
     * <p>
     * For finer-grained, locale-sensitive String comparison, refer to
     * {@link java.text.Collator}.
     *
     * @implNote
     * This method synchronizes on {@code this}, the current object, but not
     * {@code StringBuffer another} with which {@code this StringBuffer} is compared.
     *
     * @param another the {@code StringBuffer} to be compared with
     *
     * @return  the value {@code 0} if this {@code StringBuffer} contains the same
     * character sequence as that of the argument {@code StringBuffer}; a negative integer
     * if this {@code StringBuffer} is lexicographically less than the
     * {@code StringBuffer} argument; or a positive integer if this {@code StringBuffer}
     * is lexicographically greater than the {@code StringBuffer} argument.
     *
     * @since 11
     *
     * @cprover.fullSupport
     */
    @Override
    public synchronized int compareTo(StringBuffer another) {
        int len1 = length();
        int len2 = another.length();
        int lim = Math.min(len1, len2);
        for (int i = 0; i < lim; i++) {
            char c1 = charAt(i);
            char c2 = another.charAt(i);
            if (c1 != c2) {
                return c1 - c2;
            }
        }
        return len1 - len2;
    }

    @Override
    public synchronized int length() {
        // Handled by JBMC's string-refinement (exact signature match);
        // this body is never executed.
        CProver.notModelled();
        return CProver.nondetInt();
    }

    /**
     * @cprover.noSupport There is no primitive to query the true capacity.
     */
    @Override
    public synchronized int capacity() {
        CProver.notModelled();
        return CProver.nondetInt();
    }

    /**
     * @cprover.noSupport See {@link #capacity()}.
     */
    @Override
    public synchronized void ensureCapacity(int minimumCapacity) {
        CProver.notModelled();
    }

    /**
     * @since      1.5
     *
     * @cprover.noSupport See {@link #capacity()}.
     */
    @Override
    public synchronized void trimToSize() {
        CProver.notModelled();
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @see        #length()
     *
     * @cprover.fullSupport
     */
    @Override
    public synchronized void setLength(int newLength) {
        if (newLength < 0) {
            throw new StringIndexOutOfBoundsException(newLength);
        }
        CProverString.setLength(this, newLength);
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @see        #length()
     *
     * @cprover.fullSupport Not directly intercepted for this class (unlike
     *   {@code StringBuilder.charAt}); composed from {@link
     *   CProverString#charAt(StringBuffer, int)}.
     */
    @Override
    public synchronized char charAt(int index) {
        if ((index < 0) || (index >= length())) {
            throw new StringIndexOutOfBoundsException(index);
        }
        return CProverString.charAt(this, index);
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @since      1.5
     *
     * @cprover.noSupport Handled by JBMC's string-refinement (exact
     *   signature match); this body is never executed.
     */
    @Override
    public synchronized int codePointAt(int index) {
        CProver.notModelled();
        return CProver.nondetInt();
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @since     1.5
     *
     * @cprover.noSupport Handled by JBMC's string-refinement (exact
     *   signature match); this body is never executed.
     */
    @Override
    public synchronized int codePointBefore(int index) {
        CProver.notModelled();
        return CProver.nondetInt();
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @since     1.5
     *
     * @cprover.noSupport Handled by JBMC's string-refinement (exact
     *   signature match); this body is never executed.
     */
    @Override
    public synchronized int codePointCount(int beginIndex, int endIndex) {
        CProver.notModelled();
        return CProver.nondetInt();
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @since     1.5
     *
     * @cprover.fullSupport No StringBuffer-specific primitive exists for
     *   this; composed from {@link #toString} (intercepted) and {@link
     *   CProverString#offsetByCodePoints(String, int, int)}.
     */
    @Override
    public synchronized int offsetByCodePoints(int index, int codePointOffset) {
        return CProverString.offsetByCodePoints(this.toString(), index, codePointOffset);
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     *
     * @cprover.fullSupport No primitive exists for this; composed from
     *   {@link #charAt} (which routes through {@link
     *   CProverString#charAt(StringBuffer, int)}).
     */
    @Override
    public synchronized void getChars(int srcBegin, int srcEnd, char[] dst,
                                      int dstBegin)
    {
        if (srcBegin < 0 || srcEnd > length() || srcBegin > srcEnd) {
            throw new IndexOutOfBoundsException();
        }
        for (int i = srcBegin; i < srcEnd; i++) {
            dst[dstBegin + i - srcBegin] = charAt(i);
        }
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @see        #length()
     *
     * @cprover.fullSupport
     */
    @Override
    public synchronized void setCharAt(int index, char ch) {
        if ((index < 0) || (index >= this.length())) {
            throw new StringIndexOutOfBoundsException(index);
        }
        CProverString.setCharAt(this, index, ch);
    }

    /**
     * @cprover.fullSupport
     */
    @Override
    public synchronized StringBuffer append(Object obj) {
        return append(obj == null ? "null" : obj.toString());
    }

    @Override
    public synchronized StringBuffer append(String str) {
        // Handled by JBMC's string-refinement (exact signature match);
        // this body is never executed.
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    /**
     * Appends the specified {@code StringBuffer} to this sequence.
     * <p>
     * The characters of the {@code StringBuffer} argument are appended,
     * in order, to the contents of this {@code StringBuffer}, increasing the
     * length of this {@code StringBuffer} by the length of the argument.
     * If {@code sb} is {@code null}, then the four characters
     * {@code "null"} are appended to this {@code StringBuffer}.
     * <p>
     * Let <i>n</i> be the length of the old character sequence, the one
     * contained in the {@code StringBuffer} just prior to execution of the
     * {@code append} method. Then the character at index <i>k</i> in
     * the new character sequence is equal to the character at index <i>k</i>
     * in the old character sequence, if <i>k</i> is less than <i>n</i>;
     * otherwise, it is equal to the character at index <i>k-n</i> in the
     * argument {@code sb}.
     * <p>
     * This method synchronizes on {@code this}, the destination
     * object, but does not synchronize on the source ({@code sb}).
     *
     * @param   sb   the {@code StringBuffer} to append.
     * @return  a reference to this object.
     * @since 1.4
     */
    public synchronized StringBuffer append(StringBuffer sb) {
        // Handled by JBMC's string-refinement (exact signature match);
        // this body is never executed.
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    /**
     * @since 1.8
     *
     * @cprover.noSupport There is no primitive to build an arbitrary
     *   AbstractStringBuilder append from.
     */
    @Override
    synchronized StringBuffer append(AbstractStringBuilder asb) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    /**
     * Appends the specified {@code CharSequence} to this
     * sequence.
     * <p>
     * The characters of the {@code CharSequence} argument are appended,
     * in order, increasing the length of this sequence by the length of the
     * argument.
     *
     * <p>The result of this method is exactly the same as if it were an
     * invocation of this.append(s, 0, s.length());
     *
     * <p>This method synchronizes on {@code this}, the destination
     * object, but does not synchronize on the source ({@code s}).
     *
     * <p>If {@code s} is {@code null}, then the four characters
     * {@code "null"} are appended.
     *
     * @param   s the {@code CharSequence} to append.
     * @return  a reference to this object.
     * @since 1.5
     */
    @Override
    public synchronized StringBuffer append(CharSequence s) {
        // Handled by JBMC's string-refinement (exact signature match);
        // this body is never executed.
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @since      1.5
     *
     * @cprover.fullSupport No primitive exists for a range-limited append
     *   to this class (only to {@code StringBuilder}); composed via {@link
     *   #toString} on {@code s} and {@link
     *   CProverString#substring(String, int, int)}.
     */
    @Override
    public synchronized StringBuffer append(CharSequence s, int start, int end)
    {
        if ((start < 0) || (start > end) || (end > s.length())) {
            throw new IndexOutOfBoundsException();
        }
        return append(CProverString.substring(s.toString(), start, end));
    }

    /**
     * @cprover.fullSupport
     */
    @Override
    public synchronized StringBuffer append(char[] str) {
        return append(CProverString.ofCharArray(str, 0, str.length));
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     *
     * @cprover.fullSupport
     */
    @Override
    public synchronized StringBuffer append(char[] str, int offset, int len) {
        return append(CProverString.ofCharArray(str, offset, len));
    }

    /**
     * @cprover.fullSupport
     */
    @Override
    public synchronized StringBuffer append(boolean b) {
        return append(String.valueOf(b));
    }

    @Override
    public synchronized StringBuffer append(char c) {
        // Handled by JBMC's string-refinement (exact signature match);
        // this body is never executed.
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    /**
     * @cprover.fullSupport
     */
    @Override
    public synchronized StringBuffer append(int i) {
        return append(CProverString.toString(i));
    }

    /**
     * @since 1.5
     */
    @Override
    public synchronized StringBuffer appendCodePoint(int codePoint) {
        // Handled by JBMC's string-refinement (exact signature match);
        // this body is never executed.
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    /**
     * @cprover.fullSupport
     */
    @Override
    public synchronized StringBuffer append(long lng) {
        return append(CProverString.toString(lng));
    }

    /**
     * @cprover.limitedSupport Result string approximates the value: see
     *   {@link CProverString#toString(float)}.
     */
    @Override
    public synchronized StringBuffer append(float f) {
        return append(CProverString.toString(f));
    }

    /**
     * @cprover.limitedSupport Result string approximates the value: see
     *   {@link CProverString#toString(double)}.
     */
    @Override
    public synchronized StringBuffer append(double d) {
        return append(CProverString.toString(d));
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     * @since      1.2
     *
     * @cprover.fullSupport
     */
    @Override
    public synchronized StringBuffer delete(int start, int end) {
        if (start < 0) {
            throw new StringIndexOutOfBoundsException(start);
        }
        if (end > this.length()) {
            end = this.length();
        }
        if (start > end) {
            throw new StringIndexOutOfBoundsException();
        }
        return CProverString.delete(this, start, end);
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     * @since      1.2
     *
     * @cprover.fullSupport
     */
    @Override
    public synchronized StringBuffer deleteCharAt(int index) {
        if ((index < 0) || (index >= this.length())) {
            throw new StringIndexOutOfBoundsException(index);
        }
        return CProverString.deleteCharAt(this, index);
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     * @since      1.2
     *
     * @cprover.noSupport There is no primitive to build a replace
     *   operation from.
     */
    @Override
    public synchronized StringBuffer replace(int start, int end, String str) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     * @since      1.2
     */
    @Override
    public synchronized String substring(int start) {
        return substring(start, length());
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @since      1.4
     *
     * @cprover.fullSupport
     */
    @Override
    public synchronized CharSequence subSequence(int start, int end) {
        return substring(start, end);
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     * @since      1.2
     *
     * @cprover.fullSupport No {@code substring(II)} table entry exists
     *   for this class (unlike {@code substring(I)}); composed from {@link
     *   CProverString#substring(StringBuffer, int, int)}.
     */
    @Override
    public synchronized String substring(int start, int end) {
        return CProverString.substring(this, start, end);
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     * @since      1.2
     *
     * @cprover.fullSupport
     */
    @Override
    public synchronized StringBuffer insert(int index, char[] str, int offset,
                                            int len)
    {
        return insert(index, CProverString.ofCharArray(str, offset, len));
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     *
     * @cprover.fullSupport
     */
    @Override
    public synchronized StringBuffer insert(int offset, Object obj) {
        return insert(offset, String.valueOf(obj));
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     *
     * @cprover.fullSupport
     */
    @Override
    public synchronized StringBuffer insert(int offset, String str) {
        if ((offset < 0) || (offset > length())) {
            throw new StringIndexOutOfBoundsException(offset);
        }
        return CProverString.insert(this, offset, str);
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     *
     * @cprover.fullSupport
     */
    @Override
    public synchronized StringBuffer insert(int offset, char[] str) {
        return insert(offset, CProverString.ofCharArray(str, 0, str.length));
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @since      1.5
     *
     * @cprover.fullSupport
     */
    @Override
    public synchronized StringBuffer insert(int dstOffset, CharSequence s) {
        return insert(dstOffset, s == null ? "null" : s.toString());
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @since      1.5
     *
     * @cprover.fullSupport No primitive exists for a range-limited
     *   CharSequence insert; composed from {@link #toString} on {@code s}
     *   and {@link CProverString#substring(String, int, int)}.
     */
    @Override
    public synchronized StringBuffer insert(int dstOffset, CharSequence s,
            int start, int end)
    {
        if (s == null) {
            s = "null";
        }
        if ((start < 0) || (end < 0) || (start > end) || (end > s.length())
                || (dstOffset < 0) || (dstOffset > length())) {
            throw new IndexOutOfBoundsException();
        }
        return insert(dstOffset, CProverString.substring(s.toString(), start, end));
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     *
     * @cprover.fullSupport
     */
    @Override
    public synchronized StringBuffer insert(int offset, boolean b) {
        if ((offset < 0) || (offset > length())) {
            throw new StringIndexOutOfBoundsException(offset);
        }
        return CProverString.insert(this, offset, b);
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     *
     * @cprover.fullSupport
     */
    @Override
    public synchronized StringBuffer insert(int offset, char c) {
        if ((offset < 0) || (offset > length())) {
            throw new IndexOutOfBoundsException();
        }
        return CProverString.insert(this, offset, c);
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     *
     * @cprover.fullSupport
     */
    @Override
    public synchronized StringBuffer insert(int offset, int i) {
        if ((offset < 0) || (offset > length())) {
            throw new StringIndexOutOfBoundsException(offset);
        }
        return CProverString.insert(this, offset, i);
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     *
     * @cprover.fullSupport
     */
    @Override
    public synchronized StringBuffer insert(int offset, long l) {
        if ((offset < 0) || (offset > length())) {
            throw new StringIndexOutOfBoundsException(offset);
        }
        return CProverString.insert(this, offset, l);
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     *
     * @cprover.limitedSupport No {@code insert(StringBuffer,int,float)}
     *   primitive exists; converts via {@link
     *   CProverString#toString(float)} (which approximates the value) and
     *   then inserts the resulting String.
     */
    @Override
    public synchronized StringBuffer insert(int offset, float f) {
        return insert(offset, CProverString.toString(f));
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     *
     * @cprover.limitedSupport No {@code insert(StringBuffer,int,double)}
     *   primitive exists; converts via {@link
     *   CProverString#toString(double)} (which approximates the value) and
     *   then inserts the resulting String.
     */
    @Override
    public synchronized StringBuffer insert(int offset, double d) {
        return insert(offset, CProverString.toString(d));
    }

    /**
     * @since      1.4
     *
     * @cprover.noSupport There is no primitive to build an indexOf
     *   operation from.
     */
    @Override
    public synchronized int indexOf(String str) {
        CProver.notModelled();
        return CProver.nondetInt();
    }

    /**
     * @since      1.4
     *
     * @cprover.noSupport See {@link #indexOf(String)}.
     */
    @Override
    public synchronized int indexOf(String str, int fromIndex) {
        CProver.notModelled();
        return CProver.nondetInt();
    }

    /**
     * @since      1.4
     *
     * @cprover.noSupport See {@link #indexOf(String)}.
     */
    @Override
    public synchronized int lastIndexOf(String str) {
        CProver.notModelled();
        return CProver.nondetInt();
    }

    /**
     * @since      1.4
     *
     * @cprover.noSupport See {@link #indexOf(String)}.
     */
    @Override
    public synchronized int lastIndexOf(String str, int fromIndex) {
        CProver.notModelled();
        return CProver.nondetInt();
    }

    /**
     * @since   1.0.2
     *
     * @cprover.fullSupport
     */
    @Override
    public synchronized StringBuffer reverse() {
        int size = this.length();
        if (size < 2) {
            return this;
        }
        String tmp = this.toString();
        CProverString.delete(this, 0, size);
        for (int i = size - 1; i >= 0; --i) {
            this.append(CProverString.charAt(tmp, i));
        }
        return this;
    }

    /**
     * @throws IllegalArgumentException {@inheritDoc}
     *
     * @since 21
     *
     * @cprover.fullSupport
     */
    @Override
    public synchronized StringBuffer repeat(int codePoint, int count) {
        if (!Character.isValidCodePoint(codePoint)) {
            throw new IllegalArgumentException(
                String.format("Not a valid Unicode code point: 0x%X", codePoint));
        }
        if (count < 0) {
            throw new IllegalArgumentException("count is negative: " + count);
        }
        for (int i = 0; i < count; i++) {
            appendCodePoint(codePoint);
        }
        return this;
    }

    /**
     * @throws IllegalArgumentException {@inheritDoc}
     *
     * @since 21
     *
     * @cprover.fullSupport
     */
    @Override
    public synchronized StringBuffer repeat(CharSequence cs, int count) {
        if (count < 0) {
            throw new IllegalArgumentException("count is negative: " + count);
        }
        for (int i = 0; i < count; i++) {
            append(cs);
        }
        return this;
    }

    @Override
    public synchronized String toString() {
        // Handled by JBMC's string-refinement (exact signature match);
        // this body is never executed.
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    /**
     * Serializable fields for StringBuffer.
     *
     * @serialField value  char[]
     *              The backing character array of this StringBuffer.
     * @serialField count int
     *              The number of characters in this StringBuffer.
     * @serialField shared  boolean
     *              A flag indicating whether the backing array is shared.
     *              The value is ignored upon deserialization.
     */
    @Serial
    private static final ObjectStreamField[] serialPersistentFields =
    {
        new ObjectStreamField("value", char[].class),
        new ObjectStreamField("count", Integer.TYPE),
        new ObjectStreamField("shared", Boolean.TYPE),
    };

    /**
     * The {@code writeObject} method is called to write the state of the
     * {@code StringBuffer} to a stream.
     *
     * @param  s the {@code ObjectOutputStream} to which data is written
     * @throws IOException if an I/O error occurs
     */
    @Serial
    private synchronized void writeObject(ObjectOutputStream s)
            throws IOException {
        ObjectOutputStream.PutField fields = s.putFields();
        char[] val = new char[capacity()];
        if (isLatin1()) {
            StringLatin1.getChars(value, 0, count, val, 0);
        } else {
            StringUTF16.getChars(value, 0, count, val, 0);
        }
        fields.put("value", val);
        fields.put("count", count);
        fields.put("shared", false);
        s.writeFields();
    }

    /**
     * The {@code readObject} method is called to restore the state of the
     * {@code StringBuffer} from a stream.
     *
     * @param  s the {@code ObjectInputStream} from which data is read
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if a serialized class cannot be loaded
     */
    @Serial
    private void readObject(ObjectInputStream s)
        throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField fields = s.readFields();

        char[] val = (char[])fields.get("value", null);
        int c = fields.get("count", 0);
        if (c < 0 || c > val.length) {
            throw new StreamCorruptedException("count value invalid");
        }
        initBytes(val, 0, val.length);
        count = c;
        // ignore shared field
    }

    synchronized void getBytes(byte[] dst, int dstBegin, byte coder) {
        super.getBytes(dst, dstBegin, coder);
    }
}
