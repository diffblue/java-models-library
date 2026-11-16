package java.math;

/**
 * DIFFBLUE MODEL LIBRARY
 * Helpers of the models of other packages, which need the internals of
 * those of java.math.
 */
public final class CProverMath {

    private CProverMath() {
    }

    /**
     * The string that java.util.Formatter prints for {@code value} with the
     * conversion {@code c} ('e', 'f' or 'g'), the precision (-1 for none)
     * and the flag # ({@code alternate}), before its decoration (see
     * BigDecimal.cproverFormat).
     */
    public static String formatDecimal(BigDecimal value, char c,
            int precision, boolean alternate) {
        return value.cproverFormat(c, precision, alternate);
    }
}
