package item48_20220111;

import java.math.BigInteger;
import java.util.stream.Stream;

public class MersennEx {
    private static final BigInteger TWO = BigInteger.valueOf(2);

    public static void main(String[] args) {
        primes().map(p -> TWO.pow(p.intValueExact()).subtract(BigInteger.ONE))
                .filter(mersenn -> mersenn.isProbablePrime(50))
                .limit(20)
                .forEach(System.out::println);
    }

    static Stream<BigInteger> primes() {
        return Stream.iterate(TWO, BigInteger::nextProbablePrime);
    }
}
