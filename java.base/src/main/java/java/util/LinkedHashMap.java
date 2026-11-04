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

import org.cprover.CProver;

/**
 * Hash table and linked list implementation of the {@code Map} interface,
 * with predictable iteration order, modelled for JBMC.
 *
 * <p>The entries of the underlying {@link HashMap} model are already kept
 * in an array in insertion order, which is exactly the iteration order of
 * a {@code LinkedHashMap}; no separate doubly-linked list is needed.
 * Access order moves an entry to the end of the array when it is read or
 * overwritten.
 *
 * @cprover.limitedSupport {@code reversed()} and the reversed views of
 *   the sequenced collections are {@code notModelled}; serialization is
 *   {@code notModelled} (see {@link HashMap}).
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public class LinkedHashMap<K,V>
    extends HashMap<K,V>
    implements SequencedMap<K,V>
{
    private static final long serialVersionUID = 3801124242820219131L;

    /**
     * The iteration ordering method: {@code true} for access-order,
     * {@code false} for insertion-order.
     */
    final boolean accessOrder;

    public LinkedHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
        accessOrder = false;
    }

    public LinkedHashMap(int initialCapacity) {
        super(initialCapacity);
        accessOrder = false;
    }

    public LinkedHashMap() {
        super();
        accessOrder = false;
    }

    public LinkedHashMap(Map<? extends K, ? extends V> m) {
        super();
        accessOrder = false;
        for (Map.Entry<? extends K, ? extends V> e : m.entrySet())
            cproverPut(e.getKey(), e.getValue(), false);
    }

    public LinkedHashMap(int initialCapacity,
                         float loadFactor,
                         boolean accessOrder) {
        super(initialCapacity, loadFactor);
        this.accessOrder = accessOrder;
    }

    /**
     * Creates a new, empty LinkedHashMap suitable for the expected number
     * of mappings.
     *
     * @param numMappings the expected number of mappings
     * @throws IllegalArgumentException if numMappings is negative
     */
    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(int numMappings) {
        if (numMappings < 0) {
            throw new IllegalArgumentException("Negative number of mappings: " + numMappings);
        }
        return new LinkedHashMap<>(HashMap.calculateHashMapCapacity(numMappings));
    }

    /** Moves the entry at {@code index} to the end of the order. */
    private void cproverMoveToEnd(int index) {
        Node<K,V> e = table[index];
        for (int i = index; i < size - 1; i++)
            table[i] = table[i + 1];
        table[size - 1] = e;
        modCount++;
    }

    /** Puts a mapping, optionally evicting the eldest entry afterwards. */
    private V cproverPut(K key, V value, boolean evict) {
        int index = cproverIndexOfKey(key);
        if (index >= 0) {
            V oldValue = table[index].value;
            table[index].value = value;
            if (accessOrder)
                cproverMoveToEnd(index);
            return oldValue;
        }
        cproverAppend(key, value);
        if (evict && removeEldestEntry(table[0]))
            cproverRemoveIndex(0);
        return null;
    }

    @Override
    public V put(K key, V value) {
        return cproverPut(key, value, true);
    }

    public V get(Object key) {
        int index = cproverIndexOfKey(key);
        if (index < 0)
            return null;
        V v = table[index].value;
        if (accessOrder)
            cproverMoveToEnd(index);
        return v;
    }

    public V getOrDefault(Object key, V defaultValue) {
        int index = cproverIndexOfKey(key);
        if (index < 0)
            return defaultValue;
        V v = table[index].value;
        if (accessOrder)
            cproverMoveToEnd(index);
        return v;
    }

    /**
     * Returns {@code true} if this map should remove its eldest entry.
     * Invoked by {@code put} after inserting a new entry.
     *
     * @param    eldest The least recently inserted entry in the map.
     * @return   {@code true} if the eldest entry should be removed
     */
    protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
        return false;
    }

    @Override
    public Object clone() {
        LinkedHashMap<K,V> result =
            new LinkedHashMap<K,V>(capacity(), loadFactor(), accessOrder);
        for (int i = 0; i < size; i++)
            result.cproverPut(table[i].key, table[i].value, false);
        return result;
    }

    // ---------------------------------------------------------------
    // SequencedMap

    public V putFirst(K k, V v) {
        V old = null;
        int index = cproverIndexOfKey(k);
        if (index >= 0) {
            old = table[index].value;
            cproverRemoveIndex(index);
        }
        cproverAppend(k, v);
        Node<K,V> e = table[size - 1];
        for (int i = size - 1; i > 0; i--)
            table[i] = table[i - 1];
        table[0] = e;
        return old;
    }

    public V putLast(K k, V v) {
        V old = null;
        int index = cproverIndexOfKey(k);
        if (index >= 0) {
            old = table[index].value;
            cproverRemoveIndex(index);
        }
        cproverAppend(k, v);
        return old;
    }

    public SequencedMap<K, V> reversed() {
        CProver.notModelled();
        return CProver.nondetWithoutNullForNotModelled();
    }

    public SequencedSet<K> sequencedKeySet() {
        return new SequencedKeys();
    }

    public SequencedCollection<V> sequencedValues() {
        return new SequencedVals();
    }

    public SequencedSet<Map.Entry<K, V>> sequencedEntrySet() {
        return new SequencedEntries();
    }

    final class SequencedKeys extends AbstractSet<K> implements SequencedSet<K> {
        public int size()                 { return size; }
        public void clear()               { LinkedHashMap.this.clear(); }
        public Iterator<K> iterator()     { return new KeyIterator(); }
        public boolean contains(Object o) { return containsKey(o); }
        public boolean remove(Object key) {
            int index = cproverIndexOfKey(key);
            if (index < 0)
                return false;
            cproverRemoveIndex(index);
            return true;
        }
        public SequencedSet<K> reversed() {
            CProver.notModelled();
            return CProver.nondetWithoutNullForNotModelled();
        }
    }

    final class SequencedVals extends AbstractCollection<V>
            implements SequencedCollection<V> {
        public int size()                 { return size; }
        public void clear()               { LinkedHashMap.this.clear(); }
        public Iterator<V> iterator()     { return new ValueIterator(); }
        public boolean contains(Object o) { return containsValue(o); }
        public SequencedCollection<V> reversed() {
            CProver.notModelled();
            return CProver.nondetWithoutNullForNotModelled();
        }
    }

    final class SequencedEntries extends AbstractSet<Map.Entry<K,V>>
            implements SequencedSet<Map.Entry<K,V>> {
        public int size()                 { return size; }
        public void clear()               { LinkedHashMap.this.clear(); }
        public Iterator<Map.Entry<K,V>> iterator() { return new EntryIterator(); }
        public boolean contains(Object o) { return entrySet().contains(o); }
        public boolean remove(Object o)   { return entrySet().remove(o); }
        public SequencedSet<Map.Entry<K,V>> reversed() {
            CProver.notModelled();
            return CProver.nondetWithoutNullForNotModelled();
        }
    }
}
