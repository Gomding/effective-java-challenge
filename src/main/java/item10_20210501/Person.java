package item10_20210501;

import java.util.Objects;

public class Person {

    private final String name;
    private final String gender;
    private final int age;

    public Person(String name, String gender, int age) {
        this.name = name;
        this.gender = gender;
        this.age = age;
    }

    // 인텔리제이 interface를 활용해서 만든 equals 메서드
    // 같은 객체면 true
    // 비교대상이 null 이거나 클래스가 같지않으면 false
    // 필드값에 대해서 값이 같은지 검사
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return age == person.age && Objects.equals(name, person.name) && Objects.equals(gender, person.gender);
    }

    // 인텔리제이 interface를 활용해서 만든 hashCode 메서드
    @Override
    public int hashCode() {
        return Objects.hash(name, gender, age);
    }
}
