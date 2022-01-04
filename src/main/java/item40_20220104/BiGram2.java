package item40_20220104;

public class BiGram2 {
    private final char first;
    private final char second;

    public BiGram2(char first, char second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BiGram2 biGram2 = (BiGram2) o;
        return first == biGram2.first && second == biGram2.second;
    }

    public int hashCode() {
        return 31 * first + second;
    }
}
