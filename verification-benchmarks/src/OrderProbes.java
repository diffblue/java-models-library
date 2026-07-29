import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
public class OrderProbes {
  public static void insertionOrder_t() {
    LinkedHashMap<Integer,Integer> m = new LinkedHashMap<>();
    m.put(3, 30); m.put(1, 10); m.put(2, 20);
    int[] keys = new int[3]; int i = 0;
    for (int k : m.keySet()) keys[i++] = k;
    assert keys[0] == 3 && keys[1] == 1 && keys[2] == 2;
  }
  public static void reinsertKeepsOrder_t() {
    LinkedHashMap<Integer,Integer> m = new LinkedHashMap<>();
    m.put(3, 30); m.put(1, 10);
    m.put(3, 31);                        // re-put: order unchanged, value updated
    int first = m.keySet().iterator().next();
    assert first == 3 && m.get(3) == 31;
  }
  public static void accessOrder_t() {
    LinkedHashMap<Integer,Integer> m = new LinkedHashMap<>(16, 0.75f, true);
    m.put(1, 10); m.put(2, 20); m.put(3, 30);
    m.get(1);                            // access moves 1 to the end
    int first = m.keySet().iterator().next();
    assert first == 2;
  }
  public static void entryOrder_t() {
    LinkedHashMap<Integer,Integer> m = new LinkedHashMap<>();
    m.put(5, 50); m.put(4, 40);
    int i = 0; int[] ks = new int[2]; int[] vs = new int[2];
    for (Map.Entry<Integer,Integer> e : m.entrySet()) { ks[i] = e.getKey(); vs[i] = e.getValue(); i++; }
    assert ks[0] == 5 && vs[0] == 50 && ks[1] == 4 && vs[1] == 40;
  }
  public static void removeReorders_f() {
    LinkedHashMap<Integer,Integer> m = new LinkedHashMap<>();
    m.put(1, 10); m.put(2, 20);
    m.remove(1);
    int first = m.keySet().iterator().next();
    assert first == 1;                   // FALSE: 1 was removed
  }
  public static void setOrder_t() {
    LinkedHashSet<Integer> s = new LinkedHashSet<>();
    s.add(9); s.add(7); s.add(8); s.add(9);   // duplicate keeps position
    int[] es = new int[3]; int i = 0;
    for (int e : s) es[i++] = e;
    assert es[0] == 9 && es[1] == 7 && es[2] == 8;
  }
  public static void main(String[] a) {
    insertionOrder_t(); reinsertKeepsOrder_t(); accessOrder_t(); entryOrder_t(); setOrder_t();
    try { removeReorders_f(); System.out.println("BAD"); }
    catch (AssertionError e) { System.out.println("JVM: all as expected"); }
  }
}
