/*
 * Copyright (c) 2015, 2023, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package jdk.internal.util;

import java.util.Arrays;
import java.util.Collection;
import org.cprover.CProver;

/**
 * Utility methods to work with arrays.
 *
 * DIFFBLUE MODEL LIBRARY: the implementation of the JDK reads the arrays
 * with Unsafe (vectorizedMismatch), which is not modelled; the methods
 * compare the elements one by one instead. The results are the same.
 */
public class ArraysSupport {

    public static final int LOG2_ARRAY_BOOLEAN_INDEX_SCALE = 0;
    public static final int LOG2_ARRAY_BYTE_INDEX_SCALE = 0;
    public static final int LOG2_ARRAY_CHAR_INDEX_SCALE = 1;
    public static final int LOG2_ARRAY_SHORT_INDEX_SCALE = 1;
    public static final int LOG2_ARRAY_INT_INDEX_SCALE = 2;
    public static final int LOG2_ARRAY_LONG_INDEX_SCALE = 3;
    public static final int LOG2_ARRAY_FLOAT_INDEX_SCALE = 2;
    public static final int LOG2_ARRAY_DOUBLE_INDEX_SCALE = 3;

    // the basic types of the JVM
    public static final int T_BOOLEAN = 4;
    public static final int T_CHAR = 5;
    public static final int T_FLOAT = 6;
    public static final int T_DOUBLE = 7;
    public static final int T_BYTE = 8;
    public static final int T_SHORT = 9;
    public static final int T_INT = 10;
    public static final int T_LONG = 11;

    public static int vectorizedMismatch(Object a, long aOffset,
                                         Object b, long bOffset,
                                         int length,
                                         int log2ArrayIndexScale) {
        // reads the arrays with Unsafe
        CProver.notModelled();
        return CProver.nondetInt();
    }

    public static int vectorizedHashCode(Object array, int fromIndex, int length, int initialValue,
                                         int basicType) {
        return switch (basicType) {
            case T_BOOLEAN -> signedHashCode(initialValue, (byte[]) array, fromIndex, length);
            case T_CHAR -> array instanceof byte[]
                    ? utf16hashCode(initialValue, (byte[]) array, fromIndex, length)
                    : hashCode(initialValue, (char[]) array, fromIndex, length);
            case T_BYTE -> hashCode(initialValue, (byte[]) array, fromIndex, length);
            case T_SHORT -> hashCode(initialValue, (short[]) array, fromIndex, length);
            case T_INT -> hashCode(initialValue, (int[]) array, fromIndex, length);
            default -> throw new IllegalArgumentException("unrecognized basic type: " + basicType);
        };
    }

    private static int signedHashCode(int result, byte[] a, int fromIndex, int length) {
        int end = fromIndex + length;
        for (int i = fromIndex; i < end; i++) {
            result = 31 * result + (a[i] & 0xff);
        }
        return result;
    }

    private static int hashCode(int result, byte[] a, int fromIndex, int length) {
        int end = fromIndex + length;
        for (int i = fromIndex; i < end; i++) {
            result = 31 * result + a[i];
        }
        return result;
    }

    private static int hashCode(int result, char[] a, int fromIndex, int length) {
        int end = fromIndex + length;
        for (int i = fromIndex; i < end; i++) {
            result = 31 * result + a[i];
        }
        return result;
    }

    private static int hashCode(int result, short[] a, int fromIndex, int length) {
        int end = fromIndex + length;
        for (int i = fromIndex; i < end; i++) {
            result = 31 * result + a[i];
        }
        return result;
    }

    private static int hashCode(int result, int[] a, int fromIndex, int length) {
        int end = fromIndex + length;
        for (int i = fromIndex; i < end; i++) {
            result = 31 * result + a[i];
        }
        return result;
    }

    /*
     * fromIndex and length must be scaled to char indexes.
     */
    public static int utf16hashCode(int result, byte[] value, int fromIndex, int length) {
        int end = fromIndex + length;
        for (int i = fromIndex; i < end; i++) {
            // little endian, as StringUTF16.getChar
            char c = (char) ((value[2 * i] & 0xff) | ((value[2 * i + 1] & 0xff) << 8));
            result = 31 * result + c;
        }
        return result;
    }

