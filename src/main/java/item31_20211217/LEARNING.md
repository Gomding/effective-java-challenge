# 아이템31. 한정적 와일드카드를 사용해 API 유연성을 높이라

매개변수화 타입은 불공변(invariant)이다.

즉, ```List<String>```은 ```List<Object>```의 상위 타입도 하위타입도 아니다.

```List<Object>```에는 어떤 객체든 넣을 수 있지만 ```List<String>```에는 문자열만 넣을 수 있다.   
```List<Object>```가 하는 일을 ```List<String>```이 제대로 수행하지 못하니 하위 타입이 될 수 있다.(리스코프 치환 원칙에 어긋난다.)

> 하지만 때론 불공변 방식보다 유연한 무언가가 필요하다.

Stack 클래스를 예로 들어보자.

```java
public class Stack<E> {
    public Stack();
    public void push(E e);
    public E pop();
    public boolean isEmpty();
}
```

여기에 일련의 원소를 스택에 넣는 메서드를 추가해야 한다고 해보자.

```java
public void pushAll(Iterable<E> src) {
    for (E e : src) {
        push(e);
    }
}
```

이 메서드는 컴파일 되지만 완벽하지 않다.    
Iterable src의 원소 타입이 스택의 원소 타입과 일치하면 잘 동작한다.
하지만 ```Stack<Number>```로 선언한 후 pushAll(intVal)을 호출하면 어떻게 동작할까? (여기서 intVal은 Integer 타입이다.)

Integer는 Number의 하위 타입이니 논리적으로 잘 동작해야 할 것 같다. 하지만 오류가 발생한다.

```java
public class MyStack<E> {
    private E[] elements;
    private int size = 0;
    public static final int DEFAULT_INITIAL_CAPACITY = 16;

    @SuppressWarnings("unchecked")
    public MyStack() {
        elements = (E[]) new Object[DEFAULT_INITIAL_CAPACITY];
    }
    
    ...

    public void pushAll(Iterable<E> src) {
        for (E e : src)
            push(e);
    }
}

public static void main(String[] args) {
    MyStack<Number> numberMyStack = new MyStack<>();
    List<Integer> numbers = Arrays.asList(1, 2, 3, 4);
    numberMyStack.pushAll(numbers);
}

// 실행시 오류 메세지
incompatible types: List<Integer> cannot be converted to Iterable<Number>
        numberMyStack.pushAll(numbers);
```

논리적인것 같음에도 오류 메시지가 뜨는 이유는 매개변수화 타입이 불공변이기 때문이다.

자바는 이런 상황에 대처할 수 있는 한정적 와일드카드 타입이라는 특별한 매개변수화 타입을 지원한다.   
pushAll 메서드의 입력 매개변수 타입은 'E의 Iterable'이 아니라 'E의 하위 타입의 Iterable'이어야 하며, 

> ```와일드 카드 타입 Iterable<? extends E>```가 정확히 이런 뜻이다.

이제 와일드카드 타입을 사용하도록 pushAll 메서드를 수정해보자

```java
public void pushAll(Iterable<? extends E> src) {
    for (E e : src)
        push(e)
}
```

이번 수정으로 Stack은 물론 이를 사용하는 클라이언트 코드도 말끔히 컴파일된다.

다음으로 popAll 메서드를 작성할 차례다. popAll 메서드는 Stack 안의 모든 원소를 주어진 컬렉션으로 옮겨 담는다.

```java
public void popAll(Collection<E> dst) {
    while (!isEmpty())
        dst.add(pop());
}
```

이전의 pushAll의 오류처럼 popAll도 컬렉션 원소 타입과 스택의 원소 타입이 일치하면 깔끔하게 컴파일된다.   
하지만 ```Stack<Number>```의 원소를 ```List<Object>```로 옮기려하면 컴파일 오류가 발생한다.

```java
public static void main(String[] args) {
// popAll 메서드 사용 예제
MyStack<Number> numberMyStack2 = new MyStack<>();
numberMyStack2.pushAll(numbers);
List<Object> objects = new ArrayList<>();
numberMyStack2.popAll(objects);
}

// 실행 시 오류
error: incompatible types: List<Object> cannot be converted to Collection<Number>
numberMyStack2.popAll(objects);
                      ^
```

이번에도 와일드카드 타입으로 해결할 수 있다.

