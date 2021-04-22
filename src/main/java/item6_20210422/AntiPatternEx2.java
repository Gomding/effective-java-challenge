package item6_20210422;

public class AntiPatternEx2 {

    public static void main(String[] args) {

        // Java9 부터는 Boolean의 생성자가 deprecated 되었습니다.
        Boolean b1 = new Boolean("true");
        Boolean b2 = new Boolean("true");

        System.out.println(b1 == b2);

        // 정적 팩터리 메서드를 사용하면 불필요한 객체 생성을 피할 수 있습니다.
        // Boolean.valueOf(String) 정적 팩터리는 이미 만들어진 객체를 반환해줍니다.
        /*

        public static Boolean valueOf(String s) {
              return parseBoolean(s) ? TRUE : FALSE;
        }

        */
        Boolean b3 = Boolean.valueOf("true");
        Boolean b4 = Boolean.valueOf("true");

        System.out.println(b3 == b4);
    }
}
