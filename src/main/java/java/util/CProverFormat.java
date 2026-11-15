package java.util;

import org.cprover.CProver;
import org.cprover.CProverString;

/**
 * DIFFBLUE MODEL LIBRARY
 * Helpers of the model of String.format, in java.util for the exceptions
 * whose constructors are package-private.
 */
public final class CProverFormat {

    private CProverFormat() {
    }

    /** The int of the two characters of {@code s} at {@code i}. */
    private static int intAt(String s, int i) {
        return (CProverString.charAt(s, i) << 16)
            | CProverString.charAt(s, i + 1);
    }

    /**
     * The argument of an error, whose index (from 1) is at {@code i} of
     * {@code error}: any error is possible for a format string that is not
     * constant, and those that are not are excluded.
     */
    private static Object argument(String error, int i, Object[] args) {
        int index = intAt(error, i);
        CProver.assume(index >= 1 && index <= args.length);
        return args[index - 1];
    }

    /**
     * Whether Formatter prints {@code arg} with the conversion {@code c}
     * (not a date/time one) rather than throwing an
     * IllegalFormatConversionException.
     */
    private static boolean fits(char c, Object arg) {
        if (arg == null) {
            return true;
        }
        boolean integral = arg instanceof Byte || arg instanceof Short
            || arg instanceof Integer || arg instanceof Long;
        switch (c) {
        case 'c':
            return arg instanceof Character || arg instanceof Byte
                || arg instanceof Short || arg instanceof Integer;
        case 'd':
        case 'o':
        case 'x':
            return integral || arg instanceof java.math.BigInteger;
        case 'e':
        case 'f':
        case 'g':
            return arg instanceof Float || arg instanceof Double
                || arg instanceof java.math.BigDecimal;
        case 'a':
            return arg instanceof Float || arg instanceof Double;
        default:
            return true;
        }
    }

    /**
     * Throws the exception of an error of String.cproverFormatError, as
     * Formatter of JDK 17 and later does, with the arguments
     * {@code args}. The error is its code and its detail:
     * <ul>
     * <li>'U' UnknownFormatConversionException, the conversion;</li>
     * <li>'I' IllegalFormatArgumentIndexException, the index (as two
     *     characters, the most significant 16 bits first);</li>
     * <li>'D' DuplicateFormatFlagsException, the flag;</li>
     * <li>'W' IllegalFormatWidthException, the width (two
     *     characters);</li>
     * <li>'P' IllegalFormatPrecisionException, the precision (two
     *     characters);</li>
     * <li>'F' IllegalFormatFlagsException, the flags;</li>
     * <li>'X' FormatFlagsConversionMismatchException, the conversion, the
     *     argument (0 for an error of the parse) and the flags;</li>
     * <li>'M' MissingFormatWidthException, the specifier;</li>
     * <li>'A' MissingFormatArgumentException, the specifier;</li>
     * <li>'C' IllegalFormatConversionException, the conversion and the
     *     argument (from 1, two characters);</li>
     * <li>'T' IllegalFormatConversionException of a date/time, its field
     *     and the argument;</li>
     * <li>'K' IllegalFormatCodePointException, the argument;</li>
     * <li>'Z' ArithmeticException of the `,` of a locale without grouping,
     *     the argument (beyond the tenth, not checked).</li>
     * </ul>
     * The errors of an argument are those of the argument itself here.
     */
    public static void throwError(String error, Object[] args) {
        // One site for each exception, so that the paths share their
        // objects, and the string functions of CProverString, which do not
        // throw: the error is well formed where it is constant, and any
        // error is possible where it is not.
        char code = CProverString.charAt(error, 0);
        if (code == 'C' || code == 'T') {
            char c = CProverString.charAt(error, 1);
            Object arg = argument(error, 2, args);
            if (code == 'C') {
                CProver.assume(!fits(c, arg));
            } else {
                // A TemporalAccessor without the field throws too.
                CProver.assume(arg != null && !(arg instanceof Long)
                    && !(arg instanceof Date) && !(arg instanceof Calendar));
            }
            throw new IllegalFormatConversionException(c, arg.getClass());
        }
        if (code == 'X') {
            char c = CProverString.charAt(error, 1);
            if (intAt(error, 2) != 0) {
                Object arg = argument(error, 2, args);
                if (c == 's') {
                    CProver.assume(!(arg instanceof Formattable));
                } else {
                    CProver.assume(arg instanceof Byte || arg instanceof Short
                        || arg instanceof Integer || arg instanceof Long);
                }
            }
            throw new FormatFlagsConversionMismatchException(
                CProverString.substring(error, 4, error.length()), c);
        }
        if (code == 'K') {
            Object arg = argument(error, 1, args);
            int c;
            if (arg instanceof Byte) {
                c = (Byte) arg;
            } else if (arg instanceof Short) {
                c = (Short) arg;
            } else {
                CProver.assume(arg instanceof Integer);
                c = (Integer) arg;
            }
            CProver.assume(c < 0 || c > Character.MAX_CODE_POINT);
            throw new IllegalFormatCodePointException(c);
        }
        if (code == 'I' || code == 'W' || code == 'P') {
            int value = intAt(error, 1);
            if (code == 'I') {
                throw new IllegalFormatArgumentIndexException(value);
            }
            if (code == 'W') {
                throw new IllegalFormatWidthException(value);
            }
            throw new IllegalFormatPrecisionException(value);
        }
        String detail = CProverString.substring(error, 1, error.length());
        if (code == 'U') {
            throw new UnknownFormatConversionException(detail);
        }
        if (code == 'D') {
            throw new DuplicateFormatFlagsException(detail);
        }
        if (code == 'F') {
            throw new IllegalFormatFlagsException(detail);
        }
        if (code == 'M') {
            throw new MissingFormatWidthException(detail);
        }
        if (code == 'Z') {
            // the `,` of a locale without grouping in Java 8 (CLDR), 9 and
            // 10, which divide by its grouping size
            throw new ArithmeticException("/ by zero");
        }
        CProver.assume(code == 'A');
        throw new MissingFormatArgumentException(detail);
    }
}
