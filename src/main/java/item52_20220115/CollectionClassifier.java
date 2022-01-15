package item52_20220115;

import java.util.*;

public class CollectionClassifier {
    public static String classify(Set<?> s) {
        return "집합";
    }

    public static String classify(List<?> s) {
        return "리스트";
    }

    public static String classify(Collection<?> s) {
        return "그 외";
    }

    public static void main(String[] args) {
        Collection<?>[] collections = {
                new HashSet<String>(),
                new ArrayList<String>(),
                new HashMap<String, String>().values()
        };

        for (Collection<?> collection : collections) {
            System.out.println(classify(collection));
        }
    }
}
