package item34_20211225;

public enum Operation2 {
    PLUS { public double apply(double x, double y) {return x + y;} },
    MINUS { public double apply(double x, double y) {return x + y;} },
    TIMES { public double apply(double x, double y) {return x + y;} },
    DIVIDE { public double apply(double x, double y) {return x + y;} };

    // 상수가 뜻하는 연산을 수행한다.
    public abstract double apply(double x, double y);
}
