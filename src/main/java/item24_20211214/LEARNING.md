# 아이템24. 멤버 클래스는 되도록 static으로 만들라

중첩 클래스란?   
다른 클래스 안에 정의된 클래스

중첩 클래스의 종류는 네 가지다.
* 정적 멤버 클래스
* (비정적) 멤버 클래스
* 익명 클래스
* 지역 클래스

이 중 첫 번째(정적 멤버 클래스)를 제외한 나머지는 내부 클래스(inner class)에 해당한다.

이번 아이템에서는 각각의 중첩 클래스를 **언제** 그리고 **왜 사용해야 하는지** 이야기한다.

### 정적 멤버 클래스

정적 멤버 클래스는 다음 부분을 제외하고는 일반 클래스와 똑같다.
* 다른 클래스 안에 선언 
* 바깥 클래스의 private 멤버에도 접근할 수 있다

정적 멤버 클래스는 다른 정적 멤버와 똑같은 접근 규칙을 적용받는다.      
private으로 선언하면 바깥 클래스에서만 접근할 수 있다.

> 정적 멤버 클래스는 흔히 바깥 클래스와 함께 쓰일 때만 유용한 public 도우미 클래스로 쓰인다.

계산기가 지원하는 연산 종류를 열거하는 열거타입을 예로 생각해보자.

```java
class Calculator {
    enum Operation { PLUS, MINUS, MULTIPLE, DIVIDED }
}
```

위의 예처럼 구현했을 때 Calculator의 클라이언트에서 Calculator.Operation.PLUS 나 Calculator.Operation.MINUS 같은 형태로 원하는 연산을 참조할 수 있다.

```java
public class StaticClassEx {
    public static void main(String[] args) {
        Calculator.Operation plus = Calculator.Operation.PLUS;
        Calculator.Operation minus = Calculator.Operation.MINUS;
    }
}
```

### 비정적 멤버 클래스

정적 멤버 클래스와 비정적 멤버 클래스의 구문상 차이는 단지 static이 붙어있고 없고 뿐이다.   
하지만 의미상 차이는 꽤 크다.

비정적 멤버 클래스의 인스턴스는 바깥 클래스의 인스턴스와 암묵적으로 연결된다.   
그래서 비정적 멤버 클래스의 인스턴스 메서드에서 정규화된 this를 사용해 바깥 인스턴스의 메서드를 호출하거나 바깥 인스턴스의 참조를 가져올 수 있다.

```java
class Account {
    
    void doSomething() {
        System.out.println("do something");
    }
    
    public class Money {
        void doAnything() {
            Account.this.doSomething();
        }
    }
}
```

정규화된 this란 클래스명.this 형태로 바깥 클래스의 이름을 명시하는 용법을 말합니다.

따라서 개념상 중첩 클래스의 인스턴스가 바깥 인스턴스와 독립적으로 존재할 수 있다면 정적 멤버 클래스로 만들어야 한다.

> 비정적 멤버 클래스는 바깥 인스턴스 없이는 생성할 수 없기 떄문이다.

비정적 멤버 클래스의 인스턴스와 바깥 인스턴스 사이의 관계는 멤버 클래스가 인스턴스화될 때 확립되며, 더 이상 변경할 수 없다.
이 관계는 바깥 클래스의 인스턴스 메서드에서 비정적 멤버 클래스의 생성자를 호출할 때 자동으로 만들어지는게 보통이다.   
드물게 직접 **바깥 인스턴스의 클래스.new MemberClass(args)**를 호출해 수동으로 만들기도 한다.

```java
Account.Money money = account.new Money(1);
```

예상할 수 있듯, 이 관계 정보는 비정적 멤버 클래스의 인스턴스 안에 만들어져 메모리 공간을 차지하며, 생성 시간도 더 걸린다.

비정적 멤버 클래스는 어댑터를 정의할 때 자주 쓰인다. 즉, 어떤 클래스의 인스턴스를 감싸 마치 다른 클래스의 인스턴스처럼 보이게하는 뷰로 사용하는 것이다.

Map 인터페이스의 구현체들은 보통 (keySet, entrySet, values 메서드가 반환하는) 자신의 컬렉션 뷰를 구현할 때 비정적 멤버 클래스를 사용한다.

```java
// HashSet 내부에 있는 비정적 멤버 클래스 EntrySet
final class EntrySet extends AbstractSet<Map.Entry<K,V>> {
    public final int size() {
        return size;
    }
    
    public final void clear() {
        HashMap.this.clear();
    }
    
    public final Iterator<Map.Entry<K, V>> iterator() {
        return new EntryIterator();
    }
    ...
}
```

비슷하게, Set과 List 같은 다른 컬렉션 인터페이스 구현들도 자신의 반복자를 구현할 때 비정적 멤버 클래스를 주로 사용한다.

```java
public class MySet extends AbstractSet {
    @Override
    public Iterator iterator() {
        return new MyIterator();
    }

    @Override
    public int size() {
        return 0;
    }
    
    private class MyIterator implements Iterator<E> {
        ...
    }
}
```

### 멤버 클래스에서 바깥 인스턴스에 접근할 일이 없다면 무조건 static을 붙여서 정적 멤버 클래스로 만들자

static을 생략하면 바깥 인스턴스로의 숨은 외부 참조를 가지게 된다.

> 이 참조를 저장하려면 시간과 공간이 소비된다. 더 심각한 문제는 가비지 컬렉션이 바깥 클래스의 인스턴스를 수거하지 못하는 메모리 누수가 발생할 수 있다는 점이다!!

참조가 눈에 보이지 않으니 문제의 원인도 찾기 어려워진다.

