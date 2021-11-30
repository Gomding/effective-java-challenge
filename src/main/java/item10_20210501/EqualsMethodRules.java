package item10_20210501;

public class EqualsMethodRules {

    public static void main(String[] args) {
        reflexivity();
        System.out.println("===========================");
        symmetry();
        System.out.println("===========================");
        transitivity();
        System.out.println("===========================");
        consistency();
        System.out.println("===========================");
        isNotNull();
    }

    // 반사성 : null이 아닌 모든 참조 값 x에 대해 x.equals(x)는 true다.
    public static void reflexivity() {
        Object x = new Object();
        boolean result1 = x.equals(x);
        System.out.println("반사성 : x.equals(x) 는 " + result1);
    }

    // 대칭성 : null이 아닌 모든 참조 값 x, y에 대해 x.equals(y)가 true면 y.equals(x)도 true다
    public static void symmetry() {
        Object x = new Object();
        Object y = x;
        boolean result1 = x.equals(y);
        boolean result2 = y.equals(x);

        System.out.println("대칭성 : x.equals(y) 는 " + result1);
        System.out.println("대칭성 : y.equals(x) 는 " + result2);
        System.out.println("result1 == result2 는 " + (result1 == result2));
    }

    // 추이성 : null이 아닌 모든 참조 값 x, y, z에 대해
    // x.equals(y)가 true 이고 y.equals(z)도 true 면 x.equals(z)도 true 다
    public static void transitivity() {
        Object x = new Object();
        Object y = x;
        Object z = y;
        boolean result1 = x.equals(y);
        boolean result2 = y.equals(z);
        boolean result3 = x.equals(z);

        System.out.println("추이성 : x.equals(y) 는 " + result1);
        System.out.println("추이성 : y.equals(z) 는 " + result2);
        System.out.println("추이성 : x.equals(z) 는 " + result3);
    }

    // 일관성 : null이 아닌 모든 참조 값 x, y에 대해, x.equals(y)를 반복해서 호출하면 항상 true를 반환하거나 항상 false를 반환한다.
    public static void consistency() {
        System.out.println("=== x와 y가 같을때 ===");
        Object x = new Object();
        Object y = x;
        boolean result1 = x.equals(y);

        for (int i = 0; i < 4; i++) {
            System.out.println("일관성 : x.equals(y) 는 " + result1);
        }

        System.out.println("=== x와 y가 더는 같지 않을때 ===");

        y = new Object();
        boolean result2 = x.equals(y);

        for (int i = 0; i < 4; i++) {
            System.out.println("일관성 : x.equals(y) 는 " + result2);
        }
    }

    // null-아님 : null이 아닌 모든 참조 값 x에 대해 x.equals(null)은 false 다.
    public static void isNotNull() {
        Object x = new Object();
        boolean result1 = x.equals(null);

        System.out.println("null-아님 : x.equals(null) 은 " + result1);
    }
}
