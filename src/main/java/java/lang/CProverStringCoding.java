package java.lang;

import org.cprover.CProver;
import org.cprover.CProverString;

/**
 * DIFFBLUE MODEL LIBRARY
 * The encoders and decoders of String for the charsets that the models know:
 * UTF-8, ISO-8859-1, US-ASCII, UTF-16BE, UTF-16LE and UTF-16, with the
 * replacements of the JDK ('?' or U+FFFD). Other charsets, and the default
 * charset of the platform for strings beyond ASCII, are not modelled (a
 * failed "not-modelled" property, see CProverRuntime.notModelledUnless);
 * malformed input of the decoders of UTF-8 and UTF-16 gives any string.
 * (The models assumed that every string was ASCII, or of one of these
 * charsets, so that the paths of any other input were unreachable.)
 */
final class CProverStringCoding {

    private CProverStringCoding() {
    }

    static final int UTF_8 = 1;
    static final int ISO_8859_1 = 2;
    static final int US_ASCII = 3;
    static final int UTF_16BE = 4;
    static final int UTF_16LE = 5;
    static final int UTF_16 = 6;

    /** The charset of a name (or alias, in any case), 0 if unknown. */
    static int charsetOf(String name) {
        // (case-insensitive, as Charset.forName: equalsIgnoreCase follows
        // the root locale)
        if (is(name, "UTF-8") || is(name, "UTF8")) {
            return UTF_8;
        }
        if (is(name, "ISO-8859-1") || is(name, "ISO8859_1")
            || is(name, "ISO8859-1") || is(name, "LATIN1")
            || is(name, "ISO-LATIN-1") || is(name, "8859_1")) {
            return ISO_8859_1;
        }
        if (is(name, "US-ASCII") || is(name, "ASCII")) {
            return US_ASCII;
        }
        if (is(name, "UTF-16BE") || is(name, "UTF_16BE")) {
            return UTF_16BE;
        }
        if (is(name, "UTF-16LE") || is(name, "UTF_16LE")) {
            return UTF_16LE;
        }
        if (is(name, "UTF-16") || is(name, "UTF_16")) {
            return UTF_16;
        }
        return 0;
    }

    private static boolean is(String name, String charset) {
        return name.equalsIgnoreCase(charset);
    }

    /**
     * The charset of a name that the models know; for another, a failed
     * not-modelled property (Java supports many more, and throws
     * UnsupportedEncodingException for the others).
     */
    static int knownCharset(String name) {
        int charset = charsetOf(name);
        CProverRuntime.notModelledUnless(charset != 0);
        return charset;
    }

