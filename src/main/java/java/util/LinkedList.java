/*
 * Copyright (c) 1997, 2013, Oracle and/or its affiliates. All rights reserved.
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

import java.util.function.Consumer;

import org.cprover.CProver;

/**
 * Doubly-linked list implementation of the {@code List} and {@code Deque}
 * interfaces.  Implements all optional list operations, and permits all
 * elements (including {@code null}).
 *
 * <p>All of the operations perform as could be expected for a doubly-linked
 * list.  Operations that index into the list will traverse the list from
 * the beginning or the end, whichever is closer to the specified index.
 *
 * <p><strong>Note that this implementation is not synchronized.</strong>
 * If multiple threads access a linked list concurrently, and at least
 * one of the threads modifies the list structurally, it <i>must</i> be
 * synchronized externally.  (A structural modification is any operation
 * that adds or deletes one or more elements; merely setting the value of
 * an element is not a structural modification.)  This is typically
 * accomplished by synchronizing on some object that naturally
 * encapsulates the list.
 *
 * If no such object exists, the list should be "wrapped" using the
 * {@link Collections#synchronizedList Collections.synchronizedList}
 * method.  This is best done at creation time, to prevent accidental
 * unsynchronized access to the list:<pre>
 *   List list = Collections.synchronizedList(new LinkedList(...));</pre>
 *
 * <p>The iterators returned by this class's {@code iterator} and
 * {@code listIterator} methods are <i>fail-fast</i>: if the list is
 * structurally modified at any time after the iterator is created, in
 * any way except through the Iterator's own {@code remove} or
 * {@code add} methods, the iterator will throw a {@link
 * ConcurrentModificationException}.  Thus, in the face of concurrent
 * modification, the iterator fails quickly and cleanly, rather than
 * risking arbitrary, non-deterministic behavior at an undetermined
 * time in the future.
 *
 * <p>Note that the fail-fast behavior of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification.  Fail-fast iterators
 * throw {@code ConcurrentModificationException} on a best-effort basis.
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness:   <i>the fail-fast behavior of iterators
 * should be used only to detect bugs.</i>
 *
 * <p>This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @author  Josh Bloch
 * @see     List
 * @see     ArrayList
 * @since 1.2
 * @param <E> the type of elements held in this collection
 *
 * @diffblue.limitedSupport
 * Only a few high-priority methods are modelled.
 * The model uses a circular buffer to store the elements of the linked list.
 * Circular buffers are commonly used for queues with bounded capacity.
 * For performance reasons, there may be restrictions on the capacity of this
 * buffer, which limits the number of elements which can be stored in the model
 * of LinkedList:
 * <p><ul>
 * <li> LinkedLists constructed using constructors of this class will have a
 *      fixed capacity of CProver.defaultContainerCapacity().
 * <li> Non-deterministic LinkedLists are limited by the JBMC
 *      parameter `--max-nondet-array-length`.
 * <li> LinkedLists read from `--static-values` are currently unlimited.
 * </ul>
 * LinkedLists with a greater number of elements are not supported.
 * Adding/Removing elements to/from the beginning/end of the queue is easy to
 * analyse, and traces should be generated in a short amount of time. Adding or
 * removing elements to/from the middle of the array (not modelled yet) will be
 * more complicated and JBMC will take longer.
 * <p> The {@code ListIterator} implementation provided in this model is not
 * supported as an input or a return value as long as custom initialisers have
 * not been implemented in JBMC.
 */

