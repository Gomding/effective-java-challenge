package item6_20210422;

public class GoodPatternEx1 {

    public static void main(String[] args) {
        // 아래 s3 와 s4 는 하나의 같은 인스턴스로 초기화한다.
        // String 은 내부적으로 문자열 풀을 사용해서 같은 문자열이라면 같은 주소값을 가지도록 합니다.
        String s3 = "charlie";
        String s4 = "charlie";

        System.out.println(s3 == s4);
    }
}
