import java.util.LinkedList;
public class LinkedListProbes {
  public static void addGet_t() {
    LinkedList<Integer> l = new LinkedList<>();
    l.add(5); l.add(6);
    assert l.get(0) == 5 && l.get(1) == 6 && l.size() == 2;
  }
  public static void firstLast_t() {
    LinkedList<Integer> l = new LinkedList<>();
    l.addFirst(2); l.addLast(3); l.addFirst(1);
    assert l.getFirst() == 1 && l.getLast() == 3 && l.size() == 3;
  }
  public static void removeFirst_t() {
    LinkedList<Integer> l = new LinkedList<>();
    l.add(5); l.add(6);
    assert l.removeFirst() == 5 && l.size() == 1 && l.getFirst() == 6;
  }
  public static void iterSum_f() {
    LinkedList<Integer> l = new LinkedList<>();
    l.add(3); l.add(4);
    int sum = 0;
    for (int x : l) sum += x;
    assert sum == 8;
  }
  public static void main(String[] a) {
    addGet_t(); firstLast_t(); removeFirst_t();
    try { iterSum_f(); System.out.println("BAD: _f passed"); }
    catch (AssertionError e) { System.out.println("JVM: all as expected"); }
  }
}
