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

    /**
     * A cache of the last value returned by toString. Cleared
     * whenever the StringBuffer is modified.
     */
    // private transient char[] toStringCache;

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    static final long serialVersionUID = 3388685877147921107L;

    /**
     * Constructs a string buffer with no characters in it and an
     * initial capacity of 16 characters.
     *
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    public StringBuffer() {
        // DIFFBLUE MODEL LIBRARY this is replaced internally
        // super(16);
    }

    /**
     * Constructs a string buffer with no characters in it and
     * the specified initial capacity.
     *
     * @param      capacity  the initial capacity.
     * @exception  NegativeArraySizeException  if the {@code capacity}
     *               argument is less than {@code 0}.
     *
     * @diffblue.noSupport
     */
    public StringBuffer(int capacity) {
        // super(capacity);
    }

    /**
     * Constructs a string buffer initialized to the contents of the
     * specified string. The initial capacity of the string buffer is
     * {@code 16} plus the length of the string argument.
     *
     * @param   str   the initial contents of the buffer.
     *
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    public StringBuffer(String str) {
        // DIFFBLUE MODEL LIBRARY this is replaced internally
        // super(str.length() + 16);
        // append(str);
    }

    /**
     * Constructs a string buffer that contains the same characters
     * as the specified {@code CharSequence}. The initial capacity of
     * the string buffer is {@code 16} plus the length of the
     * {@code CharSequence} argument.
     * <p>
     * If the length of the specified {@code CharSequence} is
     * less than or equal to zero, then an empty buffer of capacity
     * {@code 16} is returned.
     *
     * @param      seq   the sequence to copy.
     * @since 1.5
     *
     * @diffblue.noSupport
     */
    public StringBuffer(CharSequence seq) {
        // this(seq.length() + 16);
        // append(seq);
        CProver.notModelled();
    }

    /**
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    @Override
    public synchronized int length() {
        // DIFFBLUE MODEL LIBRARY this is replaced internally
        // return count;
        return CProver.nondetInt();
    }

    /**
     * @diffblue.noSupport
     */
    @Override
    public synchronized int capacity() {
        // return value.length;
        CProver.notModelled();
        return CProver.nondetInt();
    }

    /**
     * @diffblue.noSupport
     */
    @Override
    public synchronized void ensureCapacity(int minimumCapacity) {
	// super.ensureCapacity(minimumCapacity);
        CProver.notModelled();
    }

    /**
     * @since      1.5
     *
     * @diffblue.noSupport
     */
    @Override
    public synchronized void trimToSize() {
        // super.trimToSize();
        CProver.notModelled();
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @see        #length()
     *
     * @diffblue.fullSupport
     * @diffblue.untested Only exception throwing is tested
     */
    @Override
    // TODO: this should be a call to the method of AbstractStringBuilder as in
    // the orginal implementation
    public synchronized void setLength(int newLength) {
        // toStringCache = null;
        // super.setLength(newLength);
        if (newLength < 0)
            throw new StringIndexOutOfBoundsException(newLength);
        CProverString.setLength(this, newLength);
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @see        #length()
     *
     * @diffblue.fullSupport
     * @diffblue.untested Only exception throwing is tested
     */
    @Override
    public synchronized char charAt(int index) {
        if ((index < 0) || (index >= this.length()))
            throw new StringIndexOutOfBoundsException(index);
        // return value[index];
        return CProverString.charAt(this, index);
    }

    /**
     * @since      1.5
     *
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    @Override
    public synchronized int codePointAt(int index) {
        // DIFFBLUE MODEL LIBRARY this is replaced internally
        // return super.codePointAt(index);
        return CProver.nondetInt();
    }

    /**
     * @since     1.5
     *
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    @Override
    public synchronized int codePointBefore(int index) {
        // DIFFBLUE MODEL LIBRARY this is replaced internally
        // return super.codePointBefore(index);
        return CProver.nondetInt();
    }

    /**
     * @since     1.5
     *
     * @diffblue.limitedSupport
     * The result is approximate.
     * @diffblue.untested
     */
    @Override
    public synchronized int codePointCount(int beginIndex, int endIndex) {
        // DIFFBLUE MODEL LIBRARY this is replaced internally
        // return super.codePointCount(beginIndex, endIndex);
        return CProver.nondetInt();
    }

    /**
     * @since     1.5
     *
     * @diffblue.noSupport
     */
    @Override
    public synchronized int offsetByCodePoints(int index, int codePointOffset) {
        // return super.offsetByCodePoints(index, codePointOffset);
        return CProver.nondetInt();
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     *
     * @diffblue.noSupport
     */
    @Override
    public synchronized void getChars(int srcBegin, int srcEnd, char[] dst,
                                      int dstBegin)
    {
        // super.getChars(srcBegin, srcEnd, dst, dstBegin);
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @see        #length()
     *
     * @diffblue.fullSupport
     * @diffblue.untested Only exception throwing is tested
     */
    @Override
    public synchronized void setCharAt(int index, char ch) {
        if ((index < 0) || (index >= this.length()))
            throw new StringIndexOutOfBoundsException(index);
        // toStringCache = null;
        // value[index] = ch;
        CProverString.setCharAt(this, index, ch);
    }

    /**
     * @diffblue.limitedSupport
     * This method can be slow to generate tests due to TG-2866 and is
     * also limited by which {@code toString()} methods have been modelled.
     */
    @Override
    public synchronized StringBuffer append(Object obj) {
        // toStringCache = null;
        // super.append(String.valueOf(obj));
        // return this;
        String temp = (obj == null) ? "null" : obj.toString();
        return append(temp);
    }

    /**
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    @Override
    public synchronized StringBuffer append(String str) {
        // DIFFBLUE MODEL LIBRARY this is replaced internally
        // toStringCache = null;
        // super.append(str);
        // return this;
        return CProver.nondetWithoutNullForNotModelled();
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
     *
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    public synchronized StringBuffer append(StringBuffer sb) {
        // DIFFBLUE MODEL LIBRARY this is replaced internally
        // toStringCache = null;
        // super.append(sb);
        // return this;
        return CProver.nondetWithoutNullForNotModelled();
    }

    /**
     * @since 1.8
     *
     * @diffblue.noSupport
     */
    @Override
    synchronized StringBuffer append(AbstractStringBuilder asb) {
        // toStringCache = null;
        // super.append(asb);
        // return this;
        CProver.notModelled();
        return CProver.nondetWithoutNullForNotModelled();
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
     *
     * @diffblue.fullSupport
     */
    @Override
    public synchronized StringBuffer append(CharSequence s) {
        // toStringCache = null;
        // super.append(s);
        // return this;
        String str = s.toString();
        return append(str);
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @since      1.5
     *
     * @diffblue.fullSupport
     */
    @Override
    public synchronized StringBuffer append(CharSequence s, int start, int end)
    {
        // toStringCache = null;
        // super.append(s, start, end);
        // return this;
        if ((start < 0) || (start > end) || (end > s.length())) {
            throw new IndexOutOfBoundsException();
        }
        String str = CProverString.substring(s.toString(), start, end);
        return append(str);
    }

    /**
     * @diffblue.fullSupport
     * Thrown exception has no message.
     */
    @Override
    public synchronized StringBuffer append(char[] str) {
        // toStringCache = null;
        // super.append(str);
        // return this;
        String s = "";
        for (int i = 0; i < str.length; i++) {
            s += str[i];
        }
        return append(s);
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     *
     * @diffblue.fullSupport
     * Thrown exception has no message.
     */
    @Override
    public synchronized StringBuffer append(char[] str, int offset, int len) {
        // toStringCache = null;
        // super.append(str, offset, len);
        // return this;
        if ((offset < 0) || (offset > str.length) || (len < 0) ||
            ((offset + len) > str.length) || ((offset + len) < 0)) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return this;
        }
        String s = "";
        for (int i = offset; i < offset+len; i++) {
            s += str[i];
        }
        return append(s);
    }

    /**
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    @Override
    public synchronized StringBuffer append(boolean b) {
        // DIFFBLUE MODEL LIBRARY this is replaced internally
        // toStringCache = null;
        // super.append(b);
        // return this;
        return CProver.nondetWithoutNullForNotModelled();
    }

    /**
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    @Override
    public synchronized StringBuffer append(char c) {
        // DIFFBLUE MODEL LIBRARY this is replaced internally
        // toStringCache = null;
        // super.append(c);
        // return this;
        return CProver.nondetWithoutNullForNotModelled();
    }

    /**
     * @diffblue.fullSupport
     */
    @Override
    public synchronized StringBuffer append(int i) {
        // toStringCache = null;
        // super.append(i);
        // return this;
        return append(String.valueOf(i));
    }

    /**
     * @since 1.5
     *
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    @Override
    public synchronized StringBuffer appendCodePoint(int codePoint) {
        // DIFFBLUE MODEL LIBRARY this is replaced internally
        // toStringCache = null;
        // super.appendCodePoint(codePoint);
        // return this;
        return CProver.nondetWithoutNullForNotModelled();
    }

    /**
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    @Override
    public synchronized StringBuffer append(long lng) {
        // DIFFBLUE MODEL LIBRARY this is replaced internally
        // toStringCache = null;
        // super.append(lng);
        // return this;
        return CProver.nondetWithoutNullForNotModelled();
    }

    /**
     * @diffblue.fullSupport
     */
    @Override
    public synchronized StringBuffer append(float f) {
        // toStringCache = null;
        // super.append(f);
        // return this;
        return append(String.valueOf(f));
    }

    /**
     * @diffblue.partialSupport This uses float approximation, so might return incorrect results
     */
    @Override
    public synchronized StringBuffer append(double d) {
        // toStringCache = null;
        // super.append(d);
        // return this;
        return append(String.valueOf(d));
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     * @since      1.2
     *
     * @diffblue.fullSupport
     * @diffblue.untested Only exception throwing is tested
     */
    @Override
    public synchronized StringBuffer delete(int start, int end) {
        // toStringCache = null;
        // super.delete(start, end);
        // return this;
        if (start < 0)
            throw new StringIndexOutOfBoundsException(start);
        if (end > this.length())
            end = this.length();
        if (start > end)
            throw new StringIndexOutOfBoundsException();
        return CProverString.delete(this, start, end);
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     * @since      1.2
     *
     * @diffblue.fullSupport
     * @diffblue.untested Only exception throwing is tested
     */
    @Override
    public synchronized StringBuffer deleteCharAt(int index) {
        // toStringCache = null;
        // super.deleteCharAt(index);
        // return this;
        if ((index < 0) || (index >= this.length()))
            throw new StringIndexOutOfBoundsException(index);
        return CProverString.deleteCharAt(this, index);
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     * @since      1.2
     *
     * @diffblue.noSupport
     */
    @Override
    public synchronized StringBuffer replace(int start, int end, String str) {
        // toStringCache = null;
        // super.replace(start, end, str);
        // return this;
        CProver.notModelled();
        return CProver.nondetWithoutNullForNotModelled();
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     * @since      1.2
     *
     * @diffblue.noSupport
     * Does not throw exceptions.
     */
    @Override
    public synchronized String substring(int start) {
        // return substring(start, count);
        CProver.notModelled();
        return CProver.nondetWithoutNullForNotModelled();
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @since      1.4
     *
     * @diffblue.noSupport
     */
    @Override
    public synchronized CharSequence subSequence(int start, int end) {
        // return super.substring(start, end);
        CProver.notModelled();
        return CProver.nondetWithoutNullForNotModelled();
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     * @since      1.2
     *
     * @diffblue.fullSupport
     * @diffblue.untested Only exception throwing is tested.
     */
    @Override
    public synchronized String substring(int start, int end) {
        // return super.substring(start, end);
        if (start < 0)
            throw new StringIndexOutOfBoundsException(start);
        if (end > this.length())
            throw new StringIndexOutOfBoundsException(end);
        if (start > end)
            throw new StringIndexOutOfBoundsException(end - start);
        return CProverString.substring(this, start, end);
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     * @since      1.2
     *
     * @diffblue.noSupport
     */
    @Override
    public synchronized StringBuffer insert(int index, char[] str, int offset,
                                            int len)
    {
        // toStringCache = null;
        // super.insert(index, str, offset, len);
        // return this;
        CProver.notModelled();
        return CProver.nondetWithoutNullForNotModelled();
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     *
     * @diffblue.noSupport
     */
    @Override
    public synchronized StringBuffer insert(int offset, Object obj) {
        // toStringCache = null;
        // super.insert(offset, String.valueOf(obj));
        // return this;
        CProver.notModelled();
        return this.insert(offset, obj.toString());
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     *
     * @diffblue.fullSupport
     * @diffblue.untested Only exception throwing is tested
     */
    @Override
    public synchronized StringBuffer insert(int offset, String str) {
        if ((offset < 0) || (offset > length()))
            throw new StringIndexOutOfBoundsException(offset);
        // toStringCache = null;
        // super.insert(offset, str);
        // return this;
        return CProverString.insert(this, offset, str);
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     *
     * @diffblue.noSupport
     */
    @Override
    public synchronized StringBuffer insert(int offset, char[] str) {
        // toStringCache = null;
        // super.insert(offset, str);
        // return this;
        CProver.notModelled();
        return CProver.nondetWithoutNullForNotModelled();
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @since      1.5
     *
     * @diffblue.noSupport
     */
    @Override
    public StringBuffer insert(int dstOffset, CharSequence s) {
        // // Note, synchronization achieved via invocations of other StringBuffer methods
        // // after narrowing of s to specific type
        // // Ditto for toStringCache clearing
        // super.insert(dstOffset, s);
        // return this;
        CProver.notModelled();
        return CProver.nondetWithoutNullForNotModelled();
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @since      1.5
     *
     * @diffblue.noSupport
     */
    @Override
    public synchronized StringBuffer insert(int dstOffset, CharSequence s,
            int start, int end)
    {
        // toStringCache = null;
        // super.insert(dstOffset, s, start, end);
        // return this;
        CProver.notModelled();
        return CProver.nondetWithoutNullForNotModelled();
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     *
     * @diffblue.fullSupport
     * @diffblue.untested Only exception throwing is tested.
     */
    @Override
    public  StringBuffer insert(int offset, boolean b) {
        // DIFFBLUE MODEL LIBRARY this is replaced internally
        // // Note, synchronization achieved via invocation of StringBuffer insert(int, String)
        // // after conversion of b to String by super class method
        // // Ditto for toStringCache clearing
        // super.insert(offset, b);
        // return this;
        if (offset < 0)
            throw new StringIndexOutOfBoundsException(offset);
        if (offset > this.length())
            throw new StringIndexOutOfBoundsException(this.length());
        return CProverString.insert(this, offset, b);
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     *
     * @diffblue.fullSupport
     * @diffblue.untested Only exception throwing is tested.
     */
    @Override
    public synchronized StringBuffer insert(int offset, char c) {
        // DIFFBLUE MODEL LIBRARY this is replaced internally
        // toStringCache = null;
        // super.insert(offset, c);
        // return this;
        if (offset < 0)
            throw new IndexOutOfBoundsException();
        if (offset > this.length())
            throw new IndexOutOfBoundsException();
        return CProverString.insert(this, offset, c);
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     *
     * @diffblue.fullSupport
     * @diffblue.untested Only exception throwing is tested.
     */
    @Override
    public StringBuffer insert(int offset, int i) {
        // DIFFBLUE MODEL LIBRARY this is replaced internally
        // // Note, synchronization achieved via invocation of StringBuffer insert(int, String)
        // // after conversion of i to String by super class method
        // // Ditto for toStringCache clearing
        // super.insert(offset, i);
        // return this;
        if (offset < 0)
            throw new StringIndexOutOfBoundsException(offset);
        if (offset > this.length())
            throw new StringIndexOutOfBoundsException(this.length());
        return CProverString.insert(this, offset, i);
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     *
     * @diffblue.fullSupport
     * @diffblue.untested Only exception throwing is tested.
     */
    @Override
    public StringBuffer insert(int offset, long l) {
        // DIFFBLUE MODEL LIBRARY this is replaced internally
        // // Note, synchronization achieved via invocation of StringBuffer insert(int, String)
        // // after conversion of l to String by super class method
        // // Ditto for toStringCache clearing
        // super.insert(offset, l);
        // return this;
        if (offset < 0)
            throw new StringIndexOutOfBoundsException(offset);
        if (offset > this.length())
            throw new StringIndexOutOfBoundsException(this.length());
        return CProverString.insert(this, offset, l);
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     *
     * @diffblue.noSupport
     */
    @Override
    public StringBuffer insert(int offset, float f) {
        // // Note, synchronization achieved via invocation of StringBuffer insert(int, String)
        // // after conversion of f to String by super class method
        // // Ditto for toStringCache clearing
        // super.insert(offset, f);
        // return this;
        CProver.notModelled();
        return CProver.nondetWithoutNullForNotModelled();
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     *
     * @diffblue.noSupport
     */
    @Override
    public StringBuffer insert(int offset, double d) {
        // // Note, synchronization achieved via invocation of StringBuffer insert(int, String)
        // // after conversion of d to String by super class method
        // // Ditto for toStringCache clearing
        // super.insert(offset, d);
        // return this;
        CProver.notModelled();
        return CProver.nondetWithoutNullForNotModelled();
    }

    /**
     * @since      1.4
     *
     * @diffblue.noSupport
     */
    @Override
    public int indexOf(String str) {
        // // Note, synchronization achieved via invocations of other StringBuffer methods
        // return super.indexOf(str);
        CProver.notModelled();
        return CProver.nondetInt();
    }

    /**
     * @since      1.4
     *
     * @diffblue.noSupport
     */
    @Override
    public synchronized int indexOf(String str, int fromIndex) {
        // return super.indexOf(str, fromIndex);
        CProver.notModelled();
        return CProver.nondetInt();
    }

    /**
     * @since      1.4
     *
     * @diffblue.noSupport
     */
    @Override
    public int lastIndexOf(String str) {
        // // Note, synchronization achieved via invocations of other StringBuffer methods
        // return lastIndexOf(str, count);
        CProver.notModelled();
        return CProver.nondetInt();
    }

    /**
     * @since      1.4
     *
     * @diffblue.noSupport
     */
    @Override
    public synchronized int lastIndexOf(String str, int fromIndex) {
        // return super.lastIndexOf(str, fromIndex);
        CProver.notModelled();
        return CProver.nondetInt();
    }

    /**
     * @since   JDK1.0.2
     *
     * @diffblue.noSupport
     */
    @Override
    public synchronized StringBuffer reverse() {
        // toStringCache = null;
        // super.reverse();
        // return this;
        CProver.notModelled();
        return CProver.nondetWithoutNullForNotModelled();
    }

    /**
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    @Override
    public synchronized String toString() {
        // DIFFBLUE MODEL LIBRARY this is replaced internally
        // if (toStringCache == null) {
        //     toStringCache = Arrays.copyOfRange(value, 0, count);
        // }
        // return new String(toStringCache, true);
        return CProver.nondetWithoutNullForNotModelled();
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
    // private static final java.io.ObjectStreamField[] serialPersistentFields =
    // {
    //     new java.io.ObjectStreamField("value", char[].class),
    //     new java.io.ObjectStreamField("count", Integer.TYPE),
    //     new java.io.ObjectStreamField("shared", Boolean.TYPE),
    // };

    /**
     * readObject is called to restore the state of the StringBuffer from
     * a stream.
     */
    // private synchronized void writeObject(java.io.ObjectOutputStream s)
    //     throws java.io.IOException {
    //     java.io.ObjectOutputStream.PutField fields = s.putFields();
    //     fields.put("value", value);
    //     fields.put("count", count);
    //     fields.put("shared", false);
    //     s.writeFields();
    // }

    /**
     * readObject is called to restore the state of the StringBuffer from
     * a stream.
     */
    // private void readObject(java.io.ObjectInputStream s)
    //     throws java.io.IOException, ClassNotFoundException {
    //     java.io.ObjectInputStream.GetField fields = s.readFields();
    //     value = (char[])fields.get("value", null);
    //     count = fields.get("count", 0);
    // }
}
