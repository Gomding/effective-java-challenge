package item24_20211214;

public class NonStaticClassEx2 {
    public static void main(String[] args) {
        RootClass rootClass = new RootClass();
//      RootClass.MemberClass memberClass = rootClass.new MemberClass();
        RootClass.MemberClass memberClass = new RootClass.MemberClass();
    }
}

class RootClass {
    public static class MemberClass {

    }
}
