# 아이템49. 매개변수가 유효한지 검사하라

대부분의 메서드와 생성자는 입력 매개변수의 값이 특정 조건을 만족하길 바란다. 예를들면 다음과 같다.

* 요구사항에 대한 제약조건. 
* 로또 번호는 1 ~ 45 사이의 값이어야 한다.
* 인덱스 값은 음수이면 안 된다. 
* 객체 참조는 null이 아니어야 한다.

이런 제약 조건은 반드시 문서화해야 하며 메서드 몸체가 시작되기 전에 검사해야 한다.   
이는 "**오류는 가능한 한 빨리 (발생한 곳에서) 잡아야 한다**"는 일반 원칙의 한 사례이기도 하다.   

> 오류를 발생한 즉시 잡지 못하면 해당 오류를 감지하기 어려워지고,   
> 감지하더라도 오류의 발생 지점을 찾기 어려워진다.

### 매개변수 검사를 제대로 수행하지 않는다면?

메서드 몸체가 실행되기 전에 매개변수를 확인한다면 잘못된 값이 넘어왔을 때 즉각적이고 깔끔한 방식으로 예외를 던질 수 있다.

```java
public class PositiveNumberCalculator {
    // 매개변수로 양수만을 받는 더하기 메서드다.
    public int sum(int a, int b) {
        // 매개변수 a, b가 양수인지 검사하고, 양수가 아니라면 예외를 던진다.
        if (!isPositiveNumber(a) || !isPositiveNumber(b)) {
            throw new IllegalArgumentException("a 와 b는 1이상의 양수여야 한다.");
        }
        return a + b;
    }
    
    private boolean isPositiveNumber(int number) {
        return number >= 1;
    }
}
```

매개변수 검사를 제대로 하지 못하면 몇가지 문제가 생길 수 있다.
* 매개변수 검사에 실패하면 **실패 원자성**을 어기는 결과를 낳을 수 있다. (아이템 76)
  * 메서드가 수행되는 중간에 모호한 예외를 던지며 실패할 수 있다.
  * 메서드가 잘 수행되지만 잘못된 결과를 반환할 수 있다.
  * 메서드는 문제없이 수행됐지만, 어떤 객체를 이상한 상태로 만들어 놓아서 먼 미래의 알 수 없는 시점에 이 메서드와 관련없는 오류를 낼 때다.

### public과 protected 메서드는 매개변수 값이 잘못됐을 때 던지는 예외를 문서화해야한다.

@throws 자바독 태그를 활용하면 던지는 예외를 문서화할 수 있다.   
이때 던지는 예외는 보통 IllegalArgumentException, IndexOutOfBoundsException, NullPointerException 중 하나가 될 것이다.

매개변수 제약을 문서화한다면, 그 제약을 어겼을 때 발생하는 예외도 함께 기술해야 한다.   
이런 간단한 방법으로 API 사용자가 제약을 지킬 가능성을 크게 높일 수 있다.

다음은 전형적인 BigInteger의 내부 구현 메서드인 mod()의 코드다.

```java
/**
 * (현재 값 mod m) 값을 반환한다.
 * 이 메서드는 항상 음이 아닌 BigInteger를 반환한다는 점에서 remainder 메서드와 다르다.
 *
 * @param m 계수(양수여야 한다.)
 * @return 현재 값 mod m
 * @throws ArithmeticException m이 0보다 작거나 같으면 발생한다.
 */
public BigInteger mod(BigInteger m){
        if(m.signum()<=0)
        throw new ArithmeticException("계수(m)는 양수여야 합니다. "=m)

        ...
}
```

### 클래스 수준 주석

위에서 BigInteger의 mod 메서드는 m이 null이면 m.signum() 호출 때 NullPointerException을 던진다.   
그런데 "m이 null일 때 NullPointerException을 던진다."라는 말은 메서드 설명에 없다.   
그 이유는 이 설명을 개별 메서드 수준이 아닌 BigInteger 클래스 수준에서 기술했기 때문이다.

```java
// 아래는 BigInteger의 클래스 명세에 있는 내용이다.
/**
 *  <p>All methods and constructors in this class throw
 * {@code NullPointerException} when passed
 * a null object reference for any input parameter.
 */
```

클래스 수준 주석은 그 클래스의 모든 public 메서드에 적용되므로 각 메서드에 일일이 기술하는 것보다 훨씬 깔끔한 방법이다.   

@Nullable이나 이와 비슷한 애너테이션을 사용해 특정 매개변수는 null이 될 수 있다고 알려줄 수 있지만, 표준적인 방법은 아니다.   
또한 같은 목적으로 사용할 수 있는 애너테이션도 여러 가지다.

### 자바 7부터 추가된 java.util.Objects.requireNonNull 메서드

Objects의 requireNonNull 메서드는 유연하고 사용하기도 편하다.   
더 이상 null 검사를 수동으로 하지 않아도 된다. 원하는 예외 메세지까지 지정할 수 있다.   
또 **입력을 그대로 반환**(null이 아니라면)하므로 값을 사용하는 동시에 null 검사를 수행할 수 있다.

```java
// requireNonNull 메서드 활용하기
// null 검사와 동시에 입력 값을 그대로 반환하므로 아래와 같이 사용할 수 있다. 
this.strategy = Objects.requireNonNull(strategy, "전략");
```

반환값은 그냥 무시하고 필요한 곳 어디서든 순수한 null 검사 목적으로 사용해도 된다.

자바 9에서는 Objects의 범위 검사 기능도 더해졌다.   
checkFromIndexSize, checkFromToIndex, checkIndex라는 메서드들이 그 주역이다. null 검사 메서드만큼 유연하지는 않다.   

