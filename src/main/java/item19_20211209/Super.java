package item19_20211209;

public class Super {

    public Super() {
        // 상속용 클래스에서 내부의 재정의 가능한 메서드를 호출함 - 안티패턴
        overrideMe();
    }

    public void overrideMe() {
    }
}
