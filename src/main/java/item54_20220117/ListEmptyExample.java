package item54_20220117;

import java.util.ArrayList;
import java.util.List;

public class ListEmptyExample {
}

class Cheeses2 {
    private final List<Cheese> values;

    public Cheeses2(List<Cheese> values) {
        this.values = values;
    }

    public List<Cheese> getCheeses() {
        return new ArrayList<>(values);
    }
}

class Cheese2 {
    private final String name;

    public Cheese2(String name) {
        this.name = name;
    }
}