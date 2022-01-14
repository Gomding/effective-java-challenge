# 아이템51. 메서드 시그니처를 신중히 설계하라

이번 아이템에는 개별 아이템으로 두기 애매한 API 설계 요령들을 모아 놓았다.   
이 요령들을 잘 활용하면 배우기 쉽고, 쓰기 쉬우며, 오류 가능성이 적은 API를 만들 수 있을 것이다.

### 목록
* 메서드 이름을 신중히 짓자.
* 편의 메서드를 너무 많이 만들지 말자.
* 매개변수 목록은 짧게 유지하자.
  - 여러 메서드로 쪼갠다.
  - 매개변수 여러 개를 묶어주는 도우미 클래스를 만들자.
  - 객체 생성에 사용한 빌더 패턴을 메서드에 응용하자.
* 매개변수의 타입으로는 클래스보다는 인터페이스가 낫다.
* 매개변수 타입으로 boolean보다는 원소 2개짜리 열거타입이 낫다.

### 메서드 이름을 신중히 짓자.

항상 표준 명명 규칙을 따라야 한다.

이해할 수 있고, 같은 패키지에 속한 다른 이름들과 일관되게 짓는 게 최우선 목표다.

그 다음 목표는 개발자 커뮤니티에서 널리 받아들여지는 이름을 사용하는 것이다.

긴 이름은 피하자, 애매하면 자바 라이브러리의 API 가이드를 참조하라.   
자바 라이브러리가 워낙 크다보니 일관되지 않은 이름도 제법 있지만 대부분은 납득할 만한 수준이다.

```java
class Car {
    private int position;
    
    public void move() {
        position++;
    }

    public int getPosition() {
        return position;
    }
}

class User {
    private final String name;

    public User(String name) {
        this.name = name;
    }

    // 같은 패키지에 속한 다른 이름들과 일관되게 짓는 게 최우선 목표다.
    // Car 클래스에서는 접근자 메서드 이름에 get필드명() 을 사용했지만 여기서는 필드명()을 사용했다.
    // 이것은 메서드 이름이 통일되지 않은 경우로 클라이언트에게 혼란을 줄 수 있다.
    public String name() {
        return name;
    }
}
```

### 편의 메서드를 너무 많이 만들지 말자

모든 메서드는 각각 자신의 책임을 다해야 한다. 메서드가 너무 많은 클래스는 익히고, 사용하고, 문서화하고, 테스트하고, 유지보수하기 어렵다.   
인터페이스도 마찬가지다. 메서드가 너무 많으면 이를 구현하는 사람과 사용하는 사람 모두를 고통스럽게 한다.

클래스나 인터페이스는 자신의 각 기능을 완벽히 수행하는 메서드로 제공해야 한다.

아주 자주 쓰일 경우에만 별도의 약칭 메서드를 두기 바란다. 자주 쓰인다는 확신이 없다면 편의 메서드는 만들지 말자.   

> 메서드를 작게 분리하면 좋은 효과가 생기지만 무조건 좋은 것은 아니다.
> 메서드가 너무 많으면 오히려 가독성이 떨어질 수도 있다.
> 이를 잘 고려해서 메서드를 분리하자. (메서드가 하나의 책임만을 가지고 수행하고 있다면 더 이상 메서드를 분리하지 말자.)

아래는 이전 아이템45의 아나그램 예시코드에 편의 메서드를 과하게 만들어놓은 케이스다. 원본보다 가독성이 훨씬 떨어지는것을 경험할 수 있다.   
밑의 예시는 과한 예시를 보여주기 위해 이상한 편의 메서드들이 많다. 이를 통해 편의 메서드는 자주 사용되고, 꼭 필요한 경우에만 만들자 라는걸 알려주고 싶다.

```java
public class ConvenienceMethodExample {
}

// 과한 편의 메서드의 극단적인 예시
class Anagrams {
    public static void main(String[] args) {
        String[] words = {"staple", "aplest", "abc", "wood", "doow", "wdoo"};
        anagramsOverMinGroupSize(words, 2);
    }

    public static void anagramsOverMinGroupSize(String[] words, int minGroupSize) {
      minGroupSizeFilterGroupingByWords(words, minGroupSize)
                .forEach(group -> System.out.println(group.size() + ": " + group));
    }

    private static Stream<List<String>> minGroupSizeFilterGroupingByWords(String[] words, int minGroupSize) {
        return groupingByWords(words).stream()
                .filter(group -> group.size() >= minGroupSize);
    }

    private static Collection<List<String>> groupingByWords(String[] words) {
        return Arrays.stream(words)
                .collect(groupingBy(word -> alphabetize(word)))
                .values();
    }

    private static String alphabetize(String s) {
        char[] chars = s.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }
}
```

