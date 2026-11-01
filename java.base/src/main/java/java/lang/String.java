/*
 * Copyright (c) 1994, 2024, Oracle and/or its affiliates. All rights reserved.
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

import java.io.ObjectStreamField;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Native;
import java.lang.invoke.MethodHandles;
import java.lang.constant.Constable;
import java.lang.constant.ConstantDesc;
import java.nio.charset.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import jdk.internal.util.ArraysSupport;
import jdk.internal.util.Preconditions;
import jdk.internal.vm.annotation.ForceInline;
import jdk.internal.vm.annotation.IntrinsicCandidate;
import jdk.internal.vm.annotation.Stable;

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
 *     String c = "abc".substring(2, 3);
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
 * other objects to strings. For additional information on string
 * concatenation and conversion, see <i>The Java Language Specification</i>.
 *
 * <p> Unless otherwise noted, passing a {@code null} argument to a constructor
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
 * <p>Unless otherwise noted, methods for comparing Strings do not take locale
 * into account.  The {@link java.text.Collator} class provides methods for
 * finer-grain, locale-sensitive String comparison.
 *
 * @implNote The implementation of the string concatenation operator is left to
 * the discretion of a Java compiler, as long as the compiler ultimately conforms
 * to <i>The Java Language Specification</i>. For example, the {@code javac} compiler
 * may implement the operator with {@code StringBuffer}, {@code StringBuilder},
 * or {@code java.lang.invoke.StringConcatFactory} depending on the JDK version. The
 * implementation of string conversion is typically through the method {@code toString},
 * defined by {@code Object} and inherited by all classes in Java.
 *
 * @author  Lee Boynton
 * @author  Arthur van Hoff
 * @author  Martin Buchholz
 * @author  Ulf Zibis
 * @see     java.lang.Object#toString()
 * @see     java.lang.StringBuffer
 * @see     java.lang.StringBuilder
 * @see     java.nio.charset.Charset
 * @since   1.0
 * @jls     15.18.1 String Concatenation Operator +
 */

