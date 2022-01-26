package item61_20220126;

import java.util.Comparator;

public class IntegerNewEx {
    public static void main(String[] args) {
        Comparator<Integer> naturalOrder =
                (i,j) -> (i < j) ? -1 : (i == j ? 0 : 1);

        Integer i = new Integer(42);
        Integer i2 = new Integer(42);

        System.out.println(naturalOrder.compare(i, i2));
    }
}
