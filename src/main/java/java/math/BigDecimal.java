package java.math;

import org.cprover.CProver;
import org.cprover.CProverString;

/**
 * DIFFBLUE MODEL LIBRARY
 * Immutable arbitrary-precision decimal numbers: an unscaled BigInteger
 * (see its model, exact up to 128 bits) and a scale. The algorithms are
 * those of OpenJDK 8 to 25 (on the unscaled value as a BigInteger), which
 * agree on all of it but for the parse (the messages of its exceptions
 * from Java 9, and its range of scales from Java 19), see
 * CProverRuntime.mayRunOn.
 *
 * @diffblue.limitedSupport
 * The unscaled value is exact up to 128 bits (see BigInteger); beyond, the
 * digits are any. doubleValue and floatValue are exact where Java computes
 * them from a long (see their code), any otherwise. The message of the
 * ArithmeticException of a division by zero with a scale is either of
 * Java's. sqrt is not modelled.
 */
public class BigDecimal extends Number implements Comparable<BigDecimal> {

    /** The unscaled value. */
    final BigInteger intVal;
    /** The scale: the value is intVal * 10^-scale. */
    final int scale;

    public static final int ROUND_UP = 0;
    public static final int ROUND_DOWN = 1;
    public static final int ROUND_CEILING = 2;
    public static final int ROUND_FLOOR = 3;
    public static final int ROUND_HALF_UP = 4;
    public static final int ROUND_HALF_DOWN = 5;
    public static final int ROUND_HALF_EVEN = 6;
    public static final int ROUND_UNNECESSARY = 7;

    /** 10^0 to the largest power of ten of the BigInteger model. */
    private static final BigInteger[] TEN_POWERS = tenPowers();

    private static BigInteger[] tenPowers() {
        BigInteger[] powers = new BigInteger[BigInteger.BITS * 3 / 10 + 1];
        BigInteger p = BigInteger.ONE;
        for (int i = 0; i < BigInteger.BITS * 3 / 10 + 1; i++) {
            powers[i] = p;
            p = p.multiply(BigInteger.TEN);
        }
        return powers;
    }

    public static final BigDecimal ZERO = new BigDecimal(BigInteger.ZERO, 0);
    public static final BigDecimal ONE = new BigDecimal(BigInteger.ONE, 0);
    public static final BigDecimal TWO = new BigDecimal(BigInteger.TWO, 0);
    public static final BigDecimal TEN = new BigDecimal(BigInteger.TEN, 0);

    // DIFFBLUE MODEL LIBRARY Constructors and factories.

    public BigDecimal(BigInteger unscaledVal, int scale) {
        if (unscaledVal == null) {
            throw new NullPointerException();
        }
        this.intVal = unscaledVal;
        this.scale = scale;
    }

    public BigDecimal(BigInteger val) {
        this(val, 0);
    }

    public BigDecimal(BigInteger unscaledVal, int scale, MathContext mc) {
        this(doRound(new BigDecimal(unscaledVal, scale), mc));
    }

    public BigDecimal(BigInteger val, MathContext mc) {
        this(val, 0, mc);
    }

    public BigDecimal(long val) {
        this(BigInteger.valueOf(val), 0);
    }

    public BigDecimal(int val) {
        this(BigInteger.valueOf(val), 0);
    }

    public BigDecimal(long val, MathContext mc) {
        this(BigInteger.valueOf(val), 0, mc);
    }

    public BigDecimal(int val, MathContext mc) {
        this(BigInteger.valueOf(val), 0, mc);
    }

    /** A copy of {@code v}. */
    private BigDecimal(BigDecimal v) {
        this.intVal = v.intVal;
        this.scale = v.scale;
    }

    public BigDecimal(double val) {
        this(ofDouble(val));
    }

    public BigDecimal(double val, MathContext mc) {
        this(doRound(ofDouble(val), mc));
    }

    /** The exact value of a double, as new BigDecimal(double). */
    private static BigDecimal ofDouble(double val) {
        if (Double.isInfinite(val) || Double.isNaN(val)) {
            throw new NumberFormatException("Infinite or NaN");
        }
        long valBits = Double.doubleToLongBits(val);
        int sign = ((valBits >> 63) == 0 ? 1 : -1);
        int exponent = (int) ((valBits >> 52) & 0x7ffL);
        long significand = (exponent == 0
            ? (valBits & ((1L << 52) - 1)) << 1
            : (valBits & ((1L << 52) - 1)) | (1L << 52));
        exponent -= 1075;
        if (significand == 0) {
            return new BigDecimal(BigInteger.ZERO, 0);
        }
        // normalize (at most 53 steps)
        for (int i = 0; i < 53; i++) {
            if ((significand & 1) == 0) {
                significand >>= 1;
                exponent++;
            }
        }
        BigInteger m = BigInteger.valueOf(sign * significand);
        if (exponent == 0) {
            return new BigDecimal(m, 0);
        }
        if (exponent < 0) {
            return new BigDecimal(
                BigInteger.valueOf(5).pow(-exponent).multiply(m), -exponent);
        }
        return new BigDecimal(BigInteger.TWO.pow(exponent).multiply(m), 0);
    }

    public BigDecimal(String val) {
        this(parse(val, 0, val.length(), MathContext.UNLIMITED));
    }

    public BigDecimal(String val, MathContext mc) {
        this(parse(val, 0, val.length(), mc));
    }

    public BigDecimal(char[] in) {
        this(parse(new String(in), 0, in.length, MathContext.UNLIMITED));
    }

    public BigDecimal(char[] in, MathContext mc) {
        this(parse(new String(in), 0, in.length, mc));
    }

    public BigDecimal(char[] in, int offset, int len) {
        this(in, offset, len, MathContext.UNLIMITED);
    }

    public BigDecimal(char[] in, int offset, int len, MathContext mc) {
        this(parseChars(in, offset, len, mc));
    }

    private static BigDecimal parseChars(char[] in, int offset, int len,
            MathContext mc) {
        if ((in.length | len | offset) < 0 || len > in.length - offset) {
            throw new NumberFormatException(
                "Bad offset or len arguments for char[] input.");
        }
        return parse(new String(in), offset, len, mc);
    }

