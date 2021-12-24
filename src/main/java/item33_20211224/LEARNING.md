# 아이템33. 타입 안전 이종 컨테이너를 고려하라

제네릭은 ```Set<E>, Map<K, V>``` 등의 컬렉션과 ```ThreadLocal<T>, AtomicReference<T>```등의 단일원소 컨테이너에도 흔히 쓰인다.

이런 모든 쓰임에서 매개변수화되는 대상은 (원소가 아닌) 컨테이너 자신이다.

> 따라서 하나의 컨테이너에서 매개변수화 할 수 있는 타입의 수가 제한된다.

컨테이너의 일반적인 용도에 맞게 설계된 것이니 문제는 없다. 정상적인 것이다.   
Set에는 원소의 타입을 뜻하는 단 하나의 타입 매개변수만 있으면 되며, Map에는 값의 타입을 뜻하는 2개만 필요한 식이다.

### 타입 안전 이종 컨테이너 패턴

위에서는 타입의 수가 제한되어 있지만 더 유연한 수단이 필요할 때도 있다.

예를들어 데이터베이스의 행(row)은 임의 개수의 열(column)을 가질 수 있는데, 모든 열을 타입 안전하게 이용할 수 있다면 멋질 것이다.

타입 안전 이종 컨테이너 패턴을 활용하면 제한을 벗어날 수 있으면서도 타입 안전하게 사용할 수 있다.

* 컨테이너 대신 키를 매개변수화 시킴
* 컨테이너에 값을 넣거나 뺄 때 매개변수화한 키를 함께 제공

이렇게 하면 제네릭 타입 시스템이 값의 타입이 키와 같음을 보장해줄 것이다.

이런 설계를 타입 안전 이종 컨테이너 패턴이라 한다.

설명보다 예시 코드로 보는것이 이해가 더 빠를것이다.

즐겨 찾는 인스턴스를 저장하고 검색할 수 있는 Favorites 클래스다.

```java
public class Favorites {
    Map<Class<?>, Object> values = new HashMap<>();

    public <T> void putFavorite(Class<T> type, T instance) {
        values.put(Objects.requireNonNull(type), instance);
    }

    public <T> T getFavorite(Class<T> type) {
        return type.cast(values.get(type));
    }
}

public class Example {
    public static void main(String[] args) {
        Favorities f = new Favorities();

        f.putFavorite(String.class, "Java");
        f.putFavorite(Integer.class, 123);
        f.putFavorite(Class.class, Favorities.class);

        String favoriteString = f.getFavorite(String.class);
        Integer favoriteInteger = f.getFavorite(Integer.class);
        Class favoriteClass = f.getFavorite(Class.class);
    }
}
```

각 타입의 Class 객체를 매개변수화한 키 역할로 사용하면 되는데, 이 방식이 동작하는 이유는 class의 클래스가 제네릭이기 때문이다.   
컴파일타임 타입 정보와 런타임 타입 정보를 알아내기 위해 메서드들이 주고받는 class 리터럴을 타입 토큰(type token)이라 한다.

Favorites 인스턴스는 타입 안전하다. String을 요청했는데 Integer를 반환하는 일은 절대 없다.   
또한 모든 키의 타입이 제각각이라, 일반적인 맵과 달리 여러 가지 타입의 원소를 담을 수 있다.

따라서 Favorites는 타입 안전 이종 컨테이너라 할 만하다.

Favorites 클래스에서 일어나는 일들에 대해 알아보자

### values라는 Map의 키 타입으로 비한정적 와일드카드를 사용하고 있다.

변수인 values의 타입은 ```Map<Class<?>, Object>``` 이다. 비한정적 와일드카드 타입이라 이 맵 안에 아무것도 넣을 수 없다고 생각할 수 있다.

하지만 와일드카드 타입이 중첩되었다는 점을 알아야 한다.

맵이 아니라 와일드카드 타입인 것이다. 이는 모든 키가 서로 다른 매개변수화 타입일 수 있다는 뜻이다.

### 맵의 값 타입이 단순히 Object이다.

이 맵은 키와 값 사이의 타입 관계를 보증하지 않는다.

즉, 모든 값이 키로 명시한 타입임을 보증하지 않는다. (자바의 타입 시스템에서는 이 관계를 명시할 방법이 없다.)

putFavorite 구현은 아주 쉽다. 주어진 Class 객체와 즐겨찾기 인스턴스를 favorites에 추가해 관계를 지으면 끝이다.

키와 값 사이의 '타입 링크' 정보는 버려진다. 즉, 그 값이 키 타입의 인스턴스라는 정보가 사라진다.

```java
public <T> void putFavorite(Class<T> type, T instance) {
    values.put(Objects.requireNonNull(type), instance);
}
```

getFavorite는 먼저, 주어진 Class 객체에 해당하는 값을 favorites 맵에서 꺼낸다. 이 객체가 바로 반환해야 할 객체가 맞지만, 잘못된 컴파일타임 타입을 가지고 있다.
이 객체의 타입은 Object 이지만, 우리는 이를 T로 바꿔 반환해야 한다.

따라서 getFavorite 구현은 Class의 cast 메서드를 사용해 이 객체 참조를 Class 객체가 가리키는 타입으로 동적 형변환한다.

```java
public <T> T getFavorite(Class<T> type) {
    return type.cast(values.get(type));
}
```

### cast 메서드
cast 메서드는 형변환 연산자의 동적 버전이다. 

이 메서드는 단순히 주어진 인수가 Class 객체가 알려주는 타입의 인스턴스인지를 검사한 다음, 맞다면 그 인수를 그대로 반환하고, 아니면 ClassCastException을 던진다.

> 클라이언트 코드가 깔끔히 컴파일된다면 getFavorite이 호출하는 cast는 ClassCastException을 던지지 않을 것임을 우리는 알고 있다.   
> favorites 맵 안의 값은 해당 키의 타입과 항상 일치함을 알고 있다.