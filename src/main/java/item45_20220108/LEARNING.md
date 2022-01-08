# 아이템45. 스트림은 주의해서 사용하라

스트림 API란?   
다량의 데이터 처리 작업(순차적이든 병렬적이든)을 돕고자 자바 8에 추가된 API

스트림 API가 제공하는 추상 개념 중 핵심은 두 가지다.
1. 스트림은 데이터 원소의 유한 혹은 무한 시퀸스(sequence)를 뜻한다.
2. 스트림 파이프라인(Stream Pipeline)은 이 원소들로 수행하는 연산 단계를 표현하는 개념이다.

스트림의 원소들은 어디로부터든 올 수 있다.   
대표적으로는 컬렉션, 배열, 파일, 정규표현식 패턴 매처(matcher), 난수 생성기, 혹은 다른 스트림이 있다.   

스트림의 데이터 원소로는 객체 참조나 기본 타입 값(int, long, double)이 올 수 있다.

### 스트림 종단 연산, 중간 연산

스트림 파이프라인은 소스 스트림에서 시작해 종단 연산(terminal operation)으로 끝난다.(종료 연산이라고 생각하도 괜찮다.)   
**소스 스트림과 종단 연산 사이에 하나 이상의 중간 연산**(intermediate operation)이 있을 수 있다.

각 중간 연산은 스트림을 어떠한 방식으로 변환(transform)한다.
* 각 스트림 원소에 함수를 적용할 수 있다.
* 각 스트림 원소들 중 특정 조건을 만족 못하는 원소를 걸러낼 수 있다.
* 변환된 스트림의 원소 타입은 같을 수도 있고 다를 수도 있다. (```Stream<Integer>``` -> ```Stream<Integer>```, ```Stream<Integer>``` -> ```Stream<Long>``` 같을 수도, 다를 수도 있다.)

종단 연산은 마지막 중간 연산이 내놓은 스트림에 최후의 연산을 가한다.
* 원소를 정렬해 컬렉션에 담아서 반환
* 특정 원소 하나를 선택해서 반환
* 모든 원소를 출력

### 스트림 파이프의 지연 평가(lazy evaluation)

평가는 종단 연산이 호출될 때 이뤄진다. 종단 연산이 쓰이지 않는 데이터 원소는 계산에 쓰이지 않는다.

이러한 지연 평가가 무한 스트림을 다룰 수 있게 해주는 열쇠다. 종단 연산이 없는 스트림 파이프라인은 아무일도 하지 않는 명령어인 no-op과 같으니, 종단 연산을 빼먹는 일이 절대 없도록 하자.

### 스트림 API는 메서드 연쇄(method chaining)을 지원하는 플루언트 API

파이프 라인 하나를 구성하는 모든 호출을 연결하여 단 하나의 표현식으로 완성할 수 있다.   
파이프 라인 여러 개를 연결해 표현식 하나를 만들 수도 있다.

```java
public class StreamEx {
    public static void main(String[] args) {
        String[] words = {"5", "2", "3", "1", "4"};
        Arrays.stream(words)
                .map(Integer::valueOf) // 여러개의 파이프 라인을 연결할 수 있다.
                .sorted()
                .forEach(System.out::println);
    }
}
```
### 스트림의 순차 수행 과 병렬 수행
기본적으로 스트림 파이프라인은 순차적으로 수행된다. 병렬로도 수행할 수 있는데 스트림 파이프라인을 구성하는 스트림 중 하나에서 parallel 메서드를 호출해주기만 하면 된다.(그러나 효과를 볼 수 있는 상황은 많지 않다.)

### 원소들을 다룰 때 항상 스트림 API를 사용하는 것이 좋은가?

스트림 API는 다재다능해 보이며 실제로도 그렇다. 사실상 어떠한 계산도 해낼 수 있다.   
하지만 그렇다는 뜻이 항상 써야한다는 뜻은 아니다.

> 스트림을 제대로 이해하고 사용하면 프로그램이 짧고 깔끔해지지만, 잘못 사용하면 읽기 어렵고 유지보수도 힘들어진다.

