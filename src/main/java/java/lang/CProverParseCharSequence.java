package java.lang;

/**
 * DIFFBLUE MODEL LIBRARY
 * Integer.parseInt, Integer.parseUnsignedInt, Long.parseLong and
 * Long.parseUnsignedLong of a range of a CharSequence (Java 9 and later),
 * with the JDK's code of each version: Java 9 to 21 (that of Java 18, with
 * the messages of the earlier versions), and Java 22 and later, which read
 * each char of the sequence at most once and name another index in their
 * errors. The messages are those of the JDK (String.format of %s is the
 * string of the argument).
 */
final class CProverParseCharSequence {

    private CProverParseCharSequence() {
    }

    /** Objects.requireNonNull and the check of the range. */
    private static void checkRange(CharSequence s, int beginIndex,
                                   int endIndex) {
        // Objects.requireNonNull(s)
        if (s == null) {
            throw new NullPointerException();
        }
        int length = s.length();
        if (beginIndex < 0 || beginIndex > endIndex || endIndex > length) {
            if (CProverRuntime.version() <= 17) {
                throw new IndexOutOfBoundsException();
            }
            // Objects.checkFromToIndex
            throw CProverChecks.index(CProverChecks.RANGE, beginIndex,
                endIndex, length);
        }
    }

    /** NumberFormatException.forCharSequence. */
    private static NumberFormatException forCharSequence(CharSequence s,
            int beginIndex, int endIndex, int errorIndex) {
        return new NumberFormatException("Error at index "
                + (errorIndex - beginIndex) + " in: \""
                + s.subSequence(beginIndex, endIndex) + "\"");
    }

    static int parseInt(CharSequence s, int beginIndex, int endIndex,
                        int radix) {
        if (CProverRuntime.version() >= 22) {
            return parseInt22(s, beginIndex, endIndex, radix);
        }
        checkRange(s, beginIndex, endIndex);
        CProverChecks.checkRadix(radix);

        boolean negative = false;
        int i = beginIndex;
        int limit = -Integer.MAX_VALUE;

        if (i < endIndex) {
            char firstChar = s.charAt(i);
            if (firstChar < '0') { // Possible leading "+" or "-"
                if (firstChar == '-') {
                    negative = true;
                    limit = Integer.MIN_VALUE;
                } else if (firstChar != '+') {
                    throw forCharSequence(s, beginIndex, endIndex, i);
                }
                i++;
                if (i == endIndex) { // Cannot have lone "+" or "-"
                    throw forCharSequence(s, beginIndex, endIndex, i);
                }
            }
            int multmin = limit / radix;
            int result = 0;
            while (i < endIndex) {
                int digit = Character.digit(s.charAt(i), radix);
                if (digit < 0 || result < multmin) {
                    throw forCharSequence(s, beginIndex, endIndex, i);
                }
                result *= radix;
                if (result < limit + digit) {
                    throw forCharSequence(s, beginIndex, endIndex, i);
                }
                i++;
                result -= digit;
            }
            return negative ? result : -result;
        } else {
            // (without the radix up to Java 11)
            throw CProverChecks.forInputString("", radix);
        }
    }

    private static int parseInt22(CharSequence s, int beginIndex,
                                  int endIndex, int radix) {
        checkRange(s, beginIndex, endIndex);
        CProverChecks.checkRadix(radix);

        if (beginIndex == endIndex) {
            throw CProverChecks.forInputString("", radix);
        }
        int digit = ~0xFF;
        int i = beginIndex;
        char firstChar = s.charAt(i++);
        if (firstChar != '-' && firstChar != '+') {
            digit = Character.digit(firstChar, radix);
        }
        if (digit >= 0 || digit == ~0xFF && endIndex - beginIndex > 1) {
            int limit = firstChar != '-' ? Integer.MIN_VALUE + 1
                : Integer.MIN_VALUE;
            int multmin = limit / radix;
            int result = -(digit & 0xFF);
            boolean inRange = true;
            /* Accumulating negatively avoids surprises near MAX_VALUE */
            while (i < endIndex
                    && (digit = Character.digit(s.charAt(i++), radix)) >= 0
                    && (inRange = result > multmin
                        || result == multmin
                           && digit <= radix * multmin - limit)) {
                result = radix * result - digit;
            }
            if (inRange && i == endIndex && digit >= 0) {
                return firstChar != '-' ? -result : result;
            }
        }
        throw forCharSequence(s, beginIndex, endIndex,
            i - (digit < -1 ? 0 : 1));
    }