    /** The NumberFormatException of the parse: no message in Java 8. */
    private static NumberFormatException parseError(String message) {
        boolean java8;
        if (!CProverRuntime.mayRunOn(9, 0)) {
            java8 = true;
        } else if (!CProverRuntime.mayRunOn(8, 8)) {
            java8 = false;
        } else {
            java8 = CProver.nondetBoolean();
        }
        return java8 ? new NumberFormatException()
            : new NumberFormatException(message);
    }

    /** The digit of a decimal digit of Unicode, or -1. */
    private static int decimalDigit(char c) {
        return BigInteger.digit(c, 10);
    }

    /**
     * The value of the characters {@code offset} to {@code offset + len}
     * of {@code in}, as OpenJDK's BigDecimal(char[], int, int,
     * MathContext): the sign, the digits with a point, then an exponent.
     */
    private static BigDecimal parse(String in, int offset, int len,
            MathContext mc) {
        if (len == 0 || offset >= in.length()) {
            // (an ArrayIndexOutOfBoundsException becomes this)
            throw new NumberFormatException();
        }
        boolean isneg = false;
        char first = CProverString.charAt(in, offset);
        if (first == '-') {
            isneg = true;
            offset++;
            len--;
        } else if (first == '+') {
            offset++;
            len--;
        }
        // the compact parse (of at most 18 characters) and the other give
        // other messages for a character that is not one of the number
        boolean compact = len <= 18;
        boolean dot = false;
        long scl = 0;
        long exp = 0;
        boolean hasExp = false;
        boolean anyDigit = false;
        BigInteger value = BigInteger.ZERO;
        int end = offset + len;
        for (int i = offset; i < end; i++) {
            if (!hasExp) {
                char c = CProverString.charAt(in, i);
                int d = decimalDigit(c);
                if (d >= 0) {
                    anyDigit = true;
                    value = value.multiply(BigInteger.TEN)
                        .add(BigInteger.valueOf(d));
                    if (dot) {
                        scl++;
                    }
                } else if (c == '.') {
                    if (dot) {
                        throw parseError("Character array contains more than "
                            + "one decimal point.");
                    }
                    dot = true;
                } else if (c == 'e' || c == 'E') {
                    exp = parseExp(in, i, end - i);
                    hasExp = true;
                } else if (compact) {
                    throw parseError("Character " + c + " is neither a decimal"
                        + " digit number, decimal point, nor \"e\" notation"
                        + " exponential mark.");
                } else {
                    throw parseError("Character array is missing \"e\" "
                        + "notation exponential mark.");
                }
            }
        }
        if (!anyDigit) {
            throw parseError("No digits found.");
        }
        // Java 9 to 18 check the exponent and then the scale; Java 19 and
        // later the scale (as a long) at the end
        boolean java19;
        if (!CProverRuntime.mayRunOn(8, 18)) {
            java19 = true;
        } else if (!CProverRuntime.mayRunOn(19, 0)) {
            java19 = false;
        } else {
            java19 = CProver.nondetBoolean();
        }
        long scale;
        if (java19) {
            scale = scl - exp;
            if ((int) scale != scale) {
                throw parseError("Exponent overflow.");
            }
        } else {
            if (hasExp && (int) exp != exp) {
                throw parseError("Exponent overflow.");
            }
            scale = scl - exp;
            if (exp != 0 && (scale > Integer.MAX_VALUE
                    || scale < Integer.MIN_VALUE)) {
                // (with this message in Java 8 too)
                throw new NumberFormatException("Scale out of range.");
            }
        }
        BigDecimal result =
            new BigDecimal(isneg ? value.negate() : value, (int) scale);
        return doRound(result, mc);
    }

    /**
     * The exponent of the characters from {@code offset} (an 'e' or 'E')
     * of {@code in}, of which {@code len} remain, as OpenJDK's parseExp.
     */
    private static long parseExp(String in, int offset, int len) {
        // (reading beyond the characters is an
        // ArrayIndexOutOfBoundsException, which becomes a
        // NumberFormatException without a message)
        long exp = 0;
        offset++;
        if (offset >= in.length()) {
            throw new NumberFormatException();
        }
        char c = CProverString.charAt(in, offset);
        len--;
        boolean negexp = (c == '-');
        if (negexp || c == '+') {
            offset++;
            if (offset >= in.length()) {
                throw new NumberFormatException();
            }
            c = CProverString.charAt(in, offset);
            len--;
        }
        if (len <= 0) {
            throw parseError("No exponent digits.");
        }
        // skip the leading zeros while more than 10 digits remain
        int end = offset + len;
        boolean skipping = true;
        for (int i = offset; i < end; i++) {
            if (skipping && len > 10 && decimalDigit(c) == 0) {
                offset++;
                c = CProverString.charAt(in, offset);
                len--;
            } else {
                skipping = false;
            }
        }
        if (len > 10) {
            throw parseError("Too many nonzero exponent digits.");
        }
        for (int i = 0; i < 10; i++) {
            if (i < len) {
                int v = decimalDigit(CProverString.charAt(in, offset + i));
                if (v < 0) {
                    throw parseError("Not a digit.");
                }
                exp = exp * 10 + v;
            }
        }
        return negexp ? -exp : exp;
    }

    public static BigDecimal valueOf(long unscaledVal, int scale) {
        return new BigDecimal(BigInteger.valueOf(unscaledVal), scale);
    }

    public static BigDecimal valueOf(long val) {
        return new BigDecimal(BigInteger.valueOf(val), 0);
    }

    public static BigDecimal valueOf(double val) {
        // new BigDecimal(Double.toString(val)): the parse of a string of at
        // most 24 characters ("-2.2250738585072014E-308") and 17 digits,
        // with loops of constant bounds (the string is the solver's)
        if (Double.isNaN(val) || Double.isInfinite(val)) {
            return new BigDecimal(Double.isNaN(val) ? "NaN"
                : val > 0 ? "Infinity" : "-Infinity");
        }
        String s = Double.toString(val);
        int len = s.length();
        CProver.assume(len <= 24);
        boolean negative = false;
        boolean dot = false;
        boolean inExp = false;
        boolean negExp = false;
        long digits = 0;
        int fraction = 0;
        int exp = 0;
        for (int i = 0; i < 24; i++) {
            if (i < len) {
                char c = CProverString.charAt(s, i);
                if (c == '-') {
                    if (inExp) {
                        negExp = true;
                    } else {
                        negative = true;
                    }
                } else if (c == '.') {
                    dot = true;
                } else if (c == 'E') {
                    inExp = true;
                } else if (inExp) {
                    exp = exp * 10 + (c - '0');
                } else {
                    digits = digits * 10 + (c - '0');
                    if (dot) {
                        fraction++;
                    }
                }
            }
        }
        long scale = (long) fraction - (negExp ? -exp : exp);
        return new BigDecimal(BigInteger.valueOf(negative ? -digits : digits),
            (int) scale);
    }

