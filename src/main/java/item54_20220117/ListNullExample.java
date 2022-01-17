package item54_20220117;

import java.util.ArrayList;
import java.util.List;

public class ListNullExample {
    public static void main(String[] args) {
        Cheeses cheeses = new Cheeses(new ArrayList<>());
        List<Cheese> cheeseList = cheeses.getCheeses();

        if (cheeseList != null && cheeseList.contains(new Cheese("Mozza")))
            System.out.println("음");
    }
}

/**
 * @return 매장 안의 모든 치즈 목록을 반환한다.
 *          단, 재고가 하나도 없다면 null을 반환한다.
 */
class Cheeses {
    private final List<Cheese> values;

    public Cheeses(List<Cheese> values) {
        this.values = values;
    }

    public List<Cheese> getCheeses() {
        return values.isEmpty() ? null : new ArrayList<>(values);
    }
}

class Cheese {
    private final String name;

    public Cheese(String name) {
        this.name = name;
    }
}