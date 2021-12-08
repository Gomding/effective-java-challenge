package item18_20211208;

import java.util.Arrays;

public class ExtendsExample {

    public static void main(String[] args) {
        InstrumentedHashSet<String> set = new InstrumentedHashSet<>();
        set.addAll(Arrays.asList(
                "틱", "틱틱", "틱틱틱"
        ));

        System.out.printf("set의 addCount 값은 :  %d", set.getAddCount());
    }
}
