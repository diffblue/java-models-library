package java.util.regex;

import org.cprover.CProver;

/**
 * DIFFBLUE MODEL LIBRARY
 * The syntax check of a regular expression, for Pattern.compile and the
 * regex methods of String. Pattern rejects a malformed regex with a
 * PatternSyntaxException; JBMC knows a regex to be valid where its
 * translation supports it (cproverSupported, replaced by JBMC: true for a
 * constant regex that the translation supports, which rejects every syntax
 * error, false otherwise). Any other regex may throw, with any description
 * and index: an over-approximation for valid regexes that the translation
 * does not support (JBMC warns about those).
 */
public final class CProverRegex {

    private CProverRegex() {
    }

    /** Handled internally by CBMC (see above). */
    private static boolean cproverSupported(String regex) {
        return CProver.nondetBoolean();
    }

    /**
     * The check of {@code regex} with the flags of Pattern.compile: a null
     * regex throws (as the JDK reads its length), LITERAL has no syntax,
     * and the translation, without flags, does not decide the syntax of
     * other flags (COMMENTS rejects "[ ]").
     */
    public static void checkSyntax(String regex, int flags) {
        regex.length();
        if ((flags & Pattern.LITERAL) != 0) {
            return;
        }
        if ((flags != 0 || !cproverSupported(regex))
            && CProver.nondetBoolean()) {
            // (each nondet in a variable of its own, as JBMC wants them)
            String description = CProver.nondetWithoutNull("");
            // (the indices of Pattern's errors are -1 or a position of the
            // regex, up to its length)
            int index = CProver.nondetInt();
            if (index < -1 || index > regex.length()) {
                index = -1;
            }
            throw new PatternSyntaxException(description, regex, index);
        }
    }
}
