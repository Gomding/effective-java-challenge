package item62_20220127;

import java.util.HashMap;
import java.util.Map;

public class ThreadLocal {
    private static final Map<String, Object> threadLocalMap = new HashMap<>();

    private ThreadLocal() {}

    public static void set(String key, Object value) {
        threadLocalMap.put(key, value);
    }

    public static Object get(String key) {
        return threadLocalMap.get(key);
    }
}