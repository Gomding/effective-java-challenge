package item1_20210417;

public class StaticFactoryMethodEx2 {

    public static void main(String[] args) {

        MyClass myClass = MyClass.of("이름");
    }
}

class MyClass {

    private String name;

    private MyClass(final String name) {
        this.name = name;
    }

    public static MyClass of(String value) {
        somethingToDo();
        return new MyClass(value);
    }

    private static void somethingToDo() {
        System.out.println("정적 팩터리 메서드가 실행됐습니다.");
    }
}