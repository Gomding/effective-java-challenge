package item11_20211130;

import java.util.HashMap;
import java.util.Map;

public class ExampleWithoutHashCode {

    public static void main(String[] args) {
        Map<PhoneNumber, String> phoneMap = new HashMap<>();
        phoneMap.put(new PhoneNumber(010, 000, 000), "찰리");

        String name = phoneMap.get(new PhoneNumber(010, 000, 000));
        // hashCode를 재정의 하지않아서 null이 나옴!!
        System.out.println("name = " + name);
    }
}

// equals 메서드만 재정의하고 hashCode를 재정의하지 않은 클래스
class PhoneNumber {

    private final int areaCode;
    private final int prefix;
    private final int lineNum;

    public PhoneNumber(int areaCode, int prefix, int lineNum) {
        this.areaCode = areaCode;
        this.prefix = prefix;
        this.lineNum = lineNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhoneNumber that = (PhoneNumber) o;
        return areaCode == that.areaCode && prefix == that.prefix && lineNum == that.lineNum;
    }
}