    // DIFFBLUE MODEL LIBRARY Scales and rounding.

    /** 10^n, for n >= 0. */
    static BigInteger tenPow(int n) {
        // (computed rather than read from TEN_POWERS, so that symex folds
        // it for a constant n)
        if (n <= 18) {
            long p = 1;
            for (int i = 0; i < 18; i++) {
                if (i < n) {
                    p *= 10;
                }
            }
            return BigInteger.valueOf(p);
        }
        return BigInteger.TEN.pow(n);
    }

    /** value * 10^n (value for n <= 0). */
    static BigInteger multiplyPowerTen(BigInteger value, int n) {
        if (n <= 0) {
            return value;
        }
        return value.multiply(tenPow(n));
    }

    /** The decimal digits of |v| (1 for zero). */
    static int digitLength(BigInteger v) {
        if (v.signum == 0) {
            return 1;
        }
        if (v.wide) {
            int n = CProver.nondetInt();
            CProver.assume(n >= TEN_POWERS.length);
            return n;
        }
        if (v.fitsLong() && v.longValue() != Long.MIN_VALUE) {
            // the digits of a long (folded by symex for a constant)
            long a = Math.abs(v.longValue());
            int p = 1;
            long ten = 10;
            for (int k = 1; k < 19; k++) {
                if (a >= ten) {
                    p = k + 1;
                }
                ten *= 10;
            }
            return p;
        }
        BigInteger a = v.abs();
        int p = 1;
        for (int k = 1; k < TEN_POWERS.length; k++) {
            if (a.compareTo(TEN_POWERS[k]) >= 0) {
                p = k + 1;
            }
        }
        return p;
    }

    /** The scale {@code val}, or an ArithmeticException for a non-zero. */
    private static int checkScale(BigInteger intVal, long val) {
        int asInt = (int) val;
        if (asInt != val) {
            asInt = val > Integer.MAX_VALUE ? Integer.MAX_VALUE
                : Integer.MIN_VALUE;
            if (intVal.signum() != 0) {
                throw new ArithmeticException(asInt > 0 ? "Underflow"
                    : "Overflow");
            }
        }
        return asInt;
    }

    private int checkScale(long val) {
        return checkScale(intVal, val);
    }

    private static int checkScaleNonZero(long val) {
        int asInt = (int) val;
        if (asInt != val) {
            throw new ArithmeticException(asInt > 0 ? "Underflow"
                : "Overflow");
        }
        return asInt;
    }

    private static int saturateLong(long s) {
        int i = (int) s;
        return (s == i) ? i : (s < 0 ? Integer.MIN_VALUE : Integer.MAX_VALUE);
    }

    /** Whether to round away from zero (OpenJDK's commonNeedIncrement). */
    private static boolean needIncrement(int roundingMode, int qsign,
            int cmpFracHalf, boolean oddQuot) {
        switch (roundingMode) {
        case ROUND_UNNECESSARY:
            throw new ArithmeticException("Rounding necessary");
        case ROUND_UP:
            return true;
        case ROUND_DOWN:
            return false;
        case ROUND_CEILING:
            return qsign > 0;
        case ROUND_FLOOR:
            return qsign < 0;
        default:
            if (cmpFracHalf < 0) {
                return false;
            }
            if (cmpFracHalf > 0) {
                return true;
            }
            if (roundingMode == ROUND_HALF_DOWN) {
                return false;
            }
            if (roundingMode == ROUND_HALF_UP) {
                return true;
            }
            return oddQuot;
        }
    }

    /**
     * dividend / divisor rounded by the mode; whether the remainder is zero
     * in {@code exact[0]}.
     */
    private static BigInteger divideAndRound(BigInteger dividend,
            BigInteger divisor, int roundingMode, boolean[] exact) {
        BigInteger[] qr = dividend.divideAndRemainder(divisor);
        BigInteger q = qr[0];
        BigInteger r = qr[1];
        exact[0] = r.signum() == 0;
        if (exact[0]) {
            return q;
        }
        int qsign = dividend.signum() != divisor.signum() ? -1 : 1;
        int cmpFracHalf = r.abs().shiftLeft(1).compareTo(divisor.abs());
        if (needIncrement(roundingMode, qsign, cmpFracHalf, q.testBit(0))) {
            q = q.add(BigInteger.valueOf(qsign));
        }
        return q;
    }

    private static BigDecimal divideAndRound(BigInteger dividend,
            BigInteger divisor, int scale, int roundingMode,
            int preferredScale) {
        boolean[] exact = new boolean[1];
        BigInteger q = divideAndRound(dividend, divisor, roundingMode, exact);
        if (exact[0] && preferredScale != scale) {
            return createAndStripZerosToMatchScale(q, scale, preferredScale);
        }
        return new BigDecimal(q, scale);
    }

    /** The value without the zeros at its end down to the scale. */
    private static BigDecimal createAndStripZerosToMatchScale(
            BigInteger intVal, int scale, long preferredScale) {
        if (intVal.wide) {
            // (any number of zeros)
            BigDecimal r = new BigDecimal(BigInteger.any(intVal.signum),
                CProver.nondetInt());
            CProver.assume(r.scale <= scale && r.scale >= preferredScale);
            return r;
        }
        boolean stop = false;
        for (int k = 0; k < TEN_POWERS.length; k++) {
            if (!stop && intVal.abs().compareTo(BigInteger.TEN) >= 0
                    && scale > preferredScale) {
                BigInteger[] qr = intVal.divideAndRemainder(BigInteger.TEN);
                if (intVal.testBit(0) || qr[1].signum() != 0) {
                    stop = true;
                } else {
                    intVal = qr[0];
                    scale = checkScale(intVal, (long) scale - 1);
                }
            }
        }
        return new BigDecimal(intVal, scale);
    }

