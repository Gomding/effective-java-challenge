package item45_20220108;

import java.util.*;

import static java.util.stream.Collectors.groupingBy;

public class AnagramsStream {
    public static void main(String[] args) {
        String[] words = {"staple", "aplest", "abc", "wood", "doow", "wdoo"};
        anagramsOverMinGroupSize(words, 2);
    }

    public static void anagramsOverMinGroupSize(String[] words, int minGroupSize) {
        Arrays.stream(words)
                .collect(
                        groupingBy(word -> word.chars().sorted()
                                .collect(StringBuilder::new,
                                        (sb, aChar) -> sb.append((char)aChar),
                                        StringBuilder::append).toString()))
                .values().stream()
                .filter(group -> group.size() >= minGroupSize)
                .forEach(System.out::println);
    }
}