    public static int mismatch(boolean[] a, boolean[] b, int length) {
        return mismatch(a, 0, b, 0, length);
    }

    public static int mismatch(boolean[] a, int aFromIndex,
                               boolean[] b, int bFromIndex,
                               int length) {
        for (int i = 0; i < length; i++) {
            if (a[aFromIndex + i] != b[bFromIndex + i])
                return i;
        }
        return -1;
    }

    public static int mismatch(byte[] a, byte[] b, int length) {
        return mismatch(a, 0, b, 0, length);
    }

    public static int mismatch(byte[] a, int aFromIndex,
                               byte[] b, int bFromIndex,
                               int length) {
        for (int i = 0; i < length; i++) {
            if (a[aFromIndex + i] != b[bFromIndex + i])
                return i;
        }
        return -1;
    }

    public static int mismatch(char[] a, char[] b, int length) {
        return mismatch(a, 0, b, 0, length);
    }

    public static int mismatch(char[] a, int aFromIndex,
                               char[] b, int bFromIndex,
                               int length) {
        for (int i = 0; i < length; i++) {
            if (a[aFromIndex + i] != b[bFromIndex + i])
                return i;
        }
        return -1;
    }

    public static int mismatch(short[] a, short[] b, int length) {
        return mismatch(a, 0, b, 0, length);
    }

    public static int mismatch(short[] a, int aFromIndex,
                               short[] b, int bFromIndex,
                               int length) {
        for (int i = 0; i < length; i++) {
            if (a[aFromIndex + i] != b[bFromIndex + i])
                return i;
        }
        return -1;
    }

    public static int mismatch(int[] a, int[] b, int length) {
        return mismatch(a, 0, b, 0, length);
    }

    public static int mismatch(int[] a, int aFromIndex,
                               int[] b, int bFromIndex,
                               int length) {
        for (int i = 0; i < length; i++) {
            if (a[aFromIndex + i] != b[bFromIndex + i])
                return i;
        }
        return -1;
    }

    public static int mismatch(long[] a, long[] b, int length) {
        return mismatch(a, 0, b, 0, length);
    }

    public static int mismatch(long[] a, int aFromIndex,
                               long[] b, int bFromIndex,
                               int length) {
        for (int i = 0; i < length; i++) {
            if (a[aFromIndex + i] != b[bFromIndex + i])
                return i;
        }
        return -1;
    }

    public static int mismatch(float[] a, float[] b, int length) {
        return mismatch(a, 0, b, 0, length);
    }

    public static int mismatch(float[] a, int aFromIndex,
                               float[] b, int bFromIndex,
                               int length) {
        for (int i = 0; i < length; i++) {
            if (Float.floatToIntBits(a[aFromIndex + i]) != Float.floatToIntBits(b[bFromIndex + i]))
                return i;
        }
        return -1;
    }

    public static int mismatch(double[] a, double[] b, int length) {
        return mismatch(a, 0, b, 0, length);
    }

    public static int mismatch(double[] a, int aFromIndex,
                               double[] b, int bFromIndex,
                               int length) {
        for (int i = 0; i < length; i++) {
            if (Double.doubleToLongBits(a[aFromIndex + i]) != Double.doubleToLongBits(b[bFromIndex + i]))
                return i;
        }
        return -1;
    }

    public static final int SOFT_MAX_ARRAY_LENGTH = Integer.MAX_VALUE - 8;