    /** {@code val} rounded to the precision of {@code mc}. */
    static BigDecimal doRound(BigDecimal val, MathContext mc) {
        int mcp = mc.precision;
        if (mcp <= 0) {
            return val;
        }
        BigInteger intVal = val.intVal;
        int scale = val.scale;
        int prec = val.precision();
        int drop = prec - mcp;
        // at most twice: the second for a carry (999 to 1000)
        for (int k = 0; k < 3; k++) {
            if (drop > 0) {
                scale = checkScaleNonZero((long) scale - drop);
                intVal = divideAndRound(intVal, tenPow(drop),
                    mc.roundingMode.oldMode, new boolean[1]);
                prec = digitLength(intVal);
                drop = prec - mcp;
            }
        }
        return new BigDecimal(intVal, scale);
    }

    public int scale() {
        return scale;
    }

    public int precision() {
        return digitLength(intVal);
    }

    public BigInteger unscaledValue() {
        return intVal;
    }

    public int signum() {
        return intVal.signum();
    }

    public BigDecimal setScale(int newScale, RoundingMode roundingMode) {
        return setScale(newScale, roundingMode.oldMode);
    }

    public BigDecimal setScale(int newScale, int roundingMode) {
        if (roundingMode < ROUND_UP || roundingMode > ROUND_UNNECESSARY) {
            throw new IllegalArgumentException("Invalid rounding mode");
        }
        int oldScale = this.scale;
        if (newScale == oldScale) {
            return this;
        }
        if (this.signum() == 0) {
            return new BigDecimal(BigInteger.ZERO, newScale);
        }
        if (newScale > oldScale) {
            int raise = checkScale((long) newScale - oldScale);
            return new BigDecimal(multiplyPowerTen(intVal, raise), newScale);
        }
        int drop = checkScale((long) oldScale - newScale);
        return divideAndRound(intVal, tenPow(drop), newScale, roundingMode,
            newScale);
    }

    public BigDecimal setScale(int newScale) {
        return setScale(newScale, ROUND_UNNECESSARY);
    }

    public BigDecimal round(MathContext mc) {
        return plus(mc);
    }

    public BigDecimal plus() {
        return this;
    }

    public BigDecimal plus(MathContext mc) {
        if (mc.precision == 0) {
            return this;
        }
        return doRound(this, mc);
    }

    public BigDecimal negate() {
        return new BigDecimal(intVal.negate(), scale);
    }

    public BigDecimal negate(MathContext mc) {
        return negate().plus(mc);
    }

    public BigDecimal abs() {
        return signum() < 0 ? negate() : this;
    }

    public BigDecimal abs(MathContext mc) {
        return signum() < 0 ? negate(mc) : plus(mc);
    }

    /**
     * Whether Java 13 to 19 run the program, whose movePointLeft and
     * movePointRight of 0 give the value itself (with a negative scale).
     */
    private static boolean movesNothingByZero() {
        if (!CProverRuntime.mayRunOn(13, 19)) {
            return false;
        }
        if (!CProverRuntime.mayRunOn(8, 12) && !CProverRuntime.mayRunOn(20, 0)) {
            return true;
        }
        return CProver.nondetBoolean();
    }

    public BigDecimal movePointLeft(int n) {
        if (n == 0 && (scale >= 0 || movesNothingByZero())) {
            return this;
        }
        int newScale = checkScale((long) scale + n);
        BigDecimal num = new BigDecimal(intVal, newScale);
        return num.scale < 0 ? num.setScale(0, ROUND_UNNECESSARY) : num;
    }

    public BigDecimal movePointRight(int n) {
        if (n == 0 && (scale >= 0 || movesNothingByZero())) {
            return this;
        }
        int newScale = checkScale((long) scale - n);
        BigDecimal num = new BigDecimal(intVal, newScale);
        return num.scale < 0 ? num.setScale(0, ROUND_UNNECESSARY) : num;
    }

    public BigDecimal scaleByPowerOfTen(int n) {
        return new BigDecimal(intVal, checkScale((long) scale - n));
    }

    public BigDecimal stripTrailingZeros() {
        if (intVal.signum() == 0) {
            return BigDecimal.ZERO;
        }
        return createAndStripZerosToMatchScale(intVal, scale, Long.MIN_VALUE);
    }

    public BigDecimal ulp() {
        return new BigDecimal(BigInteger.ONE, this.scale());
    }

    // DIFFBLUE MODEL LIBRARY Arithmetic.

    public BigDecimal add(BigDecimal augend) {
        long sdiff = (long) this.scale - augend.scale;
        if (sdiff == 0) {
            return new BigDecimal(intVal.add(augend.intVal), scale);
        }
        if (sdiff < 0) {
            int raise = checkScale(intVal, -sdiff);
            return new BigDecimal(
                multiplyPowerTen(intVal, raise).add(augend.intVal),
                augend.scale);
        }
        int raise = checkScale(augend.intVal, sdiff);
        return new BigDecimal(
            intVal.add(multiplyPowerTen(augend.intVal, raise)), scale);
    }

    public BigDecimal add(BigDecimal augend, MathContext mc) {
        if (mc.precision == 0) {
            return add(augend);
        }
        BigDecimal lhs = this;
        boolean lhsIsZero = lhs.signum() == 0;
        boolean augendIsZero = augend.signum() == 0;
        if (lhsIsZero || augendIsZero) {
            int preferredScale = lhs.scale() > augend.scale() ? lhs.scale()
                : augend.scale();
            if (lhsIsZero && augendIsZero) {
                return new BigDecimal(BigInteger.ZERO, preferredScale);
            }
            BigDecimal result = lhsIsZero ? doRound(augend, mc)
                : doRound(lhs, mc);
            if (result.scale() == preferredScale) {
                return result;
            } else if (result.scale() > preferredScale) {
                return createAndStripZerosToMatchScale(result.intVal,
                    result.scale, preferredScale);
            } else {
                int precisionDiff = mc.precision - result.precision();
                int scaleDiff = preferredScale - result.scale();
                if (precisionDiff >= scaleDiff) {
                    return result.setScale(preferredScale);
                }
                return result.setScale(result.scale() + precisionDiff);
            }
        }
        return doRound(add(augend), mc);
    }

    public BigDecimal subtract(BigDecimal subtrahend) {
        return add(subtrahend.negate());
    }

    public BigDecimal subtract(BigDecimal subtrahend, MathContext mc) {
        return add(subtrahend.negate(), mc);
    }

