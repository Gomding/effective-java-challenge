package item26_20211215;

import java.util.HashSet;
import java.util.Set;

public class WildCardEx2 {

    public static void main(String[] args) {
        Set<Object> s1 = new HashSet<>();
        s1.add(new Object());
        s1.add(100);
        Set<String> s2 = new HashSet<>();
        s2.add("String 타입의 요소");
        s2.add("100");
        int result = numElementsInCommon(s1, s2);
        System.out.println(result);
    }

    static int numElementsInCommon(Set<?> s1, Set<?> s2) {
        s1.add(null);
        int result = 0;
        for (Object o1 : s1)
            if (s2.contains(o1))
                result++;
        return result;
    }
}
