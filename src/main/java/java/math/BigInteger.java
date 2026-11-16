package java.math;

import java.util.Random;

import org.cprover.CProver;
import org.cprover.CProverString;

/**
 * DIFFBLUE MODEL LIBRARY
 * Immutable arbitrary-precision integers, modelled with a fixed number of
 * 32-bit limbs (LIMBS) so that every loop has a constant bound: a value
 * whose magnitude fits in them is exact, and one beyond them is "wide",
 * of which only the sign is kept. What depends on the digits of a wide
 * value is any value of those that the operation may give (an
 * over-approximation, never fewer behaviours than Java).
 *
 * The behaviour follows OpenJDK 8 to 25, which agree on all of it but for
 * the messages of some NumberFormatExceptions and the digits of a few
 * Unicode scripts (see CProverRuntime.mayRunOn).
 *
 * @diffblue.limitedSupport
 * Exact up to 128 bits of magnitude. isProbablePrime gives any answer for a
 * composite number, and modPow and modInverse give any result in range for
 * a modulus of more than 63 bits.
 */
public class BigInteger extends Number implements Comparable<BigInteger> {

    // DIFFBLUE MODEL LIBRARY The representation.

    /** The number of limbs of the magnitude. */
    static final int LIMBS = 4;
    /** The number of bits of the magnitude. */
    static final int BITS = 32 * LIMBS;
    static final long LONG_MASK = 0xFFFFFFFFL;

    /** -1, 0 or 1; never 0 for a wide value. */
    final int signum;
    /**
     * The magnitude, LIMBS limbs, the least significant first; all zero
     * for a wide value.
     */
    final int[] mag;
    /** Whether the magnitude is 2^BITS or more (then unknown). */
    final boolean wide;

    BigInteger(int signum, int[] mag, boolean wide) {
        this.signum = signum;
        this.mag = mag;
        this.wide = wide;
    }

    /** The value of the sign and the magnitude, or wide if it overflowed. */
    static BigInteger of(int sign, int[] mag, boolean overflow) {
        if (overflow) {
            return wide(sign);
        }
        return new BigInteger(isZero(mag) ? 0 : sign, mag, false);
    }

    /** A wide value of the sign {@code sign} (not 0). */
    static BigInteger wide(int sign) {
        return new BigInteger(sign, new int[LIMBS], true);
    }

    /**
     * A value whose magnitude may exceed the range of BigInteger, which
     * Java reports with an ArithmeticException (where it does not run out
     * of memory first): possible where an operation on a wide value gives
     * a wide one.
     */
    static BigInteger wideMayOverflow(int sign) {
        if (CProver.nondetBoolean()) {
            throw new ArithmeticException(
                "BigInteger would overflow supported range");
        }
        return wide(sign);
    }

    /** Any value, of the sign {@code sign} where it is -1, 0 or 1. */
    static BigInteger any(int sign) {
        boolean w = CProver.nondetBoolean();
        int[] m = new int[LIMBS];
        if (!w) {
            for (int i = 0; i < LIMBS; i++) {
                m[i] = CProver.nondetInt();
            }
        }
        int s = CProver.nondetInt();
        CProver.assume(s >= -1 && s <= 1);
        if (sign >= -1 && sign <= 1) {
            CProver.assume(s == sign);
        }
        if (w) {
            CProver.assume(s != 0);
        } else {
            CProver.assume((s == 0) == isZero(m));
        }
        return new BigInteger(s, m, w);
    }

    /** Any value. */
    static BigInteger any() {
        return any(2);
    }

    /** Any value from 0 to {@code bound} - 1, for a positive bound. */
    static BigInteger anyBelow(BigInteger bound) {
        BigInteger r = any();
        CProver.assume(r.signum >= 0 && r.compareTo(bound) < 0);
        return r;
    }

    // DIFFBLUE MODEL LIBRARY Operations on magnitudes (LIMBS limbs).

    static boolean isZero(int[] a) {
        boolean zero = true;
        for (int i = 0; i < LIMBS; i++) {
            if (a[i] != 0) {
                zero = false;
            }
        }
        return zero;
    }

    static int compareMag(int[] a, int[] b) {
        int result = 0;
        for (int i = 0; i < LIMBS; i++) {
            // the most significant difference decides
            if (a[i] != b[i]) {
                result = Integer.compareUnsigned(a[i], b[i]) < 0 ? -1 : 1;
            }
        }
        return result;
    }

    /** a + b, and whether it overflowed. */
    static boolean addMag(int[] a, int[] b, int[] r) {
        long carry = 0;
        for (int i = 0; i < LIMBS; i++) {
            long t = (a[i] & LONG_MASK) + (b[i] & LONG_MASK) + carry;
            r[i] = (int) t;
            carry = t >>> 32;
        }
        return carry != 0;
    }

    /** a - b for a >= b. */
    static void subMag(int[] a, int[] b, int[] r) {
        long borrow = 0;
        for (int i = 0; i < LIMBS; i++) {
            long t = (a[i] & LONG_MASK) - (b[i] & LONG_MASK) - borrow;
            r[i] = (int) t;
            borrow = t < 0 ? 1 : 0;
        }
    }

    /** a * b, and whether it overflowed. */
    static boolean mulMag(int[] a, int[] b, int[] r) {
        boolean over = false;
        for (int i = 0; i < LIMBS; i++) {
            r[i] = 0;
        }
        for (int i = 0; i < LIMBS; i++) {
            long carry = 0;
            for (int j = 0; j < LIMBS; j++) {
                // below 2^64: (2^32 - 1)^2 + 2 (2^32 - 1)
                long t = (a[i] & LONG_MASK) * (b[j] & LONG_MASK) + carry;
                if (i + j < LIMBS) {
                    t += r[i + j] & LONG_MASK;
                    r[i + j] = (int) t;
                } else if ((int) t != 0) {
                    over = true;
                }
                carry = t >>> 32;
            }
            if (carry != 0) {
                over = true;
            }
        }
        return over;
    }