    public BigDecimal multiply(BigDecimal multiplicand) {
        int productScale = checkScale((long) scale + multiplicand.scale);
        return new BigDecimal(intVal.multiply(multiplicand.intVal),
            productScale);
    }

    public BigDecimal multiply(BigDecimal multiplicand, MathContext mc) {
        if (mc.precision == 0) {
            return multiply(multiplicand);
        }
        return doRound(multiply(multiplicand), mc);
    }

    public BigDecimal divide(BigDecimal divisor, int scale, int roundingMode) {
        if (roundingMode < ROUND_UP || roundingMode > ROUND_UNNECESSARY) {
            throw new IllegalArgumentException("Invalid rounding mode");
        }
        if (divisor.signum() == 0) {
            // (Java's message depends on whether it divides longs)
            throw new ArithmeticException(CProver.nondetBoolean()
                ? "/ by zero" : "BigInteger divide by zero");
        }
        BigInteger dividend = this.intVal;
        int dividendScale = this.scale;
        BigInteger d = divisor.intVal;
        int divisorScale = divisor.scale;
        if (checkScale(dividend, (long) scale + divisorScale)
                > dividendScale) {
            int newScale = scale + divisorScale;
            int raise = newScale - dividendScale;
            return divideAndRound(multiplyPowerTen(dividend, raise), d, scale,
                roundingMode, scale);
        }
        int newScale = checkScale(d, (long) dividendScale - scale);
        int raise = newScale - divisorScale;
        return divideAndRound(dividend, multiplyPowerTen(d, raise), scale,
            roundingMode, scale);
    }

    public BigDecimal divide(BigDecimal divisor, int scale,
            RoundingMode roundingMode) {
        return divide(divisor, scale, roundingMode.oldMode);
    }

    public BigDecimal divide(BigDecimal divisor, int roundingMode) {
        return divide(divisor, scale, roundingMode);
    }

    public BigDecimal divide(BigDecimal divisor, RoundingMode roundingMode) {
        return divide(divisor, scale, roundingMode.oldMode);
    }

    public BigDecimal divide(BigDecimal divisor) {
        if (divisor.signum() == 0) {
            if (this.signum() == 0) {
                throw new ArithmeticException("Division undefined");
            }
            throw new ArithmeticException("Division by zero");
        }
        int preferredScale = saturateLong((long) this.scale - divisor.scale);
        if (this.signum() == 0) {
            return new BigDecimal(BigInteger.ZERO, preferredScale);
        }
        // a terminating expansion has at most this.precision() +
        // ceil(10 divisor.precision() / 3) digits
        long digits = this.precision()
            + ((10L * divisor.precision() + 2) / 3);
        MathContext mc = new MathContext(
            (int) (digits < Integer.MAX_VALUE ? digits : Integer.MAX_VALUE), RoundingMode.UNNECESSARY);
        BigDecimal quotient;
        try {
            quotient = this.divide(divisor, mc);
        } catch (ArithmeticException e) {
            throw new ArithmeticException("Non-terminating decimal expansion; "
                + "no exact representable decimal result.");
        }
        int quotientScale = quotient.scale();
        if (preferredScale > quotientScale) {
            return quotient.setScale(preferredScale, ROUND_UNNECESSARY);
        }
        return quotient;
    }

    public BigDecimal divide(BigDecimal divisor, MathContext mc) {
        int mcp = mc.precision;
        if (mcp == 0) {
            return divide(divisor);
        }
        BigDecimal dividend = this;
        long preferredScale = (long) dividend.scale - divisor.scale;
        if (divisor.signum() == 0) {
            if (dividend.signum() == 0) {
                throw new ArithmeticException("Division undefined");
            }
            throw new ArithmeticException("Division by zero");
        }
        if (dividend.signum() == 0) {
            return new BigDecimal(BigInteger.ZERO, saturateLong(preferredScale));
        }
        // OpenJDK's divide(BigInteger, int, BigInteger, int, long,
        // MathContext): xscale and yscale are the precisions, normalized so
        // that the quotient has mcp digits
        int xscale = dividend.precision();
        int yscale = divisor.precision();
        BigInteger xs = dividend.intVal;
        BigInteger ys = divisor.intVal;
        if (compareMagnitudeNormalized(xs, xscale, ys, yscale) > 0) {
            yscale -= 1;
        }
        int roundingMode = mc.roundingMode.oldMode;
        BigDecimal quotient;
        int scl = checkScaleNonZero(preferredScale + yscale - xscale + mcp);
        if (checkScaleNonZero((long) mcp + yscale - xscale) > 0) {
            int raise = checkScaleNonZero((long) mcp + yscale - xscale);
            quotient = divideAndRound(multiplyPowerTen(xs, raise), ys, scl,
                roundingMode, checkScaleNonZero(preferredScale));
        } else {
            int newScale = checkScaleNonZero((long) xscale - mcp);
            int raise = checkScaleNonZero((long) newScale - yscale);
            quotient = divideAndRound(xs, multiplyPowerTen(ys, raise), scl,
                roundingMode, checkScaleNonZero(preferredScale));
        }
        return doRound(quotient, mc);
    }

    private static int compareMagnitudeNormalized(BigInteger xs, int xscale,
            BigInteger ys, int yscale) {
        int sdiff = xscale - yscale;
        if (sdiff < 0) {
            return multiplyPowerTen(xs, -sdiff).abs().compareTo(ys.abs());
        }
        return xs.abs().compareTo(multiplyPowerTen(ys, sdiff).abs());
    }

    public BigDecimal divideToIntegralValue(BigDecimal divisor) {
        int preferredScale = saturateLong((long) this.scale - divisor.scale);
        if (this.compareMagnitude(divisor) < 0) {
            return new BigDecimal(BigInteger.ZERO, preferredScale);
        }
        if (this.signum() == 0 && divisor.signum() != 0) {
            return this.setScale(preferredScale, ROUND_UNNECESSARY);
        }
        long digits = this.precision() + ((10L * divisor.precision() + 2) / 3)
            + Math.abs((long) this.scale() - divisor.scale()) + 2;
        int maxDigits = (int) (digits < Integer.MAX_VALUE ? digits : Integer.MAX_VALUE);
        BigDecimal quotient = this.divide(divisor,
            new MathContext(maxDigits, RoundingMode.DOWN));
        if (quotient.scale > 0) {
            quotient = quotient.setScale(0, RoundingMode.DOWN);
            quotient = createAndStripZerosToMatchScale(quotient.intVal,
                quotient.scale, preferredScale);
        }
        if (quotient.scale < preferredScale) {
            quotient = quotient.setScale(preferredScale, ROUND_UNNECESSARY);
        }
        return quotient;
    }

