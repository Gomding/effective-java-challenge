# 아이템30. 이왕이면 제네릭 메서드로 만들라

클래스뿐만 아니라 메서드도 제네릭으로 만들 수 있다.

매개변수화 타입을 받는 정적 유틸리티 메서드는 보통 제네릭이다.   
Collections의 '알고리즘' 메서드(binarySearch, sort 등)는 모두 제네릭이다.

제네릭 메서드 작성법은 제네릭 타입 작성법과 비슷하다.

```java
// 두 집합의 합집합을 반환하는 메서드
// 경고가 발생한다.
public static Set union(Set s1, Set s2) {
    Set result = new HashSet(s1);
    result.addAll(s2);
    return result;
}

// 발생하는 경고문
warning: [unchecked] unchecked call to HashSet(Collection<? extends E>) as a member of the raw type HashSet 
Set result = new HashSet(s1);
^
where E is a type-variable:
E extends Object declared in class HashSet
```

경고를 없애려면 이 메서드를 타입 안전하게 만들어야 한다.

메서드 선언에서의 세 집합(입력 2개, 반환 1개)의 원소 타입을 타입 매개변수로 명시하고,   
메서드 안에서도 이 타입 매개변수만 사용하게 수정하면 된다.   
**(타입 매개변수들을 선언하는) 타입 매개변수 목록은 메서드의 제한자와 반환타입 사이에 온다.**

```java
// 메서드의 제한자(public) 과 반환타입(Set<E>) 사이에 <E>는 타입 매개변수 목록이다.
public static <E> Set<E> union(Set<E> s1, Set<E> s2) {
    Set<E> result = new HashSet<>(s1);
    result.addAll(s2);
    return result;
}
```

이 메서드는 경고없이 컴파일되며, 타입 안전하고, 쓰기 편하다.

```java
// union 메서드 사용 예시
public static void main(String[] args) {
    Set<String> guys = new HashSet<>(Arrays.asList("톰", "딕", "해리"));
    Set<String> stooges = new HashSet<>(Arrays.asList("래리", "모에", "컬리"));
    Set<String> aflCio = union(guys, stooges);
    System.out.println(aflCio);
}

// 실행 결과 (순서는 Set의 내부 구현 방식에 따라 다를 수 있다.)
[톰, 해리, 래리, 딕, 컬리, 모에]
```

union 메서드는 집합 3개(입력 2개, 반환 1개)의 타입이 모두 같아야 한다. 이를 한정적 와일드카드 타입(아이템 31)을 사용하여 더 유연하게 개선할 수 있다.

### 패턴 : 제네릭 싱글턴 팩터리

불변 객체를 여러 타입으로 활용할 수 있게 만들어야 할 때가 있다.

제네릭은 런타임에 타입 정보가 소거되므로 하나의 객체를 어떤 타입으로든 매개변수화 할 수 있다. 이를 가능하게 하려면 요청한 타입 매개변수에 맞게 매번 그 객체의 타입을 바꿔주는 정적 팩터리를 만들어야 한다.

> 이 패턴을 제네릭 싱글턴 팩터리라 한다.

Collections.reverseOrder 같은 함수 객체나 Collections.emptySet 같은 컬렉션용으로 사용한다.
```java
// Collections.reverseOrder 의 구현
public static <T> Comparator<T> reverseOrder() {
    return (Comparator<T>) ReverseComparator.REVERSE_ORDER; // 타입 매개변수에 맞게 바꿔준다.
}

/**
 * @serial include
 */
private static class ReverseComparator
        implements Comparator<Comparable<Object>>, Serializable {

    private static final long serialVersionUID = 7207038068494060240L;

    static final ReverseComparator REVERSE_ORDER
            = new ReverseComparator();

    public int compare(Comparable<Object> c1, Comparable<Object> c2) {
        return c2.compareTo(c1);
    }

    private Object readResolve() { return Collections.reverseOrder(); }

    @Override
    public Comparator<Comparable<Object>> reversed() {
        return Comparator.naturalOrder();
    }
}
```

제네릭 싱글턴 팩터리의 예시

항등함수(identity function)를 담은 클래스를 만들고 싶다고 해보자. (Function.identity를 사용하면 되지만 직접 구현하는 예시)

항등함수 객체는 상태가 없으니 모든 요청마다 새로운 객체를 생성하는 것은 낭비다.

자바의 제네릭이 실체화된다면 항등방수를 타입별로 하나씩 만들어야 했겠지만, 소거 방식을 사용한 덕에 제네릭 싱글턴하나면 충분하다.

```java
private static UnaryOperator<Object> IDENTITY_FN = (t) -> t;
    
@SuppressWarnings("unchecked")
public static <T> UnaryOperator<T> identityFunction() {
    return (UnaryOperator<T>) IDENTITY_FN;
}
```

IDENTITY_FN을 ```UnaryOperator<T>```로 형변환하면 비검사 형변환 경고가 발생한다.   
T가 어떤 타입이든 ```UnaryOperator<Object>```는 ```UnaryOperator<T>```가 아니기 때문이다.

항등함수는 입력 값을 수정 없이 그대로 반환하는 특별한 함수이므로, T가 어떤 타입이든 ```UnaryOperator<T>```를 사용해도 타입이 안전하다.   
즉, 해당 메서드가 보내는 비검사 형변환 경고는 숨겨도 된다는 의미다. (@SuppressWarning 사용)

### 재귀정 타입 한정

자기 자신이 들어간 표현식을 사용하여 타입 매개변수의 허용 범위를 한정할 수 있다.

바로 재귀적 타입 한정(recursive type bound)이라는 개념이다.

Comparable 인터페이스와 함께 사용하는 예제를 보자.

```java
public interface Comparable<T> {
    int compareTo(T o);
}
```

타입 매개변수 T는 ```Comparable<T>```를 구현한 타입이 비교할 수 있는 원소의 타입을 정의한다.

거의 모든 타입은 자신과 같은 원소와만 비교할 수 있다.   
(```String -> Comparable<String>```, ```Integer -> Comparable<Integer>```)

Comparable을 구현한 원소의 컬렉션을 입력받는 메서드들은 주로 그 원소들을 정렬, 검색, 최솟값, 최댓값을 구하는 식으로 사용된다.   
이 기능을 수행하려면 컬렉션에 담긴 모든 원소가 상호 비교될 수 있어야 한다.

> 이때 타입 한정자를 사용할 수 있다.

```java
public static <E extends Comparable<E>> E max(Collection<E> c);
```

타입 한정인 ```<E extends Comparable<E>>```는 "모든 타입 E는 자신과 비교할 수 있다"라고 읽을 수 있다.   
즉, **"Comparable의 하위 타입만 타입 매개변수로 받아들이겠다"** 라는 것이다.

> 상호 비교 가능하다는 뜻을 아주 정확하게 표현했다.

재귀적 타입 한정은 훨씬 복잡해질 가능성이 있긴 하지만, 그런 일은 잘 일어나지 않는다.

### 핵심 정리
* 제네릭 타입과 마찬가지로 클라이언트가 형변환을 명시하는 메서드보다 제네릭 메서드가 더 안전하며 사용도 쉽다.
* 메서드도 형변환 없이 사용할 수 있는 편이 좋고, 대부분 제네릭 메서드로 구현해야하는 경우다.
* 형변환을 해줘야 하는 기존 메서드는 제네릭하게 만들자

