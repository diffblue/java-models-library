package java.lang;

import org.cprover.CProver;
import org.cprover.CProverString;

/**
 * DIFFBLUE MODEL LIBRARY
 * The exceptions of the index checks of String, AbstractStringBuilder and
 * jdk.internal.util.Preconditions, with the messages of the version of the
 * runtime (CProverRuntime.version): Java 8 builds them in each method (see
 * the callers), Java 9 to 17 in String and AbstractStringBuilder (with a
 * space after the comma from Java 14), Java 18 and later in Preconditions.
 * Measured with scripts/java-version-probe on Java 8 to 25.
 */
final class CProverChecks {

    private CProverChecks() {
    }

    /** Whether the runtime is Java 8, whose messages the callers build. */
    static boolean java8() {
        return CProverRuntime.version() <= 8;
    }

    // The kinds of the messages that Throwable.getMessage makes when it is
    // first asked for (see Throwable.cproverJvmKind): building them where
    // the exception is thrown made the string solver convert the numbers of
    // every index check that symex could not decide (String.format of a
    // constant took 30 s instead of 4 s).
    static final int JAVA8_INDEX = 6;
    static final int INDEX = 7;
    static final int OFFSET = 8;
    static final int START_END = 9;
    static final int BEGIN_END = 10;
    static final int OFFSET_COUNT = 11;
    static final int RANGE = 12;
    static final int FOR_INPUT_STRING = 13;
    static final int RADIX_LOW = 14;
    static final int RADIX_HIGH = 15;

    /** Sets the kind and numbers of the message of {@code e}. */
    private static <T extends Throwable> T lazy(T e, int kind, int a, int b,
                                                int length) {
        e.cproverJvmKind = kind;
        e.cproverJvmIndex = a;
        e.cproverJvmCount = b;
        e.cproverJvmLength = length;
        return e;
    }

    /**
     * A StringIndexOutOfBoundsException with the message of {@code kind}
     * (one of the constants above) of {@code a}, {@code b} and {@code
     * length}, made when it is asked for.
     */
    static StringIndexOutOfBoundsException stringIndex(int kind, int a, int b,
                                                       int length) {
        return lazy(new StringIndexOutOfBoundsException(), kind, a, b, length);
    }

    /** An IndexOutOfBoundsException, as {@link #stringIndex}. */
    static IndexOutOfBoundsException index(int kind, int a, int b,
                                           int length) {
        return lazy(new IndexOutOfBoundsException(), kind, a, b, length);
    }

    /**
     * The message of {@code kind} (Throwable.getMessage), measured on Java 8
     * to 25: String.checkIndex and checkOffset (with a space after the comma
     * from Java 14), AbstractStringBuilder.checkRange (START_END),
     * String.checkBoundsBeginEnd and checkBoundsOffCount up to Java 17, and
     * those of jdk.internal.util.Preconditions from Java 18.
     *
     * Each kind has one concatenation, whose constant pieces the version
     * chooses: where symex merges the versions (no --java-version), a
     * concatenation per version made the string solver encode the
     * conversions and concatenations of each (a substring check took 170 s
     * instead of 10 s).
     */
    static String lazyMessage(int kind, int a, int b, int length,
                              String string) {
        int v = CProverRuntime.version();
        String comma = v <= 13 ? "," : ", ";
        boolean preconditions = v >= 18;
        if (kind == FOR_INPUT_STRING) {
            if (a != 10 && v >= 12) {
                return "For input string: \"" + string + "\" under radix "
                    + a;
            }
            return "For input string: \"" + string + "\"";
        }
        if (kind == RADIX_LOW) {
            return "radix " + a + " less than Character.MIN_RADIX";
        }
        if (kind == RADIX_HIGH) {
            return "radix " + a + " greater than Character.MAX_RADIX";
        }
        if (kind == JAVA8_INDEX) {
            return "String index out of range: " + a;
        }
        if (kind == INDEX) {
            return (preconditions ? "Index " : "index ") + a
                + (preconditions ? " out of bounds for length "
                   : comma + "length ") + length;
        }
        if (kind == OFFSET) {
            return (preconditions ? "Range [" : "offset ") + a
                + (preconditions ? ", " + length + ") out of bounds for length "
                   : comma + "length ") + length;
        }
        if (kind == OFFSET_COUNT) {
            if (preconditions) {
                return "Range [" + a + ", " + a + " + " + b
                    + ") out of bounds for length " + length;
            }
            return "offset " + a + ", count " + b + ", length " + length;
        }
        boolean range = preconditions || kind == RANGE;
        String first = range ? "Range ["
            : kind == START_END ? "start " : "begin ";
        return first + a + (range ? ", " : ", end ") + b
            + (range ? ") out of bounds for length " : ", length ") + length;
    }

