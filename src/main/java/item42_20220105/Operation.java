package item42_20220105;

import java.util.function.DoubleBinaryOperator;

public enum Operation {
    PLUS("+", (x, y) -> x + y),
    MINUS("-", (x, y) -> x - y),
    TIMES("*", (x, y) -> x * y),
    DIVIDE("/", (x, y) -> x / y);

    private final String symbol;
    private final DoubleBinaryOperator op;

    Operation(String symbol, DoubleBinaryOperator op) {
        this.symbol = symbol;
        this.op = op;
    }

    // 상수가 뜻하는 연산을 수행한다.
    public double apply(double x, double y) {
        return op.applyAsDouble(x, y);
    }

    public String toString() {
        return this.symbol;
    }
}
