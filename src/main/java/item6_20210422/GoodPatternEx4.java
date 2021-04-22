package item6_20210422;

public class GoodPatternEx4 {

    public static void main(String[] args) {
        // 오토 박싱 비용이 없으므로 위보다 약 10배 내외로 빠름
        long start = System.currentTimeMillis();
        long sum = 0L;

        for (long i = 0; i < Integer.MAX_VALUE; i++) {
            sum += i;
        }

        System.out.println(sum);
        System.out.println(System.currentTimeMillis() - start);
    }
}
