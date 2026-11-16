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

    /** The message of String.checkIndex (Java 9 and later). */
    static String indexMessage(int index, int length) {
        int v = CProverRuntime.version();
        if (v <= 13) {
            return "index " + index + ",length " + length;
        }
        if (v <= 17) {
            return "index " + index + ", length " + length;
        }
        return "Index " + index + " out of bounds for length " + length;
    }

    /** The message of String.checkOffset (Java 9 and later). */
    static String offsetMessage(int offset, int length) {
        int v = CProverRuntime.version();
        if (v <= 13) {
            return "offset " + offset + ",length " + length;
        }
        if (v <= 17) {
            return "offset " + offset + ", length " + length;
        }
        return rangeMessage(offset, length, length);
    }

    /**
     * The message of AbstractStringBuilder.checkRange and checkRangeSIOOBE
     * (Java 9 to 17) and of Preconditions.checkFromToIndex (Java 18 and
     * later).
     */
    static String startEndMessage(int start, int end, int length) {
        if (CProverRuntime.version() <= 17) {
            return "start " + start + ", end " + end + ", length " + length;
        }
        return rangeMessage(start, end, length);
    }

    /**
     * The message of String.checkBoundsBeginEnd (Java 9 to 17) and of
     * Preconditions.checkFromToIndex (Java 18 and later).
     */
    static String beginEndMessage(int begin, int end, int length) {
        if (CProverRuntime.version() <= 17) {
            return "begin " + begin + ", end " + end + ", length " + length;
        }
        return rangeMessage(begin, end, length);
    }

    /**
     * The message of String.checkBoundsOffCount (Java 9 to 17) and of
     * Preconditions.checkFromIndexSize (Java 18 and later).
     */
    static String offsetCountMessage(int offset, int count, int length) {
        if (CProverRuntime.version() <= 17) {
            return "offset " + offset + ", count " + count + ", length "
                + length;
        }
        return "Range [" + offset + ", " + offset + " + " + count
            + ") out of bounds for length " + length;
    }

    /** The message of Preconditions.checkFromToIndex. */
    static String rangeMessage(int from, int to, int length) {
        return "Range [" + from + ", " + to + ") out of bounds for length "
            + length;
    }

    /** Whether {@code [start, end)} is not a range of {@code length}. */
    static boolean outOfRange(int start, int end, int length) {
        return start < 0 || start > end || end > length;
    }

    /** String.checkIndex and AbstractStringBuilder's (Java 9 and later). */
    static void checkIndex(int index, int length) {
        if (index < 0 || index >= length) {
            throw new StringIndexOutOfBoundsException(
                indexMessage(index, length));
        }
    }

    /** String.checkOffset (Java 9 and later). */
    static void checkOffset(int offset, int length) {
        if (offset < 0 || offset > length) {
            throw new StringIndexOutOfBoundsException(
                offsetMessage(offset, length));
        }
    }

    /**
     * AbstractStringBuilder.checkRangeSIOOBE (Java 9 to 17) and
     * Preconditions.checkFromToIndex with a StringIndexOutOfBoundsException
     * (Java 18 and later).
     */
    static void checkRangeSIOOBE(int start, int end, int length) {
        if (outOfRange(start, end, length)) {
            throw new StringIndexOutOfBoundsException(
                startEndMessage(start, end, length));
        }
    }

    /**
     * AbstractStringBuilder.checkRange (Java 9 to 17) and
     * Preconditions.checkFromToIndex with an IndexOutOfBoundsException
     * (Java 18 and later).
     */
    static void checkRange(int start, int end, int length) {
        if (outOfRange(start, end, length)) {
            throw new IndexOutOfBoundsException(
                startEndMessage(start, end, length));
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
}
