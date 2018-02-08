package org.cprover;

/// This class provides an interface with string functions modeled internally
/// in CProver, for which the CProver model differs from the JDK actual behavior.
/// This is in particular the case for functions that throw exceptions.
public final class CProverString
{
    /**
     * Returns the character (Unicode code point) at the specified
     * index.
     *
     * @param      instance the String instance
     * @param      index the index to the {@code char} values
     * @return     the code point value of the character at the
     *             {@code index}
     */
    public static int codePointAt(String instance, int index) {
        return CProver.nondetInt();
    }

    /**
     * Returns the character (Unicode code point) before the specified
     * index.
     *
     * @param      instance the String instance
     * @param      index the index following the code point that should be returned
     * @return     the code point value of the character before the
     *             {@code index}
     */
    public static int codePointBefore(String instance, int index) {
        return CProver.nondetInt();
    }

    /**
     * Returns the number of Unicode code points in the specified text
     * range of this {@code String}.
     *
     * @param      instance the String instance
     * @param      beginIndex the index to the first {@code char} of
     *             the text range.
     * @param      endIndex the index after the last {@code char} of
     *             the text range.
     * @return the number of Unicode code points in the specified text
     * range
     */
    public static int codePointCount(
            String instance, int beginIndex, int endIndex) {
        return CProver.nondetInt();
    }

    /**
     * Returns the index within this {@code String} that is
     * offset from the given {@code index} by
     * {@code codePointOffset} code points.
     *
     * @param      instance the String instance
     * @param      index the index to be offset
     * @param      codePointOffset the offset in code points
     * @return the index within this {@code String}
     */
    public static int offsetByCodePoints(
            String instance, int index, int codePointOffset) {
        return  CProver.nondetInt();
    }

    /// Modeled internally in CBMC.
    /// @return '\u0000' if index is out of bounds and behave as s.charAt(i)
    ///         otherwise.
    public static char charAt(String s, int i) {
        return CProver.nondetChar();
    }

    /// Modeled internally in CBMC.
    /// @return empty string if index is too large, s if index too small and
    ///         behave as s.substring(i) otherwise.
    public static String substring(String s, int i) {
        return CProver.nondetWithoutNullForNotModelled();
    }

    /// Modeled internally in CBMC.
    /// @return empty string if i >= j, s.substring(k, l) where k = max(0, i)
    ///         and l = min(s.length() - 1, j) otherwise.
    public static String substring(String s, int i, int j) {
        return CProver.nondetWithoutNullForNotModelled();
    }

    /**
     * Returns a character sequence that is a subsequence of this sequence.
     *
     * @param   instance     the String instance
     * @param   beginIndex   the begin index, inclusive.
     * @param   endIndex     the end index, exclusive.
     * @return  the specified subsequence.
     */
    public static CharSequence subSequence(
            String s, int beginIndex, int endIndex) {
        return CProver.nondetWithoutNullForNotModelled();
    }

    /**
     * Appends a subsequence of the specified {@code CharSequence} to this
     * sequence.
     *
     * @param   instance  the StringBuilder instance
     * @param   s         the sequence to append.
     * @param   start     the starting index of the subsequence to be appended.
     * @param   end       the end index of the subsequence to be appended.
     * @return  the modified StringBuilder.
     */
    public static StringBuilder append(
            StringBuilder instance, CharSequence cs, int start, int end) {
        return CProver.nondetWithoutNullForNotModelled();
    }

    /**
     * Removes the characters in a substring of this sequence.
     *
     * @param      instance  the StringBuilder instance
     * @param      start  The beginning index, inclusive.
     * @param      end    The ending index, exclusive.
     * @return     the modified StringBuilder.
     */
    public static StringBuilder delete(
            StringBuilder instance, int start, int end) {
        return CProver.nondetWithoutNullForNotModelled();
    }

    /**
     * Removes the {@code char} at the specified position in this
     * sequence. This sequence is shortened by one {@code char}.
     *
     * @param       instance  the StringBuilder instance
     * @param       index  Index of {@code char} to remove
     * @return      the modified StringBuilder.
     */
    public static StringBuilder deleteCharAt(
            StringBuilder instance, int index) {
        return CProver.nondetWithoutNullForNotModelled();
    }

    /**
     * Equality of two strings. Should be much more efficient for
     * test-genaration than String.equals because the type is known at
     * compilation time.
     */
    public static boolean equals(String a, String b)
    {
        if (a == null) {
            return b == null;
        }
        return a.length() == b.length() && a.startsWith(b);
    }

    /**
     * Inserts the string into this character sequence.
     *
     * @param      instance  the StringBuilder instance
     * @param      offset   the offset.
     * @param      str      a string.
     * @return     the modified StringBuilder.
     */
    public static StringBuilder insert(
            StringBuilder instance, int offset, String str) {
        return CProver.nondetWithoutNullForNotModelled();
    }

    /**
     * Inserts the string representation of the {@code boolean}
     * argument into this sequence.
     *
     * @param      instance  the StringBuilder instance
     * @param      offset   the offset.
     * @param      b        a {@code boolean}.
     * @return     the modified StringBuilder.
     */
    public static StringBuilder insert(
            StringBuilder instance, int offset, boolean b) {
        return CProver.nondetWithoutNullForNotModelled();
    }

    /**
     * Inserts the string representation of the {@code char}
     * argument into this sequence.
     *
     * @param      instance  the StringBuilder instance
     * @param      offset   the offset.
     * @param      c        a {@code char}.
     * @return     the modified StringBuilder.
     */
    public static StringBuilder insert(
            StringBuilder instance, int offset, char c) {
        return CProver.nondetWithoutNullForNotModelled();
    }

