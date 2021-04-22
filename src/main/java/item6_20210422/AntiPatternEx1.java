package item6_20210422;

public class AntiPatternEx1 {

    public static void main(String[] args) {

        // 아래 s1 과 s2 는 각각 새로운 인스턴스를 생성한다.
        // 다른 객체로 활용하고 싶은게 아니라면 아래의 행위는 매우 쓸모없는 행위이다.
        String s1 = new String("charlie");
        String s2 = new String("charlie");

        System.out.println(s1 == s2);
    }
}