    /** a * f + d for 0 <= f, d < 2^32, and whether it overflowed. */
    static boolean mulAddMag(int[] a, long f, long d, int[] r) {
        long carry = d;
        for (int i = 0; i < LIMBS; i++) {
            long t = (a[i] & LONG_MASK) * f + carry;
            r[i] = (int) t;
            carry = t >>> 32;
        }
        return carry != 0;
    }

    /** a / d into q for 0 < d < 2^32; the remainder. */
    static long divLimbMag(int[] a, long d, int[] q) {
        long rem = 0;
        for (int i = LIMBS - 1; i >= 0; i--) {
            // an unsigned dividend below d 2^32: halve it to divide signed,
            // then correct by one
            long cur = (rem << 32) | (a[i] & LONG_MASK);
            long quot = ((cur >>> 1) / d) << 1;
            long r = cur - quot * d;
            if (Long.compareUnsigned(r, d) >= 0) {
                quot++;
                r -= d;
            }
            q[i] = (int) quot;
            rem = r;
        }
        return rem;
    }

    /** Whether the magnitude has bit {@code n} (0 <= n < BITS). */
    static boolean bitOf(int[] a, int n) {
        return ((a[n >>> 5] >>> (n & 31)) & 1) != 0;
    }

    /** The number of bits of the magnitude (0 for zero). */
    static int bitLengthMag(int[] a) {
        int n = 0;
        for (int i = 0; i < LIMBS; i++) {
            if (a[i] != 0) {
                n = 32 * i + 32 - Integer.numberOfLeadingZeros(a[i]);
            }
        }
        return n;
    }

    /** a / b and a % b for b != 0, by long division of the bits. */
    static void divMag(int[] a, int[] b, int[] q, int[] r) {
        for (int i = 0; i < LIMBS; i++) {
            q[i] = 0;
            r[i] = 0;
        }
        if (isZero(copyHigh(b))) {
            // a divisor of one limb
            r[0] = (int) divLimbMag(a, b[0] & LONG_MASK, q);
            return;
        }
        for (int n = BITS - 1; n >= 0; n--) {
            // r = 2 r + bit n of a (below 2 b, so no overflow beyond b's
            // range but for its top bit, which the comparison sees)
            boolean top = r[LIMBS - 1] < 0;
            for (int i = LIMBS - 1; i > 0; i--) {
                r[i] = (r[i] << 1) | (r[i - 1] >>> 31);
            }
            r[0] = (r[0] << 1) | (bitOf(a, n) ? 1 : 0);
            if (top || compareMag(r, b) >= 0) {
                subMag(r, b, r);
                q[n >>> 5] |= 1 << (n & 31);
            }
        }
    }

    /** The limbs of {@code a} but the first. */
    static int[] copyHigh(int[] a) {
        int[] h = new int[LIMBS];
        for (int i = 1; i < LIMBS; i++) {
            h[i] = a[i];
        }
        return h;
    }

    // DIFFBLUE MODEL LIBRARY Two's complement (LIMBS + 1 limbs), for the
    // bitwise operations.

    /** The value in two's complement, of LIMBS + 1 limbs. */
    int[] twos() {
        int[] t = new int[LIMBS + 1];
        for (int i = 0; i < LIMBS; i++) {
            t[i] = mag[i];
        }
        if (signum < 0) {
            long carry = 1;
            for (int i = 0; i <= LIMBS; i++) {
                long v = (~t[i] & LONG_MASK) + carry;
                t[i] = (int) v;
                carry = v >>> 32;
            }
        }
        return t;
    }

    /** The value of a two's complement of LIMBS + 1 limbs. */
    static BigInteger fromTwos(int[] t) {
        int sign = t[LIMBS] < 0 ? -1 : 1;
        int[] m = new int[LIMBS + 1];
        for (int i = 0; i <= LIMBS; i++) {
            m[i] = t[i];
        }
        if (sign < 0) {
            long carry = 1;
            for (int i = 0; i <= LIMBS; i++) {
                long v = (~m[i] & LONG_MASK) + carry;
                m[i] = (int) v;
                carry = v >>> 32;
            }
        }
        int[] r = new int[LIMBS];
        for (int i = 0; i < LIMBS; i++) {
            r[i] = m[i];
        }
        return of(sign, r, m[LIMBS] != 0);
    }

    // DIFFBLUE MODEL LIBRARY Constants and factories.

    public static final BigInteger ZERO = valueOf(0);
    public static final BigInteger ONE = valueOf(1);
    public static final BigInteger TWO = valueOf(2);
    public static final BigInteger TEN = valueOf(10);
    private static final BigInteger NEGATIVE_ONE = valueOf(-1);

    public static BigInteger valueOf(long val) {
        // (for Long.MIN_VALUE, -val is 2^63 as an unsigned long)
        long m = val < 0 ? -val : val;
        int[] mag = new int[LIMBS];
        mag[0] = (int) m;
        mag[1] = (int) (m >>> 32);
        return new BigInteger(val < 0 ? -1 : val == 0 ? 0 : 1, mag, false);
    }

    /**
     * The digit of {@code c} in {@code radix}, as Character.digit: the
     * decimal digits of Unicode (of the Basic Multilingual Plane) and the
     * Latin letters, also full width; -1 for none. The Sinhala Lith and
     * Myanmar Tai Laing digits are those of Java 9 and later only.
     */
    static int digit(char c, int radix) {
        int v = -1;
        if (c >= '0' && c <= '9') {
            v = c - '0';
        } else if (c >= 'a' && c <= 'z') {
            v = c - 'a' + 10;
        } else if (c >= 'A' && c <= 'Z') {
            v = c - 'A' + 10;
        } else if (c >= 0x80) {
            v = unicodeDigit(c);
        }
        return v < radix ? v : -1;
    }

