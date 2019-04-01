package org.cprover;

import java.io.BufferedInputStream;
import java.io.PrintStream;

public final class CProver
{
  public static final boolean enableAssume=true;
  public static final boolean enableNondet=true;
  public static final boolean enableConcurrency=true;

  public static boolean nondetBoolean()
  {
    if (enableNondet)
    {
      throw new RuntimeException(
          "Cannot execute program with CProver.nondetBoolean()");
    }

    return false;
  }

  public static byte nondetByte()
  {
    if (enableNondet)
    {
      throw new RuntimeException(
          "Cannot execute program with CProver.nondetByte()");
    }

    return 0;
  }

  public static char nondetChar()
  {
    if (enableNondet)
    {
      throw new RuntimeException(
          "Cannot execute program with CProver.nondetChar()");
    }

    return '\0';
  }

  public static short nondetShort()
  {
    if (enableNondet)
    {
      throw new RuntimeException(
          "Cannot execute program with CProver.nondetShort()");
    }

    return 0;
  }

  public static int nondetInt()
  {
    if (enableNondet)
    {
      throw new RuntimeException(
          "Cannot execute program with CProver.nondetInt()");
    }

    return 0;
  }

  public static long nondetLong()
  {
    if (enableNondet)
    {
      throw new RuntimeException(
          "Cannot execute program with CProver.nondetLong()");
    }

    return 0;
  }

  public static float nondetFloat()
  {
    if (enableNondet)
    {
      throw new RuntimeException(
          "Cannot execute program with CProver.nondetFloat()");
    }

    return 0;
  }

  public static double nondetDouble()
  {
    if (enableNondet)
    {
      throw new RuntimeException(
          "Cannot execute program with CProver.nondetDouble()");
    }

    return 0;
  }

  private static <T> T nondetWithNull()
  {
    if (enableNondet)
    {
      throw new RuntimeException(
          "Cannot execute program with CProver.nondetWithNull<T>(T)");
    }

    return null;
  }

  /**
   *
   * @param instance an instance of the type T, this is not used but is there
   *                 to make sure the class T is loaded. The parameter should
   *                 not be `null`.
   * @param <T> class of the object to return
   * @return a non-deterministic object of type T, possibly `null`.
   */
  public static <T> T nondetWithNull(T instance)
  {
    return nondetWithNull();
  }

  private static <T> T nondetWithoutNull()
  {
    T t = nondetWithNull();
    assume(t != null);
    return t;
  }

  /**
   *
   * @param instance an instance of the type T, this is not used but is there
   *                 to make sure the class T is loaded. The parameter should
   *                 not be `null`.
   * @param <T> class of the object to return
   * @return a non-deterministic object of type T, assumed to be non-null.
   */
  public static <T> T nondetWithoutNull(T instance)
  {
    return nondetWithoutNull();
  }

  public static <T> T nondetWithNullForNotModelled() {
    return nondetWithNull();
  }

  public static <T> T nondetWithoutNullForNotModelled() {
    return nondetWithoutNull();
  }

  /**
   * Return a non-deterministic PrintStream.
   * It is not recommended to use it, since it will not enforce that PrintStream
   * is loaded, but is necessary for initializing System.out and System.err.
   */
  public static PrintStream nondetPrintStream()
  {
    return nondetWithoutNull();
  }

  /**
   * Return a non-deterministic BufferedInputStream.
   * It is not recommended to use it, since it will not enforce that
   * BufferedInputStream is loaded, but is necessary for initializing System.in.
   */
  public static BufferedInputStream nondetBufferedInputStream()
  {
    return nondetWithoutNull();
  }

  public static void assume(boolean condition)
  {
    if(enableAssume)
    {
      throw new RuntimeException(
          "Cannot execute program with CProver.assume()");
    }
  }

