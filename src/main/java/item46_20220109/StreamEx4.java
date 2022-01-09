package item46_20220109;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Comparator.*;
import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class StreamEx4 {
    public static void main(String[] args) {
        final String sentence = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";
        final String[] words = sentence.split(" ");
        Map<String, Long> freq = Arrays.stream(words)
                .collect(groupingBy(String::toLowerCase, counting()));
        List<String> collect = freq.keySet().stream()
                .sorted(comparing(freq::get).reversed())
                .limit(10)
                .collect(toList());
        for (String word : collect) {
            System.out.println(word);
        }
    }
}
