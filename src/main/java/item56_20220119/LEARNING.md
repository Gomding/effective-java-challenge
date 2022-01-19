# 아이템55. 옵셔널 반환은 신중히 하라

자바 8 전에는 특정 조건에서 값을 반환할 수 없을 때 취할 수 있는 선택지가 두 가지 있다.
1. 예외 던지기
2. null을 반환

하지만 두 가지모두 허점이 있다.

예외 던지기는 진짜 예외적인 상황에서만 사용해야 하며, 예외를 생성할 때 스택 추적 전체를 캡처하므로 비용도 만만치 않다.

null을 반환하면 이런 문제가 생기지 않지만, null을 반환하는 메서드를 호출하는 입장에서는 null 처리 코드를 항상 추가해야한다.   
(null 처리를 제때 하지않으면, 반환된 null 값이 어딘가에 저장되어 언젠가 NullPointerException이 발생한다. null을 반환하게 한 시점으로 부터 한참이 지나 원인을 추적하기 힘든 상황이 올 수 있다.)

### 자바 8의 Optional

```Optional<T>```는 null이 아닌 T타입 탐조를 하나 담거나, 혹은 아무것도 담지 않을 수 있다.

> 아무것도 담지 않은 Optional을 '비었다'고 말한다.   
> 반대로 어떤 값을 담은 옵셔널은 '비지 않았다'고 말한다.

옵셔널은 원소를 최대 1개 가질 수 있는 '불변' 컬렉션이다. ```Optional<T>``` 가 ```Collection<T>```를 구현하지는 않았지만, 원칙적으로 그렇다는 말이다.

### Optional의 사용처

보통은 T를 반환하면 되지만 특정 조건에서는 아무것도 반환하지 않아야 할 때 T 대신 ```Optional<T>```를 반환하도록 선언하면 된다.   
(반환값이 존재하거나 존재하지 않음을 클라이언트에게 알려준다. 값이 없을 때 처리를 클라이언트에게 맡겨야할 때)   
이렇게 하면 유효한 반환값이 없을 때는 빈 결과를 반환하는 메서드가 만들어진다.

옵셔널을 반환하는 메서드는 예외를 던지는 메서드보다 유연하고 사용하기 쉬우며, null을 반환하는 메서드보다 오류 가능성이 작다.

다음 예시는 인자로 주어진 컬렉션에서 최댓값을 뽑아주는 메서드다.

```java
public class ThrowExceptionEx {

    public static <E extends Comparable<E>> E max(Collection<E> c) {
        if (c.isEmpty())
            throw new IllegalArgumentException("빈 컬렉션");

        E result = null;
        for (E e : c) {
            if (result == null || e.compareTo(result) > 0) {
                result = Objects.requireNonNull(e);
            }
        }

        return result;
    }
}
```

이 메서드에 빈 컬렉션을 건네면 IllegalArgumentException을 던진다.   
이를 ```Optional<E>```를 반환하도록 수정하면 다음과 같다.

```java
public class OptionalEx {

    public static <E extends Comparable<E>> Optional<E> max(Collection<E> c) {
        if (c.isEmpty())
            return Optional.empty();

        E result = null;
        for (E e : c) {
            if (result == null || e.compareTo(result) > 0) {
                result = Objects.requireNonNull(e);
            }
        }

        return Optional.of(result);
    }
}
```

Optional은 다양한 정적 팩터리 메서드를 제공한다.
* Optional.empty() : 빈 옵셔널을 만든다.
* Optional.of(value) : 값이 든 옵셔널을 사용한다.(null)을 값으로 넣을 수 없다.
  * Optional.of(value)에 null을 넣으면 NullPointerException을 던지니 주의하자.
* Optional.ofNullable(value) : null값도 허용하는 옵셔널을 만든다.

옵셔널을 반환하는 메서드에서는 **절대 null을 반환하지 말자**
이는 옵셔널을 도입한 취지를 완전히 무시하는 행위다.

