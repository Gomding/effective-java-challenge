package item39_20220103;

import java.util.ArrayList;
import java.util.List;

public class Sample4 {
    @ExceptionMyTest3(IndexOutOfBoundsException.class)
    @ExceptionMyTest3(NullPointerException.class)
    public static void doublyBad() {
        List<String> list = new ArrayList<>();

        // 자바 API 명세에 따르면 다음 메서드는 IndexOutOfBoundException이나
        // NullPointerException을 던질 수 있다.
        list.add(5, null);
    }
}
