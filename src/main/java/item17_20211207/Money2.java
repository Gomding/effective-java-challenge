package item17_20211207;

public class Money2 {

    private final int value;

    private Money2(int value) {
        this.value = value;
    }

    public static Money2 valueOf(int value) {
        return new Money2(value);
    }
}
/*
공개된 생성자가 없으므로 상속해도 사용할 수 없다.
class Won extends Money2 {
}
*/
