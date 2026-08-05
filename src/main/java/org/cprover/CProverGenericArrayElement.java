/*
 * Field-level annotation for modeled-collection backing
 * arrays whose element type is logically the enclosing
 * class's generic K (or V, E, ...) parameter, but which
 * Java's type erasure forces to declare as Object[].
 *
 * Example: in an array-backed map model that stores keys and
 * values in parallel {@code Object[]} arrays,
 *
 *     @CProverGenericArrayElement("K")
 *     private Object[] keys;
 *
 *     @CProverGenericArrayElement("V")
 *     private Object[] values;
 *
 * tells JBMC's object factory that, when nondet-initializing
 * a {@code HashMap<Integer, SpecValue>} instance, the
 * keys[] elements should be allocated as {@code Integer}
 * instances and the values[] elements as {@code SpecValue}
 * instances — rather than as generic {@code Object}
 * instances which won't match {@code .equals(Integer.valueOf(n))}
 * in containsKey(...).
 *
 * Without this annotation, lemmas with preconditions like
 * {@code precondition(map.containsKey(absN))} are vacuously
 * SUCCESSFUL because the factory cannot generate a satisfying
 * keys[i].equals(Integer.valueOf(absN)) state — it allocates
 * keys[i] as a base Object, which has no {@code value} field
 * and whose {@code @class_identifier} is not "Integer".
 *
 * @see CProverArrayLikeIterator
 */
package org.cprover;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CProverGenericArrayElement {
    /**
     * Simple name of the enclosing class's generic type
     * parameter whose specialization should be used as the
     * element type of the annotated array. For
     * {@code class HashMap<K, V> { @CProverGenericArrayElement("K")
     * private Object[] keys; }}, the value is {@code "K"}.
     */
    String value();
}
