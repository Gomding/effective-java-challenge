package item30_20211217;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UnionGenericEx {

    public static void main(String[] args) {
        Set<String> guys = new HashSet<>(Arrays.asList("톰", "딕", "해리"));
        Set<String> stooges = new HashSet<>(Arrays.asList("래리", "모에", "컬리"));
        Set<String> aflCio = union(guys, stooges);
        System.out.println(aflCio);
        Collections.reverseOrder();
    }

    public static <E> Set<E> union(Set<E> s1, Set<E> s2) {
        Set<E> result = new HashSet<>(s1);
        result.addAll(s2);
        return result;
    }
}
