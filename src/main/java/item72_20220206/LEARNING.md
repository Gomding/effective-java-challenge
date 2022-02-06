# 아이템72. 표준 예외를 사용하라

이미 있는 코드를 재사용하는것이 좋은것처럼, 예외도 재사용하는 것이 좋다.   
자바 라이브러리는 대부분 API에서 쓰기에 충분한 수의 표준 예외를 제공한다.

### 표준 예외

표준 예외를 사용하면 얻는 게 많다.
* 우리의 API가 다른 사람이 익히고 사용하기 쉬워진다. 자바 프로그래머에게 이미 익숙해진 규약을 그대로 따르기 떄문이다. (이름과 예외 상황이 익숙함)   
* 낯선 예외를 사용하지 않게 되어 읽기 쉽다는 장점도 있다.
* 예외 클래스 수가 적을수록 메모리 사용량도 줄고 클래스를 적재하는 시간도 적게 걸린다.

### IllegalArgumentException

가장 많이 재사용되는 예외는 IllegalArgumentException이다.

호출자가 인수로 부적절한 값을 넘길 때 던지는 예외로, 예를 들어 반복 횟수를 지정하는 매개변수에 음수를 건넬 때 쓸 수 있다.

```java
public void somethingLoop(int numberOfRepeat) {
    if (numberOfRepeat < 0) {
        throw new IllegalArgumentException("반복 횟수는 0보다 작을 수 없습니다. numberOfRepeat : " + numberOfRepeat);
    }
    
    for (int i = 0; i < numberOfRepeat; i++) {
        // ...
    }
}
```

### IllegalStateException

자주 재사용되는 예외로 IllegalStateException이 있다.

이 예외는 대상 객체의 상태가 호출된 메서드를 수행하기에 적합하지 않은 상태에 있을 때 주로 던진다.   
예컨대 제대로 초기화되지 않은 객체를 사용하려 할 때 던질 수 있다.

```java
class Door {
    boolean isOpen;
    
    public void open() {
        if (isOpen) {
            throw new IllegalStateException("문이 이미 열려있습니다.");
        }
    }
}
```

### NullPointerException

메서드가 던지는 모든 예외를 잘못된 인수나 상태라고 묶어버릴 수 있지만, 특수한 경우는 따로 구분해 쓰는 게 보통이다. (더욱 명확한 예외)

null 값을 허용하지 않는 메서드에 null을 건네면 관례상 IllegalArgumentException이 아닌 NullPointerException을 던진다.

### IndexOutOfBoundException

IndexOutOfBoundException는 시퀸스의 허용 범위를 넘는 값을 사용할 때 던지는 특수한 예외다.

### ConcurrentModificationException

재사용하기 좋은 또 다른 예외인 ConcurrentModificationException은 단일 스레드에서 사용하려고 설계한 객체를 여러 스레드가 동시에 수정하려 할 때 던진다.

(외부 동기화 방식으로 사용하려고 설계한 객체도 마찬가지다)

사실 동시 수정을 확실히 검출할 수 있는 안정된 방법은 없으니, 이 예외는 문제가 생길 가능성을 알려주는 정도의 역할로 쓰인다.

### UnsupportedOperationException

UnsupportedOperationException은 클라이언트가 요청한 동작을 대상 객체가 지원하지 않을 때 던진다.

대부분 객체는 자신이 정의한 메서드를 모두 지원하니 흔히 쓰이는 예외는 아니다.

자주 사용되는 상황은 구현하려는 인터페이스의 메서드 일부를 구현할 수 없을 때 쓰인다.

예컨대 원소의 조회만 지원하는 List 구현체에 대고 누군가 원소를 추가하는 add 메서드를 호출하면 이 예외를 던질 것이다.

가장 좋은 예시로 Collections.unmodifiableList() 메서드가 반환하는 타입인 UnmodifiableList가 있다. 

```java
// UnmodifiableList의 add 메서드
public void add(int index, E element) {
    throw new UnsupportedOperationException();
}
```

### 넓은 범위의 예외를 재사용하지 말자.

Exception, RuntimeException, Throwable, Error는 직접 재사용하지 말자.    
이 클래스들은 추상 클래스라고 생각하길 바란다.

이 예외들은 다른 예외들의 상위 클래스이므로, 여러 성격의 예외들을 포괄하는 클래스다. 따라서 안정적으로 예외 상황을 테스트할 수 없다.   
예를들어 RuntimeException 을 던지도록 구현했다면 테스트 시 RuntimeException의 다른 하위 클래스가 던져졌을 때도 테스트가 통과할 것이다.

### 자주 사용되는 표준 예외 정리

* IllegalArgumentException : 허용하지 않는 값이 인수로 건네졌을 때
* NullPointerException : null을 허용하지 않는 메서드에 null을 건넸을 때
* IllegalStateException : 객체가 메서드를 수행하기에 적절하지 않은 상태일 때
* IndexOutOfBoundsException : 인덱스가 범위를 넘어섰을 때
* ConcurrentModificationException : 허용하지 않는 동시 수정이 발견됐을 때
* UnsupportedOperationException : 호출한 메서드를 지원하지 않을 때

### 이외의 예외

다른 상황에 대한 표준 예외도 존재한다.

예를 들어 복소수나 유리수를 다루는 객체를 작성한다면 ArithmeticException 이나 NumberFormatException을 사용할 수 있다.

상황만 부합하면 항상 표준 예외를 재사용하자. 
이때 API문서를 참고해 그 예외가 어떤 상황에서 던져지는지 꼭 확인해야 한다.
예외의 이름뿐 아니라 예외가 던져지는 맥락도 부합할 때 재사용한다.

### 예외의 확장

표준 예외를 확장해도 좋다. 단, 예외는 직렬화할 수 있다는 사실을 기억하자. 직렬화는 부담이 따르니 이것만으로 나만의 예외를 새로 만들지 않아야 할 근거로 충분할 수 있다.

### 예외 선택에 헷갈리는 상황

종종 재사용할 예외를 선택하기가 어려울 때도 있다.

예를 들어 카드 덱을 표현하는 객체가 있고, 인수로 건넨 수만큼의 카드를 뽑아 나눠주는 메서드를 제공한다고 해보자.

이때 덱에 남아 있는 카드 수보다 큰 값을 건네면 어떤 예외를 던져야 할까?   
인수의 값이 너무 적다고 본다면 IllegalStateException을 선택할 것이다.

**일반적인 규칙은 이렇다**
* 인수값이 무엇이었든 어차피 실패했을 거라면 IllegalStateException
* 인수값에 따라 성공과 실패가 나뉜다면 IllegalArgumentException