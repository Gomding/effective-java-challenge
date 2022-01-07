# 아이템44. 표준 함수형 인터페이스를 사용하라

자바가 람다를 지원하면서 API를 작성하는 모범 사례도 크게 바뀌었다.

람다의 등장으로 상위 클래스의 기본 메서드를 재정의해 원하는 동작을 구현하는 템플릿 메서드 패턴의 매력이 크게 줄었다.

이를 대체하는 현대적인 해법은 같은 효과의 함수 객체를 받는 정적 팩터리나 생성자를 제공하는 것이다.

```java
// 템플릿 메서드 패턴 예시
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

// 실행 결과
// Task :TemplateMethodEx.main()
// TemplateChild1 : 재정의해서 동작을 정의한다.
// TemplateChild2 : 재정의해서 동작을 정의한다.

// 람다식 적용
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

// 실행결과
// Task :LambdaEx.main()
// lambda : 동작은 생성할 때 람다로 지정한다.
// lambda2 : 이건 한번이 아니라 두번 출력됩니다.lambda2 : 이건 한번이 아니라 두번 출력됩니다.

```

이 내용을 일반화해서 말하면 함수 객체를 매개변수로 받는 생성자와 메서드를 더 많이 만들어야 한다.
이때 함수형 매개변수 타입을 올바르게 선택해야 한다.

LinkedHashMap을 생각해보자. 이 클래스의 protected 메서드인 removeEldestEntry를 재정의하면 캐시로 사용할 수 있다.   
맵에 새로운 키를 추가하면 put 메서드는 이 메서드를 호출하여 true가 반환되면 맵에서 가장 오래된 원소를 제거한다.   
예컨데 removeEldestEntry를 다음처럼 재정의하면 맵에 원소가 100개가 될 때까지 커지다가, 그 이상이 되면 새로운 키가 더해질 때마다 가장 오래된 원소를 하나씩 제거한다.   
즉, 가장 최근 원소 100개를 유지한다.

```java
protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
    return size() > 100;
}
```

이 코드는 람다를 활용하면 훨씬 더 잘 해낼 수 있다.   
LinkedHashMap을 오늘날 다시 구현한다면 함수 객체를 받는 정적 팩터리나 생성자를 제공했을 것이다.

removeEldestEntry 선언을 보면 이 함수 객체는 ```Map.Entry<K,V>```를 받아 boolean을 반환해야 할 것 같지만 그렇지 않다.   
removeEldestEntry는 size()를 호출해서 맵 안의 원소 수를 알아내는데, removeEldestEntry가 인스턴스 메서드라 가능한 방식이다.

하지만 생성자에 넘기는 함수 객체는 이 맵의 인스턴스 메서드가 아니다. 팩터리나 생성자 호출할 때는 맵의 인스턴스가 존재하지 않기 때문이다.   
**따라서 맵은 자기 자신도 함수 객체에 건내줘야 한다.**

```java
@FunctionalInterface
public interface EldestEntryRemovalFunction<K, V> {
    boolean remove(Map<K, V> map, Map.Entry<K, V> eldest);
}
```

이 인터페이스는 잘 동작하지만 굳이 만들 필요가 없는 인터페이스다. **자바 표준 라이브러리에 이미 같은 모양의 인터페이스가 준비되어 있다.**

java.util.function 패키지를 보면 다양한 용도의 표준 함수형 인터페이스가 담겨있다.

> 필요한 용도에 맞는게 있다면, 직접 구현하지 말고 표준 함수형 인터페이스를 활용하라.   
> API가 다루는 개념의 수가 줄어들어 익히기 더 쉬워진다.(필자는 'API에 대한 이해가 쉬워진다' 라고 이해했다.)

또한 표준 함수형 인터페이스들은 **유용한 디폴트 메서드를 많이 제공**하므로 다른 코드와의 상호운용성도 크게 좋아질 것이다.   
예를들어 Predicate 인터페이스는 Predicate들을 조합하는 메서드를 제공한다.
앞의 LinkedHashMap 예시에서 직접만든 EldestEntryRemovalFunction 인터페이스는 ```BiPredicate<Map<K,V>, Map.Entry<K,V>>```를 사용할 수 있다.

java.util.function 패키지에는 총 43개의 인터페이스가 담겨 있다. 
전부 기억하긴 어렵겠지만, 기본 인터페이스 6개만 기억하면 나머지를 충분히 유추해 낼 수 있다.   
이 기본 인터페이스들은 모두 참조 타입용이다.
(함수 시그니쳐란, 함수형 인터페이스의 유일한 메서드를 의미한다.)
* ```UnaryOperator<T>, BinaryOperator<T>``` : 반환값과 인수의 타입이 같은 함수.
  * Operator 인터페이스는 인수가 1개인 UnaryOperator와 2개인 BinaryOperator로 나뉜다.
  * UnaryOperator 함수 시그니쳐 : T apply(T t)
  * BinaryOperator 함수 시그니쳐 : T apply(T t1, T t2)
* ```Predicate<T>``` : 인수 하나를 받아 boolean을 반환하는 함수
  * Predicate 함수 시그니쳐 : boolean test(T t)