    static int parseUnsignedInt(CharSequence s, int beginIndex, int endIndex,
                                int radix) {
        if (CProverRuntime.version() >= 22) {
            return parseUnsignedInt22(s, beginIndex, endIndex, radix);
        }
        checkRange(s, beginIndex, endIndex);

        int start = beginIndex, len = endIndex - beginIndex;

        if (len > 0) {
            char firstChar = s.charAt(start);
            if (firstChar == '-') {
                throw new NumberFormatException(
                    "Illegal leading minus sign on unsigned string " + s
                    + ".");
            } else {
                if (len <= 5 || // Integer.MAX_VALUE in Character.MAX_RADIX is 6 digits
                        (radix == 10 && len <= 9)) { // Integer.MAX_VALUE in base 10 is 10 digits
                    return parseInt(s, start, start + len, radix);
                } else {
                    long ell = parseLong(s, start, start + len, radix);
                    if ((ell & 0xffff_ffff_0000_0000L) == 0) {
                        return (int) ell;
                    } else {
                        throw new NumberFormatException("String value " + s
                            + " exceeds range of unsigned int.");
                    }
                }
            }
        } else {
            throw new NumberFormatException("");
        }
    }

    private static int parseUnsignedInt22(CharSequence s, int beginIndex,
                                          int endIndex, int radix) {
        checkRange(s, beginIndex, endIndex);
        CProverChecks.checkRadix(radix);

        if (beginIndex == endIndex) {
            throw CProverChecks.forInputString("", radix);
        }
        int i = beginIndex;
        char firstChar = s.charAt(i++);
        if (firstChar == '-') {
            throw new NumberFormatException(
                "Illegal leading minus sign on unsigned string " + s + ".");
        }
        int digit = ~0xFF;
        if (firstChar != '+') {
            digit = Character.digit(firstChar, radix);
        }
        if (digit >= 0 || digit == ~0xFF && endIndex - beginIndex > 1) {
            int multmax = Integer.divideUnsigned(-1, radix);  // -1 is max unsigned int
            int result = digit & 0xFF;
            boolean inRange = true;
            while (i < endIndex
                    && (digit = Character.digit(s.charAt(i++), radix)) >= 0
                    && (inRange = Integer.compareUnsigned(result, multmax) < 0
                        || result == multmax && digit < -radix * multmax)) {
                result = radix * result + digit;
            }
            if (inRange && i == endIndex && digit >= 0) {
                return result;
            }
        }
        if (digit < 0) {
            throw forCharSequence(s, beginIndex, endIndex,
                i - (digit < -1 ? 0 : 1));
        }
        throw new NumberFormatException(
            "String value " + s + " exceeds range of unsigned int.");
    }

    static long parseLong(CharSequence s, int beginIndex, int endIndex,
                          int radix) {
        if (CProverRuntime.version() >= 22) {
            return parseLong22(s, beginIndex, endIndex, radix);
        }
        checkRange(s, beginIndex, endIndex);
        CProverChecks.checkRadix(radix);

        boolean negative = false;
        int i = beginIndex;
        long limit = -Long.MAX_VALUE;

        if (i < endIndex) {
            char firstChar = s.charAt(i);
            if (firstChar < '0') { // Possible leading "+" or "-"
                if (firstChar == '-') {
                    negative = true;
                    limit = Long.MIN_VALUE;
                } else if (firstChar != '+') {
                    throw forCharSequence(s, beginIndex, endIndex, i);
                }
                i++;
            }
            if (i >= endIndex) { // Cannot have lone "+", "-" or ""
                throw forCharSequence(s, beginIndex, endIndex, i);
            }
            long multmin = limit / radix;
            long result = 0;
            while (i < endIndex) {
                // Accumulating negatively avoids surprises near MAX_VALUE
                int digit = Character.digit(s.charAt(i), radix);
                if (digit < 0 || result < multmin) {
                    throw forCharSequence(s, beginIndex, endIndex, i);
                }
                result *= radix;
                if (result < limit + digit) {
                    throw forCharSequence(s, beginIndex, endIndex, i);
                }
                i++;
                result -= digit;
            }
            return negative ? result : -result;
        } else {
            throw new NumberFormatException("");
        }
    }

