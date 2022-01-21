package item57_20220121;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ForExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1,2,3,4,5);

        for (Integer number : numbers) {
            //... number로 무언가 한다.
        }

        for (Iterator<Integer> i = numbers.iterator(); i.hasNext();) {
            Integer number = i.next();
            // number 와 i로 무언가 한다.
        }
    }
}
