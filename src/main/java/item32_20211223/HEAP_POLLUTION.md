# 힙 오염

이펙티브 자바 아이템 32에 힙 오염(heap pollution) 키워드가 나옵니다.   
나와있는 문장은 '매개변수화 타입의 변수가 타입이 다른 객체를 참조하면 힙 오염이 발생한다.' 입니다.   
JVM의 힙 영역(heap area)이 오염된 상태를 의미합니다.   

즉 아래와 같은 상황을 예로 들 수 있습니다.

```java
public class HeapPollutionEx {
    public static void main(String[] args) {
        List<String> strings = Arrays.asList("첫 요소");
        doSomthing(strings);
    }

    private static void doSomthing(List<String> ... stringLists) {
        List<Integer> intList = Arrays.asList(42);
        Object[] objects = stringLists;
        objects[0] = intList; // 힙 오염 발생
//        String s = stringLists[0].get(0); // ClassCastException
    }
}
```

자바에 제네릭이 도입되던 5버전에는 제네릭을 사용하지 않는 코드와의 호환성을 위해 컴파일 타임에는 제네릭 타입이 제거된다.   
이전 버전에서 사용하는 제네릭이 없는 ArrayList 와 제네릭을 사용한 ArrayList<E> 모두 정상적으로 동작해야하는 것이다.

즉, 제네릭과 매개변수화 타입은 실체화 되지 않는 실체화 불가 타입이다.   
실체화 불가 타입은 런타임에 컴파일타임보다 타입 관련 정보를 적게 담고 있다.

분명 doSomething 메서드의 인자로 들어온 가변 인자는 List<String> 배열이다.   
(가변 인자는 암묵적으로 배열을 하나 만든다.)

해당 가변 인자를 Object[] 배열 변수로 초기화하고 인덱스 0의 요소를 List<Integer> 타입의 변수로 초기화한다. 이 때 힙 오염이 발생한다. 해당 배열에 다른 타입이 두 가지가 존재한다. String과 Integer 가 함께 존재하고 있다.

예시의 마지막 요소를 꺼낼 때 ClassCastException을 던진다. 보이지 않는 형변환이 숨어 있기 때문이다.

```java
String s = stringLists[0].get(0); // ClassCastException

String s = (Integer)stringLists[0].get(0);
```

예시에서 ClassCastException이 발생하는 코드만 주석처리하고 실행하면 아래와 같은 경고만 발생시킨다.

```java
warning: [unchecked] Possible heap pollution from parameterized vararg type List<String>
    private static void doSomthing(List<String> ... stringLists) {
                                                    ^
```
실체화 불가 타입으로 varargs 매개변수를 선언하면 힙 오염이 발생할 수 있다는 것이다.

이처럼 타입 안전성이 깨지니 제네릭 varargs 배열 매개변수에 값을 저장하는 것은 안전하지 않다.   
분명 매개변수화 타입의 배열을 선언하면 컴파일타임에 오류가 발생한다.   
하지만 제네릭 varargs 매개변수를 받는 메서드를 컴파일타임에 오류를 발생시키지 않고 선언할 수 있게 한 이유는 무엇일까?

제네릭 매개변수화 타입의 varargs 매개변수가 받는 메서드가 실무에서 유용하게 쓰이기 때문이다.
 
언어 설계자는 유용함 때문에 모순을 수용하기로 했다.   
대표적으로 Arrays.asList(T... a) 같은 메서드가 있다.

자바 7부터는 @SafeVarargs 애너테이션이 추가되어 제네릭 가변인수 메서드 작성자가 클라이언트 측에서 발생하는 경고를 숨길 수 있게 되었다.

@SafeVarargs 애너테이션은 메서드 작성자가 그 메서드가 타입 안전함을 보장하는 장치다.   
(메서드가 안전한 게 확실하지 않다면 절대 @SafeVarargs 애너테이션을 달아서는 안 된다.)

 