    public BigDecimal divideToIntegralValue(BigDecimal divisor,
            MathContext mc) {
        if (mc.precision == 0 || (this.compareMagnitude(divisor) < 0)) {
            return divideToIntegralValue(divisor);
        }
        int preferredScale = saturateLong((long) this.scale - divisor.scale);
        BigDecimal result = this.divide(divisor,
            new MathContext(mc.precision, RoundingMode.DOWN));
        if (result.scale() < 0) {
            BigDecimal product = result.multiply(divisor);
            if (this.subtract(product).compareMagnitude(divisor) >= 0) {
                throw new ArithmeticException("Division impossible");
            }
        } else if (result.scale() > 0) {
            result = result.setScale(0, RoundingMode.DOWN);
        }
        int precisionDiff = mc.precision - result.precision();
        if ((preferredScale > result.scale()) && precisionDiff > 0) {
            return result.setScale(result.scale()
                + (precisionDiff < preferredScale - result.scale ? precisionDiff
                    : preferredScale - result.scale));
        }
        return createAndStripZerosToMatchScale(result.intVal, result.scale,
            preferredScale);
    }

    public BigDecimal remainder(BigDecimal divisor) {
        return divideAndRemainder(divisor)[1];
    }

    public BigDecimal remainder(BigDecimal divisor, MathContext mc) {
        return divideAndRemainder(divisor, mc)[1];
    }

    public BigDecimal[] divideAndRemainder(BigDecimal divisor) {
        BigDecimal[] result = new BigDecimal[2];
        result[0] = this.divideToIntegralValue(divisor);
        result[1] = this.subtract(result[0].multiply(divisor));
        return result;
    }

    public BigDecimal[] divideAndRemainder(BigDecimal divisor,
            MathContext mc) {
        if (mc.precision == 0) {
            return divideAndRemainder(divisor);
        }
        BigDecimal[] result = new BigDecimal[2];
        result[0] = this.divideToIntegralValue(divisor, mc);
        result[1] = this.subtract(result[0].multiply(divisor));
        return result;
    }

    public BigDecimal pow(int n) {
        if (n < 0 || n > 999999999) {
            throw new ArithmeticException("Invalid operation");
        }
        int newScale = checkScale((long) scale * n);
        return new BigDecimal(intVal.pow(n), newScale);
    }

    public BigDecimal min(BigDecimal val) {
        return (compareTo(val) <= 0 ? this : val);
    }

    public BigDecimal max(BigDecimal val) {
        return (compareTo(val) >= 0 ? this : val);
    }

    // DIFFBLUE MODEL LIBRARY Comparison.

    /** The order of |this| and |val|. */
    private int compareMagnitude(BigDecimal val) {
        if (this.signum() == 0) {
            return val.signum() == 0 ? 0 : -1;
        }
        if (val.signum() == 0) {
            return 1;
        }
        long sdiff = (long) this.scale - val.scale;
        if (sdiff != 0) {
            // the adjusted exponents first, which bound the alignment
            long xae = (long) this.precision() - this.scale;
            long yae = (long) val.precision() - val.scale;
            if (xae < yae) {
                return -1;
            }
            if (xae > yae) {
                return 1;
            }
            if (sdiff < 0) {
                return multiplyPowerTen(intVal, (int) -sdiff).abs()
                    .compareTo(val.intVal.abs());
            }
            return intVal.abs().compareTo(
                multiplyPowerTen(val.intVal, (int) sdiff).abs());
        }
        return intVal.abs().compareTo(val.intVal.abs());
    }

    public int compareTo(BigDecimal val) {
        int xsign = this.signum();
        int ysign = val.signum();
        if (xsign != ysign) {
            return (xsign > ysign) ? 1 : -1;
        }
        if (xsign == 0) {
            return 0;
        }
        int cmp = compareMagnitude(val);
        return (xsign > 0) ? cmp : -cmp;
    }

    public boolean equals(Object x) {
        if (!(x instanceof BigDecimal)) {
            return false;
        }
        BigDecimal xDec = (BigDecimal) x;
        if (x == this) {
            return true;
        }
        if (scale != xDec.scale) {
            return false;
        }
        return intVal.equals(xDec.intVal);
    }

    public int hashCode() {
        // (as OpenJDK's of a compact value, which is that of the BigInteger)
        return 31 * intVal.hashCode() + scale;
    }

    // DIFFBLUE MODEL LIBRARY Strings.

    /** {@code n} (at least 0) zeros. */
    static String zeros(long n) {
        if (n > 0xFFFF) {
            // a string of that length, of any characters (and Java may run
            // out of memory)
            String s = CProver.nondetWithoutNull("");
            CProver.assume(s.length() == n);
            return s;
        }
        String s = "";
        String power = "0";
        for (int k = 0; k < 16; k++) {
            if (((n >>> k) & 1) != 0) {
                s = s + power;
            }
            if ((n >>> (k + 1)) != 0) {
                power = power + power;
            }
        }
        return s;
    }

    /** OpenJDK's layoutChars: scientific or engineering notation. */
    private String layoutChars(boolean sci) {
        if (scale == 0) {
            return intVal.toString();
        }
        if (intVal.wide) {
            // (the digits of a wide value are any, and so is its string)
            return CProver.nondetWithoutNull("");
        }
        String coeff = intVal.abs().toString();
        String buf = signum() < 0 ? "-" : "";
        int coeffLen = coeff.length();
        long adjusted = -(long) scale + (coeffLen - 1);
        if ((scale >= 0) && (adjusted >= -6)) {
            int pad = scale - coeffLen;
            if (pad >= 0) {
                return buf + "0." + zeros(pad) + coeff;
            }
            return buf + CProverString.substring(coeff, 0, -pad) + "."
                + CProverString.substring(coeff, -pad, coeffLen);
        }
        if (sci) {
            buf = buf + CProverString.charAt(coeff, 0);
            if (coeffLen > 1) {
                buf = buf + "." + CProverString.substring(coeff, 1, coeffLen);
            }
        } else {
            int sig = (int) (adjusted % 3);
            if (sig < 0) {
                sig += 3;
            }
            adjusted -= sig;
            sig++;
            if (signum() == 0) {
                if (sig == 1) {
                    buf = buf + "0";
                } else if (sig == 2) {
                    buf = buf + "0.00";
                    adjusted += 3;
                } else {
                    buf = buf + "0.0";
                    adjusted += 3;
                }
            } else if (sig >= coeffLen) {
                buf = buf + coeff + zeros(sig - coeffLen);
            } else {
                buf = buf + CProverString.substring(coeff, 0, sig) + "."
                    + CProverString.substring(coeff, sig, coeffLen);
            }
        }
        if (adjusted != 0) {
            buf = buf + "E";
            if (adjusted > 0) {
                buf = buf + "+";
            }
            buf = buf + adjusted;
        }
        return buf;
    }

