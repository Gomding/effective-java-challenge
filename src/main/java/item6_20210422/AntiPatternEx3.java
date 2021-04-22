package item6_20210422;

public class AntiPatternEx3 {


    // String.matches() 는 정규표현식으로 문자열 형태를 확인하는 가장 쉬운 방법이지만,
    // 성능이 중요한 상황에서 반복해 사용하기엔 적합하지 않다.
    // 내부에서 만드는 정규표현식용 Pattern 인스턴스는,
    // 한 번 쓰고 버려져서 곧바로 가비지 컬렉터 대상이 된다.
    // Pattern 은 입력받은 정규표현식에 해당하는
    // 유한 상태 머신(finite state machine)을 만들기 때문에 인스턴스 생성 비용이 높다.
    static boolean isRomanNumeral(String input) {
        return input.matches("^(?=.)M*(C[MD]|D?C{0,3})"
                + "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");
    }
}