private 정적 멤버 클래스는 흔히 바깥 클래스가 표현하는 객체의 구성요소를 나타낼 때 쓴다.

Map을 예로들면 Entry 객체들은 맵과 연관되어 있지만 Entry의 메서드들은 Map을 직접 사용하지는 않는다.   
따라서 Entry는 비정적 멤버 클래스로 표현하는 것은 낭비고, private 정적 멤버 클래스가 가장 알맞다.

> static을 깜빡해도 정상 동작하지만 모든 Entry가 바깥 Map으로의 참조를 가지게 되어 공간과 시간을 낭비할 것이다.

멤버 클래스가 공개된 클래스의 public 이나 protected 멤버라면 정적이냐 아니냐는 두 배로 중요해진다.   
멤버 클래스 역시 공개 API가 되므로, 혹시라도 향후 릴리스에서 static을 붙이면 하위 호환성이 깨진다.

```java
// 멤버 클래스가 공개 API 인 경우
class RootClass {
    public class MemberClass {
        
    }
}

// 사용 예시
public class NonStaticClassEx2 {
    public static void main(String[] args) {
        RootClass rootClass = new RootClass();
        RootClass.MemberClass memberClass = rootClass.new MemberClass();
    }
}

// 멤버 클래스를 static 으로 변경
class RootClass {
    public static class MemberClass {

    }
}

public class NonStaticClassEx2 {
    public static void main(String[] args) {
        RootClass rootClass = new RootClass();
//      RootClass.MemberClass memberClass = rootClass.new MemberClass(); 
//      멤버 클래스가 static 이므로 rootClass.new MemberClass(); 에서 컴파일 에러 발생!! 
//      이것을 하위 호환성이 깨졌다 라고 말한다.
        RootClass.MemberClass memberClass = new RootClass.MemberClass(); 
    }
}
```

### 익명 클래스

익명 클래스는 바깥 클래스의 멤버도 아니다. 멤버와 달리, 쓰이는 시점에 선언과 동시에 인스턴스가 만들어진다.   
코드의 어디서든 만들 수 있다. 그리고 오직 비정적인 문맥에서 사용될 때만 바깥 클래스의 인스턴스를 참조할 수 있다.

```java
class RootClass2 {
    public void doSomething() {
        MyInterface anonymousClassInstance = new MyInterface() {
            @Override
            public void doSomething() {
                System.out.println("do something");
            }
        };
    }
}

interface MyInterface {
    void doSomething();
}
```

정적 문맥에서라도 상수 변수 이외에 정적 멤버는 가질 수 없다. 즉, 상수 표현을 위해 초기화된 final 기본 타입과 문자열 필드만 가질 수 있다.

### 익명 클래스의 제약

익명 클래스는 응용하는 데 제약이 많은 편이다.
* 선언한 지점에서만 인스턴스를 만들 수 있다.
* instanceof 검사나 클래스의 이름이 필요한 작업은 수행할 수 없다.
* 여러 인터페이스를 구현하거나, 클래스를 상속할 수 없다.
* 익명 클래스를 사용하는 클라이언트는 익명 클래스의 상위 타입에서 상속한 멤버 외에는 호출할 수 없다.
* 익명 클래스는 표현식 중간에 등장하므로 가독성이 떨어진다.

> 자바7 부터는 람다를 지원하므로 작은 함수 객체나 처리객체는 익명 클래스 대신 람다를 활용해서 만들 수 있다.

익명 클래스의 또 다른 주 쓰임은 정적 팩터리 메서드를 구현할 때다

### 지역 클래스
네 가지 중첩 클래스 중 가장 드물게 사용된다.

지역 클래스는 지역변수를 선언할 수 있는 곳이면 실질적으로 어디서든 선언할 수 있다.
(유효 범위도 지역변수와 같다.)

다른 세 가지 중첩 클래스와의 공통점도 하나씩 가지고 있다.
* 멤버 클래스처럼 이름이 있고 반복해서 사용할 수 있다.
* 익명 클래스처럼 비정적 문맥에서 사용될 때만 바깥 인스턴스를 참조할 수 있다.
* 정적 멤버는 가질 수 없으며 가독성을 위해서 짧게 작성해야 한다.

### 핵심 정리
* 중첩 클래스에는 네 가지가 있다.
  * 정적 멤버 클래스
  * 비정적 멤버 클래스
  * 익명 클래스
  * 지역 클래스
* 메서드 밖에서도 사용해야 하거나 메서드 안에 정의하기엔 너무 길다면 멤버 클래스로 만든다.
* 멤버 클래스의 인스턴스 각각이 바깥 인스턴스를 참조한다면 비정상적으로, 그렇지 않으면 정적으로 만들자.
* 중첩 클래스가 한 메서드 안에서만 쓰이면서 그 인스턴스를 생성하는 지점이 단 한 곳이고 해당 타입으로 쓰기에 적합한 클래스나 인터페이스가 이미 있다면 익명클래스로 만든다. 그렇지 않으면 지역 클래스로 만들자

### 의문점

> 비정적 멤버 클래스의 인스턴스와 바깥 인스턴스 사이의 관계는 멤버 클래스가 인스턴스화될 때 확립되며, 더 이상 변경할 수 없다.
* 예상할 수 있듯, 이 관계 정보는 비정적 멤버 클래스의 인스턴스 안에 만들어져 메모리 공간을 차지하며, 생성 시간도 더 걸린다.
* 위 두 문장에 어떤 관계가 있는지 정리되지 않음

> 비정적 멤버 클래스는 어댑터를 정의할 때 자주 쓰인다. 즉, 어떤 클래스의 인스턴스를 감싸 마치 다른 클래스의 인스턴스처럼 보이게하는 뷰로 사용하는 것이다.
