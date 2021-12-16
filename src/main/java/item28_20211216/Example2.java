package item28_20211216;

import java.util.ArrayList;
import java.util.List;

public class Example2 {

    public static void main(String[] args) {
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            doSomething(strings);
        }
    }

    static void doSomething(List<String> ...strings) {
    }
}
