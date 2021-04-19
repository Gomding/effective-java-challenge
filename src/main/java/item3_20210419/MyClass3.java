package item3_20210419;

public class MyClass3 {
    private static final MyClass3 INSTANCE = new MyClass3();

    public MyClass3() {
    }

    public static MyClass3 getInstance() {
        return INSTANCE;
    }

    // 싱글턴임을 보장해주는 readResolve 메서드
    private Object readResolve() {
        return INSTANCE;
    }
}