스트림을 언제 써야하는지 확고한 규칙은 없지만, 참고할 만한 노하우는 있다.

예시를 보자.

단어들을 읽어 사용자가 지정한 문턱값(최소 글자수)보다 원소 수가 많은 아나그램 그룹을 출력한다.(아나그램이란 철자를 구성하는 알파벳은 같고 순서만 다른 단어를 말한다. ex) staple - petals)

이 프로그램은 사용자가 명시한 사전 파일에서 각 단어를 읽어 맵에 저장한다. 맵의 키는 그 단어를 구성하는 철자들은 알파벳 순으로 정렬한 값이다.   
ex) staple 의 키는 aelpst 다, petals 의 키는 aelpst다. 따라서 staple, petals 는 같은 키값인 aelpst와 매핑된다.

```java
public class Anagrams {
    public static void main(String[] args) {
        String[] words = {"staple", "aplest", "abc", "wood", "doow", "wdoo"};
        anagramsOverMinGroupSize(words, 2);
    }

    public static void anagramsOverMinGroupSize(String[] words, int minGroupSize) {
        Map<String, Set<String>> groups = new HashMap<>();
        // 첫번째 단계
        for (String word : words) {
            groups.computeIfAbsent(alphabetize(word), unused -> new TreeSet<>()).add(word);
        }

        for (Set<String> group : groups.values()) {
            if (group.size() >= minGroupSize)
                System.out.println(group.size() + ": " + group);
        }
    }

    private static String alphabetize(String s) {
        char[] chars = s.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }
}
```

이 프로그램의 anagramsOverMinGroupSize메서드 첫 번째 단계에 주목하자.   
단어를 삽입할 때 자바8에 추가된 computeIfAbsent 메서드를 사용했다.   
이 메서드는 맵 안에 키가 있는지 찾은 다음, 있으면 단순히 그 키에 매핑된 값을 반환한다.   
키가 없으면 건네진 함수 객체를 키에 적용하여 값을 계산해낸 다음 그 키와 값을 매핑해놓고, 계산된 값을 매핑한다.   
이처럼 computeIfAbsent를 이용하면 각 키에 다수의 값을 매핑하는 맵을 쉽게 구현할 수 있다.

앞의 코드와 같은 일을 하지만 스트림을 사용한 코드를 보자. 이 코드에서는 스트림을 과하게 활용했다. 때문에 코드를 이해하기 어려워졌다.

```java
public class AnagramsStream {
    public static void main(String[] args) {
        String[] words = {"staple", "aplest", "abc", "wood", "doow", "wdoo"};
        anagramsOverMinGroupSize(words, 2);
    }

    public static void anagramsOverMinGroupSize(String[] words, int minGroupSize) {
        Arrays.stream(words)
                .collect(
                        groupingBy(word -> word.chars().sorted()
                                .collect(StringBuilder::new,
                                        (sb, aChar) -> sb.append((char)aChar),
                                        StringBuilder::append).toString()))
                .values().stream()
                .filter(group -> group.size() >= minGroupSize)
                .forEach(System.out::println);
    }
}
```

코드를 이해하지 못했다고 해도 실망하지 말자. 다른 사람도 마찬가지다. 이 코드는 이전 코드보다 짧지만 읽기는 어렵다.   
특히 스트림에 익숙하지 않으면 더욱 그렇다. **스트림을 과용하면 프로그램이 읽거나 유지보수하기 어려워진다.**

하지만 절충 지점은 있다. 스트림을 적당히 사용하면 코드도 짧고 가독성도 좋다.

```java
public class AnagramsStream2 {
    public static void main(String[] args) {
        String[] words = {"staple", "aplest", "abc", "wood", "doow", "wdoo"};
        anagramsOverMinGroupSize(words, 2);
    }

    public static void anagramsOverMinGroupSize(String[] words, int minGroupSize) {
        Arrays.stream(words)
                .collect(groupingBy(word -> alphabetize(word)))
                .values().stream()
                .filter(group -> group.size() >= minGroupSize)
                .forEach(g -> System.out.println(g.size() + ": " + g));
    }

    private static String alphabetize(String s) {
        char[] chars = s.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }
}
```

