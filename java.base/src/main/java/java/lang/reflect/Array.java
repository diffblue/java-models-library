/*
 * Copyright (c) 1996, 2020, Oracle and/or its affiliates. All rights reserved.
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

package java.lang.reflect;

import jdk.internal.vm.annotation.IntrinsicCandidate;

import org.cprover.CProver;

/**
 * The {@code Array} class provides static methods to dynamically create and
 * access Java arrays.
 *
 * <p>{@code Array} permits widening conversions to occur during a get or set
 * operation, but throws an {@code IllegalArgumentException} if a narrowing
 * conversion would occur.
 *
 * @author Nakul Saraiya
 * @since 1.1
 */
public final
class Array {

    /**
     * Constructor.  Class Array is not instantiable.
     */
    private Array() {}

    /**
     * Creates a new array with the specified component type and
     * length.
     * Invoking this method is equivalent to creating an array
     * as follows:
     * <blockquote>
     * <pre>
     * int[] x = {length};
     * Array.newInstance(componentType, x);
     * </pre>
     * </blockquote>
     *
     * <p>The number of dimensions of the new array must not
     * exceed 255.
     *
     * @param  componentType the {@code Class} object representing the
     *         component type of the new array
     * @param  length the length of the new array
     * @return the new array
     * @throws NullPointerException if the specified
     *         {@code componentType} parameter is null
     * @throws IllegalArgumentException if componentType is {@link
     *         Void#TYPE} or if the number of dimensions of the requested array
     *         instance exceed 255.
     * @throws NegativeArraySizeException if the specified {@code length}
     *         is negative
     */
    public static Object newInstance(Class<?> componentType, int length)
        throws NegativeArraySizeException {
        return newArray(componentType, length);
    }

    /**
     * Creates a new array
     * with the specified component type and dimensions.
     * If {@code componentType}
     * represents a non-array class or interface, the new array
     * has {@code dimensions.length} dimensions and
     * {@code componentType} as its component type. If
     * {@code componentType} represents an array class, the
     * number of dimensions of the new array is equal to the sum
     * of {@code dimensions.length} and the number of
     * dimensions of {@code componentType}. In this case, the
     * component type of the new array is the component type of
     * {@code componentType}.
     *
     * <p>The number of dimensions of the new array must not
     * exceed 255.
     *
     * @param componentType the {@code Class} object representing the component
     * type of the new array
     * @param dimensions an array of {@code int} representing the dimensions of
     * the new array
     * @return the new array
     * @throws    NullPointerException if the specified
     * {@code componentType} argument is null
     * @throws    IllegalArgumentException if the specified {@code dimensions}
     * argument is a zero-dimensional array, if componentType is {@link
     * Void#TYPE}, or if the number of dimensions of the requested array
     * instance exceed 255.
     * @throws    NegativeArraySizeException if any of the components in
     * the specified {@code dimensions} argument is negative.
     */
    public static Object newInstance(Class<?> componentType, int... dimensions)
        throws IllegalArgumentException, NegativeArraySizeException {
        return multiNewArray(componentType, dimensions);
    }

    /**
     * Returns the length of the specified array object, as an {@code int}.
     *
     * @param array the array
     * @return the length of the array
     * @throws    IllegalArgumentException if the object argument is not
     * an array
     */
    /**
     * @cprover.fullSupport Dispatches on the array's runtime type with
     *   {@code instanceof}, instead of asking the VM. The exceptions (and
     *   widening conversions of the typed getters/setters below) are the
     *   ones documented for this class. {@link #get} and the other
     *   getters and setters of this class work the same way.
     */
    @IntrinsicCandidate
    public static int getLength(Object array) {
        if (array == null)
            throw new NullPointerException();
        if (array instanceof Object[])
            return ((Object[]) array).length;
        if (array instanceof boolean[])
            return ((boolean[]) array).length;
        if (array instanceof byte[])
            return ((byte[]) array).length;
        if (array instanceof char[])
            return ((char[]) array).length;
        if (array instanceof short[])
            return ((short[]) array).length;
        if (array instanceof int[])
            return ((int[]) array).length;
        if (array instanceof long[])
            return ((long[]) array).length;
        if (array instanceof float[])
            return ((float[]) array).length;
        if (array instanceof double[])
            return ((double[]) array).length;
        throw new IllegalArgumentException("Argument is not an array");
    }

