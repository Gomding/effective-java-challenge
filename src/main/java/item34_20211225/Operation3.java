package item34_20211225;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public enum Operation3 {
    PLUS("+") { public double apply(double x, double y) {return x + y;} },
    MINUS("-") { public double apply(double x, double y) {return x + y;} },
    TIMES("*") { public double apply(double x, double y) {return x + y;} },
    DIVIDE("/") { public double apply(double x, double y) {return x + y;} };

    private final String symbol;

    private static final Map<String, Operation3> stringToEnum =
            Stream.of(values()).collect(Collectors.toMap(Object::toString, e -> e));

    Operation3(String symbol) {
        this.symbol = symbol;
    }

    // 상수가 뜻하는 연산을 수행한다.
    public abstract double apply(double x, double y);

    public String toString() {
        return this.symbol;
    }
}