    /**
     * The checks of String.substring, subSequence, getChars and getBytes of
     * the range {@code [begin, end)} of a string of {@code length} chars:
     * Java 8 names one bound in the message (or the negative length of the
     * range), Java 9 to 17 String.checkBoundsBeginEnd, Java 18 and later
     * Preconditions.checkFromToIndex.
     */
    static void checkBoundsBeginEnd(int begin, int end, int length) {
        if (java8()) {
            if (begin < 0) {
                throw new StringIndexOutOfBoundsException(begin);
            }
            if (end > length) {
                throw new StringIndexOutOfBoundsException(end);
            }
            if (begin > end) {
                throw new StringIndexOutOfBoundsException(end - begin);
            }
        } else if (outOfRange(begin, end, length)) {
            throw stringIndex(BEGIN_END, begin, end, length);
        }
    }

    /**
     * String.checkBoundsOffCount (Java 9 to 17) and
     * Preconditions.checkFromIndexSize with a
     * StringIndexOutOfBoundsException (Java 18 and later): whether {@code
     * count} elements from {@code offset} are within {@code length}.
     */
    static void checkBoundsOffCount(int offset, int count, int length) {
        if (offset < 0 || count < 0 || offset > length - count) {
            throw stringIndex(OFFSET_COUNT, offset, count, length);
        }
    }

    /** Whether {@code [start, end)} is not a range of {@code length}. */
    static boolean outOfRange(int start, int end, int length) {
        return start < 0 || start > end || end > length;
    }

    /** String.checkIndex and AbstractStringBuilder's (Java 9 and later). */
    static void checkIndex(int index, int length) {
        if (index < 0 || index >= length) {
            throw stringIndex(INDEX, index, 0, length);
        }
    }

    /** String.checkOffset (Java 9 and later). */
    static void checkOffset(int offset, int length) {
        if (offset < 0 || offset > length) {
            throw stringIndex(OFFSET, offset, 0, length);
        }
    }

    /**
     * AbstractStringBuilder.checkRangeSIOOBE (Java 9 to 17) and
     * Preconditions.checkFromToIndex with a StringIndexOutOfBoundsException
     * (Java 18 and later).
     */
    static void checkRangeSIOOBE(int start, int end, int length) {
        if (outOfRange(start, end, length)) {
            throw stringIndex(START_END, start, end, length);
        }
    }

    /**
     * AbstractStringBuilder.checkRange (Java 9 to 17) and
     * Preconditions.checkFromToIndex with an IndexOutOfBoundsException
     * (Java 18 and later).
     */
    static void checkRange(int start, int end, int length) {
        if (outOfRange(start, end, length)) {
            throw index(START_END, start, end, length);
        }
    }

    /**
     * The message of the NegativeArraySizeException of an array of {@code
     * size} elements: null up to Java 10, the size from Java 11 (and null
     * again where Java 11 and later allocate the bytes of a string with
     * compact strings off, which a JVM option may choose).
     */
    static String negativeSizeMessage(int size) {
        if (CProverRuntime.version() <= 10 || CProver.nondetBoolean()) {
            return null;
        }
        return "" + size;
    }

    /** The upper-case hexadecimal digits of {@code value}. */
    static String upperHex(int value) {
        // (by arithmetic, which symex folds for a constant value)
        String result = "";
        int v = value;
        for (int i = 0; i < 8; i++) {
            result = CProverString.charAt("0123456789ABCDEF", v & 15) + result;
            v >>>= 4;
            if (v == 0) {
                break;
            }
        }
        return result;
    }

    /**
     * The IllegalArgumentException of Character.toChars for a code point
     * that is not valid: no message up to Java 10.
     */
    static IllegalArgumentException invalidCodePoint(int codePoint) {
        if (CProverRuntime.version() <= 10) {
            return new IllegalArgumentException();
        }
        return new IllegalArgumentException(
            "Not a valid Unicode code point: 0x" + upperHex(codePoint));
    }

    /**
     * The NumberFormatException of the parse of a null string: "null" up
     * to Java 16, "Cannot parse null string" from 17.
     */
    static NumberFormatException nullNumberString() {
        if (CProverRuntime.version() <= 16) {
            return new NumberFormatException("null");
        }
        return new NumberFormatException("Cannot parse null string");
    }

    /**
     * NumberFormatException.forInputString: from Java 12 with the radix
     * where it is not 10 (the message is made when it is asked for).
     */
    static NumberFormatException forInputString(String s, int radix) {
        NumberFormatException e = new NumberFormatException();
        e.cproverJvmKind = FOR_INPUT_STRING;
        e.cproverJvmIndex = radix;
        e.cproverJvmString = s;
        return e;
    }

    /** The checks of the radix of the parse methods. */
    static void checkRadix(int radix) {
        if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX) {
            NumberFormatException e = new NumberFormatException();
            e.cproverJvmKind =
                radix < Character.MIN_RADIX ? RADIX_LOW : RADIX_HIGH;
            e.cproverJvmIndex = radix;
            throw e;
        }
    }
}
