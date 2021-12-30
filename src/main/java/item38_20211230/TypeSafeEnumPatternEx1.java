package item38_20211230;

public class TypeSafeEnumPatternEx1 {
    public static void main(String[] args) {

    }
}

class Operation {
    public static final Operation PLUS = new Operation("+") {
        public double apply(double x, double y) { return x + y; }
    };
    public static final Operation MINUS = new Operation("-") {
        public double apply(double x, double y) { return x - y; }
    };
    public static final Operation TIMES = new Operation("*") {
        public double apply(double x, double y) { return x * y; }
    };
    public static final Operation DIVIDE = new Operation("/") {
        public double apply(double x, double y) { return x / y; }
    };


    private final String symbol;

    public Operation(String symbol) {
        this.symbol = symbol;
    }
}
