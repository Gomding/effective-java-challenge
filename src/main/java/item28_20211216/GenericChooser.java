package item28_20211216;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GenericChooser<T> {
    private final T[] choiceArray;

    public GenericChooser(Collection<T> choices) {
        this.choiceArray = (T[])choices.toArray();
    }

    public Object choose() {
        Random rnd = ThreadLocalRandom.current();
        return choiceArray[rnd.nextInt(choiceArray.length)];
    }
}
