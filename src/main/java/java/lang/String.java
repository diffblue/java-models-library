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

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
// DIFFBLUE MODEL LIBRARY these headers are not used
// import java.io.ObjectStreamField;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.Formatter;
// import java.util.Objects;
// import java.util.StringJoiner;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;
// import java.util.regex.PatternSyntaxException;

// DIFFBLUE MODEL LIBRARY new imports in the model
import java.nio.charset.StandardCharsets;

import org.cprover.CProver;

// Used as an interface with CProver internal string functions:
import org.cprover.CProverString;

/**
 * The {@code String} class represents character strings. All
 * string literals in Java programs, such as {@code "abc"}, are
 * implemented as instances of this class.
 * <p>
 * Strings are constant; their values cannot be changed after they
 * are created. String buffers support mutable strings.
 * Because String objects are immutable they can be shared. For example:
 * <blockquote><pre>
 *     String str = "abc";
 * </pre></blockquote><p>
 * is equivalent to:
 * <blockquote><pre>
 *     char data[] = {'a', 'b', 'c'};
 *     String str = new String(data);
 * </pre></blockquote><p>
 * Here are some more examples of how strings can be used:
 * <blockquote><pre>
 *     System.out.println("abc");
 *     String cde = "cde";
 *     System.out.println("abc" + cde);
 *     String c = "abc".substring(2,3);
 *     String d = cde.substring(1, 2);
 * </pre></blockquote>
 * <p>
 * The class {@code String} includes methods for examining
 * individual characters of the sequence, for comparing strings, for
 * searching strings, for extracting substrings, and for creating a
 * copy of a string with all characters translated to uppercase or to
 * lowercase. Case mapping is based on the Unicode Standard version
 * specified by the {@link java.lang.Character Character} class.
 * <p>
 * The Java language provides special support for the string
 * concatenation operator (&nbsp;+&nbsp;), and for conversion of
 * other objects to strings. String concatenation is implemented
 * through the {@code StringBuilder}(or {@code StringBuffer})
 * class and its {@code append} method.
 * String conversions are implemented through the method
 * {@code toString}, defined by {@code Object} and
 * inherited by all classes in Java. For additional information on
 * string concatenation and conversion, see Gosling, Joy, and Steele,
 * <i>The Java Language Specification</i>.
 *
 * <p> Unless otherwise noted, passing a <tt>null</tt> argument to a constructor
 * or method in this class will cause a {@link NullPointerException} to be
 * thrown.
 *
 * <p>A {@code String} represents a string in the UTF-16 format
 * in which <em>supplementary characters</em> are represented by <em>surrogate
 * pairs</em> (see the section <a href="Character.html#unicode">Unicode
 * Character Representations</a> in the {@code Character} class for
 * more information).
 * Index values refer to {@code char} code units, so a supplementary
 * character uses two positions in a {@code String}.
 * <p>The {@code String} class provides methods for dealing with
 * Unicode code points (i.e., characters), in addition to those for
 * dealing with Unicode code units (i.e., {@code char} values).
 *
 * @author  Lee Boynton
 * @author  Arthur van Hoff
 * @author  Martin Buchholz
 * @author  Ulf Zibis
 * @see     java.lang.Object#toString()
 * @see     java.lang.StringBuffer
 * @see     java.lang.StringBuilder
 * @see     java.nio.charset.Charset
 * @since   JDK1.0
 *
 * @diffblue.limitedSupport
 * Not all methods are fully supported.
 */

