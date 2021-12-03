package item13_20211203;

public class CloneExample3 {
}

class Stack {

    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    private Object[] elements;
    private int size = 0;

    public Stack() {
        this.elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }
}