### 매개변수 목록은 짧게 유지하자.

메서드의 매개변수는 4개 이하가 좋다. 4개가 넘어가면 매개변수를 전부 기억하기가 쉽지 않다.   
여러분이 만든 API에 이 제한을 넘는 메서드가 많다면 프로그래머들은 API 문서를 옆에 끼고 개발해야 할 것이다. (수 많은 매개변수가 무엇을 의미하는지 파악하기 위해)
이는 IDE를 통해 극복은 가능하다. 하지만 매개변수는 적은 쪽이 좋다.

특히 **같은 타입의 매개변수가 여러 개 연달아 나오는 경우가 특히 해롭다.**   
사용자가 매개변수 순서를 기억하기 어렵고, 매개변수의 순서를 바꿔서 주는 실수를 하기 쉽다. 더 큰 문제는 실수임에도 정상적으로 컴파일된다는 것이다. 의도와 다르게 동작할 뿐!

```java
public class TooMuchArgumentEx {

    public static void main(String[] args) {
        String country = "한국";
        String city = "서울";
        String detail = "송파구 롯데타워 최상층 살고싶호";
        String zipCode = "11111";
        Address address = new Address(country, city, detail, zipCode);
        System.out.println("update 호출 전 주소 : " + address);

        String newZipCode = "33333";
        address.update(newZipCode, detail, city, country);
        System.out.println("update 호출 후 주소 : " + address);
        
        // 실행결과
      // 모든 매개변수가 String 이다보니 순서를 헷갈릴 수 있다. 따라서 매개변수의 순서를 잘못 넣으면 대참사가 일어난다.
//      update 호출 전 주소 : Address{country='한국', city='서울', detail='송파구 롯데타워 최상층 살고싶호', zipCode='11111'}
//      update 호출 후 주소 : Address{country='33333', city='송파구 롯데타워 최상층 살고싶호', detail='서울', zipCode='한국'}
    }
}

class Address {
    private String country;
    private String city;
    private String detail;
    private String zipCode;

    public Address(String country, String city, String detail, String zipCode) {
        this.country = country;
        this.city = city;
        this.detail = detail;
        this.zipCode = zipCode;
    }

    public void update(String country, String city, String detail, String zipCode) {
        this.country = country;
        this.city = city;
        this.detail = detail;
        this.zipCode = zipCode;
    }

    @Override
    public String toString() {
        return "Address{" +
                "country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", detail='" + detail + '\'' +
                ", zipCode='" + zipCode + '\'' +
                '}';
    }
}
```

과하게 긴 매개변수 목록을 짧게 줄여주는 기술 세 가지를 소개하겠다.

### 긴 매개변수 목록을 줄이기 - 1. 여러 메서드로 쪼갠다.

쪼개진 메서드 각각은 원래 매개변수 목록의 부분집합을 받는다. 잘못하면 메서드가 너무 많아질 수 있지만, 직교성(orthogonality)을 높여 오히려 메서드 수를 줄여주는 효과도 있다.   

> 직교성이란?   
> 소프트웨어 설계 영역에서 "직교성이 높다" 라는것은 "공통점이 없는 기능들이 잘 분리되어 있다" 혹은 "기능을 원자적으로 쪼개 제공한다" 정도로 해석할 수 있다.
> 
> 직교성을 높여 오히려 메서드 수를 줄여준다는 것은 무슨 의미인가?   
> 만약 기본 기능 3개가 있다면 이를 조합해서 만들 수 있는 기능은 7가지나 된다.   
> 중복 사용되는 메서드를 잘 분리해놓으면 직교성이 높아진다. 이를 위해 메서드는 하나의 책임만 갖도록 하는것이 좋다.
> 
> 직교성 개념은 소프트웨어 설계 전 분야로 확대할 수 있다.   
> 마이크로 서비스 아키텍처는 직교성이 높고, 이와 대비되는 모놀리식 아키텍처는 직교성이 낮다고 할 수 있다.

