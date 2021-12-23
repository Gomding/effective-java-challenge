package item32_20211223;

import java.util.ArrayList;
import java.util.List;

public class HeapPollutionEx {

    public static void main(String[] args) {
        List<String> strings = new ArrayList<>();
        strings.add("요소1");
        strings.add("요소2");

        Object obj = strings;

        List<Integer> numbers = (List<Integer>) obj;
        numbers.add(3);
        numbers.add(4);
    }
}
