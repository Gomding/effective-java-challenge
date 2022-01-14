package item51_20220114;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;

public class ConvenienceMethodExample {
}

// 편의 메서드의 극단적인 예시
class Anagrams {
    public static void main(String[] args) {
        String[] words = {"staple", "aplest", "abc", "wood", "doow", "wdoo"};
        anagramsOverMinGroupSize(words, 2);
    }

    public static void anagramsOverMinGroupSize(String[] words, int minGroupSize) {
        minGroupSizeFilterGroupingByWords(words, minGroupSize)
                .forEach(group -> System.out.println(group.size() + ": " + group));
    }

    private static Stream<List<String>> minGroupSizeFilterGroupingByWords(String[] words, int minGroupSize) {
        return groupingByWords(words).stream()
                .filter(group -> group.size() >= minGroupSize);
    }

    private static Collection<List<String>> groupingByWords(String[] words) {
        return Arrays.stream(words)
                .collect(groupingBy(word -> alphabetize(word)))
                .values();
    }

    private static String alphabetize(String s) {
        char[] chars = s.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }
}
