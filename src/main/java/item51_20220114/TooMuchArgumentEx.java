package item51_20220114;

public class TooMuchArgumentEx {

    public static void main(String[] args) {
        String country = "한국";
        String city = "서울";
        String detail = "송파구 롯데타워 최상층 살고싶호";
        String zipCode = "11111";
        Address address = new Address(country, city, detail, zipCode);
        System.out.println("update 호출 전 주소 : " + address);

        String newZipCode = "33333";
        address.update(newZipCode, detail, city, country);
        System.out.println("update 호출 후 주소 : " + address);
    }
}

class Address {
    private String country;
    private String city;
    private String detail;
    private String zipCode;

    public Address(String country, String city, String detail, String zipCode) {
        this.country = country;
        this.city = city;
        this.detail = detail;
        this.zipCode = zipCode;
    }

    public void update(String country, String city, String detail, String zipCode) {
        this.country = country;
        this.city = city;
        this.detail = detail;
        this.zipCode = zipCode;
    }

    @Override
    public String toString() {
        return "Address{" +
                "country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", detail='" + detail + '\'' +
                ", zipCode='" + zipCode + '\'' +
                '}';
    }
}
