# 아이템46. 스트림에서는 부작용 없는 함수를 사용하라

스트림을 처음 사용하면 이해하기 어렵다. 어떻게 성공할지라도 장점이 와 닿지 않을 수 있다.   
이는 스트림이 그저 또 하나의 API가 아닌, 패러다임이기 때문이다.

> 패러다임이란?   
> 한 시대의 사람들의 견해나 사고를 근본적으로 규정하고 있는 인식의 체계. 또는, 사물에 대한 이론적인 틀이나 체계

스트림이 제공하는 표현력, 속도, (상황에 따른) 병렬성을 얻으려면 API는 말할 것도 없고 이 패러다임까지 함께 받아들여야 한다.

### 스트림 패러다임의 핵심
계산을 일련의 변환(transformation)으로 재구성하는 부분이다.   
이때 각 변환 단계는 가능한 한 이전 단계의 결과를 받아 처리하는 순수 함수여야 한다.

> 순수 함수란?   
> 오직 입력만이 결과에 영향을 주는 함수   
> 다른 가변 상태를 참조하지 않고, 함수 스스로도 다른 상태를 변경하지 않는다.

순수 함수이기 위해서는 중간 단계든 최종 단계든 스트림 연산에 건네는 함수 객체는 부작용(side-effect)가 없어야 한다.

다음 코드는 문장들에서 각 단어 수를 세어 빈도표를 만드는 일을 한다.

```java
public class StreamEx {
    public static void main(String[] args) {
        final String sentence = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";
        final String[] words = sentence.split(" ");
        final Map<String, Long> freq = new HashMap<>();
        Arrays.stream(words)
                .forEach(word -> freq.merge(word.toLowerCase(), 1L, Long::sum));
    }
}
```

위 예시는 문제가 존재한다.   
스트림, 람다, 메서드 참조를 사용했고, 결과도 잘 나온다. 하지만 스트림 코드라고 할 수 없다.

스트림 코드를 가장한 반복적 코드다. 스트림 API의 이점을 살리지 못하여 같은 기능의 반복적 코드보다 길고, 읽기 어렵고, 유지보수에도 좋지 않다.   
또한 이 코드의 모든 작업은 최종 연산인 forEach에서 일어나는데, 외부 상태(빈도표)를 수정하는 람다를 실행하면서 문제가 생긴다.   
(위에서 얘기했듯 순수 함수로써 외부의 상태를 참조하거나 변경하지 않고, 오직 입력만이 결과에 영향을 줘야한다. 외부의 상태를 참조하는 순간 외부의 변경이 결과에 영향을 미칠 수 있다.)

forEach가 그저 스트림이 수행한 연산 결과를 보여주는 일 이상을 하는 것을 보니 나쁜 코드일 것 같은 냄새가 난다.   
또한 단순 forEach라는 단순 반복을 위해 스트림을 생성하는 비용을 썼다. 차라리 아래와 같이 작성하는데 비용 측면에서 더 좋다.

```java
public class StreamEx2 {
    public static void main(String[] args) {
        final String sentence = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";
        final String[] words = sentence.split(" ");
        final Map<String, Long> freq = new HashMap<>();
        for (String word : words)
            freq.merge(word.toLowerCase(), 1L, Long::sum);
    }
}
```

올바른 스트림을 활용해서 코드를 수정하자.

```java
public class StreamEx3 {
    public static void main(String[] args) {
        final String sentence = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";
        final String[] words = sentence.split(" ");
        Map<String, Long> freq = Arrays.stream(words)
                .collect(groupingBy(String::toLowerCase, counting()));
    }
}
```

앞의 예시들과 같은 일을 하지만, 스트림 API를 제대로 활용했다. 또한 짧고 명확하다.   
앞서 사용했던 스트림의 forEach 연산은 스트림의 최종 연산 중 기능이 가장 적도 가장 스트림 답지 않다.   
(앞에서도 말했듯 그냥 반복문을 사용하는것 보다 스트림 생성 비용이 더 생긴다.)

forEach 연산은 스트림 계싼 결과를 보고할 때만 사용하고, 계산하는데는 사용하지 말자.

> 물론 가끔은 계산 결과를 기존 컬렉션에 추가하는 등의 용도로 어쩔 수 없이 사용해야할 때는 있다. 그것이 코드상 더 깔끔할때도 존재한다.

바로 앞의 예시에서 collector(수집기)를 사용하는데, 스트림을 사용하려면 꼭 배워야하는 새로운 개념이다.

