/*
 * Copyright (c) 2003, 2023, Oracle and/or its affiliates. All rights reserved.
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
import java.io.Serial;
import java.io.StreamCorruptedException;

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
 * @apiNote
 * {@code StringBuilder} implements {@code Comparable} but does not override
 * {@link Object#equals equals}. Thus, the natural ordering of {@code StringBuilder}
 * is inconsistent with equals. Care should be exercised if {@code StringBuilder}
 * objects are used as keys in a {@code SortedMap} or elements in a {@code SortedSet}.
 * See {@link Comparable}, {@link java.util.SortedMap SortedMap}, or
 * {@link java.util.SortedSet SortedSet} for more information.
 *
 * @author      Michael McCloskey
 * @see         java.lang.StringBuffer
 * @see         java.lang.String
 * @since       1.5
 *
 * @cprover.limitedSupport
 * Unlike the real class, this one must explicitly re-declare every method
 * that JBMC's string-refinement intercepts by exact class+signature
 * (length, charAt, codePointAt/Before/Count, substring), since real JDK 21
 * leaves those inherited from {@link AbstractStringBuilder}, but JBMC's
 * interception is keyed on the symbol actually invoked, which for an
 * inherited method is {@code AbstractStringBuilder}'s (unmodelled) one, not
 * {@code StringBuilder}'s. See {@link AbstractStringBuilder}'s class
 * Javadoc for the full explanation.
 */
