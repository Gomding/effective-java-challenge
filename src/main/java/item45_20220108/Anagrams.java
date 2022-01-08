package item45_20220108;

import java.util.*;

public class Anagrams {
    public static void main(String[] args) {
        String[] words = {"staple", "aplest", "abc", "wood", "doow", "wdoo"};
        anagramsOverMinGroupSize(words, 2);
    }

    public static void anagramsOverMinGroupSize(String[] words, int minGroupSize) {
        Map<String, Set<String>> groups = new HashMap<>();
        for (String word : words) {
            groups.computeIfAbsent(alphabetize(word), unused -> new TreeSet<>()).add(word);
        }

        for (Set<String> group : groups.values()) {
            if (group.size() >= minGroupSize)
                System.out.println(group.size() + ": " + group);
        }
    }

    private static String alphabetize(String s) {
        char[] chars = s.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }
}
