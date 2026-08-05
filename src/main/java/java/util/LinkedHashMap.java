/*
 * Copyright (c) 2000, 2013, Oracle and/or its affiliates. All rights reserved.
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
import org.cprover.CProverMapEntry;

/**
 * JBMC model of java.util.LinkedHashMap.
 *
 * <p>DIFFBLUE MODEL LIBRARY Design: an ORDER OVERLAY over the HashMap
 * model. The map contents (key-value association) live entirely in the
 * superclass; this class additionally maintains the iteration order that
 * LinkedHashMap specifies (insertion order, or access order when
 * constructed with {@code accessOrder = true}) as a parallel key list.
 * Every structural mutation goes through the overridden {@code put} /
 * {@code remove} / {@code clear} (note {@code putAll} delegates to
 * {@code put} in the HashMap model, so bulk insertion is ordered too),
 * and the view methods {@code keySet()} / {@code values()} /
 * {@code entrySet()} are rebuilt from the ordered key list.
 *
 * <p>The JDK maps that this model must stay conformant with SPECIFY the
 * iteration order -- that is LinkedHashMap's whole point -- so methods
 * that could mutate the map behind the overlay's back (compute,
 * computeIfAbsent, computeIfPresent, merge) are {@code notModelled}
 * rather than silently inherited with a stale order.
 *
 * <p>{@code removeEldestEntry} is invoked after each insertion, per the
 * JDK contract, so LRU-cache subclasses verify correctly.
 */
public class LinkedHashMap<K, V> extends HashMap<K, V> {

    private static final long serialVersionUID = 3801124242820219131L;

    private Object[] cproverOrderedKeys;
    private int cproverOrderedCount;
    private final boolean accessOrder;

    public LinkedHashMap() {
        this.cproverOrderedKeys = new Object[10];
        this.cproverOrderedCount = 0;
        this.accessOrder = false;
    }

    public LinkedHashMap(int initialCapacity) {
        this();
    }

    public LinkedHashMap(int initialCapacity, float loadFactor) {
        this();
    }

    public LinkedHashMap(int initialCapacity, float loadFactor,
                         boolean accessOrder) {
        this.cproverOrderedKeys = new Object[10];
        this.cproverOrderedCount = 0;
        this.accessOrder = accessOrder;
    }

    public LinkedHashMap(Map<? extends K, ? extends V> m) {
        this();
        putAll(m);
    }

    private static boolean cproverKeyEquals(Object a, Object b) {
        return a == null ? b == null : a.equals(b);
    }

    private int cproverOrderIndexOf(Object key) {
        for (int i = 0; i < cproverOrderedCount; i++) {
            if (cproverKeyEquals(cproverOrderedKeys[i], key)) {
                return i;
            }
        }
        return -1;
    }

    private void cproverRemoveKeyAt(int idx) {
        for (int i = idx; i < cproverOrderedCount - 1; i++) {
            cproverOrderedKeys[i] = cproverOrderedKeys[i + 1];
        }
        cproverOrderedKeys[cproverOrderedCount - 1] = null;
        cproverOrderedCount--;
    }

    private void cproverAppendKey(Object key) {
        if (cproverOrderedCount == cproverOrderedKeys.length) {
            Object[] grown = new Object[2 * cproverOrderedKeys.length + 1];
            for (int i = 0; i < cproverOrderedCount; i++) {
                grown[i] = cproverOrderedKeys[i];
            }
            cproverOrderedKeys = grown;
        }
        cproverOrderedKeys[cproverOrderedCount++] = key;
    }

    private void cproverMoveKeyToEnd(Object key) {
        int idx = cproverOrderIndexOf(key);
        if (idx >= 0 && idx != cproverOrderedCount - 1) {
            Object k = cproverOrderedKeys[idx];
            cproverRemoveKeyAt(idx);
            cproverAppendKey(k);
        }
    }

    @Override
    public V put(K key, V value) {
        boolean had = containsKey(key);
        V previous = super.put(key, value);
        if (!had) {
            cproverAppendKey(key);
            // JDK contract: removeEldestEntry is consulted after insertion,
            // enabling LRU-cache subclasses.
            if (cproverOrderedCount > 0) {
                K eldestKey = (K) cproverOrderedKeys[0];
                // super.get: this.get would count as an ACCESS and, on an
                // access-ordered map, move the eldest to the end.
                Map.Entry<K, V> eldest =
                    new CProverMapEntry<K, V>(eldestKey, super.get(eldestKey));
                if (removeEldestEntry(eldest)) {
                    remove(eldestKey);
                }
            }
        } else if (accessOrder) {
            // Access-ordered maps treat put of an existing key as an access.
            cproverMoveKeyToEnd(key);
        }
        return previous;
    }

    @Override
    public V get(Object key) {
        V v = super.get(key);
        if (accessOrder && containsKey(key)) {
            cproverMoveKeyToEnd(key);
        }
        return v;
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        // JDK: getOrDefault is an ACCESS for access-ordered maps.
        if (containsKey(key)) {
            return get(key);
        }
        return defaultValue;
    }

    @Override
    public V putIfAbsent(K key, V value) {
        // Route through put so the order overlay is maintained.
        if (containsKey(key)) {
            return get(key);
        }
        put(key, value);
        return null;
    }

    @Override
    public V remove(Object key) {
        int idx = cproverOrderIndexOf(key);
        if (idx >= 0) {
            cproverRemoveKeyAt(idx);
        }
        return super.remove(key);
    }

    @Override
    public void clear() {
        super.clear();
        for (int i = 0; i < cproverOrderedCount; i++) {
            cproverOrderedKeys[i] = null;
        }
        cproverOrderedCount = 0;
    }

    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return false;
    }

    @Override
    public Set<K> keySet() {
        Object[] snapshot = new Object[cproverOrderedCount];
        for (int i = 0; i < cproverOrderedCount; i++) {
            snapshot[i] = cproverOrderedKeys[i];
        }
        return new HashSet<K>(snapshot, cproverOrderedCount);
    }

    @Override
    public Collection<V> values() {
        ArrayList<V> vals = new ArrayList<V>(cproverOrderedCount);
        for (int i = 0; i < cproverOrderedCount; i++) {
            vals.add(super.get(cproverOrderedKeys[i]));
        }
        return vals;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Object[] entries = new Object[cproverOrderedCount];
        for (int i = 0; i < cproverOrderedCount; i++) {
            K k = (K) cproverOrderedKeys[i];
            entries[i] = new CProverMapEntry<K, V>(k, super.get(k));
        }
        return new HashSet<Map.Entry<K, V>>(entries, cproverOrderedCount);
    }

    // Mutators that would bypass the order overlay if inherited: fail
    // loudly rather than silently iterating in a stale order.
    @Override
    public V compute(K key,
        java.util.function.BiFunction<? super K, ? super V, ? extends V> f) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    @Override
    public V computeIfAbsent(K key,
        java.util.function.Function<? super K, ? extends V> f) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    @Override
    public V computeIfPresent(K key,
        java.util.function.BiFunction<? super K, ? super V, ? extends V> f) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }

    @Override
    public V merge(K key, V value,
        java.util.function.BiFunction<? super V, ? super V, ? extends V> f) {
        CProver.notModelled();
        return CProver.nondetWithNullForNotModelled();
    }
}
