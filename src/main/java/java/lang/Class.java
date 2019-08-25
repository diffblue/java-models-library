/*
 * Copyright (c) 1994, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java.lang;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.cprover.CProverString;

// DIFFBLUE MODEL LIBRARY
// removed for compatibility with Java 9 and newer
// import sun.reflect.CallerSensitive;
// import sun.reflect.Reflection;

import org.cprover.CProver;

public final class Class<T> {

    private Class() {}

    private transient String name;

    // TODO: these boolean fields model the internal encoding of classes
    // they should be set by the getClass methods of the different classes
    private boolean cproverIsAnnotation;
    private boolean cproverIsArray;
    private boolean cproverIsInterface;
    private boolean cproverIsSynthetic;
    private boolean cproverIsLocalClass;
    private boolean cproverIsMemberClass;
    private boolean cproverIsEnum;

    public String toString() {
        return (isInterface() ? "interface " : (isPrimitive() ? "" : "class "))
            + getName();
    }

    public String toGenericString() {
        if (isPrimitive()) {
            return toString();
        } else {
            StringBuilder sb = new StringBuilder();

            // Class modifiers are a superset of interface modifiers
            /* TODO: No implementation for modifiers yet
            int modifiers = getModifiers() & Modifier.classModifiers();
            if (modifiers != 0) {
                sb.append(Modifier.toString(modifiers));
                sb.append(' ');
            }
            */

            if (isAnnotation()) {
                sb.append('@');
            }
            if (isInterface()) { // Note: all annotation types are interfaces
                sb.append("interface");
            } else {
                if (isEnum())
                    sb.append("enum");
                else
                    sb.append("class");
            }
            sb.append(' ');
            sb.append(getName());

            /* TODO: No implementation for TypeVariable yet
            TypeVariable<?>[] typeparms = getTypeParameters();
            if (typeparms.length > 0) {
                boolean first = true;
                sb.append('<');
                for(TypeVariable<?> typeparm: typeparms) {
                    if (!first)
                        sb.append(',');
                    sb.append(typeparm.getTypeName());
                    first = false;
                }
                sb.append('>');
            }
            */
            return sb.toString();
        }
    }

    // TODO: This is a very partial model of the actual behaviour of the Java
    // forName method. The goal is to correctly model combinations of forName
    // and getName, but precisely following the JDK behaviour is more involved.
    public static Class<?> forName(String className) {
        Class c=new Class();
        c.name=className;
        return c;
    }

    public static Class<?> forName(String name, boolean initialize,
                                   ClassLoader loader)
        throws ClassNotFoundException {
        return Class.forName(name);
    }

    public boolean isInstance(Object obj) { return obj.getClass()==this; }
    public boolean isInterface() { return cproverIsInterface; }
    public boolean isArray() { return cproverIsArray; }
    public boolean isPrimitive() {
        // DIFFBLUE MODEL LIBRARY
        // We use pointer equality instead of string equality because
        // it is more efficient.
        // This will only work if the name is defined through a constant literal,
        // which should be the case for primitive classes.
        return name == "boolean" ||
                name == "char" ||
                name == "byte" ||
                name == "short" ||
                name == "int" ||
                name == "long" ||
                name == "float" ||
                name == "double"||
                name == "void";
    }

    public boolean isAnnotation() { return cproverIsAnnotation; }
    public boolean isSynthetic() { return cproverIsSynthetic; }
    public boolean isLocalClass() { return cproverIsLocalClass; }
    public boolean isMemberClass() { return cproverIsMemberClass; }
    public boolean isAnonymousClass() { return "".equals(getSimpleName()); }
    public boolean isEnum() { return cproverIsEnum; }
    private boolean isLocalOrAnonymousClass() {
        return isLocalClass() || isAnonymousClass();
    }

    public String getName() {
        // TODO: this is only for objects, and primitive types and arrays need
        // a special treatment
        return this.name;
    }

    /**
     * Returns the class loader for the class.  Some implementations may use
     * null to represent the bootstrap class loader. This method will return
     * null in such implementations if this class was loaded by the bootstrap
     * class loader.
     *
     * <p> If a security manager is present, and the caller's class loader is
     * not null and the caller's class loader is not the same as or an ancestor of
     * the class loader for the class whose class loader is requested, then
     * this method calls the security manager's {@code checkPermission}
     * method with a {@code RuntimePermission("getClassLoader")}
     * permission to ensure it's ok to access the class loader for the class.
     *
     * <p>If this object
     * represents a primitive type or void, null is returned.
     *
     * @return  the class loader that loaded the class or interface
     *          represented by this object.
     * @throws SecurityException
     *    if a security manager exists and its
     *    {@code checkPermission} method denies
     *    access to the class loader for the class.
     * @see java.lang.ClassLoader
     * @see SecurityManager#checkPermission
     * @see java.lang.RuntimePermission
     */
    // DIFFBLUE MODEL LIBRARY
    // removed for compatibility with Java 9 and newer
    // @CallerSensitive
    public ClassLoader getClassLoader() {
        ClassLoader cl = getClassLoader0();
        if (cl == null)
            return null;
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            // DIFFBLUE MODEL LIBRARY
            // removed for compatibility with Java 9 and newer
            // ClassLoader.checkClassLoaderPermission(cl, Reflection.getCallerClass());
            ClassLoader.checkClassLoaderPermission(cl, null);
        }
        return cl;
    }

    ClassLoader getClassLoader0() {
        // DIFFBLUE MODEL LIBRARY The real java.lang.Class stores a
        // `private final ClassLoader classLoader` which is initialised by the
        // jvm rather than by the constructor of this object.
        // TODO test-gen should understand this method natively.
        return null;
    }

    public String getSimpleName() {
        /* DIFFBLUE MODEL LIBRARY TODO: No implementation for getComponentType
        if (isArray())
            return getComponentType().getSimpleName()+"[]";
        */

        // DIFFBLUE MODEL LIBRARY: instead of calling getSimpleBinaryName()
        // we inline function calls and simplify
        String name = getName();
        int index = name.lastIndexOf('$');
        if(index == -1) { // top level class
            return CProverString.substring(name, name.lastIndexOf('.') + 1); // strip the package name
        }
        else {
            // DIFFBLUE MODEL LIBRARY: in the original JDK getSimpleBinary
            // looks for "$1", instead we looked for '$' and assume the next
            // character will be '1'
            CProver.assume(CProverString.charAt(name, index + 1) == '1');
            // DIFFBLUE MODEL LIBRARY: $1 should be preceded by a class name
            CProver.assume(index >= 1);
            // DIFFBLUE MODEL LIBRARY: in the original JDK getSimpleName
            // removes the digits that follow
            CProver.assume(name.length() > index + 2);
            CProver.assume(!isAsciiDigit(CProverString.charAt(name, index + 2)));
            return CProverString.substring(name, index + 2);
        }
    }

    private static boolean isAsciiDigit(char c) {
        return '0' <= c && c <= '9';
    }

    public Class getEnclosingClass() {
        int index = name.lastIndexOf("$1");
        if(index==-1)
        {
            return null;
        }
        else
        {
            String enclosing_name = CProverString.substring(name, 0, index);
            return Class.forName(enclosing_name);
        }
    }

    public String getCanonicalName() {
        if (isArray()) {
            String canonicalName = "";
            /* TODO: No implementation for getComponentType yet
            String canonicalName = getComponentType().getCanonicalName();
            */
            if (canonicalName != null)
                return canonicalName + "[]";
            else
                return null;
        }
        if (isLocalOrAnonymousClass())
            return null;
        Class<?> enclosingClass = getEnclosingClass();
        if (enclosingClass == null) { // top level class
            return getName();
        }
        else {
            String enclosingName = enclosingClass.getCanonicalName();
            if (enclosingName == null)
                return null;
            return enclosingName + "." + getSimpleName();
        }
    }

    private String getSimpleBinaryName() {
        Class<?> enclosingClass = getEnclosingClass();
        if (enclosingClass == null) // top level class
            return null;
        // Otherwise, strip the enclosing class' name
        try {
            return CProverString.substring(getName(), enclosingClass.getName().length());
        } catch (IndexOutOfBoundsException ex) {
            throw new InternalError("Malformed class name", ex);
        }
    }

    private String resolveName(String name) {
        if (name == null) {
            return name;
        }
        if (!name.startsWith("/")) {
            Class<?> c = this;
            /* TODO: No implementation for getComponentType yet
            while (c.isArray()) {
                c = c.getComponentType();
            }
            */
            String baseName = c.getName();
            int index = baseName.lastIndexOf('.');
            if (index != -1) {
                name = CProverString.substring(baseName, 0, index).replace('.', '/')
                    +"/"+name;
            }
        } else {
            name = CProverString.substring(name, 1);
        }
        return name;
    }

    public Class getSuperclass(){
        // TODO: here we assume no superclass which may not be correct
        return Class.forName(null);
    }

    public static Class getPrimitiveClass(String s){
        if("boolean".equals(s))
            return Class.forName("boolean");
        if("char".equals(s))
            return Class.forName("char");
        if("byte".equals(s))
            return Class.forName("byte");
        if("short".equals(s))
            return Class.forName("short");
        if("int".equals(s))
            return Class.forName("int");
        if("long".equals(s))
            return Class.forName("long");
        if("float".equals(s))
            return Class.forName("float");
        if("double".equals(s))
            return Class.forName("double");
        if("void".equals(s))
            return Class.forName("void");
        // TODO: we should throw an exception but this does not seem to work well
        // at the moment, so we will assume it does not happen instead.
        // throw new IllegalArgumentException("Not primitive type : " + s);
        CProver.assume(false);
        return Class.forName("");
    }

    // This version is nicer for the symbolic execution as it knows how to
    // compare integers but not Strings.
    // This method should be used instead of the String version whenever
    // possible by our models.
    // Experimenting with the test booleanValue_Fail, the String version
    // takes 8 seconds while the int version takes 3 seconds.
    static Class getPrimitiveClass(int i){
        if(i==0)
            return Class.forName("boolean");
        if(i==1)
            return Class.forName("char");
        if(i==2)
            return Class.forName("byte");
        if(i==3)
            return Class.forName("short");
        if(i==4)
            return Class.forName("int");
        if(i==5)
            return Class.forName("long");
        if(i==6)
            return Class.forName("float");
        if(i==7)
            return Class.forName("double");
        return Class.forName("void");
    }

    Map<String, T> enumConstantDirectory() {
        if (enumConstantDirectory == null) {
            T[] universe = getEnumConstantsShared();
            if (universe == null)
                throw new IllegalArgumentException(
                    getName() + " is not an enum type");
            Map<String, T> m = new HashMap<>(2 * universe.length);
            for (T constant : universe)
                m.put(((Enum<?>)constant).name(), constant);
            enumConstantDirectory = m;
        }
        return enumConstantDirectory;
    }
    private volatile transient Map<String, T> enumConstantDirectory = null;

    // This method use calls that we cannot model here and
    // would probably need to be modeled internally in our tools
    T[] getEnumConstantsShared() {
        // DIFFBLUE MODEL LIBRARY @TODO: implement this method internally in CBMC
        return CProver.nondetWithoutNullForNotModelled();
    }

    /**
     * Returns the assertion status that would be assigned to this
     * class if it were to be initialized at the time this method is invoked.
     * If this class has had its assertion status set, the most recent
     * setting will be returned; otherwise, if any package default assertion
     * status pertains to this class, the most recent setting for the most
     * specific pertinent package default assertion status is returned;
     * otherwise, if this class is not a system class (i.e., it has a
     * class loader) its class loader's default assertion status is returned;
     * otherwise, the system class default assertion status is returned.
     * <p>
     * Few programmers will have any need for this method; it is provided
     * for the benefit of the JRE itself.  (It allows a class to determine at
     * the time that it is initialized whether assertions should be enabled.)
     * Note that this method is not guaranteed to return the actual
     * assertion status that was (or will be) associated with the specified
     * class when it was (or will be) initialized.
     *
     * @return the desired assertion status of the specified class.
     * @see    java.lang.ClassLoader#setClassAssertionStatus
     * @see    java.lang.ClassLoader#setPackageAssertionStatus
     * @see    java.lang.ClassLoader#setDefaultAssertionStatus
     * @since  1.4
     */
    public boolean desiredAssertionStatus() {
        ClassLoader loader = getClassLoader();
        // If the loader is null this is a system class, so ask the VM
        if (loader == null)
            return desiredAssertionStatus0(this);

        // If the classloader has been initialized with the assertion
        // directives, ask it. Otherwise, ask the VM.
        synchronized(loader.assertionLock) {
            if (loader.classAssertionStatus != null) {
                return loader.desiredAssertionStatus(getName());
            }
        }
        return desiredAssertionStatus0(this);
    }

    // Retrieves the desired assertion status of this class from the VM
    private static boolean desiredAssertionStatus0(Class<?> clazz) {
        // DIFFBLUE MODEL LIBRARY This would normally be a native method which
        // queries the JVM.
        // TODO does this need native handling, or is this acceptable?
        return true;
    }

    // DIFFBLUE MODEL LIBRARY
    // This method is called by CBMC just after nondeterministic object creation,
    // i.e., the constraints that it specifies are enforced only on objects that
    // are passed as an argument to a method, and only at the time when they are
    // first created.
    // We generally want to make sure that all necessary invariants of the class
    // are satisfied, and potentially restrict some fields to speed up test
    // generation.
    @org.cprover.MustNotThrow
    protected void cproverNondetInitialize() {
        CProver.assume(name != null);
        CProver.assume(enumConstantDirectory == null);
    }

    // DIFFBLUE MODEL LIBRARY
    // This method is called by CBMC to try to set class constants, which can
    // avoid the time-consuming process of enumerating over the constant
    // dictionary's internal array, when generating the Class object non-
    // deterministically.
    @org.cprover.MustNotThrow
    public void cproverInitializeClassLiteral(
            String name,
            boolean isAnnotation,
            boolean isArray,
            boolean isInterface,
            boolean isSynthetic,
            boolean isLocalClass,
            boolean isMemberClass,
            boolean isEnum) {
        this.name = name;
        this.cproverIsAnnotation = isAnnotation;
        this.cproverIsArray = isArray;
        this.cproverIsInterface = isInterface;
        this.cproverIsSynthetic = isSynthetic;
        this.cproverIsLocalClass = isLocalClass;
        this.cproverIsMemberClass = isMemberClass;
        this.cproverIsEnum = isEnum;
    }

    /**
     * Returns a {@code Field} object that reflects the specified public member
     * field of the class or interface represented by this {@code Class}
     * object. The {@code name} parameter is a {@code String} specifying the
     * simple name of the desired field.
     *
     * <p> The field to be reflected is determined by the algorithm that
     * follows.  Let C be the class or interface represented by this object:
     *
     * <OL>
     * <LI> If C declares a public field with the name specified, that is the
     *      field to be reflected.</LI>
     * <LI> If no field was found in step 1 above, this algorithm is applied
     *      recursively to each direct superinterface of C. The direct
     *      superinterfaces are searched in the order they were declared.</LI>
     * <LI> If no field was found in steps 1 and 2 above, and C has a
     *      superclass S, then this algorithm is invoked recursively upon S.
     *      If C has no superclass, then a {@code NoSuchFieldException}
     *      is thrown.</LI>
     * </OL>
     *
     * <p> If this {@code Class} object represents an array type, then this
     * method does not find the {@code length} field of the array type.
     *
     * @param name the field name
     * @return the {@code Field} object of this class specified by
     *         {@code name}
     * @throws NoSuchFieldException if a field with the specified name is
     *         not found.
     * @throws NullPointerException if {@code name} is {@code null}
     * @throws SecurityException
     *         If a security manager, <i>s</i>, is present and
     *         the caller's class loader is not the same as or an
     *         ancestor of the class loader for the current class and
     *         invocation of {@link SecurityManager#checkPackageAccess
     *         s.checkPackageAccess()} denies access to the package
     *         of this class.
     *
     * @since JDK1.1
     * @jls 8.2 Class Members
     * @jls 8.3 Field Declarations
     */
    // DIFFBLUE MODEL LIBRARY
    // removed for compatibility with Java 9 and newer
    // @CallerSensitive
    public Field getField(String name) throws NoSuchFieldException, SecurityException {
        return new Field(this, name);
    }

    /**
     * Returns a {@code Method} object that reflects the specified public
     * member method of the class or interface represented by this
     * {@code Class} object. The {@code name} parameter is a
     * {@code String} specifying the simple name of the desired method. The
     * {@code parameterTypes} parameter is an array of {@code Class}
     * objects that identify the method's formal parameter types, in declared
     * order. If {@code parameterTypes} is {@code null}, it is
     * treated as if it were an empty array.
     *
     * <p> If the {@code name} is "{@code <init>}" or "{@code <clinit>}" a
     * {@code NoSuchMethodException} is raised. Otherwise, the method to
     * be reflected is determined by the algorithm that follows.  Let C be the
     * class or interface represented by this object:
     * <OL>
     * <LI> C is searched for a <I>matching method</I>, as defined below. If a
     *      matching method is found, it is reflected.</LI>
     * <LI> If no matching method is found by step 1 then:
     *   <OL TYPE="a">
     *   <LI> If C is a class other than {@code Object}, then this algorithm is
     *        invoked recursively on the superclass of C.</LI>
     *   <LI> If C is the class {@code Object}, or if C is an interface, then
     *        the superinterfaces of C (if any) are searched for a matching
     *        method. If any such method is found, it is reflected.</LI>
     *   </OL></LI>
     * </OL>
     *
     * <p> To find a matching method in a class or interface C:&nbsp; If C
     * declares exactly one public method with the specified name and exactly
     * the same formal parameter types, that is the method reflected. If more
     * than one such method is found in C, and one of these methods has a
     * return type that is more specific than any of the others, that method is
     * reflected; otherwise one of the methods is chosen arbitrarily.
     *
     * <p>Note that there may be more than one matching method in a
     * class because while the Java language forbids a class to
     * declare multiple methods with the same signature but different
     * return types, the Java virtual machine does not.  This
     * increased flexibility in the virtual machine can be used to
     * implement various language features.  For example, covariant
     * returns can be implemented with {@linkplain
     * java.lang.reflect.Method#isBridge bridge methods}; the bridge
     * method and the method being overridden would have the same
     * signature but different return types.
     *
     * <p> If this {@code Class} object represents an array type, then this
     * method does not find the {@code clone()} method.
     *
     * <p> Static methods declared in superinterfaces of the class or interface
     * represented by this {@code Class} object are not considered members of
     * the class or interface.
     *
     * @param name the name of the method
     * @param parameterTypes the list of parameters
     * @return the {@code Method} object that matches the specified
     *         {@code name} and {@code parameterTypes}
     * @throws NoSuchMethodException if a matching method is not found
     *         or if the name is "&lt;init&gt;"or "&lt;clinit&gt;".
     * @throws NullPointerException if {@code name} is {@code null}
     * @throws SecurityException
     *         If a security manager, <i>s</i>, is present and
     *         the caller's class loader is not the same as or an
     *         ancestor of the class loader for the current class and
     *         invocation of {@link SecurityManager#checkPackageAccess
     *         s.checkPackageAccess()} denies access to the package
     *         of this class.
     *
     * @jls 8.2 Class Members
     * @jls 8.4 Method Declarations
     * @since JDK1.1
     */
    // DIFFBLUE MODEL LIBRARY
    // removed for compatibility with Java 9 and newer
    // @CallerSensitive
    public Method getMethod(String name, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
        return new Method(this, name, parameterTypes);
    }

    /**
     * Returns the {@code Class} representing the component type of an
     * array.  If this class does not represent an array class this method
     * returns null.
     *
     * @return the {@code Class} representing the component type of this
     * class if this class is an array
     * @see     java.lang.reflect.Array
     * @since 1.1
     */
    public Class<?> getComponentType() {
        CProver.notModelled();
        return CProver.nondetWithoutNullForNotModelled();
    }
}
