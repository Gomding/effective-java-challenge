package item51_20220114;

public class LongMethodArgumentsListEx1 {

}

class Account {
    private String name;
    private int age;
    private String country;
    private String city;
    private String zipCode;

    public Account() {
    }

    public void setAccount(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public void setAddress(String country, String city, String zipCode) {
        this.country = country;
        this.city = city;
        this.zipCode = zipCode;
    }
}

class Account2 {
    private String name;
    private int age;
    private Address address;

    public Account2() {
    }

    public void setAccount(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    static class Address {
        private String country;
        private String city;
        private String zipCode;

        public Address(String country, String city, String zipCode) {
            this.country = country;
            this.city = city;
            this.zipCode = zipCode;
        }
    }
}