* ```Function<T, R>``` 인수와 반환값 타입이 다른 함수.
  * Function 함수 시그니쳐 : R apply(T t)
* ```Supplier<T>``` : 인수가 없고 반환값만 있는 함수
  * Supplier 함수 시그니쳐 : T get()
* ```Consumer<T>``` : 반환값이 없고 인수만 있는 함수
  * Consumer 함수 시그니쳐 : void accept(T t)

기본 인터페이스는 기본 타입인 int, long, double용으로 각 3개씩 변형이 생겨난다.   
그 이름도 기본 인터페이스의 이름 앞에 해당 기본 타입 이름을 붙여 지었다.   
예컨대 int를 받는 Predicate는 IntPredicate가 되고 long을 받아 long을 반환하는 BinaryOperator는 LongBinaryOperator가 되는 식이다.

Function 인터페이스에는 기본 타입 반환을 반환하는 변경이 총 9개가 있다.
입력과 결과 타입이 모두 기본 타입이면 접두어로 SrcToResult을 사용한다. 예를들어 long을 받아 int를 반환하면 LongToIntFunction이 되는 식이다.   
입력이 객체 참조이고 반환값이 int, long, double인 변형으로 접두어로 ToResult를 사용한다. 예를들어 ToLongFunction<int[]>은 int[] 인수를 받아 long을 반환한다.

기본 함수형 인터페이스 중 3개에는 인수를 2개씩 받는 유형이 있다.   
* ```BiPredicate<T,U>```
* ```BiFunction<T,U,R>```
  * 기본형 반환 타입이 존재
  * ```ToIntBiFunction<T,U,R>```
  * ```ToLongBiFunction<T,U,R>```
  * ```ToDoubleBiFunction<T,U,R>```
* ```BiConsumer<T,U>```
  * 객체 참조와 기본타입 하나를 받는 변형이 존재
  * ```ObjIntBiConsumer<T>```
  * ```ObjLongBiConsumer<T>```
  * ```ObjDoubleBiConsumer<T>```

BooleanSuplier도 존재한다. boolean을 반환하도록 한 Suplier의 변형이다. Predicate와 그 변형 4개도 boolean을 반환할 수 있다.

표준 함수형 인터페이스는 총 43개다. 다 외우기엔 수도 많고 규칙성도 부족하다. 필요할 때 찾아 쓸 수 있는 만큼은 범용적인 이름을 사용했으므로 필요할 때 적절하게 찾아보는걸 추천한다.

### 표준 함수형 인터페이스 대신 직접 함수형 인터페이스를 작성해야하는 경우

직접 작성해야하는 경우는 언제일까?

가장 좋은 예시로 ```Comparator<T>```가 있다. 구조적으로는 ```ToIntBiFunction<T, U>```와 동일하다.(두 객체타입의 인수를 받아 int타입을 반환)

심지어 라이브러리에 ```Comparator<T>```를 추가할 당시 ```ToIntBiFunction<T, U>```가 이미 존재했더라도 ```ToIntBiFunction<T, U>```를 사용하면 안 됐다.

Comparator가 독자적인 인터페이스로 작성된 이유가 있다.
1. API에서 굉장히 자주 사용되는데, 지금의 이름이 그 용도를 아주 훌륭히 설명해준다.
2. 구현하는 쪽에서 반드시 지켜야 할 규약을 담고 있다.
3. 비교자들을 반환하고 조합해주는 유용한 디폴트 메서드들을 듬뿍 담고 있다.

정리하면 다음과 같다.
* 자주 쓰이며, 이름 자체가 용도를 명황히 설명해야한다.
* 반드시 따라야 하는 규약이 있다.
* 유용한 디폴트 메서드를 제공할 수 있다.

### 직접만든 함수형 인터페이스에는 @FunctionalInterface 애너테이션을 꼭 사용하라

이 애너테이션을 사용하는 이유는 @Override를 사용하는 이유와 비슷하다.
1. 컴파일 타임에 함수형 인터페이스인지 검사해준다. (함수형 인터페이스 조건을 만족하지 못하면 컴파일타임에 오류가 발생)
2. 해당 클래스의 코드나 설명 문서를 읽을 이에게 해당 인터페이스가 람다용으로 설계된 것임을 알려준다.
3. 유지보수 과정에서 누군가 실수로 메서드를 추가하지 못하게 막아준다.(1번과 관계있음)

### 함수형 인터페이스 API에서 사용할 때의 주의점

서로 다른 함수형 인터페이스를 같은 위치의 인수로 받는 메서드들을 **다중 정의(Overloading)**해서는 안 된다.   
클라이언트에게 불필요한 모호함만 안겨줄 뿐이며, 이 모호함으로 인해 실제로 문제가 일어나기도 한다.

ExecuteService의 submit 메서드가 그렇다.

```java
// ExecuteService 의 submit 메서드
public void submit(Callable<T> callable)

public void submit(Runnable<T> runnable)
```

이는 올바른 메서드를 알려주기 위해 형변환해야 할 때가 자주 생긴다.

이런 문제를 피하는 쉬운 방법은 다중 정의 자체를 피하는 것이다. 