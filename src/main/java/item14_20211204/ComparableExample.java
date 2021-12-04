package item14_20211204;

import java.util.Arrays;
import java.util.Comparator;

public class ComparableExample implements Comparable<ComparableExample> {

    private int number;

    public ComparableExample(int number) {
        this.number = number;
    }

    public static void main(String[] args) {
        ComparableExample comparableExample1 = new ComparableExample(10);
        ComparableExample comparableExample2 = new ComparableExample(20);
        int result = comparableExample1.compareTo(comparableExample2);

        System.out.println(result == -1);
    }

    @Override
    public int compareTo(ComparableExample o) {
        int result = this.number - o.number;
        if (result > 0) {
            return 1;
        }
        if (result < 0) {
            return -1;
        }
        return 0;
    }
}