    /**
     * Returns the value of the indexed component in the specified
     * array object.  The value is automatically wrapped in an object
     * if it has a primitive type.
     *
     * @param array the array
     * @param index the index
     * @return the (possibly wrapped) value of the indexed component in
     * the specified array
     * @throws    NullPointerException If the specified object is null
     * @throws    IllegalArgumentException If the specified object is not
     * an array
     * @throws    ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to the
     * length of the specified array
     */
    /**
     * @cprover.fullSupport See {@link #getLength}.
     */
    public static Object get(Object array, int index) {
        if (array == null)
            throw new NullPointerException();
        if (array instanceof Object[])
            return ((Object[]) array)[index];
        if (array instanceof boolean[])
            return Boolean.valueOf(((boolean[]) array)[index]);
        if (array instanceof byte[])
            return Byte.valueOf(((byte[]) array)[index]);
        if (array instanceof char[])
            return Character.valueOf(((char[]) array)[index]);
        if (array instanceof short[])
            return Short.valueOf(((short[]) array)[index]);
        if (array instanceof int[])
            return Integer.valueOf(((int[]) array)[index]);
        if (array instanceof long[])
            return Long.valueOf(((long[]) array)[index]);
        if (array instanceof float[])
            return Float.valueOf(((float[]) array)[index]);
        if (array instanceof double[])
            return Double.valueOf(((double[]) array)[index]);
        throw new IllegalArgumentException("Argument is not an array");
    }

    /**
     * Returns the value of the indexed component in the specified
     * array object, as a {@code boolean}.
     *
     * @param array the array
     * @param index the index
     * @return the value of the indexed component in the specified array
     * @throws    NullPointerException If the specified object is null
     * @throws    IllegalArgumentException If the specified object is not
     * an array, or if the indexed element cannot be converted to the
     * return type by an identity or widening conversion
     * @throws    ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to the
     * length of the specified array
     * @see Array#get
     */
    /**
     * @cprover.fullSupport See {@link #getLength}.
     */
    public static boolean getBoolean(Object array, int index) {
        if (array == null)
            throw new NullPointerException();
        if (array instanceof boolean[])
            return (boolean) ((boolean[]) array)[index];
        throw cproverTypeMismatch(array);
    }

    /**
     * Returns the value of the indexed component in the specified
     * array object, as a {@code byte}.
     *
     * @param array the array
     * @param index the index
     * @return the value of the indexed component in the specified array
     * @throws    NullPointerException If the specified object is null
     * @throws    IllegalArgumentException If the specified object is not
     * an array, or if the indexed element cannot be converted to the
     * return type by an identity or widening conversion
     * @throws    ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to the
     * length of the specified array
     * @see Array#get
     */
    /**
     * @cprover.fullSupport See {@link #getLength}.
     */
    public static byte getByte(Object array, int index) {
        if (array == null)
            throw new NullPointerException();
        if (array instanceof byte[])
            return (byte) ((byte[]) array)[index];
        throw cproverTypeMismatch(array);
    }

    /**
     * Returns the value of the indexed component in the specified
     * array object, as a {@code char}.
     *
     * @param array the array
     * @param index the index
     * @return the value of the indexed component in the specified array
     * @throws    NullPointerException If the specified object is null
     * @throws    IllegalArgumentException If the specified object is not
     * an array, or if the indexed element cannot be converted to the
     * return type by an identity or widening conversion
     * @throws    ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to the
     * length of the specified array
     * @see Array#get
     */
    /**
     * @cprover.fullSupport See {@link #getLength}.
     */
    public static char getChar(Object array, int index) {
        if (array == null)
            throw new NullPointerException();
        if (array instanceof char[])
            return (char) ((char[]) array)[index];
        throw cproverTypeMismatch(array);
    }

    /**
     * Returns the value of the indexed component in the specified
     * array object, as a {@code short}.
     *
     * @param array the array
     * @param index the index
     * @return the value of the indexed component in the specified array
     * @throws    NullPointerException If the specified object is null
     * @throws    IllegalArgumentException If the specified object is not
     * an array, or if the indexed element cannot be converted to the
     * return type by an identity or widening conversion
     * @throws    ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to the
     * length of the specified array
     * @see Array#get
     */
    /**
     * @cprover.fullSupport See {@link #getLength}.
     */
    public static short getShort(Object array, int index) {
        if (array == null)
            throw new NullPointerException();
        if (array instanceof byte[])
            return (short) ((byte[]) array)[index];
        if (array instanceof short[])
            return (short) ((short[]) array)[index];
        throw cproverTypeMismatch(array);
    }

