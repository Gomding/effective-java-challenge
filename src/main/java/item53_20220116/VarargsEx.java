package item53_20220116;

public class VarargsEx {

    public static void main(String[] args) {
        System.out.println(sum(1, 2, 3));
        System.out.println(sum());
    }

    static int sum(int... args) {
        int sum = 0;
        for (int arg : args) {
            sum += arg;
        }
        return sum;
    }

    static int min(int... args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("인수가 1개 이상 필요합니다.");
        }
        int min = args[0];
        for (int i = 1; i < args.length; i++) {
            min = Math.min(min, args[i]);
        }
        return min;
    }
}
