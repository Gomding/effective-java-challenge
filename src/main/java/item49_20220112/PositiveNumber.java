package item49_20220112;

public class PositiveNumber {
    private final int value;

    public PositiveNumber(int value) {
        if (value < 1) {
            throw new IllegalArgumentException("양수의 값은 1보다 커야합니다.");
        }
        this.value = value;
    }
}
