package item6_20210422;

import java.util.regex.Pattern;

public class GoodPatternEx3 {

    // 캐시해두고 재사용
    private static final Pattern ROMAN = Pattern.compile(
            "^(?=.)M*(C[MD]|D?C{0,3})"
            + "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");

    static boolean isRomanNumeral(String input) {
        return ROMAN.matcher(input).matches();
    }
}
