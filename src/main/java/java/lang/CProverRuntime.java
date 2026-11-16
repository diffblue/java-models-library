package java.lang;

import org.cprover.CProver;

/**
 * DIFFBLUE MODEL LIBRARY
 * What the models know of the Java runtime that runs the program.
 */
public final class CProverRuntime {

    private CProverRuntime() {
    }

    /**
     * Whether a runtime of a version from {@code first} to {@code last} (0
     * for no bound) may run the program, by JBMC's --java-version (or the
     * version of the class files). JBMC replaces this; here, any.
     */
    public static boolean mayRunOn(int first, int last) {
        return CProver.nondetBoolean();
    }

    /**
     * The version of the runtime where only one may run the program
     * (JBMC's --java-version), 0 otherwise. JBMC replaces this; here, 0.
     */
    public static int fixedVersion() {
        return 0;
    }

    /** The version that {@link #version} chose, 0 before it did. */
    private static int chosenVersion;

    /**
     * The version of the runtime that runs the program: that of
     * {@link #fixedVersion} (a constant, so that the code of the other
     * versions folds away), or else one of those that may run it, chosen
     * once, so that all the models of a run follow the same version. A
     * version beyond the last that the models know behaves as that one.
     */
    public static int version() {
        int fixed = fixedVersion();
        if (fixed != 0) {
            return fixed;
        }
        if (chosenVersion == 0) {
            int v = CProver.nondetInt();
            // (a choice among the versions, not a restriction of the
            // inputs: one of them runs the program)
            CProver.assume(v >= 8 && v <= 1000 && mayRunOn(v, v));
            chosenVersion = v;
        }
        return chosenVersion;
    }
}