    /** The decimal zeros of Unicode in the BMP, in all of Java 8 to 25. */
    private static final int[] ZEROS = {0x0660, 0x06f0, 0x07c0, 0x0966,
        0x09e6, 0x0a66, 0x0ae6, 0x0b66, 0x0be6, 0x0c66, 0x0ce6, 0x0d66,
        0x0e50, 0x0ed0, 0x0f20, 0x1040, 0x1090, 0x17e0, 0x1810, 0x1946,
        0x19d0, 0x1a80, 0x1a90, 0x1b50, 0x1bb0, 0x1c40, 0x1c50, 0xa620,
        0xa8d0, 0xa900, 0xa9d0, 0xaa50, 0xabf0, 0xff10};

    static int unicodeDigit(char c) {
        if (c >= 0xff21 && c <= 0xff3a) {
            return c - 0xff21 + 10;
        }
        if (c >= 0xff41 && c <= 0xff5a) {
            return c - 0xff41 + 10;
        }
        for (int i = 0; i < 34; i++) {
            int z = ZEROS[i];
            if (c >= z && c <= z + 9) {
                return c - z;
            }
        }
        // the decimal digits of Unicode 7, from Java 9 on
        if ((c >= 0x0de6 && c <= 0x0def) || (c >= 0xa9f0 && c <= 0xa9f9)) {
            boolean digit;
            if (!CProverRuntime.mayRunOn(8, 8)) {
                digit = true;
            } else if (!CProverRuntime.mayRunOn(9, 0)) {
                digit = false;
            } else {
                digit = CProver.nondetBoolean();
            }
            return digit ? c - (c >= 0xa9f0 ? 0xa9f0 : 0x0de6) : -1;
        }
        return -1;
    }

    /** The digits of a group of the parse (Integer.parseInt) for a radix. */
    private static final int[] DIGITS_PER_INT = {0, 0, 30, 19, 15, 13, 11,
        11, 10, 9, 9, 8, 8, 8, 8, 7, 7, 7, 7, 7, 7, 7, 6, 6, 6, 6,
        6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 5};

    /** The NumberFormatException of Integer.parseInt for {@code s}. */
    static NumberFormatException forInputString(String s, int radix) {
        // Java 12 and later name a radix other than 10
        String message = "For input string: \"" + s + "\"";
        if (radix != 10) {
            boolean suffix;
            if (!CProverRuntime.mayRunOn(8, 11)) {
                suffix = true;
            } else if (!CProverRuntime.mayRunOn(12, 0)) {
                suffix = false;
            } else {
                suffix = CProver.nondetBoolean();
            }
            if (suffix) {
                message = message + " under radix " + radix;
            }
        }
        return new NumberFormatException(message);
    }

