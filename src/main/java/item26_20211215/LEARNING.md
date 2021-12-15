# 챕터 5. 제네릭

제네릭을 지원하기 전에는 컬렉션에서 객체를 꺼낼 때마다 형변환을 해야 했다.

```java
// 제네릭 지원하기 전 컬렉션 사용 예시
public class Main {
    public static void main(String[] args) {
        List list = new ArrayList();
        // 들어가는 요소가 Object 이므로 어떤 타입의 요소도 넣을 수 있다.
        list.add(Integer.valueOf(100));
        list.add("새로운 요소");
        Integer i = (Integer) list.get(1); // 런타임에 에러가 발생함
        // 인덱스 1에 들어있는 요소는 String 타입인데 Integer로 타입 캐스팅을 시도해서 예외가 발생!        
    }
}
```

위 예시를 보면 컴파일 타임에 에러를 잡아주지 않는걸 볼 수 있다.    
프로그래머는 어떤 타입의 요소를 넣을 수 있는지 명확하게 알 수 없고 요소를 꺼낼때도 어떤 타입이 나올지 알 수 없다.

제네릭을 사용하면 컬렉션이 담을 수 있는 타입을 컴파일러에게 알려주게 된다.

이번 장에서는 제네릭의 이점을 최대로 살리고 단점을 최소화하는 방법을 이야기한다.

# 아이템26. 로 타입(raw type)은 사용하지 말라

### 용어 정리
* 제네릭 클래스 or 제네릭 인터페이스 : 클래스와 인터페이스 선언에 타입 매개변수(type-parameter)가 쓰이는 것
* 타입 매개변수 : List<E> 와 같이 '<>' 안에 있는것을 타입 매개변수라고 한다.
* 제네릭 클래스와 제네릭 인터페이스를 통틀어 **제네릭 타입**이라 한다.
* 로 타입(raw type) : 제네릭 타입에서 타입 매개변수를 전혀 사용하지 않을 떄를 말한다.
  * List<E> 의 로 타입은 List 다. ('<>' 타입 매개변수에 아무것도 지정하지 않은 상태)

```List<String>```은 원소의 타입이 String인 리스트를 뜻하는 매개변수화 타입이다.   
-> String 이 정규 타입 매개변수 E에 해당하는 실제 타입 매개변수다.

로 타입은 타입 선언에서 제네릭 타입 정보가 전부 지워진 것처럼 동작하는데, **제네릭 도입 전 코드와 호환을 위한 궁여지책이다.**

```java
// Stamps 인스턴스만 취급한다.
private final Collection stamps = ...;
```

위 코드를 사용하면 실수로 도장(Stamp) 대신 동전(Coin)을 넣어도 아무 오류 없이 컴파일되고 실행된다.   
(컴파일러가 모호한 경고 메세지를 보여주긴 할 것이다.)

```java
stamps.add(new Coin(...)); // "unchecked call" 경고를 내뱉는다.
```

컬렉션에서 동전을 다시 꺼내서 Stamp 타입으로 캐스팅해보기 전까지 오류를 알아차릴 수 없다.(컴파일도 정상적으로 된다.)

> 오류는 가능한 한 발생 즉시, 이상적으로는 컴파일할 때 발견하는 것이 좋다.

제네릭을 활용하면 이와같은 오류에서 벗어날 수 있다.

```java
private final Collection<Stamp> stamps = ...;
```

이렇게 선언하면 컴파일러는 stamps에는 Stamp의 인스턴스만 넣어야 함을 인지하게 된다.

따라서 Stamp 외의 인스턴스를 stamps에 넣으려고하면 컴파일 에러가 발생한다.

```java
stamps.add(new Coin(...));

컴파일 오류 
GenericEx1.java:10: error: no suitable method found for add(Coin)
stamps.add(new Coin());
^
method Collection.add(Stamp) is not applicable
(argument mismatch; Coin cannot be converted to Stamp)
method List.add(Stamp) is not applicable
(argument mismatch; Coin cannot be converted to Stamp)
```

컴파일러는 컬렉션에서 원소를 꺼내는 모든 곳에 보이지 않는 형변환을 추가하여 절대 실패하지 않음을 보장한다.(이번에도 컴파일러 경고가 나지 않았고 경고를 숨기지도 않았다고 가정했다.)

### raw type은 사용하지 않아야한다.