    /**
     * Inserts the string representation of the {@code int}
     * argument into this sequence.
     *
     * @param      instance  the StringBuilder instance
     * @param      offset   the offset.
     * @param      b        a {@code int}.
     * @return     the modified StringBuilder.
     */
    public static StringBuilder insert(
            StringBuilder instance, int offset, int i) {
        return CProver.nondetWithoutNullForNotModelled();
    }

    /**
     * Inserts the string representation of the {@code long}
     * argument into this sequence.
     *
     * @param      instance  the StringBuilder instance
     * @param      offset   the offset.
     * @param      b        a {@code long}.
     * @return     the modified StringBuilder.
     */
    public static StringBuilder insert(
            StringBuilder instance, int offset, long l) {
        return CProver.nondetWithoutNullForNotModelled();
    }

    /**
     * Inserts the string representation of the {@code float}
     * argument into this sequence.
     *
     * @param      instance  the StringBuilder instance
     * @param      offset   the offset.
     * @param      b        a {@code float}.
     * @return     the modified StringBuilder.
     */
    public static StringBuilder insert(
            StringBuilder instance, int offset, float f) {
        return CProver.nondetWithoutNullForNotModelled();
    }

    /**
     * Inserts the string representation of the {@code double}
     * argument into this sequence.
     *
     * @param      instance  the StringBuilder instance
     * @param      offset   the offset.
     * @param      b        a {@code double}.
     * @return     the modified StringBuilder.
     */
    public static StringBuilder insert(
            StringBuilder instance, int offset, double d) {
        return CProver.nondetWithoutNullForNotModelled();
    }

    /**
     * Sets the length of the character sequence.
     *
     * @param      instance    the StringBuffer instance
     * @param      newLength   the new length
     */
    public static void setLength(StringBuffer instance, int newLength) {
    }

    /**
     * Returns the {@code char} value in this sequence at the specified index.
     *
     * @param      instance    the StringBuffer instance
     * @param      index       the index of the desired {@code char} value.
     * @return     the {@code char} value at the specified index.
     */
    public static char charAt(StringBuffer instance, int index) {
        return CProver.nondetChar();
    }

    /**
     * The character at the specified index is set to {@code ch}.
     *
     * @param      instance    the StringBuffer instance
     * @param      index       the index of the character to modify.
     * @param      ch          the new character.
     */
    public static void setCharAt(StringBuffer instance, int index, char c) {
    }

    /**
     * Removes the characters in a substring of this sequence.
     *
     * @param      instance    the StringBuffer instance
     * @param      start       the beginning index, inclusive.
     * @param      end         the ending index, exclusive.
     * @return     The modified StringBuffer.
     */
    public static StringBuffer delete(
            StringBuffer instance, int start, int end) {
        return CProver.nondetWithoutNullForNotModelled();
    }

    /**
     * Removes the {@code char} at the specified position in this
     * sequence. This sequence is shortened by one {@code char}.
     *
     * <p>Note: If the character at the given index is a supplementary
     * character, this method does not remove the entire character. If
     * correct handling of supplementary characters is required,
     * determine the number of {@code char}s to remove by calling
     * {@code Character.charCount(thisSequence.codePointAt(index))},
     * where {@code thisSequence} is this sequence.
     *
     * @param       instance    the StringBuffer instance.
     * @param       index       index of {@code char} to remove
     * @return      this object.
     */
    public static StringBuffer deleteCharAt(StringBuffer instance, int index) {
        return CProver.nondetWithoutNullForNotModelled();
    }

    /**
     * Returns a new {@code String} that contains a subsequence of
     * characters currently contained in this sequence.
     *
     * @param      instance  the StringBuffer instance.
     * @param      start     the beginning index, inclusive.
     * @param      end       the ending index, exclusive.
     * @return     The new string.
     */
    public static String substring(StringBuffer instance, int start, int end) {
        return CProver.nondetWithoutNullForNotModelled();
    }

    /**
     * Inserts the string into this character sequence.
     *
     * @param      instance  the StringBuffer instance.
     * @param      offset    the offset.
     * @param      str       a string.
     * @return     The modified StringBuffer.
     */
    public static StringBuffer insert(
            StringBuffer instance, int offset, String str) {
        return CProver.nondetWithoutNullForNotModelled();
    }

    /**
     * Inserts the string representation of the {@code boolean}
     * argument into this sequence.
     *
     * @param      offset   the offset.
     * @param      b        a {@code boolean}.
     * @return     a reference to this object.
     */
    public static StringBuffer insert(
            StringBuffer instance, int offset, boolean b) {
        return CProver.nondetWithoutNullForNotModelled();
    }

    /**
     * Inserts the string representation of the {@code char}
     * argument into this sequence.
     *
     * @param      offset   the offset.
     * @param      c        a {@code char}.
     * @return     a reference to this object.
     */
    public static StringBuffer insert(
            StringBuffer instance, int offset, char c) {
        return CProver.nondetWithoutNullForNotModelled();
    }

    /**
     * Inserts the string representation of the {@code int}
     * argument into this sequence.
     *
     * @param      offset   the offset.
     * @param      i        a {@code int}.
     * @return     a reference to this object.
     */
    public static StringBuffer insert(
            StringBuffer instance, int offset, int i) {
        return CProver.nondetWithoutNullForNotModelled();
    }

    /**
     * Inserts the string representation of the {@code long}
     * argument into this sequence.
     *
     * @param      offset   the offset.
     * @param      l        a {@code long}.
     * @return     a reference to this object.
     */
    public static StringBuffer insert(
            StringBuffer instance, int offset, long l) {
        return CProver.nondetWithoutNullForNotModelled();
    }

}