> 람다 매개변수의 이름은 주의해서 정해야 한다. 앞 예시에서 매개변수 g는 사실 group이라고 하는게 좋다.   
> 람다에서는 타입 이름을 자주 생략하므로 매개변수 이름을 잘 지어야 스트림 파이프라인의 가독성이 유지된다.   
> 단어의 철자를 알파벳순으로 정렬하는 일은 별도 메서드인 alphabetize에서 수행했다. 
> 연산에 적절한 이름을 지어주고 세부 구현을 주 프로그램 로직 밖으로 빼내 전체적인 가독성을 높인것   
> 도우미 메서드를 적절히 활용하는 일의 중요성은 일반 반복 코드에서보다는 스트림 파이프라인에서 훨씬 크다.

alphabetize 메서드도 스트림을 활용할 수 있었지만 그렇게 하지 않았다. 명확성이 떨어지고 잘못 구현할 가능성이 커진다.   
심지어 성능이 느려질 수도 있다. 자바가 기본 타입인 char용 스트림은 지원하지 않기 때문이다.

```java
"Hello world!!".chars().forEach(System.out::print);
// 이 코드는 Hello world!!를 출력할거라 기대하지만 전혀 다른 숫자값들이 출력된다.(72101108108...생략)
// chars()가 반환하는 스트림의 원소는 char가 아니라 int값이기 때문이다.
// 올바르게 하려면 아래와 같이 형변환을 해줘야한다. 
"Hello world!!".chars().forEach(aChar -> System.out.print((char)aChar));
```

char값들을 처리할 때는 스트림을 사용하지 않는 편이 낫다.

### 스트림을 모든 반복문에 쓰고싶은 유혹을 참아라

스트림을 처음 쓰기 시작하면 모든 반복문을 스트림으로 바꾸고 싶은 유혹이 일겠지만, 서두르지 않는게 좋다. 스트림으로 바꾸는게 가능할지라도 코드 가독성과 유지보수 측면에서는 손해를 볼 수 있기 때문이다.

중간 정도 복잡한 작업에도(앞서의 아나그램 예시처럼) 스트림과 반복문을 적절히 조합하는게 최선이다.

**기존 코드는 스트림을 사용하도록 리팩토링하되, 새 코드가 나아 보일 때만 반영하자** (가독성을 고려해보자)

스트림 파이프라인은 되풀이되는 계산을 함수 객체(주로 람다나 메서드 참조)로 표현한다.   
반면 반복 코드에서는 코드 블록을 사용해 표현한다.   
그런데 **함수 객체로는 할 수 없지만, 코드 블록으로는 할 수 있는 일**들이 있다.   
계산 로직에서 이상의 일들을 수행해야 한다면 스트림과는 맞지 않다.
* 코드 블록에서는 범위 안의 지역 변수를 읽고 수정할 수 있다.
  * 람다에서는 final 이거나 사실상 final인 변수만 읽을 수 있고, 지역변수를 수정하는건 불가능하다.
* 코드 블록에서는 return문을 사용해 메서드에서 빠져나가거나, break continue를 사용해 반복문 도중에 빠져나가거나 반복 한번은 스킵할 수 있다.
* 메서드 선언에 명시된 검사 예외를 던질 수 있다.

다음 일들에는 스트림이 아주 안성맞춤이다.
* 원소들의 시퀸스를 일관되게 변환한다.
* 원소들의 시퀸스를 필터링한다.
* 원소들의 시퀸스를 하나의 연산을 사용해 결합한다.(더하기, 연결하기, 최솟값 구하기 등)
* 원소들의 시퀸스를 컬렉션에 모은다.
* 원소들의 시퀸스에서 특정 조건을 만족하는 원소를 찾는다.

### 스트림으로 처리하기 어려운 일

