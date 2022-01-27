package item62_20220127;

import java.util.HashMap;
import java.util.Map;

public class ThreadLocal2 {
    private static final Map<Key, Object> threadLocalMap = new HashMap<>();

    private ThreadLocal2() {}

    public static class Key {
        Key() {}
    }

    public static Key getKey() {
        return new Key();
    }

    public static void set(Key key, Object value) {
        threadLocalMap.put(key, value);
    }

    public static Object get(Key key) {
        return threadLocalMap.get(key);
    }
}
