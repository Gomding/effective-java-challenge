package item57_20220121;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class WhileExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1,2,3,4,5);

        Iterator<Integer> i = numbers.iterator();
        while (i.hasNext()) {
            System.out.println(i.next());
        }

        Iterator<Integer> i2 = numbers.iterator();
        while (i.hasNext()) {
            System.out.println(i.next());
        }
    }
}
