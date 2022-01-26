package item61_20220126;

public class IntegerEx {

    public static void main(String[] args) {
        Integer i = new Integer(42);
        Integer i2 = new Integer(42);

        System.out.println(i == i2);

        System.out.println(i.equals(i2));
    }
}
