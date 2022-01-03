package item39_20220103;

public class Sample {
    @MyTest
    public static void m1() {} // 성공해야 한다.

    public static void m2() {}

    @MyTest
    public static void m3() {  // 실패해야 한다.
        throw new RuntimeException("실패");
    }

    public static void m4() {}

    @MyTest
    public void m5() {}        // 잘못 사용한 예 : 정적 메서드가 아닌 인스턴스 메서드다.

    public static void m6() {}

    @MyTest
    public static void m7() {  // 실패해야 한다.
        throw new RuntimeException("실패");
    }

    public static void m8() {}
}