    /**
     * Returns the value of the indexed component in the specified
     * array object, as an {@code int}.
     *
     * @param array the array
     * @param index the index
     * @return the value of the indexed component in the specified array
     * @throws    NullPointerException If the specified object is null
     * @throws    IllegalArgumentException If the specified object is not
     * an array, or if the indexed element cannot be converted to the
     * return type by an identity or widening conversion
     * @throws    ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to the
     * length of the specified array
     * @see Array#get
     */
    /**
     * @cprover.fullSupport See {@link #getLength}.
     */
    public static int getInt(Object array, int index) {
        if (array == null)
            throw new NullPointerException();
        if (array instanceof byte[])
            return (int) ((byte[]) array)[index];
        if (array instanceof char[])
            return (int) ((char[]) array)[index];
        if (array instanceof short[])
            return (int) ((short[]) array)[index];
        if (array instanceof int[])
            return (int) ((int[]) array)[index];
        throw cproverTypeMismatch(array);
    }

    /**
     * Returns the value of the indexed component in the specified
     * array object, as a {@code long}.
     *
     * @param array the array
     * @param index the index
     * @return the value of the indexed component in the specified array
     * @throws    NullPointerException If the specified object is null
     * @throws    IllegalArgumentException If the specified object is not
     * an array, or if the indexed element cannot be converted to the
     * return type by an identity or widening conversion
     * @throws    ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to the
     * length of the specified array
     * @see Array#get
     */
    /**
     * @cprover.fullSupport See {@link #getLength}.
     */
    public static long getLong(Object array, int index) {
        if (array == null)
            throw new NullPointerException();
        if (array instanceof byte[])
            return (long) ((byte[]) array)[index];
        if (array instanceof char[])
            return (long) ((char[]) array)[index];
        if (array instanceof short[])
            return (long) ((short[]) array)[index];
        if (array instanceof int[])
            return (long) ((int[]) array)[index];
        if (array instanceof long[])
            return (long) ((long[]) array)[index];
        throw cproverTypeMismatch(array);
    }

    /**
     * Returns the value of the indexed component in the specified
     * array object, as a {@code float}.
     *
     * @param array the array
     * @param index the index
     * @return the value of the indexed component in the specified array
     * @throws    NullPointerException If the specified object is null
     * @throws    IllegalArgumentException If the specified object is not
     * an array, or if the indexed element cannot be converted to the
     * return type by an identity or widening conversion
     * @throws    ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to the
     * length of the specified array
     * @see Array#get
     */
    /**
     * @cprover.fullSupport See {@link #getLength}.
     */
    public static float getFloat(Object array, int index) {
        if (array == null)
            throw new NullPointerException();
        if (array instanceof byte[])
            return (float) ((byte[]) array)[index];
        if (array instanceof char[])
            return (float) ((char[]) array)[index];
        if (array instanceof short[])
            return (float) ((short[]) array)[index];
        if (array instanceof int[])
            return (float) ((int[]) array)[index];
        if (array instanceof long[])
            return (float) ((long[]) array)[index];
        if (array instanceof float[])
            return (float) ((float[]) array)[index];
        throw cproverTypeMismatch(array);
    }