raw type을 쓰는 걸 언어 차원에서 막아 놓지는 않았지만 절대로 써서는 안 된다. (실제로 에러도 발생하지 않는다. 경고 문구만 보여줄 뿐!) 

> raw type을 쓰면 제네릭이 안겨주는 안전성과 표현력을 모두 잃게 된다.

raw type을 혀용해놓은 것은 제네릭 없이 짠 코드가 이미 온 세상에 있기 때문이다.   
때문에 기존 코드를 모두 수용하면서 제네릭을 사용하는 새로운 코드와도 맞물려 정상 동작하게 해야만 했다.

즉 마이그레이션 호환성을 위해 raw type을 지원하고 제네릭 구현에는 소거 방식(아이템28)을 사용하기로 했다.

### raw type 과 List< Object > 의 차이

```List```(raw type)은 제네릭 타입에서 완전히 발을 뺀 것이다. 즉 제네릭이 없는 상태의 코드다.   
```List<Object>```는 모든 타입을 허용한다는 의사를 컴파일러에 명확히 전달한 것이다.

매개변수로 ```List``` 를 받는 메서드에는 ```List<String>```을 넘길 수 있다.   

매개변수로 ```List<Object>``` 를 받는 메서드에는 ```List<String>```을 넘길 수 없다. 이것은 제네릭의 하위 타입 규칙 때문이다.

더 쉽게 설명하자면 String은 Object의 하위타입이 맞지만 ```List<Object>```와 ```List<String>```은 하위 관계가 아니기 때문이다.

그 결과 ```List<Object>```같은 매개변수화 타입을 사용할 때와 달리 List 같은 raw type을 사용하면 타입 안전성을 잃게 된다.

```java
public static void main(String[] args) {
    List<String> strings = new ArrayList<>();
    unsafeAdd(strings, Integer.valueOf(42));
    String s = strings.get(0); // 컴파일러가 자동으로 형변환 코드를 넣어준다.
}

private static void unsafeAdd(List list, Object o) {
    list.add(o);
}
```

위 코드는 컴파일 되지만 타입인 List를 사용하여 다음과 같은 경고가 발생한다.

```java
uses unchecked or unsafe operations.
Note: Recompile with -Xlint:unchecked for details.
```

또한 strings.get(0); 의 결과로 형병환을 시도할 때 ClassCastException을 던진다.

```java
Exception in thread "main" java.lang.ClassCastException: java.lang.Integer cannot be cast to java.lang.String
	at item26_20211215.GenericEx2.main(GenericEx2.java:10)
```

Integer를 String으로 변환하려 시도한 것이다.    
이 형변환은 컴파일러가 자동으로 만들어준 것이라 보통은 실패하지 않는다.

메서드의 매개변수 타입을 ```List``` 에서 ```List<Object>```로 변경하면 컴파일 조차 되지 않는다.

```java
public static void main(String[] args) {
    List<String> strings = new ArrayList<>();
    unsafeAdd(strings, Integer.valueOf(42));
    String s = strings.get(0);
}

private static void unsafeAdd(List<Object> list, Object o) {
    list.add(o);
}

컴파일 오류
        error: incompatible types: List<String> cannot be converted to List<Object>
        unsafeAdd(strings, Integer.valueOf(42));
                ^
```

### raw type 과 List< ? > (와일드카드)의 차이

원소의 타입을 몰라도 되는 raw type을 쓰고싶을 수도 있지만 약간의 실수로 런타임에 예외가 발생할 수 있다.

```java
public class WildCardEx1 {

    public static void main(String[] args) {
        Set<Object> s1 = new HashSet<>();
        s1.add(new Object());
        Set<String> s2 = new HashSet<>();
        s2.add("String 타입의 요소");
        int result = numElementsInCommon(s1, s2);
        System.out.println(result);
    }

    static int numElementsInCommon(Set s1, Set s2) {
        int result = 0;
        for (Object o1 : s1)
            if (s2.contains(o1))
                result++;
        return result;
    }
}
```

위 코드는 정상 동작하지만 raw type을 사용해서 안전하지 않다.   

> 따라서 비한정적 와일드카드 타입(unbounded wildcard type)을 대신 사용하는 게 좋다.

```Set<E>```의 비한정적 와일드카드 타입은 ```Set<?>```다.

