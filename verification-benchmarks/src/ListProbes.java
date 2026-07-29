import java.util.ArrayList;
public class ListProbes {
  public static void addGet_t() {
    ArrayList<Integer> a = new ArrayList<>();
    a.add(5); a.add(6);
    assert a.get(0) == 5 && a.get(1) == 6 && a.size() == 2;
  }
  public static void setIdx_t() {
    ArrayList<Integer> a = new ArrayList<>();
    a.add(5); a.set(0, 9);
    assert a.get(0) == 9;
  }
  public static void containsIndexOf_t() {
    ArrayList<Integer> a = new ArrayList<>();
    a.add(5); a.add(6); a.add(5);
    assert a.contains(6) && a.indexOf(5) == 0 && a.lastIndexOf(5) == 2;
  }
  public static void removeIdx_t() {
    ArrayList<Integer> a = new ArrayList<>();
    a.add(5); a.add(6); a.add(7);
    a.remove(1);
    assert a.size() == 2 && a.get(1) == 7;
  }
  public static void grow_t() {
    ArrayList<Integer> a = new ArrayList<>();
    for (int i = 0; i < 12; i++) a.add(i);   // beyond default capacity 10
    assert a.size() == 12 && a.get(11) == 11;
  }
  public static void iterSum_f() {
    ArrayList<Integer> a = new ArrayList<>();
    a.add(3); a.add(4);
    int sum = 0;
    for (int x : a) sum += x;
    assert sum == 8;
  }
}