* 예외 메세지를 지정할 수 없다
* 리스트와 배열 전용으로 설계됐다. 
* 닫힌 범위(closed range; 양 끝단 값을 포함하는, >=, <=)는 다루지 못한다.

이런 제약사항이 걸림돌이 되지 않는 상황이라면 상당히 유용하고 편하다.

### 공개되지 않은 메서드라면 패키지 제작자인 우리가 메서드가 호출되는 상황을 통제할 수 있다.

우리가 메서드가 호출되는 상황을 통제할 수 있다는 의미는 **오직 유효한 값만이 메서드에 넘어올 것이라는걸 보증할 수 있다**. 또 그렇게 해야하는 것이다.   
다시 말해 public이 아닌 메서드라면 단언문(assert)을 사용해 매개변수 유효성을 검증할 수 있다.

> java의 assert 키워드는?
> assert는 자신이 단언한 조건이 무조건 참이라고 선언하는 것이다. 조건아 참이 아니라면 AssertionException을 던진다.
> assert 키워드는 개발/테스트 단계에서 파라미터가 제대로 넘어왔는지 검사하는것이다.   
> 이 말은 실제 실행 단계에서는 성능 저하가 없다는 의미다.   
> (런타임 성능 저항이 없다. 런타임에는 특정 JVM의 옵션을 사용한게 아니라면 해당 키워드는 무시하고 지나간다.)

```java
private static void sort(long[], int offset, int length) {
    assert a != null;
    assert offset >= 0 && offset <= a.length;
    assert length >= 0 && length <= a.length - offset;
    
    // ... 정렬 수행
}
```

assert는 몇 가지 면에서 일반적인 유효성 검사와 다르다.
1. 실패하면 AssertionException을 던진다.
2. 런타임에 아무런 효과도, 성능 저하도 없다. (단, JVM 실행 옵션으로 -ea 혹은 --enableassertions 플래그를 설정하면 런타임에도 영향을 준다. IntelliJ 같은 경우 기본적으로 해당 옵션이 켜져있다.)

### 메서드가 직접 사용하지는 않으나 나중에 쓰기 위해 저장하는 매개변수는 특히 더 신경써서 검사하자

정적 팩터리 메서드를 생각해보면 매개변수를 받아서 객체를 생성해 반환한다.   
그렇다면 잘못된 매개변수로 잘못된 객체를 반환한 상황을 상상해보자. 잘못된 객체를 반환받고 이를 사용하려 할 때 예외가 발생할 것이다.   
이는 해당 객체를 어디서 가져왔는지 추적하기 어려워 디버깅이 힘들어질 것이다.

### 생성자에서 매개변수의 유효성을 검사하라

생성자는 "나중에 쓰려고 저장하는 매개변수의 유효성을 검사하라"는 원칙의 특수한 사례다.

생성자 매개변수의 유효성 검사는 클래스 불변식을 어기는 객체가 만들어지지 않게 하는 데 꼭 필요하다.   
양수를 가지는 클래스의 예시를 보자.

```java
public class PositiveNumber {
    private final int value;

    public PositiveNumber(int value) {
        if (value < 1) {
            throw new IllegalArgumentException("양수의 값은 1보다 커야합니다.");
        }
        this.value = value;
    }
}
```

### 메서드 몸체 실행 전에 매개변수 유효성을 검사해라 라는 규칙에도 예외는 있다.

유효성 검사 비용이 지나치게 높거나 실용적이지 않을 때, 혹은 계산 과정에서 암묵적으로 검사가 수행될 때다.

예를 들어 Collections.sort(List)처럼 객체 리스트를 정렬하는 메서드를 생각해보자.
리스트 안의 객체들은 모두 상호 비교될 수 있어야 하며, 정렬 과정에서 이 비교가 이뤄진다.   
상호 비교할 수 없는 타입의 객체가 들어있다면? ClassCastException을 던질 것이다.

따라서 정렬 기준으로 요소들을 비교하기에 앞서 리스트 안의 모든 객체가 상호 비교될 수 있는지 검사해봐야 큰 이익은 없다.   

> 하지만 암묵적 유효성 검사에 너무 의존하다가는 실패 원자성(아이템76)을 해칠 수 있으니 주의해서 사용하자.

### 때로는 필요한 유효성 검사가 이뤄지지만 실패했을 때 잘못된 예외를 던지기도 한다.

이는 잘못된 매개변수로 인해 발생한 예외가 API 문서에서 던지기로 한 예외와 다를 수 있다는 뜻이다.   
이런 경우 아이템 73에서 설명하는 예외 번역 관용구를 사용하여 API 문서에 기재된 예외로 번역해줘야 한다.

```java
// 상위 계층에서는 저수준 예외를 잡아 자신의 추상화 수준에 맞는 예외로 바꿔 던져야 한다. 이를 예외 번역이라 한다.
try {
        ...
} catch (LowerLevelException e) {
    throw new HigherLevelException(...);
}
```

### 정리

이번 아이템을 "매개변수에 제약을 두는 게 좋다"고 해석해서는 안 된다.   
메서드는 제약조건을 지키면서 최대한 범용적으로 설계해야한다.   
메서드가 건네받은 값으로 무언가 제대로 된 일을 할 수 있다면 매개변수 제약조건은 적을수록 좋다.   
하지만 구현하라는 개념 자체가 특정한 제약을 내재한 경우도 드물지 않다.

#### 최종 정리

메서드나 생성자를 작성할 때면 그 매개변수들에 어떤 제약이 있을지 생각해야 한다.   
public이나 protected 메서드라면 제약들을 문서화하고 메서드 코드 시작 부분에서 명시적으로 검사해야 한다.   
이런 습관은 유효성 검사가 실제 오류를 처음 걸러낼 때 충분히 보상받을 수 있다.