popAll의 입력 매개변수의 타입이 'E의 Colllection'이 아니라 'E의 상위 타입의 Collection' 이어야 한다.(모든 타입은 자기 자신의 상위 타입이다.)

> 와일드 카드 타입을 사용한 ```Collection<? super E>```가 정확히 이런 의미이다.

```java
public void popAll(Collection<? super E> dst) {
    while (!isEmpty())
        dst.add(pop());
}
```

> 핵심은 '유연성을 극대화하려면 원소의 생산자나 소비자용 입력 매개변수에 와일드카드 타입을 사용하라' 이다.

한편, 입력 매개변수가 생성자와 소비자 역할을 동시에 한다면 와일드카드 타입을 써도 좋을 게 없다. 이는 타입을 정확히 지정해야 하는 상황이다. **이때는 와일드카드 타입을 쓰지 말아야 한다.**

### 와일드카드 타입을 사용하는 기본 원칙 PECS

다음 공식을 외워두면 어떤 와일드카드 타입을 써야하는지 기억하는 데 도움이 될 것이다.

> PECS : producer-extends, consumer-super

* 매개변수화 타입 T가 생산자라면 ```<? extends T>```를 사용
* 매개변수화 타입 T가 소비자라면 ```<? super T>```를 사용

이전 Stack의 예시와 PECS를 연결해보면 다음과 같다.

### producer-extends
```java
public void pushAll(Iterable<? extends E> src) {
    for (E e : src)
        push(e) // src가 Stack 내부의 인스턴스를 생산하고 있다. 즉, src는 생산자이다.
}
```

pushAll의 src 매개변수는 Stack이 사용할 E 인스턴스를 생산하므로 src의 적절한 타입은 ```Iterable<? extends E>```이다.

### consumer-super
```java
public void popAll(Collection<? super E> dst) {
    while (!isEmpty())
        dst.add(pop()); // dst는 Stack의 인스턴스들을 소비시킨다. 즉 dst는 소비자다.
}
```

popAll의 dst 매개변수는 Stack으로부터 E 인스턴스를 소비하므로 dst의 적절한 타입은 ```Collection<? super E>```이다.

> PECS 공식은 와일드카드 타입을 사용하는 기본 원칙이다.

앞장의 아이템28의 Chooser 생성자는 다음과 같이 선언했다.
```java
public Chooser(Collection<T> choice)
```

이 생성자로 넘겨지는 choice 컬렉션은 T 타입의 값을 생산하기만 하니, T를 확장하는 와일드카드 타입을 사용해 선언해야 한다.

```java
public Chooser(Collection<? extends T> choice)
```

```Chooser<Number>```의 생성자에 ```List<Integer>```를 넘기고 싶다고 해보자.   
수정전에는 컴파일 오류가 발생하지만, 수정 후에는 문제가 사라진다.

아이템 30의 union 메서드도 바꿔보자

```java
public static <E> Set<E> union(Set<E> s1, Set<E> s2)
```

s1 과 s2 모두 E의 생산자이니 PECS 공식에 따라 다음처럼 선언해야 한다.

```java
public static <E> Set<E> union(Set<? extends E> s1, Set<? extends E> s2)
```

반환 타입은 여전히 ```Set<E>```임에 주목해야한다. 반환 타입에는 한정적 와일드카드 타입을 사용하면 안 된다. 이는 유연성을 높여주기는 커녕 클라이언트 코드에서도 와일드카드 타입을 써야 하기 때문이다.

와일드카드 타입을 제대로 사용한다면 클래스 사용자는 와일드 카드 타입이 쓰였다는 사실조차 의식하지 못할 것이다. 받아들여야 할 매개변수를 받고 거절해야 할 매개변수는 거절하는 작업이 알아서 이뤄진다.

> 클래스 사용자가 와일드카드 타입을 신경 써야 한다면 그 API는 문제가 있다는 신호일 수 있다.

### 입력 매개변수에 적용한 한정 와일드카드 타입

이번에는 아이템 30의 max 메서드를 보자.

```java
public static <E extends Comparable<E>> E max(List<E> list)
```

다음은 와일드카드 타입을 사용해 리팩터링한 모습이다.

```java
public static <E extends Comparable<? super E>> E max(List<? extends E> list)
```