```java
public <E> Optional<E> doSomething() {
        ...
    // Optional을 반환하는 메서드에서 null을 반환하지 말자. 이는 잘못 사용된 예시다.
    return null;
}
```

### 스트림과 옵셔널

스트림의 종단 연산 중 상당수가 옵셔널을 반환한다.

앞의 max 메서드를 스트림 버전으로 다시 작성한다면 Stream의 max연산이 우리에게 필요한 옵셔널을 생성해줄 것이다.(비교자를 명시적으로 전달해야 하지만)

```java
public static <E extends Comparable<E>> Optional<E> max(Collection<E> c) {
    return c.stream().max(Comparator.naturalOrder());
}
```

### null반환 또는 예외를 던지는 대신 Optional 반환을 선택해야 하는 기준은?

Optional은 Checked Exception과 취지가 비슷하다.   
즉, 반환값이 없을 수도 있음을 API 사용자에게 명확히 알려준다.   

Unchecked Exception을 던지거나 null을 반환하면 API 사용자는 그 사실을 인지하지 못해 끔찍한 결과로 이어질 수 있다.   
하지만 Checked Exception을 던지면 클라이언트에서는 반드시 이에 대처하는 코드를 작성해넣어야 한다.

> 메서드가 옵셔널을 반환하면 클라이언트는 값이 없을 때의 처리를 선택해야한다.

값이 없을 때 기본값 처리 ```orElese()``` 메서드

```java
String lastWordInLexicon = max(words).orElse("단어 없음...");
```

orElse(value) 메서드는 주의할 점이 있다. orElse(value)의 인자로 들어가는 value는 이미 메모리에 존재하는 것을 필요조건으로 한다.   
따라서 Optional의 값 존재유무와 상관없이 value가 만약 함수라면 이를 실행시켜 반환값을 메모리에 올리도록 한다.   
이런 상황이 의도하지 않은거라면 orElseGet() 메서드를 사용하자.

```java
public static void main(String[] args) {
    List<String> words = Arrays.asList("1", "2");
    // Optional에 값이 있음에도 anyWord() 메서드는 호출된다.
    String lastWordInLexicon1 = max(words).orElse(anyWord());
}

private static String anyWord() {
    System.out.println("anyWord 메서드 호출");
    return "아무 단어나 반환";
}

// 실행결과
// anyWord 메서드 호출
```

orElseThrow() 메서드는 Optional에 값이 없을 때 상황에 맞는 예외를 던질 수 있다.   
예외가 실제로 발생하지 않는 한 예외 생성 비용은 들지 않는다.

```java
String word = max(words).orElseThrow(() -> new IllegalArgumentException("단어가 없다."));
```

get() 메서드는 Optional에 항상 값이 채워져 있다고 확신할 때 곧바로 값을 꺼내는 용도로 사용한다.   
(get을 사용했는데 값이 없다면 NoSuchElementException이 발생)

```java
String word2 = max(words).get();
```

orElseGet() 메서드는 기본값을 설정하는 비용이 아주 커서 부담이 될 때가 있다.   
그럴 때는 ```Supplier<T>```를 인수로 받는 orElseGet을 사용하면, 값이 처음 필요할 때 ```Suplier<T>```를 사용해 생성하므로 초기 설정 비용을 낮출 수 있다.

### 특별한 쓰임에 사용되는 Optional 메서드

filter, map, flatMap, ifPresent 가 있다.

앞서의 기본 메서드로 처리하기 어려워 보인다면 API 문서를 참조해 이 고급 메서드들이 문제를 해결해줄 수 있을지 검토해보자.

isPresent 메서드는 안전 밸브 역할의 메서드다.   
Optional에 값이 있다면 true를, 비어있다면 false를 반환한다.   
이 메서드로 원하는 모든 작업을 수행할 수 있지만 신중히 사용해야 한다.

> 실제로 isPresent를 사용한 코드 대부분이 앞서 언급한 orXXXX 시리즈의 메서드로 대체할 수 있으며,   
> 그렇게 하면 더 짧고 명확하고 용법에 맞는 코드가 된다.

