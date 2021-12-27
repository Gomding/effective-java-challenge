# 아이템36. 비트 필드 대신 EnumSet을 사용하라

열거한 값들이 주로 (단독이 아닌) 집합으로 사용될 경우, 예전에는 각 상수에 서로 다른 2의 거듭제곱 값을 할당한 정수 열거 패턴을 사용해 왔다.

```java
public class Text {
    public static final int STYLE_BOLD = 1 << 0; // 1
    public static final int STYLE_ITALIC = 1 << 1; // 2
    public static final int STYLE_UNDERLINE = 1 << 2; // 4
    public static final int STYLE_STRIKETHROUGH = 1 << 3; // 8
    
    // 매개변수 styles는 0개 이상의 STYLE_ 상수를 비트별 OR한 값이다.
    public void applyStyles(int styles) {
        ...
    }
}
```

다음과 같은 식으로 비트별 OR를 사용해 여러 상수를 하나의 집합으로 모을 수 있으며, 이렇게 만들어진 집합을 비트 필드(bit field)라 한다.

```java
text.applyStyles(STYLE_BOLD | STYLE_ITALIC);
```

비트 필드를 사용하면 비트별 연산을 사용해 합집합과 교집합 같은 집합 연산을 효율적으로 수행할 수 있다.

하지만 비트 필드는 정수 열거 상수의 단점을 그대로 지니며, 추가로 다음과 같은 문제를 안고 있다.

* 비트 필드 값이 그대로 출력되면 단순한 정수 열거 상수를 출력할 때보다 해석하기가 훨씬 어렵다.   
* 비트 필드 하나에 녹아 있는 모든 원소를 순회하기도 까다롭다.
* 최대 몇 비트가 필요한지를 API 작성 시 미리 예측하여 적절한 타입(보통은 int나 long)을 선택해야 한다.
  * API를 수정하지 않고는 비트 수(32비트 or 64비트)를 더 늘릴 수 없기 때문이다.
  
> 이제는 더 나은 대안이 있다.   
> java.util 의 EnumSet 클래스는 열거 타입 상수의 값으로 구성된 집합을 효과적으로 표현해준다.

* Set 인터페이스를 완벽히 구현한다.
* 타입 안전하다.
* 다른 어떤 Set구현체와도 함께 사용할 수 있다.

EnumSet의 내부는 비트 벡터로 구현되었다.

```java
// EnumSet 내부의 noneOf 메서드
public static <E extends Enum<E>> EnumSet<E> noneOf(Class<E> elementType) {
    Enum<?>[] universe = getUniverse(elementType);
    if (universe == null)
        throw new ClassCastException(elementType + " not an enum");

    if (universe.length <= 64)
        return new RegularEnumSet<>(elementType, universe);
    else
        return new JumboEnumSet<>(elementType, universe);
}
```

원소가 총 64개 이하라면, 즉 대부분의 경우에 EnumSet 전체를 long변수 하나로 표현하여 비트 필드에 비견되는 성능을 보여준다.

removeAll 과 retainAll 같은 대량 작업은 비트를 효율적으로 처리할 수 있는 산술 연산을 써서 구현했다.   
그러면서도 비트를 직접 다룰 때 흔히 겪는 오류들에서 해방된다. 난해한 작업은 EnumSet이 다 처리해주기 떄문.

```java
// EnumSet의 구현체중 하나인 RegularEnumSet의 removeAll, retainAll 메서드
public boolean removeAll(Collection<?> c) {
        if (!(c instanceof RegularEnumSet))
        return super.removeAll(c);

        RegularEnumSet<?> es = (RegularEnumSet<?>)c;
        if (es.elementType != elementType)
        return false;

        long oldElements = elements;
        elements &= ~es.elements;
        return elements != oldElements;
}
        
public boolean retainAll(Collection<?> c) {
        if (!(c instanceof RegularEnumSet))
        return super.retainAll(c);

        RegularEnumSet<?> es = (RegularEnumSet<?>)c;
        if (es.elementType != elementType) {
        boolean changed = (elements != 0);
        elements = 0;
        return changed;
        }

        long oldElements = elements;
        elements &= es.elements;
        return elements != oldElements;
}
```

이제 EnumSet을 사용해 수정해보자.

```java
public class Text2 {
    public enum Style { BOLD, ITALIC, UNDERLINE, STRIKETHROUGH }
    
    // 어떤 Set을 넘겨도 되나, EnumSet이 가장 좋다.
    public void applyStyles(Set<Style> styles) {
        
    }
}
```

EnumSet은 집합 생성 등 다양한 기능의 정적 팩터리를 제공한다.

```java
// of 메서드. 특정 요소를 집합으로 만든다.
EnumSet.of(Style.BOLD, Style.ITALIC);

// noneOf 메서드. 해당 열거 타입의 요소를 가질 수 있는 빈 집합을 만든다.
EnumSet.noneOf(Style.class);

// allOf 메서드. 해당 열거 타입의 요소를 전부 가지는 집합을 만든다.
EnumSet.allOf(Style.class);

text.applyStyles(EnumSet.of(Style.BOLD, Style.ITALIC));
```

applyStyles 메서드가 ```EnumSet<Style>```이 아닌 ```Set<Style>```을 받은 이유를 생각해보자.

모든 클라이언트가 EnumSet을 건네리라 짐작되는 상황이라도 이왕이면 인터페이스로 받는 게 일반적으로 좋은 습관이다.(아이템 64)

이렇게 하면 좀 특이한 클라이언트가 다른 Set 구현체를 넘기더라도 처리할 수 있다.

> EnumSet의 유일한 단점으로는 불변 EnumSet을 만들 수 없다는 것이다.