    private static long parseLong22(CharSequence s, int beginIndex,
                                    int endIndex, int radix) {
        checkRange(s, beginIndex, endIndex);
        CProverChecks.checkRadix(radix);

        if (beginIndex == endIndex) {
            throw CProverChecks.forInputString("", radix);
        }
        int digit = ~0xFF;  // ~0xFF means firstChar char is sign
        int i = beginIndex;
        char firstChar = s.charAt(i++);
        if (firstChar != '-' && firstChar != '+') {
            digit = Character.digit(firstChar, radix);
        }
        if (digit >= 0 || digit == ~0xFF && endIndex - beginIndex > 1) {
            long limit = firstChar != '-' ? Long.MIN_VALUE + 1
                : Long.MIN_VALUE;
            long multmin = limit / radix;
            long result = -(digit & 0xFF);
            boolean inRange = true;
            while (i < endIndex
                    && (digit = Character.digit(s.charAt(i++), radix)) >= 0
                    && (inRange = result > multmin
                        || result == multmin
                           && digit <= (int) (radix * multmin - limit))) {
                result = radix * result - digit;
            }
            if (inRange && i == endIndex && digit >= 0) {
                return firstChar != '-' ? -result : result;
            }
        }
        throw forCharSequence(s, beginIndex, endIndex,
            i - (digit < -1 ? 0 : 1));
    }

    static long parseUnsignedLong(CharSequence s, int beginIndex,
                                  int endIndex, int radix) {
        if (CProverRuntime.version() >= 22) {
            return parseUnsignedLong22(s, beginIndex, endIndex, radix);
        }
        checkRange(s, beginIndex, endIndex);

        int start = beginIndex, len = endIndex - beginIndex;

        if (len > 0) {
            char firstChar = s.charAt(start);
            if (firstChar == '-') {
                throw new NumberFormatException(
                    "Illegal leading minus sign on unsigned string "
                    + s.subSequence(start, start + len) + ".");
            } else {
                if (len <= 12 || // Long.MAX_VALUE in Character.MAX_RADIX is 13 digits
                    (radix == 10 && len <= 18) ) { // Long.MAX_VALUE in base 10 is 19 digits
                    return parseLong(s, start, start + len, radix);
                }

                long first = parseLong(s, start, start + len - 1, radix);
                int second = Character.digit(s.charAt(start + len - 1), radix);
                if (second < 0) {
                    throw new NumberFormatException("Bad digit at end of "
                        + s.subSequence(start, start + len));
                }
                long result = first * radix + second;

                // (see the JDK for why this tests overflow)
                int guard = radix * (int) (first >>> 57);
                if (guard >= 128 ||
                        (result >= 0 && guard >= 128 - Character.MAX_RADIX)) {
                    throw new NumberFormatException("String value "
                        + s.subSequence(start, start + len)
                        + " exceeds range of unsigned long.");
                }
                return result;
            }
        } else {
            // (without the radix up to Java 11)
            throw CProverChecks.forInputString("", radix);
        }
    }

    private static long parseUnsignedLong22(CharSequence s, int beginIndex,
                                            int endIndex, int radix) {
        checkRange(s, beginIndex, endIndex);
        CProverChecks.checkRadix(radix);

        if (beginIndex == endIndex) {
            throw CProverChecks.forInputString("", radix);
        }
        int i = beginIndex;
        char firstChar = s.charAt(i++);
        if (firstChar == '-') {
            throw new NumberFormatException(
                "Illegal leading minus sign on unsigned string " + s + ".");
        }
        int digit = ~0xFF;
        if (firstChar != '+') {
            digit = Character.digit(firstChar, radix);
        }
        if (digit >= 0 || digit == ~0xFF && endIndex - beginIndex > 1) {
            long multmax = Long.divideUnsigned(-1L, radix);  // -1L is max unsigned long
            long result = digit & 0xFF;
            boolean inRange = true;
            while (i < endIndex
                    && (digit = Character.digit(s.charAt(i++), radix)) >= 0
                    && (inRange = Long.compareUnsigned(result, multmax) < 0
                        || result == multmax
                           && digit < (int) (-radix * multmax))) {
                result = radix * result + digit;
            }
            if (inRange && i == endIndex && digit >= 0) {
                return result;
            }
        }
        if (digit < 0) {
            throw forCharSequence(s, beginIndex, endIndex,
                i - (digit < -1 ? 0 : 1));
        }
        throw new NumberFormatException(
            "String value " + s + " exceeds range of unsigned long.");
    }
}
