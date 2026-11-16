package java.lang;

import org.cprover.CProver;
import org.cprover.CProverString;

/**
 * DIFFBLUE MODEL LIBRARY
 * Double.parseDouble and Float.parseFloat after FloatingDecimal: the grammar
 * of Double.valueOf, the NumberFormatException of each version for a string
 * outside it, the exact value of a decimal of few digits, and for other
 * strings of the grammar any value of their sign. (The models gave any value
 * and never threw.)
 */
final class CProverFloatParse {

    private CProverFloatParse() {
    }

    /** What parse found, besides the value. */
    private static boolean negative;
    private static boolean exact;
    private static long digits;
    private static int exponent;
    private static int kind; // 0 number, 1 NaN, 2 Infinity
    private static boolean multiplePoints;

    /** Whether {@code c} is a digit of the radix (10 or 16). */
    private static boolean isDigit(char c, boolean hex) {
        return (c >= '0' && c <= '9')
            || (hex && ((c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F')));
    }

    /**
     * Whether {@code s} is in the grammar of Double.valueOf after
     * String.trim; sets the fields above.
     */
    private static boolean parse(String s) {
        int n = s.length();
        int i = 0;
        negative = false;
        exact = false;
        digits = 0;
        exponent = 0;
        kind = 0;
        multiplePoints = false;
        if (i < n && (CProverString.charAt(s, i) == '+'
                      || CProverString.charAt(s, i) == '-')) {
            negative = CProverString.charAt(s, i) == '-';
            i++;
        }
        if (s.startsWith("NaN", i) && n == i + 3) {
            kind = 1;
            return true;
        }
        if (s.startsWith("Infinity", i) && n == i + 8) {
            kind = 2;
            return true;
        }
        boolean hex = i + 1 < n && CProverString.charAt(s, i) == '0'
            && (CProverString.charAt(s, i + 1) == 'x'
                || CProverString.charAt(s, i + 1) == 'X');
        if (hex) {
            i += 2;
        }
        int significant = 0;
        int mantissaDigits = 0;
        boolean point = false;
        int fraction = 0;
        while (i < n) {
            char c = CProverString.charAt(s, i);
            if (isDigit(c, hex)) {
                mantissaDigits++;
                if (!hex && (digits != 0 || c != '0')) {
                    significant++;
                    if (significant <= 15) {
                        digits = digits * 10 + (c - '0');
                        if (point) {
                            fraction++;
                        }
                    } else if (!point) {
                        // (a digit beyond those kept: the value is not exact)
                        fraction--;
                    }
                } else if (!hex && point) {
                    fraction++;
                }
            } else if (c == '.' && !point) {
                point = true;
            } else if (c == '.' && (!hex || CProverRuntime.version() >= 25)) {
                // (FloatingDecimal throws as it scans the digits, from Java 25
                // also those of a hexadecimal string)
                multiplePoints = true;
                return false;
            } else {
                break;
            }
            i++;
        }
        if (mantissaDigits == 0) {
            return false;
        }
        int exp = 0;
        boolean hasExponent = false;
        if (i < n) {
            char c = CProverString.charAt(s, i);
            if ((!hex && (c == 'e' || c == 'E'))
                || (hex && (c == 'p' || c == 'P'))) {
                hasExponent = true;
                i++;
                boolean expNegative = false;
                if (i < n && (CProverString.charAt(s, i) == '+'
                              || CProverString.charAt(s, i) == '-')) {
                    expNegative = CProverString.charAt(s, i) == '-';
                    i++;
                }
                int expDigits = 0;
                while (i < n && isDigit(CProverString.charAt(s, i), false)) {
                    if (exp < 10000) {
                        exp = exp * 10 + (CProverString.charAt(s, i) - '0');
                    }
                    expDigits++;
                    i++;
                }
                if (expDigits == 0) {
                    return false;
                }
                if (expNegative) {
                    exp = -exp;
                }
            }
        }
        if (hex && !hasExponent) {
            return false;
        }
        if (i < n) {
            char c = CProverString.charAt(s, i);
            if (c == 'f' || c == 'F' || c == 'd' || c == 'D') {
                i++;
            }
        }
        if (i != n) {
            return false;
        }
        exponent = exp - fraction;
        exact = !hex && significant <= 15 && exponent >= -22
            && exponent <= 22;
        return true;
    }

    /** The exception of a string outside the grammar, of each version. */
    private static NumberFormatException error(String original,
                                               String trimmed) {
        if (trimmed.length() == 0) {
            return new NumberFormatException("empty String");
        }
        if (multiplePoints) {
            return new NumberFormatException("multiple points");
        }
        // (Java 25 names the string as given, the others as trimmed)
        String named = CProverRuntime.version() >= 25 ? original : trimmed;
        return new NumberFormatException(
            "For input string: \"" + named + "\"");
    }

    /** Ten to the power of {@code k}, from 0 to 22 (exact doubles). */
    private static double tenTo(int k) {
        double r = 1.0;
        for (int i = 0; i < k; i++) {
            r *= 10.0;
        }
        return r;
    }

    static double parseDouble(String s) {
        String trimmed = s.trim();
        if (!parse(trimmed)) {
            throw error(s, trimmed);
        }
        if (kind == 1) {
            return Double.NaN;
        }
        if (kind == 2) {
            return negative ? Double.NEGATIVE_INFINITY
                            : Double.POSITIVE_INFINITY;
        }
        double value;
        if (exact) {
            // (one rounding: the digits and the power of ten are exact)
            value = exponent >= 0 ? (double) digits * tenTo(exponent)
                                  : (double) digits / tenTo(-exponent);
        } else {
            // (any value of the sign: not modelled exactly)
            value = CProver.nondetDouble();
            CProver.assume(!Double.isNaN(value) && value >= 0.0);
        }
        return negative ? -value : value;
    }

    static float parseFloat(String s) {
        String trimmed = s.trim();
        if (!parse(trimmed)) {
            throw error(s, trimmed);
        }
        if (kind == 1) {
            return Float.NaN;
        }
        if (kind == 2) {
            return negative ? Float.NEGATIVE_INFINITY
                            : Float.POSITIVE_INFINITY;
        }
        float value;
        if (exact && digits < (1L << 24) && exponent >= -10
            && exponent <= 10) {
            float power = (float) tenTo(exponent >= 0 ? exponent : -exponent);
            value = exponent >= 0 ? (float) digits * power
                                  : (float) digits / power;
        } else {
            value = CProver.nondetFloat();
            CProver.assume(!Float.isNaN(value) && value >= 0.0f);
        }
        return negative ? -value : value;
    }
}
