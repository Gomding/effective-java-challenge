package item49_20220112;

import java.math.BigInteger;

public class PositiveNumberCalculator {
    // 매개변수로 양수만을 받는 더하기 메서드다.
    public int sum(int a, int b) {
        if (!isPositiveNumber(a) || !isPositiveNumber(b)) {
            throw new IllegalArgumentException("a 와 b는 1이상의 양수여야 한다.");
        }
        return a + b;
    }

    private boolean isPositiveNumber(int number) {
        return number >= 1;
    }
}
