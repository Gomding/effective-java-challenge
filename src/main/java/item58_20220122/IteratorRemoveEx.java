package item58_20220122;

import java.util.Arrays;
import java.util.List;

public class IteratorRemoveEx {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1,2,3,4,5,6);

        for (Integer number : numbers) {
            numbers.remove(number + 1);
        }
    }
}
