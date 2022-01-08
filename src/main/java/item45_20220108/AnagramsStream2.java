package item45_20220108;

import java.util.*;

import static java.util.stream.Collectors.groupingBy;

public class AnagramsStream2 {
    public static void main(String[] args) {
        String[] words = {"staple", "aplest", "abc", "wood", "doow", "wdoo"};
        anagramsOverMinGroupSize(words, 2);
    }

    public static void anagramsOverMinGroupSize(String[] words, int minGroupSize) {
        Arrays.stream(words)
                .collect(groupingBy(word -> alphabetize(word)))
                .values().stream()
                .filter(group -> group.size() >= minGroupSize)
                .forEach(group -> System.out.println(group.size() + ": " + group));
    }

    private static String alphabetize(String s) {
        char[] chars = s.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }
}
