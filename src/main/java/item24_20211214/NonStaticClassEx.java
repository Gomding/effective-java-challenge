package item24_20211214;

public class NonStaticClassEx {
    public static void main(String[] args) {
        Account account = new Account();
        Account.Money money = account.new Money(1);
    }
}

class Account {

    void doSomething() {
        System.out.println("do something");
    }

    public class Money {
        private final int value;

        public Money(int value) {
            this.value = value;
        }

        void doAnything() {
            Account.this.doSomething();
        }

        public int getValue() {
            return value;
        }
    }
}
