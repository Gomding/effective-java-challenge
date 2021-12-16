package item28_20211216;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GenericChooser2<T> {
    private final List<T> choiceList;

    public GenericChooser2(Collection<T> choices) {
        this.choiceList = new ArrayList<>(choices); // 방어적 복사
    }

    public Object choose() {
        Random rnd = ThreadLocalRandom.current();
        return choiceList.get(rnd.nextInt(choiceList.size()));
    }
}