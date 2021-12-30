# 아이템38. 확장할 수 있는 열거 타입이 필요하면 인터페이스를 사용하라

열거 타입은 확장이 불가능하다는 단점이 있다.

> 열거 타입은 Enum 클래스를 암묵적으로 상속받는다.   
> 컴파일된 바이트 코드를 살펴보면 아래와 같이 extend Enum 의 코드가 있는것을 확인할 수 있다.

```java
// BasicOperation의 바이트 코드
public abstract enum item38_20211230/BasicOperation extends java/lang/Enum
```

열거 타입 대용으로 사용할 수 있는 패턴으로 타입 안전 열거 패턴이 있습니다(typesafe enum pattern).

계산기 연산 기능의 열거 타입을 타입 안전 열거 패턴으로 표현하면 아래와 같다.

```java
class Operation {
    public static final Operation PLUS = new Operation("+") {
        public double apply(double x, double y) { return x + y; }
    };
    public static final Operation MINUS = new Operation("-") {
        public double apply(double x, double y) { return x - y; }
    };
    public static final Operation TIMES = new Operation("*") {
        public double apply(double x, double y) { return x * y; }
    };
    public static final Operation DIVIDE = new Operation("/") {
        public double apply(double x, double y) { return x / y; }
    };


    private final String symbol;

    public Operation(String symbol) {
        this.symbol = symbol;
    }
}
```

타입 안전 열거 패턴은 클래스로 구현했으므로 열거한 값들을 그대로 가져온 다음 값을 더 추가하여 다른 목적으로 사용이 가능하다.

반면, **열거 타입은 그렇게 할 수 없다**.

사실 대부분의 상황에서 열거 타입을 확장하는건 좋지 않은 생각이다.   
확장한 타입의 원소는 기반 타입의 원소로 취급되지만 그 반대는 성립하지 않는다면 이상하지 않은가!

```java
public static void main(String[]args){
    // 문제없이 실행된다.
    doSomething(ExtendedOperation.EXP);
    doSomething(Operation.PLUS);
}

// 부모 타입으로 매개변수를 받으면 전부 수용할 수 있다.
public void doSomething(Operation operation) {
        ...
}

// 잘못 사용된 예시
public static void main(String[]args){
    doSomething(ExtendedOperation.EXP); // ExtendedOperation원소를 넘겼으므로 정상 동작한다.
    // doSomething(Operation.PLUS); // Operation은 ExtendedOperation의 부모 타입으로 오류가 발생한다.
}

public void doSomething(ExtendedOperation operation) {
        ...
}
```

또한 기반 타입과 확장된 타입들의 원소를 모두 순회할 방법도 마땅치 않다.   
마지막으로, 확장성을 높이려면 고려할 요소가 늘어나 설계와 구현이 더 복잡해진다.

확장할 수 있는 열거 타입이 어울리는 경우가 있다. 예를들어 위에서 설명한 연산 코드다.   
이따금 API가 제공하는 기본 연산 외에 사용자 확장 연산을 추가할 수 있도록 열어줘야 할 때가 있다.

열거 타입도 확장하는 효과를 내는 멋진 방법이 있다.

> 열거 타입이 임의의 인터페이스를 구현할 수 있다는 사실을 이용하는 것이다.

연산 코드용 인터페이스를 정의하고 열거 타입이 이 인터페이스를 구현하게 하면 된다.   
**이때 열거 타입이 인터페이스의 표준 구현체 역할을 한다.**

```java
public interface OperationInterface {
    double apply(double x, double y);
}

public enum BasicOperation implements OperationInterface {
    PLUS("+") {
        @Override
        public double apply(double x, double y) {
            return x + y;
        }
    },
    MINUS("-") {
        @Override
        public double apply(double x, double y) {
            return x - y;
        }
    },
    TIMES("*") {
        @Override
        public double apply(double x, double y) {
            return x * y;
        }
    },
    DIVIDE("/") {
        @Override
        public double apply(double x, double y) {
            return x / y;
        }
    };
    
    private final String symbol;

    BasicOperation(String symbol) {
        this.symbol = symbol;
    }
}
```