  /**
   * This method is used by JBMC to detect the start of a new thread and
   * create a multithreaded bmc equation.
   * Refer to the method `start` in the model for the class `java.lang.Thread`
   * in the JBMC sources to see an example of usage
   */
  public static void startThread(int id)
  {
    if(enableConcurrency)
    {
      throw new RuntimeException(
          "Cannot execute program with CProver.startThread()");
    }
  }

  /**
   * This method is used by JBMC to detect the end of a new thread to
   * manage a multithreaded bmc equation
   * Refer to the method `start` in the model for the class `java.lang.Thread`
   * in the JBMC sources to see an example of usage
   */
  public static void endThread(int id)
  {
    if(enableConcurrency)
    {
      throw new RuntimeException(
          "Cannot execute program with CProver.endThread()");
    }
  }

  /**
   * This method is used by JBMC to return the ID of the executing thread.
   */
  public static int getCurrentThreadID()
  {
    if(enableConcurrency)
    {
      throw new RuntimeException(
          "Cannot execute program with CProver.getCurrentThreadID()");
    }
    return 0;
  }

  /**
   * This method is used by JBMC to indicate an atomic section which enforces
   * the bmc equation to avoid interleavings of the code inside the section
   */
  public static void atomicBegin()
  {
    if(enableConcurrency)
    {
      throw new RuntimeException(
          "Cannot execute program with CProver.atomicBegin()");
    }
  }

  /**
   * This method is used by JBMC to indicate the end of an atomic section
   * (see atomicBegin).
   */
  public static void atomicEnd()
  {
    if(enableConcurrency)
    {
      throw new RuntimeException(
          "Cannot execute program with CProver.atomicEnd()");
    }
  }

  /**
   * If this method is found in the test-gen trace for a particular trace,
   * that test will be discarded with a message explaining that the test
   * calls a non-modelled method.
   * This allows us to copy full classes from the JDK, and insert this function
   * call into non-modelled methods. We can then model a handful of the
   * methods on the class, ensuring that tests will only be generated for
   * 'known-good' models.
   */
  public static void notModelled()
  {
    assume(false);
  }

  /**
   * Array copy for byte arrays. Does not check for exceptions.
   * Use instead of System.arraycopy when the bounds are ensured to be
   * respected, that is, the following should be false:
   * srcPos < 0 || destPos < 0 || length < 0 ||
   * srcPos + length > src.length || destPos + length > dest.length
   *
   * @param      src      the source array.
   * @param      srcPos   starting position in the source array.
   * @param      dest     the destination array.
   * @param      destPos  starting position in the destination data.
   * @param      length   the number of array elements to be copied.
    */
  public static void arraycopy(byte[] src, int srcPos, byte[] dest,
                               int destPos, int length)  {
      byte[] temp = new byte[length];
      for (int i = 0; i < length; i++) {
	  temp[i] = src[srcPos + i];
      }
      for (int i = 0; i < length; i++) {
	  dest[destPos + i] = temp[i];
      }
  }

  /**
   * Array copy for byte arrays. Does not check for exceptions,
   * and assumes that `src` and `dest`.
   * Use instead of System.arraycopy when `src` and `dest` are guaranteed to be
   * different and the bounds are ensured to be
   * respected, that is, the following should be false:
   * src == dest || srcPos < 0 || destPos < 0 || length < 0 ||
   * srcPos + length > src.length || destPos + length > dest.length
   *
   * @param      src      the source array.
   * @param      srcPos   starting position in the source array.
   * @param      dest     the destination array.
   * @param      destPos  starting position in the destination data.
   * @param      length   the number of array elements to be copied.
    */
  public static void arraycopyInPlace(byte[] src, int srcPos, byte[] dest,
                               int destPos, int length)  {
      for (int i = 0; i < length; i++) {
          dest[destPos + i] = src[srcPos + i];
      }
  }

  /**
   *  Retrieves the current locking count for 'object'.
   */
  public static int getMonitorCount(Object object)
  {
    return object.cproverMonitorCount;
  }
}
