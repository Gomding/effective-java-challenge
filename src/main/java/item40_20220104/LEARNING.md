# 아이템40. @Override 애너테이션을 일관되게 사용하라

자바가 기본으로 제공하는 애너테이션 중 보통의 프로그래머에게 가장 중요한 것은 @Override 일 것이다.

@Override 애너테이션은 메서드 선언에만 사용할 수 있으며, 이 애너테이션이 달렸다는 것은 상위 타입의 메서드를 재정의 했다는 뜻이다.

@Override 애너테이션의 기능
* 재정의된 메서드인지 컴파일 타임에 검사를 해준다.
  * 해당 메서드가 재정의하는 메서드가 아니라면 컴파일타임에 오류가 발생한다.
* 개발자에게 해당 메서드가 재정의된 메서드임을 시각적으로 보여준다.

@Override 애너테이션을 사용하면 여러가지 악명 높은 버그들을 예방해준다.

아래 예시는 바이그램, 즉 여기서는 영어 알파벳 2개로 구성된 문자열을 표현한다.

```java
class BiGram {
    private final char first;
    private final char second;

    public BiGram(char first, char second) {
        this.first = first;
        this.second = second;
    }

    // 오버라이딩이 아닌, '오버로딩'이 됐다.
    public boolean equals(BiGram b) {
        return b.first == this.first && b.second == this.second;
    }

    public int hashCode() {
        return 31 * first + second;
    }
}

public class BiGramClient {
    public static void main (String[] args) {
        Set<BiGram> set = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            for (char ch = 'a'; ch <= 'z' ; ch++) {
                set.add(new BiGram(ch, ch));
            }
        }
        System.out.println(set.size());
    }
}
```

위 예시의 실행 결과는 26으로 예상하지만 260이 출력된다.

'a'~'z'까지 10번을 반복하며 Set에 넣는데 중복된 요소는 추가되지 않으므로 Set에는 26개만 존재할 것으로 예상한다.   
하지만 실제로는 260개의 데이터가 들어갔다. 무슨 이유일까?

BiGram의 equals 메서드가 제대로 오버라이딩되지 않아서 일어난 일이다. Object의 equals 메서드는 다음과 같다.

```java
public void equals(Object o) {
        ...
}
```

BiGram의 equals 메서드와 차이점이 보이는가?

BiGram의 equals 메서드는 매개변수로 BiGram 타입을 받는다. 따라서 오버라이딩이 아닌 오버로딩이 된 것이다. 
즉, Set 내부에서는 재정의된 equals가 없으니 Object의 equals 메서드를 그대로 사용한 것이다.

Object의 equals 메서드는 == 연산자로 단순히 객체의 주소값을 비교합니다. 결국 260개의 데이터가 들어가게 된 것입니다.

이런 오류를 방지하려면 @Override 애너테이션을 사용해서 재정의 한다는 의도를 명확히 해야한다.

```java
@Override
public boolean equals(BiGram b) {
    b.first == this.first && b.second == this.second;
}
```

위 코드처럼 수정하고 실행하면 컴파일 오류가 발생한다.

```java
error: method does not override or implement a method from a supertype
    @Override
    ^
```

잘못된 부분을 명확히 알려주므로 곧장 올바르게 수정할 수 있다.

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BiGram2 biGram2 = (BiGram2) o;
    return first == biGram2.first && second == biGram2.second;
}
```

> 상위 클래스의 메서드를 재정의하려는 모든 메서드에 @Override 애너테이션을 달자

예외는 한 가지 뿐이다. 구체 클래스에서 상위 클래스(추상 클래스, 인터페이스)의 메서드를 재정의할 때는 굳이 @Override 를 달지 않아도 된다.   
상위 클래스에 구현하지 않은 추상 메서드가 남아있다면 컴파일러가 그 사실을 바로 알려주기 때문이다.

하지만 재정의 메서드도 모두 @Override를 일괄로 붙여주는게 보기 좋다.

@Override를 일관되게 사용한다면 이처럼 실수로 재정의했을 때 경고해줄 것이다.   
재정의할 의도였으나 실수로 오버로딩으로 구현을 했다던지 완전 새로운 메서드를 추가했을 때 알려주는 컴파일 오류의 보완재 역할을 한다.

