package item13_20211203;

public class CloneExample2 {

    public static void main(String[] args) throws CloneNotSupportedException {
        AdminAccount adminAccount = new AdminAccount("park", 13, "사장님");
//      (AdminAccount)adminAccount.clone(); 에러 발생!! AdminAccount로 변환할 수 없다.
        Account clone = (Account)adminAccount.clone();

        System.out.println("원본 = " + adminAccount);
        System.out.println("복사본 = " + clone);
    }
}

class Account implements Cloneable {
    private String name;
    private int age;

    public Account(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new Account(this.name, this.age);
    }

    @Override
    public String toString() {
        return "Account{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}

class AdminAccount extends Account implements Cloneable {
    private String type;

    public AdminAccount(String name, int age, String type) {
        super(name, age);
        this.type = type;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return "AdminAccount{" +
                "type='" + type + '\'' +
                '}';
    }
}
