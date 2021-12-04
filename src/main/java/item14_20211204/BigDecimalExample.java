package item14_20211204;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class BigDecimalExample {

    public static void main(String[] args) {
        BigDecimal bigDecimal1 = new BigDecimal("1.0");
        BigDecimal bigDecimal2 = new BigDecimal("1.00");

        Set<BigDecimal> hashSet = new HashSet<>();
        hashSet.add(bigDecimal1);
        hashSet.add(bigDecimal2);

        Set<BigDecimal> treeSet = new TreeSet<>();
        treeSet.add(bigDecimal1);
        treeSet.add(bigDecimal2);

        System.out.println("hashSet size = " + hashSet.size());

        System.out.println("treeSet size = " + treeSet.size());
    }
}
