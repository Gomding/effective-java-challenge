package Item12_20211202;

public class PhoneNumber {
    private final int areaCode;
    private final int prefix;
    private final int lineNum;

    public PhoneNumber(int areaCode, int prefix, int lineNum) {
        this.areaCode = areaCode;
        this.prefix = prefix;
        this.lineNum = lineNum;
    }

    @Override
    public String toString() {
        return "PhoneNumber{" +
                "areaCode=" + areaCode +
                ", prefix=" + prefix +
                ", lineNum=" + lineNum +
                '}';
    }
}

class ToStringWithFormat {
    private final int areaCode;
    private final int prefix;
    private final int lineNum;

    public ToStringWithFormat(int areaCode, int prefix, int lineNum) {
        this.areaCode = areaCode;
        this.prefix = prefix;
        this.lineNum = lineNum;
    }

    /**
     * 이 전화번호의 문자열 표현을 반환한다.
     * 이 문자열은 "XXX-YYY-ZZZZ" 형태의 12글자로 구성된다.
     * XXX는 지역 코드, YYY는 프리픽스, ZZZZ는 가입자 번호다.
     * 각각의 대문자는 10진수 숫자 하나를 나타낸다.
     *
     * 전화번호의 각 부분의 값이 너무 작아서 자릿수를 채울 수 없다면,
     * 앞에서부터 0으로 채워나간다. 예컨대 가입자 번호가 123이라면
     * 전화번호의 마지막 네 문자는 "0123"이 된다.
     */
    @Override
    public String toString() {
        return String.format("%03d-%03d-%04d", areaCode, prefix, lineNum);
    }
}

class PotionToStringWithoutFormat {
    private long id;
    private String type;
    private String smell;
    private String form;

    public PotionToStringWithoutFormat(String type, String smell, String form) {
        this.type = type;
        this.smell = smell;
        this.form = form;
    }

    /**
     * 이 약물에 관한 대략적인 설명을 반환한다.
     * 다음은 이 설명의 일반적인 형태이나,
     * 상세 형식은 정해지지 않았으며 향후 변경될 수 있다.
     *
     * "[약물 #9: 유형=사랑, 냄새=테레빈유, 겉모습=먹물]"
     */
    @Override
    public String toString() {
        return String.format("[약물 #%d: 유형=%s, 냄새=%s, 겉모습=%s]", id, type, smell, form);
    }
}
