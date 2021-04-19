package item3_20210419;

public class MyClass2 {
    public static final MyClass2 INSTANCE = new MyClass2();

    public MyClass2() {
    }

    public static MyClass2 getInstance() {
        return INSTANCE;
    }

    public void doSomething() {
        System.out.println("뭔가를 실행함2");
    }
}
