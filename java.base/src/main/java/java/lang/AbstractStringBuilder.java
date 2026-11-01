/*
 * Copyright (c) 2003, 2025, Oracle and/or its affiliates. All rights reserved.
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

import java.util.stream.IntStream;

import org.cprover.CProver;

/**
 * A mutable sequence of characters.
 * <p>
 * Implements a modifiable string. At any point in time it contains some
 * particular sequence of characters, but the length and content of the
 * sequence can be changed through certain method calls.
 *
 * <p>Unless otherwise noted, passing a {@code null} argument to a constructor
 * or method in this class will cause a {@link NullPointerException} to be
 * thrown.
 *
 * @author      Michael McCloskey
 * @author      Martin Buchholz
 * @author      Ulf Zibis
 * @since       1.5
 *
 * @cprover.noSupport This class is not one of the classes whose field layout
 *   JBMC's string-refinement replaces (only {@code String}, {@code
 *   StringBuilder}, {@code StringBuffer}, and {@code CharSequence} are: see
 *   java_string_library_preprocess.cpp's {@code string_types} set) and
 *   JBMC's method interception is keyed on the exact declaring class, so
 *   nothing declared here can ever correctly read or write the actual
 *   (symbolically tracked) contents of a {@code StringBuilder}/{@code
 *   StringBuffer} instance: the real fields below are physically present
 *   but never updated by the intercepted operations on instances of this
 *   class's subclasses, and there is no primitive to query the true
 *   content from a method declared here. Every method is therefore
 *   unmodelled; {@link StringBuilder} and {@link StringBuffer} both
 *   re-declare, with real bodies, everything they need to behave
 *   correctly rather than relying on inheriting from this class.
 */