    public String toString() {
        return layoutChars(true);
    }

    public String toEngineeringString() {
        return layoutChars(false);
    }

    public String toPlainString() {
        if (scale == 0) {
            return intVal.toString();
        }
        if (intVal.wide) {
            return CProver.nondetWithoutNull("");
        }
        if (this.scale < 0) {
            if (signum() == 0) {
                return "0";
            }
            int trailingZeros = checkScaleNonZero(-(long) scale);
            return intVal.toString() + zeros(trailingZeros);
        }
        String str = intVal.abs().toString();
        int signum = signum();
        int insertionPoint = str.length() - scale;
        if (insertionPoint == 0) {
            return (signum < 0 ? "-0." : "0.") + str;
        } else if (insertionPoint > 0) {
            return (signum < 0 ? "-" : "")
                + CProverString.substring(str, 0, insertionPoint) + "."
                + CProverString.substring(str, insertionPoint, str.length());
        }
        return (signum < 0 ? "-0." : "0.") + zeros(-insertionPoint) + str;
    }

    // DIFFBLUE MODEL LIBRARY Conversions.

    public BigInteger toBigInteger() {
        return this.setScale(0, ROUND_DOWN).intVal;
    }

    public BigInteger toBigIntegerExact() {
        return this.setScale(0, ROUND_UNNECESSARY).intVal;
    }

    public long longValue() {
        return toBigInteger().longValue();
    }

    public long longValueExact() {
        if (scale == 0 && intVal.fitsLong()) {
            return intVal.longValue();
        }
        // Java 9 and 10 check the digits of the integer part first, also of
        // a zero
        boolean java9;
        if (!CProverRuntime.mayRunOn(9, 10)) {
            java9 = false;
        } else if (!CProverRuntime.mayRunOn(8, 8)
                && !CProverRuntime.mayRunOn(11, 0)) {
            java9 = true;
        } else {
            java9 = CProver.nondetBoolean();
        }
        if (java9 && (this.precision() - (long) this.scale) > 19) {
            throw new ArithmeticException("Overflow");
        }
        if (this.signum() == 0) {
            return 0;
        }
        if ((this.precision() - this.scale) <= 0) {
            throw new ArithmeticException("Rounding necessary");
        }
        if ((this.precision() - (long) this.scale) > 19) {
            throw new ArithmeticException("Overflow");
        }
        BigDecimal num = this.setScale(0, ROUND_UNNECESSARY);
        if (!num.intVal.fitsLong()) {
            throw new ArithmeticException("Overflow");
        }
        return num.intVal.longValue();
    }

    public int intValue() {
        return (int) longValue();
    }

    public int intValueExact() {
        long num = this.longValueExact();
        if ((int) num != num) {
            throw new ArithmeticException("Overflow");
        }
        return (int) num;
    }

    public short shortValueExact() {
        long num = this.longValueExact();
        if ((short) num != num) {
            throw new ArithmeticException("Overflow");
        }
        return (short) num;
    }

    public byte byteValueExact() {
        long num = this.longValueExact();
        if ((byte) num != num) {
            throw new ArithmeticException("Overflow");
        }
        return (byte) num;
    }

    private static final double[] DOUBLE_10_POW = {
        1.0e0,  1.0e1,  1.0e2,  1.0e3,  1.0e4,  1.0e5,
        1.0e6,  1.0e7,  1.0e8,  1.0e9,  1.0e10, 1.0e11,
        1.0e12, 1.0e13, 1.0e14, 1.0e15, 1.0e16, 1.0e17,
        1.0e18, 1.0e19, 1.0e20, 1.0e21, 1.0e22
    };

    private static final float[] FLOAT_10_POW = {
        1.0e0f, 1.0e1f, 1.0e2f, 1.0e3f, 1.0e4f, 1.0e5f,
        1.0e6f, 1.0e7f, 1.0e8f, 1.0e9f, 1.0e10f
    };

    /**
     * DIFFBLUE MODEL LIBRARY
     * The nearest double: exact where Java divides or multiplies a long of
     * 52 bits by a power of ten (as OpenJDK 8); any of those of the
     * neighbourhood otherwise (Double.parseDouble is not modelled).
     */
    public double doubleValue() {
        if (scale == 0 && intVal.fitsLong()) {
            return (double) intVal.longValue();
        }
        if (intVal.fitsLong()) {
            long v = intVal.longValue();
            if (Math.abs(v) < 1L << 52) {
                if (scale > 0 && scale < DOUBLE_10_POW.length) {
                    return (double) v / DOUBLE_10_POW[scale];
                } else if (scale < 0 && scale > -DOUBLE_10_POW.length) {
                    return (double) v * DOUBLE_10_POW[-scale];
                }
            }
        }
        return Double.parseDouble(this.toString());
    }

    public float floatValue() {
        if (scale == 0 && intVal.fitsLong()) {
            return (float) intVal.longValue();
        }
        if (intVal.fitsLong()) {
            long v = intVal.longValue();
            if (Math.abs(v) < 1L << 22) {
                if (scale > 0 && scale < FLOAT_10_POW.length) {
                    return (float) v / FLOAT_10_POW[scale];
                } else if (scale < 0 && scale > -FLOAT_10_POW.length) {
                    return (float) v * FLOAT_10_POW[-scale];
                }
            }
        }
        return Float.parseFloat(this.toString());
    }

    // DIFFBLUE MODEL LIBRARY The strings of java.util.Formatter.

