# Comparable을 구현할지 고려하라

Comparable 인터페이스에는 compareTo 메서드가 있다.   
compareTo는 Object의 메서드가 아니다.

성격 두 가지만 빼면 Object의 equals와 같다.
* compareTo는 단순 동치성 비교에 더해 순서까지 비교할 수 있다.
* 제네릭하다

> Comparable을 구현했다는 것은 그 클래스의 인스턴스들에는 자연적인 순서가 있음을 뜻한다.

Comparable을 구현한 객체들의 배열은 다음처럼 손쉽게 정렬할 수 있다.

```java
Arrays.sort(a);
```

> Arrays.sort 사용 시 기본 값 타입의 정렬에는 DualPivotQuicksort.sort를 사용한다.   
> 객체에 대한 정렬에는 LegacyMergeSort 또는 TimSort를 사용하는데 내부적으로 Comparable의 compareTo를 사용한다.   
> 자바 7부터는 별도의 설정이 없다면 TimSort를 사용한다.

검색, 극단값 계산, 자동 정렬되는 컬렉션관리도 역시 쉽게 할 수 있다.   
다음 코드에서 TreeSet을 사용해 중복을 제거하고 알파벳 순서로 출력합니다.   
String이 Comparable을 구현한 덕분이다.

```java
public class WordList {
    public static void main(String[] args) {
        Set<String> s = new TreeSet<>();
        Collections.addAll(s, args);
        System.out.println(s);
    }
}
```

사실상 자바 플랫폼 라이브러리의 모든 값 클래스와 열거 타입이 Comparable을 구현했다.   
알파벳, 숫자, 연대 같이 순서가 명확한 값 클래스를 작성한다면 반드시 Comparable 인터페이스를 구현하자.

```java
public class Number implements Comparable<Number> {

    private int value;

    public Number(int value) {
        this.value = value;
    }

    @Override
    public int compareTo(ComparableExample o) {
        int result = this.value - o.value;
        if (result > 0) {
            return 1;
        }
        if (result < 0) {
            return -1;
        }
        return 0;
    }
}
```

compareTo 메서드의 일반 규약은 equals의 규약과 비슷하다.

> 이 객체와 주어진 객체의 순서를 비교한다.    
> 이 객체가 주어진 객체보다 작으면 음의 정수를,    
> 같으면 0,    
> 크면 양의 정수를 반환한다.   
> 이 객체와 비교할 수 없는 타입의 객체가 주어지면 ClassCastException을 던진다.
> 
> 다음 설명에서 sgn(표현식) 표기는 수학에서 말하는 부호 함수(signum function)를 뜻한며, 표현식의 값이 음수, 0, 양수일 때 -1,0,1을 반환하도록 정의했다.
> 
> * Comparable을 구현한 클래스는 모든 x,y에 대해 sgn(x.compareTo(y)) == -sgn(y.compareTo(x))여야 한다. (따라서 x.compareTo(y)는 y.compareTo(x)가 예외 던질 때에 한해 예외를 던져한다.)
> * Comparable을 구현한 클래스는 추이성을 보장해야한다. 즉, (x.compareTo(y) > 0 && y.compareTo(z) > 0)이면 x.compareTo(z) > 0 이다.
> * Comparable을 구현한 클래스는 모든 z에 대해 x.compareTo(y) == 0이면 sgn(x.compareTo(z)) == sgn(y.compareTo(z))다.
> * 이 항목은 필수는 아니지만 지키는 것이 좋다. (x.compareTo(y) == 0) == (x.equals(y))여야 한다. Comparable을 구현하고 이 권고를 지키지 않는 모든 클래스는 그 사실을 명시해야 한다. 다음과 같이 명시하면 적당할 것이다.   
> "주의: 이 클래스의 순서는 equals 메서드와 일관되지 않다."

hashCode 규약을 지키지 못하면 해시를 사용하는 클래스와 어울리지 못하듯,   
compareTo 규약을 지키지 못하면 비교를 활용하는 클래스와 어울리지 못한다.

비교를 활용하는 클래스의 예
* 정렬된 컬렉션인 TreeSet 과 TreeMap
* 정렬 알고리즘을 활용하는 유틸리티 클래스인 Collections와 Arrays가 있다.

compareTo 규약 요약
1. 순서를 바꿔 비교해도 예상한 결과가 나와야한다. a > b 일 때 a.compareTo(b) 는 양수 b.compareTo(a)는 음수 여야한다.
2. a > b, b > c 라면 a > c 가 성립해야 한다.
3. 크기가 같은 객체들끼리 어떤 객체와 비교하더라도 항상 같아야한다는 뜻이다. a == b 라면 a.compareTo(c) == b.compareTo(c) 가 성립해야한다.

compareTo의 마지막 규약 ```(x.compareTo(y) == 0) == (x.equals(y))여야 한다.``` 은 필수는 아니지만 꼭 지키길 권한다.   
이 규약은 compareTo 메서드로 수행한 동치성 테스트의 결과가 equals와 같아야한다는 것이다.   
이것을 잘 지키면 compareTo로 줄지은 순서와 equals의 결과가 일관되게 된다.   

