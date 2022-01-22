package item58_20220122;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CommonForStatement {

    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1,2,3,4,5);

        int sum = 0;
        for (Iterator<Integer> i = numbers.iterator(); i.hasNext();) {
            Integer num = i.next();
            sum += num;
        }

        int[] numberArray = {1,2,3,4,5};

        int sum2 = 0;
        for (int i = 0; i < numberArray.length; i++) {
            sum2 += i;
        }
    }
}