public final class String
    implements java.io.Serializable, Comparable<String>, CharSequence,
               Constable, ConstantDesc {

    /**
     * The value is used for character storage.
     *
     * @implNote This field is trusted by the VM, and is a subject to
     * constant folding if String instance is constant. Overwriting this
     * field after construction will cause problems.
     *
     * Additionally, it is marked with {@link Stable} to trust the contents
     * of the array. No other facility in JDK provides this functionality (yet).
     * {@link Stable} is safe here, because value is never null.
     */
    @Stable
    private final byte[] value;

    /**
     * The identifier of the encoding used to encode the bytes in
     * {@code value}. The supported values in this implementation are
     *
     * LATIN1
     * UTF16
     *
     * @implNote This field is trusted by the VM, and is a subject to
     * constant folding if String instance is constant. Overwriting this
     * field after construction will cause problems.
     */
    private final byte coder;

    /** Cache the hash code for the string */
    private int hash; // Default to 0

    /**
     * Cache if the hash has been calculated as actually being zero, enabling
     * us to avoid recalculating this.
     */
    private boolean hashIsZero; // Default to false;

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    @java.io.Serial
    private static final long serialVersionUID = -6849794470754667710L;

    /**
     * If String compaction is disabled, the bytes in {@code value} are
     * always encoded in UTF16.
     *
     * For methods with several possible implementation paths, when String
     * compaction is disabled, only one code path is taken.
     *
     * The instance field value is generally opaque to optimizing JIT
     * compilers. Therefore, in performance-sensitive place, an explicit
     * check of the static boolean {@code COMPACT_STRINGS} is done first
     * before checking the {@code coder} field since the static boolean
     * {@code COMPACT_STRINGS} would be constant folded away by an
     * optimizing JIT compiler. The idioms for these cases are as follows.
     *
     * For code such as:
     *
     *    if (coder == LATIN1) { ... }
     *
     * can be written more optimally as
     *
     *    if (coder() == LATIN1) { ... }
     *
     * or:
     *
     *    if (COMPACT_STRINGS && coder == LATIN1) { ... }
     *
     * An optimizing JIT compiler can fold the above conditional as:
     *
     *    COMPACT_STRINGS == true  => if (coder == LATIN1) { ... }
     *    COMPACT_STRINGS == false => if (false)           { ... }
     *
     * @implNote
     * The actual value for this field is injected by JVM. The static
     * initialization block is used to set the value here to communicate
     * that this static final field is not statically foldable, and to
     * avoid any possible circular dependency during vm initialization.
     */
    static final boolean COMPACT_STRINGS;

    static {
        COMPACT_STRINGS = true;
    }

    /**
     * Class String is special cased within the Serialization Stream Protocol.
     *
     * A String instance is written into an ObjectOutputStream according to
     * <a href="{@docRoot}/../specs/serialization/protocol.html#stream-elements">
     * <cite>Java Object Serialization Specification</cite>, Section 6.2, "Stream Elements"</a>
     */
    @java.io.Serial
    private static final ObjectStreamField[] serialPersistentFields =
        new ObjectStreamField[0];

    /**
     * Initializes a newly created {@code String} object so that it represents
     * an empty character sequence.  Note that use of this constructor is
     * unnecessary since Strings are immutable.
     */
    public String() {
        // Handled by JBMC's string-refinement (exact signature match);
        // this body is never executed. The field assignments below are
        // only to satisfy Java's definite-assignment check for the
        // (final, otherwise unused) value/coder fields.
        CProver.notModelled();
        this.value = "".value;
        this.coder = "".coder;
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
     */
    public String(String original) {
        // Handled by JBMC's string-refinement (exact signature match);
        // this body is never executed. The field assignments below are
        // only to satisfy Java's definite-assignment check for the
        // (final, otherwise unused) value/coder fields.
        CProver.notModelled();
        this.value = "".value;
        this.coder = "".coder;
    }

    /**
     * Allocates a new {@code String} so that it represents the sequence of
     * characters currently contained in the character array argument. The
     * contents of the character array are copied; subsequent modification of
     * the character array does not affect the newly created string.
     *
     * @param  value
     *         The initial value of the string
     */
    public String(char[] value) {
        this(value, 0, value.length, null);
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
     *          If {@code offset} is negative, {@code count} is negative, or
     *          {@code offset} is greater than {@code value.length - count}
     */
    public String(char[] value, int offset, int count) {
        this(value, offset, count, rangeCheck(value, offset, count));
    }

    private static Void rangeCheck(char[] value, int offset, int count) {
        checkBoundsOffCount(offset, count, value.length);
        return null;
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
     *          If {@code offset} is negative, {@code count} is negative, or
     *          {@code offset} is greater than {@code codePoints.length - count}
     *
     * @since  1.5
     */
    /**
     * @cprover.limitedSupport Supplementary code points (requiring a
     *   surrogate pair) fall back to {@link CProver#notModelled}; BMP code
     *   points are handled exactly, composed via {@link
     *   CProverString#ofCharArray} and the {@code String(String)}
     *   constructor (intercepted).
     */
    public String(int[] codePoints, int offset, int count) {
        this(codePointsToChars(codePoints, offset, count));
    }

    private static String codePointsToChars(int[] codePoints, int offset, int count) {
        checkBoundsOffCount(offset, count, codePoints.length);
        char[] v = new char[count];
        for (int i = offset, j = 0; j < count; i++, j++) {
            int c = codePoints[i];
            if (Character.isBmpCodePoint(c)) {
                v[j] = (char) c;
            } else if (Character.isValidCodePoint(c)) {
                CProver.notModelled();
            } else {
                throw new IllegalArgumentException(Integer.toString(c));
            }
        }
        return CProverString.ofCharArray(v, 0, count);
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
     * specified in the {@link #String(byte[],int) String(byte[],int)} constructor.
     *
     * @deprecated This method does not properly convert bytes into characters.
     * As of JDK&nbsp;1.1, the preferred way to do this is via the
     * {@code String} constructors that take a {@link Charset}, charset name,
     * or that use the {@link Charset#defaultCharset() default charset}.
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
     *          If {@code offset} is negative, {@code count} is negative, or
     *          {@code offset} is greater than {@code ascii.length - count}
     *
     * @see  #String(byte[], int)
     * @see  #String(byte[], int, int, java.lang.String)
     * @see  #String(byte[], int, int, java.nio.charset.Charset)
     * @see  #String(byte[], int, int)
     * @see  #String(byte[], java.lang.String)
     * @see  #String(byte[], java.nio.charset.Charset)
     * @see  #String(byte[])
     */
    @Deprecated(since="1.1")
    /**
     * @cprover.fullSupport Composed via {@link CProverString#ofCharArray}
     *   and the {@code String(String)} constructor (intercepted).
     */
    public String(byte[] ascii, int hibyte, int offset, int count) {
        this(asciiHibyteToChars(ascii, hibyte, offset, count));
    }

    private static String asciiHibyteToChars(byte[] ascii, int hibyte, int offset, int count) {
        checkBoundsOffCount(offset, count, ascii.length);
        char[] v = new char[count];
        int hi = (hibyte & 0xff) << 8;
        for (int i = 0; i < count; i++) {
            v[i] = (char) (hi | (ascii[offset + i] & 0xff));
        }
        return CProverString.ofCharArray(v, 0, count);
    }

    /**
     * Allocates a new {@code String} containing characters constructed from
     * an array of 8-bit integer values. Each character <i>c</i> in the
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
     * {@code String} constructors that take a {@link Charset}, charset name,
     * or that use the {@link Charset#defaultCharset() default charset}.
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
     */
    @Deprecated(since="1.1")
    public String(byte[] ascii, int hibyte) {
        this(ascii, hibyte, 0, ascii.length);
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
     *
     * @param  charsetName
     *         The name of a supported {@linkplain java.nio.charset.Charset
     *         charset}
     *
     * @throws  UnsupportedEncodingException
     *          If the named charset is not supported
     *
     * @throws  IndexOutOfBoundsException
     *          If {@code offset} is negative, {@code length} is negative, or
     *          {@code offset} is greater than {@code bytes.length - length}
     *
     * @since  1.1
     */
    public String(byte[] bytes, int offset, int length, String charsetName)
            throws UnsupportedEncodingException {
        this(lookupCharset(charsetName), bytes, checkBoundsOffCount(offset, length, bytes.length), length);
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
     *          If {@code offset} is negative, {@code length} is negative, or
     *          {@code offset} is greater than {@code bytes.length - length}
     *
     * @since  1.6
     */
    public String(byte[] bytes, int offset, int length, Charset charset) {
        this(Objects.requireNonNull(charset), bytes, checkBoundsOffCount(offset, length, bytes.length), length);
    }

    /**
     * This method does not do any precondition checks on its arguments.
     * <p>
     * Important: parameter order of this method is deliberately changed in order to
     * disambiguate it against other similar methods of this class.
     */
    @SuppressWarnings("removal")
    /**
     * @cprover.noSupport Arbitrary charset decoding (variable-length byte
     *   sequences, malformed/unmappable-character handling) is not
     *   modelled.
     */
    private String(Charset charset, byte[] bytes, int offset, int length) {
        CProver.notModelled();
        this.value = "".value;
        this.coder = "".coder;
    }

    /*
     * Throws iae, instead of replacing, if malformed or unmappable.
     *
     * @param  noShare
     *         {@code true} if the resulting string MUST NOT share the byte array,
     *         {@code false} if the byte array can be exclusively used to construct
     *         the string and is not modified or used for any other purpose.
     */
    /**
     * @cprover.noSupport Arbitrary UTF-8 decoding is not modelled; see
     *   the {@code String(byte[], ..., Charset)} constructors.
     */
    static String newStringUTF8NoRepl(byte[] bytes, int offset, int length, boolean noShare) {
        checkBoundsOffCount(offset, length, bytes.length);
        CProver.notModelled();
        return CProver.nondetWithoutNullForNotModelled();
    }

    /**
     * @cprover.noSupport Arbitrary charset decoding is not modelled; see
     *   the {@code String(byte[], ..., Charset)} constructors.
     */
    static String newStringNoRepl(byte[] src, Charset cs) throws CharacterCodingException {
        CProver.notModelled();
        return CProver.nondetWithoutNullForNotModelled();
    }

    private static Charset lookupCharset(String csn) throws UnsupportedEncodingException {
        Objects.requireNonNull(csn);
        try {
            return Charset.forName(csn);
        } catch (UnsupportedCharsetException | IllegalCharsetNameException x) {
            throw new UnsupportedEncodingException(csn);
        }
    }

    /**
     * @cprover.noSupport Arbitrary UTF-8 encoding is not modelled; see
     *   {@link #getBytes(Charset)}.
     */
    static byte[] getBytesUTF8NoRepl(String s) {
        CProver.notModelled();
        return CProver.nondetWithoutNullForNotModelled();
    }

    /**
     * @cprover.noSupport Arbitrary charset encoding is not modelled; see
     *   {@link #getBytes(Charset)}.
     */
    static byte[] getBytesNoRepl(String s, Charset cs) throws CharacterCodingException {
        CProver.notModelled();
        return CProver.nondetWithoutNullForNotModelled();
    }

    //////////////////////////////// utf8 ////////////////////////////////////

    /**
     * Decodes ASCII from the source byte array into the destination
     * char array. Used via JavaLangAccess from UTF_8 and other charset
     * decoders.
     *
     * @return the number of bytes successfully decoded, at most len
     */
    /* package-private */
    static int decodeASCII(byte[] sa, int sp, char[] da, int dp, int len) {
        int count = StringCoding.countPositives(sa, sp, len);
        while (count < len) {
            if (sa[sp + count] < 0) {
                break;
            }
            count++;
        }
        StringLatin1.inflate(sa, sp, da, dp, count);
        return count;
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
     * @since  1.1
     */
    public String(byte[] bytes, String charsetName)
            throws UnsupportedEncodingException {
        this(lookupCharset(charsetName), bytes, 0, bytes.length);
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
     */
    public String(byte[] bytes, Charset charset) {
        this(Objects.requireNonNull(charset), bytes, 0, bytes.length);
    }

    /**
     * Constructs a new {@code String} by decoding the specified subarray of
     * bytes using the {@link Charset#defaultCharset() default charset}.
     * The length of the new {@code String} is a function of the charset,
     * and hence may not be equal to the length of the subarray.
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
     *          If {@code offset} is negative, {@code length} is negative, or
     *          {@code offset} is greater than {@code bytes.length - length}
     *
     * @since  1.1
     */
    public String(byte[] bytes, int offset, int length) {
        this(Charset.defaultCharset(), bytes, checkBoundsOffCount(offset, length, bytes.length), length);
    }

    /**
     * Constructs a new {@code String} by decoding the specified array of bytes
     * using the {@link Charset#defaultCharset() default charset}. The length
     * of the new {@code String} is a function of the charset, and hence may not
     * be equal to the length of the byte array.
     *
     * <p> The behavior of this constructor when the given bytes are not valid
     * in the default charset is unspecified.  The {@link
     * java.nio.charset.CharsetDecoder} class should be used when more control
     * over the decoding process is required.
     *
     * @param  bytes
     *         The bytes to be decoded into characters
     *
     * @since  1.1
     */
    public String(byte[] bytes) {
        this(Charset.defaultCharset(), bytes, 0, bytes.length);
    }

    /**
     * Allocates a new string that contains the sequence of characters
     * currently contained in the string buffer argument. The contents of the
     * string buffer are copied; subsequent modification of the string buffer
     * does not affect the newly created string.
     *
     * @param  buffer
     *         A {@code StringBuffer}
     */
    public String(StringBuffer buffer) {
        this(buffer.toString());
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
     */
    public String(StringBuilder builder) {
        this(builder, null);
    }

    /**
     * Returns the length of this string.
     * The length is equal to the number of <a href="Character.html#unicode">Unicode
     * code units</a> in the string.
     *
     * @return  the length of the sequence of characters represented by this
     *          object.
     */
    public int length() {
        // Handled by JBMC's string-refinement (exact signature match);
        // this body is never executed.
        CProver.notModelled();
        return CProver.nondetInt();
    }

    /**
     * Returns {@code true} if, and only if, {@link #length()} is {@code 0}.
     *
     * @return {@code true} if {@link #length()} is {@code 0}, otherwise
     * {@code false}
     *
     * @since 1.6
     */
    @Override
    public boolean isEmpty() {
        // Handled by JBMC's string-refinement (exact signature match);
        // this body is never executed.
        CProver.notModelled();
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
     * @throws     IndexOutOfBoundsException  if the {@code index}
     *             argument is negative or not less than the length of this
     *             string.
     */
    /**
     * @cprover.fullSupport No {@code String.charAt} table entry exists;
     *   composed from {@link CProverString#charAt(String, int)}.
     */
    public char charAt(int index) {
        if (index < 0 || index >= length()) {
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
     * @throws     IndexOutOfBoundsException  if the {@code index}
     *             argument is negative or not less than the length of this
     *             string.
     * @since      1.5
     */
    /**
     * @cprover.fullSupport Composed from {@link
     *   CProverString#codePointAt(String, int)}.
     */
    public int codePointAt(int index) {
        checkIndex(index, length());
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
     * @throws    IndexOutOfBoundsException if the {@code index}
     *            argument is less than 1 or greater than the length
     *            of this string.
     * @since     1.5
     */
    /**
     * @cprover.fullSupport Composed from {@link
     *   CProverString#codePointBefore(String, int)}.
     */
    public int codePointBefore(int index) {
        checkIndex(index - 1, length());
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
     * @throws    IndexOutOfBoundsException if the
     * {@code beginIndex} is negative, or {@code endIndex}
     * is larger than the length of this {@code String}, or
     * {@code beginIndex} is larger than {@code endIndex}.
     * @since  1.5
     */
    /**
     * @cprover.fullSupport Composed from {@link
     *   CProverString#codePointCount(String, int, int)}.
     */
    public int codePointCount(int beginIndex, int endIndex) {
        Objects.checkFromToIndex(beginIndex, endIndex, length());
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
     * @throws    IndexOutOfBoundsException if {@code index}
     *   is negative or larger than the length of this
     *   {@code String}, or if {@code codePointOffset} is positive
     *   and the substring starting with {@code index} has fewer
     *   than {@code codePointOffset} code points,
     *   or if {@code codePointOffset} is negative and the substring
     *   before {@code index} has fewer than the absolute value
     *   of {@code codePointOffset} code points.
     * @since 1.5
     */
    /**
     * @cprover.fullSupport Composed from {@link
     *   CProverString#offsetByCodePoints(String, int, int)}.
     */
    public int offsetByCodePoints(int index, int codePointOffset) {
        return CProverString.offsetByCodePoints(this, index, codePointOffset);
    }

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
     *     dstBegin + (srcEnd-srcBegin) - 1
     * </pre></blockquote>
     *
     * @param      srcBegin   index of the first character in the string
     *                        to copy.
     * @param      srcEnd     index after the last character in the string
     *                        to copy.
     * @param      dst        the destination array.
     * @param      dstBegin   the start offset in the destination array.
     * @throws    IndexOutOfBoundsException If any of the following
     *            is true:
     *            <ul><li>{@code srcBegin} is negative.
     *            <li>{@code srcBegin} is greater than {@code srcEnd}
     *            <li>{@code srcEnd} is greater than the length of this
     *                string
     *            <li>{@code dstBegin} is negative
     *            <li>{@code dstBegin+(srcEnd-srcBegin)} is larger than
     *                {@code dst.length}</ul>
     */
    /**
     * @cprover.fullSupport No primitive exists for this; composed from
     *   {@link #charAt} (which routes through {@link
     *   CProverString#charAt(String, int)}).
     */
    public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
        checkBoundsBeginEnd(srcBegin, srcEnd, length());
        checkBoundsOffCount(dstBegin, srcEnd - srcBegin, dst.length);
        for (int i = srcBegin; i < srcEnd; i++) {
            dst[dstBegin + i - srcBegin] = charAt(i);
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
     *     dstBegin + (srcEnd-srcBegin) - 1
     * </pre></blockquote>
     *
     * @deprecated  This method does not properly convert characters into
     * bytes.  As of JDK&nbsp;1.1, the preferred way to do this is via the
     * {@link #getBytes()} method, which uses the {@link Charset#defaultCharset()
     * default charset}.
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
     */
    /**
     * @cprover.fullSupport No primitive exists for this; composed from
     *   {@link #charAt} (which routes through {@link
     *   CProverString#charAt(String, int)}).
     */
    @Deprecated(since="1.1")
    public void getBytes(int srcBegin, int srcEnd, byte[] dst, int dstBegin) {
        checkBoundsBeginEnd(srcBegin, srcEnd, length());
        Objects.requireNonNull(dst);
        checkBoundsOffCount(dstBegin, srcEnd - srcBegin, dst.length);
        for (int i = srcBegin; i < srcEnd; i++) {
            dst[dstBegin + i - srcBegin] = (byte) charAt(i);
        }
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
     * @since  1.1
     */
    /**
     * @cprover.noSupport Arbitrary charset encoding is not modelled; see
     *   the {@code String(byte[], ..., Charset)} constructors.
     */
    public byte[] getBytes(String charsetName)
            throws UnsupportedEncodingException {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
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
     */
    /**
     * @cprover.noSupport See {@link #getBytes(String)}.
     */
    public byte[] getBytes(Charset charset) {
        if (charset == null) throw new NullPointerException();
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
     }

    /**
     * Encodes this {@code String} into a sequence of bytes using the
     * {@link Charset#defaultCharset() default charset}, storing the result
     * into a new byte array.
     *
     * <p> The behavior of this method when this string cannot be encoded in
     * the default charset is unspecified.  The {@link
     * java.nio.charset.CharsetEncoder} class should be used when more control
     * over the encoding process is required.
     *
     * @return  The resultant byte array
     *
     * @since      1.1
     */
    /**
     * @cprover.noSupport See {@link #getBytes(String)}.
     */
    public byte[] getBytes() {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    /**
     * Compares this string to the specified object.  The result is {@code
     * true} if and only if the argument is not {@code null} and is a {@code
     * String} object that represents the same sequence of characters as this
     * object.
     *
     * <p>For finer-grained String comparison, refer to
     * {@link java.text.Collator}.
     *
     * @param  anObject
     *         The object to compare this {@code String} against
     *
     * @return  {@code true} if the given object represents a {@code String}
     *          equivalent to this string, {@code false} otherwise
     *
     * @see  #compareTo(String)
     * @see  #equalsIgnoreCase(String)
     */
    /**
     * @cprover.fullSupport Composed from {@link
     *   CProverString#equals(String, String)}.
     */
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        return (anObject instanceof String aString)
                && CProverString.equals(aString, this);
    }

    /**
     * Compares this string to the specified {@code StringBuffer}.  The result
     * is {@code true} if and only if this {@code String} represents the same
     * sequence of characters as the specified {@code StringBuffer}. This method
     * synchronizes on the {@code StringBuffer}.
     *
     * <p>For finer-grained String comparison, refer to
     * {@link java.text.Collator}.
     *
     * @param  sb
     *         The {@code StringBuffer} to compare this {@code String} against
     *
     * @return  {@code true} if this {@code String} represents the same
     *          sequence of characters as the specified {@code StringBuffer},
     *          {@code false} otherwise
     *
     * @since  1.4
     */
    public boolean contentEquals(StringBuffer sb) {
        return contentEquals((CharSequence)sb);
    }


    /**
     * Compares this string to the specified {@code CharSequence}.  The
     * result is {@code true} if and only if this {@code String} represents the
     * same sequence of char values as the specified sequence. Note that if the
     * {@code CharSequence} is a {@code StringBuffer} then the method
     * synchronizes on it.
     *
     * <p>For finer-grained String comparison, refer to
     * {@link java.text.Collator}.
     *
     * @param  cs
     *         The sequence to compare this {@code String} against
     *
     * @return  {@code true} if this {@code String} represents the same
     *          sequence of char values as the specified sequence, {@code
     *          false} otherwise
     *
     * @since  1.5
     */
    /**
     * @cprover.fullSupport Composed from {@link #length} and {@link
     *   #charAt} (which routes through {@link CProverString#charAt(String,
     *   int)}) rather than the instanceof-dispatched special cases the
     *   real implementation uses, since those depend on {@code
     *   value}/{@code coder} directly.
     */
    public boolean contentEquals(CharSequence cs) {
        if (cs == null) {
            throw new NullPointerException();
        }
        int n = cs.length();
        if (n != length()) {
            return false;
        }
        for (int i = 0; i < n; i++) {
            if (charAt(i) != cs.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Compares this {@code String} to another {@code String}, ignoring case
     * considerations.  Two strings are considered equal ignoring case if they
     * are of the same length and corresponding Unicode code points in the two
     * strings are equal ignoring case.
     *
     * <p> Two Unicode code points are considered the same
     * ignoring case if at least one of the following is true:
     * <ul>
     *   <li> The two Unicode code points are the same (as compared by the
     *        {@code ==} operator)
     *   <li> Calling {@code Character.toLowerCase(Character.toUpperCase(int))}
     *        on each Unicode code point produces the same result
     * </ul>
     *
     * <p>Note that this method does <em>not</em> take locale into account, and
     * will result in unsatisfactory results for certain locales.  The
     * {@link java.text.Collator} class provides locale-sensitive comparison.
     *
     * @param  anotherString
     *         The {@code String} to compare this {@code String} against
     *
     * @return  {@code true} if the argument is not {@code null} and it
     *          represents an equivalent {@code String} ignoring case; {@code
     *          false} otherwise
     *
     * @see  #equals(Object)
     * @see  #codePoints()
     */
    public boolean equalsIgnoreCase(String anotherString) {
        return (this == anotherString) ? true
                : (anotherString != null)
                && (anotherString.length() == length())
                && regionMatches(true, 0, anotherString, 0, length());
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
     * determined by using the {@code <} operator, lexicographically precedes the
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
     * <p>For finer-grained String comparison, refer to
     * {@link java.text.Collator}.
     *
     * @param   anotherString   the {@code String} to be compared.
     * @return  the value {@code 0} if the argument string is equal to
     *          this string; a value less than {@code 0} if this string
     *          is lexicographically less than the string argument; and a
     *          value greater than {@code 0} if this string is
     *          lexicographically greater than the string argument.
     */
    public int compareTo(String anotherString) {
        // Handled by JBMC's string-refinement (exact signature match);
        // this body is never executed.
        CProver.notModelled();
        return CProver.nondetInt();
     }

    /**
     * A Comparator that orders {@code String} objects as by
     * {@link #compareToIgnoreCase(String) compareToIgnoreCase}.
     * This comparator is serializable.
     * <p>
     * Note that this Comparator does <em>not</em> take locale into account,
     * and will result in an unsatisfactory ordering for certain locales.
     * The {@link java.text.Collator} class provides locale-sensitive comparison.
     *
     * @see     java.text.Collator
     * @since   1.2
     */
    public static final Comparator<String> CASE_INSENSITIVE_ORDER
                                         = new CaseInsensitiveComparator();

    /**
     * CaseInsensitiveComparator for Strings.
     */
    private static class CaseInsensitiveComparator
            implements Comparator<String>, java.io.Serializable {
        // use serialVersionUID from JDK 1.2.2 for interoperability
        @java.io.Serial
        private static final long serialVersionUID = 8575799808933029326L;

        /**
         * @cprover.fullSupport Composed from {@link String#length} and
         *   {@link String#charAt} (which route through {@code
         *   CProverString}), mirroring the real algorithm, rather than
         *   the real implementation, which depends on {@code value}/
         *   {@code coder} directly.
         */
        public int compare(String s1, String s2) {
            int len1 = s1.length();
            int len2 = s2.length();
            int lim = Math.min(len1, len2);
            for (int i = 0; i < lim; i++) {
                char c1 = Character.toUpperCase(s1.charAt(i));
                char c2 = Character.toUpperCase(s2.charAt(i));
                if (c1 != c2) {
                    c1 = Character.toLowerCase(c1);
                    c2 = Character.toLowerCase(c2);
                    if (c1 != c2) {
                        return c1 - c2;
                    }
                }
            }
            return len1 - len2;
        }

        /** Replaces the de-serialized object. */
        @java.io.Serial
        private Object readResolve() { return CASE_INSENSITIVE_ORDER; }
    }

    /**
     * Compares two strings lexicographically, ignoring case
     * differences. This method returns an integer whose sign is that of
     * calling {@code compareTo} with case folded versions of the strings
     * where case differences have been eliminated by calling
     * {@code Character.toLowerCase(Character.toUpperCase(int))} on
     * each Unicode code point.
     * <p>
     * Note that this method does <em>not</em> take locale into account,
     * and will result in an unsatisfactory ordering for certain locales.
     * The {@link java.text.Collator} class provides locale-sensitive comparison.
     *
     * @param   str   the {@code String} to be compared.
     * @return  a negative integer, zero, or a positive integer as the
     *          specified String is greater than, equal to, or less
     *          than this String, ignoring case considerations.
     * @see     java.text.Collator
     * @see     #codePoints()
     * @since   1.2
     */
    public int compareToIgnoreCase(String str) {
        return CASE_INSENSITIVE_ORDER.compare(this, str);
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
     * <p>Note that this method does <em>not</em> take locale into account.  The
     * {@link java.text.Collator} class provides locale-sensitive comparison.
     *
     * @param   toffset   the starting offset of the subregion in this string.
     * @param   other     the string argument.
     * @param   ooffset   the starting offset of the subregion in the string
     *                    argument.
     * @param   len       the number of characters to compare.
     * @return  {@code true} if the specified subregion of this string
     *          exactly matches the specified subregion of the string argument;
     *          {@code false} otherwise.
     */
    /**
     * @cprover.fullSupport Composed from {@link #charAt} (which routes
     *   through {@link CProverString#charAt(String, int)}), mirroring the
     *   real algorithm, rather than the real implementation, which
     *   depends on {@code value}/{@code coder} directly.
     */
    public boolean regionMatches(int toffset, String other, int ooffset, int len) {
        // Note: toffset, ooffset, or len might be near -1>>>1.
        if ((ooffset < 0) || (toffset < 0) ||
             (toffset > (long)length() - len) ||
             (ooffset > (long)other.length() - len)) {
            return false;
        }
        while (len-- > 0) {
            if (charAt(toffset++) != other.charAt(ooffset++)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Tests if two string regions are equal.
     * <p>
     * A substring of this {@code String} object is compared to a substring
     * of the argument {@code other}. The result is {@code true} if these
     * substrings represent Unicode code point sequences that are the same,
     * ignoring case if and only if {@code ignoreCase} is true.
     * The sequences {@code tsequence} and {@code osequence} are compared,
     * where {@code tsequence} is the sequence produced as if by calling
     * {@code this.substring(toffset, toffset + len).codePoints()} and
     * {@code osequence} is the sequence produced as if by calling
     * {@code other.substring(ooffset, ooffset + len).codePoints()}.
     * The result is {@code true} if and only if all of the following
     * are true:
     * <ul><li>{@code toffset} is non-negative.
     * <li>{@code ooffset} is non-negative.
     * <li>{@code toffset+len} is less than or equal to the length of this
     * {@code String} object.
     * <li>{@code ooffset+len} is less than or equal to the length of the other
     * argument.
     * <li>if {@code ignoreCase} is {@code false}, all pairs of corresponding Unicode
     * code points are equal integer values; or if {@code ignoreCase} is {@code true},
     * {@link Character#toLowerCase(int) Character.toLowerCase(}
     * {@link Character#toUpperCase(int)}{@code )} on all pairs of Unicode code points
     * results in equal integer values.
     * </ul>
     *
     * <p>Note that this method does <em>not</em> take locale into account,
     * and will result in unsatisfactory results for certain locales when
     * {@code ignoreCase} is {@code true}.  The {@link java.text.Collator} class
     * provides locale-sensitive comparison.
     *
     * @param   ignoreCase   if {@code true}, ignore case when comparing
     *                       characters.
     * @param   toffset      the starting offset of the subregion in this
     *                       string.
     * @param   other        the string argument.
     * @param   ooffset      the starting offset of the subregion in the string
     *                       argument.
     * @param   len          the number of characters (Unicode code units -
     *                       16bit {@code char} value) to compare.
     * @return  {@code true} if the specified subregion of this string
     *          matches the specified subregion of the string argument;
     *          {@code false} otherwise. Whether the matching is exact
     *          or case insensitive depends on the {@code ignoreCase}
     *          argument.
     * @see     #codePoints()
     */
    /**
     * @cprover.fullSupport Composed from {@link #charAt} (which routes
     *   through {@link CProverString#charAt(String, int)}), mirroring the
     *   real algorithm, rather than the real implementation, which
     *   depends on {@code value}/{@code coder} directly.
     */
    public boolean regionMatches(boolean ignoreCase, int toffset,
            String other, int ooffset, int len) {
        if (!ignoreCase) {
            return regionMatches(toffset, other, ooffset, len);
        }
        // Note: toffset, ooffset, or len might be near -1>>>1.
        if ((ooffset < 0) || (toffset < 0)
                || (toffset > (long)length() - len)
                || (ooffset > (long)other.length() - len)) {
            return false;
        }
        while (len-- > 0) {
            char c1 = charAt(toffset++);
            char c2 = other.charAt(ooffset++);
            if (c1 == c2) {
                continue;
            }
            char u1 = Character.toUpperCase(c1);
            char u2 = Character.toUpperCase(c2);
            if (u1 == u2) {
                continue;
            }
            if (Character.toLowerCase(u1) == Character.toLowerCase(u2)) {
                continue;
            }
            return false;
        }
        return true;
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
     */
    public boolean startsWith(String prefix, int toffset) {
        // Handled by JBMC's string-refinement (exact signature match);
        // this body is never executed.
        CProver.notModelled();
        return CProver.nondetBoolean();
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
     * @since   1.0
     */
    public boolean startsWith(String prefix) {
        // Handled by JBMC's string-refinement (exact signature match);
        // this body is never executed.
        CProver.notModelled();
        return CProver.nondetBoolean();
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
     */
    public boolean endsWith(String suffix) {
        // Handled by JBMC's string-refinement (exact signature match);
        // this body is never executed.
        CProver.notModelled();
        return CProver.nondetBoolean();
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
     */
    /**
     * @cprover.fullSupport Computed directly via {@link #length} and
     *   {@link #charAt} (which route through {@code CProverString}),
     *   following the documented algorithm, rather than cached in the
     *   (disconnected, real) {@code hash}/{@code hashIsZero} fields.
     */
    public int hashCode() {
        int len = length();
        int h = 0;
        for (int i = 0; i < len; i++) {
            h = 31 * h + charAt(i);
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
     */
    public int indexOf(int ch) {
        // Handled by JBMC's string-refinement (exact signature match);
        // this body is never executed.
        CProver.notModelled();
        return CProver.nondetInt();
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
     * @apiNote
     * Unlike {@link #substring(int)}, for example, this method does not throw
     * an exception when {@code fromIndex} is outside the valid range.
     * Rather, it returns -1 when {@code fromIndex} is larger than the length of
     * the string.
     * This result is, by itself, indistinguishable from a genuine absence of
     * {@code ch} in the string.
     * If stricter behavior is needed, {@link #indexOf(int, int, int)}
     * should be considered instead.
     * On a {@link String} {@code s}, for example,
     * {@code s.indexOf(ch, fromIndex, s.length())} would throw if
     * {@code fromIndex} were larger than the string length, or were negative.
     */
    public int indexOf(int ch, int fromIndex) {
        // Handled by JBMC's string-refinement (exact signature match);
        // this body is never executed.
        CProver.notModelled();
        return CProver.nondetInt();
    }

    /**
     * Returns the index within this string of the first occurrence of the
     * specified character, starting the search at {@code beginIndex} and
     * stopping before {@code endIndex}.
     *
     * <p>If a character with value {@code ch} occurs in the
     * character sequence represented by this {@code String}
     * object at an index no smaller than {@code beginIndex} but smaller than
     * {@code endIndex}, then
     * the index of the first such occurrence is returned. For values
     * of {@code ch} in the range from 0 to 0xFFFF (inclusive),
     * this is the smallest value <i>k</i> such that:
     * <blockquote><pre>
     * (this.charAt(<i>k</i>) == ch) &amp;&amp; (beginIndex &lt;= <i>k</i> &lt; endIndex)
     * </pre></blockquote>
     * is true. For other values of {@code ch}, it is the
     * smallest value <i>k</i> such that:
     * <blockquote><pre>
     * (this.codePointAt(<i>k</i>) == ch) &amp;&amp; (beginIndex &lt;= <i>k</i> &lt; endIndex)
     * </pre></blockquote>
     * is true. In either case, if no such character occurs in this
     * string at or after position {@code beginIndex} and before position
     * {@code endIndex}, then {@code -1} is returned.
     *
     * <p>All indices are specified in {@code char} values
     * (Unicode code units).
     *
     * @param   ch          a character (Unicode code point).
     * @param   beginIndex  the index to start the search from (included).
     * @param   endIndex    the index to stop the search at (excluded).
     * @return  the index of the first occurrence of the character in the
     *          character sequence represented by this object that is greater
     *          than or equal to {@code beginIndex} and less than {@code endIndex},
     *          or {@code -1} if the character does not occur.
     * @throws  StringIndexOutOfBoundsException if {@code beginIndex}
     *          is negative, or {@code endIndex} is larger than the length of
     *          this {@code String} object, or {@code beginIndex} is larger than
     *          {@code endIndex}.
     * @since   21
     */
    /**
     * @cprover.fullSupport No primitive exists for this (unlike the
     *   no-range overloads); composed from {@link #charAt}/{@link
     *   #codePointAt} (which route through {@code CProverString}).
     */
    public int indexOf(int ch, int beginIndex, int endIndex) {
        checkBoundsBeginEnd(beginIndex, endIndex, length());
        if (ch >= Character.MIN_VALUE && ch <= Character.MAX_VALUE) {
            for (int i = beginIndex; i < endIndex; i++) {
                if (charAt(i) == ch) {
                    return i;
                }
            }
        } else {
            for (int i = beginIndex; i < endIndex; i++) {
                if (codePointAt(i) == ch) {
                    return i;
                }
            }
        }
        return -1;
    }

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
     */
    public int lastIndexOf(int ch) {
        // Handled by JBMC's string-refinement (exact signature match);
        // this body is never executed.
        CProver.notModelled();
        return CProver.nondetInt();
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
     */
    public int lastIndexOf(int ch, int fromIndex) {
        // Handled by JBMC's string-refinement (exact signature match);
        // this body is never executed.
        CProver.notModelled();
        return CProver.nondetInt();
    }

    /**
     * Returns the index within this string of the first occurrence of the
     * specified substring.
     *
     * <p>The returned index is the smallest value {@code k} for which:
     * <pre>{@code
     * this.startsWith(str, k)
     * }</pre>
     * If no such value of {@code k} exists, then {@code -1} is returned.
     *
     * @param   str   the substring to search for.
     * @return  the index of the first occurrence of the specified substring,
     *          or {@code -1} if there is no such occurrence.
     */
    public int indexOf(String str) {
        // Handled by JBMC's string-refinement (exact signature match);
        // this body is never executed.
        CProver.notModelled();
        return CProver.nondetInt();
    }

    /**
     * Returns the index within this string of the first occurrence of the
     * specified substring, starting at the specified index.
     *
     * <p>The returned index is the smallest value {@code k} for which:
     * <pre>{@code
     *     k >= Math.min(fromIndex, this.length()) &&
     *                   this.startsWith(str, k)
     * }</pre>
     * If no such value of {@code k} exists, then {@code -1} is returned.
     *
     * @apiNote
     * Unlike {@link #substring(int)}, for example, this method does not throw
     * an exception when {@code fromIndex} is outside the valid range.
     * Rather, it returns -1 when {@code fromIndex} is larger than the length of
     * the string.
     * This result is, by itself, indistinguishable from a genuine absence of
     * {@code str} in the string.
     * If stricter behavior is needed, {@link #indexOf(String, int, int)}
     * should be considered instead.
     * On {@link String} {@code s} and a non-empty {@code str}, for example,
     * {@code s.indexOf(str, fromIndex, s.length())} would throw if
     * {@code fromIndex} were larger than the string length, or were negative.
     *
     * @param   str         the substring to search for.
     * @param   fromIndex   the index from which to start the search.
     * @return  the index of the first occurrence of the specified substring,
     *          starting at the specified index,
     *          or {@code -1} if there is no such occurrence.
     */
    public int indexOf(String str, int fromIndex) {
        // Handled by JBMC's string-refinement (exact signature match);
        // this body is never executed.
        CProver.notModelled();
        return CProver.nondetInt();
    }

    /**
     * Returns the index of the first occurrence of the specified substring
     * within the specified index range of {@code this} string.
     *
     * <p>This method returns the same result as the one of the invocation
     * <pre>{@code
     *     s.substring(beginIndex, endIndex).indexOf(str) + beginIndex
     * }</pre>
     * if the index returned by {@link #indexOf(String)} is non-negative,
     * and returns -1 otherwise.
     * (No substring is instantiated, though.)
     *
     * @param   str         the substring to search for.
     * @param   beginIndex  the index to start the search from (included).
     * @param   endIndex    the index to stop the search at (excluded).
     * @return  the index of the first occurrence of the specified substring
     *          within the specified index range,
     *          or {@code -1} if there is no such occurrence.
     * @throws  StringIndexOutOfBoundsException if {@code beginIndex}
     *          is negative, or {@code endIndex} is larger than the length of
     *          this {@code String} object, or {@code beginIndex} is larger than
     *          {@code endIndex}.
     * @since   21
     */
    /**
     * @cprover.fullSupport No primitive exists for this (unlike the
     *   no-range overload); composed from {@link #regionMatches}.
     */
    public int indexOf(String str, int beginIndex, int endIndex) {
        checkBoundsBeginEnd(beginIndex, endIndex, length());
        int strLen = str.length();
        int max = endIndex - strLen;
        for (int i = Math.max(beginIndex, 0); i <= max; i++) {
            if (regionMatches(i, str, 0, strLen)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the index within this string of the last occurrence of the
     * specified substring.  The last occurrence of the empty string ""
     * is considered to occur at the index value {@code this.length()}.
     *
     * <p>The returned index is the largest value {@code k} for which:
     * <pre>{@code
     * this.startsWith(str, k)
     * }</pre>
     * If no such value of {@code k} exists, then {@code -1} is returned.
     *
     * @param   str   the substring to search for.
     * @return  the index of the last occurrence of the specified substring,
     *          or {@code -1} if there is no such occurrence.
     */
    public int lastIndexOf(String str) {
        // Handled by JBMC's string-refinement (exact signature match);
        // this body is never executed.
        CProver.notModelled();
        return CProver.nondetInt();
    }

    /**
     * Returns the index within this string of the last occurrence of the
     * specified substring, searching backward starting at the specified index.
     *
     * <p>The returned index is the largest value {@code k} for which:
     * <pre>{@code
     *     k <= Math.min(fromIndex, this.length()) &&
     *                   this.startsWith(str, k)
     * }</pre>
     * If no such value of {@code k} exists, then {@code -1} is returned.
     *
     * @param   str         the substring to search for.
     * @param   fromIndex   the index to start the search from.
     * @return  the index of the last occurrence of the specified substring,
     *          searching backward from the specified index,
     *          or {@code -1} if there is no such occurrence.
     */
    public int lastIndexOf(String str, int fromIndex) {
        // Handled by JBMC's string-refinement (exact signature match);
        // this body is never executed.
        CProver.notModelled();
        return CProver.nondetInt();
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
     * @throws     IndexOutOfBoundsException  if
     *             {@code beginIndex} is negative or larger than the
     *             length of this {@code String} object.
     */
    /**
     * @cprover.fullSupport Composed from {@link
     *   CProverString#substring(String, int)}.
     */
    public String substring(int beginIndex) {
        if (beginIndex < 0 || beginIndex > length()) {
            throw new StringIndexOutOfBoundsException(beginIndex);
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
     * @throws     IndexOutOfBoundsException  if the
     *             {@code beginIndex} is negative, or
     *             {@code endIndex} is larger than the length of
     *             this {@code String} object, or
     *             {@code beginIndex} is larger than
     *             {@code endIndex}.
     */
    /**
     * @cprover.fullSupport Composed from {@link
     *   CProverString#substring(String, int, int)}.
     */
    public String substring(int beginIndex, int endIndex) {
        checkBoundsBeginEnd(beginIndex, endIndex, length());
        return CProverString.substring(this, beginIndex, endIndex);
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
     */
    public String concat(String str) {
        // Handled by JBMC's string-refinement (exact signature match);
        // this body is never executed.
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
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
     */
    public String replace(char oldChar, char newChar) {
        // Handled by JBMC's string-refinement (exact signature match);
        // this body is never executed.
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
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
     */
    /**
     * @cprover.noSupport Regular-expression matching is deferred to a
     *   future Pattern/Matcher stage.
     */
    public boolean matches(String regex) {
        CProver.notModelled();
        return CProver.nondetBoolean();
    }

    /**
     * Returns true if and only if this string contains the specified
     * sequence of char values.
     *
     * @param s the sequence to search for
     * @return true if this string contains {@code s}, false otherwise
     * @since 1.5
     */
    public boolean contains(CharSequence s) {
        // Handled by JBMC's string-refinement (exact signature match);
        // this body is never executed.
        CProver.notModelled();
        return CProver.nondetBoolean();
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
     * java.util.regex.Pattern#compile(String) compile}(<i>regex</i>).{@link
     * java.util.regex.Pattern#matcher(java.lang.CharSequence) matcher}(<i>str</i>).{@link
     * java.util.regex.Matcher#replaceFirst(String) replaceFirst}(<i>repl</i>)
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
     */
    /**
     * @cprover.noSupport Regular-expression matching is deferred to a
     *   future Pattern/Matcher stage.
     */
    public String replaceFirst(String regex, String replacement) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
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
     * java.util.regex.Pattern#compile(String) compile}(<i>regex</i>).{@link
     * java.util.regex.Pattern#matcher(java.lang.CharSequence) matcher}(<i>str</i>).{@link
     * java.util.regex.Matcher#replaceAll(String) replaceAll}(<i>repl</i>)
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
     */
    /**
     * @cprover.noSupport Regular-expression matching is deferred to a
     *   future Pattern/Matcher stage.
     */
    public String replaceAll(String regex, String replacement) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
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
     */
    public String replace(CharSequence target, CharSequence replacement) {
        // Handled by JBMC's string-refinement (exact signature match);
        // this body is never executed.
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
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
     * array.
     * <ul>
     *    <li><p>
     *    If the <i>limit</i> is positive then the pattern will be applied
     *    at most <i>limit</i>&nbsp;-&nbsp;1 times, the array's length will be
     *    no greater than <i>limit</i>, and the array's last entry will contain
     *    all input beyond the last matched delimiter.</p></li>
     *
     *    <li><p>
     *    If the <i>limit</i> is zero then the pattern will be applied as
     *    many times as possible, the array can have any length, and trailing
     *    empty strings will be discarded.</p></li>
     *
     *    <li><p>
     *    If the <i>limit</i> is negative then the pattern will be applied
     *    as many times as possible and the array can have any length.</p></li>
     * </ul>
     *
     * <p> The string {@code "boo:and:foo"}, for example, yields the
     * following results with these parameters:
     *
     * <blockquote><table class="plain">
     * <caption style="display:none">Split example showing regex, limit, and result</caption>
     * <thead>
     * <tr>
     *     <th scope="col">Regex</th>
     *     <th scope="col">Limit</th>
     *     <th scope="col">Result</th>
     * </tr>
     * </thead>
     * <tbody>
     * <tr><th scope="row" rowspan="3" style="font-weight:normal">:</th>
     *     <th scope="row" style="font-weight:normal; text-align:right; padding-right:1em">2</th>
     *     <td>{@code { "boo", "and:foo" }}</td></tr>
     * <tr><!-- : -->
     *     <th scope="row" style="font-weight:normal; text-align:right; padding-right:1em">5</th>
     *     <td>{@code { "boo", "and", "foo" }}</td></tr>
     * <tr><!-- : -->
     *     <th scope="row" style="font-weight:normal; text-align:right; padding-right:1em">-2</th>
     *     <td>{@code { "boo", "and", "foo" }}</td></tr>
     * <tr><th scope="row" rowspan="3" style="font-weight:normal">o</th>
     *     <th scope="row" style="font-weight:normal; text-align:right; padding-right:1em">5</th>
     *     <td>{@code { "b", "", ":and:f", "", "" }}</td></tr>
     * <tr><!-- o -->
     *     <th scope="row" style="font-weight:normal; text-align:right; padding-right:1em">-2</th>
     *     <td>{@code { "b", "", ":and:f", "", "" }}</td></tr>
     * <tr><!-- o -->
     *     <th scope="row" style="font-weight:normal; text-align:right; padding-right:1em">0</th>
     *     <td>{@code { "b", "", ":and:f" }}</td></tr>
     * </tbody>
     * </table></blockquote>
     *
     * <p> An invocation of this method of the form
     * <i>str.</i>{@code split(}<i>regex</i>{@code ,}&nbsp;<i>n</i>{@code )}
     * yields the same result as the expression
     *
     * <blockquote>
     * <code>
     * {@link java.util.regex.Pattern}.{@link
     * java.util.regex.Pattern#compile(String) compile}(<i>regex</i>).{@link
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
     */
    public String[] split(String regex, int limit) {
        return split(regex, limit, false);
    }

    /**
     * Splits this string around matches of the given regular expression and
     * returns both the strings and the matching delimiters.
     *
     * <p> The array returned by this method contains each substring of this
     * string that is terminated by another substring that matches the given
     * expression or is terminated by the end of the string.
     * Each substring is immediately followed by the subsequence (the delimiter)
     * that matches the given expression, <em>except</em> for the last
     * substring, which is not followed by anything.
     * The substrings in the array and the delimiters are in the order in which
     * they occur in the input.
     * If the expression does not match any part of the input then the resulting
     * array has just one element, namely this string.
     *
     * <p> When there is a positive-width match at the beginning of this
     * string then an empty leading substring is included at the beginning
     * of the resulting array. A zero-width match at the beginning however
     * never produces such empty leading substring nor the empty delimiter.
     *
     * <p> The {@code limit} parameter controls the number of times the
     * pattern is applied and therefore affects the length of the resulting
     * array.
     * <ul>
     *    <li> If the <i>limit</i> is positive then the pattern will be applied
     *    at most <i>limit</i>&nbsp;-&nbsp;1 times, the array's length will be
     *    no greater than 2 &times; <i>limit</i> - 1, and the array's last
     *    entry will contain all input beyond the last matched delimiter.</li>
     *
     *    <li> If the <i>limit</i> is zero then the pattern will be applied as
     *    many times as possible, the array can have any length, and trailing
     *    empty strings will be discarded.</li>
     *
     *    <li> If the <i>limit</i> is negative then the pattern will be applied
     *    as many times as possible and the array can have any length.</li>
     * </ul>
     *
     * <p> The input {@code "boo:::and::foo"}, for example, yields the following
     * results with these parameters:
     *
     * <table class="plain" style="margin-left:2em;">
     * <caption style="display:none">Split example showing regex, limit, and result</caption>
     * <thead>
     * <tr>
     *     <th scope="col">Regex</th>
     *     <th scope="col">Limit</th>
     *     <th scope="col">Result</th>
     * </tr>
     * </thead>
     * <tbody>
     * <tr><th scope="row" rowspan="3" style="font-weight:normal">:+</th>
     *     <th scope="row" style="font-weight:normal; text-align:right; padding-right:1em">2</th>
     *     <td>{@code { "boo", ":::", "and::foo" }}</td></tr>
     * <tr><!-- : -->
     *     <th scope="row" style="font-weight:normal; text-align:right; padding-right:1em">5</th>
     *     <td>{@code { "boo", ":::", "and", "::", "foo" }}</td></tr>
     * <tr><!-- : -->
     *     <th scope="row" style="font-weight:normal; text-align:right; padding-right:1em">-1</th>
     *     <td>{@code { "boo", ":::", "and", "::", "foo" }}</td></tr>
     * <tr><th scope="row" rowspan="3" style="font-weight:normal">o</th>
     *     <th scope="row" style="font-weight:normal; text-align:right; padding-right:1em">5</th>
     *     <td>{@code { "b", "o", "", "o", ":::and::f", "o", "", "o", "" }}</td></tr>
     * <tr><!-- o -->
     *     <th scope="row" style="font-weight:normal; text-align:right; padding-right:1em">-1</th>
     *     <td>{@code { "b", "o", "", "o", ":::and::f", "o", "", "o", "" }}</td></tr>
     * <tr><!-- o -->
     *     <th scope="row" style="font-weight:normal; text-align:right; padding-right:1em">0</th>
     *     <td>{@code { "b", "o", "", "o", ":::and::f", "o", "", "o" }}</td></tr>
     * </tbody>
     * </table>
     *
     * @apiNote An invocation of this method of the form
     * <i>str.</i>{@code splitWithDelimiters(}<i>regex</i>{@code ,}&nbsp;<i>n</i>{@code )}
     * yields the same result as the expression
     *
     * <blockquote>
     * <code>
     * {@link java.util.regex.Pattern}.{@link
     * java.util.regex.Pattern#compile(String) compile}(<i>regex</i>).{@link
     * java.util.regex.Pattern#splitWithDelimiters(CharSequence,int) splitWithDelimiters}(<i>str</i>,&nbsp;<i>n</i>)
     * </code>
     * </blockquote>
     *
     * @param  regex
     *         the delimiting regular expression
     *
     * @param  limit
     *         the result threshold, as described above
     *
     * @return  the array of strings computed by splitting this string
     *          around matches of the given regular expression, alternating
     *          substrings and matching delimiters
     *
     * @since   21
     */
    public String[] splitWithDelimiters(String regex, int limit) {
        return split(regex, limit, true);
    }

    /**
     * @cprover.noSupport Regular-expression matching is deferred to a
     *   future Pattern/Matcher stage.
     */
    private String[] split(String regex, int limit, boolean withDelimiters) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
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
     * <blockquote><table class="plain">
     * <caption style="display:none">Split examples showing regex and result</caption>
     * <thead>
     * <tr>
     *  <th scope="col">Regex</th>
     *  <th scope="col">Result</th>
     * </tr>
     * </thead>
     * <tbody>
     * <tr><th scope="row" style="text-weight:normal">:</th>
     *     <td>{@code { "boo", "and", "foo" }}</td></tr>
     * <tr><th scope="row" style="text-weight:normal">o</th>
     *     <td>{@code { "b", "", ":and:f" }}</td></tr>
     * </tbody>
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
     */
    public String[] split(String regex) {
        return split(regex, 0, false);
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
     */
    public static String join(CharSequence delimiter, CharSequence... elements) {
        var delim = delimiter.toString();
        var elems = new String[elements.length];
        for (int i = 0; i < elements.length; i++) {
            elems[i] = String.valueOf(elements[i]);
        }
        return join("", "", delim, elems, elems.length);
    }

    /**
     * Designated join routine.
     *
     * @param prefix the non-null prefix
     * @param suffix the non-null suffix
     * @param delimiter the non-null delimiter
     * @param elements the non-null array of non-null elements
     * @param size the number of elements in the array (<= elements.length)
     * @return the joined string
     */
    /**
     * @cprover.fullSupport Composed via {@link StringBuilder#append},
     *   rather than the real byte-buffer-based implementation, which
     *   depends on {@code value}/{@code coder} directly.
     */
    static String join(String prefix, String suffix, String delimiter, String[] elements, int size) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sb.append(delimiter);
            }
            sb.append(elements[i]);
        }
        sb.append(suffix);
        return sb.toString();
    }

    /**
     * Returns a new {@code String} composed of copies of the
     * {@code CharSequence elements} joined together with a copy of the
     * specified {@code delimiter}.
     *
     * <blockquote>For example,
     * <pre>{@code
     *     List<String> strings = List.of("Java", "is", "cool");
     *     String message = String.join(" ", strings);
     *     // message returned is: "Java is cool"
     *
     *     Set<String> strings =
     *         new LinkedHashSet<>(List.of("Java", "is", "very", "cool"));
     *     String message = String.join("-", strings);
     *     // message returned is: "Java-is-very-cool"
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
     */
    public static String join(CharSequence delimiter,
            Iterable<? extends CharSequence> elements) {
        Objects.requireNonNull(delimiter);
        Objects.requireNonNull(elements);
        var delim = delimiter.toString();
        var elems = new String[8];
        int size = 0;
        for (CharSequence cs: elements) {
            if (size >= elems.length) {
                elems = Arrays.copyOf(elems, elems.length << 1);
            }
            elems[size++] = String.valueOf(cs);
        }
        return join("", "", delim, elems, size);
    }

    /**
     * Converts all of the characters in this {@code String} to lower
     * case using the rules of the given {@code Locale}.  Case mapping is based
     * on the Unicode Standard version specified by the {@link java.lang.Character Character}
     * class. Since case mappings are not always 1:1 char mappings, the resulting {@code String}
     * and this {@code String} may differ in length.
     * <p>
     * Examples of lowercase mappings are in the following table:
     * <table class="plain">
     * <caption style="display:none">Lowercase mapping examples showing language code of locale, upper case, lower case, and description</caption>
     * <thead>
     * <tr>
     *   <th scope="col">Language Code of Locale</th>
     *   <th scope="col">Upper Case</th>
     *   <th scope="col">Lower Case</th>
     *   <th scope="col">Description</th>
     * </tr>
     * </thead>
     * <tbody>
     * <tr>
     *   <td>tr (Turkish)</td>
     *   <th scope="row" style="font-weight:normal; text-align:left">&#92;u0130</th>
     *   <td>&#92;u0069</td>
     *   <td>capital letter I with dot above -&gt; small letter i</td>
     * </tr>
     * <tr>
     *   <td>tr (Turkish)</td>
     *   <th scope="row" style="font-weight:normal; text-align:left">&#92;u0049</th>
     *   <td>&#92;u0131</td>
     *   <td>capital letter I -&gt; small letter dotless i </td>
     * </tr>
     * <tr>
     *   <td>(all)</td>
     *   <th scope="row" style="font-weight:normal; text-align:left">French Fries</th>
     *   <td>french fries</td>
     *   <td>lowercased all chars in String</td>
     * </tr>
     * <tr>
     *   <td>(all)</td>
     *   <th scope="row" style="font-weight:normal; text-align:left">
     *       &Iota;&Chi;&Theta;&Upsilon;&Sigma;</th>
     *   <td>&iota;&chi;&theta;&upsilon;&sigma;</td>
     *   <td>lowercased all chars in String</td>
     * </tr>
     * </tbody>
     * </table>
     *
     * @param locale use the case transformation rules for this locale
     * @return the {@code String}, converted to lowercase.
     * @see     java.lang.String#toLowerCase()
     * @see     java.lang.String#toUpperCase()
     * @see     java.lang.String#toUpperCase(Locale)
     * @since   1.1
     */
    /**
     * @cprover.limitedSupport Composed via {@link Character#toLowerCase
     *   (char)} per character, ignoring {@code locale}: correct for the
     *   common case, but misses locale-specific exceptions (e.g. Turkish
     *   dotted/dotless i) and 1:many case mappings (e.g. German {@code
     *   ß}) that the real, full Unicode-and-locale-aware algorithm
     *   handles.
     */
    public String toLowerCase(Locale locale) {
        int len = length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(Character.toLowerCase(charAt(i)));
        }
        return sb.toString();
    }

    /**
     * Converts all of the characters in this {@code String} to lower
     * case using the rules of the default locale. This method is equivalent to
     * {@code toLowerCase(Locale.getDefault())}.
     *
     * @apiNote This method is locale sensitive, and may produce unexpected
     * results if used for strings that are intended to be interpreted locale
     * independently.
     * Examples are programming language identifiers, protocol keys, and HTML
     * tags.
     * For instance, {@code "TITLE".toLowerCase()} in a Turkish locale
     * returns {@code "t\u005Cu0131tle"}, where '\u005Cu0131' is the
     * LATIN SMALL LETTER DOTLESS I character.
     * To obtain correct results for locale insensitive strings, use
     * {@code toLowerCase(Locale.ROOT)}.
     *
     * @return  the {@code String}, converted to lowercase.
     * @see     java.lang.String#toLowerCase(Locale)
     */
    public String toLowerCase() {
        return toLowerCase(Locale.getDefault());
    }

    /**
     * Converts all of the characters in this {@code String} to upper
     * case using the rules of the given {@code Locale}. Case mapping is based
     * on the Unicode Standard version specified by the {@link java.lang.Character Character}
     * class. Since case mappings are not always 1:1 char mappings, the resulting {@code String}
     * and this {@code String} may differ in length.
     * <p>
     * Examples of locale-sensitive and 1:M case mappings are in the following table:
     * <table class="plain">
     * <caption style="display:none">Examples of locale-sensitive and 1:M case mappings. Shows Language code of locale, lower case, upper case, and description.</caption>
     * <thead>
     * <tr>
     *   <th scope="col">Language Code of Locale</th>
     *   <th scope="col">Lower Case</th>
     *   <th scope="col">Upper Case</th>
     *   <th scope="col">Description</th>
     * </tr>
     * </thead>
     * <tbody>
     * <tr>
     *   <td>tr (Turkish)</td>
     *   <th scope="row" style="font-weight:normal; text-align:left">&#92;u0069</th>
     *   <td>&#92;u0130</td>
     *   <td>small letter i -&gt; capital letter I with dot above</td>
     * </tr>
     * <tr>
     *   <td>tr (Turkish)</td>
     *   <th scope="row" style="font-weight:normal; text-align:left">&#92;u0131</th>
     *   <td>&#92;u0049</td>
     *   <td>small letter dotless i -&gt; capital letter I</td>
     * </tr>
     * <tr>
     *   <td>(all)</td>
     *   <th scope="row" style="font-weight:normal; text-align:left">&#92;u00df</th>
     *   <td>&#92;u0053 &#92;u0053</td>
     *   <td>small letter sharp s -&gt; two letters: SS</td>
     * </tr>
     * <tr>
     *   <td>(all)</td>
     *   <th scope="row" style="font-weight:normal; text-align:left">Fahrvergn&uuml;gen</th>
     *   <td>FAHRVERGN&Uuml;GEN</td>
     *   <td></td>
     * </tr>
     * </tbody>
     * </table>
     * @param locale use the case transformation rules for this locale
     * @return the {@code String}, converted to uppercase.
     * @see     java.lang.String#toUpperCase()
     * @see     java.lang.String#toLowerCase()
     * @see     java.lang.String#toLowerCase(Locale)
     * @since   1.1
     */
    /**
     * @cprover.limitedSupport Composed via {@link Character#toUpperCase
     *   (char)} per character, ignoring {@code locale}: see {@link
     *   #toLowerCase(Locale)}.
     */
    public String toUpperCase(Locale locale) {
        int len = length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(Character.toUpperCase(charAt(i)));
        }
        return sb.toString();
    }

    /**
     * Converts all of the characters in this {@code String} to upper
     * case using the rules of the default locale. This method is equivalent to
     * {@code toUpperCase(Locale.getDefault())}.
     *
     * @apiNote This method is locale sensitive, and may produce unexpected
     * results if used for strings that are intended to be interpreted locale
     * independently.
     * Examples are programming language identifiers, protocol keys, and HTML
     * tags.
     * For instance, {@code "title".toUpperCase()} in a Turkish locale
     * returns {@code "T\u005Cu0130TLE"}, where '\u005Cu0130' is the
     * LATIN CAPITAL LETTER I WITH DOT ABOVE character.
     * To obtain correct results for locale insensitive strings, use
     * {@code toUpperCase(Locale.ROOT)}.
     *
     * @return  the {@code String}, converted to uppercase.
     * @see     java.lang.String#toUpperCase(Locale)
     */
    public String toUpperCase() {
        return toUpperCase(Locale.getDefault());
    }

    /**
     * Returns a string whose value is this string, with all leading
     * and trailing space removed, where space is defined
     * as any character whose codepoint is less than or equal to
     * {@code 'U+0020'} (the space character).
     * <p>
     * If this {@code String} object represents an empty character
     * sequence, or the first and last characters of character sequence
     * represented by this {@code String} object both have codes
     * that are not space (as defined above), then a
     * reference to this {@code String} object is returned.
     * <p>
     * Otherwise, if all characters in this string are space (as
     * defined above), then a  {@code String} object representing an
     * empty string is returned.
     * <p>
     * Otherwise, let <i>k</i> be the index of the first character in the
     * string whose code is not a space (as defined above) and let
     * <i>m</i> be the index of the last character in the string whose code
     * is not a space (as defined above). A {@code String}
     * object is returned, representing the substring of this string that
     * begins with the character at index <i>k</i> and ends with the
     * character at index <i>m</i>-that is, the result of
     * {@code this.substring(k, m + 1)}.
     * <p>
     * This method may be used to trim space (as defined above) from
     * the beginning and end of a string.
     *
     * @return  a string whose value is this string, with all leading
     *          and trailing space removed, or this string if it
     *          has no leading or trailing space.
     */
    public String trim() {
        // Handled by JBMC's string-refinement (exact signature match);
        // this body is never executed.
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    /**
     * Returns a string whose value is this string, with all leading
     * and trailing {@linkplain Character#isWhitespace(int) white space}
     * removed.
     * <p>
     * If this {@code String} object represents an empty string,
     * or if all code points in this string are
     * {@linkplain Character#isWhitespace(int) white space}, then an empty string
     * is returned.
     * <p>
     * Otherwise, returns a substring of this string beginning with the first
     * code point that is not a {@linkplain Character#isWhitespace(int) white space}
     * up to and including the last code point that is not a
     * {@linkplain Character#isWhitespace(int) white space}.
     * <p>
     * This method may be used to strip
     * {@linkplain Character#isWhitespace(int) white space} from
     * the beginning and end of a string.
     *
     * @return  a string whose value is this string, with all leading
     *          and trailing white space removed
     *
     * @see Character#isWhitespace(int)
     *
     * @since 11
     */
    /**
     * @cprover.fullSupport Composed via {@link #indexOfNonWhitespace},
     *   {@link #lastIndexOfNonWhitespace} and {@link #substring}.
     */
    public String strip() {
        int left = indexOfNonWhitespace();
        if (left == length()) {
            return "";
        }
        int right = lastIndexOfNonWhitespace();
        return (left > 0 || right < length()) ? substring(left, right) : this;
    }

    /**
     * Returns a string whose value is this string, with all leading
     * {@linkplain Character#isWhitespace(int) white space} removed.
     * <p>
     * If this {@code String} object represents an empty string,
     * or if all code points in this string are
     * {@linkplain Character#isWhitespace(int) white space}, then an empty string
     * is returned.
     * <p>
     * Otherwise, returns a substring of this string beginning with the first
     * code point that is not a {@linkplain Character#isWhitespace(int) white space}
     * up to and including the last code point of this string.
     * <p>
     * This method may be used to trim
     * {@linkplain Character#isWhitespace(int) white space} from
     * the beginning of a string.
     *
     * @return  a string whose value is this string, with all leading white
     *          space removed
     *
     * @see Character#isWhitespace(int)
     *
     * @since 11
     */
    /**
     * @cprover.fullSupport Composed via {@link #indexOfNonWhitespace} and
     *   {@link #substring}.
     */
    public String stripLeading() {
        int left = indexOfNonWhitespace();
        return (left == 0) ? this : substring(left);
    }

    /**
     * Returns a string whose value is this string, with all trailing
     * {@linkplain Character#isWhitespace(int) white space} removed.
     * <p>
     * If this {@code String} object represents an empty string,
     * or if all characters in this string are
     * {@linkplain Character#isWhitespace(int) white space}, then an empty string
     * is returned.
     * <p>
     * Otherwise, returns a substring of this string beginning with the first
     * code point of this string up to and including the last code point
     * that is not a {@linkplain Character#isWhitespace(int) white space}.
     * <p>
     * This method may be used to trim
     * {@linkplain Character#isWhitespace(int) white space} from
     * the end of a string.
     *
     * @return  a string whose value is this string, with all trailing white
     *          space removed
     *
     * @see Character#isWhitespace(int)
     *
     * @since 11
     */
    /**
     * @cprover.fullSupport Composed via {@link #lastIndexOfNonWhitespace}
     *   and {@link #substring}.
     */
    public String stripTrailing() {
        int right = lastIndexOfNonWhitespace();
        return (right == length()) ? this : substring(0, right);
    }

    /**
     * Returns {@code true} if the string is empty or contains only
     * {@linkplain Character#isWhitespace(int) white space} codepoints,
     * otherwise {@code false}.
     *
     * @return {@code true} if the string is empty or contains only
     *         {@linkplain Character#isWhitespace(int) white space} codepoints,
     *         otherwise {@code false}
     *
     * @see Character#isWhitespace(int)
     *
     * @since 11
     */
    public boolean isBlank() {
        return indexOfNonWhitespace() == length();
    }

    /**
     * Returns a stream of lines extracted from this string,
     * separated by line terminators.
     * <p>
     * A <i>line terminator</i> is one of the following:
     * a line feed character {@code "\n"} (U+000A),
     * a carriage return character {@code "\r"} (U+000D),
     * or a carriage return followed immediately by a line feed
     * {@code "\r\n"} (U+000D U+000A).
     * <p>
     * A <i>line</i> is either a sequence of zero or more characters
     * followed by a line terminator, or it is a sequence of one or
     * more characters followed by the end of the string. A
     * line does not include the line terminator.
     * <p>
     * The stream returned by this method contains the lines from
     * this string in the order in which they occur.
     *
     * @apiNote This definition of <i>line</i> implies that an empty
     *          string has zero lines and that there is no empty line
     *          following a line terminator at the end of a string.
     *
     * @implNote This method provides better performance than
     *           split("\R") by supplying elements lazily and
     *           by faster search of new line terminators.
     *
     * @return  the stream of lines extracted from this string
     *
     * @since 11
     */
    /**
     * @cprover.fullSupport Composed via {@link #charAt} and
     *   {@link #substring}, rather than the real line-terminator scanner.
     */
    public Stream<String> lines() {
        List<String> result = new ArrayList<>();
        int len = length();
        int lineStart = 0;
        int i = 0;
        while (i < len) {
            char c = charAt(i);
            if (c == '\n') {
                result.add(substring(lineStart, i));
                i++;
                lineStart = i;
            } else if (c == '\r') {
                result.add(substring(lineStart, i));
                i++;
                if (i < len && charAt(i) == '\n') {
                    i++;
                }
                lineStart = i;
            } else {
                i++;
            }
        }
        if (lineStart < len) {
            result.add(substring(lineStart, len));
        }
        return result.stream();
    }

    /**
     * Adjusts the indentation of each line of this string based on the value of
     * {@code n}, and normalizes line termination characters.
     * <p>
     * This string is conceptually separated into lines using
     * {@link String#lines()}. Each line is then adjusted as described below
     * and then suffixed with a line feed {@code "\n"} (U+000A). The resulting
     * lines are then concatenated and returned.
     * <p>
     * If {@code n > 0} then {@code n} spaces (U+0020) are inserted at the
     * beginning of each line.
     * <p>
     * If {@code n < 0} then up to {@code n}
     * {@linkplain Character#isWhitespace(int) white space characters} are removed
     * from the beginning of each line. If a given line does not contain
     * sufficient white space then all leading
     * {@linkplain Character#isWhitespace(int) white space characters} are removed.
     * Each white space character is treated as a single character. In
     * particular, the tab character {@code "\t"} (U+0009) is considered a
     * single character; it is not expanded.
     * <p>
     * If {@code n == 0} then the line remains unchanged. However, line
     * terminators are still normalized.
     *
     * @param n  number of leading
     *           {@linkplain Character#isWhitespace(int) white space characters}
     *           to add or remove
     *
     * @return string with indentation adjusted and line endings normalized
     *
     * @see String#lines()
     * @see String#isBlank()
     * @see Character#isWhitespace(int)
     *
     * @since 12
     */
    public String indent(int n) {
        if (isEmpty()) {
            return "";
        }
        Stream<String> stream = lines();
        if (n > 0) {
            final String spaces = " ".repeat(n);
            stream = stream.map(s -> spaces + s);
        } else if (n == Integer.MIN_VALUE) {
            stream = stream.map(s -> s.stripLeading());
        } else if (n < 0) {
            stream = stream.map(s -> s.substring(Math.min(-n, s.indexOfNonWhitespace())));
        }
        return stream.collect(Collectors.joining("\n", "", "\n"));
    }

    private int indexOfNonWhitespace() {
        int len = length();
        int left = 0;
        while (left < len) {
            int codepoint = codePointAt(left);
            if (!Character.isWhitespace(codepoint)) {
                break;
            }
            left += Character.charCount(codepoint);
        }
        return left;
    }

    private int lastIndexOfNonWhitespace() {
        int right = length();
        while (0 < right) {
            int codepoint = codePointBefore(right);
            if (!Character.isWhitespace(codepoint)) {
                break;
            }
            right -= Character.charCount(codepoint);
        }
        return right;
    }

    /**
     * Returns a string whose value is this string, with incidental
     * {@linkplain Character#isWhitespace(int) white space} removed from
     * the beginning and end of every line.
     * <p>
     * Incidental {@linkplain Character#isWhitespace(int) white space}
     * is often present in a text block to align the content with the opening
     * delimiter. For example, in the following code, dots represent incidental
     * {@linkplain Character#isWhitespace(int) white space}:
     * <blockquote><pre>
     * String html = """
     * ..............&lt;html&gt;
     * ..............    &lt;body&gt;
     * ..............        &lt;p&gt;Hello, world&lt;/p&gt;
     * ..............    &lt;/body&gt;
     * ..............&lt;/html&gt;
     * ..............""";
     * </pre></blockquote>
     * This method treats the incidental
     * {@linkplain Character#isWhitespace(int) white space} as indentation to be
     * stripped, producing a string that preserves the relative indentation of
     * the content. Using | to visualize the start of each line of the string:
     * <blockquote><pre>
     * |&lt;html&gt;
     * |    &lt;body&gt;
     * |        &lt;p&gt;Hello, world&lt;/p&gt;
     * |    &lt;/body&gt;
     * |&lt;/html&gt;
     * </pre></blockquote>
     * First, the individual lines of this string are extracted. A <i>line</i>
     * is a sequence of zero or more characters followed by either a line
     * terminator or the end of the string.
     * If the string has at least one line terminator, the last line consists
     * of the characters between the last terminator and the end of the string.
     * Otherwise, if the string has no terminators, the last line is the start
     * of the string to the end of the string, in other words, the entire
     * string.
     * A line does not include the line terminator.
     * <p>
     * Then, the <i>minimum indentation</i> (min) is determined as follows:
     * <ul>
     *   <li><p>For each non-blank line (as defined by {@link String#isBlank()}),
     *   the leading {@linkplain Character#isWhitespace(int) white space}
     *   characters are counted.</p>
     *   </li>
     *   <li><p>The leading {@linkplain Character#isWhitespace(int) white space}
     *   characters on the last line are also counted even if
     *   {@linkplain String#isBlank() blank}.</p>
     *   </li>
     * </ul>
     * <p>The <i>min</i> value is the smallest of these counts.
     * <p>
     * For each {@linkplain String#isBlank() non-blank} line, <i>min</i> leading
     * {@linkplain Character#isWhitespace(int) white space} characters are
     * removed, and any trailing {@linkplain Character#isWhitespace(int) white
     * space} characters are removed. {@linkplain String#isBlank() Blank} lines
     * are replaced with the empty string.
     *
     * <p>
     * Finally, the lines are joined into a new string, using the LF character
     * {@code "\n"} (U+000A) to separate lines.
     *
     * @apiNote
     * This method's primary purpose is to shift a block of lines as far as
     * possible to the left, while preserving relative indentation. Lines
     * that were indented the least will thus have no leading
     * {@linkplain Character#isWhitespace(int) white space}.
     * The result will have the same number of line terminators as this string.
     * If this string ends with a line terminator then the result will end
     * with a line terminator.
     *
     * @implSpec
     * This method treats all {@linkplain Character#isWhitespace(int) white space}
     * characters as having equal width. As long as the indentation on every
     * line is consistently composed of the same character sequences, then the
     * result will be as described above.
     *
     * @return string with incidental indentation removed and line
     *         terminators normalized
     *
     * @see String#lines()
     * @see String#isBlank()
     * @see String#indent(int)
     * @see Character#isWhitespace(int)
     *
     * @since 15
     *
     */
    public String stripIndent() {
        int length = length();
        if (length == 0) {
            return "";
        }
        char lastChar = charAt(length - 1);
        boolean optOut = lastChar == '\n' || lastChar == '\r';
        List<String> lines = lines().toList();
        final int outdent = optOut ? 0 : outdent(lines);
        return lines.stream()
            .map(line -> {
                int firstNonWhitespace = line.indexOfNonWhitespace();
                int lastNonWhitespace = line.lastIndexOfNonWhitespace();
                int incidentalWhitespace = Math.min(outdent, firstNonWhitespace);
                return firstNonWhitespace > lastNonWhitespace
                    ? "" : line.substring(incidentalWhitespace, lastNonWhitespace);
            })
            .collect(Collectors.joining("\n", "", optOut ? "\n" : ""));
    }

    private static int outdent(List<String> lines) {
        // Note: outdent is guaranteed to be zero or positive number.
        // If there isn't a non-blank line then the last must be blank
        int outdent = Integer.MAX_VALUE;
        for (String line : lines) {
            int leadingWhitespace = line.indexOfNonWhitespace();
            if (leadingWhitespace != line.length()) {
                outdent = Integer.min(outdent, leadingWhitespace);
            }
        }
        String lastLine = lines.get(lines.size() - 1);
        if (lastLine.isBlank()) {
            outdent = Integer.min(outdent, lastLine.length());
        }
        return outdent;
    }

    /**
     * Returns a string whose value is this string, with escape sequences
     * translated as if in a string literal.
     * <p>
     * Escape sequences are translated as follows;
     * <table class="striped">
     *   <caption style="display:none">Translation</caption>
     *   <thead>
     *   <tr>
     *     <th scope="col">Escape</th>
     *     <th scope="col">Name</th>
     *     <th scope="col">Translation</th>
     *   </tr>
     *   </thead>
     *   <tbody>
     *   <tr>
     *     <th scope="row">{@code \u005Cb}</th>
     *     <td>backspace</td>
     *     <td>{@code U+0008}</td>
     *   </tr>
     *   <tr>
     *     <th scope="row">{@code \u005Ct}</th>
     *     <td>horizontal tab</td>
     *     <td>{@code U+0009}</td>
     *   </tr>
     *   <tr>
     *     <th scope="row">{@code \u005Cn}</th>
     *     <td>line feed</td>
     *     <td>{@code U+000A}</td>
     *   </tr>
     *   <tr>
     *     <th scope="row">{@code \u005Cf}</th>
     *     <td>form feed</td>
     *     <td>{@code U+000C}</td>
     *   </tr>
     *   <tr>
     *     <th scope="row">{@code \u005Cr}</th>
     *     <td>carriage return</td>
     *     <td>{@code U+000D}</td>
     *   </tr>
     *   <tr>
     *     <th scope="row">{@code \u005Cs}</th>
     *     <td>space</td>
     *     <td>{@code U+0020}</td>
     *   </tr>
     *   <tr>
     *     <th scope="row">{@code \u005C"}</th>
     *     <td>double quote</td>
     *     <td>{@code U+0022}</td>
     *   </tr>
     *   <tr>
     *     <th scope="row">{@code \u005C'}</th>
     *     <td>single quote</td>
     *     <td>{@code U+0027}</td>
     *   </tr>
     *   <tr>
     *     <th scope="row">{@code \u005C\u005C}</th>
     *     <td>backslash</td>
     *     <td>{@code U+005C}</td>
     *   </tr>
     *   <tr>
     *     <th scope="row">{@code \u005C0 - \u005C377}</th>
     *     <td>octal escape</td>
     *     <td>code point equivalents</td>
     *   </tr>
     *   <tr>
     *     <th scope="row">{@code \u005C<line-terminator>}</th>
     *     <td>continuation</td>
     *     <td>discard</td>
     *   </tr>
     *   </tbody>
     * </table>
     *
     * @implNote
     * This method does <em>not</em> translate Unicode escapes such as "{@code \u005cu2022}".
     * Unicode escapes are translated by the Java compiler when reading input characters and
     * are not part of the string literal specification.
     *
     * @throws IllegalArgumentException when an escape sequence is malformed.
     *
     * @return String with escape sequences translated.
     *
     * @jls 3.10.7 Escape Sequences
     *
     * @since 15
     */
    public String translateEscapes() {
        if (isEmpty()) {
            return "";
        }
        char[] chars = toCharArray();
        int length = chars.length;
        int from = 0;
        int to = 0;
        while (from < length) {
            char ch = chars[from++];
            if (ch == '\\') {
                ch = from < length ? chars[from++] : '\0';
                switch (ch) {
                case 'b':
                    ch = '\b';
                    break;
                case 'f':
                    ch = '\f';
                    break;
                case 'n':
                    ch = '\n';
                    break;
                case 'r':
                    ch = '\r';
                    break;
                case 's':
                    ch = ' ';
                    break;
                case 't':
                    ch = '\t';
                    break;
                case '\'':
                case '\"':
                case '\\':
                    // as is
                    break;
                case '0': case '1': case '2': case '3':
                case '4': case '5': case '6': case '7':
                    int limit = Integer.min(from + (ch <= '3' ? 2 : 1), length);
                    int code = ch - '0';
                    while (from < limit) {
                        ch = chars[from];
                        if (ch < '0' || '7' < ch) {
                            break;
                        }
                        from++;
                        code = (code << 3) | (ch - '0');
                    }
                    ch = (char)code;
                    break;
                case '\n':
                    continue;
                case '\r':
                    if (from < length && chars[from] == '\n') {
                        from++;
                    }
                    continue;
                default: {
                    String msg = "Invalid escape sequence: \\" + ch
                        + " \\\\u" + Integer.toHexString((int) ch);
                    throw new IllegalArgumentException(msg);
                }
                }
            }

            chars[to++] = ch;
        }

        return new String(chars, 0, to);
    }

    /**
     * This method allows the application of a function to {@code this}
     * string. The function should expect a single String argument
     * and produce an {@code R} result.
     * <p>
     * Any exception thrown by {@code f.apply()} will be propagated to the
     * caller.
     *
     * @param f    a function to apply
     *
     * @param <R>  the type of the result
     *
     * @return     the result of applying the function to this string
     *
     * @see java.util.function.Function
     *
     * @since 12
     */
    public <R> R transform(Function<? super String, ? extends R> f) {
        return f.apply(this);
    }

    /**
     * This object (which is already a string!) is itself returned.
     *
     * @return  the string itself.
     */
    public String toString() {
        // Handled by JBMC's string-refinement (exact signature match);
        // this body is never executed.
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    /**
     * Returns a stream of {@code int} zero-extending the {@code char} values
     * from this sequence.  Any char which maps to a {@linkplain
     * Character##unicode surrogate code point} is passed through
     * uninterpreted.
     *
     * @return an IntStream of char values from this sequence
     * @since 9
     */
    /**
     * @cprover.fullSupport Composed via {@link #charAt} into a materialized
     *   array, rather than the real lazy byte-buffer-backed spliterator.
     */
    @Override
    public IntStream chars() {
        int len = length();
        int[] result = new int[len];
        for (int i = 0; i < len; i++) {
            result[i] = charAt(i);
        }
        return Arrays.stream(result);
    }


    /**
     * Returns a stream of code point values from this sequence.  Any surrogate
     * pairs encountered in the sequence are combined as if by {@linkplain
     * Character#toCodePoint Character.toCodePoint} and the result is passed
     * to the stream. Any other code units, including ordinary BMP characters,
     * unpaired surrogates, and undefined code units, are zero-extended to
     * {@code int} values which are then passed to the stream.
     *
     * @return an IntStream of Unicode code points from this sequence
     * @since 9
     */
    /**
     * @cprover.fullSupport Composed via {@link #codePointAt} into a
     *   materialized array, rather than the real lazy
     *   byte-buffer-backed spliterator.
     */
    @Override
    public IntStream codePoints() {
        int len = length();
        int[] buf = new int[len];
        int count = 0;
        int i = 0;
        while (i < len) {
            int cp = codePointAt(i);
            buf[count++] = cp;
            i += Character.charCount(cp);
        }
        return Arrays.stream(buf, 0, count);
    }

    /**
     * Converts this string to a new character array.
     *
     * @return  a newly allocated character array whose length is the length
     *          of this string and whose contents are initialized to contain
     *          the character sequence represented by this string.
     */
    /**
     * @cprover.fullSupport No primitive exists for this; composed from
     *   {@link #charAt} (which routes through {@link
     *   CProverString#charAt(String, int)}).
     */
    public char[] toCharArray() {
        int len = length();
        char[] result = new char[len];
        for (int i = 0; i < len; i++) {
            result[i] = charAt(i);
        }
        return result;
    }

    /**
     * Returns a formatted string using the specified format string and
     * arguments.
     *
     * <p> The locale always used is the one returned by {@link
     * java.util.Locale#getDefault(java.util.Locale.Category)
     * Locale.getDefault(Locale.Category)} with
     * {@link java.util.Locale.Category#FORMAT FORMAT} category specified.
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
     *         <cite>The Java Virtual Machine Specification</cite>.
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
     */
    /**
     * @cprover.noSupport Full {@code java.util.Formatter} printf-style
     *   parsing (flags, width, precision, per-conversion-type handling) is
     *   not modelled.
     */
    public static String format(String format, Object... args) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
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
     *         <cite>The Java Virtual Machine Specification</cite>.
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
     */
    /**
     * @cprover.noSupport See {@link #format(String, Object...)}.
     */
    public static String format(Locale l, String format, Object... args) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    /**
     * Formats using this string as the format string, and the supplied
     * arguments.
     *
     * @implSpec This method is equivalent to {@code String.format(this, args)}.
     *
     * @param  args
     *         Arguments referenced by the format specifiers in this string.
     *
     * @return  A formatted string
     *
     * @see  java.lang.String#format(String,Object...)
     * @see  java.util.Formatter
     *
     * @since 15
     *
     */
    /**
     * @cprover.noSupport See {@link #format(String, Object...)}.
     */
    public String formatted(Object... args) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    /**
     * Returns the string representation of the {@code Object} argument.
     *
     * @param   obj   an {@code Object}.
     * @return  if the argument is {@code null}, then a string equal to
     *          {@code "null"}; otherwise, the value of
     *          {@code obj.toString()} is returned.
     * @see     java.lang.Object#toString()
     */
    public static String valueOf(Object obj) {
        return (obj == null) ? "null" : obj.toString();
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
     */
    public static String valueOf(char[] data) {
        return new String(data);
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
     * @throws    IndexOutOfBoundsException if {@code offset} is
     *          negative, or {@code count} is negative, or
     *          {@code offset+count} is larger than
     *          {@code data.length}.
     */
    public static String valueOf(char[] data, int offset, int count) {
        return new String(data, offset, count);
    }

    /**
     * Equivalent to {@link #valueOf(char[], int, int)}.
     *
     * @param   data     the character array.
     * @param   offset   initial offset of the subarray.
     * @param   count    length of the subarray.
     * @return  a {@code String} that contains the characters of the
     *          specified subarray of the character array.
     * @throws    IndexOutOfBoundsException if {@code offset} is
     *          negative, or {@code count} is negative, or
     *          {@code offset+count} is larger than
     *          {@code data.length}.
     */
    public static String copyValueOf(char[] data, int offset, int count) {
        return new String(data, offset, count);
    }

    /**
     * Equivalent to {@link #valueOf(char[])}.
     *
     * @param   data   the character array.
     * @return  a {@code String} that contains the characters of the
     *          character array.
     */
    public static String copyValueOf(char[] data) {
        return new String(data);
    }

    /**
     * Returns the string representation of the {@code boolean} argument.
     *
     * @param   b   a {@code boolean}.
     * @return  if the argument is {@code true}, a string equal to
     *          {@code "true"} is returned; otherwise, a string equal to
     *          {@code "false"} is returned.
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
     */
    /**
     * @cprover.fullSupport Composed via {@link
     *   CProverString#ofCharArray} and the {@code String(String)}
     *   constructor (intercepted), rather than the real byte-buffer-based
     *   implementation: see {@link Integer#toString(int)}.
     */
    public static String valueOf(char c) {
        return CProverString.ofCharArray(new char[] {c}, 0, 1);
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
     */
    public static String valueOf(int i) {
        return Integer.toString(i);
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
     */
    public static String valueOf(long l) {
        return Long.toString(l);
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
     */
    public static String valueOf(float f) {
        return Float.toString(f);
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
     */
    public static String valueOf(double d) {
        return Double.toString(d);
    }

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
     * interned. String literals are defined in section {@jls 3.10.5} of the
     * <cite>The Java Language Specification</cite>.
     *
     * @return  a string that has the same contents as this string, but is
     *          guaranteed to be from a pool of unique strings.
     *
     * @cprover.limitedSupport Approximates the real (unbounded, VM-wide)
     *   intern pool with a fixed-size cache, in the same style as {@link
     *   Class#forName}; interning more than {@value
     *   #CPROVER_MAX_INTERN_POOL} distinct strings over a verification run
     *   just stops adding to the pool (every such string is still
     *   returned as-is, just without the "same instance as any other
     *   equal interned string" guarantee).
     */
    public String intern() {
        if (cproverInternPool == null) {
            cproverInternPool = new String[CPROVER_MAX_INTERN_POOL];
        }
        for (int i = 0; i < cproverInternPoolSize; ++i) {
            if (equals(cproverInternPool[i])) {
                return cproverInternPool[i];
            }
        }
        if (cproverInternPoolSize < cproverInternPool.length) {
            cproverInternPool[cproverInternPoolSize++] = this;
        }
        return this;
    }

    private static final int CPROVER_MAX_INTERN_POOL = 8;
    private static String[] cproverInternPool;
    private static int cproverInternPoolSize;

    /**
     * Returns a string whose value is the concatenation of this
     * string repeated {@code count} times.
     * <p>
     * If this string is empty or count is zero then the empty
     * string is returned.
     *
     * @param   count number of times to repeat
     *
     * @return  A string composed of this string repeated
     *          {@code count} times or the empty string if this
     *          string is empty or count is zero
     *
     * @throws  IllegalArgumentException if the {@code count} is
     *          negative.
     *
     * @since 11
     */
    /**
     * @cprover.fullSupport Composed via {@link
     *   StringBuilder#repeat(CharSequence, int)}, rather than the real
     *   byte-buffer-based implementation: see {@link Integer#toString
     *   (int)}.
     */
    public String repeat(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("count is negative: " + count);
        }
        if (count == 1) {
            return this;
        }
        return new StringBuilder().repeat(this, count).toString();
    }

    ////////////////////////////////////////////////////////////////

    /**
     * Copy character bytes from this string into dst starting at dstBegin.
     * This method doesn't perform any range checking.
     *
     * Invoker guarantees: dst is in UTF16 (inflate itself for asb), if two
     * coders are different, and dst is big enough (range check)
     *
     * @cprover.untested Only called from {@link StringConcatHelper}, which
     *   in turn is only reachable from javac's invokedynamic-based string
     *   concatenation; see {@code String(byte[], byte)}.
     *
     * @param dstBegin  the char index, not offset of byte[]
     * @param coder     the coder of dst[]
     */
    void getBytes(byte[] dst, int dstBegin, byte coder) {
        if (coder() == coder) {
            System.arraycopy(value, 0, dst, dstBegin << coder, value.length);
        } else {    // this.coder == LATIN && coder == UTF16
            StringLatin1.inflate(value, 0, dst, dstBegin, value.length);
        }
    }

    /**
     * Copy character bytes from this string into dst starting at dstBegin.
     * This method doesn't perform any range checking.
     *
     * Invoker guarantees: dst is in UTF16 (inflate itself for asb), if two
     * coders are different, and dst is big enough (range check)
     *
     * @param srcPos    the char index, not offset of byte[]
     * @param dstBegin  the char index to start from
     * @param coder     the coder of dst[]
     * @param length    the amount of copied chars
     */
    void getBytes(byte[] dst, int srcPos, int dstBegin, byte coder, int length) {
        if (coder() == coder) {
            System.arraycopy(value, srcPos << coder, dst, dstBegin << coder, length << coder);
        } else {    // this.coder == LATIN && coder == UTF16
            StringLatin1.inflate(value, srcPos, dst, dstBegin, length);
        }
    }

    /*
     * Private constructor. Trailing Void argument is there for
     * disambiguating it against other (public) constructors.
     *
     * Stores the char[] value into a byte[] that each byte represents
     * the8 low-order bits of the corresponding character, if the char[]
     * contains only latin1 character. Or a byte[] that stores all
     * characters in their byte sequences defined by the {@code StringUTF16}.
     *
     * <p> The contents of the string are unspecified if the character array
     * is modified during string construction.
     */
    /**
     * @cprover.fullSupport Composed via {@link CProverString#ofCharArray}
     *   and the {@code String(String)} constructor (intercepted), rather
     *   than the real compact-string construction (which this model
     *   cannot perform: see {@link AbstractStringBuilder}'s class
     *   Javadoc for why directly manipulating {@code value}/{@code coder}
     *   does not work).
     */
    private String(char[] value, int off, int len, Void sig) {
        this(CProverString.ofCharArray(value, off, len));
    }

    /**
     * @cprover.noSupport Handled by JBMC's string-refinement (exact
     *   signature match on {@code String(StringBuilder)}); this body is
     *   never executed.
     */
    String(AbstractStringBuilder asb, Void sig) {
        CProver.notModelled();
        this.value = "".value;
        this.coder = "".coder;
    }

   /*
    * Package private constructor which shares value array for speed.
    *
    * Reachable only via StringConcatHelper#newString, which in turn is
    * only reachable from javac's invokedynamic-based string
    * concatenation (JDK 9+ default). JBMC does not currently resolve
    * java.lang.invoke.StringConcatFactory invokedynamic call sites, so
    * this constructor is real code with a real caller, but that caller
    * is not presently exercised under JBMC.
    */
    String(byte[] value, byte coder) {
        this.value = value;
        this.coder = coder;
    }

    byte coder() {
        return COMPACT_STRINGS ? coder : UTF16;
    }

    byte[] value() {
        return value;
    }

    @Native static final byte LATIN1 = 0;
    @Native static final byte UTF16  = 1;

    /*
     * StringIndexOutOfBoundsException  if {@code index} is
     * negative or greater than or equal to {@code length}.
     */
    static void checkIndex(int index, int length) {
        Preconditions.checkIndex(index, length, Preconditions.SIOOBE_FORMATTER);
    }

    /*
     * StringIndexOutOfBoundsException  if {@code offset}
     * is negative or greater than {@code length}.
     */
    static void checkOffset(int offset, int length) {
        Preconditions.checkFromToIndex(offset, length, length, Preconditions.SIOOBE_FORMATTER);
    }

    /*
     * Check {@code offset}, {@code count} against {@code 0} and {@code length}
     * bounds.
     *
     * @return  {@code offset} if the sub-range within bounds of the range
     * @throws  StringIndexOutOfBoundsException
     *          If {@code offset} is negative, {@code count} is negative,
     *          or {@code offset} is greater than {@code length - count}
     */
    static int checkBoundsOffCount(int offset, int count, int length) {
        return Preconditions.checkFromIndexSize(offset, count, length, Preconditions.SIOOBE_FORMATTER);
    }

    /*
     * Check {@code begin}, {@code end} against {@code 0} and {@code length}
     * bounds.
     *
     * @throws  StringIndexOutOfBoundsException
     *          If {@code begin} is negative, {@code begin} is greater than
     *          {@code end}, or {@code end} is greater than {@code length}.
     */
    static void checkBoundsBeginEnd(int begin, int end, int length) {
        Preconditions.checkFromToIndex(begin, end, length, Preconditions.SIOOBE_FORMATTER);
    }

    /**
     * Returns the string representation of the {@code codePoint}
     * argument.
     *
     * @param   codePoint a {@code codePoint}.
     * @return  a string of length {@code 1} or {@code 2} containing
     *          as its single character the argument {@code codePoint}.
     * @throws IllegalArgumentException if the specified
     *          {@code codePoint} is not a {@linkplain Character#isValidCodePoint
     *          valid Unicode code point}.
     */
    /**
     * @cprover.fullSupport Composed via {@link
     *   CProverString#ofCharArray}, rather than the real byte-buffer-based
     *   implementation: see {@link Integer#toString(int)}.
     */
    static String valueOfCodePoint(int codePoint) {
        if (Character.isBmpCodePoint(codePoint)) {
            return CProverString.ofCharArray(new char[] {(char) codePoint}, 0, 1);
        } else if (Character.isSupplementaryCodePoint(codePoint)) {
            char[] surrogates = new char[2];
            Character.toSurrogates(codePoint, surrogates, 0);
            return CProverString.ofCharArray(surrogates, 0, 2);
        }

        throw new IllegalArgumentException(
            "Not a valid Unicode code point: 0x" + Integer.toHexString(codePoint));
    }

    /**
     * Returns an {@link Optional} containing the nominal descriptor for this
     * instance, which is the instance itself.
     *
     * @return an {@link Optional} describing the {@linkplain String} instance
     * @since 12
     */
    @Override
    public Optional<String> describeConstable() {
        return Optional.of(this);
    }

    /**
     * Resolves this instance as a {@link ConstantDesc}, the result of which is
     * the instance itself.
     *
     * @param lookup ignored
     * @return the {@linkplain String} instance
     * @since 12
     */
    @Override
    public String resolveConstantDesc(MethodHandles.Lookup lookup) {
        return this;
    }

}
