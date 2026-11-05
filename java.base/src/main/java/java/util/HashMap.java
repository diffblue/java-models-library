/*
 * Copyright (c) 1997, 2023, Oracle and/or its affiliates. All rights reserved.
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

package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.cprover.CProver;

/**
 * Hash table based implementation of the {@code Map} interface, modelled
 * for JBMC.
 *
 * <p>The entries are kept in an insertion-ordered array that is searched
 * linearly with {@code equals}, instead of in a table of hash buckets
 * (with resizing, treeification and hash spreading). Hashing is a pure
 * performance device: the {@code Map} contract does not depend on it, and
 * symbolic execution of the bucket code (with nondeterministic keys whose
 * {@code hashCode} dispatches to every loaded class) is very expensive.
 * Iteration order is unspecified for {@code HashMap}, so insertion order
 * is one of the permitted behaviours.
 *
 * @cprover.limitedSupport Not faithful to the JDK in: at most 128 mappings
 *   are supported (paths with more are pruned by an assumption), the iteration order
 *   (always insertion order), {@code hashCode} not being consulted for
 *   key lookup (a key whose {@code equals} is inconsistent with
 *   {@code hashCode} is found here, but not by the JDK), and
 *   serialization ({@code notModelled}). Spliterators are the generic
 *   {@link Spliterators} ones.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public class HashMap<K,V> extends AbstractMap<K,V>
    implements Map<K,V>, Cloneable, Serializable {

    private static final long serialVersionUID = 362498820763181265L;

    /** The default initial capacity. */
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;

    /** The maximum capacity. */
    static final int MAXIMUM_CAPACITY = 1 << 30;

    /** The default load factor. */
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /** Backing array size at construction. */
    private static final int CPROVER_MIN_TABLE = 8;

    /**
     * Backing array size after the first growth, and the maximum number of
     * mappings: arrays are only ever allocated with constant sizes, since
     * an allocation whose size depends on a nondeterministic array length
     * yields an array of symbolic size that blows up the SAT encoding.
     */
    private static final int CPROVER_MAX_TABLE = 128;

    /**
     * Basic entry.
     */
    static class Node<K,V> implements Map.Entry<K,V> {
        final K key;
        V value;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public final K getKey()        { return key; }
        public final V getValue()      { return value; }
        public final String toString() { return key + "=" + value; }

        public final int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        public final V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        public final boolean equals(Object o) {
            if (o == this)
                return true;

            return o instanceof Map.Entry<?, ?> e
                    && Objects.equals(key, e.getKey())
                    && Objects.equals(value, e.getValue());
        }
    }

    /**
     * The entries, in insertion order, in {@code table[0..size)}; the
     * remaining slots are unused.
     */
    transient Node<K,V>[] table;

    /** Cached entrySet view. */
    transient Set<Map.Entry<K,V>> entrySet;

    /** The number of key-value mappings. */
    transient int size;

    /** The number of structural modifications (for fail-fast iterators). */
    transient int modCount;

    /** The capacity requested at construction, reported by capacity(). */
    private transient int cproverRequestedCapacity;

    /** The load factor requested at construction. */
    private final float loadFactor;

    // ---------------------------------------------------------------
    // Construction

    /**
     * Returns the table size needed for the given number of mappings.
     */
    static final int tableSizeFor(int cap) {
        int n = -1 >>> Integer.numberOfLeadingZeros(cap - 1);
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

    /**
     * Calculates the initial capacity needed to hold the given number of
     * mappings without resizing.
     */
    static int calculateHashMapCapacity(int numMappings) {
        return (int) Math.ceil(numMappings / (double) DEFAULT_LOAD_FACTOR);
    }

    /**
     * Creates a new, empty HashMap suitable for the expected number of
     * mappings.
     *
     * @param numMappings the expected number of mappings
     * @throws IllegalArgumentException if numMappings is negative
     */
    public static <K, V> HashMap<K, V> newHashMap(int numMappings) {
        if (numMappings < 0) {
            throw new IllegalArgumentException("Negative number of mappings: " + numMappings);
        }
        return new HashMap<>(calculateHashMapCapacity(numMappings));
    }

    @SuppressWarnings("unchecked")
    private void cproverInit(int initialCapacity) {
        this.cproverRequestedCapacity = initialCapacity;
        this.table = (Node<K,V>[]) new Node[CPROVER_MIN_TABLE];
        this.size = 0;
        this.modCount = 0;
    }

    /**
     * Constructs an empty {@code HashMap} with the specified initial
     * capacity and load factor.
     *
     * @param  initialCapacity the initial capacity
     * @param  loadFactor      the load factor
     * @throws IllegalArgumentException if the initial capacity is negative
     *         or the load factor is nonpositive
     */
    public HashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                                               initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " +
                                               loadFactor);
        this.loadFactor = loadFactor;
        cproverInit(initialCapacity);
    }

    /**
     * Constructs an empty {@code HashMap} with the specified initial
     * capacity and the default load factor (0.75).
     *
     * @param  initialCapacity the initial capacity.
     * @throws IllegalArgumentException if the initial capacity is negative.
     */
    public HashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Constructs an empty {@code HashMap} with the default initial
     * capacity (16) and the default load factor (0.75).
     */
    public HashMap() {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        cproverInit(DEFAULT_INITIAL_CAPACITY);
    }

    /**
     * Constructs a new {@code HashMap} with the same mappings as the
     * specified {@code Map}.
     *
     * @param   m the map whose mappings are to be placed in this map
     * @throws  NullPointerException if the specified map is null
     */
    public HashMap(Map<? extends K, ? extends V> m) {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        cproverInit(Math.max(DEFAULT_INITIAL_CAPACITY,
                             calculateHashMapCapacity(m.size())));
        putAll(m);
    }

    final int capacity() {
        return Math.max(cproverRequestedCapacity, tableSizeFor(size));
    }

    final float loadFactor() {
        return loadFactor;
    }

    // ---------------------------------------------------------------
    // Lookup helpers

    /**
     * Returns the index of the entry with the given key, or -1.
     */
    final int cproverIndexOfKey(Object key) {
        if (key == null) {
            for (int i = 0; i < size; i++)
                if (table[i].key == null)
                    return i;
        } else {
            for (int i = 0; i < size; i++) {
                K k = table[i].key;
                if (k == key || key.equals(k))
                    return i;
            }
        }
        return -1;
    }

    /**
     * Returns the index of the first entry with the given value, or -1.
     */
    final int cproverIndexOfValue(Object value) {
        if (value == null) {
            for (int i = 0; i < size; i++)
                if (table[i].value == null)
                    return i;
        } else {
            for (int i = 0; i < size; i++) {
                V v = table[i].value;
                if (v == value || value.equals(v))
                    return i;
            }
        }
        return -1;
    }

    /**
     * Removes the entry at the given index, keeping the order of the rest.
     */
    final void cproverRemoveIndex(int index) {
        for (int i = index; i < size - 1; i++)
            table[i] = table[i + 1];
        table[size - 1] = null;
        size--;
        modCount++;
    }

    /**
     * Appends a new entry, growing the backing array when full.
     */
    @SuppressWarnings("unchecked")
    final void cproverAppend(K key, V value) {
        if (size == table.length) {
            // Only constant-size allocations (see CPROVER_MAX_TABLE).
            CProver.assume(table.length < CPROVER_MAX_TABLE);
            Node<K,V>[] bigger = (Node<K,V>[]) new Node[CPROVER_MAX_TABLE];
            for (int i = 0; i < size; i++)
                bigger[i] = table[i];
            table = bigger;
        }
        table[size++] = new Node<K,V>(key, value);
        modCount++;
    }

    // ---------------------------------------------------------------
    // Map operations

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public V get(Object key) {
        int index = cproverIndexOfKey(key);
        return index < 0 ? null : table[index].value;
    }

    public boolean containsKey(Object key) {
        return cproverIndexOfKey(key) >= 0;
    }

    public V put(K key, V value) {
        int index = cproverIndexOfKey(key);
        if (index >= 0) {
            V oldValue = table[index].value;
            table[index].value = value;
            return oldValue;
        }
        cproverAppend(key, value);
        return null;
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> e : m.entrySet())
            put(e.getKey(), e.getValue());
    }

    public V remove(Object key) {
        int index = cproverIndexOfKey(key);
        if (index < 0)
            return null;
        V v = table[index].value;
        cproverRemoveIndex(index);
        return v;
    }

    public void clear() {
        for (int i = 0; i < size; i++)
            table[i] = null;
        size = 0;
        modCount++;
    }

    public boolean containsValue(Object value) {
        return cproverIndexOfValue(value) >= 0;
    }

    // The remaining Map methods (getOrDefault, putIfAbsent, remove(k,v),
    // replace, compute*, merge, forEach, replaceAll) use the default
    // implementations of Map, which are specified in terms of the methods
    // above.

    /**
     * Returns a shallow copy of this {@code HashMap} instance.
     *
     * @cprover.limitedSupport Subclasses must override clone(); Object.clone
     *   is not supported.
     */
    @Override
    public Object clone() {
        return new HashMap<K,V>(this);
    }

    // ---------------------------------------------------------------
    // Views

    public Set<K> keySet() {
        Set<K> ks = keySet;
        if (ks == null) {
            ks = new KeySet();
            keySet = ks;
        }
        return ks;
    }

    final class KeySet extends AbstractSet<K> {
        public final int size()                 { return size; }
        public final void clear()               { HashMap.this.clear(); }
        public final Iterator<K> iterator()     { return new KeyIterator(); }
        public final boolean contains(Object o) { return containsKey(o); }
        public final boolean remove(Object key) {
            int index = cproverIndexOfKey(key);
            if (index < 0)
                return false;
            cproverRemoveIndex(index);
            return true;
        }
        public final Spliterator<K> spliterator() {
            return Spliterators.spliterator(this, Spliterator.DISTINCT);
        }
    }

    public Collection<V> values() {
        Collection<V> vs = values;
        if (vs == null) {
            vs = new Values();
            values = vs;
        }
        return vs;
    }

    final class Values extends AbstractCollection<V> {
        public final int size()                 { return size; }
        public final void clear()               { HashMap.this.clear(); }
        public final Iterator<V> iterator()     { return new ValueIterator(); }
        public final boolean contains(Object o) { return containsValue(o); }
        public final Spliterator<V> spliterator() {
            return Spliterators.spliterator(this, 0);
        }
    }

    public Set<Map.Entry<K,V>> entrySet() {
        Set<Map.Entry<K,V>> es = entrySet;
        if (es == null) {
            es = new EntrySet();
            entrySet = es;
        }
        return es;
    }

    final class EntrySet extends AbstractSet<Map.Entry<K,V>> {
        public final int size()                 { return size; }
        public final void clear()               { HashMap.this.clear(); }
        public final Iterator<Map.Entry<K,V>> iterator() {
            return new EntryIterator();
        }
        public final boolean contains(Object o) {
            if (!(o instanceof Map.Entry<?, ?> e))
                return false;
            int index = cproverIndexOfKey(e.getKey());
            return index >= 0 && table[index].equals(e);
        }
        public final boolean remove(Object o) {
            if (o instanceof Map.Entry<?, ?> e) {
                int index = cproverIndexOfKey(e.getKey());
                if (index >= 0 && table[index].equals(e)) {
                    cproverRemoveIndex(index);
                    return true;
                }
            }
            return false;
        }
        public final Spliterator<Map.Entry<K,V>> spliterator() {
            return Spliterators.spliterator(this, Spliterator.DISTINCT);
        }
    }

    // ---------------------------------------------------------------
    // Iterators

    // HashIterator itself implements Iterator: JBMC's virtual-function
    // resolution does not find hasNext/remove when they are inherited from a
    // superclass that does not implement the interface.
    abstract class HashIterator<T> implements Iterator<T> {
        int cursor;           // index of the next entry to return
        int lastRet = -1;     // index of the last entry returned, or -1
        int expectedModCount; // for fail-fast

        HashIterator() {
            expectedModCount = modCount;
        }

        public final boolean hasNext() {
            return cursor < size;
        }

        final Node<K,V> nextNode() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            if (cursor >= size)
                throw new NoSuchElementException();
            lastRet = cursor++;
            return table[lastRet];
        }

        public final void remove() {
            if (lastRet < 0)
                throw new IllegalStateException();
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            cproverRemoveIndex(lastRet);
            cursor = lastRet;
            lastRet = -1;
            expectedModCount = modCount;
        }
    }

    final class KeyIterator extends HashIterator<K> {
        public final K next() { return nextNode().key; }
    }

    final class ValueIterator extends HashIterator<V> {
        public final V next() { return nextNode().value; }
    }

    final class EntryIterator extends HashIterator<Map.Entry<K,V>> {
        public final Map.Entry<K,V> next() { return nextNode(); }
    }

    // ---------------------------------------------------------------
    // Array helpers (used by HashSet and LinkedHashSet)

    /**
     * Returns an array of the right runtime type and size for the keys.
     */
    @SuppressWarnings("unchecked")
    final <T> T[] prepareArray(T[] a) {
        int size = this.size;
        if (a.length < size) {
            return (T[]) CProver.createArrayWithType(size, a);
        }
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }

    /** Fills the (big enough) array with the keys, in map order. */
    <T> T[] keysToArray(T[] a) {
        return keysToArray(a, false);
    }

    /** Fills the (big enough) array with the keys, optionally reversed. */
    final <T> T[] keysToArray(T[] a, boolean reversed) {
        Object[] r = a;
        for (int i = 0; i < size; i++)
            r[i] = table[reversed ? size - 1 - i : i].key;
        return a;
    }

    /** Fills the (big enough) array with the values, optionally reversed. */
    final <T> T[] valuesToArray(T[] a, boolean reversed) {
        Object[] r = a;
        for (int i = 0; i < size; i++)
            r[i] = table[reversed ? size - 1 - i : i].value;
        return a;
    }

    <T> T[] valuesToArray(T[] a) {
        return valuesToArray(a, false);
    }

    // ---------------------------------------------------------------
    // Serialization

    private void writeObject(ObjectOutputStream s) throws IOException {
        CProver.notModelled();
    }

    private void readObject(ObjectInputStream s)
            throws IOException, ClassNotFoundException {
        CProver.notModelled();
    }
}
