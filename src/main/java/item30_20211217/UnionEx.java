package item30_20211217;

import java.util.HashSet;
import java.util.Set;

public class UnionEx {

    public static void main(String[] args) {

    }

    public static Set union(Set s1, Set s2) {
        Set result = new HashSet(s1);
        result.addAll(s2);
        return result;
    }
}