대표적인 예로, 한 데이터가 파이프라인 여러 단계를 통과할 때 이 데이터의 각 단계에서의 값들에 동시에 접근하기는 어려운 경우다.   
스트림 파이프라인은 일단 한 값을 다른 값에 매핑하고 나면 원래의 값은 잃는 구조이다. (때문에 스트림은 일회성이라고 한다.)   
원래 값과 새로운 값의 쌍을 저장하는 객체를 사용해 매핑하는 우회 방법도 있다. 그리 만족스러운 해법은 아닐 것이다.   
매핑 객체가 필요한 단계가 여러 곳이라면 계속해서 매핑하는 클래스가 생겨날 것이다. 코드양도 많고 지저분해진다. 또한 스트림의 본래 목적에서 벗어난 행동이다.

```java
public class StreamEx2 {
    public static void main(String[] args) {
        String[] words = {"5", "2", "3", "1", "4"};
        Arrays.stream(words)
                .map(word -> "새로운 값" + word) // 새로운 값과 예전값 모두 매핑할 방법은 이대로 불가능하다.
                .toArray();

        Arrays.stream(words)
                .map(word -> new WordOldAndNew(word, "새로운 값" + word)) // 둘다 매핑할 수 있는 객체를 생성해야 가능
                .toArray();
    }
    
    static class WordOldAndNew {
        private final String oldValue;
        private final String newValue;

        public WordOldAndNew(String oldValue, String newValue) {
            this.oldValue = oldValue;
            this.newValue = newValue;
        }
    }
}
```

### 스트림과 반복문 중 어떤걸 선택할지 어려운 상황

스트림과 반복 중 어느 쪽을 써야 할지 바로 알기 어려운 작업도 많다. 카드 덱을 초기화하는 작업을 생각해보자.

카드는 숫자(rank)와 무늬(suit)을 묶은 불변 값 클래스이고, 숫자와 무늬는 모두 열거 타입이라 하자.

이 작업은 두 집합의 원소들로 가능한 모든 조합을 계산하는 문제다. 이는 두 집합의 데카르트 곱이라고 부른다.

다음은 forEach를 사용해서 구현한 코드다.

```java
public class CardDeck {
    private static List<Card> newDeck() {
        List<Card> newDeck = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                newDeck.add(new Card(suit, rank));
            }
        }
        return newDeck;
    }
}

class Card {
    private final Suit suit;
    private final Rank rank;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }
}

enum Rank {
    ACE(1), TWO(2), THREE(3), FOUR(4),
    FIVE(5), SIX(6), SEVEN(7), EIGHT(8),
    NINE(9), TEN(10), JACK(11), QUEEN(12),
    KING(13);

    private int number;

    Rank(int number) {
        this.number = number;
    }
}

enum Suit {
    DIAMOND, HEART, CLOVER, SPADE
}
```

다음은 스트림으로 구현한 코드다. 종복된 코드 Rank, Suit, Card는 생략했다.   
중간 연산으로 사용한 flatMap은 스트림 원소 각각을 하나의 스트림으로 매핑한 다음 그 스트림들을 다시 하나의 스트림으로 합친다. 이를 평탄화라고도 한다.

```java
private static List<Card> newDeckStream() {
    return Arrays.stream(Suit.values())
        .flatMap(suit ->
            Arrays.stream(Rank.values())
                .map(rank -> new Card(suit, rank)))
        .collect(Collectors.toList());
    }
```

어느 메서드가 더 좋아 보이는가? 이는 개인의 취향 또는 팀의 컨벤션에 따라 다를것이다.
처음 방식이 더 자연스러워 보이고, 이해하기도 쉬우며 유지보수하기 좋아 보인다.   
하지만 스트림 방식을 더 편하게 생각하는 프로그래머도 있다.   
확신이 없다면 첫번째 반복문 방식을 사용하고 스트림 방식이 더 좋고 팀에서 스트림을 잘 사용하고 이해한다면 스트림 방식도 괜찮다.   

**애매하다면 둘 다 해보고 괜찮은 방법을 선택하라**