java.util.stream.Collectors 클래스는 메서드를 무려 39갸나 가지고 있고, 그 중에는 타입 매개변수가 5개나 되는 것도 있다.   
다행히 복잡한 세부 내용은 잘 몰라도 이 API의 장점을 대부분 활용할 수 있다.   
익숙해지기 전에는 Collector 인터페이스를 잠시 잊고, 그저 **축소 전략**을 캡슐화한 블랙박스 객체라고 생각하자.

여기서 **축소**는 스트림의 원소들을 객체 하나에 취합한다는 뜻이다. 수집가가 생성하는 객체는 일반적으로 컬렉션이다. 그래서 collector라는 이름을 쓴다.   
(Collector는 자바의 Collection API를 활용하는 것이지 Collection API에 속하는것이 아님을 주의하자.)

수집기를 사용하면 스트림의 원소를 손쉽게 컬렉션으로 모을 수 있다.   
수집기는 총 세 가지로, toList(), toSet(), toCollection(collectionFactory)가 그 주인공이다.
차례로 리스트(List), 집합(Set), 프로그래머가 지정한 컬렉션 타입을 반환한다.

단어 빈도표에서 가장 흔한 단어 10개를 뽑아내는 스트림 파이프라인을 작성해보자.

```java
public class StreamEx4 {
    public static void main(String[] args) {
        final String sentence = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";
        final String[] words = sentence.split(" ");
        Map<String, Long> freq = Arrays.stream(words)
                .collect(groupingBy(String::toLowerCase, counting()));
        List<String> collect = freq.keySet().stream()
                .sorted(Comparator.comparing(freq::get).reversed())
                .limit(10)
                .collect(Collectors.toList());
        for (String word : collect) {
            System.out.println(word);
        }
    }
}
```

> 가능하면 Collector나 Comparator 같은것은 static import를 활용해서 가독성을 더 높여도 된다.

```java
List<String> collect = freq.keySet().stream()
    .sorted(comparing(freq::get).reversed())
    .limit(10)
    .collect(toList());
```

sorted 메서드의 경우 맵의 원소들을 비교해 정렬해준다.

Collector의 나머지 36개 메서드도 알아보자.

### 스트림을 맵으로 취합하기

스트림 원소를 맵으로 취합하는 기능은 진짜 컬렉션에 취합하는 것보다 훨씬 복잡하다.   
스트림의 각 원소는 키 하나의 값 하나에 연관되어 있다. 다수의 스트림 원소가 하나의 같은 키에 연관될 수 있다.

#### toMap

가장 간단한 맵 수집기는 toMap(keyMapper, valueMapper)로, 보다시피 스트림 원소를 키에 매핑하는 함수와 값에 매핑하는 함수를 인수로 받는다.

다음 코드는 스트림의 각 원소가 고유한 키에 매핑되어 있을 때 적합하다.   
스트림 원소 다수가 같은 키를 사용한다면 파이프라인이 IllegalStateException을 던지며 종료될 것이다.
이 예시는 아이템 34에서 다뤘던 연산자의 enum 타입을 문자열과 상수 타입을 매핑시키는 코드이다.

```java
private static final Map<String, Operation> stringToEnum = Stream.of(values())
        .collect(toMap(Object::toString, e -> e));
```

더 복잡한 형태의 toMap이나 groupingBy는 이런 충동을 다루는 다양한 전략을 제공한다.   
예컨대 toMap에 키 매퍼와 값 매퍼는 물론 병합(merge) 함수까지 제공할 수 있다.   
병합 함수 형태는 ```BinaryOperator<U>```이며, 여기서 U는 해당 맵의 값 타입이다.

같은 키를 공유하는 값들은 이 병합 함수를 사용해 기존 값에 합쳐진다. 병합 함수가 곱셈이라면 키가 같은 모든 값들이 더해진 결과를 값으로 얻는다.

인수 3개를 받는 toMap은 어떤 키와 그 키에 연관된 원소들 중 하나를 골라 연관 짓는 맵을 만들 때 유용하다.    
예컨대 다양한 음악가의 앨범들을 담은 스트림을 가지고, 음악가와 그 음악가의 베스트 앨범을 연관 짓고 싶다고 해보자.

```java
public static void main(String[] args) {
    List<Album> albums = Arrays.asList(
        new Album("찰리 1집", new Artist("찰리"), 10),
        new Album("찰리 2집", new Artist("찰리"), 10000),
        new Album("초콜릿 1집", new Artist("초콜릿"), 200),
        new Album("초콜릿 3집", new Artist("초콜릿"), 300),
        new Album("초콜릿 2집", new Artist("초콜릿"), 1000)
    );
    Map<Artist, Album> bestAlbum = albums.stream().collect(
            toMap(Album::artist, a -> a, maxBy(comparing(Album::sales))));
    for (Map.Entry<Artist, Album> artistAlbumEntry : bestAlbum.entrySet()) {
        System.out.printf("아티스트 : %s, 앨범명 : %s%n", artistAlbumEntry.getKey().name(), artistAlbumEntry.getValue().name());
    }
}
```

