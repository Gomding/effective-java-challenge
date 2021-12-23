# 아이템32 제네릭과 가변인수를 함께 쓸 떄는 신중하라.

가변인수 메서드와 제네릭은 자바5에 함께 추가됐다.
서로 잘 어우러지리라 기대하지만 슬프게도 그렇지 않다.

가변인수 메서드를 호출하면 가변인수를 담기 위한 배열이 자동으로 만들어진다.
내부로 감췄어야 할 이 배열을 그만 클라이언트에 노출하는 문제가 생겼다.

> 그 결과 varargs 매개변수에 제네릭이나 매개변수화 타입이 포함되면 알기 어려운 컴파일 경고가 발생한다.

메서드를 선언할 때 실체화 불가 타입으로 varargs 매개변수를 선언하면 컴파일러가 경고를 보낸다.

경고 형태는 다음과 같다.

```java
warning: [unchecked] Possible heap pollution from parameterized vararg type List<String>
    private static void doSomthing(List<String> ... stringLists) {
                                                    ^
```

> 매개변수화 타입의 변수가 타입이 다른 객체를 참조하면 힙 오염이 발생한다.

다른 타입 객체를 참조하는 상황에서는 컴파일러가 자동 생성한 형변환이 실패할 수 있으니, 제네릭 타입 시스템이 약속한 타입 안전성의 근간이 흔들려버린다.

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
        String s = stringLists[0].get(0); // ClassCastException
    }
}
```

이 메서드에서는 형변환하는 곳이 없는데도 인수를 건네 호출하면 ClassCastException을 던진다.
아래 명령어에서 형변환이 숨어있다.
```java
String s = stringLists[0].get(0);
```

> 이렇게 타입 안전성이 깨지니 제네릭 varargs 배열 매개변수에 값을 저장하는 것은 안전하지 않다.

### 제네릭 배열과 가변인자의 모순점

제네릭 배열을 프로그래머가 직접 생성하는 건 허용하지 않으면서 제네릭 varargs 매개변수를 받는 메서드를 선언할 수 있게 한 이유는 무엇일까?
제네릭 배열은 컴파일 오류를 발생시키는데, 제네릭 varargs 매개변수를 받는 메서드는 경고로 끝난다.

이 모순에 대한 답은 제네릭이나 매개변수화 타입의 varargs 매개변수를 받는 메서드가 실무에서 매우 유용하기 때문이다.
그래서 언어 설계자는 이 모순을 수용하기로 했다.

대표적으로 Arrays.asList(T... a) 메서드가 있다.

이런 경고들은 사용하기에 안전함이 보장된다면 숨기는 것이 마땅하다.
자바 7부터 @SafeVarargs 애너테이션을 추가해서 제네릭 가변인수 메서드 작성자가 클라이언트 측에서 발생하는 경고를 숨길 수 있게 되었다.

@SafeVarargs 애너테이션은 메서드 작성자가 그 메서드가 타입 안전함을 보장하는 장치다.

> 메서드가 안전한 게 확실하지 않다면 절대 @SafeVarargs 애너테이션을 달아서는 안 된다.

### 제네릭이나 타입 매개변수의 가변인수 메서드가 안전한지 어떻게 확인하는가?

앞서 가변인수 메서드를 호출할 때 varargs 매개변수를 담는 제네릭 배열이 만들어진다고 했다.

메서드가 이 배열에 아무것도 저장하지 않고 그 배열의 참조가 밖으로 노출되지 않는다면 타입 안전하다.

> varargs 매개변수 배열이 호출자로부터 그 메서드로 순수하게 인수들을 전달하는 일만 한다면 그 메서드는 안전하다.

이 때 varargs 매개변수 배열에 아무것도 저장하지 않고도 타입 안전성을 깰 수도 있으니 주의해야 한다.

다음은 위험한 제네릭 메서드다

```java
static <T> T[] toArray(T... args) {
    return args;
}
```

이 메서드가 반환하는 배열의 타입은 이 메서드에 인수를 넘기는 컴파일타임에 결정된다. 그 시점에는 컴파일러에게 충분한 정보가 주어지지 않아 타입을 잘못 판단할 수 있다.

```java
static <T> T[] toArray(T... args) {
    return args;
}

static <T> T[] pickTwo(T a, T b, T c) {
    switch(ThreadLocalRandom.current().nextInt(3)) {
        case 0: return toArray(a, b);
        case 1: return toArray(a, c);
        case 2: return toArray(b, c);
    }
}
```

이 메서드는 제네릭 가변인수를 받는 toArray 메서드를 호출한다는 점만 빼면 위험하지 않고 경고도 내지 않을 것이다.
이 코드가 만드는 배열의 타입은 Object[]인데, pickTwo에 어떤 타입의 객체를 넘기더라도 담을 수 있는 가장 구체적인 타입이기 때문이다.

즉, pickTwo 는 항상 Object[] 타입 배열을 반환한다.

```java
public static void main(String[] args) {
    String[] attributes = pickTwo("좋은", "빠른", "저렴한");
}
```

아무런 문제가 없는 메서드이니 별다른 경고 없이 컴파일된다. 하지만 실행하면 ClassCastException이 발생한다.

해당 코드에는 타입 캐스팅하는 코드를 컴파일러가 자동으로 생성한다. 즉, 다음과 같이 변한다.

```java
String[] attributes = (String[]) pickTwo("좋은", "빠른", "저렴한");
```

Object[]는 String[] 의 하위 타입이 아니므로 이 형변환은 실패한다.

> 제네릭 varargs 매개변수 배열에 다른 메서드가 접근하도록 허용하면 안전하지 않다는 점을 생각하자.

예외가 두 가지 있다.
* @SafeVarargs로 제대로 처리가된 또 다른 varargs 메서드에 넘기는 것은 안전하다.
* 이 배열 내용의 일부 함수를 호출만 하는(varargs를 받지 않는) 일반 메서드에 넘기는 것도 안전하다.

### 제네릭 varargs 매개변수를 안전하게 사용하는 전형적인 예

```java
@SafeVarargs
static <T> List<T> flatten(List<? extends T>... lists) {
    List<T> result = new ArrayList<>();
    for (List<? extneds T> list : lists)
        result.addAll(list);
    return result;
}
```

flatten 메서드는 임의 개수의 리스트를 인수로 받아, 받은 순서대로 그 안의 모든 원소를 하나의 리스트로 옮겨 담아 반환한다.
(varargs 인자 자체를 반환하지 않으므로 안전하다.)

### @SafeVarargs 애너테이션을 사용해야 할 때

* 제네릭이나 매개변수화 타입의 varargs 매개변수를 받는 모든 메서드에 @SafeVarargs를 달아주자.
* 안전하지 않은 varargs 메서드는 절대 사용하지 말라

다음 두 조건을 만족한 제네릭 varargs 메서드는 안전하다.
* varargs 매개변수 배열에 아무것도 저장하지 않는다.
* 그 배열(혹은 복제본)을 신뢰할 수 없는 코드에 노출하지 않는다.

> @SafeVarargs 애너테이션은 재정의할 수 없는 메서드에만 달아야 한다.
> 재정의한 메서드도 안전할지는 보장할 수 없기 때문이다.
> 자바 8에서 이 애너테이션은 오직 정적 메서드와 final 인스턴스 메서드에만 붙일 수 있고, 자바 9부터는 private 인스턴스 메서드에도 허용된다.

