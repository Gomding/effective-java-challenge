package item51_20220114;

public class MethodNameExample {

}

class Car {
    private int position;

    public void move() {
        position++;
    }

    public int getPosition() {
        return position;
    }
}

class User {
    private final String name;

    public User(String name) {
        this.name = name;
    }

    // Car 클래스에서는 접근자 메서드 이름에 get필드명() 을 사용했지만 여기서는 필드명()을 사용했다.
    // 이것은 메서드 이름이 통일되지 않은 경우로 클라이언트에게 혼란을 줄 수 있다.
    public String name() {
        return name;
    }
}