public final class StringBuilder
    extends AbstractStringBuilder
    implements Appendable, java.io.Serializable, Comparable<StringBuilder>, CharSequence
{

    /** use serialVersionUID for interoperability */
    @Serial
    static final long serialVersionUID = 4383685877147921099L;

    /**
     * Constructs a string builder with no characters in it and an
     * initial capacity of 16 characters.
     */
    public StringBuilder() {
        super(16);
    }

    /**
     * Constructs a string builder with no characters in it and an
     * initial capacity specified by the {@code capacity} argument.
     *
     * @param      capacity  the initial capacity.
     * @throws     NegativeArraySizeException  if the {@code capacity}
     *               argument is less than {@code 0}.
     */
    public StringBuilder(int capacity) {
        super(capacity);
    }

    /**
     * Constructs a string builder initialized to the contents of the
     * specified string. The initial capacity of the string builder is
     * {@code 16} plus the length of the string argument.
     *
     * @param   str   the initial contents of the buffer.
     */
    public StringBuilder(String str) {
        super(str);
    }

    /**
     * Constructs a string builder that contains the same characters
     * as the specified {@code CharSequence}. The initial capacity of
     * the string builder is {@code 16} plus the length of the
     * {@code CharSequence} argument.
     *
     * @param      seq   the sequence to copy.
     */
    public StringBuilder(CharSequence seq) {
        super(seq);
    }

    /**
     * Compares two {@code StringBuilder} instances lexicographically. This method
     * follows the same rules for lexicographical comparison as defined in the
     * {@linkplain java.lang.CharSequence#compare(java.lang.CharSequence,
     * java.lang.CharSequence)  CharSequence.compare(this, another)} method.
     *
     * <p>
     * For finer-grained, locale-sensitive String comparison, refer to
     * {@link java.text.Collator}.
     *
     * @param another the {@code StringBuilder} to be compared with
     *
     * @return  the value {@code 0} if this {@code StringBuilder} contains the same
     * character sequence as that of the argument {@code StringBuilder}; a negative integer
     * if this {@code StringBuilder} is lexicographically less than the
     * {@code StringBuilder} argument; or a positive integer if this {@code StringBuilder}
     * is lexicographically greater than the {@code StringBuilder} argument.
     *
     * @since 11
     *
     * @cprover.fullSupport
     */
    @Override
    public int compareTo(StringBuilder another) {
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
    public StringBuilder append(Object obj) {
        String temp = (obj == null) ? "null" : obj.toString();
        return append(temp);
    }

    @Override
    public StringBuilder append(String str) {
        // Handled by JBMC's string-refinement (exact signature match);
        // this body is never executed.
        CProver.notModelled();
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
     * @cprover.fullSupport
     */
    public StringBuilder append(StringBuffer sb) {
        // Handled by JBMC's string-refinement (exact signature match);
        // this body is never executed.
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    @Override
    public StringBuilder append(CharSequence s) {
        // Handled by JBMC's string-refinement (exact signature match);
        // this body is never executed.
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    /**
     * @throws     IndexOutOfBoundsException {@inheritDoc}
     *
     * @cprover.fullSupport
     */
    @Override
    public StringBuilder append(CharSequence s, int start, int end) {
        if ((start < 0) || (start > end) || (end > s.length())) {
            throw new IndexOutOfBoundsException();
        }
        return CProverString.append(this, s, start, end);
    }

    /**
     * @cprover.fullSupport
     */
    @Override
    public StringBuilder append(char[] str) {
        return append(CProverString.ofCharArray(str, 0, str.length));
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     *
     * @cprover.fullSupport
     */
    @Override
    public StringBuilder append(char[] str, int offset, int len) {
        return append(CProverString.ofCharArray(str, offset, len));
    }

    /**
     * @cprover.fullSupport
     */
    @Override
    public StringBuilder append(boolean b) {
        return append(String.valueOf(b));
    }

    @Override
    public StringBuilder append(char c) {
        // Handled by JBMC's string-refinement (exact signature match);
        // this body is never executed.
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    /**
     * @cprover.fullSupport
     */
    @Override
    public StringBuilder append(int i) {
        return append(CProverString.toString(i));
    }

    /**
     * @cprover.fullSupport
     */
    @Override
    public StringBuilder append(long lng) {
        return append(CProverString.toString(lng));
    }

    /**
     * @cprover.limitedSupport Result string approximates the value: see
     *   {@link CProverString#toString(float)}.
     */
    @Override
    public StringBuilder append(float f) {
        return append(CProverString.toString(f));
    }

    /**
     * @cprover.limitedSupport Result string approximates the value: see
     *   {@link CProverString#toString(double)}.
     */
    @Override
    public StringBuilder append(double d) {
        return append(CProverString.toString(d));
    }

    /**
     * @since 1.5
     */
    @Override
    public StringBuilder appendCodePoint(int codePoint) {
        // Handled by JBMC's string-refinement (exact signature match);
        // this body is never executed.
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     *
     * @cprover.fullSupport
     */
    @Override
    public StringBuilder delete(int start, int end) {
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
     *
     * @cprover.fullSupport
     */
    @Override
    public StringBuilder deleteCharAt(int index) {
        if ((index < 0) || (index >= this.length())) {
            throw new StringIndexOutOfBoundsException(index);
        }
        return CProverString.deleteCharAt(this, index);
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     *
     * @cprover.noSupport There is no primitive to build a replace operation
     *   from.
     */
    @Override
    public StringBuilder replace(int start, int end, String str) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     *
     * @cprover.fullSupport
     */
    @Override
    public StringBuilder insert(int index, char[] str, int offset,
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
    public StringBuilder insert(int offset, Object obj) {
        return insert(offset, String.valueOf(obj));
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     *
     * @cprover.fullSupport
     */
    @Override
    public StringBuilder insert(int offset, String str) {
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
    public StringBuilder insert(int offset, char[] str) {
        return insert(offset, CProverString.ofCharArray(str, 0, str.length));
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     *
     * @cprover.fullSupport
     */
    @Override
    public StringBuilder insert(int dstOffset, CharSequence s) {
        return insert(dstOffset, s == null ? "null" : s.toString());
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     *
     * @cprover.fullSupport
     */
    @Override
    public StringBuilder insert(int dstOffset, CharSequence s,
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
    public StringBuilder insert(int offset, boolean b) {
        return insert(offset, String.valueOf(b));
    }

    /**
     * @throws IndexOutOfBoundsException {@inheritDoc}
     *
     * @cprover.fullSupport
     */
    @Override
    public StringBuilder insert(int offset, char c) {
        return insert(offset, CProverString.ofCharArray(new char[] {c}, 0, 1));
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     *
     * @cprover.fullSupport
     */
    @Override
    public StringBuilder insert(int offset, int i) {
        return insert(offset, CProverString.toString(i));
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     *
     * @cprover.fullSupport
     */
    @Override
    public StringBuilder insert(int offset, long l) {
        return insert(offset, CProverString.toString(l));
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     *
     * @cprover.limitedSupport Result string approximates the value: see
     *   {@link CProverString#toString(float)}.
     */
    @Override
    public StringBuilder insert(int offset, float f) {
        return insert(offset, CProverString.toString(f));
    }

    /**
     * @throws StringIndexOutOfBoundsException {@inheritDoc}
     *
     * @cprover.limitedSupport Result string approximates the value: see
     *   {@link CProverString#toString(double)}.
     */
    @Override
    public StringBuilder insert(int offset, double d) {
        return insert(offset, CProverString.toString(d));
    }

    /**
     * @cprover.noSupport There is no primitive to build an indexOf
     *   operation from.
     */
    @Override
    public int indexOf(String str) {
        CProver.notModelled();
        return CProver.nondetInt();
    }

    /**
     * @cprover.noSupport See {@link #indexOf(String)}.
     */
    @Override
    public int indexOf(String str, int fromIndex) {
        CProver.notModelled();
        return CProver.nondetInt();
    }

    /**
     * @cprover.noSupport See {@link #indexOf(String)}.
     */
    @Override
    public int lastIndexOf(String str) {
        CProver.notModelled();
        return CProver.nondetInt();
    }

    /**
     * @cprover.noSupport See {@link #indexOf(String)}.
     */
    @Override
    public int lastIndexOf(String str, int fromIndex) {
        CProver.notModelled();
        return CProver.nondetInt();
    }

    /**
     * @cprover.fullSupport
     */
    @Override
    public StringBuilder reverse() {
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
    public StringBuilder repeat(int codePoint, int count) {
        if (!Character.isValidCodePoint(codePoint)) {
            throw new IllegalArgumentException(
                "Not a valid Unicode code point: 0x" + Integer.toHexString(codePoint));
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
    public StringBuilder repeat(CharSequence cs, int count) {
        if (count < 0) {
            throw new IllegalArgumentException("count is negative: " + count);
        }
        for (int i = 0; i < count; i++) {
            append(cs);
        }
        return this;
    }

    @Override
    public String toString() {
        // Handled by JBMC's string-refinement (exact signature match);
        // this body is never executed.
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    /**
     * Not declared by real JDK 21 (inherited from {@link
     * AbstractStringBuilder}), but must be re-declared here: see this
     * class's Javadoc.
     *
     * @cprover.noSupport Handled by JBMC's string-refinement (exact
     *   signature match); this body is never executed.
     */
    @Override
    public int length() {
        CProver.notModelled();
        return CProver.nondetInt();
    }

    /**
     * Not declared by real JDK 21 (inherited from {@link
     * AbstractStringBuilder}), but must be re-declared here: see this
     * class's Javadoc.
     *
     * @cprover.noSupport Handled by JBMC's string-refinement (exact
     *   signature match); this body is never executed.
     */
    @Override
    public char charAt(int index) {
        CProver.notModelled();
        return CProver.nondetChar();
    }

    /**
     * Not declared by real JDK 21 (inherited from {@link
     * AbstractStringBuilder}), but must be re-declared here: see this
     * class's Javadoc.
     *
     * @cprover.noSupport Handled by JBMC's string-refinement (exact
     *   signature match); this body is never executed.
     */
    @Override
    public int codePointAt(int index) {
        CProver.notModelled();
        return CProver.nondetInt();
    }

    /**
     * Not declared by real JDK 21 (inherited from {@link
     * AbstractStringBuilder}), but must be re-declared here: see this
     * class's Javadoc.
     *
     * @cprover.noSupport Handled by JBMC's string-refinement (exact
     *   signature match); this body is never executed.
     */
    @Override
    public int codePointBefore(int index) {
        CProver.notModelled();
        return CProver.nondetInt();
    }

    /**
     * Not declared by real JDK 21 (inherited from {@link
     * AbstractStringBuilder}), but must be re-declared here: see this
     * class's Javadoc.
     *
     * @cprover.noSupport Handled by JBMC's string-refinement (exact
     *   signature match); this body is never executed.
     */
    @Override
    public int codePointCount(int beginIndex, int endIndex) {
        CProver.notModelled();
        return CProver.nondetInt();
    }

    /**
     * Not declared by real JDK 21 (inherited from {@link
     * AbstractStringBuilder}); no StringBuilder-specific primitive exists
     * for this, so it is composed from {@link #toString} (intercepted) and
     * {@link CProverString#offsetByCodePoints(String, int, int)}.
     *
     * @cprover.fullSupport
     */
    @Override
    public int offsetByCodePoints(int index, int codePointOffset) {
        return CProverString.offsetByCodePoints(this.toString(), index, codePointOffset);
    }

    /**
     * Not declared by real JDK 21 (inherited from {@link
     * AbstractStringBuilder}), but must be re-declared here: see this
     * class's Javadoc.
     *
     * @cprover.noSupport Handled by JBMC's string-refinement (exact
     *   signature match); this body is never executed.
     */
    @Override
    public String substring(int start) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    /**
     * Not declared by real JDK 21 (inherited from {@link
     * AbstractStringBuilder}), but must be re-declared here: see this
     * class's Javadoc.
     *
     * @cprover.noSupport Handled by JBMC's string-refinement (exact
     *   signature match); this body is never executed.
     */
    @Override
    public String substring(int start, int end) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    /**
     * Not declared by real JDK 21 (inherited from {@link
     * AbstractStringBuilder}); composed from {@link #substring(int, int)}.
     *
     * @cprover.fullSupport
     */
    @Override
    public CharSequence subSequence(int start, int end) {
        return substring(start, end);
    }

    /**
     * Not declared by real JDK 21 (inherited from {@link
     * AbstractStringBuilder}); no primitive exists for this, so it is
     * composed from {@link #charAt} (intercepted).
     *
     * @cprover.fullSupport
     */
    public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
        if (srcBegin < 0 || srcEnd > length() || srcBegin > srcEnd) {
            throw new IndexOutOfBoundsException();
        }
        for (int i = srcBegin; i < srcEnd; i++) {
            dst[dstBegin + i - srcBegin] = charAt(i);
        }
    }

    /**
     * Not declared by real JDK 21 (inherited from {@link
     * AbstractStringBuilder}); composed from {@link
     * CProverString#setCharAt(StringBuilder, int, char)}.
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @see    #length()
     *
     * @cprover.fullSupport
     */
    @Override
    public void setCharAt(int index, char ch) {
        if ((index < 0) || (index >= this.length())) {
            throw new StringIndexOutOfBoundsException(index);
        }
        CProverString.setCharAt(this, index, ch);
    }

    /**
     * Not declared by real JDK 21 (inherited from {@link
     * AbstractStringBuilder}); composed from {@link
     * CProverString#setLength(StringBuilder, int)}.
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @see    #length()
     *
     * @cprover.fullSupport
     */
    @Override
    public void setLength(int newLength) {
        if (newLength < 0) {
            throw new StringIndexOutOfBoundsException(newLength);
        }
        CProverString.setLength(this, newLength);
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
     *
     * @param  s the {@code ObjectOutputStream} to which data is written
     * @throws IOException if an I/O error occurs
     */
    @Serial
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(count);
        char[] val = new char[capacity()];
        if (isLatin1()) {
            StringLatin1.getChars(value, 0, count, val, 0);
        } else {
            StringUTF16.getChars(value, 0, count, val, 0);
        }
        s.writeObject(val);
    }

    /**
     * readObject is called to restore the state of the StringBuilder from
     * a stream.
     *
     * @param  s the {@code ObjectInputStream} from which data is read
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if a serialized class cannot be loaded
     */
    @Serial
    private void readObject(ObjectInputStream s)
            throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        int c = s.readInt();
        char[] val = (char[]) s.readObject();
        if (c < 0 || c > val.length) {
            throw new StreamCorruptedException("count value invalid");
        }
        initBytes(val, 0, val.length);
        count = c;
    }
}
