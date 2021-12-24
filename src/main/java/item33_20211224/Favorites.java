package item33_20211224;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Favorites {
    public static void main(String[] args) {
        Favorites f = new Favorites();

        f.putFavorite(String.class, "Java");
        f.putFavorite(Integer.class, 123);
        f.putFavorite(Class.class, Favorites.class);

        String favoriteString = f.getFavorite(String.class);
        Integer favoriteInteger = f.getFavorite(Integer.class);
        Class favoriteClass = f.getFavorite(Class.class);
    }

    Map<Class<?>, Object> values = new HashMap<>();

    public <T> void putFavorite(Class<T> type, T instance) {
        values.put(Objects.requireNonNull(type), instance);
    }

    public <T> T getFavorite(Class<T> type) {
        return type.cast(values.get(type));
    }
}
