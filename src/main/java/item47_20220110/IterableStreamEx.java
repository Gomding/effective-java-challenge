package item47_20220110;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class IterableStreamEx {

    public static <E> Stream<E> streamOf(Iterable<E> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }
}
