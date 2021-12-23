package item32_20211223;

import java.util.Arrays;
import java.util.List;

public class HeapPollutionEx2 {
    public static void main(String[] args) {
        List<String> strings = Arrays.asList("첫 요소");
        doSomthing(strings);
    }

    private static void doSomthing(List<String> ... stringLists) {
        List<Integer> intList = Arrays.asList(42);
        Object[] objects = stringLists;
        objects[0] = intList; // 힙 오염 발생
//        String s = stringLists[0].get(0); // ClassCastException

    }
}