    /**
     * Returns the value of the indexed component in the specified
     * array object, as a {@code double}.
     *
     * @param array the array
     * @param index the index
     * @return the value of the indexed component in the specified array
     * @throws    NullPointerException If the specified object is null
     * @throws    IllegalArgumentException If the specified object is not
     * an array, or if the indexed element cannot be converted to the
     * return type by an identity or widening conversion
     * @throws    ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to the
     * length of the specified array
     * @see Array#get
     */
    /**
     * @cprover.fullSupport See {@link #getLength}.
     */
    public static double getDouble(Object array, int index) {
        if (array == null)
            throw new NullPointerException();
        if (array instanceof byte[])
            return (double) ((byte[]) array)[index];
        if (array instanceof char[])
            return (double) ((char[]) array)[index];
        if (array instanceof short[])
            return (double) ((short[]) array)[index];
        if (array instanceof int[])
            return (double) ((int[]) array)[index];
        if (array instanceof long[])
            return (double) ((long[]) array)[index];
        if (array instanceof float[])
            return (double) ((float[]) array)[index];
        if (array instanceof double[])
            return (double) ((double[]) array)[index];
        throw cproverTypeMismatch(array);
    }

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified new value.  The new value is first
     * automatically unwrapped if the array has a primitive component
     * type.
     * @param array the array
     * @param index the index into the array
     * @param value the new value of the indexed component
     * @throws    NullPointerException If the specified object argument
     * is null
     * @throws    IllegalArgumentException If the specified object argument
     * is not an array, or if the array component type is primitive and
     * an unwrapping conversion fails
     * @throws    ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to
     * the length of the specified array
     */
    /**
     * @cprover.fullSupport See {@link #getLength}.
     */
    public static void set(Object array, int index, Object value) {
        if (array == null)
            throw new NullPointerException();
        if (array instanceof Object[]) {
            try {
                ((Object[]) array)[index] = value;
            } catch (ArrayStoreException e) {
                throw new IllegalArgumentException("array element type mismatch");
            }
            return;
        }
        if (!cproverIsPrimitiveArray(array))
            throw new IllegalArgumentException("Argument is not an array");
        if (value instanceof Boolean) {
            setBoolean(array, index, ((Boolean) value).booleanValue());
            return;
        }
        if (value instanceof Byte) {
            setByte(array, index, ((Byte) value).byteValue());
            return;
        }
        if (value instanceof Character) {
            setChar(array, index, ((Character) value).charValue());
            return;
        }
        if (value instanceof Short) {
            setShort(array, index, ((Short) value).shortValue());
            return;
        }
        if (value instanceof Integer) {
            setInt(array, index, ((Integer) value).intValue());
            return;
        }
        if (value instanceof Long) {
            setLong(array, index, ((Long) value).longValue());
            return;
        }
        if (value instanceof Float) {
            setFloat(array, index, ((Float) value).floatValue());
            return;
        }
        if (value instanceof Double) {
            setDouble(array, index, ((Double) value).doubleValue());
            return;
        }
        throw new IllegalArgumentException("argument type mismatch");
    }

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified {@code boolean} value.
     * @param array the array
     * @param index the index into the array
     * @param z the new value of the indexed component
     * @throws    NullPointerException If the specified object argument
     * is null
     * @throws    IllegalArgumentException If the specified object argument
     * is not an array, or if the specified value cannot be converted
     * to the underlying array's component type by an identity or a
     * primitive widening conversion
     * @throws    ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to
     * the length of the specified array
     * @see Array#set
     */
    /**
     * @cprover.fullSupport See {@link #getLength}.
     */
    public static void setBoolean(Object array, int index, boolean z) {
        if (array == null)
            throw new NullPointerException();
        if (array instanceof boolean[]) {
            ((boolean[]) array)[index] = (boolean) z;
            return;
        }
        throw cproverTypeMismatch(array);
    }

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified {@code byte} value.
     * @param array the array
     * @param index the index into the array
     * @param b the new value of the indexed component
     * @throws    NullPointerException If the specified object argument
     * is null
     * @throws    IllegalArgumentException If the specified object argument
     * is not an array, or if the specified value cannot be converted
     * to the underlying array's component type by an identity or a
     * primitive widening conversion
     * @throws    ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to
     * the length of the specified array
     * @see Array#set
     */
    /**
     * @cprover.fullSupport See {@link #getLength}.
     */
    public static void setByte(Object array, int index, byte b) {
        if (array == null)
            throw new NullPointerException();
        if (array instanceof byte[]) {
            ((byte[]) array)[index] = (byte) b;
            return;
        }
        if (array instanceof short[]) {
            ((short[]) array)[index] = (short) b;
            return;
        }
        if (array instanceof int[]) {
            ((int[]) array)[index] = (int) b;
            return;
        }
        if (array instanceof long[]) {
            ((long[]) array)[index] = (long) b;
            return;
        }
        if (array instanceof float[]) {
            ((float[]) array)[index] = (float) b;
            return;
        }
        if (array instanceof double[]) {
            ((double[]) array)[index] = (double) b;
            return;
        }
        throw cproverTypeMismatch(array);
    }

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified {@code char} value.
     * @param array the array
     * @param index the index into the array
     * @param c the new value of the indexed component
     * @throws    NullPointerException If the specified object argument
     * is null
     * @throws    IllegalArgumentException If the specified object argument
     * is not an array, or if the specified value cannot be converted
     * to the underlying array's component type by an identity or a
     * primitive widening conversion
     * @throws    ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to
     * the length of the specified array
     * @see Array#set
     */
    /**
     * @cprover.fullSupport See {@link #getLength}.
     */
    public static void setChar(Object array, int index, char c) {
        if (array == null)
            throw new NullPointerException();
        if (array instanceof char[]) {
            ((char[]) array)[index] = (char) c;
            return;
        }
        if (array instanceof int[]) {
            ((int[]) array)[index] = (int) c;
            return;
        }
        if (array instanceof long[]) {
            ((long[]) array)[index] = (long) c;
            return;
        }
        if (array instanceof float[]) {
            ((float[]) array)[index] = (float) c;
            return;
        }
        if (array instanceof double[]) {
            ((double[]) array)[index] = (double) c;
            return;
        }
        throw cproverTypeMismatch(array);
    }

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified {@code short} value.
     * @param array the array
     * @param index the index into the array
     * @param s the new value of the indexed component
     * @throws    NullPointerException If the specified object argument
     * is null
     * @throws    IllegalArgumentException If the specified object argument
     * is not an array, or if the specified value cannot be converted
     * to the underlying array's component type by an identity or a
     * primitive widening conversion
     * @throws    ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to
     * the length of the specified array
     * @see Array#set
     */
    /**
     * @cprover.fullSupport See {@link #getLength}.
     */
    public static void setShort(Object array, int index, short s) {
        if (array == null)
            throw new NullPointerException();
        if (array instanceof short[]) {
            ((short[]) array)[index] = (short) s;
            return;
        }
        if (array instanceof int[]) {
            ((int[]) array)[index] = (int) s;
            return;
        }
        if (array instanceof long[]) {
            ((long[]) array)[index] = (long) s;
            return;
        }
        if (array instanceof float[]) {
            ((float[]) array)[index] = (float) s;
            return;
        }
        if (array instanceof double[]) {
            ((double[]) array)[index] = (double) s;
            return;
        }
        throw cproverTypeMismatch(array);
    }

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified {@code int} value.
     * @param array the array
     * @param index the index into the array
     * @param i the new value of the indexed component
     * @throws    NullPointerException If the specified object argument
     * is null
     * @throws    IllegalArgumentException If the specified object argument
     * is not an array, or if the specified value cannot be converted
     * to the underlying array's component type by an identity or a
     * primitive widening conversion
     * @throws    ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to
     * the length of the specified array
     * @see Array#set
     */
    /**
     * @cprover.fullSupport See {@link #getLength}.
     */
    public static void setInt(Object array, int index, int i) {
        if (array == null)
            throw new NullPointerException();
        if (array instanceof int[]) {
            ((int[]) array)[index] = (int) i;
            return;
        }
        if (array instanceof long[]) {
            ((long[]) array)[index] = (long) i;
            return;
        }
        if (array instanceof float[]) {
            ((float[]) array)[index] = (float) i;
            return;
        }
        if (array instanceof double[]) {
            ((double[]) array)[index] = (double) i;
            return;
        }
        throw cproverTypeMismatch(array);
    }

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified {@code long} value.
     * @param array the array
     * @param index the index into the array
     * @param l the new value of the indexed component
     * @throws    NullPointerException If the specified object argument
     * is null
     * @throws    IllegalArgumentException If the specified object argument
     * is not an array, or if the specified value cannot be converted
     * to the underlying array's component type by an identity or a
     * primitive widening conversion
     * @throws    ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to
     * the length of the specified array
     * @see Array#set
     */
    /**
     * @cprover.fullSupport See {@link #getLength}.
     */
    public static void setLong(Object array, int index, long l) {
        if (array == null)
            throw new NullPointerException();
        if (array instanceof long[]) {
            ((long[]) array)[index] = (long) l;
            return;
        }
        if (array instanceof float[]) {
            ((float[]) array)[index] = (float) l;
            return;
        }
        if (array instanceof double[]) {
            ((double[]) array)[index] = (double) l;
            return;
        }
        throw cproverTypeMismatch(array);
    }

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified {@code float} value.
     * @param array the array
     * @param index the index into the array
     * @param f the new value of the indexed component
     * @throws    NullPointerException If the specified object argument
     * is null
     * @throws    IllegalArgumentException If the specified object argument
     * is not an array, or if the specified value cannot be converted
     * to the underlying array's component type by an identity or a
     * primitive widening conversion
     * @throws    ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to
     * the length of the specified array
     * @see Array#set
     */
    /**
     * @cprover.fullSupport See {@link #getLength}.
     */
    public static void setFloat(Object array, int index, float f) {
        if (array == null)
            throw new NullPointerException();
        if (array instanceof float[]) {
            ((float[]) array)[index] = (float) f;
            return;
        }
        if (array instanceof double[]) {
            ((double[]) array)[index] = (double) f;
            return;
        }
        throw cproverTypeMismatch(array);
    }

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified {@code double} value.
     * @param array the array
     * @param index the index into the array
     * @param d the new value of the indexed component
     * @throws    NullPointerException If the specified object argument
     * is null
     * @throws    IllegalArgumentException If the specified object argument
     * is not an array, or if the specified value cannot be converted
     * to the underlying array's component type by an identity or a
     * primitive widening conversion
     * @throws    ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to
     * the length of the specified array
     * @see Array#set
     */
    /**
     * @cprover.fullSupport See {@link #getLength}.
     */
    public static void setDouble(Object array, int index, double d) {
        if (array == null)
            throw new NullPointerException();
        if (array instanceof double[]) {
            ((double[]) array)[index] = (double) d;
            return;
        }
        throw cproverTypeMismatch(array);
    }

