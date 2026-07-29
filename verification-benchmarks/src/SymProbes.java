import java.util.HashMap;
public class SymProbes {
  // Nondet map parameter: sizes/content unknown. size()>=0 must hold (t);
  // and a get after put must return the put value (t).
  public static void sizeNonNeg_t(HashMap<Integer,Integer> h) {
    if (h != null) { assert h.size() >= -1_000_000_000 ? h.size() >= 0 : false; }
  }
  public static void putThenGet_t(HashMap<Integer,Integer> h) {
    if (h != null) {
      h.put(5, 50);
      Integer v = h.get(5);
      assert v != null && v == 50;
    }
  }
}