java.util.List 인터페이스가 좋은 예시다.    
리스트에서 주어진 원소의 인덱스를 찾아야 하는데, 전체 리스트가 아니라 지정된 범위의 부분리스트에서의 인덱스를 찾는다고 해보자.   
* 이 기능을 하나의 메서드로 구현하려면 '부분 리스트의 시작', '부분 리스트의 끝', '찾을 원소'까지 총 3개의 매개변수가 필요하다.   
* List는 그 대신 부분리스트를 반환하는 subList 메서드와 주어진 원소의 인덱스를 알려주는 indexOf 메서드를 별개로 제공한다.
  * 두 가지 메서드를 조합해서 원하는 목적을 이룰 수 있다.

### 긴 매개변수 목록을 줄이기 - 2. 매개 변수 여러개를 묶어주는 도우미 클래스 만들기

일반적으로 이런 도우미 클래스는 정적 멤버 클래스로 둔다.   
특히 잇따른 매개변수 몇 개를 독립된 하나의 개념으로 볼 수 있을 때 추천하는 기법이다.

```java
class Account {
    private String name;
    private int age;
    private String country;
    private String city;
    private String zipCode;

    public Account() {
    }

    public void setAccount(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    public void setAddress(String country, String city, String zipCode) {
        this.country = country;
        this.city = city;
        this.zipCode = zipCode;
    }
}

// 많은 매개변수를 개선한 예시
class Account2 {
    private String name;
    private int age;
    private Address address;

    public Account2() {
    }

    public void setAccount(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
    
    static class Address {
        private String country;
        private String city;
        private String zipCode;

        public Address(String country, String city, String zipCode) {
            this.country = country;
            this.city = city;
            this.zipCode = zipCode;
        }
    }
}
```

### 긴 매개변수 목록을 줄이기 - 3. 앞서의 두 기법을 혼합

객체 생성에 사용한 빌더 패턴을 메서드 호출에 응용한다고 보면 된다.

이 기법은 매개변수가 많을 때, 특히 그 중 일부는 생략해도 괜찮을 때 도움이 된다.   
먼저 모든 매개변수를 하나로 추상화한 객체를 정의 -> 클라이언트에서 이 객체의 setter 메서드를 호출해 필요한 값을 설정하게 하는 것이다.   
이때 각 setter 메서드는 매개변수 하나 혹은 서로 연관된 몇 개만 설정하게 한다.

클라이언트는 먼저 필요한 매개변수를 설정한 다음, execute 메서드를 호출해서 앞서 설정한 매개변수들의 유효성을 검사한다. 마지막으로 설정이 완료된 객체를 넘겨 원하는 계산을 수행한다.

### 매개변수의 타입으로는 클래스보다 인터페이스가 더 낫다.

매개변수로 적합한 인터페이스가 있다면 그 인터페이스를 매개변수의 타입으로 사용하자.

예를 들어 메서드 매개변수에 HashMap 타입으로 고정한다면?   
Map의 다른 구현체는 매개변수로 넘길 수 없게 된다.(해당 구현체로 변환을 하면 가능하지만 비싼 복사 비용이 발생한다.)

따라서 가능하면 Map을 매개변수의 타입으로 지정하자. 그렇게하면 HashMap 뿐만 아니라 TreeMap, ConcurrentHashMap, Map의 새로운 구현체까지도 인수로 건네줄 수 있다.

> 인터페이스 대신 클래스를 사용하면 클라이언트에게 특정 구현체만 사용하도록 제한하는 꼴이다.

### boolean보다는 원소 2개짜리 열거 타입이 낫다.

이 원칙은 메서드 이름상 boolean을 받는게 의미상 더 명확하다면 boolean을 사용해도 된다.

열거 타입을 사용하면 코드를 읽고 쓰기가 더 쉬워진다. 또한 나중에 선택지를 추가하기도 좋다.

예를 들어 화씨 온도(Fahrenheit)와 섭씨 온도(Celsius)를 원소로 정의한 열거 타입이다.

```java
public enum TemperatureScale {
    FAHRENHEIT, CELSIUS
}
```

온도계 클래스의 정적 팩터리 메서드가 이 열거 타입을 입력받아 적합한 온도계 인스턴스를 생성해준다고 해보자.   
```Thermometer.newInstance(true)```보다는 ```Thermometer.newInstance(TemperatureScale.CELSIUS)```가 하는 일을 훨씬 명확히 알려준다.