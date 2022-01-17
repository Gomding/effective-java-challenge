package item54_20220117;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReturnEmptyListExample {
}

class Cheeses3 {
    private final List<Cheese> values;

    public Cheeses3(List<Cheese> values) {
        this.values = values;
    }

    public List<Cheese> getCheeses() {
        return values.isEmpty() ? Collections.emptyList() : new ArrayList<>(values);
    }
}

class Cheese3 {
    private final String name;

    public Cheese3(String name) {
        this.name = name;
    }
}