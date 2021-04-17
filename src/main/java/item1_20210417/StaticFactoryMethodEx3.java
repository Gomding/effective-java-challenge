package item1_20210417;

public class StaticFactoryMethodEx3 {

    public static void main(String[] args) {
        Person person = Person.getInstance();
        System.out.println(person.name());
    }
}

class Person {
    private static final Person INSTANCE = new Person("항상 있어야하는 사람");

    private String name;

    private Person(String name) {
        this.name = name;
    }

    public static Person getInstance() {
        return INSTANCE;
    }

    public String name() {
        return this.name;
    }
}
