package item26_20211215;

import java.util.ArrayList;
import java.util.List;

public class GenericEx3 {
    public static void main(String[] args) {
        List<String> strings = new ArrayList<>();
//        unsafeAdd(strings, Integer.valueOf(42)); 컴파일 오류!! List<Object> 타입에 List<String>
//        String s = strings.get(0);
    }

    private static void unsafeAdd(List<Object> list, Object o) {
        list.add(o);
    }
}
