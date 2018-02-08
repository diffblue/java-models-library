package org.cprover;

/// This class provides an interface with string functions modeled internally
/// in CProver, for which the CProver model differs from the JDK actual behavior.
/// This is in particular the case for functions that throw exceptions.
public final class CProverString
{
    /// Modeled internally in CBMC.
    /// @return '\u0000' if index is out of bounds and behave as s.charAt(i)
    ///         otherwise.
    public static char charAt(String s, int i) {
        return CProver.nondetChar();
    }

    /// Modeled internally in CBMC.
    /// @return empty string if index is too large, s if index too small and
    ///         behave as s.substring(i) otherwise.
    public static String substring(String s, int i) {
        return CProver.nondetWithoutNull();
    }

    /// Modeled internally in CBMC.
    /// @return empty string if i >= j, s.substring(k, l) where k = max(0, i)
    ///         and l = min(s.length() - 1, j) otherwise.
    public static String substring(String s, int i, int j) {
        return CProver.nondetWithoutNull();
    }
}