public final class String
    implements java.io.Serializable, Comparable<String>, CharSequence {
    /** The value is used for character storage. */
    // DIFFBLUE MODEL LIBRARY This is treated internally in CBMC
    // private final char value[];

    /** Cache the hash code for the string */
    // DIFFBLUE MODEL LIBRARY This is not necessary for modelling
    // private int hash; // Default to 0

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = -6849794470754667710L;

    /**
     * Class String is special cased within the Serialization Stream Protocol.
     *
     * A String instance is written into an ObjectOutputStream according to
     * <a href="{@docRoot}/../platform/serialization/spec/output.html">
     * Object Serialization Specification, Section 6.2, "Stream Elements"</a>
     */
    // DIFFBLUE MODEL LIBRARY This is not necessary for modelling
    // private static final ObjectStreamField[] serialPersistentFields =
    //    new ObjectStreamField[0];

    /**
     * Initializes a newly created {@code String} object so that it represents
     * an empty character sequence.  Note that use of this constructor is
     * unnecessary since Strings are immutable.
     *
     * @diffblue.fullSupport
     */
    public String() {
        // DIFFBLUE MODEL LIBRARY This is treated internally in CBMC
        // this.value = new char[0];
    }

    /**
     * Initializes a newly created {@code String} object so that it represents
     * the same sequence of characters as the argument; in other words, the
     * newly created string is a copy of the argument string. Unless an
     * explicit copy of {@code original} is needed, use of this constructor is
     * unnecessary since Strings are immutable.
     *
     * @param  original
     *         A {@code String}
     *
     * @diffblue.fullSupport
     */
    public String(String original) {
        // DIFFBLUE MODEL LIBRARY This is treated internally in CBMC
        // this.value = original.value;
        // this.hash = original.hash;
    }

    /**
     * Intermediary function for modelling the constructor String(char value[]).
     */
    private static String CProverStringOfCharArray(char value[]) {
        if (value == null) {
            throw new NullPointerException();
        }
        return CProverString.ofCharArray(value, 0, value.length);
    }

    /**
     * Allocates a new {@code String} so that it represents the sequence of
     * characters currently contained in the character array argument. The
     * contents of the character array are copied; subsequent modification of
     * the character array does not affect the newly created string.
     *
     * @param  value
     *         The initial value of the string
     *
     * @diffblue.limitedSupport
     * The length of the String constructed is limited by the unwind
     * parameter.
     */
    public String(char value[]) {
        // this.value = Arrays.copyOf(value, value.length);

        // DIFFBLUE MODEL LIBRARY
        this(CProverStringOfCharArray(value));
    }

    /**
     * Intermediary function for modelling the constructor for
     * String(char value[], int offset, int count).
     * The code is copied from the original String(char value[], int offset, int count).
     */
    private static String CProverStringOfCharArray(char value[], int offset, int count) {
        if (offset < 0) {
            throw new StringIndexOutOfBoundsException(offset);
        }
        if (count < 0) {
            throw new StringIndexOutOfBoundsException(count);
        }
        // Note: offset or count might be near -1>>>1.
        if (offset > value.length - count) {
            throw new StringIndexOutOfBoundsException(offset + count);
        }
        // DIFFBLUE MODEL LIBRARY Use CProverString function instead of array copy
        return CProverString.ofCharArray(value, offset, count);
        // this.value = Arrays.copyOfRange(value, offset, offset+count);
    }

    /**
     * Allocates a new {@code String} that contains characters from a subarray
     * of the character array argument. The {@code offset} argument is the
     * index of the first character of the subarray and the {@code count}
     * argument specifies the length of the subarray. The contents of the
     * subarray are copied; subsequent modification of the character array does
     * not affect the newly created string.
     *
     * @param  value
     *         Array that is the source of characters
     *
     * @param  offset
     *         The initial offset
     *
     * @param  count
     *         The length
     *
     * @throws  IndexOutOfBoundsException
     *          If the {@code offset} and {@code count} arguments index
     *          characters outside the bounds of the {@code value} array
     *
     * @diffblue.limitedSupport
     * @diffblue.untested
     */
    public String(char value[], int offset, int count) {
        // DIFFBLUE MODEL LIBRARY
        this(CProverStringOfCharArray(value, offset, count));
        // if (offset < 0) {
        //     throw new StringIndexOutOfBoundsException(offset);
        // }
        // if (count < 0) {
        //     throw new StringIndexOutOfBoundsException(count);
        // }
        // // Note: offset or count might be near -1>>>1.
        // if (offset > value.length - count) {
        //     throw new StringIndexOutOfBoundsException(offset + count);
        // }
        // this.value = Arrays.copyOfRange(value, offset, offset+count);
    }

    /**
     * DIFFBLUE MODEL LIBRARY
     * Helper for generating non-deterministic strings.
     */
    private static String cproverNonDet()
    {
        return CProver.nondetWithoutNull("");
    }

    /**
     * Allocates a new {@code String} that contains characters from a subarray
     * of the <a href="Character.html#unicode">Unicode code point</a> array
     * argument.  The {@code offset} argument is the index of the first code
     * point of the subarray and the {@code count} argument specifies the
     * length of the subarray.  The contents of the subarray are converted to
     * {@code char}s; subsequent modification of the {@code int} array does not
     * affect the newly created string.
     *
     * @param  codePoints
     *         Array that is the source of Unicode code points
     *
     * @param  offset
     *         The initial offset
     *
     * @param  count
     *         The length
     *
     * @throws  IllegalArgumentException
     *          If any invalid Unicode code point is found in {@code
     *          codePoints}
     *
     * @throws  IndexOutOfBoundsException
     *          If the {@code offset} and {@code count} arguments index
     *          characters outside the bounds of the {@code codePoints} array
     *
     * @since  1.5
     *
     * @diffblue.limitedSupport
     * Assumes all codePoints are in the Basic Multilingual Plane.
     * An implication of this limitation is that no trace is generated for
     * the cases where IllegalArgumentException would be thrown.
     */
    public String(int[] codePoints, int offset, int count) {
        // if (offset < 0) {
        //     throw new StringIndexOutOfBoundsException(offset);
        // }
        // if (count < 0) {
        //     throw new StringIndexOutOfBoundsException(count);
        // }
        // // Note: offset or count might be near -1>>>1.
        // if (offset > codePoints.length - count) {
        //     throw new StringIndexOutOfBoundsException(offset + count);
        // }

        // final int end = offset + count;

        // // Pass 1: Compute precise size of char[]
        // int n = count;
        // for (int i = offset; i < end; i++) {
        //     int c = codePoints[i];
        //     if (Character.isBmpCodePoint(c))
        //         continue;
        //     else if (Character.isValidCodePoint(c))
        //         n++;
        //     else throw new IllegalArgumentException(Integer.toString(c));
        // }

        // // Pass 2: Allocate and fill in char[]
        // final char[] v = new char[n];

        // for (int i = offset, j = 0; i < end; i++, j++) {
        //     int c = codePoints[i];
        //     if (Character.isBmpCodePoint(c))
        //         v[j] = (char)c;
        //     else
        //         Character.toSurrogates(c, v, j++);
        // }

        // this.value = v;

        // DIFFBLUE MODEL LIBRARY
        // We initialize the string non-deterministically and add constraints on
        // it, instead of using an array of characters.
        this(cproverNonDet());

        if (offset < 0) {
            throw new StringIndexOutOfBoundsException(offset);
        }
        if (count < 0) {
            throw new StringIndexOutOfBoundsException(count);
        }
        if (offset > codePoints.length - count) {
            throw new StringIndexOutOfBoundsException(offset + count);
        }
        final int end = offset + count;
        CProver.assume(length() == count);
        for (int i = offset, j = 0; i < end; i++, j++) {
            int c = codePoints[i];
            CProver.assume(Character.isBmpCodePoint(c));
            CProver.assume(CProverString.charAt(this, j) == (char)c);
        }
    }

    /**
     * Allocates a new {@code String} constructed from a subarray of an array
     * of 8-bit integer values.
     *
     * <p> The {@code offset} argument is the index of the first byte of the
     * subarray, and the {@code count} argument specifies the length of the
     * subarray.
     *
     * <p> Each {@code byte} in the subarray is converted to a {@code char} as
     * specified in the method above.
     *
     * @deprecated This method does not properly convert bytes into characters.
     * As of JDK&nbsp;1.1, the preferred way to do this is via the
     * {@code String} constructors that take a {@link
     * java.nio.charset.Charset}, charset name, or that use the platform's
     * default charset.
     *
     * @param  ascii
     *         The bytes to be converted to characters
     *
     * @param  hibyte
     *         The top 8 bits of each 16-bit Unicode code unit
     *
     * @param  offset
     *         The initial offset
     * @param  count
     *         The length
     *
     * @throws  IndexOutOfBoundsException
     *          If the {@code offset} or {@code count} argument is invalid
     *
     * @see  #String(byte[], int)
     * @see  #String(byte[], int, int, java.lang.String)
     * @see  #String(byte[], int, int, java.nio.charset.Charset)
     * @see  #String(byte[], int, int)
     * @see  #String(byte[], java.lang.String)
     * @see  #String(byte[], java.nio.charset.Charset)
     * @see  #String(byte[])
     *
     * @diffblue.limitedSupport
     * The length of the String constructed is limited by the unwind
     * parameter.
     */
    @Deprecated
    public String(byte ascii[], int hibyte, int offset, int count) {
        // checkBounds(ascii, offset, count);
        // char value[] = new char[count];

        // if (hibyte == 0) {
        //     for (int i = count; i-- > 0;) {
        //         value[i] = (char)(ascii[i + offset] & 0xff);
        //     }
        // } else {
        //     hibyte <<= 8;
        //     for (int i = count; i-- > 0;) {
        //         value[i] = (char)(hibyte | (ascii[i + offset] & 0xff));
        //     }
        // }
        // this.value = value;

        // DIFFBLUE MODEL LIBRARY
        // We initialize the string non-deterministically and add constraints on
        // it, instead of using an array of characters.
        this(cproverNonDet());

        checkBounds(ascii, offset, count);
        CProver.assume(length() == count);

        if (hibyte == 0) {
            for (int i = count; i-- > 0;) {
                CProver.assume(CProverString.charAt(this, i) == (char)(ascii[i + offset] & 0xff));
            }
        } else {
            hibyte <<= 8;
            for (int i = count; i-- > 0;) {
                CProver.assume(CProverString.charAt(this, i) == (char)(hibyte | (ascii[i + offset] & 0xff)));
            }
        }
    }

    /**
     * Allocates a new {@code String} containing characters constructed from
     * an array of 8-bit integer values. Each character <i>c</i>in the
     * resulting string is constructed from the corresponding component
     * <i>b</i> in the byte array such that:
     *
     * <blockquote><pre>
     *     <b><i>c</i></b> == (char)(((hibyte &amp; 0xff) &lt;&lt; 8)
     *                         | (<b><i>b</i></b> &amp; 0xff))
     * </pre></blockquote>
     *
     * @deprecated  This method does not properly convert bytes into
     * characters.  As of JDK&nbsp;1.1, the preferred way to do this is via the
     * {@code String} constructors that take a {@link
     * java.nio.charset.Charset}, charset name, or that use the platform's
     * default charset.
     *
     * @param  ascii
     *         The bytes to be converted to characters
     *
     * @param  hibyte
     *         The top 8 bits of each 16-bit Unicode code unit
     *
     * @see  #String(byte[], int, int, java.lang.String)
     * @see  #String(byte[], int, int, java.nio.charset.Charset)
     * @see  #String(byte[], int, int)
     * @see  #String(byte[], java.lang.String)
     * @see  #String(byte[], java.nio.charset.Charset)
     * @see  #String(byte[])
     *
     * @diffblue.limitedSupport
     * The length of the String constructed is limited by the unwind
     * parameter.
     */
    @Deprecated
    public String(byte ascii[], int hibyte) {
        this(ascii, hibyte, 0, ascii.length);
    }

    /* Common private utility method used to bounds check the byte array
     * and requested offset & length values used by the String(byte[],..)
     * constructors.
     */
    private static void checkBounds(byte[] bytes, int offset, int length) {
        if (length < 0)
            throw new StringIndexOutOfBoundsException(length);
        if (offset < 0)
            throw new StringIndexOutOfBoundsException(offset);
        if (offset > bytes.length - length)
            throw new StringIndexOutOfBoundsException(offset + length);
    }

    /**
     * Constructs a new {@code String} by decoding the specified subarray of
     * bytes using the specified charset.  The length of the new {@code String}
     * is a function of the charset, and hence may not be equal to the length
     * of the subarray.
     *
     * <p> The behavior of this constructor when the given bytes are not valid
     * in the given charset is unspecified.  The {@link
     * java.nio.charset.CharsetDecoder} class should be used when more control
     * over the decoding process is required.
     *
     * @param  bytes
     *         The bytes to be decoded into characters
     *
     * @param  offset
     *         The index of the first byte to decode
     *
     * @param  length
     *         The number of bytes to decode

     * @param  charsetName
     *         The name of a supported {@linkplain java.nio.charset.Charset
     *         charset}
     *
     * @throws  UnsupportedEncodingException
     *          If the named charset is not supported
     *
     * @throws  IndexOutOfBoundsException
     *          If the {@code offset} and {@code length} arguments index
     *          characters outside the bounds of the {@code bytes} array
     *
     * @since  JDK1.1
     *
     * @diffblue.limitedSupport
     * The length of the String constructed is limited by the unwind
     * parameter.
     */
    public String(byte bytes[], int offset, int length, String charsetName)
            throws UnsupportedEncodingException {
        // if (charsetName == null)
        //     throw new NullPointerException("charsetName");
        // checkBounds(bytes, offset, length);
        // this.value = StringCoding.decode(charsetName, bytes, offset, length);

        // DIFFBLUE MODEL LIBRARY
        // We initialize the string non-deterministically and add constraints on
        // it, instead of using an array of characters.
        this(cproverNonDet());

        if (charsetName == null)
            throw new NullPointerException("charsetName");
        checkBounds(bytes, offset, length);

        byte[] getBytesResult = cproverReversibleGetBytes(charsetName);
        CProver.assume(getBytesResult.length == length);
        for (int i = 0; i < length; i++) {
            CProver.assume(bytes[i + offset] == getBytesResult[i]);
        }
    }

    /**
     * Constructs a new {@code String} by decoding the specified subarray of
     * bytes using the specified {@linkplain java.nio.charset.Charset charset}.
     * The length of the new {@code String} is a function of the charset, and
     * hence may not be equal to the length of the subarray.
     *
     * <p> This method always replaces malformed-input and unmappable-character
     * sequences with this charset's default replacement string.  The {@link
     * java.nio.charset.CharsetDecoder} class should be used when more control
     * over the decoding process is required.
     *
     * @param  bytes
     *         The bytes to be decoded into characters
     *
     * @param  offset
     *         The index of the first byte to decode
     *
     * @param  length
     *         The number of bytes to decode
     *
     * @param  charset
     *         The {@linkplain java.nio.charset.Charset charset} to be used to
     *         decode the {@code bytes}
     *
     * @throws  IndexOutOfBoundsException
     *          If the {@code offset} and {@code length} arguments index
     *          characters outside the bounds of the {@code bytes} array
     *
     * @since  1.6
     *
     * @diffblue.limitedSupport
     * The length of the String constructed is limited by the unwind
     * parameter.
     */
    public String(byte bytes[], int offset, int length, Charset charset)
    {
        // if (charset == null)
        //     throw new NullPointerException("charset");
        // checkBounds(bytes, offset, length);
        // this.value =  StringCoding.decode(charset, bytes, offset, length);

        // DIFFBLUE MODEL LIBRARY
        // We initialize the string non-deterministically and add constraints on
        // it, instead of using an array of characters.
        this(cproverNonDet());
        if (charset == null)
            throw new NullPointerException("charset");
        checkBounds(bytes, offset, length);

        byte[] getBytesResult = cproverReversibleGetBytes(charset);
        CProver.assume(getBytesResult.length == length);
        for (int i = 0; i < length; i++) {
            CProver.assume(bytes[i + offset] == getBytesResult[i]);
        }
    }

    /**
     * Constructs a new {@code String} by decoding the specified array of bytes
     * using the specified {@linkplain java.nio.charset.Charset charset}.  The
     * length of the new {@code String} is a function of the charset, and hence
     * may not be equal to the length of the byte array.
     *
     * <p> The behavior of this constructor when the given bytes are not valid
     * in the given charset is unspecified.  The {@link
     * java.nio.charset.CharsetDecoder} class should be used when more control
     * over the decoding process is required.
     *
     * @param  bytes
     *         The bytes to be decoded into characters
     *
     * @param  charsetName
     *         The name of a supported {@linkplain java.nio.charset.Charset
     *         charset}
     *
     * @throws  UnsupportedEncodingException
     *          If the named charset is not supported
     *
     * @since  JDK1.1
     *
     * @diffblue.limitedSupport
     * The length of the String constructed is limited by the unwind
     * parameter.
     */
    public String(byte bytes[], String charsetName)
            throws UnsupportedEncodingException {
        this(bytes, 0, bytes.length, charsetName);
    }

    /**
     * DIFFBLUE MODEL LIBRARY
     * Used to construct a string from a byte array and a charset.
     *
     * It does so by generating a non-deterministic string `s` and assuming
     * `bytes` is equal to the result of `s.getBytes(charset)`.
     *
     * @param  bytes
     *         The bytes to be decoded into characters
     *
     * @param  charset
     *         a supported {@linkplain java.nio.charset.Charset charset}
     *
     * @return the constructed string
     */
    private static String cproverOfByteArray(byte[] bytes, Charset charset)
    {
        String s = CProver.nondetWithoutNull("");
        byte[] getBytesResult = s.cproverReversibleGetBytes(charset);
        CProver.assume(getBytesResult.length == bytes.length);
        for (int i = 0; i < bytes.length; i++) {
            CProver.assume(bytes[i] == getBytesResult[i]);
        }
        return s;
    }

    /**
     * Constructs a new {@code String} by decoding the specified array of
     * bytes using the specified {@linkplain java.nio.charset.Charset charset}.
     * The length of the new {@code String} is a function of the charset, and
     * hence may not be equal to the length of the byte array.
     *
     * <p> This method always replaces malformed-input and unmappable-character
     * sequences with this charset's default replacement string.  The {@link
     * java.nio.charset.CharsetDecoder} class should be used when more control
     * over the decoding process is required.
     *
     * @param  bytes
     *         The bytes to be decoded into characters
     *
     * @param  charset
     *         The {@linkplain java.nio.charset.Charset charset} to be used to
     *         decode the {@code bytes}
     *
     * @since  1.6
     *
     * @diffblue.limitedSupport
     * Only standard charsets are supported and for ASCII, UTF-8 and ISO-8859-1
     * we restrict all the characters to be ASCII.
     */
    public String(byte bytes[], Charset charset)
    {
        // this(bytes, 0, bytes.length, charset);

        // DIFFBLUE MODEL LIBRARY
        this(cproverOfByteArray(bytes, charset));
    }

    /**
     * Constructs a new {@code String} by decoding the specified subarray of
     * bytes using the platform's default charset.  The length of the new
     * {@code String} is a function of the charset, and hence may not be equal
     * to the length of the subarray.
     *
     * <p> The behavior of this constructor when the given bytes are not valid
     * in the default charset is unspecified.  The {@link
     * java.nio.charset.CharsetDecoder} class should be used when more control
     * over the decoding process is required.
     *
     * @param  bytes
     *         The bytes to be decoded into characters
     *
     * @param  offset
     *         The index of the first byte to decode
     *
     * @param  length
     *         The number of bytes to decode
     *
     * @throws  IndexOutOfBoundsException
     *          If the {@code offset} and the {@code length} arguments index
     *          characters outside the bounds of the {@code bytes} array
     *
     * @since  JDK1.1
     *
     * @diffblue.limitedSupport We assume all the bytes are ASCII characters,
     * and that the default charset encodes ASCII characters with one byte.
     */
    public String(byte bytes[], int offset, int length) {
        // DIFFBLUE MODEL LIBRARY
        // We initialize the string non-deterministically and add constraints on
        // it, instead of using an array of characters.
        this(cproverNonDet());

        checkBounds(bytes, offset, length);
        // DIFFBLUE MODEL LIBRARY We replace StringCoding.decode by the implementation below
        // this.value = StringCoding.decode(bytes, offset, length);

        CProver.assume(length() == length);
        byte[] getBytesResult = cproverGetBytesEnforceAscii();
        CProver.assume(getBytesResult.length == length);
        for (int i = 0; i < length; i++) {
            CProver.assume(bytes[i + offset] == getBytesResult[i]);
        }
    }

    /**
     * Constructs a new {@code String} by decoding the specified array of bytes
     * using the platform's default charset.  The length of the new {@code
     * String} is a function of the charset, and hence may not be equal to the
     * length of the byte array.
     *
     * <p> The behavior of this constructor when the given bytes are not valid
     * in the default charset is unspecified.  The {@link
     * java.nio.charset.CharsetDecoder} class should be used when more control
     * over the decoding process is required.
     *
     * @param  bytes
     *         The bytes to be decoded into characters
     *
     * @since  JDK1.1
     *
     * @diffblue.limitedSupport We assume all the bytes are ASCII characters,
     * and that the default charset encodes ASCII characters with one byte.
     */
    public String(byte bytes[]) {
        this(bytes, 0, bytes.length);
    }

    /**
     * Allocates a new string that contains the sequence of characters
     * currently contained in the string buffer argument. The contents of the
     * string buffer are copied; subsequent modification of the string buffer
     * does not affect the newly created string.
     *
     * @param  buffer
     *         A {@code StringBuffer}
     *
     * @diffblue.fullSupport
     */
    public String(StringBuffer buffer) {
        this(buffer.toString());
        // synchronized(buffer) {
        //     this.value = Arrays.copyOf(buffer.getValue(), buffer.length());
        // }
    }

    /**
     * Allocates a new string that contains the sequence of characters
     * currently contained in the string builder argument. The contents of the
     * string builder are copied; subsequent modification of the string builder
     * does not affect the newly created string.
     *
     * <p> This constructor is provided to ease migration to {@code
     * StringBuilder}. Obtaining a string from a string builder via the {@code
     * toString} method is likely to run faster and is generally preferred.
     *
     * @param   builder
     *          A {@code StringBuilder}
     *
     * @since  1.5
     *
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    public String(StringBuilder builder) {
        // DIFFBLUE MODEL LIBRARY This is treated internally in CBMC
        // this.value = Arrays.copyOf(builder.getValue(), builder.length());
    }

    /*
    * Package private constructor which shares value array for speed.
    * this constructor is always expected to be called with share==true.
    * a separate constructor is needed because we already have a public
    * String(char[]) constructor that makes a copy of the given char[].
    */
    // DIFFBLUE MODEL LIBRARY Unused package private method
    // String(char[] value, boolean share) {
    //     // assert share : "unshared not supported";
    //     this.value = value;
    // }

    /**
     * Returns the length of this string.
     * The length is equal to the number of <a href="Character.html#unicode">Unicode
     * code units</a> in the string.
     *
     * @return  the length of the sequence of characters represented by this
     *          object.
     *
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    public int length() {
        // DIFFBLUE MODEL LIBRARY This is treated internally in CBMC
        // return value.length;
        return CProver.nondetInt();
    }

    /**
     * Returns {@code true} if, and only if, {@link #length()} is {@code 0}.
     *
     * @return {@code true} if {@link #length()} is {@code 0}, otherwise
     * {@code false}
     *
     * @since 1.6
     *
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    public boolean isEmpty() {
        // DIFFBLUE MODEL LIBRARY This is treated internally in CBMC
        // return value.length == 0;
        return CProver.nondetBoolean();
    }

    /**
     * Returns the {@code char} value at the
     * specified index. An index ranges from {@code 0} to
     * {@code length() - 1}. The first {@code char} value of the sequence
     * is at index {@code 0}, the next at index {@code 1},
     * and so on, as for array indexing.
     *
     * <p>If the {@code char} value specified by the index is a
     * <a href="Character.html#unicode">surrogate</a>, the surrogate
     * value is returned.
     *
     * @param      index   the index of the {@code char} value.
     * @return     the {@code char} value at the specified index of this string.
     *             The first {@code char} value is at index {@code 0}.
     * @exception  IndexOutOfBoundsException  if the {@code index}
     *             argument is negative or not less than the length of this
     *             string.
     *
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    public char charAt(int index) {
        // DIFFBLUE MODEL LIBRARY
        // Our implementation differs slightly from the original
        // as we use the internal CProverString.charAt of CBMC,
        // however the behavior is the same as the JDK version.
        // if ((index < 0) || (index >= value.length)) {
        //     throw new StringIndexOutOfBoundsException(index);
        // }
        // return value[index];
        if ((index < 0) || (index >= length())) {
            throw new StringIndexOutOfBoundsException(index);
        }
        return CProverString.charAt(this, index);
    }

    /**
     * Returns the character (Unicode code point) at the specified
     * index. The index refers to {@code char} values
     * (Unicode code units) and ranges from {@code 0} to
     * {@link #length()}{@code  - 1}.
     *
     * <p> If the {@code char} value specified at the given index
     * is in the high-surrogate range, the following index is less
     * than the length of this {@code String}, and the
     * {@code char} value at the following index is in the
     * low-surrogate range, then the supplementary code point
     * corresponding to this surrogate pair is returned. Otherwise,
     * the {@code char} value at the given index is returned.
     *
     * @param      index the index to the {@code char} values
     * @return     the code point value of the character at the
     *             {@code index}
     * @exception  IndexOutOfBoundsException  if the {@code index}
     *             argument is negative or not less than the length of this
     *             string.
     * @since      1.5
     *
     * @diffblue.fullSupport
     * @diffblue.untested Tests only cover exception throwing
     */
    public int codePointAt(int index) {
        if ((index < 0) || (index >= this.length())) {
            throw new StringIndexOutOfBoundsException(index);
        }
        // return Character.codePointAtImpl(value, index, value.length);
        return CProverString.codePointAt(this, index);
    }

    /**
     * Returns the character (Unicode code point) before the specified
     * index. The index refers to {@code char} values
     * (Unicode code units) and ranges from {@code 1} to {@link
     * CharSequence#length() length}.
     *
     * <p> If the {@code char} value at {@code (index - 1)}
     * is in the low-surrogate range, {@code (index - 2)} is not
     * negative, and the {@code char} value at {@code (index -
     * 2)} is in the high-surrogate range, then the
     * supplementary code point value of the surrogate pair is
     * returned. If the {@code char} value at {@code index -
     * 1} is an unpaired low-surrogate or a high-surrogate, the
     * surrogate value is returned.
     *
     * @param     index the index following the code point that should be returned
     * @return    the Unicode code point value before the given index.
     * @exception IndexOutOfBoundsException if the {@code index}
     *            argument is less than 1 or greater than the length
     *            of this string.
     * @since     1.5
     *
     * @diffblue.fullSupport
     */
    public int codePointBefore(int index) {
        int i = index - 1;
        if ((i < 0) || (i >= this.length())) {
            throw new StringIndexOutOfBoundsException(index);
        }
        // return Character.codePointBeforeImpl(value, index, 0);
        return CProverString.codePointBefore(this, index);
    }

    /**
     * Returns the number of Unicode code points in the specified text
     * range of this {@code String}. The text range begins at the
     * specified {@code beginIndex} and extends to the
     * {@code char} at index {@code endIndex - 1}. Thus the
     * length (in {@code char}s) of the text range is
     * {@code endIndex-beginIndex}. Unpaired surrogates within
     * the text range count as one code point each.
     *
     * @param beginIndex the index to the first {@code char} of
     * the text range.
     * @param endIndex the index after the last {@code char} of
     * the text range.
     * @return the number of Unicode code points in the specified text
     * range
     * @exception IndexOutOfBoundsException if the
     * {@code beginIndex} is negative, or {@code endIndex}
     * is larger than the length of this {@code String}, or
     * {@code beginIndex} is larger than {@code endIndex}.
     * @since  1.5
     *
     * @diffblue.limitedSupport
     * The result of this function is approximated.
     * @diffblue.untested Tests only cover exception throwing
     */
    public int codePointCount(int beginIndex, int endIndex) {
        if (beginIndex < 0 || endIndex > this.length() || beginIndex > endIndex) {
            throw new IndexOutOfBoundsException();
        }
        // return Character.codePointCountImpl(value, beginIndex, endIndex - beginIndex);
        return CProverString.codePointCount(this, beginIndex, endIndex);
    }

    /**
     * Returns the index within this {@code String} that is
     * offset from the given {@code index} by
     * {@code codePointOffset} code points. Unpaired surrogates
     * within the text range given by {@code index} and
     * {@code codePointOffset} count as one code point each.
     *
     * @param index the index to be offset
     * @param codePointOffset the offset in code points
     * @return the index within this {@code String}
     * @exception IndexOutOfBoundsException if {@code index}
     *   is negative or larger then the length of this
     *   {@code String}, or if {@code codePointOffset} is positive
     *   and the substring starting with {@code index} has fewer
     *   than {@code codePointOffset} code points,
     *   or if {@code codePointOffset} is negative and the substring
     *   before {@code index} has fewer than the absolute value
     *   of {@code codePointOffset} code points.
     * @since 1.5
     *
     * @diffblue.limitedSupport
     * The result of this function is approximated. Only the
     * {@code IndexOutOfBoundsException} related to {@code index} is thrown.
     * @diffblue.untested Only exception throwing is tested.
     */
    public int offsetByCodePoints(int index, int codePointOffset) {
        if (index < 0 || index > this.length()) {
            throw new IndexOutOfBoundsException();
        }
        // return Character.offsetByCodePointsImpl(value, 0, value.length,
        //         index, codePointOffset);
        return CProverString.offsetByCodePoints(this, index, codePointOffset);
    }

    /**
     * Copy characters from this string into dst starting at dstBegin.
     * This method doesn't perform any range checking.
     */
    // DIFFBLUE MODEL LIBRARY Unused private method
    // void getChars(char dst[], int dstBegin) {
    //     System.arraycopy(value, 0, dst, dstBegin, value.length);
    // }

    /**
     * Copies characters from this string into the destination character
     * array.
     * <p>
     * The first character to be copied is at index {@code srcBegin};
     * the last character to be copied is at index {@code srcEnd-1}
     * (thus the total number of characters to be copied is
     * {@code srcEnd-srcBegin}). The characters are copied into the
     * subarray of {@code dst} starting at index {@code dstBegin}
     * and ending at index:
     * <blockquote><pre>
     *     dstbegin + (srcEnd-srcBegin) - 1
     * </pre></blockquote>
     *
     * @param      srcBegin   index of the first character in the string
     *                        to copy.
     * @param      srcEnd     index after the last character in the string
     *                        to copy.
     * @param      dst        the destination array.
     * @param      dstBegin   the start offset in the destination array.
     * @exception IndexOutOfBoundsException If any of the following
     *            is true:
     *            <ul><li>{@code srcBegin} is negative.
     *            <li>{@code srcBegin} is greater than {@code srcEnd}
     *            <li>{@code srcEnd} is greater than the length of this
     *                string
     *            <li>{@code dstBegin} is negative
     *            <li>{@code dstBegin+(srcEnd-srcBegin)} is larger than
     *                {@code dst.length}</ul>
     *
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    public void getChars(int srcBegin, int srcEnd, char dst[], int dstBegin) {
        if (srcBegin < 0) {
            throw new StringIndexOutOfBoundsException(srcBegin);
        }
        // if (srcEnd > value.length) {
        // DIFFBLUE MODEL LIBRARY we have no value member
        if (srcEnd > length()) {
            throw new StringIndexOutOfBoundsException(srcEnd);
        }
        if (srcBegin > srcEnd) {
            throw new StringIndexOutOfBoundsException(srcEnd - srcBegin);
        }
        // System.arraycopy(value, srcBegin, dst, dstBegin, srcEnd - srcBegin);
        // DIFFBLUE MODEL LIBRARY we inline System.arraycopy here so that we
        // can specialize it for characters.
        for(int i = 0; i < srcEnd - srcBegin; i++) {
            dst[dstBegin + i] = CProverString.charAt(this, srcBegin + i);
        }
    }

    /**
     * Copies characters from this string into the destination byte array. Each
     * byte receives the 8 low-order bits of the corresponding character. The
     * eight high-order bits of each character are not copied and do not
     * participate in the transfer in any way.
     *
     * <p> The first character to be copied is at index {@code srcBegin}; the
     * last character to be copied is at index {@code srcEnd-1}.  The total
     * number of characters to be copied is {@code srcEnd-srcBegin}. The
     * characters, converted to bytes, are copied into the subarray of {@code
     * dst} starting at index {@code dstBegin} and ending at index:
     *
     * <blockquote><pre>
     *     dstbegin + (srcEnd-srcBegin) - 1
     * </pre></blockquote>
     *
     * @deprecated  This method does not properly convert characters into
     * bytes.  As of JDK&nbsp;1.1, the preferred way to do this is via the
     * {@link #getBytes()} method, which uses the platform's default charset.
     *
     * @param  srcBegin
     *         Index of the first character in the string to copy
     *
     * @param  srcEnd
     *         Index after the last character in the string to copy
     *
     * @param  dst
     *         The destination array
     *
     * @param  dstBegin
     *         The start offset in the destination array
     *
     * @throws  IndexOutOfBoundsException
     *          If any of the following is true:
     *          <ul>
     *            <li> {@code srcBegin} is negative
     *            <li> {@code srcBegin} is greater than {@code srcEnd}
     *            <li> {@code srcEnd} is greater than the length of this String
     *            <li> {@code dstBegin} is negative
     *            <li> {@code dstBegin+(srcEnd-srcBegin)} is larger than {@code
     *                 dst.length}
     *          </ul>
     *
     * @diffblue.noSupport
     */
    @Deprecated
    public void getBytes(int srcBegin, int srcEnd, byte dst[], int dstBegin) {
        CProver.notModelled();
        // if (srcBegin < 0) {
        //     throw new StringIndexOutOfBoundsException(srcBegin);
        // }
        // if (srcEnd > value.length) {
        //     throw new StringIndexOutOfBoundsException(srcEnd);
        // }
        // if (srcBegin > srcEnd) {
        //     throw new StringIndexOutOfBoundsException(srcEnd - srcBegin);
        // }
        // Objects.requireNonNull(dst);
        //
        // int j = dstBegin;
        // int n = srcEnd;
        // int i = srcBegin;
        // char[] val = value;   /* avoid getfield opcode */
        //
        // while (i < n) {
        //     dst[j++] = (byte)val[i++];
        // }
    }

    /**
     * Encodes this {@code String} into a sequence of bytes using the named
     * charset, storing the result into a new byte array.
     *
     * <p> The behavior of this method when this string cannot be encoded in
     * the given charset is unspecified.  The {@link
     * java.nio.charset.CharsetEncoder} class should be used when more control
     * over the encoding process is required.
     *
     * @param  charsetName
     *         The name of a supported {@linkplain java.nio.charset.Charset
     *         charset}
     *
     * @return  The resultant byte array
     *
     * @throws  UnsupportedEncodingException
     *          If the named charset is not supported
     *
     * @since  JDK1.1
     *
     * @diffblue.limitedSupport
     * Works as expected when the argument equals one of the following:
     * <ul>
     *     <li> "US-ASCII" </li>
     *     <li> "UTF-16BE" </li>
     *     <li> "UTF-16LE" </li>
     *     <li> "UTF-16" </li>
     * </ul>
     * Will enforce the string is composed of ASCII characters when the argument
     * equals one of the following:
     * <ul>
     *     <li> "ISO-8859-1" </li>
     *     <li> "UTF-8" </li>
     * </ul>
     * UnsupportedEncodingException will never be thrown.
     *
     * Uses loops so is affected by the `unwind` parameter, which needs to be
     * 1 + length of the string to convert.
     */
    public byte[] getBytes(String charsetName)
        throws java.io.UnsupportedEncodingException {
        if (charsetName == null) throw new NullPointerException();
        // return StringCoding.encode(charsetName, value, 0, value.length);

        if(CProverString.equals(charsetName, "US-ASCII"))
            return getBytesAscii();
        if(CProverString.equals(charsetName, "UTF-16BE"))
            return getBytesUTF_16BE();
        if(CProverString.equals(charsetName, "UTF-16LE"))
            return getBytesUTF_16LE();
        if(CProverString.equals(charsetName, "UTF-16"))
            return getBytesUTF_16();

        // DIFFBLUE MODEL LIBRARY For now these conversions are not efficient so
        // we force the string to be ASCII.
        // DIFFBLUE MODEL LIBRARY @diffblue.todo: Support further encodings
        if(CProverString.equals(charsetName, "ISO-8859-1"))
            return cproverGetBytesEnforceAscii();

        CProver.assume(CProverString.equals(charsetName, "UTF-8"));
        return cproverGetBytesEnforceAscii();

        // DIFFBLUE MODEL LIBRARY We do not know the complete list of supported
        // encodings so we cannot be sure an exception will be thrown.
        // throw new java.io.UnsupportedEncodingException();
    }

    /**
     * Encodes this {@code String} into a sequence of bytes using the given
     * {@linkplain java.nio.charset.Charset charset}, storing the result into a
     * new byte array.
     *
     * <p> This method always replaces malformed-input and unmappable-character
     * sequences with this charset's default replacement byte array.  The
     * {@link java.nio.charset.CharsetEncoder} class should be used when more
     * control over the encoding process is required.
     *
     * @param  charset
     *         The {@linkplain java.nio.charset.Charset} to be used to encode
     *         the {@code String}
     *
     * @return  The resultant byte array
     *
     * @since  1.6
     *
     * @diffblue.limitedSupport
     * Works as expected if the argument is:
     * <ul>
     *     <li> "US-ASCII" </li>
     * </ul>
     * Will enforce the string does not contain code points from supplementary
     * planes https://en.wikipedia.org/wiki/Plane_(Unicode)#Supplementary_Multilingual_Plane
     * if the argument is:
     * <ul>
     *     <li> "UTF-16BE" </li>
     *     <li> "UTF-16LE" </li>
     *     <li> "UTF-16" </li>
     * </ul>
     * Will enforce the string is composed of ASCII characters if the argument
     * is one of the following:
     * <ul>
     *     <li> "ISO-8859-1" </li>
     *     <li> "UTF-8" </li>
     * </ul>
     *
     * Uses loops so is affected by the `unwind` parameter, which needs to be
     * 1 + length of the string to convert.
     */
    public byte[] getBytes(Charset charset) {
        // if (charset == null) throw new NullPointerException();
        // return StringCoding.encode(charset, value, 0, value.length);
        // DIFFBLUE MODEL LIBRARY
        // @diffblue.todo: Write a model for StringCoding, change this method
        // back to its original implementation and remove the import for
        // StandardCharsets.
        if (charset == null) {
            throw new NullPointerException();
        }
        if (CProverString.equals(charset.name(), "US-ASCII")) {
            return getBytesAscii();
        }
        if (CProverString.equals(charset.name(), "UTF-16BE")) {
            return getBytesUTF_16BE();
        }
        if (CProverString.equals(charset.name(), "UTF-16LE")) {
            return getBytesUTF_16LE();
        }
        if (CProverString.equals(charset.name(), "UTF-16")) {
            return getBytesUTF_16();
        }
        // DIFFBLUE MODEL LIBRARY @diffblue.todo: Support further encodings
        // (StandardCharsets.ISO_8859_1, StandardCharsets.UTF_8, ...)
        CProver.assume(CProverString.equals(charset.name(), "UTF-8")
                       || CProverString.equals(charset.name(), "ISO-8859-1"));
        return cproverGetBytesEnforceAscii();
    }

    // DIFFBLUE MODELS LIBRARY
    // The following private methods are utility function for our model of
    // getBytes and are not present in the original implementation.
    private byte[] getBytesAscii() {
        int l = length();
        byte result[] = new byte[l];
        for(int i = 0; i < l; i++)
        {
            char c = CProverString.charAt(this, i);
            if(c>127)
                result[i] = (byte) '?';
            else
                result[i] = (byte) c;
        }
        return result;
    }

    // DIFFBLUE MODELS LIBRARY utility function
    // This converts the String to a byte array and adds assumptions enforcing
    // all characters are valid ASCII
    private byte[] cproverGetBytesEnforceAscii() {
        int l = length();
        byte result[] = new byte[l];
        for(int i = 0; i < l; i++)
        {
            char c = CProverString.charAt(this, i);
            CProver.assume(c<=127);
            result[i] = (byte) c;
        }
        return result;
    }

    // DIFFBLUE MODELS LIBRARY utility function
    private byte[] getBytesISO_8859_1() {
        int l = length();
        byte result[] = new byte[l];
        for(int i = 0; i < l; i++)
        {
            char c = CProverString.charAt(this, i);
            if(c>255)
                result[i] = (byte) '?';
            else
                result[i] = (byte) c;
        }
        return result;
    }

    // DIFFBLUE MODELS LIBRARY utility function
    private byte[] getBytesUTF_16BE() {
        int l = length();
        byte result[] = new byte[2*l];
        for(int i = 0; i < l; i++)
        {
            char c = CProverString.charAt(this, i);
            CProver.assume(c < '\ud800');
            result[2*i] = (byte) (c >> 8);
            result[2*i+1] = (byte) (c & 0xFF);
        }
        return result;
    }

    // DIFFBLUE MODELS LIBRARY utility function
    private byte[] getBytesUTF_16LE() {
        int l = length();
        byte result[] = new byte[2*l];
        for(int i = 0; i < l; i++)
        {
            char c = CProverString.charAt(this, i);
            CProver.assume(c < '\ud800');
            result[2*i] = (byte) (c & 0xFF);
            result[2*i+1] = (byte) (c >> 8);
        }
        return result;
    }

    // DIFFBLUE MODELS LIBRARY utility function
    private byte[] getBytesUTF_16() {
        // Like UTF_16BE with FE FF at the beginning to mark byte order
        int l = length();
        byte result[] = new byte[2*l+2];
        result[0] = (byte) 0xFE;
        result[1] = (byte) 0xFF;
        for (int i = 0; i < l; i++)
        {
            char c = CProverString.charAt(this, i);
            CProver.assume(c < '\ud800');
            result[2 * i + 2] = (byte) (c >> 8);
            result[2 * i + 3] = (byte) (c & 0xFF);
        }
        return result;
    }

    // DIFFBLUE MODELS LIBRARY utility function
    private byte[] getBytesUTF_8() {
        int l = length();
        int output_size = 0;
        for(int i = 0; i < l; i++)
        {
            int c = CProverString.charAt(this, i);
            if(c>=0xD800)
            {
                i++;
                c = 0x10000 | ((c & 0x3FF) << 10)
                        | (CProverString.charAt(this, i) & 0x3FF);
            }
            if(c<=0x7F)
                output_size += 1;
            else if(c<=0x7FF)
                output_size += 2;
            else if(c<=0xFFFF)
                output_size += 3;
            else
                output_size += 4;
        }

        byte result[] = new byte[output_size];
        int index = 0;
        for(int i = 0; i < l; i++)
        {
            int c = CProverString.charAt(this, i);
            if(c>=0xD800)
            {
                i++;
                c = 0x10000 | ((c & 0x3FF) << 10)
                        | (CProverString.charAt(this, i) & 0x3FF);
            }
            if(c<=0x7F)
                result[index++]=(byte)c;
            else if(c<=0x7FF)
            {
                result[index++]=(byte)((c >> 6) | 0xC0);
                result[index++]=(byte)((c & 0x3F) | 0x80);
            }
            else if(c<=0xFFFF)
            {
                result[index++]=(byte)((c >> 12) | 0xE0);
                result[index++]=(byte)(((c >> 6) &0x3F) | 0x80);
                result[index++]=(byte)((c & 0x3F) | 0x80);
            }
            else
            {
                result[index++]=(byte)((c >> 18) | 0xF0);
                result[index++]=(byte)(((c >> 12) & 0x3F)| 0x80);
                result[index++]=(byte)(((c >> 6) & 0x3F) | 0x80);
                result[index++]=(byte)((c & 0x3F) | 0x80);
            }
        }
        return result;
    }

    /**
     * DIFFBLUE MODEL LIBRARY
     * `getBytes` is not an injective function because there are several ways
     * of getting `?` in some encoding. This version makes sure there is only
     * one possible way to get a character, by assuming characters are ASCII
     * for non-UTF-16 encodings. This is useful for models of methods which
     * do the inverse transformation of getBytes.
     *
     * @param  charsetName
     *         The name of the Charset used to encode the {@code String}
     */
    private byte[] cproverReversibleGetBytes(String charsetName)
        throws UnsupportedEncodingException
    {
        if (CProverString.equals(charsetName, "UTF-16BE")) {
            return getBytesUTF_16BE();
        }
        if (CProverString.equals(charsetName, "UTF-16LE")) {
            return getBytesUTF_16LE();
        }
        if (CProverString.equals(charsetName, "UTF-16")) {
            return getBytesUTF_16();
        }
        if (CProverString.equals(charsetName, "UTF-8")
                || CProverString.equals(charsetName, "ISO-8859-1")
                || CProverString.equals(charsetName, "US-ASCII")) {
            return cproverGetBytesEnforceAscii();
        }
        throw new UnsupportedEncodingException(charsetName);
    }

    /**
     * Overload of cproverReversibleGetBytes(String charsetName) with a Charset
     * parameter instead of a String.
     *
     * @param  charset
     *         The {@linkplain java.nio.charset.Charset} to be used to encode
     *         the {@code String}
     */
    private byte[] cproverReversibleGetBytes(Charset charset) {
        if (charset == null) {
            throw new NullPointerException();
        }
        try {
            return cproverReversibleGetBytes(charset.name());
        } catch (UnsupportedEncodingException e) {
            CProver.assume(false);
            return new byte[0];
        }
    }

    /**
     * Encodes this {@code String} into a sequence of bytes using the
     * platform's default charset, storing the result into a new byte array.
     *
     * <p> The behavior of this method when this string cannot be encoded in
     * the default charset is unspecified.  The {@link
     * java.nio.charset.CharsetEncoder} class should be used when more control
     * over the encoding process is required.
     *
     * @return  The resultant byte array
     *
     * @since      JDK1.1
     *
     * @diffblue.limitedSupport
     * We enforce all characters are ASCII and the standard encoding is one
     * in which ASCII characters are encoded with one byte.
     * In particular this is wrong if the standard charset is UTF16.
     */
    public byte[] getBytes() {
        // DIFFBLUE MODEL LIBRARY
        // Instead of looking for standard charset, we enforce all characters
        // are ASCII. This will be correct if the default encoding of the system
        // encodes ASCII characters as one byte: this is the case of ASCII,
        // UTF-8 and ISO-8859-1 but not UTF-16.
        // return StringCoding.encode(value, 0, value.length);
        return cproverGetBytesEnforceAscii();
    }

    /**
     * Compares this string to the specified object.  The result is {@code
     * true} if and only if the argument is not {@code null} and is a {@code
     * String} object that represents the same sequence of characters as this
     * object.
     *
     * @param  anObject
     *         The object to compare this {@code String} against
     *
     * @return  {@code true} if the given object represents a {@code String}
     *          equivalent to this string, {@code false} otherwise
     *
     * @see  #compareTo(String)
     * @see  #equalsIgnoreCase(String)
     *
     * @diffblue.fullSupport
     */
    public boolean equals(Object anObject) {
        // if (this == anObject) {
        //     return true;
        // }
        // if (anObject instanceof String) {
        //     String anotherString = (String)anObject;
        //     int n = value.length;
        //     if (n == anotherString.value.length) {
        //         char v1[] = value;
        //         char v2[] = anotherString.value;
        //         int i = 0;
        //         while (n-- != 0) {
        //             if (v1[i] != v2[i])
        //                 return false;
        //             i++;
        //         }
        //         return true;
        //     }
        // }
        // return false;

        // DIFFBLUE MODEL use a CProverString function
        if (anObject instanceof String) {
            return CProverString.equals((String) anObject, this);
        }
        return false;
    }

    /**
     * Compares this string to the specified {@code StringBuffer}.  The result
     * is {@code true} if and only if this {@code String} represents the same
     * sequence of characters as the specified {@code StringBuffer}. This method
     * synchronizes on the {@code StringBuffer}.
     *
     * @param  sb
     *         The {@code StringBuffer} to compare this {@code String} against
     *
     * @return  {@code true} if this {@code String} represents the same
     *          sequence of characters as the specified {@code StringBuffer},
     *          {@code false} otherwise
     *
     * @since  1.4
     *
     * @diffblue.noSupport
     */
    public boolean contentEquals(StringBuffer sb) {
        // return contentEquals((CharSequence)sb);
        CProver.notModelled();
        return CProver.nondetBoolean();
    }

    // DIFFBLUE MODEL LIBRARY Unused private method
    // private boolean nonSyncContentEquals(AbstractStringBuilder sb) {
    //     char v1[] = value;
    //     char v2[] = sb.getValue();
    //     int n = v1.length;
    //     if (n != sb.length()) {
    //         return false;
    //     }
    //     for (int i = 0; i < n; i++) {
    //         if (v1[i] != v2[i]) {
    //             return false;
    //         }
    //     }
    //     return true;
    // }

    /**
     * Compares this string to the specified {@code CharSequence}.  The
     * result is {@code true} if and only if this {@code String} represents the
     * same sequence of char values as the specified sequence. Note that if the
     * {@code CharSequence} is a {@code StringBuffer} then the method
     * synchronizes on it.
     *
     * @param  cs
     *         The sequence to compare this {@code String} against
     *
     * @return  {@code true} if this {@code String} represents the same
     *          sequence of char values as the specified sequence, {@code
     *          false} otherwise
     *
     * @since  1.5
     *
     * @diffblue.noSupport
     * @diffblue.todo implement internally in CBMC
     */
    public boolean contentEquals(CharSequence cs) {
        CProver.notModelled();
        return CProver.nondetBoolean();
        // // Argument is a StringBuffer, StringBuilder
        // if (cs instanceof AbstractStringBuilder) {
        //     if (cs instanceof StringBuffer) {
        //         synchronized(cs) {
        //            return nonSyncContentEquals((AbstractStringBuilder)cs);
        //         }
        //     } else {
        //         return nonSyncContentEquals((AbstractStringBuilder)cs);
        //     }
        // }
        // // Argument is a String
        // if (cs.equals(this))
        //     return true;
        // // Argument is a generic CharSequence
        // char v1[] = value;
        // int n = v1.length;
        // if (n != cs.length()) {
        //     return false;
        // }
        // for (int i = 0; i < n; i++) {
        //     if (v1[i] != cs.charAt(i)) {
        //         return false;
        //     }
        // }
        // return true;
    }

    /**
     * Compares this {@code String} to another {@code String}, ignoring case
     * considerations.  Two strings are considered equal ignoring case if they
     * are of the same length and corresponding characters in the two strings
     * are equal ignoring case.
     *
     * <p> Two characters {@code c1} and {@code c2} are considered the same
     * ignoring case if at least one of the following is true:
     * <ul>
     *   <li> The two characters are the same (as compared by the
     *        {@code ==} operator)
     *   <li> Applying the method {@link
     *        java.lang.Character#toUpperCase(char)} to each character
     *        produces the same result
     *   <li> Applying the method {@link
     *        java.lang.Character#toLowerCase(char)} to each character
     *        produces the same result
     * </ul>
     *
     * @param  anotherString
     *         The {@code String} to compare this {@code String} against
     *
     * @return  {@code true} if the argument is not {@code null} and it
     *          represents an equivalent {@code String} ignoring case; {@code
     *          false} otherwise
     *
     * @see  #equals(Object)
     *
     * @diffblue.limitedSupport
     * This may not be correct for the case where the argument can be null.
     * @diffblue.untested
     */
    public boolean equalsIgnoreCase(String anotherString) {
        // DIFFBLUE MODEL LIBRARY This is treated internally in CBMC
        return CProver.nondetBoolean();
        // return (this == anotherString) ? true
        //         : (anotherString != null)
        //         && (anotherString.value.length == value.length)
        //         && regionMatches(true, 0, anotherString, 0, value.length);
    }

    /**
     * Compares two strings lexicographically.
     * The comparison is based on the Unicode value of each character in
     * the strings. The character sequence represented by this
     * {@code String} object is compared lexicographically to the
     * character sequence represented by the argument string. The result is
     * a negative integer if this {@code String} object
     * lexicographically precedes the argument string. The result is a
     * positive integer if this {@code String} object lexicographically
     * follows the argument string. The result is zero if the strings
     * are equal; {@code compareTo} returns {@code 0} exactly when
     * the {@link #equals(Object)} method would return {@code true}.
     * <p>
     * This is the definition of lexicographic ordering. If two strings are
     * different, then either they have different characters at some index
     * that is a valid index for both strings, or their lengths are different,
     * or both. If they have different characters at one or more index
     * positions, let <i>k</i> be the smallest such index; then the string
     * whose character at position <i>k</i> has the smaller value, as
     * determined by using the &lt; operator, lexicographically precedes the
     * other string. In this case, {@code compareTo} returns the
     * difference of the two character values at position {@code k} in
     * the two string -- that is, the value:
     * <blockquote><pre>
     * this.charAt(k)-anotherString.charAt(k)
     * </pre></blockquote>
     * If there is no index position at which they differ, then the shorter
     * string lexicographically precedes the longer string. In this case,
     * {@code compareTo} returns the difference of the lengths of the
     * strings -- that is, the value:
     * <blockquote><pre>
     * this.length()-anotherString.length()
     * </pre></blockquote>
     *
     * @param   anotherString   the {@code String} to be compared.
     * @return  the value {@code 0} if the argument string is equal to
     *          this string; a value less than {@code 0} if this string
     *          is lexicographically less than the string argument; and a
     *          value greater than {@code 0} if this string is
     *          lexicographically greater than the string argument.
     *
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    public int compareTo(String anotherString) {
        // DIFFBLUE MODEL LIBRARY This is treated internally in CBMC
        return CProver.nondetInt();
        // int len1 = value.length;
        // int len2 = anotherString.value.length;
        // int lim = Math.min(len1, len2);
        // char v1[] = value;
        // char v2[] = anotherString.value;
        //
        // int k = 0;
        // while (k < lim) {
        //     char c1 = v1[k];
        //     char c2 = v2[k];
        //     if (c1 != c2) {
        //         return c1 - c2;
        //     }
        //     k++;
        // }
        // return len1 - len2;
    }

    /**
     * A Comparator that orders {@code String} objects as by
     * {@code compareToIgnoreCase}. This comparator is serializable.
     * <p>
     * Note that this Comparator does <em>not</em> take locale into account,
     * and will result in an unsatisfactory ordering for certain locales.
     * The java.text package provides <em>Collators</em> to allow
     * locale-sensitive ordering.
     *
     * @see     java.text.Collator#compare(String, String)
     * @since   1.2
     *
     * @diffblue.noSupport
     * @diffblue.untested
     */
    // public static final Comparator<String> CASE_INSENSITIVE_ORDER
    //                                      = new CaseInsensitiveComparator();
    // DIFFBLUE MODEL LIBRARY For some reason this needs to be not null for
    // FileReader tests to pass.
    public static final Comparator<String> CASE_INSENSITIVE_ORDER
                                            = CProver.nondetWithoutNullForNotModelled();
    // DIFFBLUE MODEL LIBRARY Not needed for modelling
    // private static class CaseInsensitiveComparator
    //         implements Comparator<String>, java.io.Serializable {
    //     // use serialVersionUID from JDK 1.2.2 for interoperability
    //     private static final long serialVersionUID = 8575799808933029326L;
    //
    //     public int compare(String s1, String s2) {
    //         int n1 = s1.length();
    //         int n2 = s2.length();
    //         int min = Math.min(n1, n2);
    //         for (int i = 0; i < min; i++) {
    //             char c1 = s1.charAt(i);
    //             char c2 = s2.charAt(i);
    //             if (c1 != c2) {
    //                 c1 = Character.toUpperCase(c1);
    //                 c2 = Character.toUpperCase(c2);
    //                 if (c1 != c2) {
    //                     c1 = Character.toLowerCase(c1);
    //                     c2 = Character.toLowerCase(c2);
    //                     if (c1 != c2) {
    //                         // No overflow because of numeric promotion
    //                         return c1 - c2;
    //                     }
    //                 }
    //             }
    //         }
    //         return n1 - n2;
    //     }
    //
    //     /** Replaces the de-serialized object. */
    //     private Object readResolve() { return CASE_INSENSITIVE_ORDER; }
    // }

    /**
     * Compares two strings lexicographically, ignoring case
     * differences. This method returns an integer whose sign is that of
     * calling {@code compareTo} with normalized versions of the strings
     * where case differences have been eliminated by calling
     * {@code Character.toLowerCase(Character.toUpperCase(character))} on
     * each character.
     * <p>
     * Note that this method does <em>not</em> take locale into account,
     * and will result in an unsatisfactory ordering for certain locales.
     * The java.text package provides <em>collators</em> to allow
     * locale-sensitive ordering.
     *
     * @param   str   the {@code String} to be compared.
     * @return  a negative integer, zero, or a positive integer as the
     *          specified String is greater than, equal to, or less
     *          than this String, ignoring case considerations.
     * @see     java.text.Collator#compare(String, String)
     * @since   1.2
     *
     * @diffblue.noSupport
     */
    public int compareToIgnoreCase(String str) {
        CProver.notModelled();
        return CProver.nondetInt();
        // return CASE_INSENSITIVE_ORDER.compare(this, str);
    }

    /**
     * Tests if two string regions are equal.
     * <p>
     * A substring of this {@code String} object is compared to a substring
     * of the argument other. The result is true if these substrings
     * represent identical character sequences. The substring of this
     * {@code String} object to be compared begins at index {@code toffset}
     * and has length {@code len}. The substring of other to be compared
     * begins at index {@code ooffset} and has length {@code len}. The
     * result is {@code false} if and only if at least one of the following
     * is true:
     * <ul><li>{@code toffset} is negative.
     * <li>{@code ooffset} is negative.
     * <li>{@code toffset+len} is greater than the length of this
     * {@code String} object.
     * <li>{@code ooffset+len} is greater than the length of the other
     * argument.
     * <li>There is some nonnegative integer <i>k</i> less than {@code len}
     * such that:
     * {@code this.charAt(toffset + }<i>k</i>{@code ) != other.charAt(ooffset + }
     * <i>k</i>{@code )}
     * </ul>
     *
     * @param   toffset   the starting offset of the subregion in this string.
     * @param   other     the string argument.
     * @param   ooffset   the starting offset of the subregion in the string
     *                    argument.
     * @param   len       the number of characters to compare.
     * @return  {@code true} if the specified subregion of this string
     *          exactly matches the specified subregion of the string argument;
     *          {@code false} otherwise.
     *
     * @diffblue.noSupport
     */
    public boolean regionMatches(int toffset, String other, int ooffset,
            int len) {
        CProver.notModelled();
        return CProver.nondetBoolean();
        // char ta[] = value;
        // int to = toffset;
        // char pa[] = other.value;
        // int po = ooffset;
        // // Note: toffset, ooffset, or len might be near -1>>>1.
        // if ((ooffset < 0) || (toffset < 0)
        //         || (toffset > (long)value.length - len)
        //         || (ooffset > (long)other.value.length - len)) {
        //     return false;
        // }
        // while (len-- > 0) {
        //     if (ta[to++] != pa[po++]) {
        //         return false;
        //     }
        // }
        // return true;
    }

    /**
     * Tests if two string regions are equal.
     * <p>
     * A substring of this {@code String} object is compared to a substring
     * of the argument {@code other}. The result is {@code true} if these
     * substrings represent character sequences that are the same, ignoring
     * case if and only if {@code ignoreCase} is true. The substring of
     * this {@code String} object to be compared begins at index
     * {@code toffset} and has length {@code len}. The substring of
     * {@code other} to be compared begins at index {@code ooffset} and
     * has length {@code len}. The result is {@code false} if and only if
     * at least one of the following is true:
     * <ul><li>{@code toffset} is negative.
     * <li>{@code ooffset} is negative.
     * <li>{@code toffset+len} is greater than the length of this
     * {@code String} object.
     * <li>{@code ooffset+len} is greater than the length of the other
     * argument.
     * <li>{@code ignoreCase} is {@code false} and there is some nonnegative
     * integer <i>k</i> less than {@code len} such that:
     * <blockquote><pre>
     * this.charAt(toffset+k) != other.charAt(ooffset+k)
     * </pre></blockquote>
     * <li>{@code ignoreCase} is {@code true} and there is some nonnegative
     * integer <i>k</i> less than {@code len} such that:
     * <blockquote><pre>
     * Character.toLowerCase(this.charAt(toffset+k)) !=
     Character.toLowerCase(other.charAt(ooffset+k))
     * </pre></blockquote>
     * and:
     * <blockquote><pre>
     * Character.toUpperCase(this.charAt(toffset+k)) !=
     *         Character.toUpperCase(other.charAt(ooffset+k))
     * </pre></blockquote>
     * </ul>
     *
     * @param   ignoreCase   if {@code true}, ignore case when comparing
     *                       characters.
     * @param   toffset      the starting offset of the subregion in this
     *                       string.
     * @param   other        the string argument.
     * @param   ooffset      the starting offset of the subregion in the string
     *                       argument.
     * @param   len          the number of characters to compare.
     * @return  {@code true} if the specified subregion of this string
     *          matches the specified subregion of the string argument;
     *          {@code false} otherwise. Whether the matching is exact
     *          or case insensitive depends on the {@code ignoreCase}
     *          argument.
     *
     * @diffblue.noSupport
     */
    public boolean regionMatches(boolean ignoreCase, int toffset,
            String other, int ooffset, int len) {
        CProver.notModelled();
        return CProver.nondetBoolean();
        // char ta[] = value;
        // int to = toffset;
        // char pa[] = other.value;
        // int po = ooffset;
        // // Note: toffset, ooffset, or len might be near -1>>>1.
        // if ((ooffset < 0) || (toffset < 0)
        //         || (toffset > (long)value.length - len)
        //         || (ooffset > (long)other.value.length - len)) {
        //     return false;
        // }
        // while (len-- > 0) {
        //     char c1 = ta[to++];
        //     char c2 = pa[po++];
        //     if (c1 == c2) {
        //         continue;
        //     }
        //     if (ignoreCase) {
        //         // If characters don't match but case may be ignored,
        //         // try converting both characters to uppercase.
        //         // If the results match, then the comparison scan should
        //         // continue.
        //         char u1 = Character.toUpperCase(c1);
        //         char u2 = Character.toUpperCase(c2);
        //         if (u1 == u2) {
        //             continue;
        //         }
        //         // Unfortunately, conversion to uppercase does not work properly
        //         // for the Georgian alphabet, which has strange rules about case
        //         // conversion.  So we need to make one last check before
        //         // exiting.
        //         if (Character.toLowerCase(u1) == Character.toLowerCase(u2)) {
        //             continue;
        //         }
        //     }
        //     return false;
        // }
        // return true;
    }

    /**
     * Tests if the substring of this string beginning at the
     * specified index starts with the specified prefix.
     *
     * @param   prefix    the prefix.
     * @param   toffset   where to begin looking in this string.
     * @return  {@code true} if the character sequence represented by the
     *          argument is a prefix of the substring of this object starting
     *          at index {@code toffset}; {@code false} otherwise.
     *          The result is {@code false} if {@code toffset} is
     *          negative or greater than the length of this
     *          {@code String} object; otherwise the result is the same
     *          as the result of the expression
     *          <pre>
     *          this.substring(toffset).startsWith(prefix)
     *          </pre>
     *
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    public boolean startsWith(String prefix, int toffset) {
        // DIFFBLUE MODEL LIBRARY This is treated internally in CBMC
        return CProver.nondetBoolean();
        // char ta[] = value;
        // int to = toffset;
        // char pa[] = prefix.value;
        // int po = 0;
        // int pc = prefix.value.length;
        // // Note: toffset might be near -1>>>1.
        // if ((toffset < 0) || (toffset > value.length - pc)) {
        //     return false;
        // }
        // while (--pc >= 0) {
        //     if (ta[to++] != pa[po++]) {
        //         return false;
        //     }
        // }
        // return true;
    }

    /**
     * Tests if this string starts with the specified prefix.
     *
     * @param   prefix   the prefix.
     * @return  {@code true} if the character sequence represented by the
     *          argument is a prefix of the character sequence represented by
     *          this string; {@code false} otherwise.
     *          Note also that {@code true} will be returned if the
     *          argument is an empty string or is equal to this
     *          {@code String} object as determined by the
     *          {@link #equals(Object)} method.
     * @since   1. 0
     *
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    public boolean startsWith(String prefix) {
        // DIFFBLUE MODEL LIBRARY This is treated internally in CBMC
        return CProver.nondetBoolean();
        // return startsWith(prefix, 0);
    }

    /**
     * Tests if this string ends with the specified suffix.
     *
     * @param   suffix   the suffix.
     * @return  {@code true} if the character sequence represented by the
     *          argument is a suffix of the character sequence represented by
     *          this object; {@code false} otherwise. Note that the
     *          result will be {@code true} if the argument is the
     *          empty string or is equal to this {@code String} object
     *          as determined by the {@link #equals(Object)} method.
     *
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    public boolean endsWith(String suffix) {
        // DIFFBLUE MODEL LIBRARY This is treated internally in CBMC
        return CProver.nondetBoolean();
        // return startsWith(suffix, value.length - suffix.value.length);
    }

    /**
     * Returns a hash code for this string. The hash code for a
     * {@code String} object is computed as
     * <blockquote><pre>
     * s[0]*31^(n-1) + s[1]*31^(n-2) + ... + s[n-1]
     * </pre></blockquote>
     * using {@code int} arithmetic, where {@code s[i]} is the
     * <i>i</i>th character of the string, {@code n} is the length of
     * the string, and {@code ^} indicates exponentiation.
     * (The hash value of the empty string is zero.)
     *
     * @return  a hash code value for this object.
     *
     * @diffblue.fullSupport length of the string is limited by unwind value
     */
    public int hashCode() {
        // DIFFBLUE MODEL LIBRARY
        // int h = hash;
        // if (h == 0 && value.length > 0) {
        //     char val[] = value;
        //
        //     for (int i = 0; i < value.length; i++) {
        //         h = 31 * h + val[i];
        //     }
        //     hash = h;
        // }
        // return h;
        int len = length();
        int h = 0;
        for (int i = 0; i < len; i++) {
            h = 31 * h + CProverString.charAt(this, i);
        }
        return h;
    }

    /**
     * Returns the index within this string of the first occurrence of
     * the specified character. If a character with value
     * {@code ch} occurs in the character sequence represented by
     * this {@code String} object, then the index (in Unicode
     * code units) of the first such occurrence is returned. For
     * values of {@code ch} in the range from 0 to 0xFFFF
     * (inclusive), this is the smallest value <i>k</i> such that:
     * <blockquote><pre>
     * this.charAt(<i>k</i>) == ch
     * </pre></blockquote>
     * is true. For other values of {@code ch}, it is the
     * smallest value <i>k</i> such that:
     * <blockquote><pre>
     * this.codePointAt(<i>k</i>) == ch
     * </pre></blockquote>
     * is true. In either case, if no such character occurs in this
     * string, then {@code -1} is returned.
     *
     * @param   ch   a character (Unicode code point).
     * @return  the index of the first occurrence of the character in the
     *          character sequence represented by this object, or
     *          {@code -1} if the character does not occur.
     *
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    public int indexOf(int ch) {
        // DIFFBLUE MODEL LIBRARY This is treated internally in CBMC
        return CProver.nondetInt();
        // return indexOf(ch, 0);
    }

    /**
     * Returns the index within this string of the first occurrence of the
     * specified character, starting the search at the specified index.
     * <p>
     * If a character with value {@code ch} occurs in the
     * character sequence represented by this {@code String}
     * object at an index no smaller than {@code fromIndex}, then
     * the index of the first such occurrence is returned. For values
     * of {@code ch} in the range from 0 to 0xFFFF (inclusive),
     * this is the smallest value <i>k</i> such that:
     * <blockquote><pre>
     * (this.charAt(<i>k</i>) == ch) {@code &&} (<i>k</i> &gt;= fromIndex)
     * </pre></blockquote>
     * is true. For other values of {@code ch}, it is the
     * smallest value <i>k</i> such that:
     * <blockquote><pre>
     * (this.codePointAt(<i>k</i>) == ch) {@code &&} (<i>k</i> &gt;= fromIndex)
     * </pre></blockquote>
     * is true. In either case, if no such character occurs in this
     * string at or after position {@code fromIndex}, then
     * {@code -1} is returned.
     *
     * <p>
     * There is no restriction on the value of {@code fromIndex}. If it
     * is negative, it has the same effect as if it were zero: this entire
     * string may be searched. If it is greater than the length of this
     * string, it has the same effect as if it were equal to the length of
     * this string: {@code -1} is returned.
     *
     * <p>All indices are specified in {@code char} values
     * (Unicode code units).
     *
     * @param   ch          a character (Unicode code point).
     * @param   fromIndex   the index to start the search from.
     * @return  the index of the first occurrence of the character in the
     *          character sequence represented by this object that is greater
     *          than or equal to {@code fromIndex}, or {@code -1}
     *          if the character does not occur.
     *
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    public int indexOf(int ch, int fromIndex) {
        // DIFFBLUE MODEL LIBRARY This is treated internally in CBMC
        return CProver.nondetInt();
        // final int max = value.length;
        // if (fromIndex < 0) {
        //     fromIndex = 0;
        // } else if (fromIndex >= max) {
        //     // Note: fromIndex might be near -1>>>1.
        //     return -1;
        // }
        //
        // if (ch < Character.MIN_SUPPLEMENTARY_CODE_POINT) {
        //     // handle most cases here (ch is a BMP code point or a
        //     // negative value (invalid code point))
        //     final char[] value = this.value;
        //     for (int i = fromIndex; i < max; i++) {
        //         if (value[i] == ch) {
        //             return i;
        //         }
        //     }
        //     return -1;
        // } else {
        //     return indexOfSupplementary(ch, fromIndex);
        // }
    }

    /**
     * Handles (rare) calls of indexOf with a supplementary character.
     */
    // DIFFBLUE MODEL LIBRARY Unused private method
    // private int indexOfSupplementary(int ch, int fromIndex) {
    //     if (Character.isValidCodePoint(ch)) {
    //         final char[] value = this.value;
    //         final char hi = Character.highSurrogate(ch);
    //         final char lo = Character.lowSurrogate(ch);
    //         final int max = value.length - 1;
    //         for (int i = fromIndex; i < max; i++) {
    //             if (value[i] == hi && value[i + 1] == lo) {
    //                 return i;
    //             }
    //         }
    //     }
    //     return -1;
    // }

    /**
     * Returns the index within this string of the last occurrence of
     * the specified character. For values of {@code ch} in the
     * range from 0 to 0xFFFF (inclusive), the index (in Unicode code
     * units) returned is the largest value <i>k</i> such that:
     * <blockquote><pre>
     * this.charAt(<i>k</i>) == ch
     * </pre></blockquote>
     * is true. For other values of {@code ch}, it is the
     * largest value <i>k</i> such that:
     * <blockquote><pre>
     * this.codePointAt(<i>k</i>) == ch
     * </pre></blockquote>
     * is true.  In either case, if no such character occurs in this
     * string, then {@code -1} is returned.  The
     * {@code String} is searched backwards starting at the last
     * character.
     *
     * @param   ch   a character (Unicode code point).
     * @return  the index of the last occurrence of the character in the
     *          character sequence represented by this object, or
     *          {@code -1} if the character does not occur.
     *
     * @diffblue.limitedSupport
     * This is not correct for supplementary code points.
     * @diffblue.untested
     */
    public int lastIndexOf(int ch) {
        // DIFFBLUE MODEL LIBRARY This is treated internally in CBMC
        return CProver.nondetInt();
        // return lastIndexOf(ch, value.length - 1);
    }

    /**
     * Returns the index within this string of the last occurrence of
     * the specified character, searching backward starting at the
     * specified index. For values of {@code ch} in the range
     * from 0 to 0xFFFF (inclusive), the index returned is the largest
     * value <i>k</i> such that:
     * <blockquote><pre>
     * (this.charAt(<i>k</i>) == ch) {@code &&} (<i>k</i> &lt;= fromIndex)
     * </pre></blockquote>
     * is true. For other values of {@code ch}, it is the
     * largest value <i>k</i> such that:
     * <blockquote><pre>
     * (this.codePointAt(<i>k</i>) == ch) {@code &&} (<i>k</i> &lt;= fromIndex)
     * </pre></blockquote>
     * is true. In either case, if no such character occurs in this
     * string at or before position {@code fromIndex}, then
     * {@code -1} is returned.
     *
     * <p>All indices are specified in {@code char} values
     * (Unicode code units).
     *
     * @param   ch          a character (Unicode code point).
     * @param   fromIndex   the index to start the search from. There is no
     *          restriction on the value of {@code fromIndex}. If it is
     *          greater than or equal to the length of this string, it has
     *          the same effect as if it were equal to one less than the
     *          length of this string: this entire string may be searched.
     *          If it is negative, it has the same effect as if it were -1:
     *          -1 is returned.
     * @return  the index of the last occurrence of the character in the
     *          character sequence represented by this object that is less
     *          than or equal to {@code fromIndex}, or {@code -1}
     *          if the character does not occur before that point.
     *
     * @diffblue.limitedSupport
     * This is not correct for supplementary code points.
     */
    public int lastIndexOf(int ch, int fromIndex) {
        // DIFFBLUE MODEL LIBRARY This is treated internally in CBMC
        return CProver.nondetInt();
        // if (ch < Character.MIN_SUPPLEMENTARY_CODE_POINT) {
        //     // handle most cases here (ch is a BMP code point or a
        //     // negative value (invalid code point))
        //     final char[] value = this.value;
        //     int i = Math.min(fromIndex, value.length - 1);
        //     for (; i >= 0; i--) {
        //         if (value[i] == ch) {
        //             return i;
        //         }
        //     }
        //     return -1;
        // } else {
        //     return lastIndexOfSupplementary(ch, fromIndex);
        // }
    }

    /**
     * Handles (rare) calls of lastIndexOf with a supplementary character.
     */
    // DIFFBLUE MODEL LIBRARY Unused private method
    // private int lastIndexOfSupplementary(int ch, int fromIndex) {
    //     if (Character.isValidCodePoint(ch)) {
    //         final char[] value = this.value;
    //         char hi = Character.highSurrogate(ch);
    //         char lo = Character.lowSurrogate(ch);
    //         int i = Math.min(fromIndex, value.length - 2);
    //         for (; i >= 0; i--) {
    //             if (value[i] == hi && value[i + 1] == lo) {
    //                 return i;
    //             }
    //         }
    //     }
    //     return -1;
    // }

    /**
     * Returns the index within this string of the first occurrence of the
     * specified substring.
     *
     * <p>The returned index is the smallest value <i>k</i> for which:
     * <blockquote><pre>
     * this.startsWith(str, <i>k</i>)
     * </pre></blockquote>
     * If no such value of <i>k</i> exists, then {@code -1} is returned.
     *
     * @param   str   the substring to search for.
     * @return  the index of the first occurrence of the specified substring,
     *          or {@code -1} if there is no such occurrence.
     *
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    public int indexOf(String str) {
        // DIFFBLUE MODEL LIBRARY This is treated internally in CBMC
        return CProver.nondetInt();
        // return indexOf(str, 0);
    }

    /**
     * Returns the index within this string of the first occurrence of the
     * specified substring, starting at the specified index.
     *
     * <p>The returned index is the smallest value <i>k</i> for which:
     * <blockquote><pre>
     * <i>k</i> &gt;= fromIndex {@code &&} this.startsWith(str, <i>k</i>)
     * </pre></blockquote>
     * If no such value of <i>k</i> exists, then {@code -1} is returned.
     *
     * @param   str         the substring to search for.
     * @param   fromIndex   the index from which to start the search.
     * @return  the index of the first occurrence of the specified substring,
     *          starting at the specified index,
     *          or {@code -1} if there is no such occurrence.
     *
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    public int indexOf(String str, int fromIndex) {
        // DIFFBLUE MODEL LIBRARY This is treated internally in CBMC
        return CProver.nondetInt();
        // return indexOf(value, 0, value.length,
        //         str.value, 0, str.value.length, fromIndex);
    }

    /**
     * Code shared by String and AbstractStringBuilder to do searches. The
     * source is the character array being searched, and the target
     * is the string being searched for.
     *
     * @param   source       the characters being searched.
     * @param   sourceOffset offset of the source string.
     * @param   sourceCount  count of the source string.
     * @param   target       the characters being searched for.
     * @param   fromIndex    the index to begin searching from.
     *
     * @diffblue.noSupport
     */
    static int indexOf(char[] source, int sourceOffset, int sourceCount,
            String target, int fromIndex) {
        CProver.notModelled();
        return CProver.nondetInt();
        // return indexOf(source, sourceOffset, sourceCount,
        //                target.value, 0, target.value.length,
        //                fromIndex);
    }

    /**
     * Code shared by String and StringBuffer to do searches. The
     * source is the character array being searched, and the target
     * is the string being searched for.
     *
     * @param   source       the characters being searched.
     * @param   sourceOffset offset of the source string.
     * @param   sourceCount  count of the source string.
     * @param   target       the characters being searched for.
     * @param   targetOffset offset of the target string.
     * @param   targetCount  count of the target string.
     * @param   fromIndex    the index to begin searching from.
     *
     * @diffblue.noSupport
     */
    static int indexOf(char[] source, int sourceOffset, int sourceCount,
            char[] target, int targetOffset, int targetCount,
            int fromIndex) {
        CProver.notModelled();
        return CProver.nondetInt();
        // if (fromIndex >= sourceCount) {
        //     return (targetCount == 0 ? sourceCount : -1);
        // }
        // if (fromIndex < 0) {
        //     fromIndex = 0;
        // }
        // if (targetCount == 0) {
        //     return fromIndex;
        // }
        //
        // char first = target[targetOffset];
        // int max = sourceOffset + (sourceCount - targetCount);
        //
        // for (int i = sourceOffset + fromIndex; i <= max; i++) {
        //     /* Look for first character. */
        //     if (source[i] != first) {
        //         while (++i <= max && source[i] != first);
        //     }
        //
        //     /* Found first character, now look at the rest of v2 */
        //     if (i <= max) {
        //         int j = i + 1;
        //         int end = j + targetCount - 1;
        //         for (int k = targetOffset + 1; j < end && source[j]
        //                 == target[k]; j++, k++);
        //
        //         if (j == end) {
        //             /* Found whole string. */
        //             return i - sourceOffset;
        //         }
        //     }
        // }
        // return -1;
    }

    /**
     * Returns the index within this string of the last occurrence of the
     * specified substring.  The last occurrence of the empty string ""
     * is considered to occur at the index value {@code this.length()}.
     *
     * <p>The returned index is the largest value <i>k</i> for which:
     * <blockquote><pre>
     * this.startsWith(str, <i>k</i>)
     * </pre></blockquote>
     * If no such value of <i>k</i> exists, then {@code -1} is returned.
     *
     * @param   str   the substring to search for.
     * @return  the index of the last occurrence of the specified substring,
     *          or {@code -1} if there is no such occurrence.
     *
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    public int lastIndexOf(String str) {
        // DIFFBLUE MODEL LIBRARY This is treated internally in CBMC
        return CProver.nondetInt();
        // return lastIndexOf(str, value.length);
    }

    /**
     * Returns the index within this string of the last occurrence of the
     * specified substring, searching backward starting at the specified index.
     *
     * <p>The returned index is the largest value <i>k</i> for which:
     * <blockquote><pre>
     * <i>k</i> {@code <=} fromIndex {@code &&} this.startsWith(str, <i>k</i>)
     * </pre></blockquote>
     * If no such value of <i>k</i> exists, then {@code -1} is returned.
     *
     * @param   str         the substring to search for.
     * @param   fromIndex   the index to start the search from.
     * @return  the index of the last occurrence of the specified substring,
     *          searching backward from the specified index,
     *          or {@code -1} if there is no such occurrence.
     *
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    public int lastIndexOf(String str, int fromIndex) {
        // DIFFBLUE MODEL LIBRARY This is treated internally in CBMC
        return CProver.nondetInt();
        // return lastIndexOf(value, 0, value.length,
        //         str.value, 0, str.value.length, fromIndex);
    }

    /**
     * Code shared by String and AbstractStringBuilder to do searches. The
     * source is the character array being searched, and the target
     * is the string being searched for.
     *
     * @param   source       the characters being searched.
     * @param   sourceOffset offset of the source string.
     * @param   sourceCount  count of the source string.
     * @param   target       the characters being searched for.
     * @param   fromIndex    the index to begin searching from.
     *
     * @diffblue.noSupport
     */
    static int lastIndexOf(char[] source, int sourceOffset, int sourceCount,
            String target, int fromIndex) {
        CProver.notModelled();
        return CProver.nondetInt();
        // return lastIndexOf(source, sourceOffset, sourceCount,
        //                target.value, 0, target.value.length,
        //                fromIndex);
    }

    /**
     * Code shared by String and StringBuffer to do searches. The
     * source is the character array being searched, and the target
     * is the string being searched for.
     *
     * @param   source       the characters being searched.
     * @param   sourceOffset offset of the source string.
     * @param   sourceCount  count of the source string.
     * @param   target       the characters being searched for.
     * @param   targetOffset offset of the target string.
     * @param   targetCount  count of the target string.
     * @param   fromIndex    the index to begin searching from.
     *
     * @diffblue.noSupport
     */
    static int lastIndexOf(char[] source, int sourceOffset, int sourceCount,
            char[] target, int targetOffset, int targetCount,
            int fromIndex) {
        CProver.notModelled();
        return CProver.nondetInt();
        // /*
        //  * Check arguments; return immediately where possible. For
        //  * consistency, don't check for null str.
        //  */
        // int rightIndex = sourceCount - targetCount;
        // if (fromIndex < 0) {
        //     return -1;
        // }
        // if (fromIndex > rightIndex) {
        //     fromIndex = rightIndex;
        // }
        // /* Empty string always matches. */
        // if (targetCount == 0) {
        //     return fromIndex;
        // }
        //
        // int strLastIndex = targetOffset + targetCount - 1;
        // char strLastChar = target[strLastIndex];
        // int min = sourceOffset + targetCount - 1;
        // int i = min + fromIndex;
        //
        // startSearchForLastChar:
        // while (true) {
        //     while (i >= min && source[i] != strLastChar) {
        //         i--;
        //     }
        //     if (i < min) {
        //         return -1;
        //     }
        //     int j = i - 1;
        //     int start = j - (targetCount - 1);
        //     int k = strLastIndex - 1;
        //
        //     while (j > start) {
        //         if (source[j--] != target[k--]) {
        //             i--;
        //             continue startSearchForLastChar;
        //         }
        //     }
        //     return start - sourceOffset + 1;
        // }
    }

    /**
     * Returns a string that is a substring of this string. The
     * substring begins with the character at the specified index and
     * extends to the end of this string. <p>
     * Examples:
     * <blockquote><pre>
     * "unhappy".substring(2) returns "happy"
     * "Harbison".substring(3) returns "bison"
     * "emptiness".substring(9) returns "" (an empty string)
     * </pre></blockquote>
     *
     * @param      beginIndex   the beginning index, inclusive.
     * @return     the specified substring.
     * @exception  IndexOutOfBoundsException  if
     *             {@code beginIndex} is negative or larger than the
     *             length of this {@code String} object.
     *
     * @diffblue.fullSupport
     * @diffblue.untested Only exception throwing is tested.
     */
    public String substring(int beginIndex) {
        // DIFFBLUE MODEL LIBRARY
        // Our implementation differs from the original
        // in that we use the internal CProverString.substring.
        // However the behavior is the same as the JDK version.
        // if (beginIndex < 0) {
        //     throw new StringIndexOutOfBoundsException(beginIndex);
        // }
        // int subLen = value.length - beginIndex;
        // if (subLen < 0) {
        //     throw new StringIndexOutOfBoundsException(subLen);
        // }
        // return (beginIndex == 0) ? this : new String(value, beginIndex, subLen);
        if (beginIndex < 0) {
            throw new StringIndexOutOfBoundsException(beginIndex);
        }
        int subLen = length() - beginIndex;
        if (subLen < 0) {
            throw new StringIndexOutOfBoundsException(subLen);
        }
        return CProverString.substring(this, beginIndex);
    }

    /**
     * Returns a string that is a substring of this string. The
     * substring begins at the specified {@code beginIndex} and
     * extends to the character at index {@code endIndex - 1}.
     * Thus the length of the substring is {@code endIndex-beginIndex}.
     * <p>
     * Examples:
     * <blockquote><pre>
     * "hamburger".substring(4, 8) returns "urge"
     * "smiles".substring(1, 5) returns "mile"
     * </pre></blockquote>
     *
     * @param      beginIndex   the beginning index, inclusive.
     * @param      endIndex     the ending index, exclusive.
     * @return     the specified substring.
     * @exception  IndexOutOfBoundsException  if the
     *             {@code beginIndex} is negative, or
     *             {@code endIndex} is larger than the length of
     *             this {@code String} object, or
     *             {@code beginIndex} is larger than
     *             {@code endIndex}.
     *
     * @diffblue.fullSupport
     * @diffblue.untested Only exception throwing is tested.
     */
    public String substring(int beginIndex, int endIndex) {
        // DIFFBLUE MODEL LIBRARY
        // Our implementation differs from the original
        // in that we use the internal CProverString.substring.
        // However the behavior is the same as the JDK version.
        // if (beginIndex < 0) {
        //     throw new StringIndexOutOfBoundsException(beginIndex);
        // }
        // if (endIndex > value.length) {
        //     throw new StringIndexOutOfBoundsException(endIndex);
        // }
        // int subLen = endIndex - beginIndex;
        // if (subLen < 0) {
        //     throw new StringIndexOutOfBoundsException(subLen);
        // }
        // return ((beginIndex == 0) && (endIndex == value.length)) ? this
        //         : new String(value, beginIndex, subLen);
        if (beginIndex < 0) {
            throw new StringIndexOutOfBoundsException(beginIndex);
        }
        if (endIndex > length()) {
            throw new StringIndexOutOfBoundsException(endIndex);
        }
        int subLen = endIndex - beginIndex;
        if (subLen < 0) {
            throw new StringIndexOutOfBoundsException(subLen);
        }
        // DIFFBLUE MODEL LIBRARY Exception case imported from {@code String(char value[], int offset, int count)}
        // Note: beginIndex or subLen might be near -1>>>1.
        if (beginIndex > length() - subLen) {
            throw new StringIndexOutOfBoundsException(beginIndex + subLen);
        }
        return CProverString.substring(this, beginIndex, beginIndex + subLen);
    }

    /**
     * Returns a character sequence that is a subsequence of this sequence.
     *
     * <p> An invocation of this method of the form
     *
     * <blockquote><pre>
     * str.subSequence(begin,&nbsp;end)</pre></blockquote>
     *
     * behaves in exactly the same way as the invocation
     *
     * <blockquote><pre>
     * str.substring(begin,&nbsp;end)</pre></blockquote>
     *
     * @apiNote
     * This method is defined so that the {@code String} class can implement
     * the {@link CharSequence} interface.
     *
     * @param   beginIndex   the begin index, inclusive.
     * @param   endIndex     the end index, exclusive.
     * @return  the specified subsequence.
     *
     * @throws  IndexOutOfBoundsException
     *          if {@code beginIndex} or {@code endIndex} is negative,
     *          if {@code endIndex} is greater than {@code length()},
     *          or if {@code beginIndex} is greater than {@code endIndex}
     *
     * @since 1.4
     * @spec JSR-51
     *
     * @diffblue.fullSupport
     * @diffblue.untested Only exception throwing is tested
     */
    public CharSequence subSequence(int beginIndex, int endIndex) {
        return this.substring(beginIndex, endIndex);
    }

    /**
     * Concatenates the specified string to the end of this string.
     * <p>
     * If the length of the argument string is {@code 0}, then this
     * {@code String} object is returned. Otherwise, a
     * {@code String} object is returned that represents a character
     * sequence that is the concatenation of the character sequence
     * represented by this {@code String} object and the character
     * sequence represented by the argument string.<p>
     * Examples:
     * <blockquote><pre>
     * "cares".concat("s") returns "caress"
     * "to".concat("get").concat("her") returns "together"
     * </pre></blockquote>
     *
     * @param   str   the {@code String} that is concatenated to the end
     *                of this {@code String}.
     * @return  a string that represents the concatenation of this object's
     *          characters followed by the string argument's characters.
     *
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    public String concat(String str) {
        // DIFFBLUE MODEL LIBRARY This is treated internally in CBMC
        return CProver.nondetWithNullForNotModelled();
        // int otherLen = str.length();
        // if (otherLen == 0) {
        //     return this;
        // }
        // int len = value.length;
        // char buf[] = Arrays.copyOf(value, len + otherLen);
        // str.getChars(buf, len);
        // return new String(buf, true);
    }

    /**
     * Returns a string resulting from replacing all occurrences of
     * {@code oldChar} in this string with {@code newChar}.
     * <p>
     * If the character {@code oldChar} does not occur in the
     * character sequence represented by this {@code String} object,
     * then a reference to this {@code String} object is returned.
     * Otherwise, a {@code String} object is returned that
     * represents a character sequence identical to the character sequence
     * represented by this {@code String} object, except that every
     * occurrence of {@code oldChar} is replaced by an occurrence
     * of {@code newChar}.
     * <p>
     * Examples:
     * <blockquote><pre>
     * "mesquite in your cellar".replace('e', 'o')
     *         returns "mosquito in your collar"
     * "the war of baronets".replace('r', 'y')
     *         returns "the way of bayonets"
     * "sparring with a purple porpoise".replace('p', 't')
     *         returns "starring with a turtle tortoise"
     * "JonL".replace('q', 'x') returns "JonL" (no change)
     * </pre></blockquote>
     *
     * @param   oldChar   the old character.
     * @param   newChar   the new character.
     * @return  a string derived from this string by replacing every
     *          occurrence of {@code oldChar} with {@code newChar}.
     *
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    public String replace(char oldChar, char newChar) {
        // DIFFBLUE MODEL LIBRARY This is treated internally in CBMC
        return CProver.nondetWithNullForNotModelled();
        // if (oldChar != newChar) {
        //     int len = value.length;
        //     int i = -1;
        //     char[] val = value; /* avoid getfield opcode */
        //
        //     while (++i < len) {
        //         if (val[i] == oldChar) {
        //             break;
        //         }
        //     }
        //     if (i < len) {
        //         char buf[] = new char[len];
        //         for (int j = 0; j < i; j++) {
        //             buf[j] = val[j];
        //         }
        //         while (i < len) {
        //             char c = val[i];
        //             buf[i] = (c == oldChar) ? newChar : c;
        //             i++;
        //         }
        //         return new String(buf, true);
        //     }
        // }
        // return this;
    }

    /**
     * Tells whether or not this string matches the given <a
     * href="../util/regex/Pattern.html#sum">regular expression</a>.
     *
     * <p> An invocation of this method of the form
     * <i>str</i>{@code .matches(}<i>regex</i>{@code )} yields exactly the
     * same result as the expression
     *
     * <blockquote>
     * {@link java.util.regex.Pattern}.{@link java.util.regex.Pattern#matches(String,CharSequence)
     * matches(<i>regex</i>, <i>str</i>)}
     * </blockquote>
     *
     * @param   regex
     *          the regular expression to which this string is to be matched
     *
     * @return  {@code true} if, and only if, this string matches the
     *          given regular expression
     *
     * @throws  PatternSyntaxException
     *          if the regular expression's syntax is invalid
     *
     * @see java.util.regex.Pattern
     *
     * @since 1.4
     * @spec JSR-51
     *
     * @diffblue.noSupport
     */
    public boolean matches(String regex) {
        CProver.notModelled();
        return CProver.nondetBoolean();
        // return Pattern.matches(regex, this);
    }

    /**
     * Returns true if and only if this string contains the specified
     * sequence of char values.
     *
     * @param s the sequence to search for
     * @return true if this string contains {@code s}, false otherwise
     * @since 1.5
     *
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    public boolean contains(CharSequence s) {
        // DIFFBLUE MODEL LIBRARY This is treated internally in CBMC
        return CProver.nondetBoolean();
        // return indexOf(s.toString()) > -1;
    }

    /**
     * Replaces the first substring of this string that matches the given <a
     * href="../util/regex/Pattern.html#sum">regular expression</a> with the
     * given replacement.
     *
     * <p> An invocation of this method of the form
     * <i>str</i>{@code .replaceFirst(}<i>regex</i>{@code ,} <i>repl</i>{@code )}
     * yields exactly the same result as the expression
     *
     * <blockquote>
     * <code>
     * {@link java.util.regex.Pattern}.{@link
     * java.util.regex.Pattern#compile compile}(<i>regex</i>).{@link
     * java.util.regex.Pattern#matcher(java.lang.CharSequence) matcher}(<i>str</i>).{@link
     * java.util.regex.Matcher#replaceFirst replaceFirst}(<i>repl</i>)
     * </code>
     * </blockquote>
     *
     *<p>
     * Note that backslashes ({@code \}) and dollar signs ({@code $}) in the
     * replacement string may cause the results to be different than if it were
     * being treated as a literal replacement string; see
     * {@link java.util.regex.Matcher#replaceFirst}.
     * Use {@link java.util.regex.Matcher#quoteReplacement} to suppress the special
     * meaning of these characters, if desired.
     *
     * @param   regex
     *          the regular expression to which this string is to be matched
     * @param   replacement
     *          the string to be substituted for the first match
     *
     * @return  The resulting {@code String}
     *
     * @throws  PatternSyntaxException
     *          if the regular expression's syntax is invalid
     *
     * @see java.util.regex.Pattern
     *
     * @since 1.4
     * @spec JSR-51
     *
     * @diffblue.noSupport
     */
    public String replaceFirst(String regex, String replacement) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
        // return Pattern.compile(regex).matcher(this).replaceFirst(replacement);
    }

    /**
     * Replaces each substring of this string that matches the given <a
     * href="../util/regex/Pattern.html#sum">regular expression</a> with the
     * given replacement.
     *
     * <p> An invocation of this method of the form
     * <i>str</i>{@code .replaceAll(}<i>regex</i>{@code ,} <i>repl</i>{@code )}
     * yields exactly the same result as the expression
     *
     * <blockquote>
     * <code>
     * {@link java.util.regex.Pattern}.{@link
     * java.util.regex.Pattern#compile compile}(<i>regex</i>).{@link
     * java.util.regex.Pattern#matcher(java.lang.CharSequence) matcher}(<i>str</i>).{@link
     * java.util.regex.Matcher#replaceAll replaceAll}(<i>repl</i>)
     * </code>
     * </blockquote>
     *
     *<p>
     * Note that backslashes ({@code \}) and dollar signs ({@code $}) in the
     * replacement string may cause the results to be different than if it were
     * being treated as a literal replacement string; see
     * {@link java.util.regex.Matcher#replaceAll Matcher.replaceAll}.
     * Use {@link java.util.regex.Matcher#quoteReplacement} to suppress the special
     * meaning of these characters, if desired.
     *
     * @param   regex
     *          the regular expression to which this string is to be matched
     * @param   replacement
     *          the string to be substituted for each match
     *
     * @return  The resulting {@code String}
     *
     * @throws  PatternSyntaxException
     *          if the regular expression's syntax is invalid
     *
     * @see java.util.regex.Pattern
     *
     * @since 1.4
     * @spec JSR-51
     *
     * @diffblue.limitedSupport
     * We enforce the regex argument is a string literal without any special
     * characters used in regular expressions:
     * '[', ']','.', '\\', '?', '^', '$', '*', '+', '{', '}', '|', '(', ')',
     * hence PatternSyntaxException will never be thrown.
     */
    public String replaceAll(String regex, String replacement)
    {
        // return Pattern.compile(regex).matcher(this).replaceAll(replacement);
        // DIFFBLUE MODELS LIBRARY: we assume the expression is just a string literal
        CProver.assume(
            regex.indexOf('[') == -1 &&
            regex.indexOf(']') == -1 &&
            regex.indexOf('.') == -1 &&
            regex.indexOf('\\') == -1 &&
            regex.indexOf('?') == -1 &&
            regex.indexOf('^') == -1 &&
            regex.indexOf('$') == -1 &&
            regex.indexOf('*') == -1 &&
            regex.indexOf('+') == -1 &&
            regex.indexOf('{') == -1 &&
            regex.indexOf('}') == -1 &&
            regex.indexOf('|') == -1 &&
            regex.indexOf('(') == -1 &&
            regex.indexOf(')') == -1);
        return replace(regex, replacement);
    }

    /**
     * Replaces each substring of this string that matches the literal target
     * sequence with the specified literal replacement sequence. The
     * replacement proceeds from the beginning of the string to the end, for
     * example, replacing "aa" with "b" in the string "aaa" will result in
     * "ba" rather than "ab".
     *
     * @param  target The sequence of char values to be replaced
     * @param  replacement The replacement sequence of char values
     * @return  The resulting string
     * @since 1.5
     *
     * @diffblue.limitedSupport
     * Only works for arguments that are constant strings with only 1 character.
     * For instance, we can generate traces for s.replace("a", "b") but not
     * s.replace("a", "bc") or s.replace(arg, "b").
     * @diffblue.untested
     */
    public String replace(CharSequence target, CharSequence replacement) {
        // DIFFBLUE MODEL LIBRARY This is treated internally in CBMC
        return CProver.nondetWithNullForNotModelled();
        // return Pattern.compile(target.toString(), Pattern.LITERAL).matcher(
        //         this).replaceAll(Matcher.quoteReplacement(replacement.toString()));
    }

    /**
     * Splits this string around matches of the given
     * <a href="../util/regex/Pattern.html#sum">regular expression</a>.
     *
     * <p> The array returned by this method contains each substring of this
     * string that is terminated by another substring that matches the given
     * expression or is terminated by the end of the string.  The substrings in
     * the array are in the order in which they occur in this string.  If the
     * expression does not match any part of the input then the resulting array
     * has just one element, namely this string.
     *
     * <p> When there is a positive-width match at the beginning of this
     * string then an empty leading substring is included at the beginning
     * of the resulting array. A zero-width match at the beginning however
     * never produces such empty leading substring.
     *
     * <p> The {@code limit} parameter controls the number of times the
     * pattern is applied and therefore affects the length of the resulting
     * array.  If the limit <i>n</i> is greater than zero then the pattern
     * will be applied at most <i>n</i>&nbsp;-&nbsp;1 times, the array's
     * length will be no greater than <i>n</i>, and the array's last entry
     * will contain all input beyond the last matched delimiter.  If <i>n</i>
     * is non-positive then the pattern will be applied as many times as
     * possible and the array can have any length.  If <i>n</i> is zero then
     * the pattern will be applied as many times as possible, the array can
     * have any length, and trailing empty strings will be discarded.
     *
     * <p> The string {@code "boo:and:foo"}, for example, yields the
     * following results with these parameters:
     *
     * <blockquote><table cellpadding=1 cellspacing=0 summary="Split example showing regex, limit, and result">
     * <tr>
     *     <th>Regex</th>
     *     <th>Limit</th>
     *     <th>Result</th>
     * </tr>
     * <tr><td align=center>:</td>
     *     <td align=center>2</td>
     *     <td>{@code { "boo", "and:foo" }}</td></tr>
     * <tr><td align=center>:</td>
     *     <td align=center>5</td>
     *     <td>{@code { "boo", "and", "foo" }}</td></tr>
     * <tr><td align=center>:</td>
     *     <td align=center>-2</td>
     *     <td>{@code { "boo", "and", "foo" }}</td></tr>
     * <tr><td align=center>o</td>
     *     <td align=center>5</td>
     *     <td>{@code { "b", "", ":and:f", "", "" }}</td></tr>
     * <tr><td align=center>o</td>
     *     <td align=center>-2</td>
     *     <td>{@code { "b", "", ":and:f", "", "" }}</td></tr>
     * <tr><td align=center>o</td>
     *     <td align=center>0</td>
     *     <td>{@code { "b", "", ":and:f" }}</td></tr>
     * </table></blockquote>
     *
     * <p> An invocation of this method of the form
     * <i>str.</i>{@code split(}<i>regex</i>{@code ,}&nbsp;<i>n</i>{@code )}
     * yields the same result as the expression
     *
     * <blockquote>
     * <code>
     * {@link java.util.regex.Pattern}.{@link
     * java.util.regex.Pattern#compile compile}(<i>regex</i>).{@link
     * java.util.regex.Pattern#split(java.lang.CharSequence,int) split}(<i>str</i>,&nbsp;<i>n</i>)
     * </code>
     * </blockquote>
     *
     *
     * @param  regex
     *         the delimiting regular expression
     *
     * @param  limit
     *         the result threshold, as described above
     *
     * @return  the array of strings computed by splitting this string
     *          around matches of the given regular expression
     *
     * @throws  PatternSyntaxException
     *          if the regular expression's syntax is invalid
     *
     * @see java.util.regex.Pattern
     *
     * @since 1.4
     * @spec JSR-51
     *
     * @diffblue.limitedSupport
     * This forces the regex argument to contain at most one character.
     * The model assumes the regex is not a special regex character:
     * <code> \.[{()<>*+-=?^$| </code>.
     * So no trace can be generated for these characters.
     * In particular this prevents any trace for the PatternSyntaxException case
     * from being generated.
     *
     * The size of the computed array is limited by the unwind parameter.
     * No trace can be generated for which the resulting array would be greater
     * than the unwind value.
     */
    public String[] split(String regex, int limit) {
        // /* fastpath if the regex is a
        //  (1)one-char String and this character is not one of the
        //     RegEx's meta characters ".$|()[{^?*+\\", or
        //  (2)two-char String and the first char is the backslash and
        //     the second is not the ascii digit or ascii letter.
        //  */
        // char ch = 0;
        // if (((regex.value.length == 1 &&
        //      ".$|()[{^?*+\\".indexOf(ch = regex.charAt(0)) == -1) ||
        //      (regex.length() == 2 &&
        //       regex.charAt(0) == '\\' &&
        //       (((ch = regex.charAt(1))-'0')|('9'-ch)) < 0 &&
        //       ((ch-'a')|('z'-ch)) < 0 &&
        //       ((ch-'A')|('Z'-ch)) < 0)) &&
        //     (ch < Character.MIN_HIGH_SURROGATE ||
        //      ch > Character.MAX_LOW_SURROGATE))
        // {
        //     int off = 0;
        //     int next = 0;
        //     boolean limited = limit > 0;
        //     ArrayList<String> list = new ArrayList<>();
        //     while ((next = indexOf(ch, off)) != -1) {
        //         if (!limited || list.size() < limit - 1) {
        //             list.add(substring(off, next));
        //             off = next + 1;
        //         } else {    // last one
        //             //assert (list.size() == limit - 1);
        //             list.add(substring(off, value.length));
        //             off = value.length;
        //             break;
        //         }
        //     }
        //     // If no match was found, return this
        //     if (off == 0)
        //         return new String[]{this};
        //
        //     // Add remaining segment
        //     if (!limited || list.size() < limit)
        //         list.add(substring(off, value.length));
        //
        //     // Construct result
        //     int resultSize = list.size();
        //     if (limit == 0) {
        //         while (resultSize > 0 && list.get(resultSize - 1).length() == 0) {
        //             resultSize--;
        //         }
        //     }
        //     String[] result = new String[resultSize];
        //     return list.subList(0, resultSize).toArray(result);
        // }
        // return Pattern.compile(regex).split(this, limit);

        // DIFFBLUE MODEL LIBRARY
        if (limit == 0) {
            return split(regex);
        }

        int size = CProver.nondetInt();
        CProver.assume(size > 0);
        String[] result = new String[size];

        if (regex.length() == 0) {
            int tokenIndex = 0;
            while (tokenIndex < length() && tokenIndex != limit - 1) {
                result[tokenIndex] =
                        CProverString.substring(this, tokenIndex, tokenIndex + 1);
                tokenIndex++;
            }
            // extract the remainder of the string
            if (tokenIndex < length()) {
                result[tokenIndex] =
                        CProverString.substring(this, tokenIndex, length());
                tokenIndex++;
            } else {
                result[tokenIndex] = "";
                tokenIndex++;
            }
            // Ensure the size of the array corresponds to the number of tokens
            CProver.assume(tokenIndex == size);
            return result;
        }

        // We only handle empty or single character delimiters
        CProver.assume(regex.length() == 1);
        char delimiter = CProverString.charAt(regex, 0);

        // We don't handle special regex characters \.[{()<>*+-=?^$|
        CProver.assume(delimiter != '\\' && delimiter != '.' && delimiter != '['
                       && delimiter != '{' && delimiter != '('
                       && delimiter != ')' && delimiter != '<'
                       && delimiter != '>' && delimiter != '*'
                       && delimiter != '+' && delimiter != '-'
                       && delimiter != '=' && delimiter != '?'
                       && delimiter != '^' && delimiter != '$'
                       && delimiter != '|');

        int tokenStart = 0;
        int tokenIndex = 0;
        int tokenEnd = indexOf(delimiter, tokenStart);
        while (tokenIndex < size - 1) {
            // extract the token prior to the delimiter
            CProver.assume(tokenEnd != -1);
            result[tokenIndex] =
                    CProverString.substring(this, tokenStart, tokenEnd);
            tokenIndex++;
            tokenStart = tokenEnd + 1;
            tokenEnd = indexOf(delimiter, tokenStart);
        }

        // extract the remainder of the string
        result[tokenIndex] =
                CProverString.substring(this, tokenStart, length());
        tokenIndex++;
        CProver.assume(tokenIndex == limit || tokenEnd == -1);

        CProver.assume(limit <= 0 || size <= limit);
        return result;
    }

    /**
     * Splits this string around matches of the given <a
     * href="../util/regex/Pattern.html#sum">regular expression</a>.
     *
     * <p> This method works as if by invoking the two-argument {@link
     * #split(String, int) split} method with the given expression and a limit
     * argument of zero.  Trailing empty strings are therefore not included in
     * the resulting array.
     *
     * <p> The string {@code "boo:and:foo"}, for example, yields the following
     * results with these expressions:
     *
     * <blockquote><table cellpadding=1 cellspacing=0 summary="Split examples showing regex and result">
     * <tr>
     *  <th>Regex</th>
     *  <th>Result</th>
     * </tr>
     * <tr><td align=center>:</td>
     *     <td>{@code { "boo", "and", "foo" }}</td></tr>
     * <tr><td align=center>o</td>
     *     <td>{@code { "b", "", ":and:f" }}</td></tr>
     * </table></blockquote>
     *
     *
     * @param  regex
     *         the delimiting regular expression
     *
     * @return  the array of strings computed by splitting this string
     *          around matches of the given regular expression
     *
     * @throws  PatternSyntaxException
     *          if the regular expression's syntax is invalid
     *
     * @see java.util.regex.Pattern
     *
     * @since 1.4
     * @spec JSR-51
     *
     * @diffblue.limitedSupport
     * This forces the regex argument to contain at most one character.
     * The model assumes the regex is not a special regex character:
     * <code> \.[{()<>*+-=?^$| </code>.
     * So no trace can be generated for these characters.
     * In particular this prevents any trace for the PatternSyntaxException case
     * from being generated.
     *
     * The size of the computed array is limited by the unwind parameter.
     * No trace can be generated for which the resulting array would be greater
     * than the unwind value.
     */
    public String[] split(String regex) {
        // return split(regex, 0);

        // DIFFBLUE MODEL LIBRARY
        // The (String, int) version in our model calls the String version,
        // which the other way in the original implementation.
        // This way, if the String version is called in the code we analyse,
        // only the code for this one is loaded.
        // The 0 case is a bit special compared to the others in that it disregard trailing empty strings.
        int size = CProver.nondetInt();
        CProver.assume(size > 0);
        String[] result = new String[size];

        if (regex.length() == 0) {
            int tokenIndex = 0;
            if (length() == 0) {
                CProver.assume(size == 1);
                result[tokenIndex] = "";
                return result;
            }
            while (tokenIndex < length()) {
                result[tokenIndex] =
                        CProverString.substring(this, tokenIndex, tokenIndex + 1);
                tokenIndex++;
            }
            // Ensure the size of the array corresponds to the number of tokens
            CProver.assume(tokenIndex == size);
            return result;
        }

        // We only handle empty or single character delimiters
        CProver.assume(regex.length() == 1);
        char delimiter = CProverString.charAt(regex, 0);

        // We don't handle special regex characters \.[{()<>*+-=?^$|
        CProver.assume(delimiter != '\\' && delimiter != '.' && delimiter != '['
                && delimiter != '{' && delimiter != '('
                && delimiter != ')' && delimiter != '<'
                && delimiter != '>' && delimiter != '*'
                && delimiter != '+' && delimiter != '-'
                && delimiter != '=' && delimiter != '?'
                && delimiter != '^' && delimiter != '$'
                && delimiter != '|');

        int tokenIndex = 0;
        int tokenStart = 0;
        int tokenEnd = indexOf(delimiter, tokenStart);
        while (tokenIndex < size - 1) {
            CProver.assume(tokenEnd != -1);
            result[tokenIndex++] =
                    CProverString.substring(this, tokenStart, tokenEnd);
            tokenStart = tokenEnd + 1;
            tokenEnd = indexOf(delimiter, tokenStart);
        }
        // extract the remainder of the string
        if (tokenEnd == -1) {
            // Exclude trailing empty strings
            CProver.assume(tokenStart != length());

            result[tokenIndex] =
                CProverString.substring(this, tokenStart, length());
        } else {
            // Exclude trailing empty strings
            CProver.assume(tokenStart != tokenEnd);

            result[tokenIndex] =
                CProverString.substring(this, tokenStart, tokenEnd);

            // ignore trailing empty strings
            for (int i = tokenEnd + 1; i < length(); i++) {
                CProver.assume(charAt(i) == delimiter);
            }
        }
        return result;
    }

    /**
     * Returns a new String composed of copies of the
     * {@code CharSequence elements} joined together with a copy of
     * the specified {@code delimiter}.
     *
     * <blockquote>For example,
     * <pre>{@code
     *     String message = String.join("-", "Java", "is", "cool");
     *     // message returned is: "Java-is-cool"
     * }</pre></blockquote>
     *
     * Note that if an element is null, then {@code "null"} is added.
     *
     * @param  delimiter the delimiter that separates each element
     * @param  elements the elements to join together.
     *
     * @return a new {@code String} that is composed of the {@code elements}
     *         separated by the {@code delimiter}
     *
     * @throws NullPointerException If {@code delimiter} or {@code elements}
     *         is {@code null}
     *
     * @see java.util.StringJoiner
     * @since 1.8
     *
     * @diffblue.limitedSupport
     * The model assumes the delimiter and elements objects are not null
     * instead of throwing an exception when they are.
     * The number of elements will be limited by the unwind parameter.
     */
    public static String join(CharSequence delimiter, CharSequence... elements) {
        CProver.assume(delimiter != null);
        CProver.assume(elements != null);

        StringBuilder builder = new StringBuilder();
        if (elements.length > 0)
            builder.append(elements[0]);
        for (int i = 1; i < elements.length; i++) {
            builder.append(delimiter);
            builder.append(elements[i]);
        }
        return builder.toString();
        // Objects.requireNonNull(delimiter);
        // Objects.requireNonNull(elements);
        // // Number of elements not likely worth Arrays.stream overhead.
        // StringJoiner joiner = new StringJoiner(delimiter);
        // for (CharSequence cs: elements) {
        //     joiner.add(cs);
        // }
        // return joiner.toString();
    }

    /**
     * Returns a new {@code String} composed of copies of the
     * {@code CharSequence elements} joined together with a copy of the
     * specified {@code delimiter}.
     *
     * <blockquote>For example,
     * <pre>{@code
     *     List<String> strings = new LinkedList<>();
     *     strings.add("Java");strings.add("is");
     *     strings.add("cool");
     *     String message = String.join(" ", strings);
     *     //message returned is: "Java is cool"
     *
     *     Set<String> strings = new LinkedHashSet<>();
     *     strings.add("Java"); strings.add("is");
     *     strings.add("very"); strings.add("cool");
     *     String message = String.join("-", strings);
     *     //message returned is: "Java-is-very-cool"
     * }</pre></blockquote>
     *
     * Note that if an individual element is {@code null}, then {@code "null"} is added.
     *
     * @param  delimiter a sequence of characters that is used to separate each
     *         of the {@code elements} in the resulting {@code String}
     * @param  elements an {@code Iterable} that will have its {@code elements}
     *         joined together.
     *
     * @return a new {@code String} that is composed from the {@code elements}
     *         argument
     *
     * @throws NullPointerException If {@code delimiter} or {@code elements}
     *         is {@code null}
     *
     * @see    #join(CharSequence,CharSequence...)
     * @see    java.util.StringJoiner
     * @since 1.8
     *
     * @diffblue.limitedSupport
     * The model assumes the delimiter and elements objects are not null
     * instead of throwing an exception when they are.
     * The number of elements will be limited by the unwind parameter.
     */
    public static String join(CharSequence delimiter,
            Iterable<? extends CharSequence> elements) {
        CProver.assume(delimiter != null);
        CProver.assume(elements != null);
        Iterator<? extends CharSequence> iterator = elements.iterator();
        if (!iterator.hasNext())
            return "";

        StringBuilder builder = new StringBuilder();
        builder.append(iterator.next().toString());
        while (iterator.hasNext()) {
            builder.append(delimiter.toString());
            builder.append(iterator.next().toString());
        }
        return builder.toString();
        // Objects.requireNonNull(delimiter);
        // Objects.requireNonNull(elements);
        // StringJoiner joiner = new StringJoiner(delimiter);
        // for (CharSequence cs: elements) {
        //     joiner.add(cs);
        // }
        // return joiner.toString();
    }

    /**
     * Converts all of the characters in this {@code String} to lower
     * case using the rules of the given {@code Locale}.  Case mapping is based
     * on the Unicode Standard version specified by the {@link java.lang.Character Character}
     * class. Since case mappings are not always 1:1 char mappings, the resulting
     * {@code String} may be a different length than the original {@code String}.
     * <p>
     * Examples of lowercase  mappings are in the following table:
     * <table border="1" summary="Lowercase mapping examples showing language code of locale, upper case, lower case, and description">
     * <tr>
     *   <th>Language Code of Locale</th>
     *   <th>Upper Case</th>
     *   <th>Lower Case</th>
     *   <th>Description</th>
     * </tr>
     * <tr>
     *   <td>tr (Turkish)</td>
     *   <td>&#92;u0130</td>
     *   <td>&#92;u0069</td>
     *   <td>capital letter I with dot above -&gt; small letter i</td>
     * </tr>
     * <tr>
     *   <td>tr (Turkish)</td>
     *   <td>&#92;u0049</td>
     *   <td>&#92;u0131</td>
     *   <td>capital letter I -&gt; small letter dotless i </td>
     * </tr>
     * <tr>
     *   <td>(all)</td>
     *   <td>French Fries</td>
     *   <td>french fries</td>
     *   <td>lowercased all chars in String</td>
     * </tr>
     * <tr>
     *   <td>(all)</td>
     *   <td><img src="doc-files/capiota.gif" alt="capiota"><img src="doc-files/capchi.gif" alt="capchi">
     *       <img src="doc-files/captheta.gif" alt="captheta"><img src="doc-files/capupsil.gif" alt="capupsil">
     *       <img src="doc-files/capsigma.gif" alt="capsigma"></td>
     *   <td><img src="doc-files/iota.gif" alt="iota"><img src="doc-files/chi.gif" alt="chi">
     *       <img src="doc-files/theta.gif" alt="theta"><img src="doc-files/upsilon.gif" alt="upsilon">
     *       <img src="doc-files/sigma1.gif" alt="sigma"></td>
     *   <td>lowercased all chars in String</td>
     * </tr>
     * </table>
     *
     * @param locale use the case transformation rules for this locale
     * @return the {@code String}, converted to lowercase.
     * @see     java.lang.String#toLowerCase()
     * @see     java.lang.String#toUpperCase()
     * @see     java.lang.String#toUpperCase(Locale)
     * @since   1.1
     *
     * @diffblue.limitedSupport
     * This is only correct for ASCII characters.
     * This ignores the locale, making it incorrect for Turkish, Azeri and Lithuanian
     * @diffblue.untested
     */
    public String toLowerCase(Locale locale) {
        // DIFFBLUE MODEL LIBRARY We just ignore the argument as it does not
        // make a difference in most cases
        return toLowerCase();
        // if (locale == null) {
        //     throw new NullPointerException();
        // }
        //
        // int firstUpper;
        // final int len = value.length;
        //
        // /* Now check if there are any characters that need to be changed. */
        // scan: {
        //     for (firstUpper = 0 ; firstUpper < len; ) {
        //         char c = value[firstUpper];
        //         if ((c >= Character.MIN_HIGH_SURROGATE)
        //                 && (c <= Character.MAX_HIGH_SURROGATE)) {
        //             int supplChar = codePointAt(firstUpper);
        //             if (supplChar != Character.toLowerCase(supplChar)) {
        //                 break scan;
        //             }
        //             firstUpper += Character.charCount(supplChar);
        //         } else {
        //             if (c != Character.toLowerCase(c)) {
        //                 break scan;
        //             }
        //             firstUpper++;
        //         }
        //     }
        //     return this;
        // }
        //
        // char[] result = new char[len];
        // int resultOffset = 0;  /* result may grow, so i+resultOffset
        //                         * is the write location in result */
        //
        // /* Just copy the first few lowerCase characters. */
        // System.arraycopy(value, 0, result, 0, firstUpper);
        //
        // String lang = locale.getLanguage();
        // boolean localeDependent =
        //         (lang == "tr" || lang == "az" || lang == "lt");
        // char[] lowerCharArray;
        // int lowerChar;
        // int srcChar;
        // int srcCount;
        // for (int i = firstUpper; i < len; i += srcCount) {
        //     srcChar = (int)value[i];
        //     if ((char)srcChar >= Character.MIN_HIGH_SURROGATE
        //             && (char)srcChar <= Character.MAX_HIGH_SURROGATE) {
        //         srcChar = codePointAt(i);
        //         srcCount = Character.charCount(srcChar);
        //     } else {
        //         srcCount = 1;
        //     }
        //     if (localeDependent || srcChar == '\u03A3') { // GREEK CAPITAL LETTER SIGMA
        //         lowerChar = ConditionalSpecialCasing.toLowerCaseEx(this, i, locale);
        //     } else {
        //         lowerChar = Character.toLowerCase(srcChar);
        //     }
        //     if ((lowerChar == Character.ERROR)
        //             || (lowerChar >= Character.MIN_SUPPLEMENTARY_CODE_POINT)) {
        //         if (lowerChar == Character.ERROR) {
        //             lowerCharArray =
        //                     ConditionalSpecialCasing.toLowerCaseCharArray(this, i, locale);
        //         } else if (srcCount == 2) {
        //             resultOffset += Character.toChars(lowerChar, result, i + resultOffset) - srcCount;
        //             continue;
        //         } else {
        //             lowerCharArray = Character.toChars(lowerChar);
        //         }
        //
        //         /* Grow result if needed */
        //         int mapLen = lowerCharArray.length;
        //         if (mapLen > srcCount) {
        //             char[] result2 = new char[result.length + mapLen - srcCount];
        //             System.arraycopy(result, 0, result2, 0, i + resultOffset);
        //             result = result2;
        //         }
        //         for (int x = 0; x < mapLen; ++x) {
        //             result[i + resultOffset + x] = lowerCharArray[x];
        //         }
        //         resultOffset += (mapLen - srcCount);
        //     } else {
        //         result[i + resultOffset] = (char)lowerChar;
        //     }
        // }
        // return new String(result, 0, len + resultOffset);
    }

    /**
     * Converts all of the characters in this {@code String} to lower
     * case using the rules of the default locale. This is equivalent to calling
     * {@code toLowerCase(Locale.getDefault())}.
     * <p>
     * <b>Note:</b> This method is locale sensitive, and may produce unexpected
     * results if used for strings that are intended to be interpreted locale
     * independently.
     * Examples are programming language identifiers, protocol keys, and HTML
     * tags.
     * For instance, {@code "TITLE".toLowerCase()} in a Turkish locale
     * returns {@code "t\u005Cu0131tle"}, where '\u005Cu0131' is the
     * LATIN SMALL LETTER DOTLESS I character.
     * To obtain correct results for locale insensitive strings, use
     * {@code toLowerCase(Locale.ROOT)}.
     * <p>
     * @return  the {@code String}, converted to lowercase.
     * @see     java.lang.String#toLowerCase(Locale)
     *
     * @diffblue.limitedSupport
     * This is only correct for ASCII characters.
     * This ignores the locale, making it incorrect for Turkish, Azeri and Lithuanian
     * @diffblue.untested
     */
    public String toLowerCase() {
        // DIFFBLUE MODEL LIBRARY This is treated internally in CBMC
        return CProver.nondetWithNullForNotModelled();
        // return toLowerCase(Locale.getDefault());
    }

    /**
     * Converts all of the characters in this {@code String} to upper
     * case using the rules of the given {@code Locale}. Case mapping is based
     * on the Unicode Standard version specified by the {@link java.lang.Character Character}
     * class. Since case mappings are not always 1:1 char mappings, the resulting
     * {@code String} may be a different length than the original {@code String}.
     * <p>
     * Examples of locale-sensitive and 1:M case mappings are in the following table.
     *
     * <table border="1" summary="Examples of locale-sensitive and 1:M case mappings. Shows Language code of locale, lower case, upper case, and description.">
     * <tr>
     *   <th>Language Code of Locale</th>
     *   <th>Lower Case</th>
     *   <th>Upper Case</th>
     *   <th>Description</th>
     * </tr>
     * <tr>
     *   <td>tr (Turkish)</td>
     *   <td>&#92;u0069</td>
     *   <td>&#92;u0130</td>
     *   <td>small letter i -&gt; capital letter I with dot above</td>
     * </tr>
     * <tr>
     *   <td>tr (Turkish)</td>
     *   <td>&#92;u0131</td>
     *   <td>&#92;u0049</td>
     *   <td>small letter dotless i -&gt; capital letter I</td>
     * </tr>
     * <tr>
     *   <td>(all)</td>
     *   <td>&#92;u00df</td>
     *   <td>&#92;u0053 &#92;u0053</td>
     *   <td>small letter sharp s -&gt; two letters: SS</td>
     * </tr>
     * <tr>
     *   <td>(all)</td>
     *   <td>Fahrvergn&uuml;gen</td>
     *   <td>FAHRVERGN&Uuml;GEN</td>
     *   <td></td>
     * </tr>
     * </table>
     * @param locale use the case transformation rules for this locale
     * @return the {@code String}, converted to uppercase.
     * @see     java.lang.String#toUpperCase()
     * @see     java.lang.String#toLowerCase()
     * @see     java.lang.String#toLowerCase(Locale)
     * @since   1.1
     *
     * @diffblue.limitedSupport
     * This is only correct for ASCII characters.
     * This ignores the locale, making it incorrect for Turkish, Azeri and Lithuanian.
     * @diffblue.untested
     */
    public String toUpperCase(Locale locale) {
        return toUpperCase();
        // if (locale == null) {
        //     throw new NullPointerException();
        // }
        //
        // int firstLower;
        // final int len = value.length;
        //
        // /* Now check if there are any characters that need to be changed. */
        // scan: {
        //     for (firstLower = 0 ; firstLower < len; ) {
        //         int c = (int)value[firstLower];
        //         int srcCount;
        //         if ((c >= Character.MIN_HIGH_SURROGATE)
        //                 && (c <= Character.MAX_HIGH_SURROGATE)) {
        //             c = codePointAt(firstLower);
        //             srcCount = Character.charCount(c);
        //         } else {
        //             srcCount = 1;
        //         }
        //         int upperCaseChar = Character.toUpperCaseEx(c);
        //         if ((upperCaseChar == Character.ERROR)
        //                 || (c != upperCaseChar)) {
        //             break scan;
        //         }
        //         firstLower += srcCount;
        //     }
        //     return this;
        // }
        //
        // /* result may grow, so i+resultOffset is the write location in result */
        // int resultOffset = 0;
        // char[] result = new char[len]; /* may grow */
        //
        // /* Just copy the first few upperCase characters. */
        // System.arraycopy(value, 0, result, 0, firstLower);
        //
        // String lang = locale.getLanguage();
        // boolean localeDependent =
        //         (lang == "tr" || lang == "az" || lang == "lt");
        // char[] upperCharArray;
        // int upperChar;
        // int srcChar;
        // int srcCount;
        // for (int i = firstLower; i < len; i += srcCount) {
        //     srcChar = (int)value[i];
        //     if ((char)srcChar >= Character.MIN_HIGH_SURROGATE &&
        //         (char)srcChar <= Character.MAX_HIGH_SURROGATE) {
        //         srcChar = codePointAt(i);
        //         srcCount = Character.charCount(srcChar);
        //     } else {
        //         srcCount = 1;
        //     }
        //     if (localeDependent) {
        //         upperChar = ConditionalSpecialCasing.toUpperCaseEx(this, i, locale);
        //     } else {
        //         upperChar = Character.toUpperCaseEx(srcChar);
        //     }
        //     if ((upperChar == Character.ERROR)
        //             || (upperChar >= Character.MIN_SUPPLEMENTARY_CODE_POINT)) {
        //         if (upperChar == Character.ERROR) {
        //             if (localeDependent) {
        //                 upperCharArray =
        //                         ConditionalSpecialCasing.toUpperCaseCharArray(this, i, locale);
        //             } else {
        //                 upperCharArray = Character.toUpperCaseCharArray(srcChar);
        //             }
        //         } else if (srcCount == 2) {
        //             resultOffset += Character.toChars(upperChar, result, i + resultOffset) - srcCount;
        //             continue;
        //         } else {
        //             upperCharArray = Character.toChars(upperChar);
        //         }
        //
        //         /* Grow result if needed */
        //         int mapLen = upperCharArray.length;
        //         if (mapLen > srcCount) {
        //             char[] result2 = new char[result.length + mapLen - srcCount];
        //             System.arraycopy(result, 0, result2, 0, i + resultOffset);
        //             result = result2;
        //         }
        //         for (int x = 0; x < mapLen; ++x) {
        //             result[i + resultOffset + x] = upperCharArray[x];
        //         }
        //         resultOffset += (mapLen - srcCount);
        //     } else {
        //         result[i + resultOffset] = (char)upperChar;
        //     }
        // }
        // return new String(result, 0, len + resultOffset);
    }

    /**
     * Converts all of the characters in this {@code String} to upper
     * case using the rules of the default locale. This method is equivalent to
     * {@code toUpperCase(Locale.getDefault())}.
     * <p>
     * <b>Note:</b> This method is locale sensitive, and may produce unexpected
     * results if used for strings that are intended to be interpreted locale
     * independently.
     * Examples are programming language identifiers, protocol keys, and HTML
     * tags.
     * For instance, {@code "title".toUpperCase()} in a Turkish locale
     * returns {@code "T\u005Cu0130TLE"}, where '\u005Cu0130' is the
     * LATIN CAPITAL LETTER I WITH DOT ABOVE character.
     * To obtain correct results for locale insensitive strings, use
     * {@code toUpperCase(Locale.ROOT)}.
     * <p>
     * @return  the {@code String}, converted to uppercase.
     * @see     java.lang.String#toUpperCase(Locale)
     *
     * @diffblue.limitedSupport
     * This is only correct for ASCII characters.
     * This ignores the locale, making it incorrect for Turkish, Azeri and Lithuanian
     * @diffblue.untested
     */
    public String toUpperCase() {
        // DIFFBLUE MODEL LIBRARY This is treated internally in CBMC
        return CProver.nondetWithNullForNotModelled();
        // return toUpperCase(Locale.getDefault());
    }

    /**
     * Returns a string whose value is this string, with any leading and trailing
     * whitespace removed.
     * <p>
     * If this {@code String} object represents an empty character
     * sequence, or the first and last characters of character sequence
     * represented by this {@code String} object both have codes
     * greater than {@code '\u005Cu0020'} (the space character), then a
     * reference to this {@code String} object is returned.
     * <p>
     * Otherwise, if there is no character with a code greater than
     * {@code '\u005Cu0020'} in the string, then a
     * {@code String} object representing an empty string is
     * returned.
     * <p>
     * Otherwise, let <i>k</i> be the index of the first character in the
     * string whose code is greater than {@code '\u005Cu0020'}, and let
     * <i>m</i> be the index of the last character in the string whose code
     * is greater than {@code '\u005Cu0020'}. A {@code String}
     * object is returned, representing the substring of this string that
     * begins with the character at index <i>k</i> and ends with the
     * character at index <i>m</i>-that is, the result of
     * {@code this.substring(k, m + 1)}.
     * <p>
     * This method may be used to trim whitespace (as defined above) from
     * the beginning and end of a string.
     *
     * @return  A string whose value is this string, with any leading and trailing white
     *          space removed, or this string if it has no leading or
     *          trailing white space.
     *
     * @diffblue.fullSupport
     */
    public String trim() {
        // DIFFBLUE MODEL LIBRARY This is treated internally in CBMC
        return CProver.nondetWithNullForNotModelled();
        // int len = value.length;
        // int st = 0;
        // char[] val = value;    /* avoid getfield opcode */
        //
        // while ((st < len) && (val[st] <= ' ')) {
        //     st++;
        // }
        // while ((st < len) && (val[len - 1] <= ' ')) {
        //     len--;
        // }
        // return ((st > 0) || (len < value.length)) ? substring(st, len) : this;
    }

    /**
     * This object (which is already a string!) is itself returned.
     *
     * @return  the string itself.
     *
     * @diffblue.limitedSupport
     * This returns a copy of the string rather than the string itself.
     * @diffblue.todo Correct this by restoring the original code and make CBMC
     * stop overwriting the method.
     */
    public String toString() {
        // DIFFBLUE MODEL LIBRARY This is treated internally in CBMC
        return CProver.nondetWithoutNullForNotModelled();
        // return this;
    }

    /**
     * Converts this string to a new character array.
     *
     * @return  a newly allocated character array whose length is the length
     *          of this string and whose contents are initialized to contain
     *          the character sequence represented by this string.
     *
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    public char[] toCharArray() {
        // DIFFBLUE MODEL LIBRARY Not using arraycopy as its support is limited
        // // Cannot use Arrays.copyOf because of class initialization order issues
        // char result[] = new char[value.length];
        // System.arraycopy(value, 0, result, 0, value.length);
        char[] result = new char[this.length()];
        for(int i = 0; i < this.length(); i++)
            result[i] = CProverString.charAt(this, i);
        return result;
    }

    /**
     * Helper function for the {@code format} function.
     * Serialize potential String.format argument to a String.
     * The returned String is never null.
     */
    static String cproverFormatArgument(Object obj) {
        if (obj == null) {
            return "null";
        }
        if (obj instanceof String) {
            return (String) obj;
        }

        // All primitive types are cast to a long
        long longValue = 0;
        if (obj instanceof Integer) {
            longValue = (Integer) obj;
        } else if (obj instanceof Long) {
            longValue = (Long) obj;
        } else if (obj instanceof Float) {
            longValue = (long) ((Float) obj).doubleValue();
        } else if (obj instanceof Double) {
            longValue = (long) ((Double) obj).doubleValue();
        } else if (obj instanceof Character) {
            char charValue = (Character) obj;
            longValue = (long) charValue;
        } else if (obj instanceof Byte) {
            byte byteValue = ((Byte) obj);
            longValue = (long) byteValue;
        } else if (obj instanceof Short) {
            short shortValue = ((Short) obj);
            longValue = (long) shortValue;
        } else if (obj instanceof Boolean) {
            longValue = ((Boolean) obj) ? 1 : 0;
        } else {
            CProver.assume(false);
        }

        // The long value is encoded using a string of 4 characters
        StringBuilder builder = new StringBuilder();
        builder.append((char) (longValue >> 48 & 0xFFFF));
        builder.append((char) (longValue >> 32 & 0xFFFF));
        builder.append((char) (longValue >> 16 & 0xFFFF));
        builder.append((char) (longValue & 0xFFFF));
        String str = builder.toString();

        // These redundant constraints are necessary in case the string solver
        // decides not to propagate the constraints for the concatenation above.
        // This happens, for instance, when the method under test calls
        // String.format on an integer and returns the length. In this case,
        // only the constraints for the length are propagated in the
        // dependency graph, but the length of the formatted string depends
        // on the value of the integer, whose conversion is not normally
        // propagated in the dependency graph.
        CProver.assume(str.charAt(0) == (char) (longValue >> 48 & 0xFFFF));
        CProver.assume(str.charAt(1) == (char) (longValue >> 32 & 0xFFFF));
        CProver.assume(str.charAt(2) == (char) (longValue >> 16 & 0xFFFF));
        CProver.assume(str.charAt(3) == (char) (longValue & 0xFFFF));

        return str;
    }

    /**
     * Returns a formatted string using the specified format string and
     * arguments.
     *
     * <p> The locale always used is the one returned by {@link
     * java.util.Locale#getDefault() Locale.getDefault()}.
     *
     * @param  format
     *         A <a href="../util/Formatter.html#syntax">format string</a>
     *
     * @param  args
     *         Arguments referenced by the format specifiers in the format
     *         string.  If there are more arguments than format specifiers, the
     *         extra arguments are ignored.  The number of arguments is
     *         variable and may be zero.  The maximum number of arguments is
     *         limited by the maximum dimension of a Java array as defined by
     *         <cite>The Java&trade; Virtual Machine Specification</cite>.
     *         The behaviour on a
     *         {@code null} argument depends on the <a
     *         href="../util/Formatter.html#syntax">conversion</a>.
     *
     * @throws  java.util.IllegalFormatException
     *          If a format string contains an illegal syntax, a format
     *          specifier that is incompatible with the given arguments,
     *          insufficient arguments given the format string, or other
     *          illegal conditions.  For specification of all possible
     *          formatting errors, see the <a
     *          href="../util/Formatter.html#detail">Details</a> section of the
     *          formatter class specification.
     *
     * @return  A formatted string
     *
     * @see  java.util.Formatter
     * @since  1.5
     *
     * @diffblue.limitedSupport
     * The limitation of the current implementation are:
     * <ul>
     * <li>
     *   'dateTime', 'hashcode' and 'octal' format specifiers are not
     *   implemented, which can lead to incorrect trace when they are used
     * </li>
     * <li>
     *   precision and width are ignored
     * </li>
     * <li>
     *   no trace will be generated if the format specifier is not a constant
     *   String with correct syntax and compatible with the arguments,
     *   in particular no trace can throw IllegalFormatException
     * </li>
     * <li>
     *   we arbitrary limit the number of arguments to 10 (increasing
     *   this number slows down the solver and when the number of
     *   arguments exceeds the limit the result will be incorrect)
     * </li>
     * <li>
     *   having 5 arguments or more makes the solver slow
     * </li>
     * </ul>
     * @diffblue.untested
     */
    public static String format(String format, Object... args) {
        // return new Formatter().format(format, args).toString();
        // DIFFBLUE MODEL LIBRARY
        CProver.assume(args.length <= 10);
        String arg0 = args.length > 0 ? cproverFormatArgument(args[0]) : "";
        String arg1 = args.length > 1 ? cproverFormatArgument(args[1]) : "";
        String arg2 = args.length > 2 ? cproverFormatArgument(args[2]) : "";
        String arg3 = args.length > 3 ? cproverFormatArgument(args[3]) : "";
        String arg4 = args.length > 4 ? cproverFormatArgument(args[4]) : "";
        String arg5 = args.length > 5 ? cproverFormatArgument(args[5]) : "";
        String arg6 = args.length > 6 ? cproverFormatArgument(args[6]) : "";
        String arg7 = args.length > 7 ? cproverFormatArgument(args[7]) : "";
        String arg8 = args.length > 8 ? cproverFormatArgument(args[8]) : "";
        String arg9 = args.length > 9 ? cproverFormatArgument(args[9]) : "";
        return CProverString.format(format, arg0, arg1, arg2, arg3, arg4, arg5, arg6,
                arg7, arg8, arg9);
    }


    /**
     * Returns a formatted string using the specified locale, format string,
     * and arguments.
     *
     * @param  l
     *         The {@linkplain java.util.Locale locale} to apply during
     *         formatting.  If {@code l} is {@code null} then no localization
     *         is applied.
     *
     * @param  format
     *         A <a href="../util/Formatter.html#syntax">format string</a>
     *
     * @param  args
     *         Arguments referenced by the format specifiers in the format
     *         string.  If there are more arguments than format specifiers, the
     *         extra arguments are ignored.  The number of arguments is
     *         variable and may be zero.  The maximum number of arguments is
     *         limited by the maximum dimension of a Java array as defined by
     *         <cite>The Java&trade; Virtual Machine Specification</cite>.
     *         The behaviour on a
     *         {@code null} argument depends on the
     *         <a href="../util/Formatter.html#syntax">conversion</a>.
     *
     * @throws  java.util.IllegalFormatException
     *          If a format string contains an illegal syntax, a format
     *          specifier that is incompatible with the given arguments,
     *          insufficient arguments given the format string, or other
     *          illegal conditions.  For specification of all possible
     *          formatting errors, see the <a
     *          href="../util/Formatter.html#detail">Details</a> section of the
     *          formatter class specification
     *
     * @return  A formatted string
     *
     * @see  java.util.Formatter
     * @since  1.5
     *
     * @diffblue.noSupport
     */
    public static String format(Locale l, String format, Object... args) {
        // return new Formatter(l).format(format, args).toString();
        return format(format, args);
    }

    /**
     * Returns the string representation of the {@code Object} argument.
     *
     * @param   obj   an {@code Object}.
     * @return  if the argument is {@code null}, then a string equal to
     *          {@code "null"}; otherwise, the value of
     *          {@code obj.toString()} is returned.
     * @see     java.lang.Object#toString()
     *
     * @diffblue.noSupport
     * @diffblue.todo add this by using the original implementation
     */
    public static String valueOf(Object obj) {
        // DIFFBLUE MODEL LIBRARY This is treated internally in CBMC
        // return (obj == null) ? "null" : obj.toString();
        return CProver.nondetWithoutNullForNotModelled();
    }

    /**
     * Returns the string representation of the {@code char} array
     * argument. The contents of the character array are copied; subsequent
     * modification of the character array does not affect the returned
     * string.
     *
     * @param   data     the character array.
     * @return  a {@code String} that contains the characters of the
     *          character array.
     *
     * @diffblue.noSupport
     * @diffblue.todo implement this using the String([C) constructor
     */
    public static String valueOf(char data[]) {
        CProver.notModelled();
        return CProver.nondetWithoutNullForNotModelled();
        // return new String(data);
    }

    /**
     * Returns the string representation of a specific subarray of the
     * {@code char} array argument.
     * <p>
     * The {@code offset} argument is the index of the first
     * character of the subarray. The {@code count} argument
     * specifies the length of the subarray. The contents of the subarray
     * are copied; subsequent modification of the character array does not
     * affect the returned string.
     *
     * @param   data     the character array.
     * @param   offset   initial offset of the subarray.
     * @param   count    length of the subarray.
     * @return  a {@code String} that contains the characters of the
     *          specified subarray of the character array.
     * @exception IndexOutOfBoundsException if {@code offset} is
     *          negative, or {@code count} is negative, or
     *          {@code offset+count} is larger than
     *          {@code data.length}.
     *
     * @diffblue.noSupport
     */
    public static String valueOf(char data[], int offset, int count) {
        CProver.notModelled();
        return CProver.nondetWithoutNullForNotModelled();
        // return new String(data, offset, count);
    }

    /**
     * Equivalent to {@link #valueOf(char[], int, int)}.
     *
     * @param   data     the character array.
     * @param   offset   initial offset of the subarray.
     * @param   count    length of the subarray.
     * @return  a {@code String} that contains the characters of the
     *          specified subarray of the character array.
     * @exception IndexOutOfBoundsException if {@code offset} is
     *          negative, or {@code count} is negative, or
     *          {@code offset+count} is larger than
     *          {@code data.length}.
     *
     * @diffblue.noSupport
     */
    public static String copyValueOf(char data[], int offset, int count) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
        // return new String(data, offset, count);
    }

    /**
     * Equivalent to {@link #valueOf(char[])}.
     *
     * @param   data   the character array.
     * @return  a {@code String} that contains the characters of the
     *          character array.
     *
     * @diffblue.noSupport
     */
    public static String copyValueOf(char data[]) {
        CProver.notModelled();
        return CProver.nondetWithoutNullForNotModelled();
        // return new String(data);
    }

    /**
     * Returns the string representation of the {@code boolean} argument.
     *
     * @param   b   a {@code boolean}.
     * @return  if the argument is {@code true}, a string equal to
     *          {@code "true"} is returned; otherwise, a string equal to
     *          {@code "false"} is returned.
     *
     * @diffblue.fullSupport
     */
    public static String valueOf(boolean b) {
        return b ? "true" : "false";
    }

    /**
     * Returns the string representation of the {@code char}
     * argument.
     *
     * @param   c   a {@code char}.
     * @return  a string of length {@code 1} containing
     *          as its single character the argument {@code c}.
     *
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    public static String valueOf(char c) {
        char data[] = {c};
        return CProverString.ofCharArray(data, 0, 1);
        // char data[] = {c};
        // return new String(data, true);
    }

    /**
     * Returns the string representation of the {@code int} argument.
     * <p>
     * The representation is exactly the one returned by the
     * {@code Integer.toString} method of one argument.
     *
     * @param   i   an {@code int}.
     * @return  a string representation of the {@code int} argument.
     * @see     java.lang.Integer#toString(int, int)
     *
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    public static String valueOf(int i) {
        return CProverString.toString(i);
        // return Integer.toString(i);
    }

    /**
     * Returns the string representation of the {@code long} argument.
     * <p>
     * The representation is exactly the one returned by the
     * {@code Long.toString} method of one argument.
     *
     * @param   l   a {@code long}.
     * @return  a string representation of the {@code long} argument.
     * @see     java.lang.Long#toString(long)
     *
     * @diffblue.fullSupport
     * @diffblue.untested
     */
    public static String valueOf(long l) {
        return CProverString.toString(l);
        // return Long.toString(l);
    }

    /**
     * Returns the string representation of the {@code float} argument.
     * <p>
     * The representation is exactly the one returned by the
     * {@code Float.toString} method of one argument.
     *
     * @param   f   a {@code float}.
     * @return  a string representation of the {@code float} argument.
     * @see     java.lang.Float#toString(float)
     *
     * @diffblue.limitedSupport
     * The precision in the produced string may not match that of the
     * actual program.
     */
    public static String valueOf(float f) {
        return CProverString.toString(f);
        // return Float.toString(f);
    }

    /**
     * Returns the string representation of the {@code double} argument.
     * <p>
     * The representation is exactly the one returned by the
     * {@code Double.toString} method of one argument.
     *
     * @param   d   a {@code double}.
     * @return  a  string representation of the {@code double} argument.
     * @see     java.lang.Double#toString(double)
     *
     * @diffblue.limitedSupport
     * The precision in the produced string may not match that of the
     * actual program.
     */
    public static String valueOf(double d) {
        // string solver only knows how to convert floats to string
        return CProverString.toString(d);
        // return Double.toString(d);
    }

    /**
     * Pool of strings used by the {@code String}.intern model.
     */
    static String[] cproverInternPool = null;

    /**
     * Number of elements stored in the pool for {@code String.intern}.
     * This can be smaller than {@code cproverInternPool.length} which
     * represents the capacity of the array and is fixed for each execution.
     */
    static int cproverInternPoolSize = 0;

    /**
     * Returns a canonical representation for the string object.
     * <p>
     * A pool of strings, initially empty, is maintained privately by the
     * class {@code String}.
     * <p>
     * When the intern method is invoked, if the pool already contains a
     * string equal to this {@code String} object as determined by
     * the {@link #equals(Object)} method, then the string from the pool is
     * returned. Otherwise, this {@code String} object is added to the
     * pool and a reference to this {@code String} object is returned.
     * <p>
     * It follows that for any two strings {@code s} and {@code t},
     * {@code s.intern() == t.intern()} is {@code true}
     * if and only if {@code s.equals(t)} is {@code true}.
     * <p>
     * All literal strings and string-valued constant expressions are
     * interned. String literals are defined in section 3.10.5 of the
     * <cite>The Java&trade; Language Specification</cite>.
     *
     * @return  a string that has the same contents as this string, but is
     *          guaranteed to be from a pool of unique strings.
     *
     * @diffblue.limitedSupport literal strings and string-valued constant
     * expressions are not interned.
     * @diffblue.untested
     */
    // DIFFBLUE MODEL LIBRARY
    // public native String intern();
    public String intern() {
        // Initialize the pool if needed
        if (cproverInternPool == null) {
            int capacity = CProver.nondetInt();
            CProver.assume(capacity > 0);
            cproverInternPool = new String[capacity];
            cproverInternPool[0] = this;
            return this;
        }
        // Look for an entry in the pool equal to `this`
        for (int i = 0; i < cproverInternPoolSize; ++i) {
            if (CProverString.equals(cproverInternPool[i], this)) {
                return cproverInternPool[i];
            }
        }
        // Add `this` to the pool
        CProver.assume(cproverInternPool.length > cproverInternPoolSize);
        cproverInternPool[cproverInternPoolSize++] = this;
        return this;
    }
}