    /*
     * Private
     */

    /**
     * @cprover.limitedSupport Creates arrays of every primitive component
     *   type (and of dimension 1 only through {@link #multiNewArray}).
     *   Arrays of a reference component type are not supported
     *   ({@code CProver.notModelled()}): jbmc keeps an array's runtime
     *   element type as hidden fields that can only be copied from an
     *   existing array of that type (see {@code
     *   CProver.createArrayWithType}), and here only a {@code Class}
     *   object is available, so an array created for it would have the
     *   wrong runtime type and make later casts to it fail.
     */
    @IntrinsicCandidate
    private static Object newArray(Class<?> componentType, int length) {
        if (componentType == null)
            throw new NullPointerException();
        if (!componentType.isPrimitive()) {
            CProver.notModelled();
            return CProver.nondetWithoutNullForNotModelled();
        }
        if (componentType == void.class)
            throw new IllegalArgumentException();
        if (length < 0)
            throw new NegativeArraySizeException(Integer.toString(length));
        if (componentType == boolean.class)
            return new boolean[length];
        if (componentType == byte.class)
            return new byte[length];
        if (componentType == char.class)
            return new char[length];
        if (componentType == short.class)
            return new short[length];
        if (componentType == int.class)
            return new int[length];
        if (componentType == long.class)
            return new long[length];
        if (componentType == float.class)
            return new float[length];
        if (componentType == double.class)
            return new double[length];
        throw new IllegalArgumentException();
    }

