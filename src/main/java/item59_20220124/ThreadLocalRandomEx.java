package item59_20220124;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ThreadLocalRandomEx {
    public static void main(String[] args) {
        Random random = new Random();
        ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();

        long start1 = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            random.nextInt();
        }
        System.out.println(System.currentTimeMillis() - start1);

        long start2 = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            threadLocalRandom.nextInt();
        }
        System.out.println(System.currentTimeMillis() - start2);
    }
}
