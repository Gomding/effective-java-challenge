package item70_20220204;

public class ExceptionEx4 {
}

class CheckCard {
    private int balance;

    public CheckCard(int balance) {
        this.balance = balance;
    }

    public void payment(int price) throws LackOfBalanceException {
        if (balance - price < 0) {
            throw new LackOfBalanceException(balance, price);
        }
        balance -= price;
    }

    public int getBalance() {
        return balance;
    }
}

class LackOfBalanceException extends Exception {
    private final int balance;
    private final int price;

    public LackOfBalanceException(int balance, int price) {
        super();
        this.balance = balance;
        this.price = price;
    }

    public int lackOfBalance() {
        return balance - price;
    }
}