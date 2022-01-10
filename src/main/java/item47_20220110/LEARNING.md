# 아이템47. 반환 타입으로는 스트림보다 컬렉션이 낫다.

원소 시퀸스, 즉 일련의 원소를 반환하는 메서드는 수없이 많다.

자바 7까지는 이런 메서드의 반환 타입으로 컬렉션 인터페이스(Collection, Set, List) 혹은 Iterable이나 배열을 썼다. (둘 중 보통은 컬렉션 인터페이스를 사용했다.)   
for-each 문에서만 쓰이거나 반환된 원소 시퀸스가 일부 Collection 메서드를 구현할 수 없을 때는 Iterable 인터페이스를 썼다.   
반환 원소들이 기본 타입(int, long, double 등등)이거나 성능에 민감한 상황이라면 배열을 썼다.   

그런데 자바 8이 스트림이라는 개념을 들고 오면서 선택은 복잡해졌다.

### 원소 시퀸스 반환 타입 Stream VS Iterable
원소 시퀸스를 반환 할 때 스트림을 반환 할지 Iterable을 반환 할지 고민될 것이다.
* Stream은 반복(iteration)을 지원하지 않는다.
  * Stream은 Iterable 인터페이스가 정의한 추상 메서드를 가지고 있고 전부 Iterable 방식으로 동작한다.
  * 하지만 Stream은 Iterable을 확장(extends)하지 않았다.
* Iterable은 스트림 파이프라인을 사용할 수 없다. 

### Stream이 반복문을 지원하도록 하는 방법

Stream이 반복문을 지원하도록 하는 방법 중 깔끔한 우회로는 없다.

Stream의 iterator 메서드를 사용해서 반복문을 사용할 수 있을것같지만 사용하면 컴파일 오류가 생긴다.

```java
public class StreamIteratorEx {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("a", "b", "c");
        Stream<String> wordStream = words.stream();

//        아래 코드는 컴파일 에러가 발생한다.
//        for (String word : wordStream.iterator()) {
//            System.out.println(word);
//        }
    }
}
```

이 오류를 바로잡으려면 메서드 참조를 매개변수화된 Iterable로 적절히 형변환해줘야 한다.

```java
public class StreamIteratorEx {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("a", "b", "c");
        Stream<String> wordStream = words.stream();

        for (String word : (Iterable<String>) wordStream::iterator) {
            System.out.println(word);
        }
    }
}
```

위 코드는 정상적으로 동작한다. 하지만 난잡하고 직관성이 떨어진다.   
어댑터 메서드를 사용하면 상황이 더 나아진다. 자바의 타입 추론이 문맥을 잘 파악하여 어댑터 메서드 안에서 따로 형변환하지 않아도 된다.

```java
// Stream<E>를 Iterable<E>로 중개해주는 어댑터
public static <E> Iterable<E> iterableOf(Stream<E> stream) {
    return stream::iterator;
}
```

### Iterable에 스트림 파이프라인 사용하기

이를 위한 어댑터도 쉽게 구현할 수 있다.

```java
public static <E> Stream<E> streamOf(Iterable<E> iterable) {
    // 두번째 인자는 병렬을 사용할것인지에 대한 boolean 값이다.
    return StreamSupport.stream(iterable.spliterator(), false);
}
```

객체 시퀸스를 반환하는 메서드를 작성하는데, 오직 스트림 파이프라인에서만 쓰일 걸 안다면 스트림을 반환하게 해주자.   
반대로 반복문에서만 쓰일 걸 안다면 Iterable을 반환하자.

하지만 공개 API를 작성할 떄는 스트림 파이프라인의 수요와 반복문의 수요를 모두 배려해야한다.   
사용자가 한 가지 방식만을 사용할거라는 확실한 근거가 없다면.

Collection 인터페이스는 Iterable의 하위 타입이고 stream 메서드도 제공하니 반복과 스트림을 동시에 지원한다.

> 따라서 원소 시퀸스를 반환하는 공개 API의 반환 타입에는 Collection이나 그 하위 타입을 쓰는 게 일반적으로 최선이다.

반환하는 시퀸스의 크기가 메모리에 올려도 안절할 만큼 작다면 ArrayList나 HashSet 같은 표준 컬렉션 구현체를 반환하는 게 최선일 수 있다.   
단지 컬렉션을 반환한다는 이유로 덩치 큰 시퀸스를 메모리에 올려서는 안된다.

### 반활할 시퀸스가 크지만 간결하게 할 수 있다면 전용 컬렉션을 구현하는 쪽을 검토하자

주어진 집합의 멱집합(한 집합의 모든 부분집합을 원소로 하는 집합)을 반환하는 상황.

원소가 n개면 멱집합의 원소 개수는 2^n 개가 된다.   
멱집합을 표준 컬렉션 구현체에 저장하려는 생각은 위험하다.

AbstractList를 이용하면 훌륭한 전용 컬렉션을 손쉽게 구현할 수 있다.   
멱집합을 구성하는 각 원소의 인덱스를 비트 벡터로 사용하는 것이다. 인덱스의 n번째 비트 값은 멱집합의 해당 원소가 원래 집합의 n번째 원소를 포함하는지 여부를 알려준다.   
0부터 2^n - 1까지의 이진수와 원소 n개인 집합의 멱집합과 자연스럽게 매핑된다.

```java
/**
 * 입력 집합의 원소 수가 30을 넘으면 PowerSet.of가 예외를 던진다.
 * 이는 Stream이나 Iterable이 아닌 Collection을 반환 타입으로 쓸 때의 단점을 잘 보여준다.
 * 다시 말해, Collection의 size 메서드가 int 값을 반환하므로 PowerSet.of가 반환되는 시퀸스의 최대 길이는 Integer.MAX_VALUE 혹은 2^31 - 1로 제한된다.
 * Collection 명세에 따르면 컬렉션이 더 크거나 심지어 무한대일 때 size가 2^31 - 1을 반환해도 되지만 완전히 만족스러운 해법은 아니다.
 */
public class PowerSet {
    public static final <E> Collection<Set<E>> of(Set<E> s) {
        List<E> src = new ArrayList<>(s);
        if (src.size() > 30) {
            throw new IllegalArgumentException("집합에 원소가 너무 많습니다(최대 30개).: " + s);
        }
        
        return new AbstractList<Set<E>>() {
            @Override
            public Set<E> get(int index) {
                Set<E> result = new HashSet<>();
                for (int i = 0; index != 0; i++, index >>= 1) {
                    if ((index & 1) == 1) {
                        result.add(src.get(i));
                    }
                }
                return result;
            }

            @Override
            public int size() {
                return 1 << src.size();
            }

            @Override
            public boolean contains(Object o) {
                return o instanceof  Set && src.containsAll((Set) o);
            }
        };
    }
}
```

### 핵심정리

원소 시퀸스를 반환하는 메서드를 작성할 때는 스트림 파이프라인, 반복문 처리를 모두 원할 수 있다.   
따라서 양쪽을 모두 만족시키려 노력하자. 컬렉션을 반환할 수 있다면 그렇게 하라.   
반환 전부터 이미 컬렉션에 담겨있거나 원소 수가 적다면 ArrayList같은 표준 컬렉션에 반환하라.   
그렇지 않으면 최적화한 전용 컬렉션을 구현할지 고민하라.

> 나중에 Stream 인터페이스가 Iterable을 지원하도록 자바가 수정된다면, 그때는 안심하고 스트림을 반환하면 될 것이다.