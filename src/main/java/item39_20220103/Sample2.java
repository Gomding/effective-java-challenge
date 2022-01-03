package item39_20220103;

public class Sample2 {
    @ExceptionMyTest(ArithmeticException.class)
    public static void m1() {    // 성공해야 한다.
        int i = 0;
        i = i / i;
    }

    @ExceptionMyTest(ArithmeticException.class)
    public static void m2() {    // 실패해야 한다. (다른 예외 발생)
        int[] a = new int[0];
        int b = a[1];
    }

    @ExceptionMyTest(ArrayIndexOutOfBoundsException.class)
    public static void m3() {    // 성공해야 한다.
        int[] a = new int[0];
        int b = a[1];
    }

    @ExceptionMyTest(ArithmeticException.class)
    public static void m4() { }  // 실패해야 한다. (예외가 발생하지 않음)
}
