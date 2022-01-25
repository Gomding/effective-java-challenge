package item60_20220125;

public class FloatDoubleEx {

    public static void main(String[] args) {
        System.out.println(1.03 - 0.42);

        System.out.println(1.00 - (9 * 0.10));

        double funds = 1.00;
        int itemsBought = 0;
        for(double price = 0.10; funds >= price; price += 0.10) {
            funds -= price;
            itemsBought++;
        }
        System.out.println(itemsBought + "개 구입");
        System.out.println("잔돈(달러):" + funds);
    }
}