이번에는 PECS 공식이 두번 적용됐다. 입력 매개변수 목록, 타입 매개변수 두 곳이다.

타입 매개변수 쪽을 살펴보자. 원래 선언에서는 E가 ```Comparable<E>```를 확장한다고 정의했는데, 이때 ```Comparable<E>```는 E 인스턴스를 소비한다. (그리고 선후 관계를 뜻하는 정수를 생산한다.)

Comparable은 언제나 소비자이므로, 일반적으로 ```Comparable<E>``` 보다는 ```Comparable<? super E>```를 사용하는 편이 낫다.

다음의 List는 오직 수정된 max로만 처리할 수 있다.

```java
List<ScheduledFuture<?>> scheduledFutures = ...;
```

수정 전 max가 이 리스트를 처리할 수 없는 이유는 (java.util.concurrent 패키지의) ScheduledFuture가 ```Comparable<ScheduledFuture>```를 구현하지 않았기 때문이다.

아래와 같이 구현되어 있다.
```java
public interface Comparable<E>
public interface Delayed extends Comparable<Delayed>
public interface ScheduledFuture<V> extends Delayed, Future<V>
```

ScheduledFuture의 인스턴스는 다른 ScheduledFuture 인스턴스뿐 아니라 Delayed 인스턴스와도 비교할 수 있어서 수정 전 max가 이 리스트를 거부하는 것이다.

> Comparable(혹은 Comparator)를 직접 구현하지 않고, 직접 구현한 다른 타입을 확장한 타입을 지원하기 위해 와일드 카드가 필요하다.

### 타입 매개변수와 와일드카드

타입 매개변수와 와일드카드에는 공통되는 부분이 있어서, 메서드를 정의할 때 둘 중 어느 것을 사용해도 괜찮을 때가 많다.

예를 들어 주어진 리스트에서 명시한 두 인덱스의 아이템들을 교환하는 swap 정적 메서드를 두 방식 모두로 정의해보자.

```java
public static <E> void swap(List<E> list, int i, int j); // 비한정적 타입 매개변수
public static void swap(List<?> list, int i, int j); // 비한정적 와일드카드
```

어떤 선언이 더 날을까?, 더 나은 이유는 무엇일까?

public API라면 두 번째 비한정적 와일드카드를 사용하는 것이 낫다. 어떤 리스트든 이 메서드에 넘기면 명시한 인덱스의 원소들을 교환해 줄 것이다. 신경 써야 할 매개변수도 없다.

기본 규칙은 ```메서드 선언에 타입 매개변수가 한 번만 나오면 와일드카드로 대체하라``` 이다. 
* 이때 비한정적 타입 매개변수라면 비한정적 와일드카드로 바꾸고, 한정적 타입 매개변수라면 한정적 와일드카드로 바꾸면 된다.

사실 두 번째 swap 메서드 (비한정적 와일드카드)에는 문제가 하나 있다.

```java
public static void swap(List<?> list, int i, int j) {
    list.set(i, list.set(j, list.get(i))); // 컴파일 오류 발생
}
```

리스트에서 꺼낸 원소를 리스트에 다시 넣을 수 없는 상황이 발생한다. 이는 ```List<?>```이기 때문에 null 외에는 어떤 값도 넣을 수 없다는 데 있다.

이를 해결하는 방법으로 **private Helper 메서드**를 활용할 수 있다.

```java
public static void swap(List<?> list, int i, int j) {
    swapHelper(list, i, j);
}

private static <E> void swapHelper(List<E> list, int i, int j) {
    list.set(i, list.set(j, list.get(i)));
}
```

swapHelper 메서드는 리스트가 ```List<E>```임을 알고 있다.

> 즉, 이 리스트에서 꺼낸 값의 타입은 항상 E이고, E 타입의 값이라면 이 리스트에 넣어도 안전함을 알고 있다.

### 핵심 정리
조금 복잡하더라도 와일드카드 타입을 적용하면 API가 훨씬 유연해진다.

그러니 널리 쓰일 라이브러리를 작성한다면 반드시 반드시 와일드카드 타입을 적절히 사용해줘야 한다.

**PECS**공식을 기억하자.   
즉, 생성자(producer)는 extends를 소비자(consumer)는 super를 사용한다. Comparable과 Comparator는 모두 소비자라는 사실도 잊지 말자.




