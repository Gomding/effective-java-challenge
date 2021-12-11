# 아이템21. 인터페이스는 구현하는 쪽을 생각해 설계하라.

자바 8 전에는 기존 구현체를 깨뜨리지 않고는 인터페이스에 메서드를 추가할 방법이 없었다.   
(인터페이스에 메서드를 추가하면 보통은 컴파일 오류가 발생한다. 기존 구현체에 해당 메서드가 있을 확률은 거의 없기 때문)

자바 8에서 인터페이스에 디폴트 메서드를 추가할 수 있지만, 위험은 사라지지 않았다.

> 인터페이스에 디폴트 메서드를 선언하면, 인터페이스를 구현한 모든 클래스에서 디폴트 구현이 쓰이게 된다.   
> **즉, 디폴트 메서드는 구현 클래스에 대해 아무것도 모른 채, 구현 클래스와 합의도 없이 무작정 '삽입'된다.**

(자바 7까지의 세상에서는 모든 클래스가 "현재의 인터페이스에 새로운 메서드가 추가될 일은 영원히 없다." 고 가정하고 작성됐다.)

### 자바 8에서의 디폴트 메서드 사용

자바 8에서는 핵심 컬렉션 인터페이스들에 다수의 디폴트 메서드가 추가되었다.

```java
    // Collection 인터페이스의 대표적인 디폴트 메서드 removeIf
    default boolean removeIf(Predicate<? super E> filter) {
        Objects.requireNonNull(filter);
        boolean removed = false;
        final Iterator<E> each = iterator();
        while (each.hasNext()) {
            if (filter.test(each.next())) {
                each.remove();
                removed = true;
            }
        }
        return removed;
    }
    // 이외에도 spliterator, stream, parallelStream 가 있다.
```

자바 라이브러리의 디폴트 메서드는 코드 품질이 높고 범용적이라 대부분 상황에서 잘 작동한다.

하지만 **생각할 수 있는 모든 상황에서 불변식을 해치지 않는 디폴트 메서드를 작성하기란 어려운 법이다.**

위에서 예시로 보여준 removeIf는 범용적으로 잘 구현한 예시다. 하지만 현존하는 모든 Collection 구현체와 잘 어우러지는 것은 아니다.   
대표적인 예로 org.apache.commons.collections4.collection.SynchronizedColelction 이 있다.   
이 클래스는 java.util의 Collections.synchronizedCollection 정적 팩터리 메서드가 반환하는 클래스와 비슷하다.

apache 버전은 클라이언트가 제공한 객체로 락을 거는 능력을 추가로 제공한다.
하지만 자바8이 나온 시점에 apache의 removeIf를 재정의하지 않았다. 따라서 동기화 시켜주는 것이 불가능해진다. 즉 클라이언트가 제공하는 객체를 받아 락을 걸 수 없다는 것이다.
(애초에 removeIf의 구현은 동기화에 관해 아무것도 모르므로 락 객체를 사용할 수도 없다.)

자바 플랫폼 라이브러리에서도 이런 문제를 예방하기 위해 일련의 조치를 취했다.
예를 들어 구현한 인터페이스의 디폴트 메서드를 재정의하고, 다른 메서드에서는 디폴트 메서드를 호출하기 전에 필요한 작업을 수행하도록 했다.

Collections.synchronizedCollection() 이 반환하는 package-private 클래스들은 removeIf를 재정의하고, 이를 호출하는 다른 메서드들은 디폴트 구현을 호출하기 전에 동기화하도록 했다.
```java
// Collections 내부 클래스인 SynchronizedCollection의 구현 일부
@Override
public boolean removeIf(Predicate<? super E> filter) {
    synchronized (mutex) {return c.removeIf(filter);}
}
@Override
public Spliterator<E> spliterator() {
    return c.spliterator(); // Must be manually synched by user!
}
@Override
public Stream<E> stream() {
    return c.stream(); // Must be manually synched by user!
}
@Override
public Stream<E> parallelStream() {
    return c.parallelStream(); // Must be manually synched by user!
}
private void writeObject(ObjectOutputStream s) throws IOException {
    synchronized (mutex) {s.defaultWriteObject();}
}
```

하지만 자바 플랫폼에 속하지 않은 제 3의 기존 컬렉션 구현체(apache의 SynchronizedCollection같은)들은 이런 언어 차원의 인터페이스 변화에 발맞춰 수정될 기회가 없었으며, 그 중 일부는 여전히 수정되지 않고 있다.

### 디폴트 메서드 추가는 꼭 필요한 경우가 아니면 피해라

디폴트 메서드는 (컴파일에 성공하더라도) 기존 구현체에 런타임 오류를 일으킬 수 있다.

자바 8은 컬렉션 인터페이스에 꽤 많은 디폴트 메서드를 추가했고, 그 결과 기존에 짜여진 자바 코드가 영향을 많이 받았다.

> 기존 인터페이스에 디폴트 메서드로 새 메서드를 추가하는 일은 꼭 필요한 경우가 아니면 피해야한다.

새로운 인터페이스를 만드는 경우라면 표준적인 메서드 구현을 제공하는 데 아주 유용한 수단이며, 그 인터페이스를 더 쉽게 구현해 활용할 수 있게끔 해준다.

### 핵심
디폴트 메서드라는 도구가 생겼더라도 인터페이스 설계를 할 때는 여전히 세심한 주의가 필요하다.

기존 인터페이스에 디폴트 메서드를 추가하면 새로운 메서드가 생기는 것으로 API에 어떤 재앙을 몰고 올지 알 수 없다.

새로운 인터페이스라면 충분한 테스트를 해야한다.
* 서로 다른 방식으로 최소한 세 가지를 테스트 구현
* 각 인터페이스의 인스턴스를 다양한 작업에 활용하는 클라이언트도 여럿 만들어보기

> 인터페이스를 릴리스한 후라도 결함을 수정하는 게 가능한 경우도 있겠지만, 절대 그 가능성에 기대서는 안된다.