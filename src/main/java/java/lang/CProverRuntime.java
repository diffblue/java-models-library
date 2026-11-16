package java.lang;

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
        return org.cprover.CProver.nondetBoolean();
    }
}