    /**
     * @cprover.limitedSupport Only one dimension is supported (via {@link
     *   #newArray}); for more, the nested arrays would need static types
     *   that are not known here ({@code CProver.notModelled()}).
     */
    private static Object multiNewArray(Class<?> componentType, int[] dimensions) {
        if (componentType == null)
            throw new NullPointerException();
        if (dimensions.length == 0)
            throw new IllegalArgumentException(
                "Empty dimensions array");
        if (dimensions.length == 1)
            return newArray(componentType, dimensions[0]);
        CProver.notModelled();
        return CProver.nondetWithoutNullForNotModelled();
    }

    private static boolean cproverIsPrimitiveArray(Object array) {
        if (array instanceof boolean[])
            return true;
        if (array instanceof byte[])
            return true;
        if (array instanceof char[])
            return true;
        if (array instanceof short[])
            return true;
        if (array instanceof int[])
            return true;
        if (array instanceof long[])
            return true;
        if (array instanceof float[])
            return true;
        if (array instanceof double[])
            return true;
        return false;
    }

    private static IllegalArgumentException cproverTypeMismatch(Object array) {
        if (array instanceof Object[])
            return new IllegalArgumentException(
                "Argument is not an array of primitive type");
        if (cproverIsPrimitiveArray(array))
            return new IllegalArgumentException("argument type mismatch");
        return new IllegalArgumentException("Argument is not an array");
    }

}
