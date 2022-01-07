package item44_20220107;

import java.util.function.Consumer;

public class LambdaEx {
    public static void main(String[] args) {
        LambdaInsteadTemplateMethod<String> lambda = new LambdaInsteadTemplateMethod<>(System.out::println);
        lambda.templateMethod("lambda : 동작은 생성할 때 람다로 지정한다.");

        LambdaInsteadTemplateMethod<String> lambda2 = new LambdaInsteadTemplateMethod<>(str -> System.out.println(str + str));
        lambda2.templateMethod("lambda2 : 이건 한번이 아니라 두번 출력됩니다.");
    }
}

class LambdaInsteadTemplateMethod<T> {

    private final Consumer<T> consumer;

    public LambdaInsteadTemplateMethod(Consumer<T> consumer) {
        this.consumer = consumer;
    }

    public void templateMethod(T t) {
        consumer.accept(t);
    }
}