    /** Whether all the characters of {@code s} are ASCII. */
    static boolean isAscii(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (CProverString.charAt(s, i) > 127) {
                return false;
            }
        }
        return true;
    }

    /**
     * The bytes of {@code s} in the default charset of the platform: those of
     * ASCII for ASCII strings (the same in the charsets that defaults are);
     * the others are not modelled.
     */
    static byte[] encodeDefault(String s) {
        CProverRuntime.notModelledUnless(isAscii(s));
        return encode(s, US_ASCII);
    }

    /** Whether the characters at i and i + 1 of {@code s} are a pair. */
    private static boolean isPairAt(String s, int i) {
        return i + 1 < s.length()
            && Character.isHighSurrogate(CProverString.charAt(s, i))
            && Character.isLowSurrogate(CProverString.charAt(s, i + 1));
    }

    /** The bytes of {@code s} in {@code charset} (one of the above). */
    static byte[] encode(String s, int charset) {
        int l = s.length();
        if (charset == UTF_8) {
            int n = 0;
            for (int i = 0; i < l; i++) {
                char c = CProverString.charAt(s, i);
                if (c < 0x80) {
                    n += 1;
                } else if (c < 0x800) {
                    n += 2;
                } else if (isPairAt(s, i)) {
                    n += 4;
                    i++;
                } else if (Character.isSurrogate(c)) {
                    n += 1;
                } else {
                    n += 3;
                }
            }
            byte[] result = new byte[n];
            int k = 0;
            for (int i = 0; i < l; i++) {
                char c = CProverString.charAt(s, i);
                if (c < 0x80) {
                    result[k++] = (byte) c;
                } else if (c < 0x800) {
                    result[k++] = (byte) (0xc0 | (c >> 6));
                    result[k++] = (byte) (0x80 | (c & 0x3f));
                } else if (isPairAt(s, i)) {
                    int cp = Character.toCodePoint(c,
                        CProverString.charAt(s, i + 1));
                    result[k++] = (byte) (0xf0 | (cp >> 18));
                    result[k++] = (byte) (0x80 | ((cp >> 12) & 0x3f));
                    result[k++] = (byte) (0x80 | ((cp >> 6) & 0x3f));
                    result[k++] = (byte) (0x80 | (cp & 0x3f));
                    i++;
                } else if (Character.isSurrogate(c)) {
                    result[k++] = (byte) '?';
                } else {
                    result[k++] = (byte) (0xe0 | (c >> 12));
                    result[k++] = (byte) (0x80 | ((c >> 6) & 0x3f));
                    result[k++] = (byte) (0x80 | (c & 0x3f));
                }
            }
            return result;
        }
        if (charset == ISO_8859_1 || charset == US_ASCII) {
            int max = charset == ISO_8859_1 ? 0xff : 0x7f;
            // (a surrogate pair is one character that does not map: '?')
            int n = 0;
            for (int i = 0; i < l; i++) {
                if (isPairAt(s, i)) {
                    i++;
                }
                n++;
            }
            byte[] result = new byte[n];
            int k = 0;
            for (int i = 0; i < l; i++) {
                char c = CProverString.charAt(s, i);
                if (isPairAt(s, i)) {
                    result[k++] = (byte) '?';
                    i++;
                } else {
                    result[k++] = c <= max ? (byte) c : (byte) '?';
                }
            }
            return result;
        }
        // UTF-16: a surrogate that is not in a pair is U+FFFD
        int bom = charset == UTF_16 ? 2 : 0;
        byte[] result = new byte[bom + 2 * l];
        if (bom != 0) {
            result[0] = (byte) 0xfe;
            result[1] = (byte) 0xff;
        }
        for (int i = 0; i < l; i++) {
            char c = CProverString.charAt(s, i);
            if (Character.isSurrogate(c)) {
                boolean paired = isPairAt(s, i)
                    || (i > 0 && Character.isLowSurrogate(c)
                        && Character.isHighSurrogate(
                            CProverString.charAt(s, i - 1)));
                if (!paired) {
                    c = '\ufffd';
                }
            }
            int hi = bom + 2 * i;
            int lo = hi + 1;
            if (charset == UTF_16LE) {
                hi = 2 * i + 1;
                lo = 2 * i;
            }
            result[hi] = (byte) (c >> 8);
            result[lo] = (byte) (c & 0xff);
        }
        return result;
    }

    /**
     * The string of the default charset of the platform of {@code length}
     * bytes from {@code offset}: that of ASCII for ASCII bytes; the others
     * are not modelled.
     */
    static String decodeDefault(byte[] bytes, int offset, int length) {
        boolean ascii = true;
        for (int i = 0; i < length; i++) {
            if (bytes[offset + i] < 0) {
                ascii = false;
            }
        }
        CProverRuntime.notModelledUnless(ascii);
        return decode(bytes, offset, length, US_ASCII);
    }

    /** The string of {@code length} bytes from {@code offset}. */
    static String decode(byte[] bytes, int offset, int length, int charset) {
        StringBuilder result = new StringBuilder();
        if (charset == ISO_8859_1 || charset == US_ASCII) {
            for (int i = 0; i < length; i++) {
                int b = bytes[offset + i];
                if (b >= 0) {
                    result.append((char) b);
                } else if (charset == ISO_8859_1) {
                    result.append((char) (b & 0xff));
                } else {
                    result.append('\ufffd');
                }
            }
            return result.toString();
        }
        if (charset == UTF_8) {
            int i = 0;
            while (i < length) {
                int b = bytes[offset + i] & 0xff;
                if (b < 0x80) {
                    result.append((char) b);
                    i += 1;
                    continue;
                }
                int n = b >= 0xf0 ? 4 : b >= 0xe0 ? 3 : b >= 0xc0 ? 2 : 0;
                int cp = n == 4 ? b & 0x07 : n == 3 ? b & 0x0f : b & 0x1f;
                boolean wellFormed = n != 0 && i + n <= length
                    && b != 0xc0 && b != 0xc1 && b < 0xf5;
                for (int k = 1; wellFormed && k < n; k++) {
                    int c = bytes[offset + i + k] & 0xff;
                    if ((c & 0xc0) != 0x80) {
                        wellFormed = false;
                    }
                    cp = (cp << 6) | (c & 0x3f);
                }
                if (wellFormed) {
                    // (overlong forms, surrogates and values beyond the
                    // last code point are malformed)
                    int min = n == 2 ? 0x80 : n == 3 ? 0x800 : 0x10000;
                    wellFormed = cp >= min && cp <= 0x10ffff
                        && !(cp >= 0xd800 && cp <= 0xdfff);
                }
                if (!wellFormed) {
                    // (the replacements of malformed input are not modelled)
                    return CProver.nondetWithoutNull("");
                }
                result.appendCodePoint(cp);
                i += n;
            }
            return result.toString();
        }
        // UTF-16
        boolean bigEndian = charset != UTF_16LE;
        int start = 0;
        if (charset == UTF_16 && length >= 2) {
            int b0 = bytes[offset] & 0xff;
            int b1 = bytes[offset + 1] & 0xff;
            if (b0 == 0xfe && b1 == 0xff) {
                start = 2;
            } else if (b0 == 0xff && b1 == 0xfe) {
                start = 2;
                bigEndian = false;
            }
        }
        if ((length - start) % 2 != 0) {
            return CProver.nondetWithoutNull("");
        }
        for (int i = start; i < length; i += 2) {
            int b0 = bytes[offset + i] & 0xff;
            int b1 = bytes[offset + i + 1] & 0xff;
            char c = bigEndian ? (char) ((b0 << 8) | b1)
                               : (char) ((b1 << 8) | b0);
            result.append(c);
        }
        String s = result.toString();
        // (surrogates that are not in pairs are replaced: not modelled)
        for (int i = 0; i < s.length(); i++) {
            char c = CProverString.charAt(s, i);
            if (Character.isHighSurrogate(c) && isPairAt(s, i)) {
                i++;
            } else if (Character.isSurrogate(c)) {
                return CProver.nondetWithoutNull("");
            }
        }
        return s;
    }
}
