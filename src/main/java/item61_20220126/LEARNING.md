# 아이템61. 박싱된 기본 타입보다는 기본 타입을 사용하라

자바의 데이터 타입은 크게 두 가지로 나눌 수 있다.
* 기본 타입 : int, long, double, boolean ...
* 박싱된 기본 타입(참조 타입) : Integer, Long, Double, Boolean ...

기본 타입과 박싱된 기본 타입은 **오토박싱과 오토언박싱** 덕분에 두 타입을 크게 구분하지 않고 사용할 수는 있지만, 그렇다고 차이가 사라지는 것은 아니다.   
둘 사이에 분명히 차이는 존재한다. 어떤 타입(기본 타입과 박싱된 기본 타입)을 사용하는지는 상당히 중요하고, 주의해서 선택해야 한다.

### 기본 타입과 박싱된 기본 타입의 주된 차이 세 가지

* 첫 번째, 기본 타입은 값만 가지고 있으나, 박싱된 기본 타입은 값에 더해 식별성(identity)이란 속성을 가진다.

박싱된 기본 타입의 두 인스턴스는 값이 같아서 서로 다르다고 식별될 수 있다. (==, 동일성 비교)
```java
Integer i = new Integer(42);
Integer i2 = new Integer(42);

// i 와 i2는 42라는 값을 가지지만 서로 다르다고 식별된다.
System.out.println(i == i2);

// 출력 결과
// false
```

이렇게 식별되는 이유는 Integer도 참조 타입이기 때문이다.   
즉, new Integer(42); 로 생성한 객체는 생성할 때마다 다른 참조 값을 할당한다.

만약 Integer의 내부 value를 비고하고 싶으면 equals() 메서드를 사용하자.

```java
System.out.println(i.equals(i2));

// 출력 결과
// true
```

* 두 번째, null 허용여부

기본 타입의 경우 값이 언제나 유효하다. int 변수를 따로 초기화 하지 않는다면 기본 값인 0을 가진다.   
하지만 박싱된 기본 타입의 경우 null로 초기화가 가능하다. 즉, null 값을 가질 수 있다.

null 값으로 초기화 가능하다는게 꼭 단점은 아니다. 박싱된 기본 타입의 경우 0과 값이 없음(null)을 구분할 수 있다.

* 세 번째, 기본 타입이 박싱된 기본 타입보다 시간과 메모리 사용면에서 더 효율적이다.

박싱된 기본 타입의 경우 참조 값을 가져야하기 때문에 메모리도 더 사용한다.

이상 세 가지 차이가 있기때문에 주의하지 않고 사용하면 진짜 문제가 발생할 수 있다.

### 박싱된 기본 타입의 값 비교

다음 예시는 Integer 값을 오름차순으로 정렬하는 비교자다.   
compare 메서드는 첫 번째 원소가 두 번째 원소보다 작으면 음수, 같으면 0, 크면 양수를 반환한다.   

Integer 자체로 순서가 있으니 이 비교자는 굳이 만들 의미는 없다.

예시를 통해 박싱된 기본 타입의 문제점을 알아보자

```java
Comparator<Integer> naturalOrder =
        (i,j) -> (i < j) ? -1 : (i == j ? 0 : 1);
```

위 예시는 큰 문제가 없어보인다.   
하지만 직접 compare 메서드를 사용해보면 올바르지 않은 결과를 반환한다.

```java
Integer i = new Integer(42);
Integer i2 = new Integer(42);

System.out.println(naturalOrder.compare(i, i2));

// 출력 결과
// 1
```

분명 값이 같은 42인데도 1을 반환한다.   
왜 그런걸까? 여기서는 new Integer() 생성자를 사용해서 객체를 생성한 점과 naturalOrder 에서 Integer의 비교에 동일성 비교(==)를 했다는 점에 주목하자.

new Integer() 로 객체를 생성하면 매번 참조값이 다른 객체를 생성한다. 여기에 동일성 비교를 하면 참조값이 다르므로 당연히 같지 않다는 결과를 반환한다.

만약 Integer의 valueOf 정적 팩터리 메서드 나 동등성 비교(equals) 를 했다면 어떨까?

우선 valueOf 팩터리 메서드를 활용해보자

```java
public class IntegerValueOfEx {

    public static void main(String[] args) {
        Comparator<Integer> naturalOrder =
                (i,j) -> (i < j) ? -1 : (i == j ? 0 : 1);

        // valueOf 정적 팩터리를 활용한 객체 생성
        Integer i = Integer.valueOf(42);
        Integer i2 = Integer.valueOf(42);

        System.out.println(naturalOrder.compare(i, i2));
    }
}
// 출력 결과
// 0
```

