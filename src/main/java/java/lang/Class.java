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

import java.util.HashMap;
import java.util.Map;
import org.cprover.CProver;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;

public final class Class<T> {

    private Class() {}

    private transient String name;

    // TODO: these boolean fields model the internal encoding of classes
    // they should be set by the getClass methods of the different classes
    private boolean isAnnotation;
    private boolean isArray;
    private boolean isInterface;
    private boolean isSynthetic;
    private boolean isLocalClass;
    private boolean isMemberClass;
    private boolean isEnum;

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
    public boolean isInterface() { return isInterface; }
    public boolean isArray() { return isArray; }
    public boolean isPrimitive() {
        return "java.lang.Boolean".equals(name) ||
            "java.lang.Character".equals(name) ||
            "java.lang.Byte".equals(name) ||
            "java.lang.Short".equals(name) ||
            "java.lang.Integer".equals(name) ||
            "java.lang.Long".equals(name) ||
            "java.lang.Float".equals(name) ||
            "java.lang.Double".equals(name) ||
            "java.lang.Void".equals(name);}

    public boolean isAnnotation() { return isAnnotation; }
    public boolean isSynthetic() { return isSynthetic; }
    public boolean isLocalClass() { return isLocalClass; }
    public boolean isMemberClass() { return isMemberClass; }
    public boolean isAnonymousClass() { return "".equals(getSimpleName()); }
    public boolean isEnum() { return isEnum; }
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
    @CallerSensitive
    public ClassLoader getClassLoader() {
        ClassLoader cl = getClassLoader0();
        if (cl == null)
            return null;
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            ClassLoader.checkClassLoaderPermission(cl, Reflection.getCallerClass());
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
            return name.substring(name.lastIndexOf('.') + 1); // strip the package name
        }
        else {
            // DIFFBLUE MODEL LIBRARY: in the original JDK getSimpleBinary
            // looks for "$1", instead we looked for '$' and assume the next
            // character will be '1'
            CProver.assume(name.charAt(index+1) == '1');
            // DIFFBLUE MODEL LIBRARY: $1 should be preceded by a class name
            CProver.assume(index >= 1);
            // DIFFBLUE MODEL LIBRARY: in the original JDK getSimpleName
            // removes the digits that follow
            CProver.assume(!isAsciiDigit(name.charAt(index + 2)));
            return name.substring(index + 2);
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
            String enclosing_name = name.substring(0, index);
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
            return getName().substring(enclosingClass.getName().length());
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
                name = baseName.substring(0, index).replace('.', '/')
                    +"/"+name;
            }
        } else {
            name = name.substring(1);
        }
        return name;
    }

    public Class getSuperclass(){
        // TODO: here we assume no superclass which may not be correct
        return Class.forName(null);
    }

    public static Class getPrimitiveClass(String s){
        if("boolean".equals(s))
            return Class.forName("java.lang.Boolean");
        if("char".equals(s))
            return Class.forName("java.lang.Character");
        if("byte".equals(s))
            return Class.forName("java.lang.Byte");
        if("short".equals(s))
            return Class.forName("java.lang.Short");
        if("int".equals(s))
            return Class.forName("java.lang.Integer");
        if("long".equals(s))
            return Class.forName("java.lang.Long");
        if("float".equals(s))
            return Class.forName("java.lang.Float");
        if("double".equals(s))
            return Class.forName("java.lang.Double");
        if("void".equals(s))
            return Class.forName("java.lang.Void");
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
            return Class.forName("java.lang.Boolean");
        if(i==1)
            return Class.forName("java.lang.Character");
        if(i==2)
            return Class.forName("java.lang.Byte");
        if(i==3)
            return Class.forName("java.lang.Short");
        if(i==4)
            return Class.forName("java.lang.Integer");
        if(i==5)
            return Class.forName("java.lang.Long");
        if(i==6)
            return Class.forName("java.lang.Float");
        if(i==7)
            return Class.forName("java.lang.Double");
        return Class.forName("java.lang.Void");
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
        return CProver.nondetWithoutNull(); 
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
}
