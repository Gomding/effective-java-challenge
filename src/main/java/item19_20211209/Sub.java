package item19_20211209;

public final class Sub extends Super {
    // 초기화되지 않은 final 필드
    private final Object object;

    public Sub() {
        // super() 먼저 실행됨
        this.object = new Object();
    }

    @Override
    public void overrideMe() {
        System.out.println(object);
    }

    public static void main(String[] args) {
        Sub sub = new Sub();
        sub.overrideMe();
    }
}
