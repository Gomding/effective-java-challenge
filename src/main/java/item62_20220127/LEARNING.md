# 아이템62. 다른 타입이 적절하다면 문자열 사용을 피하라

문자열(String)은 텍스트를 표현하도록 설계되었고, 그 일을 멋지게 수행하고 있다.

String은 자바에서 많은 기능을 제공하고 사용하기 간편해서, 원래 의도와 다른 용도로 쓰이는 경우가 있다. 

### 문자열은 다른 값 타입을 대신하기에 적합하지 않다.

많은 사람이 파일, 네트워크, 키보드 입력으로부터 데이터를 받을 때 주로 문자열을 사용한다.   
자연스러워 보이지만, 입력받을 데이터가 진짜 문자열일 때만 그렇게 하는게 좋다.   

받은 데이터가 수치형이라면 int, float, BigInteger 등 적당한 수치 타입을 사용하는게 맞다.   
'예/아니오' 질문의 답이라면 "true", "false" 문자열이 아닌 boolean으로 변환해야한다.

> 기본 타입이든 참조 타입이든 적절한 값 타입이 있다면 그것을 사용하고, 없다면 값 타입을 만들어서 쓰자

### 문자열은 열거 타입을 대신하기에 적합하지 않다.

열거형을 다룬 아이템 34에서 이미 다룬 이야기다.   
상수를 열거할 때는 문자열보다는 열거 타입이 월등히 낫다.

### 문자열은 혼합 타입을 대신하기에 적합하지 않다.

여러 요소가 혼합된 데이터를 하나의 문자열로 표현하는 것은 좋은 생각이 아니다.

다음 예시를 보자.

```java
String compoundKey = className + "#" + i.next();
```

단점이 매우 많은 방식이다.   
혹여라도 두 요소를 구분해주는 문자 "#"이 두 요소 중 하나에서 쓰였다면 혼란스러운 결과를 초래한다.   

compoundKey가 다음과 같다고 생각해보자.   
"SomeClass#word##1" 일 때 다음 코드는 어떤 결과를 반환하겠는가?

```java
//"SomeClass#word##1"
compoundKey.split("#");

// String 배열의 결과는 다음과 같은 것이다.
// { "SomeClass", "word", "", "1" }
```

또한 각 요소를 개별로 접근하려면 문자열을 파싱해야 해서 느리고, 귀찮고, 오류 가능성도 커진다.

적절한 equals, toString, compareTo 메서드를 제공할 수 없으며, String이 제공하는 기능에만 의존해야 한다.

> 차라리 전용 클래스를 새로 만들어서 사용하자.(이런 클래스는 보통 private 정적 멤버 클래스로 선언한다.)

CompoundKey를 private 정적 멤버 클래스로 선언해서 바꿔보자. 아래 클래스는 완벽하진 않지만 아까보다 나아졌다.
```java
public class Example {
    
    private final CompoundKey compoundKey;

    public Example(CompoundKey compoundKey) {
        this.compoundKey = compoundKey;
    }

    private static class CompoundKey {
        private final String className;
        private final Object key;

        public CompoundKey(String className, Object key) {
            this.className = className;
            this.key = key;
        }
    }
}
```

### 문자열은 권한을 표현하기에 적합하지 않다.

권한(capacity)을 문자열로 표현하는 경우가 종종 있다.

자바의 ThreadLocal 클래스를 예로 들어보자. ThreadLocal은 스레드 자신만의 지역벽수를 가질 수 있도록 하는 기능이다.

자바가 이 기능을 지원한건 2버전 부터다. 이전에는 프로그래머가 직접 구현해야 했다.   
그 당시 이 기능을 설계해야 했던 여러 프로그래머가 독립적으로 방법을 모색하다가 종국에는 똑같은 설계에 이르렀다.   

바로 클라이언트가 제공한 문자열 키로 스레드별 지역변수를 식별한 것이다.

```java
public class ThreadLocal {
    private ThreadLocal() {}
    
    private static final Map<String, Object> threadLocalMap = new HashMap<>();
    
    public static void set(String key, Object value) {
        threadLocalMap.put(key, value);
    }
    
    public static Object get(String key) {
        return threadLocalMap.get(key);
    }
}
```

이 방식의 문제는 스레드 구분용 문자열 키가 전역 이름공간에서 공유된다는 점이다.

이 방식의 의도대로 동작하려면 각 클라이언트가 고유한 키를 제공해야 한다.

만약 두 클라이언트가 서로 소통하지 못해서 같은 키를 쓰기로 했다면, 스레드 고유의 변수를 공유하게 된다.   
결국 두 클라이언트 모두 제대로 기능하지 못할 것이다. (보안도 취약하다.)

악의적인 클라이언트라면 의도적으로 같은 키를 사용하여 다른 클라이언트의 값을 가져올 수도 있다.

이 API는 문자열 대신 위조할 수 없는 키를 사용하면 해결된다. 이 키를 권한(capacity)이라고도 한다.

```java
public class ThreadLocal2 {
    private static final Map<Key, Object> threadLocalMap = new HashMap<>();

    private ThreadLocal2() {}
    
    public static class Key {
        Key() {}
    }
    
    // 위조 불가능한 고유 키를 생성
    public static Key getKey() {
        return new Key();
    }

    public static void set(Key key, Object value) {
        threadLocalMap.put(key, value);
    }

    public static Object get(Key key) {
        return threadLocalMap.get(key);
    }
}
```

앞서의 문자열 기반 API의 문제 두 가지를 모두 해결해주지만, 개선할 여지는 남아있다.

set과 get은 이제 정적 메서드일 이유가 없으니 Key 클래스의 인스턴스 메서드로 바꾸자.

이렇게하면 Key는 더 이상 스레드 지역변수를 구분하기 위한 키가 아니라, 그 자체가 스레드 지역변수가 된다.

결과적으로 지금의 톱레벨 클래스인 ThreadLocal은 별달리 하는 일이 없어지므로 치워버리고, 중첩 클래스 Key의 이름을 ThreadLocal로 바꿔버리자.

```java
public final class ThreadLocal {
    public ThreadLocal();
    public void set(Object value);
    public Object get();
}
```

이 API에서는 get으로 얻은 Object를 실제 타입으로 형변환해 써야 해서 타입 안전하지 않다.

처음의 문자열 기반 API는 타입 안전하게 만들 수 없으며, Key를 사용한 API도 타입안전하게 만들기 어렵다.

하지만 ThreadLocal을 매개변수화 타입으로 선언하면 간단하게 문제가 해결된다.

```java
public final class ThreadLocal<T> {
    public ThreadLocal();
    public void set(T value);
    public T get();
}
```

이제 자바의 java.lang.ThreadLocal과 흡사해졌다.

문자열 기반 API의 문제를 해결해주며, 키 기반 API 보다 빠르고 우아하다.