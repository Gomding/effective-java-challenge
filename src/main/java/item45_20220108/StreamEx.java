package item45_20220108;

import java.util.Arrays;

public class StreamEx {
    public static void main(String[] args) {
        String[] words = {"5", "2", "3", "1", "4"};
        Arrays.stream(words)
                .map(Integer::valueOf)
                .sorted()
                .forEach(System.out::println);
    }
}
