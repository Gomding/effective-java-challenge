# 아이템42. 익명 클래스보다는 람다를 사용하라

예전에는 자바에서 함수 타입을 표현할 때 추상 메서드를 하나만 담은 인터페이스(드물게는 추상 클래스)를 사용했다.

이런 인터페이스의 인스턴스를 함수 객체(function object)라고 하여, 특정 함수나 동작을 나타내는 데 썼다.

```java
interface FunctionObject {
    void apply();
}

// 사용 예시
public static void main(String[] args) {
    // 함수 객체 생성
    FunctionObject fo = new FunctionObject() {
        @Override
        public void apply() {
            System.out.println("Hello World!!");
        }
    }
    doSomething(fo);
}

public static void doSomething(FunctionObject fo) {
    fo.apply();
}
```

JDK 1.1이 등장하면서 함수 객체를 만드는 주요 수단은 익명 클래스가 되었다.

```java
interface FunctionObject {
    void apply();
}

// 사용 예시
public static void main(String[] args) {
    // 익명 클래스의 객체를 생성
    doSomething(new FunctionObject() {
        @Override
        public void apply() {
            System.out.println("Hello World!!");
        }
    });
}

public static void doSomething(FunctionObject fo) {
    fo.apply();
}
```

전략 패턴처럼, 함수 객체를 사용하는 과거 객체 지향 디자인 패턴에는 익명 클래스면 충분했다.

위 코드에서 FunctionObject 인터페이스가 추상 전략을 뜻하며, 어떤 행동을 할지는 익명 클래스로 구현했다.

익명 클래스를 사용한 함수형 프로그래밍은 문제가 있다.
> 코드가 너무 길기 때문에 자바는 함수형 프로그래밍에 적합하지 않다.

### 자바 8 람다의 출현

자바 8에 와서 추상 메서드 하나짜리 인터페이스는 특별한 의미를 인정받아 특별한 대우를 받게 되었다.

지금은 함수형 인터페이스라 부르는 이 인터페이스들의 인스턴스를 람다식(lambda expression)을 사용해 만들 수 있게 된 것이다.

@FunctionalInterface 애너테이션을 사용해서 컴파일타임에 함수형 인터페이스인지 검사를 할 수 있다. (함수형 인터페이스가 아니라면 컴파일 오류가 발생)   
함수형 인터페이스의 조건은 아래와 같다.
* 인터페이스에서 default 메서드를 제외한 추상 메서드는 1개만 존재해야한다.
  * 추상 메서드가 존재하지 않거나, 2개 이상이라면 함수형 인터페이스가 아니다.
* default 메서드의 개수는 함수형 인터페이스에 상관없다. 

```java
// @FunctionalInterface 애너테이션은 해당 인터페이스가 함수형 인터페이스인지 컴파일 타임에 체크해준다.
@FunctionalInterface
public interface MyFunction<T, R> {
    R apply(T t);
}

// default 메서드의 개수는 함수형 인터페이스의 조건에 영향이 없다. 추상 메서드가 1개만 존재하면 된다.
// 따라서 아래도 함수형 인터페이스의 조건을 만족한다.
@FunctionalInterface
public interface MyFunction<T, R> {
    R apply(T t);
    
    default void doSomething() {
        System.out.println("doSomething");
    }
}

// 추상 메서드가 2개라서 함수형 인터페이스가 아니다.
@FunctionalInterface
public interface MyFunction2<T, R> {
    R apply(T t);

    R apply2(T t);
}
```

람다는 함수나 익명 클래스와 개념은 비슷하지만 코드는 훨씬 간결하다. 다음은 익명 클래스를 사용한 앞의 코드를 람다 방식으로 바꾼 모습이다.   
자질구레한 코드들이 사라지고 어떤 동작을 하는지가 명확하게 드러난다.

```java
public static void main(String[] args) {
    // 람다식 사용
    doSomething(() -> System.out.println("Hello World!!"));
}
```

아래는 실제 사용 사례이다. Collections.sort() 메서드는 첫번째 인자로 컬렉션 타입 예시에서는 단어들을 의미하는 문자열 컬렉션을 넣었다., 두번째 인자로 Comparator 타입을 받는다.   
(Comparator는 interface 타입이므로 compare 메서드 구현이 필요하다.)

```java
// Comparator<T> 의 compare(T v1, T v2) 를 람다식으로 표현했다.
Collections.sort(words, (s1, s2) -> Integer.compare(s1.length(), s2.length()));

// 람다식을 사용하지 않으면 아래와 같이 표현된다.
Collections.sort(words, new Comparator<String>() {
    public int compare(String s1, String s2) {
        return Integer.compare(s1.length(), s2.length());
    }
});
```

여기서 람다, 매개변수 (s1, s2), 반환값의 타입은 각각 다음과 같다.
* 람다 : (Comparator<String>)
* 매개변수 (s1, s2) : String
* 반환값 : int

