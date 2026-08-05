import java.util.HashMap;
public class MapProbes {
  // _t = expect SUCCESSFUL, _f = expect FAILED
  public static void putGet_t() {
    HashMap<Integer,Integer> h = new HashMap<>();
    h.put(1, 10); h.put(2, 20);
    assert h.get(1) == 10 && h.get(2) == 20;
  }
  public static void overwrite_t() {
    HashMap<Integer,Integer> h = new HashMap<>();
    h.put(1, 10); h.put(1, 11);
    assert h.get(1) == 11 && h.size() == 1;
  }
  public static void nullKey_t() {
    HashMap<Integer,Integer> h = new HashMap<>();
    h.put(null, 42);
    assert h.get(null) == 42 && h.containsKey(null);
  }
  public static void nullValue_t() {
    HashMap<Integer,Integer> h = new HashMap<>();
    h.put(3, null);
    assert h.get(3) == null && h.containsKey(3);
  }
  public static void removeKey_t() {
    HashMap<Integer,Integer> h = new HashMap<>();
    h.put(1, 10); h.put(2, 20);
    h.remove(1);
    assert !h.containsKey(1) && h.size() == 1 && h.get(2) == 20;
  }
  public static void missingGet_t() {
    HashMap<Integer,Integer> h = new HashMap<>();
    h.put(1, 10);
    assert h.get(7) == null && !h.containsKey(7);
  }
  public static void wrongValue_f() {
    HashMap<Integer,Integer> h = new HashMap<>();
    h.put(1, 10);
    assert h.get(1) == 99;
  }
  public static void entryIter_t() {
    HashMap<Integer,Integer> h = new HashMap<>();
    h.put(1, 10); h.put(2, 20);
    int ksum = 0, vsum = 0;
    for (java.util.Map.Entry<Integer,Integer> e : h.entrySet()) {
      ksum += e.getKey(); vsum += e.getValue();
    }
    assert ksum == 3 && vsum == 30;
  }
}
