/*
 * Copyright (c) 1994, 2012, Oracle and/or its affiliates. All rights reserved.
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

import org.cprover.CProver;
import java.lang.NullPointerException;
import java.lang.IllegalMonitorStateException;

public class Object {

    // lock needed for synchronization in cbmc
    // used by monitorenter, monitorexit, wait, and notify
    // Not present in the original Object class
    public int cproverMonitorCount;

    public Object() {
      cproverMonitorCount = 0;
    }

    public final Class<?> getClass() {
      /*
       * MODELS LIBRARY {
       *   We make this call to Class.forName to ensure it is loaded
       *   by CBMC even with --lazy-methods on. We have to do this
       *   because the internal support for getClass use the model of
       *   Class.forName.
       * }
       */
      Class c = Class.forName("");
      return CProver.nondetWithoutNullForNotModelled();
    }

    public int hashCode() {
      return 0;
    }

    public boolean equals(Object obj) {
        return (this == obj);
    }

    protected Object clone() throws CloneNotSupportedException {
      throw new CloneNotSupportedException();
    }

    public String toString() {
        return getClass().getName() + "@" + Integer.toHexString(hashCode());
    }

    public final void notify()
    {
      // FIXME: the thread must own the lock when it calls notify
    }

    // See implementation of notify
    public final void notifyAll()
    {
      // FIXME: the thread must own the lock when it calls notifyAll
    }

    public final void wait(long timeout) throws InterruptedException {
      // FIXME: the thread must own the lock when it calls wait
      // FIXME: should only throw if the interrupted flag in Thread is enabled
      throw new InterruptedException();
    }

    public final void wait(long timeout, int nanos) throws InterruptedException {
        if (timeout < 0) {
            throw new IllegalArgumentException("timeout value is negative");
        }

        if (nanos < 0 || nanos > 999999) {
            throw new IllegalArgumentException(
                                "nanosecond timeout value out of range");
        }

        if (nanos > 0) {
            timeout++;
        }

        wait(timeout);
    }

    public final void wait() throws InterruptedException {
        wait(0);
    }

    protected void finalize() throws Throwable { }

    /**
     * This method is not present in the original Objecct class.
     * It will be called by JBMC when the monitor in this instance
     * is being acquired as a result of either the execution of a
     * monitorenter bytecode instruction or the call to a synchronized
     * method. It uses a counter to enable reentrance and an atomic section
     * to ensure multiple threads do not race in the access/modification of
     * the counter.
     */
    public static void monitorenter(Object object)
    {
      //FIXME: we shoud remove the call to this method from the call
      // stack appended to the thrown exception
      if (object == null)
          throw new NullPointerException();

      CProver.atomicBegin();
      // this assume blocks this execution path in JBMC and simulates
      // the thread having to wait because the monitor is not available
      CProver.assume(object.cproverMonitorCount == 0);
      object.cproverMonitorCount++;
      CProver.atomicEnd();
    }

    /**
     * This method is not present in the original Objecct class.
     * It will be called by JBMC when the monitor in this instance
     * is being released as a result of either the execution of a
     * monitorexit bytecode instruction or the return (normal or exceptional)
     * of a synchronized method. It decrements the cproverMonitorCount that
     * had been incremented in monitorenter().
     */
    public static void monitorexit(Object object)
    {
      //FIXME: we shoud remove the call to this method from the call
      // stack appended to the thrown exception
      // FIXME: Enabling these exceptions makes
      // jbmc-regression/synchronized-blocks/test_sync.desc
      // run into an infinite loop during symex
      // if (object == null)
      //   throw new NullPointerException();

      // if (object.cproverMonitorCount == 0)
      //   throw new IllegalMonitorStateException();
      CProver.atomicBegin();
      object.cproverMonitorCount--;
      CProver.atomicEnd();
    }

}