compareTo와 equals의 결과가 일관되지 않은 클래스도 동작은 한다. 
단, 이 클래스의 객체를 '정렬된 컬렉션'에 넣으면 해당 컬렉션이 구현한 인터페이스(Collection, Set 혹은 Map)에 정의된 동작과 엇박을 낼 것이다.   
이 인터페이스들은 equals메서드의 규약을 따른다고 되어있지만 놀랍게도 정렬된 컬렉션들은 동치성 비교시 equals 대신 compareTo를 활용한다.

compareTo와 equals가 일관되지 않는 예로 BigDecimal 클래스가 있다.
HashSet 인스턴스를 생성한 다음 new BigDecimal("1.0")과 new BigDecimal("1.00")을 차례로 추가한다.   
이 두 BigDecimal은 equals 메서드로 비교하면 서로 다르기 때문에 HashSet은 원소 2개를 갖게된다.

TreeSet 인스턴스에 위의 두 BigDecimal을 저장하면 1개의 원소를 갖는다. compareTo 메서드로 비교하면 두 BigDecimal 인스턴스가 똑같기 때문이다. (숫자로는 같은 값을 가짐)

Comparable을 구현하지 않은 필드나 표준이 아닌 순서로 비교해야한다면 비교자(Comparator)를 대신 사용한다.

이펙티브 자바 2판에서는 compareTo 메서드에서 정수 기본 타입 필드를 비교할 때는 관계 연산자인 < 와 >를, 실수 기본 타입 필드를 비교할 때는 정적 메서드인 Double.compare와 Float.compare를 사용하라고 권헀다.   
그런데 자바 7부터는 상황이 변했다. 박싱된 기본 타입 클래스들에 새로 추가된 정적 메서드인 compare를 이용하면 되는 것이다. 

> compareTo 메서드에서 관계 연산자 < 와 >를 사용하는 이전 방식은 거추장스럽고 오류를 유발하니, 이제는 추천하지 않는다.

클래스에 핵심 필드가 여러개라면 어느 것을 먼저 비교하느냐가 중요해진다.   
가장 핵심적인 필드부터 비교해나가자. 비교 결과가 0이 아니라면, 즉 순서가 결정되면 거기서 바로 순서를 반환하면 된다.

```java
// 이전에 구현했던 PhoneNumber 클래스
// 010-1234-5678 의 값을 필드와 매핑하면 다음과 같다.
// areaCode : 010
// prefix : 1234
// lineNum : 5678
public int compareTo(PhoneNumber pn) {
    int result = Short.compare(areaCode, pn.areaCode);
    if (result == 0) {
        result = Short.compare(prefix, pn.prefix);
        if (result == 0) {
            result = Short.compare(lineNum, pn.lineNum);
        }
    }
    return result;
}
```

위와 같이 하면 앞쪽의 핵심 필드에서 이미 순서가 정해진 경우 다음 비교를 스킵하고 빠르게 반환할 수 있다.

자바 8에서는 Comparator 인터페이스가 일련의 비교자 생성 메서드와 팀을 꾸려 메서드 연쇄 방식으로 비교자를 생성할 수 있게 되었다.
그리고 비교자들을 Comparable 인터페이스가 원하는 compareTo 메서드를 구현하는 데 멋지게 활용할 수 있다.   
(이 방식은 간결함이 있지만 사실 약간의 성능 저하가 뒤따른다.)

방금 전 PhoneNumber의 compareTo 메서드에 이 방식을 적용하면 아래와 같다.

```java
private static final Comparator<PhoneNumber> COMPARATOR = 
        comparingInt((PhoneNumber pn) -> pn.areaCode)
        .thenComparingInt(pn -> pn.prefix)
        .thenComparingInt(pn -> pn.lineNum);

public int compareTo(PhoneNumber pn) {
    return COMPARATOR.compare(this, pn);
}
```

Comparator는 수 많은 보조 생성 메서드들로 중무장하고 있다. long과 double용으로는 comparingInt와 thenComparingInt의 변형 메서드를 준비했다.
float은 double용을 이용해 수행한다. 이런 식으로 자바의 숫자용 기본 타입을 모두 커버한다.

이따금 '값의 차'를 기준으로 첫 번째 값이 두 번째 값보다 작으면 음수를, 두 값이 같으면 0을, 첫 번째 값이 크면 양수를 반환하는 compareTo나 compare 메서드와 마주할 것이다.

```java
static Comparator<Object> hashCodeOrder = new Comparator<>() {
    public int compare(Object o1, Object o2) {
        return o1.hashCode() - o2.hashCOde();
    }
};
```

위와 같은 방식은 사용하면 안 된다. 이 방식은 정수 오버플로를 일으키거나 IEEE 754 부동소수점 계싼 방식에 따른 오류를 낼 수 있다. 그렇다고 이번 아이템에서 설명한 방법대로 구현한 코드보다 월등히 빠르지도 않을 것이다.

다음의 두 방식 중 하나를 사용하자

```java
// 정적 compare 메서드를 활용한 비교자
static Comparator<Object> hashCodeOrder = new Comparator<>() {
    public int compare(Object o1, Object o2) {
        return Integer.compare(o1.hashCode(), o2.hashCode());
    }
};
```

```java
// 비교자 생성 메서드를 활용한 비교자
static Comparator<Object> hashCodeOrder = Comparator.comparingInt(o -> o.hashCode());
```