이것은 어떤 타입이라도 담을 수 있는 가장 범용적인 매개변수화 Set 타입이다.

```java
public class WildCardEx2 {

    public static void main(String[] args) {
        Set<Object> s1 = new HashSet<>();
        s1.add(new Object());
        Set<String> s2 = new HashSet<>();
        s2.add("String 타입의 요소");
        int result = numElementsInCommon(s1, s2);
        System.out.println(result);
    }

    static int numElementsInCommon(Set<?> s1, Set<?> s2) {
        s1.add(null);
        int result = 0;
        for (Object o1 : s1)
            if (s2.contains(o1))
                result++;
        return result;
    }
}
```

와일드카드 타입은 안전하고, raw type은 안전하지 않다. raw type 컬렉션에는 아무 원소나 넣을 수 있으니 타입 불변식을 훼손하기 쉽다.   
반면, ```Collection<?>```에는 null을 제외하고 어떤 요소도 넣을 수 없다.

```Set<?>```에 null이 아닌 요소를 추가하면 컴파일 오류가 발생한다.

```java
static int numElementsInCommon(Set<?> s1, Set<?> s2) {
    s1.add(100);
        ...
}

컴파일 오류 발생
        WildCardEx2.java:20: error: no suitable method found for add(int)
        s1.add(100);
        ^
        method Collection.add(CAP#1) is not applicable
        (argument mismatch; int cannot be converted to CAP#1)
        method Set.add(CAP#1) is not applicable
        (argument mismatch; int cannot be converted to CAP#1)
        where CAP#1 is a fresh type-variable:
        CAP#1 extends Object from capture of ?
```

해당 오류 메세지에 대해 간단히 설명하자면 컴파일러가 잘못된 타입을 변수에 할당하고 있다고 생각한다.
즉, 컴파일러가 해당 와일드 카드의 타입 추론에 실패한 것이다.

이는 컬렉션의 타입 불변식을 훼손하지 못하게 막아준다. 컴파일러가 제 역할을 한 것이다.

어떤 원소도 ```Collection<?>```에 넣지 못하게 했으며 컬렉션에서 꺼낼 수 있는 객체의 타입도 전혀 알 수 없게 했다.

> 이러한 제약을 받아들일 수 없다면 제네릭 메서드(아이템30)나 한정적 와일드카드 타입(아이템31)을 사용하면 된다.

### raw type 을 예외적으로 사용해야하는 경우

raw type을 쓰지 말라는 규칙에도 소소한 예외가 몇 개 있다.

* 첫 번째 예외

class 리터럴에는 raw type을 써야 한다. 자바 명세는 class 리터럴에 매개변수화 타입을 사용하지 못하게 했다.(배열과 기본 타입은 허용한다)   
예를 들어 List.class, String[].class, int.class는 허용하고 ```List<String>.class```와 ```List<?>.class```는 혀용하지 않는다.

* 두 번째 예외

instanceof 연산자와 관련이 있다. 런타임에는 제네릭 타임 정보가 지워지므로 instanceof 연산자는 비한정적 와일드카드 타입 이외의 매개변수화 타입에는 적용할 수 없다.
그리고 raw type이든 비한정적 와일드카드 타입이든 instanceof는 완전히 똑같이 동작한다. 따라서 비한정적 와일드 카드 타입의 꺾쇠괄호와 물음표는 아무런 역할 없이 코드만 지저분하게 만드므로, 차라리 raw type을 쓰는 편이 깔끔하다.

```java
if (o instanceof Set) {     // raw type 사용 Set<?>을 사용해도 되지만 코드가 조금 지저분하다.
    Set<?> s = (Set<?>) o;  // 와일드 카드 타입 사용
        ...
}
```

### 핵심 정리
* raw type을 사용하면 런타임에 예외가 일어날 수 있으니 사용하면 안 된다.
  * raw type은 제네릭이 도입되기 이전 코드와의 호환성을 위해 제공될 뿐이다.
* ```Set<Object>```는 어떤 타입의 객체도 저장할 수 있는 매개변수화 타입
* ```Set<?>```는 모종의 타입 객체만 저장할 수 있는 와일드 카드 타입이다.
* ```Set``` 은 raw type으로 제네릭 타입 시스템에 속하지 않는다.(제네릭이 적용되지 않은 타입)
* ```Set<Object>, Set<?>```는 안전하지만, raw tpye은 안전하지 않다.