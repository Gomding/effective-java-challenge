package item53_20220116;

public class VarargsEx2 {
    static int min(int firstArg, int... remainingArgs) {
        int min = firstArg;
        for (int arg : remainingArgs) {
            min = Math.min(min, arg);
        }
        return min;
    }
}