    public BigInteger(String val, int radix) {
        // DIFFBLUE MODEL LIBRARY As OpenJDK: the sign, the leading zeros,
        // then groups of digits as Integer.parseInt parses them (its
        // exception names the group).
        int len = val.length();
        if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX) {
            throw new NumberFormatException("Radix out of range");
        }
        if (len == 0) {
            throw new NumberFormatException("Zero length BigInteger");
        }
        int sign = 1;
        int cursor = 0;
        int index1 = val.lastIndexOf('-');
        int index2 = val.lastIndexOf('+');
        if (index1 >= 0) {
            if (index1 != 0 || index2 >= 0) {
                throw new NumberFormatException(
                    "Illegal embedded sign character");
            }
            sign = -1;
            cursor = 1;
        } else if (index2 >= 0) {
            if (index2 != 0) {
                throw new NumberFormatException(
                    "Illegal embedded sign character");
            }
            cursor = 1;
        }
        if (cursor == len) {
            throw new NumberFormatException("Zero length BigInteger");
        }
        while (cursor < len && digit(CProverString.charAt(val, cursor), radix) == 0) {
            cursor++;
        }
        int[] m = new int[LIMBS];
        boolean over = false;
        if (cursor < len) {
            int numDigits = len - cursor;
            int perInt = DIGITS_PER_INT[radix];
            int groupLen = numDigits % perInt;
            if (groupLen == 0) {
                groupLen = perInt;
            }
            while (cursor < len) {
                String group = CProverString.substring(val, cursor, cursor + groupLen);
                long value = 0;
                long factor = 1;
                for (int i = 0; i < groupLen; i++) {
                    int d = digit(CProverString.charAt(group, i), radix);
                    if (d < 0) {
                        throw forInputString(group, radix);
                    }
                    value = value * radix + d;
                    factor *= radix;
                }
                if (mulAddMag(m, factor, value, m)) {
                    over = true;
                }
                cursor += groupLen;
                groupLen = perInt;
            }
        }
        BigInteger v = of(sign, m, over);
        this.signum = v.signum;
        this.mag = v.mag;
        this.wide = v.wide;
    }

    public BigInteger(String val) {
        this(val, 10);
    }

    public BigInteger(byte[] val) {
        // DIFFBLUE MODEL LIBRARY two's complement, the most significant
        // byte first
        if (val.length == 0) {
            throw new NumberFormatException("Zero length BigInteger");
        }
        boolean negative = val[0] < 0;
        int[] t = new int[LIMBS + 1];
        if (negative) {
            for (int i = 0; i <= LIMBS; i++) {
                t[i] = -1;
            }
        }
        boolean over = false;
        int skip = negative ? -1 : 0;
        boolean leading = true;
        int bytes = 0;
        for (int i = 0; i < val.length; i++) {
            if (leading && val[i] == skip) {
                continue;
            }
            leading = false;
            // shift in the byte
            if ((t[LIMBS] >>> 24) != (negative ? 0xff : 0)) {
                over = true;
            }
            for (int k = LIMBS; k > 0; k--) {
                t[k] = (t[k] << 8) | (t[k - 1] >>> 24);
            }
            t[0] = (t[0] << 8) | (val[i] & 0xff);
            bytes++;
        }
        BigInteger v = over ? wide(negative ? -1 : 1) : fromTwos(t);
        this.signum = v.signum;
        this.mag = v.mag;
        this.wide = v.wide;
    }

    public BigInteger(int signum, byte[] magnitude) {
        if (signum < -1 || signum > 1) {
            throw new NumberFormatException("Invalid signum value");
        }
        int[] m = new int[LIMBS];
        boolean over = false;
        for (int i = 0; i < magnitude.length; i++) {
            if ((m[LIMBS - 1] >>> 24) != 0) {
                over = true;
            }
            for (int k = LIMBS - 1; k > 0; k--) {
                m[k] = (m[k] << 8) | (m[k - 1] >>> 24);
            }
            m[0] = (m[0] << 8) | (magnitude[i] & 0xff);
        }
        boolean zero = !over && isZero(m);
        if (signum == 0 && !zero) {
            throw new NumberFormatException("signum-magnitude mismatch");
        }
        BigInteger v = of(signum, m, over);
        this.signum = v.signum;
        this.mag = v.mag;
        this.wide = v.wide;
    }

    public BigInteger(int numBits, Random rnd) {
        // DIFFBLUE MODEL LIBRARY any value from 0 to 2^numBits - 1
        if (numBits < 0) {
            throw new IllegalArgumentException("numBits must be non-negative");
        }
        if (numBits > 0 && rnd == null) {
            throw new NullPointerException();
        }
        BigInteger v = any();
        CProver.assume(v.signum >= 0);
        if (numBits < BITS) {
            CProver.assume(!v.wide && v.bitLength() <= numBits);
        }
        this.signum = v.signum;
        this.mag = v.mag;
        this.wide = v.wide;
    }

    // DIFFBLUE MODEL LIBRARY Arithmetic.

    public int signum() {
        return signum;
    }

    public BigInteger negate() {
        return new BigInteger(-signum, mag, wide);
    }

    public BigInteger abs() {
        return signum >= 0 ? this : negate();
    }

    public BigInteger add(BigInteger val) {
        if (val.signum == 0) {
            return this;
        }
        if (signum == 0) {
            return val;
        }
        if (wide || val.wide) {
            if (signum == val.signum) {
                // larger in magnitude than the wide one
                return wideMayOverflow(signum);
            }
            // a wide value plus a smaller one of the other sign keeps its
            // sign; two wide values of other signs give any value
            if (wide && val.wide) {
                return any();
            }
            return any(wide ? signum : val.signum);
        }
        int[] r = new int[LIMBS];
        if (signum == val.signum) {
            return of(signum, r, addMag(mag, val.mag, r));
        }
        int c = compareMag(mag, val.mag);
        if (c == 0) {
            return ZERO;
        }
        if (c > 0) {
            subMag(mag, val.mag, r);
            return of(signum, r, false);
        }
        subMag(val.mag, mag, r);
        return of(val.signum, r, false);
    }

    public BigInteger subtract(BigInteger val) {
        return add(val.negate());
    }

    public BigInteger multiply(BigInteger val) {
        int sign = signum * val.signum;
        if (sign == 0) {
            return ZERO;
        }
        if (wide || val.wide) {
            return wideMayOverflow(sign);
        }
        int[] r = new int[LIMBS];
        return of(sign, r, mulMag(mag, val.mag, r));
    }

    /** The quotient and the remainder of a non-zero divisor. */
    BigInteger[] divRem(BigInteger val) {
        if (val.signum == 0) {
            throw new ArithmeticException("BigInteger divide by zero");
        }
        if (signum == 0) {
            return new BigInteger[] {ZERO, ZERO};
        }
        if (!wide && val.wide) {
            return new BigInteger[] {ZERO, this};
        }
        if (wide) {
            BigInteger q = any(val.wide ? 2 : signum * val.signum);
            BigInteger r = any();
            CProver.assume(r.signum == 0 || r.signum == signum);
            CProver.assume(r.abs().compareTo(val.abs()) < 0);
            return new BigInteger[] {q, r};
        }
        int[] q = new int[LIMBS];
        int[] r = new int[LIMBS];
        long a = (mag[0] & LONG_MASK) | ((long) mag[1] << 32);
        long b = (val.mag[0] & LONG_MASK) | ((long) val.mag[1] << 32);
        if (mag[2] == 0 && mag[3] == 0 && val.mag[2] == 0 && val.mag[3] == 0
                && a >= 0 && b >= 0) {
            // (for values of longs but Long.MIN_VALUE: constant limbs that
            // leave the long division below aside)
            long qv = a / b;
            long rv = a - qv * b;
            q[0] = (int) qv;
            q[1] = (int) (qv >>> 32);
            r[0] = (int) rv;
            r[1] = (int) (rv >>> 32);
        } else {
            divMag(mag, val.mag, q, r);
        }
        return new BigInteger[] {of(signum * val.signum, q, false),
            of(signum, r, false)};
    }

    public BigInteger divide(BigInteger val) {
        return divRem(val)[0];
    }

    public BigInteger remainder(BigInteger val) {
        return divRem(val)[1];
    }

    public BigInteger[] divideAndRemainder(BigInteger val) {
        return divRem(val);
    }

    public BigInteger mod(BigInteger m) {
        if (m.signum <= 0) {
            throw new ArithmeticException("BigInteger: modulus not positive");
        }
        BigInteger r = remainder(m);
        return r.signum >= 0 ? r : r.add(m);
    }

    public BigInteger pow(int exponent) {
        if (exponent < 0) {
            throw new ArithmeticException("Negative exponent");
        }
        if (exponent == 0) {
            return ONE;
        }
        int sign = signum < 0 && (exponent & 1) != 0 ? -1 : signum == 0 ? 0 : 1;
        if (signum == 0 || (!wide && isOne(mag))) {
            return sign == 0 ? ZERO : sign < 0 ? NEGATIVE_ONE : ONE;
        }
        if (wide) {
            return exponent == 1 ? this : wideMayOverflow(sign);
        }
        // by the bits of the exponent, the most significant first; a power
        // beyond BITS bits only grows
        int[] r = new int[LIMBS];
        r[0] = 1;
        boolean over = false;
        int[] t = new int[LIMBS];
        for (int n = 30; n >= 0; n--) {
            if (!over && (exponent >>> n) != 0) {
                if (mulMag(r, r, t)) {
                    over = true;
                }
                copy(t, r);
                if (((exponent >>> n) & 1) != 0) {
                    if (mulMag(r, mag, t)) {
                        over = true;
                    }
                    copy(t, r);
                }
            }
        }
        if (over) {
            // The power has from (length - 1) exponent + 1 to length
            // exponent bits, and Java's range is below 2^31 bits.
            long length = bitLengthMag(mag);
            if ((length - 1) * exponent + 1 > Integer.MAX_VALUE) {
                return overflow();
            }
            if (length * exponent > Integer.MAX_VALUE) {
                return wideMayOverflow(sign);
            }
            return wide(sign);
        }
        return of(sign, r, false);
    }

    static boolean isOne(int[] a) {
        return a[0] == 1 && isZero(copyHigh(a));
    }

    static void copy(int[] from, int[] to) {
        for (int i = 0; i < LIMBS; i++) {
            to[i] = from[i];
        }
    }

    public BigInteger gcd(BigInteger val) {
        if (signum == 0) {
            return val.abs();
        }
        if (val.signum == 0) {
            return abs();
        }
        if (wide || val.wide) {
            BigInteger g = any(1);
            if (!wide) {
                CProver.assume(g.compareTo(abs()) <= 0);
            }
            if (!val.wide) {
                CProver.assume(g.compareTo(val.abs()) <= 0);
            }
            return g;
        }
        // binary gcd: at most 2 BITS subtractions
        int[] a = new int[LIMBS];
        int[] b = new int[LIMBS];
        copy(mag, a);
        copy(val.mag, b);
        int za = trailingZeros(a);
        int zb = trailingZeros(b);
        int shift = za < zb ? za : zb;
        shiftRightMag(a, za, a);
        shiftRightMag(b, zb, b);
        for (int n = 0; n < 2 * BITS; n++) {
            // a and b odd; b - a even
            if (!isZero(b)) {
                if (compareMag(a, b) > 0) {
                    int[] t = a;
                    a = b;
                    b = t;
                }
                subMag(b, a, b);
                if (!isZero(b)) {
                    shiftRightMag(b, trailingZeros(b), b);
                }
            }
        }
        int[] g = new int[LIMBS];
        shiftLeftMag(a, shift, g);
        return of(1, g, false);
    }

    /** The number of zero bits below the lowest one (BITS for zero). */
    static int trailingZeros(int[] a) {
        int n = BITS;
        for (int i = LIMBS - 1; i >= 0; i--) {
            if (a[i] != 0) {
                n = 32 * i + Integer.numberOfTrailingZeros(a[i]);
            }
        }
        return n;
    }

    public BigInteger min(BigInteger val) {
        return compareTo(val) < 0 ? this : val;
    }

    public BigInteger max(BigInteger val) {
        return compareTo(val) > 0 ? this : val;
    }

    public BigInteger sqrt() {
        if (signum < 0) {
            throw new ArithmeticException("Negative BigInteger");
        }
        if (wide) {
            BigInteger r = any(1);
            CProver.assume(r.bitLength() > BITS / 2);
            return r;
        }
        // bit by bit, from the most significant bit of the root
        int[] r = new int[LIMBS];
        int[] sq = new int[LIMBS];
        for (int n = BITS / 2 - 1; n >= 0; n--) {
            int[] c = new int[LIMBS];
            copy(r, c);
            c[n >>> 5] |= 1 << (n & 31);
            if (!mulMag(c, c, sq) && compareMag(sq, mag) <= 0) {
                copy(c, r);
            }
        }
        return of(1, r, false);
    }

    /**
     * a^e mod m for a modulus of at most 63 bits (by products of at most
     * 126 bits); any value from 0 to m - 1 otherwise.
     */
    public BigInteger modPow(BigInteger exponent, BigInteger m) {
        if (m.signum <= 0) {
            throw new ArithmeticException("BigInteger: modulus not positive");
        }
        if (exponent.signum < 0) {
            return modInverse(m).modPow(exponent.negate(), m);
        }
        if (m.wide || m.bitLength() > 63) {
            return anyBelow(m);
        }
        if (exponent.wide) {
            return anyBelow(m);
        }
        if (m.equals(ONE)) {
            return ZERO;
        }
        BigInteger base = mod(m);
        BigInteger r = ONE;
        for (int n = BITS - 1; n >= 0; n--) {
            if (n < exponent.bitLength()) {
                r = r.multiply(r).mod(m);
                if (exponent.testBit(n)) {
                    r = r.multiply(base).mod(m);
                }
            }
        }
        return r;
    }

    /**
     * The inverse mod m, for a modulus of at most 63 bits (by the extended
     * Euclidean algorithm on longs); any value otherwise, where Java may
     * also find none.
     */
    public BigInteger modInverse(BigInteger m) {
        if (m.signum <= 0) {
            throw new ArithmeticException("BigInteger: modulus not positive");
        }
        if (m.equals(ONE)) {
            return ZERO;
        }
        if (m.wide || m.bitLength() > 63) {
            if (CProver.nondetBoolean()) {
                throw new ArithmeticException("BigInteger not invertible.");
            }
            return anyBelow(m);
        }
        long mod = m.longValue();
        long a = mod(m).longValue();
        long r0 = mod;
        long r1 = a;
        // the coefficients of a, as BigIntegers of at most 64 bits
        BigInteger t0 = ZERO;
        BigInteger t1 = ONE;
        // at most 92 steps for 63 bits (Fibonacci)
        for (int n = 0; n < 94; n++) {
            if (r1 != 0) {
                long q = r0 / r1;
                long r2 = r0 - q * r1;
                BigInteger t2 = t0.subtract(valueOf(q).multiply(t1));
                r0 = r1;
                r1 = r2;
                t0 = t1;
                t1 = t2;
            }
        }
        if (r0 != 1) {
            throw new ArithmeticException("BigInteger not invertible.");
        }
        return t0.mod(m);
    }

    /**
     * DIFFBLUE MODEL LIBRARY
     * Whether the absolute value is probably a prime: true for a prime (as
     * Java, exactly below 2^16), and for a certainty of 0 or less; false
     * for 0, 1 and even numbers but 2; any answer for other composite
     * numbers, which Java finds composite only with a probability.
     */
    public boolean isProbablePrime(int certainty) {
        if (certainty <= 0) {
            return true;
        }
        BigInteger w = abs();
        if (w.equals(TWO)) {
            return true;
        }
        if (!w.wide && ((w.mag[0] & 1) == 0 || isOne(w.mag))) {
            return false;
        }
        if (!w.wide && isZero(copyHigh(w.mag)) && (w.mag[0] >>> 16) == 0) {
            int v = w.mag[0];
            boolean prime = true;
            for (int d = 3; d < 256; d += 2) {
                if (d * d <= v && v % d == 0) {
                    prime = false;
                }
            }
            if (prime) {
                return true;
            }
        }
        return CProver.nondetBoolean();
    }

    // DIFFBLUE MODEL LIBRARY Bits.

    static void shiftLeftMag(int[] a, int n, int[] r) {
        int limbs = n >>> 5;
        int bits = n & 31;
        int[] t = new int[LIMBS];
        for (int i = 0; i < LIMBS; i++) {
            int lo = i - limbs;
            int v = lo >= 0 ? a[lo] << bits : 0;
            if (bits != 0 && lo - 1 >= 0) {
                v |= a[lo - 1] >>> (32 - bits);
            }
            t[i] = v;
        }
        copy(t, r);
    }

    static void shiftRightMag(int[] a, int n, int[] r) {
        int limbs = n >>> 5;
        int bits = n & 31;
        int[] t = new int[LIMBS];
        for (int i = 0; i < LIMBS; i++) {
            int hi = i + limbs;
            int v = hi < LIMBS ? a[hi] >>> bits : 0;
            if (bits != 0 && hi + 1 < LIMBS) {
                v |= a[hi + 1] << (32 - bits);
            }
            t[i] = v;
        }
        copy(t, r);
    }

    public BigInteger shiftLeft(int n) {
        if (signum == 0) {
            return ZERO;
        }
        if (n < 0) {
            // (-Integer.MIN_VALUE is negative: a shift right by 2^31)
            return n == Integer.MIN_VALUE ? (signum < 0 ? NEGATIVE_ONE : ZERO)
                : shiftRight(-n);
        }
        if (n == 0) {
            return this;
        }
        if (wide) {
            return wideMayOverflow(signum);
        }
        long length = bitLengthMag(mag);
        if (length + n > Integer.MAX_VALUE) {
            return overflow();
        }
        if (length + n > BITS) {
            return wide(signum);
        }
        int[] r = new int[LIMBS];
        shiftLeftMag(mag, n, r);
        return of(signum, r, false);
    }

    /**
     * The ArithmeticException of a magnitude of 2^31 bits or more, beyond
     * the range of Java.
     */
    static BigInteger overflow() {
        throw new ArithmeticException(
            "BigInteger would overflow supported range");
    }

    public BigInteger shiftRight(int n) {
        if (signum == 0) {
            return ZERO;
        }
        if (n < 0) {
            return n == Integer.MIN_VALUE ? overflow() : shiftLeft(-n);
        }
        if (n == 0) {
            return this;
        }
        if (wide) {
            // toward minus infinity: a negative value stays negative
            return any(signum < 0 ? -1 : 2);
        }
        if (n >= BITS) {
            return signum < 0 ? NEGATIVE_ONE : ZERO;
        }
        int[] r = new int[LIMBS];
        shiftRightMag(mag, n, r);
        if (signum < 0) {
            // floor: round the magnitude up where bits were lost
            int[] back = new int[LIMBS];
            shiftLeftMag(r, n, back);
            if (compareMag(back, mag) != 0) {
                int[] one = new int[LIMBS];
                one[0] = 1;
                addMag(r, one, r);
            }
        }
        return of(signum, r, false);
    }

    public BigInteger and(BigInteger val) {
        if (wide || val.wide) {
            return anyBitwise(val, 0);
        }
        int[] a = twos();
        int[] b = val.twos();
        int[] r = new int[LIMBS + 1];
        for (int i = 0; i <= LIMBS; i++) {
            r[i] = a[i] & b[i];
        }
        return fromTwos(r);
    }

    public BigInteger or(BigInteger val) {
        if (wide || val.wide) {
            return anyBitwise(val, 1);
        }
        int[] a = twos();
        int[] b = val.twos();
        int[] r = new int[LIMBS + 1];
        for (int i = 0; i <= LIMBS; i++) {
            r[i] = a[i] | b[i];
        }
        return fromTwos(r);
    }

    public BigInteger xor(BigInteger val) {
        if (wide || val.wide) {
            return anyBitwise(val, 2);
        }
        int[] a = twos();
        int[] b = val.twos();
        int[] r = new int[LIMBS + 1];
        for (int i = 0; i <= LIMBS; i++) {
            r[i] = a[i] ^ b[i];
        }
        return fromTwos(r);
    }

    public BigInteger andNot(BigInteger val) {
        if (wide || val.wide) {
            return anyBitwise(val, 3);
        }
        int[] a = twos();
        int[] b = val.twos();
        int[] r = new int[LIMBS + 1];
        for (int i = 0; i <= LIMBS; i++) {
            r[i] = a[i] & ~b[i];
        }
        return fromTwos(r);
    }

    /**
     * Any result of a bitwise operation with a wide value, of the sign of
     * that operation on the signs (two's complement: a negative value has
     * the sign bit): and (0), or (1), xor (2), andNot (3).
     */
    BigInteger anyBitwise(BigInteger val, int op) {
        boolean a = signum < 0;
        boolean b = val.signum < 0;
        boolean negative = op == 0 ? a && b : op == 1 ? a || b
            : op == 2 ? a != b : a && !b;
        BigInteger r = any();
        CProver.assume((r.signum < 0) == negative);
        return r;
    }

    public BigInteger not() {
        // -this - 1
        return negate().subtract(ONE);
    }

    public boolean testBit(int n) {
        if (n < 0) {
            throw new ArithmeticException("Negative bit address");
        }
        if (wide) {
            return CProver.nondetBoolean();
        }
        if (n >= BITS) {
            return signum < 0;
        }
        int[] t = twos();
        return ((t[n >>> 5] >>> (n & 31)) & 1) != 0;
    }

    public BigInteger setBit(int n) {
        if (n < 0) {
            throw new ArithmeticException("Negative bit address");
        }
        return or(ONE.shiftLeft(n));
    }

    public BigInteger clearBit(int n) {
        if (n < 0) {
            throw new ArithmeticException("Negative bit address");
        }
        return andNot(ONE.shiftLeft(n));
    }

    public BigInteger flipBit(int n) {
        if (n < 0) {
            throw new ArithmeticException("Negative bit address");
        }
        return xor(ONE.shiftLeft(n));
    }

    public int getLowestSetBit() {
        if (signum == 0) {
            return -1;
        }
        if (wide) {
            int n = CProver.nondetInt();
            CProver.assume(n >= 0);
            return n;
        }
        // (the same for the magnitude and the two's complement)
        return trailingZeros(mag);
    }

    public int bitLength() {
        if (wide) {
            int n = CProver.nondetInt();
            CProver.assume(n >= BITS);
            return n;
        }
        int n = bitLengthMag(mag);
        if (signum < 0) {
            // a power of two is one bit shorter in two's complement
            int[] m1 = new int[LIMBS];
            int[] one = new int[LIMBS];
            one[0] = 1;
            subMag(mag, one, m1);
            n = bitLengthMag(m1);
        }
        return n;
    }

    public int bitCount() {
        if (wide) {
            int n = CProver.nondetInt();
            CProver.assume(n >= 0);
            return n;
        }
        // of the bits that differ from the sign bit
        int[] m = mag;
        if (signum < 0) {
            m = new int[LIMBS];
            int[] one = new int[LIMBS];
            one[0] = 1;
            subMag(mag, one, m);
        }
        int n = 0;
        for (int i = 0; i < LIMBS; i++) {
            n += Integer.bitCount(m[i]);
        }
        return n;
    }

    // DIFFBLUE MODEL LIBRARY Comparison.

    public int compareTo(BigInteger val) {
        if (!wide && !val.wide) {
            if (signum != val.signum) {
                return signum < val.signum ? -1 : 1;
            }
            int c = compareMag(mag, val.mag);
            return signum < 0 ? -c : c;
        }
        if (signum != val.signum) {
            return signum < val.signum ? -1 : 1;
        }
        // the same sign: a wide value is the larger in magnitude
        if (!val.wide) {
            return signum;
        }
        if (!wide) {
            return -signum;
        }
        int r = CProver.nondetInt();
        CProver.assume(r >= -1 && r <= 1);
        return r;
    }

    public boolean equals(Object x) {
        if (x == this) {
            return true;
        }
        if (!(x instanceof BigInteger)) {
            return false;
        }
        BigInteger val = (BigInteger) x;
        if (!wide && !val.wide) {
            return signum == val.signum && compareMag(mag, val.mag) == 0;
        }
        if (wide != val.wide || signum != val.signum) {
            return false;
        }
        return CProver.nondetBoolean();
    }

    public int hashCode() {
        if (wide) {
            return CProver.nondetInt();
        }
        // as OpenJDK, from the most significant non-zero limb (the zero ones
        // before it add nothing)
        int h = 0;
        for (int i = LIMBS - 1; i >= 0; i--) {
            h = (int) (31 * h + (mag[i] & LONG_MASK));
        }
        return h * signum;
    }

    // DIFFBLUE MODEL LIBRARY Conversions.

    /** Whether the value is in the range of long. */
    boolean fitsLong() {
        if (wide || mag[2] != 0 || mag[3] != 0) {
            return false;
        }
        long m = (mag[0] & LONG_MASK) | ((long) mag[1] << 32);
        return m >= 0 || (signum < 0 && m == Long.MIN_VALUE);
    }

    public String toString(int radix) {
        if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX) {
            radix = 10;
        }
        if (wide) {
            // at least the digits of 2^BITS
            String s = CProver.nondetWithoutNull("");
            CProver.assume(s.length() >= (signum < 0 ? 2 : 1) + BITS / 5);
            return s;
        }
        if (mag[2] == 0 && mag[3] == 0) {
            // (constant for a value of a long, which leaves the chunks below
            // to the values of more limbs)
            return toStringOf64(radix);
        }
        // chunks of the digits of an int (radix^perInt < 2^31), the least
        // significant first
        int perInt = DIGITS_PER_INT[radix];
        long chunk = 1;
        for (int i = 0; i < 30; i++) {
            if (i < perInt) {
                chunk *= radix;
            }
        }
        int[] m = new int[LIMBS];
        copy(mag, m);
        String s = "";
        boolean more = true;
        // (a chunk has 25 bits or more)
        for (int k = 0; k < BITS / 25 + 1; k++) {
            if (more) {
                long rem = divLimbMag(m, chunk, m);
                more = !isZero(m);
                String c = Long.toString(rem, radix);
                if (more) {
                    // the zeros of the chunk
                    String zeros = "000000000000000000000000000000";
                    c = CProverString.substring(zeros, 0, perInt - c.length()) + c;
                }
                s = c + s;
            }
        }
        return signum < 0 ? "-" + s : s;
    }

    public String toString() {
        return toString(10);
    }

    /** toString(radix) of a magnitude of at most 64 bits. */
    String toStringOf64(int radix) {
        long m = (mag[0] & LONG_MASK) | ((long) mag[1] << 32);
        String s;
        if (m >= 0) {
            s = Long.toString(m, radix);
        } else {
            // 2^63 or more: the last digit apart
            long q = ((m >>> 1) / radix) << 1;
            long r = m - q * radix;
            if (r >= radix) {
                q++;
                r -= radix;
            }
            s = Long.toString(q, radix) + (char) (r < 10 ? '0' + r : 'a' + r - 10);
        }
        return signum < 0 ? "-" + s : s;
    }

    public byte[] toByteArray() {
        if (wide) {
            int n = CProver.nondetInt();
            CProver.assume(n > BITS / 8);
            byte[] b = new byte[n];
            b[0] = (byte) (signum < 0 ? -1 : 0);
            return b;
        }
        int len = bitLength() / 8 + 1;
        int[] t = twos();
        byte[] b = new byte[len];
        for (int i = 0; i < len; i++) {
            int k = len - 1 - i;
            b[i] = (byte) (t[k >>> 2] >>> (8 * (k & 3)));
        }
        return b;
    }

    public long longValue() {
        if (wide) {
            return CProver.nondetLong();
        }
        long m = (mag[0] & LONG_MASK) | ((long) mag[1] << 32);
        return signum < 0 ? -m : m;
    }

    public int intValue() {
        return (int) longValue();
    }

    public long longValueExact() {
        if (fitsLong()) {
            return longValue();
        }
        throw new ArithmeticException("BigInteger out of long range");
    }

    public int intValueExact() {
        if (fitsLong() && longValue() == (int) longValue()) {
            return (int) longValue();
        }
        throw new ArithmeticException("BigInteger out of int range");
    }

    public short shortValueExact() {
        if (fitsLong() && longValue() == (short) longValue()) {
            return (short) longValue();
        }
        throw new ArithmeticException("BigInteger out of short range");
    }

    public byte byteValueExact() {
        if (fitsLong() && longValue() == (byte) longValue()) {
            return (byte) longValue();
        }
        throw new ArithmeticException("BigInteger out of byte range");
    }

    /**
     * The magnitude rounded to 63 bits, and the power of two by which to
     * multiply it: the bits below the 63 are one sticky bit, so that a
     * conversion of the long rounds as one of the magnitude.
     */
    long roundedTop() {
        int length = bitLengthMag(mag);
        int shift = length > 63 ? length - 63 : 0;
        int[] t = new int[LIMBS];
        shiftRightMag(mag, shift, t);
        long top = (t[0] & LONG_MASK) | ((long) t[1] << 32);
        int[] back = new int[LIMBS];
        shiftLeftMag(t, shift, back);
        if (compareMag(back, mag) != 0) {
            top |= 1;
        }
        return top;
    }

    int roundedShift() {
        int length = bitLengthMag(mag);
        return length > 63 ? length - 63 : 0;
    }

    public double doubleValue() {
        if (wide) {
            // 2^BITS or more in magnitude, or an infinity
            double d = CProver.nondetDouble();
            CProver.assume(d >= 0x1p128 || d == Double.POSITIVE_INFINITY);
            return signum < 0 ? -d : d;
        }
        if (fitsLong()) {
            return (double) longValue();
        }
        double d = (double) roundedTop();
        int shift = roundedShift();
        // exact multiplications by powers of two (shift <= BITS - 63)
        double p = 1;
        for (int i = 0; i < BITS - 63; i++) {
            if (i < shift) {
                p *= 2;
            }
        }
        return signum * (d * p);
    }

    public float floatValue() {
        if (wide) {
            // 2^128 or more rounds to an infinity
            float f = CProver.nondetFloat();
            CProver.assume(BITS >= 128 ? f == Float.POSITIVE_INFINITY
                : f >= 0x1p127f);
            return signum < 0 ? -f : f;
        }
        if (fitsLong()) {
            return (float) longValue();
        }
        float f = (float) roundedTop();
        int shift = roundedShift();
        float p = 1;
        for (int i = 0; i < BITS - 63; i++) {
            if (i < shift) {
                p *= 2;
            }
        }
        return signum * (f * p);
    }

    /**
     * DIFFBLUE MODEL LIBRARY
     * Whether the value is wide (see the class): the encoding of
     * String.format gives its digits only where it is not.
     */
    boolean cproverIsWide() {
        return wide;
    }
}
