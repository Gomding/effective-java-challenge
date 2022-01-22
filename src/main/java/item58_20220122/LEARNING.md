# 아이템58. 전통적인 for 문보다는 for-each 문을 사용하라

for-each 문의 정식 이름은 '향상된 for문(enhanced for statement)'이다.

다음은 전통적인 for 문으로 컬렉션을 순회하는 코드다.

```java
public class CommonForStatement {

    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1,2,3,4,5);

        int sum = 0;
        for (Iterator<Integer> i = numbers.iterator(); i.hasNext();) {
            Integer num = i.next();
            sum += num;
        }

        int[] numberArray = {1,2,3,4,5};

        int sum2 = 0;
        for (int i = 0; i < numberArray.length; i++) {
            sum2 += i;
        }
    }
}
```

이 관용구들은 while문보다는 낫지만 가장 좋은 방법은 아니다.   
반복자와 인덱스 변수는 모두 코드를 지저분하게 할 뿐이다. 우리에게 진짜 필요한건 원소들이다.

이렇게 쓰이는 요소의 종류가 늘어나면 오류가 생길 가능성이 높아진다.   
1회 반복마다 반복자가 3번 등장하며, 인덱스의 경우는 네번이나 등장해서 잘못 사용될 포인트가 많다.   
(잘못된 변수를 사용했을 때 컴파일러가 잡아주리란 보장도 없다.)

### for-each문을 사용하자

for-each문을 사용하면 반복자와 인덱스 변수를 사용하지 않으니 코드가 깔끔해지고 오류가 날 일도 없다.   
하나의 관용구로 컬렉션과 배열을 모두 처리할 수 있어서 어떤 컨테이너를 다루는지는 신경쓰지 않아도 된다.

```java
public class ForEachStatement {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

        int sum = 0;
        for (Integer number : numbers) {
            sum += number;
        }
    }
}
```

for-each문의 콜론(:)은 "안의(in)"라고 읽으면 된다. 따라서 이 반복문은 "numbers안의 각 원소 number에 대해"라고 읽는다.   

### for-each문의 최적화

반복 대상이 컬렉션이든 배열이든 for-each문을 사용하면 속도도 그대로다.   
for-each 문이 만들어내는 코드는 사람이 손으로 최적화한 것과 사실상 같기 때문이다.

### 반복문 중첩에서 전통적인 for문 사용 시 문제점

컬렉션을 중첩해 순회해야 한다면 for-each 문의 이점이 더욱 커진다.   
다음은 반복문을 중첩할 때(이중 for문) 흔히 저지르는 실수가 담겨 있다.

```java
public class Card {
    private final Suit suit;
    private final Rank rank;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }
}


enum Suit {
    CLUB, DIAMOND, HEART, SPADE
}

enum Rank {
    ACE, DEUCE, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING
}

public class CardEx {
    public static void main(String[] args) {
        List<Suit> suits = Arrays.asList(Suit.values());
        List<Rank> ranks = Arrays.asList(Rank.values());
        
        List<Card> deck = new ArrayList<>();
        for (Iterator<Suit> i = suits.iterator(); i.hasNext();) 
            for (Iterator<Rank> j = ranks.iterator(); j.hasNext();) 
                deck.add(new Card(i.next(), j.next()));
    }
}
```

여기서 문제는 바깥 컬렉션(suit) 반복자의 next 메서드가 너무 많이 호출된다는 것이다.

원래 의도는 하나의 문양(Suit)에 13개의 카드 숫자(Rank)가 조합되는 것이다. 하지만 안쪽의 반복문에서 카드 문양(suit)의 반복자가 next()로 계속 호출되고있다.

정상적인 코드는 다음과 같다.

```java
    public static void main(String[] args) {
        List<Suit> suits = Arrays.asList(Suit.values());
        List<Rank> ranks = Arrays.asList(Rank.values());

        List<Card> deck = new ArrayList<>();
        for (Iterator<Suit> i = suits.iterator(); i.hasNext();) {
            Suit suit = i.next();
            for (Iterator<Rank> j = ranks.iterator(); j.hasNext(); )
                deck.add(new Card(suit, j.next()));
        }
    }
```

