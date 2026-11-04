/*
 * Copyright (c) 1994, 2023, Oracle and/or its affiliates. All rights reserved.
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
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import org.cprover.CProver;

/**
 * This class implements a hash table, which maps keys to values, modelled
 * for JBMC.
 *
 * <p>Like the {@link HashMap} model, the entries are kept in an array that
 * is searched linearly with {@code equals}; there are no hash buckets, no
 * rehashing and no per-method locking (the methods remain
 * {@code synchronized}, which is the only part of the locking protocol that
 * is observable). Iteration order is unspecified for {@code Hashtable},
 * so insertion order is one of the permitted behaviours.
 *
 * @cprover.limitedSupport Iteration order (insertion order) and
 *   key lookup by {@code equals} only differ from the JDK where
 *   {@code equals} is inconsistent with {@code hashCode}. The views
 *   returned by {@code keySet()}, {@code entrySet()} and {@code values()}
 *   are not wrapped in synchronized collections. Serialization is
 *   {@code notModelled}. {@code clone()} of a subclass returns a plain
 *   {@code Hashtable} unless the subclass overrides it.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public class Hashtable<K,V>
    extends Dictionary<K,V>
    implements Map<K,V>, Cloneable, java.io.Serializable {

    private static final long serialVersionUID = 1421746759512286392L;

    /** One key-value pair. */
    private static class Entry<K,V> implements Map.Entry<K,V> {
        final K key;
        V value;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey()   { return key; }
        public V getValue() { return value; }

        public V setValue(V value) {
            if (value == null)
                throw new NullPointerException();
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        public boolean equals(Object o) {
            return o instanceof Map.Entry<?, ?> e
                    && (key == null ? e.getKey() == null : key.equals(e.getKey()))
                    && (value == null ? e.getValue() == null : value.equals(e.getValue()));
        }

        public int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        public String toString() {
            return key.toString() + "=" + value.toString();
        }
    }

    /** The entries, in insertion order, in {@code table[0..count)}. */
    private transient Entry<K,V>[] table;

    /** The total number of entries in the hash table. */
    private transient int count;

    /** The number of times this Hashtable has been structurally modified. */
    private transient int modCount;

    /**
     * Constructs a new, empty hashtable with the specified initial
     * capacity and the specified load factor.
     *
     * @param      initialCapacity   the initial capacity of the hashtable.
     * @param      loadFactor        the load factor of the hashtable.
     * @throws     IllegalArgumentException  if the initial capacity is less
     *             than zero, or if the load factor is nonpositive.
     */
    @SuppressWarnings("unchecked")
    public Hashtable(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal Capacity: "+
                                               initialCapacity);
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal Load: "+loadFactor);
        table = (Entry<K,V>[]) new Entry<?,?>[4];
    }

    /**
     * Constructs a new, empty hashtable with the specified initial capacity
     * and default load factor (0.75).
     *
     * @param     initialCapacity   the initial capacity of the hashtable.
     * @throws    IllegalArgumentException if the initial capacity is less
     *              than zero.
     */
    public Hashtable(int initialCapacity) {
        this(initialCapacity, 0.75f);
    }

    /**
     * Constructs a new, empty hashtable with a default initial capacity (11)
     * and load factor (0.75).
     */
    public Hashtable() {
        this(11, 0.75f);
    }

    /**
     * Constructs a new hashtable with the same mappings as the given Map.
     *
     * @param t the map whose mappings are to be placed in this map.
     * @throws NullPointerException if the specified map is null.
     */
    public Hashtable(Map<? extends K, ? extends V> t) {
        this(Math.max(2*t.size(), 11), 0.75f);
        putAll(t);
    }

    /**
     * A constructor chained from Properties; entries are held in the
     * ordinary table of this model.
     */
    Hashtable(Void dummy) {
        this(11, 0.75f);
    }

    private int indexOfKey(Object key) {
        if (key == null)
            throw new NullPointerException();
        for (int i = 0; i < count; i++) {
            K k = table[i].key;
            if (k == key || key.equals(k))
                return i;
        }
        return -1;
    }

    private int indexOfValue(Object value) {
        if (value == null)
            throw new NullPointerException();
        for (int i = 0; i < count; i++) {
            V v = table[i].value;
            if (v == value || value.equals(v))
                return i;
        }
        return -1;
    }

    private void removeIndex(int index) {
        for (int i = index; i < count - 1; i++)
            table[i] = table[i + 1];
        table[count - 1] = null;
        count--;
        modCount++;
    }

    @SuppressWarnings("unchecked")
    private void append(K key, V value) {
        if (count == table.length) {
            Entry<K,V>[] bigger = (Entry<K,V>[]) new Entry<?,?>[table.length * 2];
            for (int i = 0; i < count; i++)
                bigger[i] = table[i];
            table = bigger;
        }
        table[count++] = new Entry<>(key, value);
        modCount++;
    }

    public synchronized int size() {
        return count;
    }

    public synchronized boolean isEmpty() {
        return count == 0;
    }

    public synchronized Enumeration<K> keys() {
        return new KeyIter();
    }

    public synchronized Enumeration<V> elements() {
        return new ValueIter();
    }

    public synchronized boolean contains(Object value) {
        return indexOfValue(value) >= 0;
    }

    public boolean containsValue(Object value) {
        return contains(value);
    }

    public synchronized boolean containsKey(Object key) {
        return indexOfKey(key) >= 0;
    }

    @SuppressWarnings("unchecked")
    public synchronized V get(Object key) {
        int index = indexOfKey(key);
        return index < 0 ? null : table[index].value;
    }

    /**
     * Increases the capacity of and internally reorganizes this hashtable.
     * A no-op in this model.
     */
    protected void rehash() {
    }

    public synchronized V put(K key, V value) {
        // Make sure the value is not null
        if (value == null) {
            throw new NullPointerException();
        }
        int index = indexOfKey(key);
        if (index >= 0) {
            V old = table[index].value;
            table[index].value = value;
            return old;
        }
        append(key, value);
        return null;
    }

    public synchronized V remove(Object key) {
        int index = indexOfKey(key);
        if (index < 0)
            return null;
        V old = table[index].value;
        removeIndex(index);
        return old;
    }

    public synchronized void putAll(Map<? extends K, ? extends V> t) {
        for (Map.Entry<? extends K, ? extends V> e : t.entrySet())
            put(e.getKey(), e.getValue());
    }

    public synchronized void clear() {
        for (int i = 0; i < count; i++)
            table[i] = null;
        count = 0;
        modCount++;
    }

    public synchronized Object clone() {
        Hashtable<K,V> t = new Hashtable<>(this);
        return t;
    }

    public synchronized String toString() {
        int max = size() - 1;
        if (max == -1)
            return "{}";

        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (int i = 0; ; i++) {
            K key = table[i].key;
            V value = table[i].value;
            sb.append(key   == this ? "(this Map)" : key.toString());
            sb.append('=');
            sb.append(value == this ? "(this Map)" : value.toString());

            if (i == max)
                return sb.append('}').toString();
            sb.append(", ");
        }
    }

    // ---------------------------------------------------------------
    // Views

    public Set<K> keySet() {
        return new KeySetView();
    }

    public Set<Map.Entry<K,V>> entrySet() {
        return new EntrySetView();
    }

    public Collection<V> values() {
        return new ValuesView();
    }

    private final class KeySetView extends AbstractSet<K> {
        public Iterator<K> iterator()     { return new KeyIter(); }
        public int size()                 { return count; }
        public boolean contains(Object o) { return containsKey(o); }
        public boolean remove(Object o)   { return Hashtable.this.remove(o) != null; }
        public void clear()               { Hashtable.this.clear(); }
    }

    private final class EntrySetView extends AbstractSet<Map.Entry<K,V>> {
        public Iterator<Map.Entry<K,V>> iterator() { return new EntryIter(); }
        public boolean add(Map.Entry<K,V> o) { return super.add(o); }
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry<?, ?> entry))
                return false;
            int index = indexOfKey(entry.getKey());
            return index >= 0 && table[index].equals(entry);
        }
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry<?, ?> entry))
                return false;
            int index = indexOfKey(entry.getKey());
            if (index >= 0 && table[index].equals(entry)) {
                removeIndex(index);
                return true;
            }
            return false;
        }
        public int size()   { return count; }
        public void clear() { Hashtable.this.clear(); }
    }

    private final class ValuesView extends AbstractCollection<V> {
        public Iterator<V> iterator()     { return new ValueIter(); }
        public int size()                 { return count; }
        public boolean contains(Object o) { return containsValue(o); }
        public void clear()               { Hashtable.this.clear(); }
    }

    // ---------------------------------------------------------------
    // Iterators

    private abstract class Iter<T> implements Enumeration<T>, Iterator<T> {
        int cursor;
        int lastRet = -1;
        int expectedModCount = modCount;

        abstract T extract(Entry<K,V> e);

        public boolean hasMoreElements() { return cursor < count; }
        public boolean hasNext()         { return hasMoreElements(); }

        public T nextElement() {
            synchronized (Hashtable.this) {
                if (cursor >= count)
                    throw new NoSuchElementException("Hashtable Enumerator");
                lastRet = cursor++;
                return extract(table[lastRet]);
            }
        }

        public T next() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            return nextElement();
        }

        public void remove() {
            synchronized (Hashtable.this) {
                if (lastRet < 0)
                    throw new IllegalStateException("Hashtable Enumerator");
                if (modCount != expectedModCount)
                    throw new ConcurrentModificationException();
                removeIndex(lastRet);
                cursor = lastRet;
                lastRet = -1;
                expectedModCount = modCount;
            }
        }
    }

    private final class KeyIter extends Iter<K> {
        K extract(Entry<K,V> e) { return e.key; }
    }

    private final class ValueIter extends Iter<V> {
        V extract(Entry<K,V> e) { return e.value; }
    }

    private final class EntryIter extends Iter<Map.Entry<K,V>> {
        Map.Entry<K,V> extract(Entry<K,V> e) { return e; }
    }

    // ---------------------------------------------------------------
    // Comparison and hashing (as specified by Map)

    public synchronized boolean equals(Object o) {
        if (o == this)
            return true;

        if (!(o instanceof Map<?, ?> t))
            return false;
        if (t.size() != size())
            return false;

        try {
            for (int i = 0; i < count; i++) {
                K key = table[i].key;
                V value = table[i].value;
                if (value == null) {
                    if (!(t.get(key) == null && t.containsKey(key)))
                        return false;
                } else {
                    if (!value.equals(t.get(key)))
                        return false;
                }
            }
        } catch (ClassCastException | NullPointerException unused) {
            return false;
        }

        return true;
    }

    public synchronized int hashCode() {
        int h = 0;
        for (int i = 0; i < count; i++)
            h += table[i].hashCode();
        return h;
    }

    // ---------------------------------------------------------------
    // Map defaults, with the Hashtable null checks (put rejects null)

    @Override
    public synchronized V getOrDefault(Object key, V defaultValue) {
        V result = get(key);
        return (null == result) ? defaultValue : result;
    }

    @Override
    public synchronized void forEach(BiConsumer<? super K, ? super V> action) {
        Objects.requireNonNull(action);
        int expected = modCount;
        for (int i = 0; i < count; i++) {
            action.accept(table[i].key, table[i].value);
            if (expected != modCount)
                throw new ConcurrentModificationException();
        }
    }

    @Override
    public synchronized void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        Objects.requireNonNull(function);
        int expected = modCount;
        for (int i = 0; i < count; i++) {
            V v = Objects.requireNonNull(function.apply(table[i].key, table[i].value));
            table[i].value = v;
            if (expected != modCount)
                throw new ConcurrentModificationException();
        }
    }

    // putIfAbsent, remove(k,v), replace, computeIfAbsent, computeIfPresent,
    // compute and merge use the Map defaults, which are specified in terms
    // of get/put/remove.

    private void writeObject(ObjectOutputStream s) throws IOException {
        CProver.notModelled();
    }

    private void readObject(ObjectInputStream s)
            throws IOException, ClassNotFoundException {
        CProver.notModelled();
    }
}
