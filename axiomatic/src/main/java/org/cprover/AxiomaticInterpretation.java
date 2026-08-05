/*
 * Marker annotation for methods whose semantics are encoded
 * directly into CBMC GOTO IR rather than executed from
 * bytecode. JBMC's java_bytecode_axiomatic pass walks
 * function bodies and rewrites CALLs to annotated methods
 * into the SMT-relevant operation named by `op`.
 *
 * Currently a no-op when --axiomatic-collections is not set.
 *
 * The Java method body provided in the model jar is a
 * concrete fallback — JBMC ignores it when the lowering pass
 * runs, but javac needs it to type-check and other JVM tools
 * (test harnesses, IDEs) need it to load classes.
 *
 * Recognised values for `op`:
 *
 *   "select"        — receiver.kv[key] (returns the stored value)
 *   "store"         — receiver.kv := store(receiver.kv, key, value)
 *   "contains_key"  — receiver.kv[key] != UNSET sentinel
 *   "size"          — receiver.size (ghost int)
 *   "is_empty"      — receiver.size == 0
 *   "remove"        — receiver.kv[key] := UNSET; receiver.size--
 *   "clear"         — receiver.kv := empty array; receiver.size := 0
 *
 * `array` and `sizeVar` parameters name the ghost members
 * of the receiver. Default values match the conventions used
 * by the axiomatic-models jar.
 */
package org.cprover;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AxiomaticInterpretation {
    /** SMT-relevant operation name (see class javadoc). */
    String op();

    /** Ghost-array field name on the receiver. */
    String array() default "kv";

    /** Ghost-int field name on the receiver. */
    String sizeVar() default "size";
}
