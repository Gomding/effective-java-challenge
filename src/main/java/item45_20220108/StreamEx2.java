package item45_20220108;

import java.util.Arrays;

public class StreamEx2 {
    public static void main(String[] args) {
        String[] words = {"5", "2", "3", "1", "4"};
        Arrays.stream(words)
                .map(word -> "새로운 값" + word) // 새로운 값과 예전값 모두 매핑할 방법은 이대로 불가능하다.
                .toArray();

        Arrays.stream(words)
                .map(word -> new WordOldAndNew(word, "새로운 값" + word)) // 둘다 매핑할 수 있는 객체를 생성해야 가능
                .toArray();
    }

    static class WordOldAndNew {
        private final String oldValue;
        private final String newValue;

        public WordOldAndNew(String oldValue, String newValue) {
            this.oldValue = oldValue;
            this.newValue = newValue;
        }
    }
}
