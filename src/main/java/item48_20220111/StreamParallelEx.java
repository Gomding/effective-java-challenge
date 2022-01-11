package item48_20220111;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.stream.LongStream;

public class StreamParallelEx {

    public static void main(String[] args) {
//        long start1 = System.currentTimeMillis();
//        pi(10);
//        System.out.println(System.currentTimeMillis() - start1);

        long start2 = System.currentTimeMillis();
        parallelPi(10);
        System.out.println(System.currentTimeMillis() - start2);
    }

    static long pi(long n) {
        return LongStream.rangeClosed(2, n)
                .mapToObj(BigInteger::valueOf)
                .filter(i -> i.isProbablePrime(50))
                .count();
    }

    static long parallelPi(long n) {
        return LongStream.rangeClosed(2, n)
                .parallel()
                .mapToObj(BigInteger::valueOf)
                .filter(i -> i.isProbablePrime(50))
                .count();
    }
}
