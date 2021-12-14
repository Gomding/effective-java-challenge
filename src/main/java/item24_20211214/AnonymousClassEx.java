package item24_20211214;

public class AnonymousClassEx {
}

class RootClass2 {
    public void doSomething() {
        MyInterface anonymousClassInstance = new MyInterface() {
            @Override
            public void doSomething() {
                System.out.println("do something");
            }
        };
    }
}

interface MyInterface {
    void doSomething();
}