package item46_20220109;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class StreamEx {
    public static void main(String[] args) {
        final String sentence = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";
        final String[] words = sentence.split(" ");
        final Map<String, Long> freq = new HashMap<>();
        Arrays.stream(words)
                .forEach(word -> freq.merge(word.toLowerCase(), 1L, Long::sum));
    }
}
