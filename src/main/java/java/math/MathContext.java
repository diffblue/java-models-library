package java.math;

/**
 * DIFFBLUE MODEL LIBRARY
 * A precision and a rounding mode for BigDecimal, as OpenJDK.
 *
 * @diffblue.fullSupport
 */
public final class MathContext implements java.io.Serializable {

    private static final int DEFAULT_DIGITS = 9;
    private static final RoundingMode DEFAULT_ROUNDINGMODE =
        RoundingMode.HALF_UP;
    private static final int MIN_DIGITS = 0;

    final int precision;
    final RoundingMode roundingMode;

    public static final MathContext UNLIMITED =
        new MathContext(0, RoundingMode.HALF_UP);
    public static final MathContext DECIMAL32 =
        new MathContext(7, RoundingMode.HALF_EVEN);
    public static final MathContext DECIMAL64 =
        new MathContext(16, RoundingMode.HALF_EVEN);
    public static final MathContext DECIMAL128 =
        new MathContext(34, RoundingMode.HALF_EVEN);

    public MathContext(int setPrecision) {
        this(setPrecision, DEFAULT_ROUNDINGMODE);
    }

    public MathContext(int setPrecision, RoundingMode setRoundingMode) {
        if (setPrecision < MIN_DIGITS) {
            throw new IllegalArgumentException("Digits < 0");
        }
        if (setRoundingMode == null) {
            throw new NullPointerException("null RoundingMode");
        }
        precision = setPrecision;
        roundingMode = setRoundingMode;
    }

    public MathContext(String val) {
        int setPrecision;
        if (val == null) {
            throw new NullPointerException("null String");
        }
        RoundingMode mode;
        try {
            if (!val.startsWith("precision=")) {
                throw new RuntimeException();
            }
            int fence = val.indexOf(' ');
            setPrecision = Integer.parseInt(val.substring(10, fence));
            if (!val.startsWith("roundingMode=", fence + 1)) {
                throw new RuntimeException();
            }
            mode = RoundingMode.valueOf(val.substring(fence + 1 + 13));
        } catch (RuntimeException re) {
            throw new IllegalArgumentException("bad string format");
        }
        if (setPrecision < MIN_DIGITS) {
            throw new IllegalArgumentException("Digits < 0");
        }
        precision = setPrecision;
        roundingMode = mode;
    }

    public int getPrecision() {
        return precision;
    }

    public RoundingMode getRoundingMode() {
        return roundingMode;
    }

    public boolean equals(Object x) {
        if (!(x instanceof MathContext)) {
            return false;
        }
        MathContext mc = (MathContext) x;
        return mc.precision == this.precision
            && mc.roundingMode == this.roundingMode;
    }

    public int hashCode() {
        return this.precision + roundingMode.hashCode() * 59;
    }

    public String toString() {
        return "precision=" + precision + " roundingMode=" + roundingMode;
    }
}
