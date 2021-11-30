package item11_20211130;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ExampleWithHashCode {

    public static void main(String[] args) {
        Map<PhoneNumber2, String> phoneMap = new HashMap<>();
        phoneMap.put(new PhoneNumber2(010, 000, 000), "찰리");

        String name = phoneMap.get(new PhoneNumber2(010, 000, 000));
        // hashCode를 제대로 정의했으므로 "찰리"를 가져옴!
        System.out.println("name = " + name);
    }
}

class PhoneNumber2 {
    private final int areaCode;
    private final int prefix;
    private final int lineNum;

    public PhoneNumber2(int areaCode, int prefix, int lineNum) {
        this.areaCode = areaCode;
        this.prefix = prefix;
        this.lineNum = lineNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhoneNumber2 that = (PhoneNumber2) o;
        return areaCode == that.areaCode && prefix == that.prefix && lineNum == that.lineNum;
    }

    // 아래의 hashCode 메서드보다 성능이 약간 떨어지지만 코드가 간결함!!
    @Override
    public int hashCode() {
        return Objects.hash(areaCode, prefix, lineNum);
    }

//    더욱 빠른 hashCode
//    @Override
//    public int hashCode() {
//        int result = Integer.hashCode(areaCode);
//        result = 31 * result + Integer.hashCode(prefix);
//        result = 31 * result + Integer.hashCode(lineNum);
//        return result;
//    }
}
