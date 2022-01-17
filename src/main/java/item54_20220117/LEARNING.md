# 아이템54. null이 아닌, 빈 컬렉션이나 배열을 반환하라

다음과 같은 메서드가 있다고 해보자

```java
/**
 * @return 매장 안의 모든 치즈 목록을 반환한다.
 *          단, 재고가 하나도 없다면 null을 반환한다.
 */
class Cheeses {
    private final List<Cheese> values;

    public Cheeses(List<Cheese> values) {
        this.values = values;
    }

    public List<Cheese> getCheeses() {
        return values.isEmpty() ? null : new ArrayList<>(values);
    }
}

class Cheese {
    private final String name;

    public Cheese(String name) {
        this.name = name;
    }
}
```

재고가 없다고 해서 특별히 취급할 이유는 없다. 그럼에도 위 예시처럼 null을 반환한다면, 클라이언트에게 null처리가 강제된다.

```java
//클라이언트측 사용 코드
public class ListNullExample {
    public static void main(String[] args) {
        Cheeses cheeses = new Cheeses(new ArrayList<>());
        List<Cheese> cheeseList = cheeses.getCheeses();

        // null 처리가 강제됨
        if (cheeseList != null && cheeseList.contains(new Cheese("Mozza")))
            System.out.println("음");
    }
}
```

컬렉션이나 배열같은 컨테이너가 비었을 때 null을 반환하는 메서드를 사용할 때면 항시 이와같은 방어 코드를 넣어줘야 한다.   
(클라이언트에서 방어 코드를 빼먹으면 오류(대부분의 경우 NullPointerException)가 발생할 수 있다.)   
객체가 0개일 가능성이 없는 상황에서 수십년 뒤에 오류가 발생할 수도 있다.

null을 반환하려면 반환하는 쪽에서도 이 상황을 특별히 취급해줘야 해서 코드가 더 복잡해진다.

### null 대신 빈 컨테이너 반환이 비용이 더 드니까 좋지않다?

때로는 빈 컨테이너를 할당하는 데도 비용이 드니 null을 반환하는 쪽이 낫다는 주장도 있다.

이는 두 가지 측면에서 잘못된 주장이다.
1. 성능 분석 결과 이 할당이 성능 저하의 주범이라고 확인되지 않는 한, 이 정도의 성능 차이는 신경 쓸 수준이 못 된다.
2. 빈 컬렉션과 배열은 굳이 새로 할당하지 않고도 반환할 수 있다.

빈 컬렉션을 반환하려면 대부분의 경우 다음과 같이 작성한다.

```java
public List<Cheese> getCheese() {
    return new ArrayList<>(values);
}
```

리스트가 비어있다면 자연스럽게 빈 리스트가 반환된다.

그럼에도 빈 컬렉션 할당이 성능을 떨어뜨릴 것 같거나, 실제로 성능 저하가 있다면 해결 방법이 있다.   

> 매번 똑같은 빈 '불변' 컬렉션을 반환하는 것이다.

알다시피 불변 객체는 자유롭게 공유해도 안전하다. Collections.emptyList 메서드가 그러하다.   
Set이 필요하면 Collections.emptySet, Map이 필요하면 Collections.emptyMap 이 있다.

단, 이것은 최적화에 해당하니 꼭 필요할 때만 사용하자.   
최적화가 필요하다고 판단되면 수정 전과 후의 성능을 측정하여 **실제로 성능이 개선되는지 꼭 확인하자**.

```java
public List<Cheese> getCheeses() {
    return values.isEmpty() ? Collections.emptyList() : new ArrayList<>(values);
}
```

### null대신, 빈 배열을 반환하라

배열을 쓸 때도 위와 마찬가지다. 절대 null을 반환하지 말고 길이가 0인 배열을 반환하라.

보통은 단순히 정확한 길이의 배열을 반환하기만 하면 된다. 그 길이가 0일 수도 있을 뿐이다.   
다음 코드에서 toArray 메서드에 건넨 길이 0짜리 배열은 우리가 원하는 반환 타입(이 경우엔 Cheese[])을 알려주는 역할을 한다.

```java
class Cheeses4 {
    private final List<Cheese4> values;

    public Cheeses4(List<Cheese4> values) {
        this.values = values;
    }

    public Cheese4[] getCheeses() {
        return values.toArray(new Cheese4[0]);
    }
}
```

이 방식이 성능을 떨어뜨릴 것 같다면 길이 0짜리 배열을 미리 선언해두고 매번 그 배열을 반환하면 된다. 길이 0인 배열은 모두 불변이기 때문이다.

```java
class Cheeses4 {
    private static final Cheese4[] EMPTY_CHEESE_ARRAY = new Cheese4[0];
    
    ...

    public Cheese4[] getCheeses() {
        return values.toArray(EMPTY_CHEESE_ARRAY);
    }
}
```

이 최적화 버전의 getCheeses는 항상 EMPTY_CHEESE_ARRAY를 인수로 넘겨 toArray를 호출한다.

따라서 values가 비었을 때면 언제나 EMPTY_CHEESE_ARRAY를 반환하게 된다.

단순히 성능을 개선할 목적이라면 toArray에 넘기는 배열을 미리 할당하는 건 추천하지 않는다. 오히려 성능이 떨어진다는 연구 결과도 있다.

```java
// 나쁜 예시코드, 배열을 미리 할당하면 성능이 나빠진다.
return values.toArray(new Cheese[values.size()]);
```