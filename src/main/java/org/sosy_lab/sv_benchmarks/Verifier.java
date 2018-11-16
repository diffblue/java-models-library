/*
 * Contributed by Peter Schrammel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sosy_lab.sv_benchmarks;

import org.cprover.CProver;

public final class Verifier
{
  public static void assume(boolean condition)
  {
    CProver.assume(condition);
  }

  public static boolean nondetBoolean()
  {
    return CProver.nondetBoolean();
  }

  public static byte nondetByte()
  {
    return CProver.nondetByte();
  }

  public static char nondetChar()
  {
    return CProver.nondetChar();
  }

  public static short nondetShort()
  {
    return CProver.nondetShort();
  }

  public static int nondetInt()
  {
    return CProver.nondetInt();
  }

  public static long nondetLong()
  {
    return CProver.nondetLong();
  }

  public static float nondetFloat()
  {
    return CProver.nondetFloat();
  }

  public static double nondetDouble()
  {
    return CProver.nondetDouble();
  }

  public static String nondetString()
  {
    return CProver.nondetWithoutNull("");
  }
}