다음 예시를 살펴보자   
부모 프로세스의 프로세스 ID를 출력하거나, 부모가 없다면 "N/A"를 출력하는 코드다.   
(예시의 ProcessHandle은 자바9에서 소개된 클래스다.)

```java
Optional<ProcessHandle> parentProcess = ph.parent();
System.out.println("부모 PID: " + (parentProcess.isPresent() ? 
        String.valueOf(parentProcess.get().pid()) : "N/A"));
```

위 코드는 Optional의 map을 사용해 다음처럼 수정할 수 있다.

```java
System.out.println("부모 PID: " +
        ph.parent().map(h -> String.valueOf(h.pid())).orElse("N/A"));
```

스트림을 사용한다면 옵셔널들을 ```Stream<Option<T>>```로 받아서, 그중 채워진 옵셔널들에서 값을 뽑아 ```Stream<T>```에 담아 처리하는 경우가 드물지 않다.   
자바 8부터는 다음과 같이 구현할 수 있다.

```java
streamOfOPtionals
        .filter(Optional::isPresent)
        .map(Optional::get);
```

자바 9부터는 Optional에 stream() 메서드가 추가되었다.   
이 메서드는 Optional을 Stream으로 변환해주는 어댑터다. 옵셔널에 값이 있으면 그 값을 원소로 담은 스트림으로, 값이 없다면 빈 스트림으로 변환한다.   
이를 Stream의 flatMap 메서드와 조합하면 다음처럼 명료하게 바꿀 수 있다.

```java
streamofOptionals
        .flatMap(Optional::stream);
```

### 항상 Optional을 반환하는것이 정답은 아니다

항상 Optional을 반환한다고 해서 이득이 되는 것은 아니다.

컬렉션, 스트림, 배열, 옵셔널 같은 컨테이너 타입은 옵셔널로 감싸면 안 된다.   
빈 ```Optional<List<T>>```를 반환하는것 보다 비어있는 ```List<T>```를 반환하는 게 좋다.

빈 컨테이너를 그대로 반환하면 되는데 굳이 Optional로 감싸지 않아도 되는 것이다.

Optional도 생성하는데 비용이 들어가니 의미없는 포장을 하지 않아야한다.

### 어떤 경우 Optional로 반환해야 할까

기본 규칙은 이렇다.   
결과가 없을 수 있으며, 클라이언트가 이 상황을 특별하게 처리해야 한다면 ```Optional<T>```를 반환한다.

Optional도 생성 비용이 있고, Optional을 반환받는 곳에서는 값을 꺼내야하므로 한단계가 더 생긴다.   
즉, 성능에 약간의 영향을 줄 수 있는데, 성능이 중요한 상황에서는 Optional이 맞지 않을 수 있다.   
(Optional이 정말 성능에 영향을 주는지는 세밀하게 측정해봐야한다.)

### 기본 타입을 지원하는 OptionalInt. OptionalLong, OptionalDouble

박싱된 기본 타입을 담는 옵셔널은 기본 타입 자체보다 무거울 수밖에 없다.   
값을 두 겹이나 감싸기 떄문이다.

자바 API 설계자는 int, long, double 전용 Optional 클래스를 준비해놨다.

OptionalInt, OptionalLong, OptionalDouble이 그 주인공이다.
Optional이 제공하는 메서드도 전부 지원한다.

### Optional을 인스턴스 필드에 저장해두는 게 필요할 때가 있을까?

이런 상황 대부분은 필수 필드를 갖는 클래스와, 이를 확장해 선택적 필드를 추가한 하위 클래스를 따로 만들어야 함을 암시하는 '나쁜 냄새'다.   

가끔은 적절한 상황도 있다. 기본 타입은 값이 없음을 나타낼 방법이 마땅치 않다.(기본 타입은 기본 값이 있다.)   
따라서 기본 타입에 값이 없음을 나타낼 경우는 Optional을 활용 선언하는것도 좋은 방법이다. (하지만 Integer, Long 같은 포장타입을 쓰는게 더 낫지않을까?)

