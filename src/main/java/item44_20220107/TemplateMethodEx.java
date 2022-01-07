package item44_20220107;

public class TemplateMethodEx {
    public static void main(String[] args) {
        TemplateChild1 templateChild1 = new TemplateChild1();
        templateChild1.templateMethod();

        TemplateChild2 templateChild2 = new TemplateChild2();
        templateChild2.templateMethod();
    }
}

abstract class TemplateMethodClass {
    public void templateMethod() {
        doSomething();
    }

    abstract protected void doSomething();
}

class TemplateChild1 extends TemplateMethodClass {
    @Override
    protected void doSomething() {
        System.out.println("TemplateChild1 : 재정의해서 동작을 정의한다.");
    }
}

class TemplateChild2 extends TemplateMethodClass {
    @Override
    protected void doSomething() {
        System.out.println("TemplateChild2 : 재정의해서 동작을 정의한다.");
    }
}
