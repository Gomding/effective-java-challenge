# 아이템65. 리플렉션보다는 인터페이스를 사용하라

리플렉션 기능(java.lang.reflect)을 이용하면 프로그램에서 임의의 클래스에 접근할 수 있다.

Class 객체가 주어지면 다음과 같은 인스턴스를 생성할 수 있다.
* Constructor - 생성자
* Method - 메서드
* Field - 필드

또한 위의 생성자, 메서드, 필드 인스턴스로는 해당 클래스의 요소들을 가져올 수 있다.
* 멤버 이름
* 필드 타입
* 메서드 시그니쳐
* 등등

Constructor, Method, Field 인스턴스를 활용해 각각에 연결된 실제 생성자, 메서드, 필드를 조작할 수도 있다.
* Constructor - 인스턴스 생성
* Method - 메서드 호출
* Field - 필드에 접근

Method 의 invoke 메서드는 메서드를 호출할 수 있게 해준다.
(물론 일반적인 보안 제약사항은 준수해야 한다.)

리플렉션을 이용하면 컴파일 당시에 존재하지 않던 클래스도 이용할 수 있다.

### 리플렉션의 단점 

리플렉션에도 단점이 있다.

* 컴파일타임 타입 검사가 주는 이점을 하나도 누릴 수 없다.
  * 예외 검사도 마찬가지다.
  * 리플렉션을 사용해 존재하지 않는 혹은 접근할 수 없는 메서드를 호출하려 시도하면 런타임 예외가 발생한다.
* 리플렉션을 이용하면 코드가 지저분해지고 장황해진다. 읽기에 지루하고 이해하기도 어렵다.
* 성능이 떨어진다. 리플렉션을 통한 메서드 호출은 일반 메서드 호출보다 훨씬 느리다.

> 코드 분석 도구나 의존관계 주입 프레임워크처럼 리플렉션을 써야하는 복잡한 애플리케이션이 몇 가지 있다.   
> 하지만 이런 도구들마저 리플렉션 사용을 점차 줄이고 있다.

만약 우리가 애플리케이션에 리플렉션이 필요한지 확신할 수 없다면 아마도 필요없을 가능성이 크다.

### 리플렉션은 아주 제한된 형태로만 사용해야 단점을 피하고 이점을 취할 수 있다.

컴파일타임에 이용할 수 없는 클래스를 사용해야만 하는 프로그램은 비록 컴파일타임이라도 적절한 인터페이스나 상위 클래스를 이용할 수는 있을 것이다.

리플렉션은 인스턴스 생성에만 쓰고, 이렇게 만든 인스턴스는 인터페이스나 상위 클래스로 참조해 사용하자.

다음 예시를 보자.   
다음 프로그램은 ```Set<String>``` 인터페이스의 인스턴스를 생성하는데, 정확한 클래스는 명령줄의 첫 번째 인수로 확정한다.   
그리고 생성한 집합(Set)에 두 번째 인수들을 추가한 다음 화면에 출력한다.   
첫 번째 인수와 상관없이 이후의 인수들은 중복은 제거한 후 출력한다. 
반면, 이 인수들이 출력되는 순서는 첫 번째 인수로 지정한 클래스가 무엇이냐에 따라 달라진다.

HashSet을 지정하면 무작위 순서, TreeSet을 지정하면 알파벳 순서로 출력될 것이다.

```java
public class SetEx1 {

    public static void main(String[] args) {
        SetEx1 setEx1 = new SetEx1();
        setEx1.generateStringSet("java.util.HashSet", "word1", "word2", "word3");
    }

    private Set<String> generateStringSet(String typeNameOfSet, String... args) {
        // 클래스 이름을 Class 객체로 변환
        Class<? extends Set<String>> cl = null;
        try {
            cl = (Class<? extends Set<String>>) Class.forName(typeNameOfSet);
        } catch (ClassNotFoundException e) {
            fatalError("클래스를 찾을 수 없습니다.");
        }

        // 생성자를 얻는다.
        Constructor<? extends Set<String>> cons = null;
        try {
            cons = cl.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            fatalError("매개변수 없는 생성자를 찾을 수 없습니다.");
        }

        // Set의 인스턴스를 만든다.
        Set<String> s = null;
        try {
            s = cons.newInstance();
        } catch (IllegalAccessException e) {
            fatalError("생성자에 접근할 수 없습니다.");
        } catch (InstantiationException e) {
            fatalError("클래스를 인스턴스화할 수 없습니다.");
        } catch (InvocationTargetException e) {
            fatalError("생성자가 예외를 던졌습니다: " + e.getCause());
        } catch (ClassCastException e) {
            fatalError("Set을 구현하지 않은 클래스입니다.");
        }

        // 생성한 집합을 사용한다.
        s.addAll(Arrays.asList(args));
        System.out.println(s);

        return s;
    }

    private void fatalError(String msg) {
        System.err.println(msg);
        System.exit(1);
    }
}

```

이 예시에는 리플렉션의 두 가지 단점을 보여준다.

첫 번째. 런타임에 총 여섯가지나 되는 예외를 던질 수 있다. 리플렉션을 쓰지않고 인스턴스를 그냥 생성했다면 컴파일 타임에 잡을 수 있는 예외들이다.

> 예외의 경우 자바 7부터 ReflectiveOperationException을 잡으면 예외를 잡는 코드를 줄일 수 있다.

두 번째. 클래스 이름만으로 인스턴스를 생성해내기 위해 25줄이나 되는 코드를 작성했다.

위 코드를 컴파일하면 비검사 형변환 경고가 뜬다. 
하지만 ```Class<? extends Set<String>>```으로의 형변환은 심지어 명시한 클래스가 Set을 구현하지 않았더라도 성공할 것이라, 실제 문제로 이어지는건 아니다.
(단 그 클래스의 인스턴스를 생성하려 할 때 ClassCastException을 던지게 된다.)

### 리플렉션은 런타임에 존재하지 않을 수도 있는 다른 클래스, 메서드, 필드와의 의존성을 관리할 때 적합하다.

이 기법은 버전이 여러 개 존재하는 외부 패키지를 다룰 때 유용하다.   
가동할 수 있는 최소한의 환경, 즉 수로 가장 오래된 버전만을 지원하도록 컴파일한 후, 이후 버전의 클래스와 메서드 등은 리플렉션으로 접근하는 방식이다.   

> 이렇게 하려면 접근하려는 새로운 클래스나 메서드가 런타임에 존재하지 않을 수 있다는 사실을 반드시 감안해야 한다.   
> 즉, 같은 목적을 이룰 수 있는 대체 수단을 이용하거나 기능을 줄여 동작하는 등의 적절한 조치를 취해야 한다.

