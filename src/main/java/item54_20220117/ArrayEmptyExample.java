package item54_20220117;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArrayEmptyExample {
}

class Cheeses4 {
    private static final Cheese4[] EMPTY_CHEESE_ARRAY = new Cheese4[0];

    private final List<Cheese4> values;

    public Cheeses4(List<Cheese4> values) {
        this.values = values;
    }

    public Cheese4[] getCheeses() {
        return values.toArray(EMPTY_CHEESE_ARRAY);
    }
}

class Cheese4 {
    private final String name;

    public Cheese4(String name) {
        this.name = name;
    }
}