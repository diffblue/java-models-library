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

    /** use serialVersionUID for interoperability */
    static final long serialVersionUID = 4383685877147921099L;

    /**
     * Constructs a string builder with no characters in it and an
     * initial capacity of 16 characters.
     *
     * @diffblue.fullSupport
     */
    public StringBuilder() {
        // DIFFBLUE MODEL LIBRARY modelled internally in CBMC
        // super(16);
    }

    /**
     * Constructs a string builder with no characters in it and an
     * initial capacity specified by the {@code capacity} argument.
     *
     * @param      capacity  the initial capacity.
     * @throws     NegativeArraySizeException  if the {@code capacity}
     *               argument is less than {@code 0}.
     *
     * @diffblue.noSupport
     */
    public StringBuilder(int capacity) {
        CProver.notModelled();
        // super(capacity);
    }

    /**
     * Constructs a string builder initialized to the contents of the
     * specified string. The initial capacity of the string builder is
     * {@code 16} plus the length of the string argument.
     *
     * @param   str   the initial contents of the buffer.
     *
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    public StringBuilder(String str) {
        // DIFFBLUE MODEL LIBRARY modelled internally in CBMC
        // super(str.length() + 16);
        // append(str);
    }

    /**
     * Constructs a string builder that contains the same characters
     * as the specified {@code CharSequence}. The initial capacity of
     * the string builder is {@code 16} plus the length of the
     * {@code CharSequence} argument.
     *
     * @param      seq   the sequence to copy.
     *
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    public StringBuilder(CharSequence seq) {
        append(seq);
        // this(seq.length() + 16);
        // append(seq);
    }

    /**
     * @diffblue.limitedSupport
     * This method can be slow to generate tests due to TG-2866 and is
     * also limited by which {@code toString()} methods have been modelled.
     */
    @Override
    public StringBuilder append(Object obj) {
        // return append(String.valueOf(obj));
        String temp = (obj == null) ? "null" : obj.toString();
        return append(temp);
    }

    /**
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    @Override
    public StringBuilder append(String str) {
        // DIFFBLUE MODEL LIBRARY: Handled internally by CBMC
        // super.append(str);
        // return this;
        return CProver.nondetWithNullForNotModelled();
    }

    /**
     * Appends the specified {@code StringBuffer} to this sequence.
     * <p>
     * The characters of the {@code StringBuffer} argument are appended,
     * in order, to this sequence, increasing the
     * length of this sequence by the length of the argument.
     * If {@code sb} is {@code null}, then the four characters
     * {@code "null"} are appended to this sequence.
     * <p>
     * Let <i>n</i> be the length of this character sequence just prior to
     * execution of the {@code append} method. Then the character at index
     * <i>k</i> in the new character sequence is equal to the character at
     * index <i>k</i> in the old character sequence, if <i>k</i> is less than
     * <i>n</i>; otherwise, it is equal to the character at index <i>k-n</i>
     * in the argument {@code sb}.
     *
     * @param   sb   the {@code StringBuffer} to append.
     * @return  a reference to this object.
     *
     * @diffblue.noSupport
     */
    public StringBuilder append(StringBuffer sb) {
        // super.append(sb);
        // return this;
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    /**
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    @Override
    public StringBuilder append(CharSequence s) {
        return append(s.toString());
        // super.append(s);
        // return this;
    }

    /**
     * @throws     IndexOutOfBoundsException {@inheritDoc}
     *
     * @diffblue.fullSupport
     * Thrown exception has no message.
     * @diffblue.untested Only exception throwing is tested.
    */
    @Override
    public StringBuilder append(CharSequence s, int start, int end) {
        // super.append(s, start, end);
        // return this;
        if ((start < 0) || (start > end) || (end > s.length())) {
            throw new IndexOutOfBoundsException();
        }

        return CProverString.append(this, s, start, end);
    }

    /**
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    @Override
    public StringBuilder append(char[] str) {
        // DIFFBLUE MODEL LIBRARY
        String string = CProverString.ofCharArray(str, 0, str.length);
        return append(string);
        // super.append(str);
        // return this;
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    @Override
    public StringBuilder append(char[] str, int offset, int len) {
        // DIFFBLUE MODEL LIBRARY
        String string = new String(str, offset, len);
        return append(string);
        // super.append(str, offset, len);
        // return this;
    }

    /**
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    @Override
    public StringBuilder append(boolean b) {
        // super.append(b);
        // return this;
        return append(String.valueOf(b));
    }

    /**
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    @Override
    public StringBuilder append(char c) {
        // super.append(c);
        // return this;
        return append(String.valueOf(c));
    }

    /**
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    @Override
    public StringBuilder append(int i) {
        // super.append(i);
        // return this;
        return append(String.valueOf(i));
    }

    /**
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    @Override
    public StringBuilder append(long lng) {
        // super.append(lng);
        // return this;
        return append(String.valueOf(lng));
    }

    /**
     * @diffblue.limitedSupport
     * Result string is an approximate the value.
     * @diffblue.untested
     */
    @Override
    public StringBuilder append(float f) {
        // super.append(f);
        // return this;
        return append(String.valueOf(f));
    }

    /**
     * @diffblue.limitedSupport
     * Result string is an approximate the value.
     * @diffblue.untested
     */
    @Override
    public StringBuilder append(double d) {
        // super.append(d);
        // return this;
        return append(String.valueOf(d));
    }

    /**
     * @since 1.5
     *
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    @Override
    public StringBuilder appendCodePoint(int codePoint) {
        // DIFFBLUE MODEL LIBRARY: Handled internally by CBMC
        // super.appendCodePoint(codePoint);
        // return this;
        return CProver.nondetWithNullForNotModelled();
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     *
     * @diffblue.fullSupport
     * @diffblue.untested Only exception throwing is tested.
     */
    @Override
    public StringBuilder delete(int start, int end) {
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
     * @diffblue.fullSupport
     * @diffblue.untested Only exception support is tested.
     * */
    @Override
    public StringBuilder deleteCharAt(int index) {
        // super.deleteCharAt(index);
        // return this;
        if ((index < 0) || (index >= this.length()))
            throw new StringIndexOutOfBoundsException(index);
        return CProverString.deleteCharAt(this, index);
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     *
     * @diffblue.noSupport
     */
    @Override
    public StringBuilder replace(int start, int end, String str) {
        // super.replace(start, end, str);
        // return this;
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     *
     * @diffblue.noSupport
     */
    @Override
    public StringBuilder insert(int index, char[] str, int offset,
                                int len)
    {
        // DIFFBLUE MODEL LIBRARY: Handled internally by CBMC
        // super.insert(index, str, offset, len);
        // return this;
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     *
     * @diffblue.noSupport
     */
    @Override
    public StringBuilder insert(int offset, Object obj) {
        // super.insert(offset, obj);
        // return this;
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     *
     * @diffblue.fullSupport
     * @diffblue.untested Only exception throwing is tested
     */
    @Override
    public StringBuilder insert(int offset, String str) {
        // super.insert(offset, str);
        // return this;
        if ((offset < 0) || (offset > length()))
            throw new StringIndexOutOfBoundsException(offset);
        return CProverString.insert(this, offset, str);
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     *
     * @diffblue.noSupport
     */
    @Override
    public StringBuilder insert(int offset, char[] str) {
        // super.insert(offset, str);
        // return this;
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     *
     * @diffblue.noSupport
     */
    @Override
    public StringBuilder insert(int dstOffset, CharSequence s) {
        // super.insert(dstOffset, s);
        // return this;
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     *
     * @diffblue.noSupport
     */
    @Override
    public StringBuilder insert(int dstOffset, CharSequence s,
                                int start, int end)
    {
        // super.insert(dstOffset, s, start, end);
        // return this;
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     *
     * @diffblue.fullSupport
     * @diffblue.untested Only exception throwing is tested
     */
    @Override
    public StringBuilder insert(int offset, boolean b) {
        // super.insert(offset, b);
        // return this;
        if ((offset < 0) || (offset > length()))
            throw new StringIndexOutOfBoundsException(offset);
        return CProverString.insert(this, offset, String.valueOf(b));
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     *
     * @diffblue.fullSupport
     * @diffblue.untested Only exception throwing is tested
     */
    @Override
    public StringBuilder insert(int offset, char c) {
        // super.insert(offset, c);
        // return this;
        if ((offset < 0) || (offset > length()))
            throw new IndexOutOfBoundsException();
        return CProverString.insert(this, offset, String.valueOf(c));
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     *
     * @diffblue.fullSupport
     * @diffblue.untested Only exception throwing is tested
     */
    @Override
    public StringBuilder insert(int offset, int i) {
        // super.insert(offset, i);
        // return this;
        if ((offset < 0) || (offset > length()))
            throw new StringIndexOutOfBoundsException(offset);
        return CProverString.insert(this, offset, String.valueOf(i));
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     *
     * @diffblue.fullSupport
     * @diffblue.untested
     * @diffblue.untested Only exception throwing is tested
     */
    @Override
    public StringBuilder insert(int offset, long l) {
        // super.insert(offset, l);
        // return this;
        if ((offset < 0) || (offset > length()))
            throw new StringIndexOutOfBoundsException(offset);
        return CProverString.insert(this, offset, String.valueOf(l));
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     *
     * @diffblue.limitedSupport
     * Value of the floating point is approximated.
     * @diffblue.untested Only exception throwing is tested
     */
    @Override
    public StringBuilder insert(int offset, float f) {
        // super.insert(offset, f);
        // return this;
        if ((offset < 0) || (offset > length()))
            throw new StringIndexOutOfBoundsException(offset);
        return CProverString.insert(this, offset, String.valueOf(f));
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     *
     * @diffblue.noSupport
     * Value of the floating point is approximated.
     * @diffblue.untested Only exception throwing is tested
     * @diffblue.todo Handle CProverString.insert(StringBuilder, int, double)
     * in jbmc.
     */
    @Override
    public StringBuilder insert(int offset, double d) {
        // super.insert(offset, d);
        // return this;
        if ((offset < 0) || (offset > length()))
            throw new StringIndexOutOfBoundsException(offset);
        return CProverString.insert(this, offset, String.valueOf(d));
    }

    /**
     * @diffblue.noSupport
     */
    @Override
    public int indexOf(String str) {
        // return super.indexOf(str);
        CProver.notModelled();
        return CProver.nondetInt();
    }

    /**
     * @diffblue.noSupport
     */
    @Override
    public int indexOf(String str, int fromIndex) {
        // return super.indexOf(str, fromIndex);
        CProver.notModelled();
        return CProver.nondetInt();
    }

    /**
     * @diffblue.noSupport
     */
    @Override
    public int lastIndexOf(String str) {
        // return super.lastIndexOf(str);
        CProver.notModelled();
        return CProver.nondetInt();
    }

    /**
     * @diffblue.noSupport
     */
    @Override
    public int lastIndexOf(String str, int fromIndex) {
        // return super.lastIndexOf(str, fromIndex);
        CProver.notModelled();
        return CProver.nondetInt();
    }

    /**
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    @Override
    public StringBuilder reverse() {
        // super.reverse();
        // return this;
        int size = this.length();
        if (size < 2)
          return this;
        String tmp = this.toString();
        CProverString.delete(this, 0, size);
        for (int i=size-1; i>=0; --i) {
            this.append(CProverString.charAt(tmp, i));
         }
        return this;
    }

    /**
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    @Override
    public String toString() {
        // DIFFBLUE MODEL LIBRARY: Handled internally by CBMC
        // // Create a copy, don't share the array
        // return new String(value, 0, count);
        return CProver.nondetWithNullForNotModelled();
    }

    /**
     * Save the state of the {@code StringBuilder} instance to a stream
     * (that is, serialize it).
     *
     * @serialData the number of characters currently stored in the string
     *             builder ({@code int}), followed by the characters in the
     *             string builder ({@code char[]}).   The length of the
     *             {@code char} array may be greater than the number of
     *             characters currently stored in the string builder, in which
     *             case extra characters are ignored.
     */
    // private void writeObject(java.io.ObjectOutputStream s)
    //     throws java.io.IOException {
    //     s.defaultWriteObject();
    //     s.writeInt(count);
    //     s.writeObject(value);
    // }

    /**
     * readObject is called to restore the state of the StringBuffer from
     * a stream.
     */
    // private void readObject(java.io.ObjectInputStream s)
    //     throws java.io.IOException, ClassNotFoundException {
    //     s.defaultReadObject();
    //     count = s.readInt();
    //     value = (char[]) s.readObject();
    // }

    // DIFFBLUE MODEL LIBRARY This should be inherited from AbstractStringBuilder
    /**
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    public void getChars(int srcBegin, int srcEnd, char dst[], int dstBegin) {
        toString().getChars(srcBegin, srcEnd, dst, dstBegin);
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @see    #length()
     *
     * @diffblue.fullSupport
     */
    @Override
    public void setLength(int newLength) {
        if (newLength < 0)
            throw new StringIndexOutOfBoundsException(newLength);
        CProverString.setLength(this, newLength);
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @see    #length()
     *
     * @diffblue.fullSupport
     */
    @Override
    public void setCharAt(int index, char ch) {
        if ((index < 0) || (index >= this.length()))
            throw new StringIndexOutOfBoundsException(index);
        CProverString.setCharAt(this, index, ch);
    }
}