하지만 타입에 대해서 람다식에서는 언급이 없다. 이것은 우리 대신 컴파일러가 문맥을 살펴보고 타입을 추론해준 것이다.   
상황에 따라 컴파일러가 타입을 결정하지 못할 수도 있는데, 그럴 때는 프로그래머가 직접 명시해야 한다.   

타입 추론 규칙은 자바 언어 명세의 장(chapter) 하나를 통째로 차지할 만큼 복잡하다. 너무 복잡해서 규칙을 다 알기도 어렵고 잘 알지 못해도 상관없다.

> 타입을 명시해야 하는 코드가 명활할 때를 제외하고는, 람다의 모든 매개변수 타입은 생략하자.

만약 컴파일러가 "타입을 알 수 없다"라는 오류를 낸다면 그 때 해당 타입을 명시하면 된다.

위의 예시는 좀 더 리팩터링 하자면 비교자 생성 메서드를 사용하면 더 간결하게 만들 수 있다.

```java
Collections.sort(words, comparingInt(String::length));
```

더 나아가 자바 8때 List 인터페이스에 추가된 sort 메서드를 이용하면 더욱 짧아진다.

```java
word.sort(comparingInt(String::Length));
```

### 람다의 활용

람다를 언어 차원에서 지원하면서 기존에는 함수 객체가 적합하지 않았던 곳에도 실용적으로 사용할 수 있게 됐다.

아이템 34의 Operation 열거 타입을 예로 들어보자. apply 메서드의 동작이 상수마다(+, -, *, / 같은 연산자마다) 달라야 해서 상수별 클래스 몸체를 사용해 apply 메서드를 재정의 했었다. 

```java
public enum Operation {
    PLUS("+") { public double apply(double x, double y) {return x + y;} },
    MINUS("-") { public double apply(double x, double y) {return x + y;} },
    TIMES("*") { public double apply(double x, double y) {return x + y;} },
    DIVIDE("/") { public double apply(double x, double y) {return x + y;} };

    private final String symbol;

    Operation3(String symbol) {
        this.symbol = symbol;
    }

    // 상수가 뜻하는 연산을 수행한다.
    public abstract double apply(double x, double y);

    public String toString() {
        return this.symbol;
    }
}
```

이때 상수별 클래스 몸체를 구현하는 방식보다는 열거 타입에 인스턴스 필드를 두는 편이 낫다고 했다. 람다를 이용하면 후자의 방식이 가능하다.   
즉, 열거 타입의 인스턴스 필드를 이용하는 방식으로 상수별로 다르게 동작하는 코드를 구현할 수 있다.

생성자는 상수별로 선언한 람다를 생성자에서 인스턴스 필드에 저장해둔다. 그런 다음 추상 메서드가 아닌 일반 apply 메서드에서 해당 필드에 저장된 람다를 호출하기만 하면 된다.

```java
public enum Operation {
    PLUS("+", (x, y) -> x + y),
    MINUS("-", (x, y) -> x - y),
    TIMES("*", (x, y) -> x * y),
    DIVIDE("/", (x, y) -> x / y);

    private final String symbol;
    private final DoubleBinaryOperator op;

    Operation(String symbol, DoubleBinaryOperator op) {
        this.symbol = symbol;
        this.op = op;
    }

    // 상수가 뜻하는 연산을 수행한다.
    public double apply(double x, double y) {
        return op.applyAsDouble(x, y);
    }

    public String toString() {
        return this.symbol;
    }
}
```

람다 기반 Operation 열거 타입을 보면 상수별 클래스 몸체는 더 이상 사용할 이유가 없다고 느낄지 모르지만 꼭 그렇지는 않다.

> 메서드나 클래스와 달리 람다는 이름이 없고 문서화도 못한다.

따라서 코드 자체로 동작이 명확히 설명되지 않거나 코드 줄 수가 많아지면 람다를 쓰지 말아야 한다.

람다는 한 줄일 때 가장 좋고 길어도 세줄 안에는 끝내는 것이 좋다. 그 이상이 넘어가면 가독성이 나빠진다.   
람다가 길거나 가독성이 나쁘다면 람다를 사용하지 않는 쪽으로 리팩터링해보자.

열거 타입 생성자에 넘겨지는 인수들의 타입도 컴파일타임에 추론된다. 즉, 열거 타입 생성자 안의 람다는 열거 타입의 인스턴스에 접근할 수 없다.

따라서 상수별 동작을 몇 줄 안에 끝낼 수 없거나, 인스턴스 필드나 메서드가 필요한 상황이라면 람다 대신 상수별 클래스 몸체를 쓰는것이 더 좋다.

### 람다로 대체할 수 없는 곳

람다로 대체할 수 없는 곳도 있다.
* 추상 클래스의 인스턴스를 만들 때
* 추상 메서드가 여러개인 인터페이스의 인스턴스를 만들 때
* 람다는 자신을 참조할 수 없고 람다 내부의 this 키워드는 바깥의 인스턴스를 의미한다.
  * 익명 클래스의 this는 익명클래스 자신을 의미한다.


