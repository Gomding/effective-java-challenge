package item56_20220119;

import java.util.*;

public class OptionalEx {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("1", "2");
        String lastWordInLexicon = max(words).orElse("단어 없음...");

        String lastWordInLexicon2 = max(words).orElse(anyWord());

        String word = max(words).orElseThrow(() -> new IllegalArgumentException("단어가 없다."));

        String word2 = max(words).get();
    }

    private static String anyWord() {
        System.out.println("anyWord 메서드 호출");
        return "아무 단어나 반환";
    }

    public static <E extends Comparable<E>> Optional<E> max(Collection<E> c) {
        if (c.isEmpty())
            return Optional.empty();

        E result = null;
        for (E e : c) {
            if (result == null || e.compareTo(result) > 0) {
                result = Objects.requireNonNull(e);
            }
        }

        return Optional.of(result);
    }
}
