package item47_20220110;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class StreamIteratorEx {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("a", "b", "c");
        Stream<String> wordStream = words.stream();

//        아래 코드는 컴파일 에러가 발생한다.
//        for (String word : wordStream.iterator()) {
//            System.out.println(word);
//        }
        for (String word : (Iterable<String>) wordStream::iterator) {
            System.out.println(word);
        }

        for (String word : iterableOf(words.stream())) {
            System.out.println(word);
        }
    }

    public static <E> Iterable<E> iterableOf(Stream<E> stream) {
        return stream::iterator;
    }
}
