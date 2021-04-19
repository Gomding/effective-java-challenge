package item3_20210419;

public class MyClass {
    public static final MyClass INSTANCE = new MyClass();

    private MyClass() {
    }

    public void doSomething() {
        System.out.println("뭔가를 실행");
    }
}
