package item4_20210420;

public class PrivateConstructorEx2 {
    public static void main(String[] args) {
        MyAbstractClass myAbstractClass = new MyChildClass();
    }
}

abstract class MyAbstractClass {

    void doSomething() {
        System.out.println("뭔가를 할겁니다.");
    }
}

class MyChildClass extends MyAbstractClass {

    @Override
    void doSomething() {
        System.out.println("자식이 뭔가를 합니다.");
    }
}