그래도 이전의 오류는 카드 문양(Suit)가 바닥나면서 NoSuchElementException을 던진다.   
운이 나빠서 바깥 컬렉션의 크기가 안쪽 컬렉션의 크기와 같은 배수라면 예외도 발생하지 않는다.   
다음의 주사위 예시를 보자.

```java
public class DiceEx {
    public static void main(String[] args) {
        Collection<Face> faces = EnumSet.allOf(Face.class);
        
        for (Iterator<Face> i = faces.iterator(); i.hasNext();)
            for(Iterator<Face> j = faces.iterator(); j.hasNext();)
                System.out.println(i.next() + " " + j.next());
    }
}

enum Face {
    ONE, TWO, THREE, FOUR, FIVE, SIX
}
```

이 프로그램은 예외가 발생하지 않는다. 다만 의도한대로 출력되지 않는다.

원래 의도는 "ONE ONE", "ONE TWO", "ONE THREE" ... "SIX SIX" 총 36개가 출력되는 걸 기대 했을것이다.
하지만 결과는 "ONE ONE", "TWO TWO" ... "SIX SIX" 총 6개만 출력된다.

### 반복문 중첩 시 for-each 사용

for-each 문을 사용하면 이런 문제를 간단하게 해결하고 가독성도 좋아진다. 

```java
public class CardEx3 {
    public static void main(String[] args) {
        List<Suit> suits = Arrays.asList(Suit.values());
        List<Rank> ranks = Arrays.asList(Rank.values());

        List<Card> deck = new ArrayList<>();
        for (Suit suit : suits) 
            for (Rank rank : ranks) 
                deck.add(new Card(suit, rank));
    }
}
```

### for-each를 사용할 수 없는 상황

하지만 안타깝게도 for-each문을 사용할 수 없는 상황도 세 가지 존재한다.

* 파괴적인 필터링
* 변형
* 병렬 반복

#### 파괴적인 필터링

컬렉션을 순회하면서 선택된 원소를 제거해야 한다면 반복자의 remove 메서드를 호출해야 한다.    
Iterator로 반복하는 경우에는 remove의 사용이 곤란하게 된다.   
반복문을 도는 과정에서 원래 컬렉션의 원소는 제거됐지만, Iterator의 원소는 그대로 남아있을 수 있다. 

자바 8 부터는 Collection의 removeIf 메서드를 사용해 컬렉션을 명시적으로 순회하는 일을 피할 수 있다.

#### 변형

리스트나 배열을 순회하면서 그 원소의 값 일부 혹은 전체를 교체해야 한다면 리스트의 반복자나 배열의 인덱스를 사용해야 한다.

```java
List<User> users = new ArrayList<>();

for (int i = 0; i < user.size(); i++) {
    User user = users.get(i);
    user.updateName(user.getName + "공통 수정내용");
}
```

#### 병렬 반복

여러 컬렉션을 별렬로 순회해야 한다면 각각의 반복자와 인덱스 변수를 사용해 엄격하고 명시적으로 제어해야 한다.


위 세 가지 상황 중 하나에 속할 때는 일반적인 for문을 사용하되 이번 아이템에서 언급한 문제들을 경계하기 바란다.

for-each문은 컬렉션과 배열은 물론 Iterable 인터페이스를 구현한 객체라면 무엇이든 순회할 수 있다.   
Iterable 인터페이스는 다음과 같이 메서드가 하나 뿐이다.

```java
public interface Iterable<E> {
    // 이 객체의 원소들을 순회하는 반복자를 반환한다.
    Iterator<E> iterator();
}
```

Iterable을 처음부터 직접 구현하기는 까다롭지만, 원소들의 묶음을 표현하는 타입을 작성해야 한다면 Iterable을 구현하는 쪽으로 고민해보기 바란다.   
해당 타입에서 Collection 인터페이스는 구현하지 않기로 했어도 말이다.

Iterator를 구현하여 for-each문을 사용할 수 있게 한다면 그 타입을 사용하는 프로그래머가 좋아할 것이다.

