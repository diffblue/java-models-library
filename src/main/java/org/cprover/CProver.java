package org.cprover;

public final class CProver
{
  public static boolean enableAssume=true;
  public static boolean enableNondet=true;
  public static boolean enableConcurrency=true;

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

  public static <T> T nondetWithNull()
  {
    if (enableNondet)
    {
      throw new RuntimeException(
          "Cannot execute program with CProver.nondetWithNull<T>(T)");
    }

    return null;
  }

  public static <T> T nondetWithoutNull()
  {
    if (enableNondet)
    {
      throw new RuntimeException(
          "Cannot execute program with CProver.nondetWithoutNull<T>(T)");
    }

    return null;
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

  public static void assume(boolean condition)
  {
    if(enableAssume && !condition)
    {
      throw new RuntimeException("CProver.assume() predicate is false");
    }
  }

  /**
   * This method is used by jbmc to indicate an atomic section which enforces
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
   * This method is used by jbmc to indicate the end of an atomic section
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
}
