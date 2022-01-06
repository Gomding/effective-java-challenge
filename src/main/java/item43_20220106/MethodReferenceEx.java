package item43_20220106;

import java.util.HashMap;
import java.util.Map;

public class MethodReferenceEx {
    public static void main(String[] args) {
        Map<Integer, Integer> map = new HashMap<>();
        Integer key = 1;
        // 람다식 예제
        map.merge(key, 1, (count, incr) -> count + incr);

        // 메서드 레퍼런스 예제
        map.merge(key, 1, Integer::sum);


    }

    public void doSomething() {
        //execute(() -> action());
        execute(MethodReferenceEx::action);
    }

    public void execute(Executor e) {
        e.execute();
    }


    public static void action() {
        System.out.println("do action!!!");
    }
}

@FunctionalInterface
interface Executor {
    void execute();
}
