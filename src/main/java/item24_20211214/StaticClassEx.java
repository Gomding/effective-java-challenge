package item24_20211214;

import java.util.HashMap;

public class StaticClassEx {
    public static void main(String[] args) {
        Calculator.Operation plus = Calculator.Operation.PLUS;
        Calculator.Operation minus = Calculator.Operation.MINUS;
    }
}

class Calculator {

    public Calculator() {
    }

    public enum Operation { PLUS, MINUS, MULTIPLE, DIVIDED }
}