    /** Formatter's BigDecimalLayout: the mantissa, its point, its exponent. */
    static final class Layout {
        String mant;
        String exp;
        boolean dot;
        int scale;
    }

    /**
     * Formatter.BigDecimalLayout of the unscaled value {@code intVal} (not
     * negative) and the scale, for %e ({@code sci}) or %f; {@code java8}
     * for the zeros of a zero of a negative scale.
     */
    static Layout layout(BigInteger intVal, int scale, boolean sci,
            boolean java8) {
        Layout l = new Layout();
        String coeff = intVal.toString();
        l.scale = scale;
        int len = coeff.length();
        if (scale == 0) {
            if (len > 1) {
                if (sci) {
                    l.mant = CProverString.charAt(coeff, 0) + "."
                        + CProverString.substring(coeff, 1, len);
                    l.dot = true;
                    l.exp = len < 10 ? "+0" + (len - 1) : "+" + (len - 1);
                } else {
                    l.mant = coeff;
                }
            } else {
                l.mant = coeff;
                if (sci) {
                    l.exp = "+00";
                }
            }
        } else if (!sci) {
            if (scale >= len) {
                l.mant = "0." + zeros((long) scale - len) + coeff;
                l.dot = true;
            } else if (scale > 0) {
                int pad = len - scale;
                l.mant = CProverString.substring(coeff, 0, pad) + "."
                    + CProverString.substring(coeff, pad, len);
                l.dot = true;
            } else {
                l.mant = coeff;
                if (intVal.signum() != 0 || java8) {
                    l.mant = l.mant + zeros(-(long) scale);
                }
                l.scale = 0;
            }
        } else {
            l.mant = "" + CProverString.charAt(coeff, 0);
            if (len > 1) {
                l.mant = l.mant + "." + CProverString.substring(coeff, 1, len);
                l.dot = true;
            }
            long adjusted = -(long) scale + (len - 1);
            if (adjusted != 0) {
                long abs = Math.abs(adjusted);
                l.exp = (adjusted < 0 ? "-" : "+") + (abs < 10 ? "0" : "")
                    + abs;
            } else {
                l.exp = "+00";
            }
        }
        return l;
    }

    /**
     * DIFFBLUE MODEL LIBRARY
     * The string that java.util.Formatter prints for this with the
     * conversion {@code c} ('e', 'f' or 'g') and the precision (-1 for
     * none) and with the flag # ({@code alternate}), before its sign,
     * zeros, separators, case and width: a '-' for a negative value, then
     * that of the absolute value. Java 8 to 16 and 17 and later differ for
     * %g (from 17 on, the value is rounded first) and Java 8 for a zero of
     * a negative scale.
     */
    String cproverFormat(char c, int precision, boolean alternate) {
        if (intVal.wide) {
            // (the digits of a wide value are any)
            return CProver.nondetWithoutNull("");
        }
        // the strings of the versions that may run the program, each with
        // constant choices (so that symex folds those of a constant), then
        // any of them
        BigDecimal v = abs();
        String result = null;
        if (CProverRuntime.mayRunOn(8, 8)) {
            result = formatAbs(v, c, precision, alternate, true, false);
        }
        if (CProverRuntime.mayRunOn(9, 16)) {
            String s = formatAbs(v, c, precision, alternate, false, false);
            result = result == null || CProver.nondetBoolean() ? s : result;
        }
        if (CProverRuntime.mayRunOn(17, 0)) {
            String s = formatAbs(v, c, precision, alternate, false, true);
            result = result == null || CProver.nondetBoolean() ? s : result;
        }
        return signum() < 0 ? "-" + result : result;
    }

    /** Formatter.print(StringBuilder, BigDecimal, ...) of {@code value}. */
    private static String formatAbs(BigDecimal value, char c, int precision,
            boolean alternate, boolean java8, boolean java17) {
        if (c == 'g') {
            int prec = precision;
            if (precision == -1) {
                prec = 6;
            } else if (precision == 0) {
                prec = 1;
            }
            if (java17) {
                value = value.round(new MathContext(prec));
            }
            if (value.equals(BigDecimal.ZERO)
                || ((value.compareTo(BigDecimal.valueOf(1, 4)) != -1)
                    && (value.compareTo(BigDecimal.valueOf(1, -prec)) == -1))) {
                int e = -value.scale() + (value.precision() - 1);
                prec = prec - e - 1;
                return formatFixed(value, prec, alternate, java8);
            }
            return formatScientific(value, prec - 1, alternate, java8);
        }
        if (c == 'e') {
            return formatScientific(value, precision, alternate, java8);
        }
        return formatFixed(value, precision, alternate, java8);
    }

    private static String formatScientific(BigDecimal value, int precision,
            boolean alternate, boolean java8) {
        int prec = (precision == -1 ? 6 : precision);
        int scale = value.scale();
        int origPrec = value.precision();
        int nzeros = 0;
        int compPrec;
        if (prec > origPrec - 1) {
            compPrec = origPrec;
            nzeros = prec - (origPrec - 1);
        } else {
            compPrec = prec + 1;
        }
        MathContext mc = new MathContext(compPrec);
        BigDecimal v = new BigDecimal(value.unscaledValue(), scale, mc);
        Layout bdl = layout(v.unscaledValue(), v.scale(), true, java8);
        String mant = bdl.mant;
        if ((origPrec == 1 || !bdl.dot) && (nzeros > 0 || alternate)) {
            mant = mant + ".";
        }
        return mant + zeros(nzeros) + "e" + bdl.exp;
    }

    private static String formatFixed(BigDecimal value, int precision,
            boolean alternate, boolean java8) {
        int prec = (precision == -1 ? 6 : precision);
        int scale = value.scale();
        if (scale > prec) {
            int compPrec = value.precision();
            if (compPrec <= scale) {
                value = value.setScale(prec, RoundingMode.HALF_UP);
            } else {
                compPrec -= (scale - prec);
                value = new BigDecimal(value.unscaledValue(), scale,
                    new MathContext(compPrec));
            }
        }
        Layout bdl = layout(value.unscaledValue(), value.scale(), false,
            java8);
        String mant = bdl.mant;
        int nzeros = (bdl.scale < prec ? prec - bdl.scale : 0);
        if (bdl.scale == 0 && (alternate || nzeros > 0)) {
            mant = mant + ".";
        }
        return mant + zeros(nzeros);
    }
}