public class LinkedList<E>
    extends AbstractSequentialList<E>
    implements List<E>, Deque<E>, Cloneable, java.io.Serializable
{
    transient int size = 0;

    /**
     * Pointer to first node.
     * Invariant: (first == null && last == null) ||
     *            (first.prev == null && first.item != null)
     */
    // DIFFBLUE MODEL LIBRARY
    // This package-private variable is only used in the original implementation
    // of LinkedList, not in the model.
    // transient Node<E> first;

    /**
     * Pointer to last node.
     * Invariant: (first == null && last == null) ||
     *            (last.next == null && last.item != null)
     */
    // DIFFBLUE MODEL LIBRARY
    // This package-private variable is only used in the original implementation
    // of LinkedList, not in the model.
    // transient Node<E> last;

    // DIFFBLUE MODEL LIBRARY
    // We avoid the doubly-linked list data structure from the original
    // implementation, which would be difficult to analyse for CBMC, and instead
    // use a circular buffer representation. This representation consists of
    // - an array cproverData
    // - an index cproverFirstIndex
    // - a value size.
    // The first element of the list (if nonempty) is stored at
    // cproverData[cproverFirstIndex], and the element n steps after the first
    // one is stored at
    // cproverData[(cproverFirstIndex + n) mod cproverData.length],
    // i.e., indices of subsequent elements increase by 1 until the end of the
    // array is reached, at which point the structure "wraps around" and the
    // index of the next element is 0.
    // The empty case (size == 0) is treated as a special case when necessary.
    E[] cproverData;
    int cproverFirstIndex;

    /**
     * Constructs an empty list.
     *
     * @diffblue.limitedSupport
     * The number of elements in a LinkedList created that way is limited by
     * CProver.defaultContainerCapacity().
     */
    public LinkedList() {
        // DIFFBLUE MODEL LIBRARY
        // cproverData = (E[]) new Object[CProver.defaultContainerCapacity()];
        cproverData = (E[]) new Object[4096];
        cproverFirstIndex = 0;
        size = 0;
    }

    /**
     * Constructs a list containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.
     *
     * @param  c the collection whose elements are to be placed into this list
     * @throws NullPointerException if the specified collection is null
     *
     * @diffblue.fullSupport
     */
    public LinkedList(Collection<? extends E> c) {
        this();
        addAll(c);
    }

    /**
     * Links e as first element.
     */
    private void linkFirst(E e) {
        // final Node<E> f = first;
        // final Node<E> newNode = new Node<>(null, e, f);
        // first = newNode;
        // if (f == null)
        //     last = newNode;
        // else
        //     f.prev = newNode;
        // size++;
        // modCount++;
        int newFirst = cproverGetShiftedIndex(cproverFirstIndex, -1);
        CProver.assume(newFirst != cproverGetLastIndex() || size == 0);
        cproverData[newFirst] = e;
        cproverFirstIndex = newFirst;
        size++;
        modCount++;
    }

    /**
     * Links e as last element.
     */
    void linkLast(E e) {
        // final Node<E> l = last;
        // final Node<E> newNode = new Node<>(l, e, null);
        // last = newNode;
        // if (l == null)
        //     first = newNode;
        // else
        //     l.next = newNode;
        // size++;
        // modCount++;
        int newLast = cproverGetShiftedIndex(cproverGetLastIndex(), 1);
        CProver.assume(newLast != cproverFirstIndex || size == 0);
        cproverData[newLast] = e;
        size++;
        modCount++;
    }

    /**
     * Inserts element e before non-null Node succ.
     */
    // DIFFBLUE MODEL LIBRARY Package-private method
    // void linkBefore(E e, Node<E> succ) {
        // // assert succ != null;
        // final Node<E> pred = succ.prev;
        // final Node<E> newNode = new Node<>(pred, e, succ);
        // succ.prev = newNode;
        // if (pred == null)
        //     first = newNode;
        // else
        //     pred.next = newNode;
        // size++;
        // modCount++;
    // }

    /**
     * Unlinks non-null first node f.
     */
    // DIFFBLUE MODEL LIBRARY Private method
    // private E unlinkFirst(Node<E> f) {
        // // assert f == first && f != null;
        // final E element = f.item;
        // final Node<E> next = f.next;
        // f.item = null;
        // f.next = null; // help GC
        // first = next;
        // if (next == null)
        //     last = null;
        // else
        //     next.prev = null;
        // size--;
        // modCount++;
        // return element;
    // }

    /**
     * Unlinks non-null last node l.
     */
    // DIFFBLUE MODEL LIBRARY Private method
    // private E unlinkLast(Node<E> l) {
        // // assert l == last && l != null;
        // final E element = l.item;
        // final Node<E> prev = l.prev;
        // l.item = null;
        // l.prev = null; // help GC
        // last = prev;
        // if (prev == null)
        //     first = null;
        // else
        //     prev.next = null;
        // size--;
        // modCount++;
        // return element;
    // }

    /**
     * Unlinks non-null node x.
     */
    // DIFFBLUE MODEL LIBRARY Package-private method
    // E unlink(Node<E> x) {
        // // assert x != null;
        // final E element = x.item;
        // final Node<E> next = x.next;
        // final Node<E> prev = x.prev;

        // if (prev == null) {
        //     first = next;
        // } else {
        //     prev.next = next;
        //     x.prev = null;
        // }

        // if (next == null) {
        //     last = prev;
        // } else {
        //     next.prev = prev;
        //     x.next = null;
        // }

        // x.item = null;
        // size--;
        // modCount++;
        // return element;
    // }

    /**
     * Returns the first element in this list.
     *
     * @return the first element in this list
     * @throws NoSuchElementException if this list is empty
     *
     * @diffblue.fullSupport
     */
    public E getFirst() {
        // final Node<E> f = first;
        // if (f == null)
        //     throw new NoSuchElementException();
        // return f.item;
        if (size == 0) {
            throw new NoSuchElementException();
        }
        return cproverData[cproverFirstIndex];
    }

    /**
     * Returns the last element in this list.
     *
     * @return the last element in this list
     * @throws NoSuchElementException if this list is empty
     *
     * @diffblue.fullSupport
     */
    public E getLast() {
        // final Node<E> l = last;
        // if (l == null)
        //     throw new NoSuchElementException();
        // return l.item;
        if (size == 0)
            throw new NoSuchElementException();
        return cproverData[cproverGetLastIndex()];
    }

    /**
     * Removes and returns the first element from this list.
     *
     * @return the first element from this list
     * @throws NoSuchElementException if this list is empty
     *
     * @diffblue.fullSupport
     */
    public E removeFirst() {
        // final Node<E> f = first;
        // if (f == null)
        //     throw new NoSuchElementException();
        // return unlinkFirst(f);
        if (size == 0)
            throw new NoSuchElementException();
        E first = cproverData[cproverFirstIndex];
        cproverFirstIndex = cproverGetShiftedIndex(cproverFirstIndex, 1);
        size--;
        modCount++;
        return first;
    }

    /**
     * Removes and returns the last element from this list.
     *
     * @return the last element from this list
     * @throws NoSuchElementException if this list is empty
     *
     * @diffblue.fullSupport
     */
    public E removeLast() {
        // final Node<E> l = last;
        // if (l == null)
        //     throw new NoSuchElementException();
        // return unlinkLast(l);
        if (size == 0)
            throw new NoSuchElementException();
        E last = cproverData[cproverGetLastIndex()];
        size--;
        modCount++;
        return last;
    }

    /**
     * Inserts the specified element at the beginning of this list.
     *
     * @param e the element to add
     *
     * @diffblue.fullSupport
     */
    public void addFirst(E e) {
        linkFirst(e);
   }

    /**
     * Appends the specified element to the end of this list.
     *
     * <p>This method is equivalent to {@link #add}.
     *
     * @param e the element to add
     *
     * @diffblue.fullSupport
     * @diffblue.untested
     * Untested since practically the same implementation as add()
     */
    public void addLast(E e) {
        linkLast(e);
    }

    /**
     * Returns {@code true} if this list contains the specified element.
     * More formally, returns {@code true} if and only if this list contains
     * at least one element {@code e} such that
     * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
     *
     * @param o element whose presence in this list is to be tested
     * @return {@code true} if this list contains the specified element
     *
     * @diffblue.fullSupport
     */
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements in this list
     *
     * @diffblue.fullSupport
     */
    public int size() {
        return size;
    }

    /**
     * Appends the specified element to the end of this list.
     *
     * <p>This method is equivalent to {@link #addLast}.
     *
     * @param e element to be appended to this list
     * @return {@code true} (as specified by {@link Collection#add})
     *
     * @diffblue.fullSupport
     */
    public boolean add(E e) {
        linkLast(e);
        return true;
    }

    /**
     * Removes the first occurrence of the specified element from this list,
     * if it is present.  If this list does not contain the element, it is
     * unchanged.  More formally, removes the element with the lowest index
     * {@code i} such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>
     * (if such an element exists).  Returns {@code true} if this list
     * contained the specified element (or equivalently, if this list
     * changed as a result of the call).
     *
     * @param o element to be removed from this list, if present
     * @return {@code true} if this list contained the specified element
     *
     * @diffblue.limitedSupport
     * No traces generated for String lists containing null values
     */
    public boolean remove(Object o) {
        // if (o == null) {
        //     for (Node<E> x = first; x != null; x = x.next) {
        //         if (x.item == null) {
        //             unlink(x);
        //             return true;
        //         }
        //     }
        // } else {
        //     for (Node<E> x = first; x != null; x = x.next) {
        //         if (o.equals(x.item)) {
        //             unlink(x);
        //             return true;
        //         }
        //     }
        // }
        // return false;
        // We use indexOf to get the index of o then call remove(int).
        // If the index doesn't exist, indexOf returns -1.
        int i = indexOf(o);
        if (i == -1) {
            return false;
        }
        remove(i);
        return true;
    }

    /**
     * Appends all of the elements in the specified collection to the end of
     * this list, in the order that they are returned by the specified
     * collection's iterator.  The behavior of this operation is undefined if
     * the specified collection is modified while the operation is in
     * progress.  (Note that this will occur if the specified collection is
     * this list, and it's nonempty.)
     *
     * @param c collection containing elements to be added to this list
     * @return {@code true} if this list changed as a result of the call
     * @throws NullPointerException if the specified collection is null
     *
     * @diffblue.fullSupport
     */
    public boolean addAll(Collection<? extends E> c) {
        // return addAll(size, c);
        boolean modified = false;
        E[] a = (E[]) c.toArray();
        for (int i = 0; i < a.length; i++) {
            modified |= add(a[i]);
        }
        return modified;
    }

    /**
     * Inserts all of the elements in the specified collection into this
     * list, starting at the specified position.  Shifts the element
     * currently at that position (if any) and any subsequent elements to
     * the right (increases their indices).  The new elements will appear
     * in the list in the order that they are returned by the
     * specified collection's iterator.
     *
     * @param index index at which to insert the first element
     *              from the specified collection
     * @param c collection containing elements to be added to this list
     * @return {@code true} if this list changed as a result of the call
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @throws NullPointerException if the specified collection is null
     *
     * @diffblue.noSupport
     */
    public boolean addAll(int index, Collection<? extends E> c) {
        // checkPositionIndex(index);

        // Object[] a = c.toArray();
        // int numNew = a.length;
        // if (numNew == 0)
        //     return false;

        // Node<E> pred, succ;
        // if (index == size) {
        //     succ = null;
        //     pred = last;
        // } else {
        //     succ = node(index);
        //     pred = succ.prev;
        // }

        // for (Object o : a) {
        //     @SuppressWarnings("unchecked") E e = (E) o;
        //     Node<E> newNode = new Node<>(pred, e, null);
        //     if (pred == null)
        //         first = newNode;
        //     else
        //         pred.next = newNode;
        //     pred = newNode;
        // }

        // if (succ == null) {
        //     last = pred;
        // } else {
        //     pred.next = succ;
        //     succ.prev = pred;
        // }

        // size += numNew;
        // modCount++;
        // return true;
        CProver.notModelled();
        return CProver.nondetBoolean();
    }

    /**
     * Removes all of the elements from this list.
     * The list will be empty after this call returns.
     *
     * @diffblue.fullSupport
     */
    // DIFFBLUE MODEL LIBRARY
    // Memory management can be ignored in the model.
    public void clear() {
        // // Clearing all of the links between nodes is "unnecessary", but:
        // // - helps a generational GC if the discarded nodes inhabit
        // //   more than one generation
        // // - is sure to free memory even if there is a reachable Iterator
        // for (Node<E> x = first; x != null; ) {
        //     Node<E> next = x.next;
        //     x.item = null;
        //     x.next = null;
        //     x.prev = null;
        //     x = next;
        // }
        // first = last = null;
        // size = 0;
        // modCount++;
        modCount++;
        size = 0;
        cproverFirstIndex = 0;
    }


    // Positional Access Operations

    /**
     * Returns the element at the specified position in this list.
     *
     * @param index index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     *
     * @diffblue.limitedSupport
     * We do not support exception messages.
     */
    public E get(int index) {
        checkElementIndex(index);
        // return node(index).item;
        int dest = cproverGetShiftedIndex(cproverFirstIndex, index);
        return cproverData[dest];
    }

    /**
     * Replaces the element at the specified position in this list with the
     * specified element.
     *
     * @param index index of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException {@inheritDoc}
     *
     * @diffblue.fullSupport
     */
    public E set(int index, E element) {
        // checkElementIndex(index);
        // Node<E> x = node(index);
        // E oldVal = x.item;
        // x.item = element;
        // return oldVal;
        checkElementIndex(index);
        E oldValue = get(index);
        int dest = cproverGetShiftedIndex(cproverFirstIndex, index);
        cproverData[dest] = element;
        return oldValue;
    }

    /**
     * Inserts the specified element at the specified position in this list.
     * Shifts the element currently at that position (if any) and any
     * subsequent elements to the right (adds one to their indices).
     *
     * @param index index at which the specified element is to be inserted
     * @param element element to be inserted
     * @throws IndexOutOfBoundsException {@inheritDoc}
     *
     * @diffblue.fullSupport
     */
    public void add(int index, E element) {
        // checkPositionIndex(index);

        // if (index == size)
        //     linkLast(element);
        // else
        //     linkBefore(element, node(index));
        checkPositionIndex(index);
        int newLast = cproverGetShiftedIndex(cproverGetLastIndex(), 1);
        CProver.assume(newLast != cproverFirstIndex || size == 0);
        for (int i = newLast; i > index; i--) {
            cproverData[cproverGetShiftedIndex(cproverFirstIndex, i)] =
                    cproverData[cproverGetShiftedIndex(cproverFirstIndex, i-1)];
        }
        cproverData[cproverGetShiftedIndex(cproverFirstIndex, index)] = element;
        size++;
        modCount++;
    }

    /**
     * Removes the element at the specified position in this list.  Shifts any
     * subsequent elements to the left (subtracts one from their indices).
     * Returns the element that was removed from the list.
     *
     * @param index the index of the element to be removed
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException {@inheritDoc}
     *
     * @diffblue.fullSupport
     */
    public E remove(int index) {
        // checkElementIndex(index);
        // return unlink(node(index));
        checkElementIndex(index);
        E oldValue = cproverData[cproverGetShiftedIndex(cproverFirstIndex, index)];
        for (int i = index; i < size - 1; i++) {
            cproverData[cproverGetShiftedIndex(cproverFirstIndex, i)] =
                    cproverData[cproverGetShiftedIndex(cproverFirstIndex, i+1)];
        }
        size--;
        modCount++;
        return oldValue;
    }

    /**
     * Tells if the argument is the index of an existing element.
     */
    private boolean isElementIndex(int index) {
        return index >= 0 && index < size;
    }

    /**
     * Tells if the argument is the index of a valid position for an
     * iterator or an add operation.
     */
    private boolean isPositionIndex(int index) {
        return index >= 0 && index <= size;
    }

    /**
     * Constructs an IndexOutOfBoundsException detail message.
     * Of the many possible refactorings of the error handling code,
     * this "outlining" performs best with both server and client VMs.
     */
    // DIFFBLUE MODEL LIBRARY Private method
    // private String outOfBoundsMsg(int index) {
    //     return "Index: "+index+", Size: "+size;
    // }

    private void checkElementIndex(int index) {
        if (!isElementIndex(index))
            // throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
            // DIFFBLUE MODEL LIBRARY
            // We avoid the string concatenation in exception messages to speed
            // up JBMC.
            throw new IndexOutOfBoundsException();
    }

    private void checkPositionIndex(int index) {
        if (!isPositionIndex(index))
            // throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
            // DIFFBLUE MODEL LIBRARY
            // We avoid the string concatenation in exception messages to speed
            // up JBMC.
            throw new IndexOutOfBoundsException();
    }

    /**
     * Returns the (non-null) Node at the specified element index.
     */
    // DIFFBLUE MODEL LIBRARY Package-private method
    // Node<E> node(int index) {
        // // assert isElementIndex(index);

        // if (index < (size >> 1)) {
        //     Node<E> x = first;
        //     for (int i = 0; i < index; i++)
        //         x = x.next;
        //     return x;
        // } else {
        //     Node<E> x = last;
        //     for (int i = size - 1; i > index; i--)
        //         x = x.prev;
        //     return x;
        // }
    // }

    // Search Operations

    /**
     * Returns the index of the first occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the lowest index {@code i} such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     *
     * @param o element to search for
     * @return the index of the first occurrence of the specified element in
     *         this list, or -1 if this list does not contain the element
     *
     * @diffblue.limitedSupport
     * No traces generated for String lists containing null values
     */
    public int indexOf(Object o) {
        // int index = 0;
        // if (o == null) {
        //     for (Node<E> x = first; x != null; x = x.next) {
        //         if (x.item == null)
        //             return index;
        //         index++;
        //     }
        // } else {
        //     for (Node<E> x = first; x != null; x = x.next) {
        //         if (o.equals(x.item))
        //             return index;
        //         index++;
        //     }
        // }
        // return -1;
        if (o == null) {
            for (int i = 0; i < size; i++)
                if (cproverData[cproverGetShiftedIndex(cproverFirstIndex, i)]==null)
                    return i;
        } else {
            for (int i = 0; i < size; i++)
                if (o.equals(cproverData[cproverGetShiftedIndex(cproverFirstIndex, i)]))
                    return i;
        }
        return -1;
    }

    /**
     * Returns the index of the last occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the highest index {@code i} such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     *
     * @param o element to search for
     * @return the index of the last occurrence of the specified element in
     *         this list, or -1 if this list does not contain the element
     *
     * @diffblue.limitedSupport
     * No traces generated for String lists containing null values
     */
    public int lastIndexOf(Object o) {
        // int index = size;
        // if (o == null) {
        //     for (Node<E> x = last; x != null; x = x.prev) {
        //         index--;
        //         if (x.item == null)
        //             return index;
        //     }
        // } else {
        //     for (Node<E> x = last; x != null; x = x.prev) {
        //         index--;
        //         if (o.equals(x.item))
        //             return index;
        //     }
        // }
        // return -1;
        if (o == null) {
            for (int i = size-1; i >= 0; i--)
                if (cproverData[cproverGetShiftedIndex(cproverFirstIndex, i)]==null)
                    return i;
        } else {
            for (int i = size-1; i >= 0; i--)
                if (o.equals(cproverData[cproverGetShiftedIndex(cproverFirstIndex, i)]))
                    return i;
        }
        return -1;
    }

    // Queue operations.

    /**
     * Retrieves, but does not remove, the head (first element) of this list.
     *
     * @return the head of this list, or {@code null} if this list is empty
     * @since 1.5
     *
     * @diffblue.fullSupport
     */
    public E peek() {
        // final Node<E> f = first;
        // return (f == null) ? null : f.item;
        return (size == 0) ? null : cproverData[cproverFirstIndex];
    }

    /**
     * Retrieves, but does not remove, the head (first element) of this list.
     *
     * @return the head of this list
     * @throws NoSuchElementException if this list is empty
     * @since 1.5
     *
     * @diffblue.untested
     * @diffblue.fullSupport
     * Untested since simply calls getFirst()
     */
    public E element() {
        return getFirst();
    }

    /**
     * Retrieves and removes the head (first element) of this list.
     *
     * @return the head of this list, or {@code null} if this list is empty
     * @since 1.5
     *
     * @diffblue.fullSupport
     */
    public E poll() {
        // final Node<E> f = first;
        // return (f == null) ? null : unlinkFirst(f);
        if (size == 0)
            return null;
        E first = cproverData[cproverFirstIndex];
        cproverFirstIndex = cproverGetShiftedIndex(cproverFirstIndex, 1);
        size--;
        modCount++;
        return first;
    }

    /**
     * Retrieves and removes the head (first element) of this list.
     *
     * @return the head of this list
     * @throws NoSuchElementException if this list is empty
     * @since 1.5
     *
     * @diffblue.untested
     * @diffblue.fullSupport
     * Untested since simply calls removeFirst()
     */
    public E remove() {
        return removeFirst();
    }

    /**
     * Adds the specified element as the tail (last element) of this list.
     *
     * @param e the element to add
     * @return {@code true} (as specified by {@link Queue#offer})
     * @since 1.5
     *
     * @diffblue.untested
     * @diffblue.fullSupport
     * Untested since simply calls add()
     */
    public boolean offer(E e) {
        return add(e);
    }

    // Deque operations
    /**
     * Inserts the specified element at the front of this list.
     *
     * @param e the element to insert
     * @return {@code true} (as specified by {@link Deque#offerFirst})
     * @since 1.6
     *
     * @diffblue.untested
     * @diffblue.fullSupport
     * Untested since simply calls addFirst()
     */
    public boolean offerFirst(E e) {
        addFirst(e);
        return true;
    }

    /**
     * Inserts the specified element at the end of this list.
     *
     * @param e the element to insert
     * @return {@code true} (as specified by {@link Deque#offerLast})
     * @since 1.6
     *
     * @diffblue.untested
     * @diffblue.fullSupport
     * Untested since simply calls addLast
     */
    public boolean offerLast(E e) {
        addLast(e);
        return true;
    }

    /**
     * Retrieves, but does not remove, the first element of this list,
     * or returns {@code null} if this list is empty.
     *
     * @return the first element of this list, or {@code null}
     *         if this list is empty
     * @since 1.6
     *
     * @diffblue.untested
     * @diffblue.fullSupport
     * Untested since same implementation as peek()
     */
    public E peekFirst() {
        // final Node<E> f = first;
        // return (f == null) ? null : f.item;
        return peek();
    }

    /**
     * Retrieves, but does not remove, the last element of this list,
     * or returns {@code null} if this list is empty.
     *
     * @return the last element of this list, or {@code null}
     *         if this list is empty
     * @since 1.6
     *
     * @diffblue.untested
     * @diffblue.fullSupport
     * Untested since trivial
     */
    public E peekLast() {
        // final Node<E> l = last;
        // return (l == null) ? null : l.item;
        return (size == 0) ? null : cproverData[cproverGetLastIndex()];
    }

    /**
     * Retrieves and removes the first element of this list,
     * or returns {@code null} if this list is empty.
     *
     * @return the first element of this list, or {@code null} if
     *     this list is empty
     * @since 1.6
     *
     * @diffblue.fullSupport
     * @diffblue.untested
     * Untested since same implementation as poll()
     */
    public E pollFirst() {
        // final Node<E> f = first;
        // return (f == null) ? null : unlinkFirst(f);
        return poll();
    }

    /**
     * Retrieves and removes the last element of this list,
     * or returns {@code null} if this list is empty.
     *
     * @return the last element of this list, or {@code null} if
     *     this list is empty
     * @since 1.6
     *
     * @diffblue.fullSupport
     */
    public E pollLast() {
        // final Node<E> l = last;
        // return (l == null) ? null : unlinkLast(l);
        if (size == 0)
            return null;
        E last = cproverData[cproverGetLastIndex()];
        size--;
        modCount++;
        return last;
    }

    /**
     * Pushes an element onto the stack represented by this list.  In other
     * words, inserts the element at the front of this list.
     *
     * <p>This method is equivalent to {@link #addFirst}.
     *
     * @param e the element to push
     * @since 1.6
     *
     * @diffblue.fullSupport
     */
    public void push(E e) {
        addFirst(e);
    }

    /**
     * Pops an element from the stack represented by this list.  In other
     * words, removes and returns the first element of this list.
     *
     * <p>This method is equivalent to {@link #removeFirst()}.
     *
     * @return the element at the front of this list (which is the top
     *         of the stack represented by this list)
     * @throws NoSuchElementException if this list is empty
     * @since 1.6
     *
     * @diffblue.fullSupport
     * @diffblue.untested
     * Untested since simply calls removeFirst()
     */
    public E pop() {
        return removeFirst();
    }

    /**
     * Removes the first occurrence of the specified element in this
     * list (when traversing the list from head to tail).  If the list
     * does not contain the element, it is unchanged.
     *
     * @param o element to be removed from this list, if present
     * @return {@code true} if the list contained the specified element
     * @since 1.6
     *
     * @diffblue.limitedSupport
     * @diffblue.untested
     * Untested since simply calls remove()
     * No traces generated for String lists containing null values
     */
    public boolean removeFirstOccurrence(Object o) {
        return remove(o);
    }

    /**
     * Removes the last occurrence of the specified element in this
     * list (when traversing the list from head to tail).  If the list
     * does not contain the element, it is unchanged.
     *
     * @param o element to be removed from this list, if present
     * @return {@code true} if the list contained the specified element
     * @since 1.6
     *
     * @diffblue.limitedSupport
     * No traces generated for String lists containing null values
     */
    public boolean removeLastOccurrence(Object o) {
        // if (o == null) {
        //     for (Node<E> x = last; x != null; x = x.prev) {
        //         if (x.item == null) {
        //             unlink(x);
        //             return true;
        //         }
        //     }
        // } else {
        //     for (Node<E> x = last; x != null; x = x.prev) {
        //         if (o.equals(x.item)) {
        //             unlink(x);
        //             return true;
        //         }
        //     }
        // }
        // return false;
        int i = lastIndexOf(o);
        if (i == -1) {
            return false;
        }
        remove(i);
        return true;
    }

    /**
     * Returns a list-iterator of the elements in this list (in proper
     * sequence), starting at the specified position in the list.
     * Obeys the general contract of {@code List.listIterator(int)}.<p>
     *
     * The list-iterator is <i>fail-fast</i>: if the list is structurally
     * modified at any time after the Iterator is created, in any way except
     * through the list-iterator's own {@code remove} or {@code add}
     * methods, the list-iterator will throw a
     * {@code ConcurrentModificationException}.  Thus, in the face of
     * concurrent modification, the iterator fails quickly and cleanly, rather
     * than risking arbitrary, non-deterministic behavior at an undetermined
     * time in the future.
     *
     * @param index index of the first element to be returned from the
     *              list-iterator (by a call to {@code next})
     * @return a ListIterator of the elements in this list (in proper
     *         sequence), starting at the specified position in the list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @see List#listIterator(int)
     *
     * @diffblue.limitedSupport
     * Exception messages not supported and not all iterator methods modelled
     */
    public ListIterator<E> listIterator(int index) {
        checkPositionIndex(index);
        return new ListItr(index);
    }

    private class ListItr implements ListIterator<E> {
        // private Node<E> lastReturned;
        // private Node<E> next;

        // DIFFBLUE MODEL LIBRARY
        // We store the elements directly instead of node objects
        private E cproverLastReturned;
        private E cproverNext;
        private int cproverLastIndex; // index of last element returned

        private int nextIndex;
        private int expectedModCount = modCount;

        ListItr(int index) {
            // // assert isPositionIndex(index);
            // next = (index == size) ? null : node(index);
            // nextIndex = index;
            cproverNext = (index == size) ? null : get(index);
            nextIndex = index;
            cproverLastIndex = -1;
        }

        public boolean hasNext() {
            return nextIndex < size;
        }

        public E next() {
            // checkForComodification();
            // if (!hasNext())
            //     throw new NoSuchElementException();

            // lastReturned = next;
            // next = next.next;
            // nextIndex++;
            // return lastReturned.item;
            checkForComodification();
            if (!hasNext())
                throw new NoSuchElementException();
            cproverLastReturned = cproverNext;
            cproverLastIndex = nextIndex;
            nextIndex++;
            cproverNext = (nextIndex == size) ? null : get(nextIndex);
            return cproverLastReturned;
        }

        public boolean hasPrevious() {
            return nextIndex > 0;
        }

        public E previous() {
            // checkForComodification();
            // if (!hasPrevious())
            //     throw new NoSuchElementException();

            // lastReturned = next = (next == null) ? last : next.prev;
            // nextIndex--;
            // return lastReturned.item;
            checkForComodification();
            if (!hasPrevious())
                throw new NoSuchElementException();
            nextIndex--;
            cproverLastIndex = nextIndex;
            cproverNext = (nextIndex == size) ? getLast() : get(nextIndex);
            cproverLastReturned = cproverNext;
            return cproverLastReturned;
        }

        public int nextIndex() {
            return nextIndex;
        }

        public int previousIndex() {
            return nextIndex - 1;
        }

        public void remove() {
            // checkForComodification();
            // if (lastReturned == null)
            //     throw new IllegalStateException();

            // Node<E> lastNext = lastReturned.next;
            // unlink(lastReturned);
            // if (next == lastReturned)
            //     next = lastNext;
            // else
            //     nextIndex--;
            // lastReturned = null;
            // expectedModCount++;
            CProver.notModelled();
        }

        public void set(E e) {
            // if (lastReturned == null)
            //     throw new IllegalStateException();
            // checkForComodification();
            // lastReturned.item = e;
            if (cproverLastReturned == null)
                throw new IllegalStateException();
            checkForComodification();
            LinkedList.this.set(cproverLastIndex, e);
        }

        public void add(E e) {
            // checkForComodification();
            // lastReturned = null;
            // if (next == null)
            //     linkLast(e);
            // else
            //     linkBefore(e, next);
            // nextIndex++;
            // expectedModCount++;
            CProver.notModelled();
        }

        public void forEachRemaining(Consumer<? super E> action) {
            // Objects.requireNonNull(action);
            // while (modCount == expectedModCount && nextIndex < size) {
            //     action.accept(next.item);
            //     lastReturned = next;
            //     next = next.next;
            //     nextIndex++;
            // }
            // checkForComodification();
            CProver.notModelled();
        }

        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

    // DIFFBLUE MODEL LIBRARY
    // Private class used for the original implementation of LinkedList
    // private static class Node<E> {
        // E item;
        // Node<E> next;
        // Node<E> prev;

        // Node(Node<E> prev, E element, Node<E> next) {
            // this.item = element;
            // this.next = next;
            // this.prev = prev;
        // }
    // }

    /**
     * @since 1.6
     *
     * @diffblue.limitedSupport
     * Descending iterator doesn't have remove() modelled
     * Assertion error from multiple calls to next(), returns
     * only the first element correctly
     */
    public Iterator<E> descendingIterator() {
        return new DescendingIterator();
    }

    /**
     * Adapter to provide descending iterators via ListItr.previous
     */
    private class DescendingIterator implements Iterator<E> {
        private final ListItr itr = new ListItr(size());
        public boolean hasNext() {
            return itr.hasPrevious();
        }
        public E next() {
            return itr.previous();
        }
        public void remove() {
            // itr.remove();
            CProver.notModelled();
        }
    }

    // DIFFBLUE MODEL LIBRARY Private method
    // @SuppressWarnings("unchecked")
    // private LinkedList<E> superClone() {
        // try {
        //     return (LinkedList<E>) super.clone();
        // } catch (CloneNotSupportedException e) {
        //     throw new InternalError(e);
        // }
    // }

    /**
     * Returns a shallow copy of this {@code LinkedList}. (The elements
     * themselves are not cloned.)
     *
     * @return a shallow copy of this {@code LinkedList} instance
     *
     * @diffblue.fullSupport
     */
    public Object clone() {
        // LinkedList<E> clone = superClone();

        // // Put clone into "virgin" state
        // clone.first = clone.last = null;
        // clone.size = 0;
        // clone.modCount = 0;

        // // Initialize clone with our elements
        // for (Node<E> x = first; x != null; x = x.next)
        //     clone.add(x.item);

        // return clone;
        LinkedList<E> v = new LinkedList<E>();
        CProver.assume(v.cproverData.length >= size);
        for (int i = 0; i < size; i++) {
            v.cproverData[i] = cproverData[i];
        }
        v.modCount = 0;
        v.size = size;
        v.cproverFirstIndex = cproverFirstIndex;
        return v;
    }

    /**
     * Returns an array containing all of the elements in this list
     * in proper sequence (from first to last element).
     *
     * <p>The returned array will be "safe" in that no references to it are
     * maintained by this list.  (In other words, this method must allocate
     * a new array).  The caller is thus free to modify the returned array.
     *
     * <p>This method acts as bridge between array-based and collection-based
     * APIs.
     *
     * @return an array containing all of the elements in this list
     *         in proper sequence
     *
     * @diffblue.limitedSupport
     * Arrays may not always be correctly generated
     */
    public Object[] toArray() {
        // Object[] result = new Object[size];
        // int i = 0;
        // for (Node<E> x = first; x != null; x = x.next)
        //     result[i++] = x.item;
        // return result;
        Object[] a = new Object[size];
        for (int i = 0; i < size; i++) {
            a[i] = cproverData[cproverGetShiftedIndex(cproverFirstIndex, i)];
        }
        return a;
    }

    /**
     * Returns an array containing all of the elements in this list in
     * proper sequence (from first to last element); the runtime type of
     * the returned array is that of the specified array.  If the list fits
     * in the specified array, it is returned therein.  Otherwise, a new
     * array is allocated with the runtime type of the specified array and
     * the size of this list.
     *
     * <p>If the list fits in the specified array with room to spare (i.e.,
     * the array has more elements than the list), the element in the array
     * immediately following the end of the list is set to {@code null}.
     * (This is useful in determining the length of the list <i>only</i> if
     * the caller knows that the list does not contain any null elements.)
     *
     * <p>Like the {@link #toArray()} method, this method acts as bridge between
     * array-based and collection-based APIs.  Further, this method allows
     * precise control over the runtime type of the output array, and may,
     * under certain circumstances, be used to save allocation costs.
     *
     * <p>Suppose {@code x} is a list known to contain only strings.
     * The following code can be used to dump the list into a newly
     * allocated array of {@code String}:
     *
     * <pre>
     *     String[] y = x.toArray(new String[0]);</pre>
     *
     * Note that {@code toArray(new Object[0])} is identical in function to
     * {@code toArray()}.
     *
     * @param a the array into which the elements of the list are to
     *          be stored, if it is big enough; otherwise, a new array of the
     *          same runtime type is allocated for this purpose.
     * @return an array containing the elements of the list
     * @throws ArrayStoreException if the runtime type of the specified array
     *         is not a supertype of the runtime type of every element in
     *         this list
     * @throws NullPointerException if the specified array is null
     *
     * @diffblue.limitedSupport
     * Arrays may not always be correctly generated
     *
     * DIFFBLUE MODEL LIBRARY
     * This behaves like the real JDK method, except that an ArrayStoreException
     * won't be raised if you pass in an array that can't store the actual
     * elements held in this container.
     */
    // @SuppressWarnings("unchecked")
    // (the JDK implementation makes an unchecked cast from Array.newInstance;
    //  we use CPRover.createArrayWithType instead)
    public <T> T[] toArray(T[] a) {
        // if (a.length < size)
        //     a = (T[])java.lang.reflect.Array.newInstance(
        //                         a.getClass().getComponentType(), size);
        // int i = 0;
        // Object[] result = a;
        // for (Node<E> x = first; x != null; x = x.next)
        //     result[i++] = x.item;

        // if (a.length > size)
        //     a[size] = null;

        // return a;
        if (a.length < size) {
            // DIFFBLUE MODEL LIBRARY
            // Object.getClass() is currently not modelled, so we need to use
            // createArrayWithType to create a new array of the required type.
            T[] newArray = CProver.createArrayWithType(size, a);
            for (int i = 0; i < size; i++) {
                newArray[i] = (T) cproverData[cproverGetShiftedIndex(cproverFirstIndex, i)];
            }
            return newArray;
        }
        for (int i = 0; i < size; i++) {
            a[i] = (T) cproverData[cproverGetShiftedIndex(cproverFirstIndex, i)];
        }
        if (a.length > size)
            a[size] = null;
        return a;
    }

    // DIFFBLUE MODEL LIBRARY Private field
    // private static final long serialVersionUID = 876323262645176354L;

    /**
     * Saves the state of this {@code LinkedList} instance to a stream
     * (that is, serializes it).
     *
     * @serialData The size of the list (the number of elements it
     *             contains) is emitted (int), followed by all of its
     *             elements (each an Object) in the proper order.
     */
    // DIFFBLUE MODEL LIBRARY Private method
    // private void writeObject(java.io.ObjectOutputStream s)
        // throws java.io.IOException {
        // // Write out any hidden serialization magic
        // s.defaultWriteObject();

        // // Write out size
        // s.writeInt(size);

        // // Write out all elements in the proper order.
        // for (Node<E> x = first; x != null; x = x.next)
        //     s.writeObject(x.item);
    // }

    /**
     * Reconstitutes this {@code LinkedList} instance from a stream
     * (that is, deserializes it).
     */
    // DIFFBLUE MODEL LIBRARY Private method
    // @SuppressWarnings("unchecked")
    // private void readObject(java.io.ObjectInputStream s)
        // throws java.io.IOException, ClassNotFoundException {
        // // Read in any hidden serialization magic
        // s.defaultReadObject();

        // // Read in size
        // int size = s.readInt();

        // // Read in all elements in the proper order.
        // for (int i = 0; i < size; i++)
        //     linkLast((E)s.readObject());
    // }

    /**
     * Creates a <em><a href="Spliterator.html#binding">late-binding</a></em>
     * and <em>fail-fast</em> {@link Spliterator} over the elements in this
     * list.
     *
     * <p>The {@code Spliterator} reports {@link Spliterator#SIZED} and
     * {@link Spliterator#ORDERED}.  Overriding implementations should document
     * the reporting of additional characteristic values.
     *
     * @implNote
     * The {@code Spliterator} additionally reports {@link Spliterator#SUBSIZED}
     * and implements {@code trySplit} to permit limited parallelism..
     *
     * @return a {@code Spliterator} over the elements in this list
     * @since 1.8
     *
     * @diffblue.noSupport
     */
    @Override
    public Spliterator<E> spliterator() {
        // return new LLSpliterator<E>(this, -1, 0);
        CProver.notModelled();
        return CProver.nondetWithoutNullForNotModelled();
    }

    /** A customized variant of Spliterators.IteratorSpliterator */
    // DIFFBLUE MODEL LIBRARY
    // Package-private class required for the spliterator() method, which is not
    // modelled yet.
    // static final class LLSpliterator<E> implements Spliterator<E> {
        // static final int BATCH_UNIT = 1 << 10;  // batch array size increment
        // static final int MAX_BATCH = 1 << 25;  // max batch array size;
        // final LinkedList<E> list; // null OK unless traversed
        // Node<E> current;      // current node; null until initialized
        // int est;              // size estimate; -1 until first needed
        // int expectedModCount; // initialized when est set
        // int batch;            // batch size for splits

        // LLSpliterator(LinkedList<E> list, int est, int expectedModCount) {
            // this.list = list;
            // this.est = est;
            // this.expectedModCount = expectedModCount;
        // }

        // final int getEst() {
            // int s; // force initialization
            // final LinkedList<E> lst;
            // if ((s = est) < 0) {
            //     if ((lst = list) == null)
            //         s = est = 0;
            //     else {
            //         expectedModCount = lst.modCount;
            //         current = lst.first;
            //         s = est = lst.size;
            //     }
            // }
            // return s;
        // }

        // public long estimateSize() { return (long) getEst(); }

        // public Spliterator<E> trySplit() {
            // Node<E> p;
            // int s = getEst();
            // if (s > 1 && (p = current) != null) {
            //     int n = batch + BATCH_UNIT;
            //     if (n > s)
            //         n = s;
            //     if (n > MAX_BATCH)
            //         n = MAX_BATCH;
            //     Object[] a = new Object[n];
            //     int j = 0;
            //     do { a[j++] = p.item; } while ((p = p.next) != null && j < n);
            //     current = p;
            //     batch = j;
            //     est = s - j;
            //     return Spliterators.spliterator(a, 0, j, Spliterator.ORDERED);
            // }
            // return null;
        // }

        // public void forEachRemaining(Consumer<? super E> action) {
            // Node<E> p; int n;
            // if (action == null) throw new NullPointerException();
            // if ((n = getEst()) > 0 && (p = current) != null) {
            //     current = null;
            //     est = 0;
            //     do {
            //         E e = p.item;
            //         p = p.next;
            //         action.accept(e);
            //     } while (p != null && --n > 0);
            // }
            // if (list.modCount != expectedModCount)
            //     throw new ConcurrentModificationException();
        // }

        // public boolean tryAdvance(Consumer<? super E> action) {
            // Node<E> p;
            // if (action == null) throw new NullPointerException();
            // if (getEst() > 0 && (p = current) != null) {
            //     --est;
            //     E e = p.item;
            //     current = p.next;
            //     action.accept(e);
            //     if (list.modCount != expectedModCount)
            //         throw new ConcurrentModificationException();
            //     return true;
            // }
            // return false;
        // }

        // public int characteristics() {
            // return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
        // }
    // }

    // DIFFBLUE MODEL LIBRARY
    // The % operator returns the remainder of the division operation, which may
    // be negative. This helper method returns i mod j (which is always in the
    // range [0, j)).
    private static int cproverPositiveModulo(int i, int j) {
        return (i % j + j) % j;
    }

    // DIFFBLUE MODEL LIBRARY
    // Given a start index and a number of steps to move to the right (to the
    // left if distance is negative), returns the index where we will end up,
    // assuming that the two ends of the array are connected.
    private int cproverGetShiftedIndex(int startIndex, int distance) {
        return cproverPositiveModulo(startIndex + distance, cproverData.length);
    }

    // DIFFBLUE MODEL LIBRARY
    // Returns the index of the last element in the list.
    private int cproverGetLastIndex() {
        return cproverGetShiftedIndex(cproverFirstIndex, size - 1);
    }

    // DIFFBLUE MODEL LIBRARY
    // This method is called by CBMC just after nondeterministic object
    // creation, i.e. the constraints that it specifies are only enforced at
    // that time and do not have to hold globally.
    // We generally want to make sure that all necessary invariants of the class
    // are satisfied, and potentially restrict some fields to speed up test
    // generation.
    @org.cprover.MustNotThrow
    protected void cproverNondetInitialize() {
        CProver.assume(size >= 0);
        CProver.assume(cproverData != null);
        CProver.assume(cproverData.length >= size);
        // Initial value chosen for the first element of the list. By setting
        // this value to 0, we know that in custom initialisers, we can call the
        // add method on the elements to be stored at indices 0, 1, 2, ...
        // size - 1.
        CProver.assume(cproverFirstIndex == 0);
        // Nondeterministic LinkedLists are created using a
        // call to the
        //     public LinkedList()
        // constructor, followed by a sequence of calls to add, where the number
        // of such calls is equal to size.
        // Each call to add increments the modCount variable by 1.
        CProver.assume(modCount == size);
    }

}