valueOf 정적 팩터리 메서드는 Integer 내부에 캐시해둔 객체를 반환한다.   
Integer는 내부적으로 -128 ~ 127의 값을 캐싱해둔다.(new 생성자로 객체를 만들면 캐싱한 객체를 사용하지 않는다.)   
따라서 캐싱한 객체를 가져오기 때문에 같은 참조값을 가지고 == 비교에도 같다 라는 응답을 반환한다.

Integer equals() 메서드도 알아보자.

```java
public class IntegerEqualsEx {
    public static void main(String[] args) {
        Comparator<Integer> naturalOrder =
                (i, j) -> (i < j) ? -1 : (i.equals(j)  ? 0 : 1); // equals() 를 사용해서 동등성 비교

        Integer i = new Integer(42);
        Integer i2 = new Integer(42);

        System.out.println(naturalOrder.compare(i, i2));
    }
}
```

Comparator 비교자에서 equals() 를 사용해 비교했다.   
따라서 객체의 참조값 비교가 아닌 Integer 내부의 equals 메서드를 재정의한 비교를 사용했다.   
박싱된 기본 타입의 경우 equals 메서드는 값 자체를 비교하도록 재정의 되어있다.

### 박싱된 기본 타입과 기본 타입의 비교

```java
public class Unbelievable {
    static Integer i;
    
    public static void main(String[] args) {
        if (i == 42)
            System.out.println("믿을 수 없군!");
    }
}
```

이 프로그램은 "믿을 수 없군!"을 출력하지 않는다.   
하지만 이상한 결과를 보여준다.   

i == 42 비교에서 NullPointerException을 던진다.   
원인은 i가 int가 아닌 Integer이며, 다른 참조 타입 필드와 마찬가지로 i의 초깃값도 null이라는 데 있다.   
즉, i == 42는 Integer와 int를 비교하는 것이다.   

거의 예외 없이 기본 타입과 박싱된 기본 타입을 혼용한 연산에서는 박싱된 기본 타입의 박싱이 자동으로 풀린다.   
그리고 null 참조를 언박싱 할 때 NullPointerException이 발생한다.

### 오토박싱 오토언박싱이 일어나는지 체크하자

다음 예시를 보자

```java
public class AutoBoxingEx {
    public static void main(String[] args) {
        Long sum = 0L;
        for (long i = 0; i <= Integer.MAX_VALUE; i++) {
            sum += i;
        }
        System.out.println(sum);
    }
}
```
이 프로그램은 성능이 엄청나게 느리다.
위 예시에서 얼마나 많은 오토 박싱과 오토 언박싱이 일어나는지 알아보자.

```java
// Long 박싱된 기본 타입 객체 생성
Long sum = 0L;

// i 는 기본 타입 
// i <= Integer.MAX_VALUE는 Integer.MAX_VALUE는 오토 언박싱이 된다.
for (long i = 0; i <= Integer.MAX_VALUE; i++) {
    // sum은 박싱된 기본 타입이지만 i가 기본 타입이므로 sum이 오토 언박싱이 일어난다.
    sum += i;
}
System.out.println(sum);
```

Integer.MAX_VALUE(2147483647) 횟수만큼 박싱과 언박싱이 반복해서 일어나 체감될 정도로 성능이 느려진다.

만약 sum을 박싱된 기본 타입이 아닌 기본 타입으로 선언했다면 덜 했을 것이다.

### 기본 타입과 박싱된 기본 타입은 언제 써야 하는가?

박싱된 기본 타입은 언제 쓰는가?
* 첫 번째, 컬렉션의 원소, 키, 값으로 쓴다.
  * 컬렉션은 기본 타입을 담을 수 없으므로 어쩔 수 없이 박싱된 기본 타입을 써야만 한다.
* 타입 매개변수로는 박싱된 기본 타입을 써야 한다.
* 리플렉션을 통해 메서드를 호출할 때도 박싱된 기본 타입을 사용해야 한다.

### 핵심 정리

기본 타입과 박싱된 기본 타입 중 하나를 선택해야 한다면 가능하면 기본 타입을 사용해라.

기본 타입은 간단하고 빠르다. 박싱된 기본 타입을 써야 한다면 주의를 기울이자.

오토박싱이 박싱된 기본 타입을 사용할 때의 번거로움을 줄여주지만, 그 위험까지 없애주지는 않는다.   
두 박싱된 기본 타입을 == 연산자로 비교한다면 식별성 비교가 이뤄지는데, 이는 여러분이 원한 게 아닐 가능성이 크다.   
같은 연산에서 기본 타입과 박싱된 기본 타입을 혼용하면 언박싱이 이뤄지며, 언박싱 과정에서 NullPointerException을 던질 수 있다.

마지막으로, 기본 타입과 박싱된 기본 타입을 적절하게 사용하지 않으면 필요 없는 객체 생성 비용(박싱 비용)이 발생할 수 있다.