package item40_20220104;

import java.util.HashSet;
import java.util.Set;

public class BiGramClient {
    public static void main (String[] args) {
        Set<BiGram> set = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            for (char ch = 'a'; ch <= 'z' ; ch++) {
                set.add(new BiGram(ch, ch));
            }
        }
        System.out.println(set.size());
    }
}

class BiGram {
    private final char first;
    private final char second;

    public BiGram(char first, char second) {
        this.first = first;
        this.second = second;
    }

    // 오버라이딩이 아닌, '오버로딩'이 됐다.
    public boolean equals(BiGram b) {
        return b.first == this.first && b.second == this.second;
    }

    public int hashCode() {
        return 31 * first + second;
    }
}