abstract sealed class AbstractStringBuilder implements Appendable, CharSequence
    permits StringBuilder, StringBuffer {
    /**
     * The value is used for character storage.
     */
    byte[] value;

    /**
     * The id of the encoding used to encode the bytes in {@code value}.
     */
    byte coder;

    /**
     *  The attribute indicates {@code value} might be compressible to LATIN1 if it is UTF16-encoded.
     *  An inflated byte array becomes compressible only when those non-latin1 chars are deleted.
     *  We simply set this attribute in all methods which may delete chars. Therefore, there are
     *  false positives. Subclasses and String need to handle it properly.
     */
    boolean maybeLatin1;

    /**
     * The count is the number of characters used.
     */
    int count;

    private static final byte[] EMPTYVALUE = new byte[0];

    AbstractStringBuilder() {}

    AbstractStringBuilder(int capacity) {}

    AbstractStringBuilder(String str) {}

    AbstractStringBuilder(CharSequence seq) {}

    int compareTo(AbstractStringBuilder another) {
        CProver.notModelled();
        return CProver.nondetInt();
    }

    @Override
    public int length() {
        CProver.notModelled();
        return CProver.nondetInt();
    }

    public int capacity() {
        CProver.notModelled();
        return CProver.nondetInt();
    }

    public void ensureCapacity(int minimumCapacity) {
        CProver.notModelled();
    }

    public void trimToSize() {
        CProver.notModelled();
    }

    public void setLength(int newLength) {
        CProver.notModelled();
    }

    @Override
    public char charAt(int index) {
        CProver.notModelled();
        return CProver.nondetChar();
    }

    public int codePointAt(int index) {
        CProver.notModelled();
        return CProver.nondetInt();
    }

    public int codePointBefore(int index) {
        CProver.notModelled();
        return CProver.nondetInt();
    }

    public int codePointCount(int beginIndex, int endIndex) {
        CProver.notModelled();
        return CProver.nondetInt();
    }

    public int offsetByCodePoints(int index, int codePointOffset) {
        CProver.notModelled();
        return CProver.nondetInt();
    }

    public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
        CProver.notModelled();
    }

    public void setCharAt(int index, char ch) {
        CProver.notModelled();
    }

    public AbstractStringBuilder append(Object obj) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder append(String str) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder append(StringBuffer sb) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    AbstractStringBuilder append(AbstractStringBuilder asb) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    @Override
    public AbstractStringBuilder append(CharSequence s) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    @Override
    public AbstractStringBuilder append(CharSequence s, int start, int end) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder append(char[] str) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder append(char[] str, int offset, int len) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder append(boolean b) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    @Override
    public AbstractStringBuilder append(char c) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder append(int i) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder append(long l) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder append(float f) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder append(double d) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder delete(int start, int end) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder appendCodePoint(int codePoint) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder deleteCharAt(int index) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder replace(int start, int end, String str) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    public String substring(int start) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    public String substring(int start, int end) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder insert(int index, char[] str, int offset,
                                               int len) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder insert(int offset, Object obj) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder insert(int offset, String str) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder insert(int offset, char[] str) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder insert(int dstOffset, CharSequence s) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder insert(int dstOffset, CharSequence s,
                                               int start, int end) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder insert(int offset, boolean b) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder insert(int offset, char c) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder insert(int offset, int i) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder insert(int offset, long l) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder insert(int offset, float f) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    public AbstractStringBuilder insert(int offset, double d) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    public int indexOf(String str) {
        CProver.notModelled();
        return CProver.nondetInt();
    }

    public int indexOf(String str, int fromIndex) {
        CProver.notModelled();
        return CProver.nondetInt();
    }

    public int lastIndexOf(String str) {
        CProver.notModelled();
        return CProver.nondetInt();
    }

    public int lastIndexOf(String str, int fromIndex) {
        CProver.notModelled();
        return CProver.nondetInt();
    }

    public AbstractStringBuilder reverse() {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    @Override
    public abstract String toString();

    /**
     * {@inheritDoc}
     * @since 9
     */
    @Override
    public IntStream chars() {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    /**
     * {@inheritDoc}
     * @since 9
     */
    @Override
    public IntStream codePoints() {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    /**
     * @since 21
     */
    public AbstractStringBuilder repeat(int codePoint, int count) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    /**
     * @since 21
     */
    public AbstractStringBuilder repeat(CharSequence cs, int count) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    // The following package-private accessors operate on this class's own
    // real (not CBMC-replaced) fields, and are kept with their real bodies
    // rather than being stubbed above:
    // - they are relied on by other real, unmodified java.base code
    //   (String's use of getValue()/getCoder()/isLatin1(), and
    //   StringConcatHelper's indirect use of mix()/prepend() via
    //   JavaLangAccess for invokedynamic string concatenation), so removing
    //   them would break compilation of those files;
    // - correctness of what they report is moot either way: the real
    //   fields they read are never updated by the intercepted operations
    //   above, and invokedynamic string concatenation is out of scope for
    //   the same reason java.lang.invoke generally is (JBMC synthesizes
    //   those call sites itself; see lambda_synthesis.cpp).

    /**
     * Needed by {@code String} for the contentEquals method.
     */
    final byte[] getValue() {
        return value;
    }

    /*
     * Invoker guarantees it is in UTF16 (inflate itself for asb), if two
     * coders are different and the dstBegin has enough space
     *
     * @param dstBegin  the char index, not offset of byte[]
     * @param coder     the coder of dst[]
     */
    void getBytes(byte[] dst, int dstBegin, byte coder) {
        if (this.coder == coder) {
            System.arraycopy(value, 0, dst, dstBegin << coder, count << coder);
        } else {        // this.coder == LATIN && coder == UTF16
            StringLatin1.inflate(value, 0, dst, dstBegin, count);
        }
    }

    /* for readObject() */
    void initBytes(char[] value, int off, int len) {
        if (String.COMPACT_STRINGS) {
            byte[] val = StringUTF16.compress(value, off, len);
            this.coder = StringUTF16.coderFromArrayLen(val, len);
            this.value = val;
            return;
        }
        this.coder = String.UTF16;
        this.value = StringUTF16.toBytes(value, off, len);
    }

    final byte getCoder() {
        return String.COMPACT_STRINGS ? coder : String.UTF16;
    }

    final boolean isLatin1() {
        return String.COMPACT_STRINGS && coder == String.LATIN1;
    }

    long mix(long lengthCoder) {
        return (lengthCoder + count) | ((long)coder << 32);
    }

    /**
     * Used by StringConcatHelper via JLA. Adds the characters in the builder value to the
     * concatenation buffer and then updates the running accumulation of length.
     *
     * @param lengthCoder running accumulation of length and coder
     * @param buffer      concatenation buffer
     *
     * @return running accumulation of length and coder minus the number of characters added
     */
    long prepend(long lengthCoder, byte[] buffer) {
        lengthCoder -= count;

        if (lengthCoder < ((long)String.UTF16 << 32)) {
            System.arraycopy(value, 0, buffer, (int)lengthCoder, count);
        } else if (coder == String.LATIN1) {
            StringUTF16.inflate(value, 0, buffer, (int)lengthCoder, count);
        } else {
            System.arraycopy(value, 0, buffer, (int)lengthCoder << 1, count << 1);
        }

        return lengthCoder;
    }
}
