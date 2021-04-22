package item6_20210422;

public class AntiPatternEx4 {
    public static void main(String[] args) {
        // 오토 박싱 비용으로 인해 불필요한 작업이 추가로 생김
        long start = System.currentTimeMillis();
        Long sum = 0L;

        for (long i = 0; i < Integer.MAX_VALUE; i++) {
            sum += i;
        }

        System.out.println(sum);
        System.out.println(System.currentTimeMillis() - start);
    }
}