열거 타입인 BasicOperation은 확장할 수 없지만 인터페이스인 Operation은 확장할 수 있고, 이 인터페이스를 연산의 타입으로 사용하면 된다.

Operation을 구현한 또 다른 열거 타입을 정의해 다른 열거 타입으로 대체할 수 있다.

연산 타입을 확장해 지수 연산(EXP)와 나머지 연산(REMAINDER)를 추가해보자.

```java
public enum ExtendedOperation implements OperationInterface {
    EXP("^") {
        public double apply(double x, double y) {
            return Math.pow(x, y);
        }
    },
    REMAINDER("%") {
        @Override
        public double apply(double x, double y) {
            return x % y;
        }
    };
    
    private final String symbol;

    ExtendedOperation(String symbol) {
        this.symbol = symbol;
    }
}
```

새로 작성한 연산은 기존 연산을 쓰던 곳이면 어디든 쓸 수 있다. (Operation 인터페이스를 사용하도록 작성만 되어 있다면)

아래 예시는 테스트 프로그램을 가져와 ExtendedOperation의 모든 원소를 테스트하도록 수정한 모습이다.

```java
public static void main(String[]args){
    double x = Double.parseDouble(3.0d);
    double y = Double.parseDouble(2.0d);
    test(ExtendedOperation.class, x, y);
}

private static <T extends Enum<T> & OperationInterface> void test(Class<T> opEnumType, double x, double y) {
    for (Operation op : opEnumType.getEnumConstants())
        System.out.printf("%f %s %f = %f%n", x, op, y, op.apply(x, y));
}
```

main 메서드는 test 메서드에 ExtendedOperation의 class 리터럴을 넘겨 확장된 연산들이 무엇인지 알려준다.

여기서 class 리터럴은 한정적 타입 토큰 역할을 한다.

```<T extends Enum<T> & OperationInterface>```는 Class 객체가 열거 타입임과 동시에 OperationInterface의 하위타입이어야 한다는 뜻이다.

열거 타입이어야 원소를 순회할 수 있고, OperationInterface 이어야 원소가 뜻하는 연산을 수행할 수 있기 때문이다.

Class 객체 대신 한정적 와일드카드 타입을 이용하는 방법도 있다.

```java
public static void main(String[]args){
    double x = Double.parseDouble(3.0d);
    double y = Double.parseDouble(2.0d);
        test(Arrays.asList(ExtendedOperation.values()), x, y);
}

private static void test(Collection<? extends OperationInterface> opSet, double x, double y) {
    for (Operation op : opSet)
        System.out.printf("%f %s %f = %f%n", x, op, y, op.apply(x, y));
}
```

한정적 타입 토큰을 활용하는 대신 ```Collection<? extends OperationInterface>```를 넘기는 방법이다.

이 코드는 그나마 덜 복잡하고 test 메서드가 살짝 더 유연하다.

다시 말해 여러 구현 타입의 연산을 조합해 호출할 수 있게 되었다. 하지만 특정 연산에서 EnumSet과 EnumMap을 사용하지 못한다.

### 인터페이스를 활용해 확장 가능한 열거 타입을 흉내 내는 방식의 단점

열거 타입끼리 구현을 상속할 수 없다는 점이 있다.

아무 상태에도 의존하지 않는 경우에는 default 메서드 구현을 통해 해결하는 방법이 있다.

OperationInterface 의 예시는 연산 기호를 저장하고 찾는 로직이 BasicOperation과 ExtendedOperation 모두에 들어가야만 한다.

이 경우 별도의 도우미 클래스나 정적 도우미 메서드로 분리하는 방식으로 코드 중복을 없앨 수 있을 것이다.