    /**
     * Computes a new array length given an array's current length, a minimum growth
     * amount, and a preferred growth amount. The computation is done in an overflow-safe
     * fashion.
     *
     * This method is used by objects that contain an array that might need to be grown
     * in order to fulfill some immediate need (the minimum growth amount) but would also
     * like to request more space (the preferred growth amount) in order to accommodate
     * potential future needs. The returned length is usually clamped at the soft maximum
     * length in order to avoid hitting the JVM implementation limit. However, the soft
     * maximum will be exceeded if the minimum growth amount requires it.
     *
     * If the preferred growth amount is less than the minimum growth amount, the
     * minimum growth amount is used as the preferred growth amount.
     *
     * The preferred length is determined by adding the preferred growth amount to the
     * current length. If the preferred length does not exceed the soft maximum length
     * (SOFT_MAX_ARRAY_LENGTH) then the preferred length is returned.
     *
     * If the preferred length exceeds the soft maximum, we use the minimum growth
     * amount. The minimum required length is determined by adding the minimum growth
     * amount to the current length. If the minimum required length exceeds Integer.MAX_VALUE,
     * then this method throws OutOfMemoryError. Otherwise, this method returns the greater of
     * the soft maximum or the minimum required length.
     *
     * Note that this method does not do any array allocation itself; it only does array
     * length growth computations. However, it will throw OutOfMemoryError as noted above.
     *
     * Note also that this method cannot detect the JVM's implementation limit, and it
     * may compute and return a length value up to and including Integer.MAX_VALUE that
     * might exceed the JVM's implementation limit. In that case, the caller will likely
     * attempt an array allocation with that length and encounter an OutOfMemoryError.
     * Of course, regardless of the length value returned from this method, the caller
     * may encounter OutOfMemoryError if there is insufficient heap to fulfill the request.
     *
     * @param oldLength   current length of the array (must be nonnegative)
     * @param minGrowth   minimum required growth amount (must be positive)
     * @param prefGrowth  preferred growth amount
     * @return the new array length
     * @throws OutOfMemoryError if the new length would exceed Integer.MAX_VALUE
     */
    public static int newLength(int oldLength, int minGrowth, int prefGrowth) {
        // preconditions not checked because of inlining
        // assert oldLength >= 0
        // assert minGrowth > 0

        int prefLength = oldLength + Math.max(minGrowth, prefGrowth); // might overflow
        if (0 < prefLength && prefLength <= SOFT_MAX_ARRAY_LENGTH) {
            return prefLength;
        } else {
            // put code cold in a separate method
            return hugeLength(oldLength, minGrowth);
        }
    }

    private static int hugeLength(int oldLength, int minGrowth) {
        int minLength = oldLength + minGrowth;
        if (minLength < 0) { // overflow
            throw new OutOfMemoryError(
                "Required array length " + oldLength + " + " + minGrowth + " is too large");
        } else if (minLength <= SOFT_MAX_ARRAY_LENGTH) {
            return SOFT_MAX_ARRAY_LENGTH;
        } else {
            return minLength;
        }
    }

    /**
     * Reverses the elements of an array in-place.
     *
     * @param <T> the array component type
     * @param a the array to be reversed
     * @return the reversed array, always the same array as the argument
     */
    public static <T> T[] reverse(T[] a) {
        int limit = a.length / 2;
        for (int i = 0, j = a.length - 1; i < limit; i++, j--) {
            T t = a[i];
            a[i] = a[j];
            a[j] = t;
        }
        return a;
    }

    /**
     * Dump the contents of the given collection into the given array, in reverse order.
     * This mirrors the semantics of Collection.toArray(T[]) in regard to reusing the given
     * array, appending null if necessary, or allocating a new array of the same component type.
     * <p>
     * A constraint is that this method should issue exactly one method call on the collection
     * to obtain the elements and the size. Having a separate size() call or using an Iterator
     * could result in errors if the collection changes size between calls. This implies that
     * the elements need to be obtained via a single call to one of the toArray() methods.
     * This further implies allocating memory proportional to the number of elements and
     * making an extra copy, but this seems unavoidable.
     * <p>
     * An obvious approach would be simply to call coll.toArray(array) and then reverse the
     * order of the elements. This doesn't work, because if given array is sufficiently long,
     * we cannot tell how many elements were copied into it and thus there is no way to reverse
     * the right set of elements while leaving the remaining array elements undisturbed.
     *
     * @throws ArrayStoreException if coll contains elements that can't be stored in the array
     */
    public static <T> T[] toArrayReversed(Collection<?> coll, T[] array) {
        T[] newArray = reverse(coll.toArray(Arrays.copyOfRange(array, 0, 0)));
        if (newArray.length > array.length) {
            return newArray;
        } else {
            System.arraycopy(newArray, 0, array, 0, newArray.length);
            if (array.length > newArray.length) {
                array[newArray.length] = null;
            }
            return array;
        }
    }
}
