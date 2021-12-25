package item33_20211224;

import java.util.Collections;

public class Example1 {
    public static void main(String[] args) {
        Favorites f = new Favorites();

        f.putFavorite((Class)Integer.class, "Integer의 인스턴스가 아닙니다.");
        int favoriteInteger = f.getFavorite(Integer.class);
    }
}
