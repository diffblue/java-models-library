import java.util.HashSet;
public class SetProbes {
  public static void addContains_t() {
    HashSet<Integer> s = new HashSet<>();
    assert s.add(1) && s.add(2) && !s.add(1);   // duplicate add returns false
    assert s.contains(1) && s.contains(2) && !s.contains(3);
    assert s.size() == 2 && !s.isEmpty();
  }
  public static void removeClear_t() {
    HashSet<Integer> s = new HashSet<>();
    s.add(1); s.add(2);
    assert s.remove(1) && !s.remove(7);
    assert s.size() == 1;
    s.clear();
    assert s.isEmpty();
  }
  public static void nullElem_t() {
    HashSet<Integer> s = new HashSet<>();
    assert s.add(null) && s.contains(null) && !s.add(null);
  }
  public static void iterSum_t() {
    HashSet<Integer> s = new HashSet<>();
    s.add(3); s.add(4);
    int sum = 0;
    for (int x : s) sum += x;
    assert sum == 7;
  }
  public static void wrongSize_f() {
    HashSet<Integer> s = new HashSet<>();
    s.add(1); s.add(1);
    assert s.size() == 2;
  }
  public static void main(String[] a) {
    addContains_t(); removeClear_t(); nullElem_t(); iterSum_t();
    try { wrongSize_f(); System.out.println("JVM: wrongSize_f did NOT fail (BAD)"); }
    catch (AssertionError e) { System.out.println("JVM: all probes behave as expected"); }
  }
}
