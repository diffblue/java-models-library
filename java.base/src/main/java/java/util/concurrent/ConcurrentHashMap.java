/*
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

/*
 * This file is available under and governed by the GNU General Public
 * License version 2 only, as published by the Free Software Foundation.
 * However, the following notice accompanied the original version of this
 * file:
 *
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */

package java.util.concurrent;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * DIFFBLUE MODEL LIBRARY: a hash table that delegates to a {@link HashMap}
 * (the JDK implementation uses Unsafe and VarHandle for its compare-and-set
 * operations). Null keys and values are rejected as in the JDK. The
 * operations are not atomic and the iterators are not weakly consistent: the
 * model is meant for the sequential use of the class.
 *
 * @cprover.limitedSupport Only the basic map operations are provided; the
 *   bulk operations (search, reduce, forEach with a parallelism threshold),
 *   the key set view with a default value and the field-level API of the JDK
 *   are not.
 */
public class ConcurrentHashMap<K,V> extends AbstractMap<K,V>
    implements ConcurrentMap<K,V>, Serializable {
    private static final long serialVersionUID = 7249069246763182397L;

    private final HashMap<K,V> map;

    public ConcurrentHashMap() {
        map = new HashMap<K,V>();
    }

    public ConcurrentHashMap(int initialCapacity) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException();
        map = new HashMap<K,V>();
    }

    public ConcurrentHashMap(Map<? extends K, ? extends V> m) {
        map = new HashMap<K,V>();
        putAll(m);
    }

    public ConcurrentHashMap(int initialCapacity, float loadFactor) {
        this(initialCapacity, loadFactor, 1);
    }

    public ConcurrentHashMap(int initialCapacity,
                             float loadFactor, int concurrencyLevel) {
        if (!(loadFactor > 0.0f) || initialCapacity < 0 || concurrencyLevel <= 0)
            throw new IllegalArgumentException();
        map = new HashMap<K,V>();
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public V get(Object key) {
        if (key == null)
            throw new NullPointerException();
        return map.get(key);
    }

    public boolean containsKey(Object key) {
        if (key == null)
            throw new NullPointerException();
        return map.containsKey(key);
    }

    public boolean containsValue(Object value) {
        if (value == null)
            throw new NullPointerException();
        return map.containsValue(value);
    }

    public V put(K key, V value) {
        if (key == null || value == null)
            throw new NullPointerException();
        return map.put(key, value);
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> e : m.entrySet())
            put(e.getKey(), e.getValue());
    }

    public V remove(Object key) {
        if (key == null)
            throw new NullPointerException();
        return map.remove(key);
    }

    public void clear() {
        map.clear();
    }

    public Set<K> keySet() {
        return map.keySet();
    }

    public Collection<V> values() {
        return map.values();
    }

    public Set<Map.Entry<K,V>> entrySet() {
        return map.entrySet();
    }

    public V putIfAbsent(K key, V value) {
        if (key == null || value == null)
            throw new NullPointerException();
        V old = map.get(key);
        if (old == null)
            map.put(key, value);
        return old;
    }

    public boolean remove(Object key, Object value) {
        if (key == null)
            throw new NullPointerException();
        if (value != null && value.equals(map.get(key))) {
            map.remove(key);
            return true;
        }
        return false;
    }

    public boolean replace(K key, V oldValue, V newValue) {
        if (key == null || oldValue == null || newValue == null)
            throw new NullPointerException();
        if (oldValue.equals(map.get(key))) {
            map.put(key, newValue);
            return true;
        }
        return false;
    }

    public V replace(K key, V value) {
        if (key == null || value == null)
            throw new NullPointerException();
        if (map.get(key) != null)
            return map.put(key, value);
        return null;
    }

    public V getOrDefault(Object key, V defaultValue) {
        V v = get(key);
        return v == null ? defaultValue : v;
    }

    public void forEach(BiConsumer<? super K, ? super V> action) {
        if (action == null)
            throw new NullPointerException();
        for (Map.Entry<K,V> e : map.entrySet())
            action.accept(e.getKey(), e.getValue());
    }

    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        if (function == null)
            throw new NullPointerException();
        for (Map.Entry<K,V> e : map.entrySet()) {
            V v = function.apply(e.getKey(), e.getValue());
            if (v == null)
                throw new NullPointerException();
            e.setValue(v);
        }
    }

    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        if (key == null || mappingFunction == null)
            throw new NullPointerException();
        V v = map.get(key);
        if (v == null) {
            v = mappingFunction.apply(key);
            if (v != null)
                map.put(key, v);
        }
        return v;
    }

    public V computeIfPresent(K key,
                              BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        if (key == null || remappingFunction == null)
            throw new NullPointerException();
        V old = map.get(key);
        if (old == null)
            return null;
        V v = remappingFunction.apply(key, old);
        if (v != null)
            map.put(key, v);
        else
            map.remove(key);
        return v;
    }

    public V compute(K key,
                     BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        if (key == null || remappingFunction == null)
            throw new NullPointerException();
        V old = map.get(key);
        V v = remappingFunction.apply(key, old);
        if (v != null)
            map.put(key, v);
        else if (old != null)
            map.remove(key);
        return v;
    }

    public V merge(K key, V value,
                   BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        if (key == null || value == null || remappingFunction == null)
            throw new NullPointerException();
        V old = map.get(key);
        V v = old == null ? value : remappingFunction.apply(old, value);
        if (v != null)
            map.put(key, v);
        else
            map.remove(key);
        return v;
    }

    public boolean contains(Object value) {
        return containsValue(value);
    }

    public Enumeration<K> keys() {
        return Collections.enumeration(map.keySet());
    }

    public Enumeration<V> elements() {
        return Collections.enumeration(map.values());
    }

    public long mappingCount() {
        return map.size();
    }

    public static <T> Set<T> newKeySet() {
        return Collections.newSetFromMap(new ConcurrentHashMap<T, Boolean>());
    }

    public static <T> Set<T> newKeySet(int initialCapacity) {
        return Collections.newSetFromMap(new ConcurrentHashMap<T, Boolean>(initialCapacity));
    }
}