인수가 3개인 toMap은 충돌이 나면 마지막 값을 취하는 수집기를 만들 때도 유용하다. 많은 스트림의 결과가 비결정적이다.   
하지만 매핑 함수가 키 하나에 연결해준 값들이 모두 같을 때, 혹은 값이 다르더라도 모두 허용되는 값일 때 이렇게 동작하는 수집기가 필요하다.

```java
toMap(keyMapper, valueMapper, (oldVal, newVal) -> newVal)
```

세 번째이자 마지막 toMap은 네 번째 인수로 맵 팩터리를 받는다. 이 인수로는 EnumMap이나 TreeMap 처럼 원하는 특정 맵 구현체를 직접 지정할 수 있다.
ConcurrentHashMap 인스턴스를 생성하는 toMap의 변종 toConcurrentMap도 존재한다.

#### groupingBy

groupingBy 메서드는 입력으로 분류 함수(classifier)를 받고 출력으로는 원소들을 카테고리별로 모아 놓은 맵을 담은 수집기를 반환한다.   
분류 함수는 입력받은 원소가 속하는 카테고리를 반환한다. 이 카테고리는 맵의 키로 쓰인다.

다중 정의된 groupingBy 중 형태가 가장 간단한 것은 분류 함수 하나를 인수로 받아 맵을 반환한다. (분류함수의 반환 값은 맵의 키로 사용된다.)   
반환된 맵의 키에 담기는 값은 카테고리에 해당하는 모든 원소를 담은 리스트다.

이전 아이템 45에서 사용했던 아나그램에서 사용한 예시가 있다.

```java
words.collect(groupingBy(word -> alphabetize(word)));
```

groupingBy가 반환하는 수집기가 리스트 외의 값을 갖는 맵을 생성하게 하려면, 분류 함수와 함께 다운스트림(down stream) 수집기도 명시해야 한다.   
다운 스트림 수집기의 역할은 해당 카테고리의 모든 원소를 담은 스트림으로부터 값을 생성하는 일이다.   
이 매개변수를 사용하는 가장 간단한 방법은 toSet(), toCollection(collectionFactory)을 넘기는 것이다. 그러면 리스트가 아닌 집합을 값으로 갖는 맵을 만들어낸다.

다운스트림 수집기로 counting()을 건네는 방법도 있다. 이렇게 하면 각 카테고리를 해당 카테고리에 속하는 원소의 개수와 매핑한 맵을 얻는다.

```java
Map<String, Long> freq = words.collect(groupingBy(String::toLowerCase, counting()));
```

groupingBy의 세 번째 버전은 다운 스트림 수집기에 더해 맵 팩터리도 지정할 수 있게 해준다.   
(이 메서드는 점층적 인수 목록 패턴에 어긋난다. mapFactory 매개변수가 downStream 매개변수보다 앞에 놓인다.)
이 groupingBy를 사용하면 맵과 그 안에 담긴 컬렉션의 타입을 모두 지정할 수 있다. 예컨대 값이 TreeSet인 TreeMap을 반환하는 수집기를 만들 수 있다.

ConcurrentHashMap을 반환하는 groupingByConcurrent 메서드도 존재한다.

#### 번외. partitioningBy

groupingBy의 사촌격인 partitioningBy도 존재한다.
분류 함수 자리에 프레디키드(predicate)를 받고 키가 Boolean인 맵을 반환한다.   
프레디키드에 더해 다운스트림 수집기까지 입력받는 버전도 다중정의되어 있다.

#### 그 외 Collectors 메서드

이외에 counting, summing, averaging, summarizing이 있고 각각 int, long, double 스트림용으로 스트림이 하나씩 존재한다.   
다중 정의된 reducing 메서드들, filtering, mapping, flatMapping, collectingAndThen 메서드가 있는데, 대부분 프로그래머는 이들을 몰라도 상관없다.

이외에 maxBy, minBy, joining 메서드가 있다.
* maxBy, minBy : 인수로 받은 비교자를 사용해 스트림에서 값이 가장 큰(maxBy), 가장 작은(minBy) 원소를 찾아 반환한다.   
* joining : CharSequence 인스턴스의 스트림에만 적용할 수 있다. delimiter, prefix, suffix를 인수로 받을 수 있다.
  * delimiter : 각 문자 사이에 더해줄 문자를 지정
  * prefix : 최종적인 문자열의 가장 앞에 더해줄 문자를 지정
  * suffix : 최종적인 문자열의 가장 뒤에 더해줄 문자를 지정