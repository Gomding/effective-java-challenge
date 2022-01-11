# 아이템48. 스트림 병렬화는 주의해서 적용하라

이번 아이템의 핵심을 먼저 읽고가자.   
**스트림 병렬화는 오직 성능 최적화 수단이며, 다른 최적화와 마찬가지로 변경 전후로 반드시 성능을 테스트하여 병렬화를 사용할 가치가 있는지 확인해야 한다.**

자바는 동시성 프로그래밍 측면에서 항상 앞서갔다.   
처음 릴리스된 1996년부터 스레드, 동기화, wait/notify를 지원.   
자바 5부터는 동시성 컬렉션인 java.util.concurrent 라이브러리와 실행자(Executor) 프레임워크를 지원.
자바 7부터는 고성능 병렬 분해 프레임워크 fork-join 패키지 추가.   
자바 8부터는 parallel 메서드만 한 번 호출하면 파이프라인을 병렬 실행할 수 있는 스트림을 지원했다.

자바의 동시성 프로그램을 작성하기는 쉬워지고 있지만, 올바르고 빠르게 작성하는 일은 여전히 어렵다.   
동시성 프로그래밍에는 안전성과 응답가능 상태를 유지하기 위해 애써야 한다. 병렬 스트림 파이프라인 프로그래밍도 이와 같다.

아래 예시는 아이템 45에서 다루었던 메르센 소스를 생성하는 프로그램이다.

```java
public class MersennEx {
    private static final BigInteger TWO = BigInteger.valueOf(2);

    public static void main(String[] args) {
        primes().map(p -> TWO.pow(p.intValueExact()).subtract(BigInteger.ONE))
                .filter(mersenn -> mersenn.isProbablePrime(50))
                .limit(20)
                .forEach(System.out::println);
    }

    static Stream<BigInteger> primes() {
        return Stream.iterate(TWO, BigInteger::nextProbablePrime);
    }
}
```

모든 결과를 출력할 때까지 필자의 컴퓨터에서 25초가 걸렸다.   
속도를 높이고 싶어 병렬 프로그래밍을 사용하고싶다고 하자. 스트림 파이프라인에서 parallel()을 호출하겠다는 순진한 생각을 했다고 치자.

```java
public static void main(String[] args) {
    primes().map(p -> TWO.pow(p.intValueExact()).subtract(BigInteger.ONE))
        .parallel
        .filter(mersenn -> mersenn.isProbablePrime(50))
        .limit(20)
        .forEach(System.out::println);
}
```

이렇게 하면 얼마나 빨라질까? 느려질까?   
이 프로그램은 아무것도 출력하지 못하면서 CPU는 90%나 잡아먹는 상태가 무한히 계속된다.   
프로그램이 이렇게 느려진 원인은 스트림 라이브러리가 이 파이프라인을 병렬화하는 방법을 찾아내지 못했기 때문이다.   

환경이 아무리 좋더라도 **데이터 소스가 Stream.iterate**거나 **중간 연산으로 limit를 쓰면** 파이프라인 병렬화로는 성능 개선을 기대할 수 없다.   
(위 예시는 두가지 문제를 다 가지고 있다.)

파이프라인 병렬화는 limit를 다룰 때 CPU 코어가 남는다면 원소를 몇 개 더 처리한 후 제한된 개수 이후의 결과를 버려도 아무런 해가 없다고 가정한다.   
이 코드의 경우 새롭게 메르센 소스를 찾을 때마다 그 전 소수를 찾을 때보다 두배정도 더 오래 걸린다.   
따라서 위의 파이프라인 자동 병렬화 알고리즘이 제 기능을 못하게 마비된다.

> 스트림 파이프라인은 마구잡이로 병렬화하면 안 된다. 성능이 오히려 끔찍하게 나빠질 수도 있다.

### 병렬화의 효과가 좋은 자료구조

대체로 스트림의 소스가 **ArrayList, HashMap, HashSet, ConcurrentHashMap**의 인스턴스거나 배열, int 범위, long 범위일 때 병렬화의 효과가 가장 좋다.

이 자료구조들은 모두 데이터를 원하는 크기로 정확하고 손쉽게 나눌 수 있어서 일을 다수의 스레드에 분배하기에 좋다는 특징이 있다.   
나누는 작업은 Spliterator가 담당하며, Spliterator 객체는 Stream 이나 Iterable의 spliterator() 메서드로 얻어올 수 있다.

이 자료구조들의 또 다른 중요한 공통점은 원소들을 순차적으로 실행할 때의 참조 지역성이 뛰어나다는 것이다.

> 참조 지역성이란?   
> 이웃한 원소의 참조들이 메모리에 연속해서 저장되어 있다는 뜻이다.

참조들이 가리키는 실제 객체가 메모리에서 서로 떨어져 있을 수 있는데, 그러면 참조 지역성이 나빠진다.   
참조 지역성이 낮으면 스레드는 데이터가 주 메모리에서 캐시 메모리로 전송되어 오기를 기다리며 대부분 그냥 시간을 보내게 된다.
따라서 참조 지역성은 다량의 데이터를 처리하는 벌크 연산을 병렬화할 때 아주 중요한 요소로 작용한다.

**참조 지역성이 가장 뛰어난 자료구조는 기본 타입의 배열이다.**   
기본 타입 배열에서는 (참조가 아닌) 데이터 자체가 메모리에 연속해서 저장되기 때문이다.

### 스트림 파이프라인의 최종 연산과 병렬 수행의 관계

스트림 파이프라인의 최종 연산의 동작 방식 역시 병렬 수행 효율에 영향을 준다.   
최종 연산에서 수행하는 작업량이 파이프라인 전체 작업에서 상당 비중을 차지하면서 순차적인 연산이라면 파이프 라인 병렬 수행의 효과는 제한될 수 밖에 없다.
종단 연산 중 병렬화에 가장 적합한 것은 축소(reduction)다.

축소는 파이프라인에서 만들어진 모든 원소를 하나로 합치는 작업으로, 
Stream의 reduce 메서드 중 하나, 혹은 min, max, count, sum 같이 완성된 형태로 제공되는 메서드 중 하나를 선택해 수행한다.   
anyMatch, allMatch, noneMatch처럼 조건에 맞으면 바로 반환되는 메서드도 병렬화에 적합하다.

가변 축소를 수행하는 Stream의 collect 메서드는 병렬화에 적합하지 않다. 컬렉션을 합치는 부담이 크기 때문이다.   
직접 구현한 Stream, Iterable, Collection이 병렬화의 이점을 제대로 누리게 성능을 강도 높게 테스트하라.   
(고효율 spliterator를 작성하기란 상당한 난이도의 일이고, 아쉽지만 이 책에서는 다루지 않는다.)

### 안전 실패(safety failure)
스트림을 잘못 병렬화하면 (응답 불가를 포함해) 성능이 나빠질 뿐만 아니라 결과 자체가 잘못되거나 예상 못한 동작이 발생할 수 있다.

결과가 잘못되거나 오동작하는 것을 **안전 실패**라 한다.

안전 실패는 병렬화한 파이프라인이 사용하는 mappers, filters, 혹은 프로그래머가 제공한 다른 함수 객체가 명세대로 동작하지 않을 때 벌어질 수 있다.

Stream 명세는 이때 사용되는 함수 객체에 관한 엄중한 규약을 정의해놨다.
* Stream의 reduce 연산에 건네지는 accumulator(누적기)와 combine(결합기) 함수는 반드시 결합법칙을 만족
* 간섭받지 않고(non-interfering)
* 상태를 갖지 않아야한다(stateless).

이런 요구사항을 지키지 못해서 파이프라인을 순차적으로 수행하면 올바른 결과를 얻을 수 있다. 하지만 병렬 수행시에는 올바른 결과를 얻을 수 없다.

### 파이프라인 병렬화가 효과가 있는지 어떻게 예측할까?

데이터 소스 스트림이 효율적으로 나눠지고,   
병렬화하거나 빨리 끝나는 최종 연산을 사용하고,   
함수 객체들도 간섭하지 않더라도,   
-> 파이프라인이 수행하는 진짜 작업이 병렬화에 드는 추가 비용을 상쇄하지 못한다면 성능 향상은 미미할 수 있다.

실제로 성능이 향상될지를 추정해보는 간단한 방법이 있다. **스트림 안의 원소 수와 원소당 수행되는 코드 줄 수를 곱해보자.**   
이 값이 **최소 수십만**은 되어야 성능 향상을 맛볼 수 있다.

### 스트림 병렬화 테스트

처음에 말했던것 처럼 병렬화 변경 전후로 반드시 성능을 테스트하여 병렬화를 사용할 가치가 있는지 테스트해야 한다.   
운영 시스템과 흡사한 환경에서 테스트하는 것이 좋다.   
보통은 병렬 스트림 파이프라인도 공통의 포크-조인 풀에서 수행되므로, 잘못된 파이프라인 하나가 시스템의 다른 부분의 성능에까지 악영향을 줄 수 있음을 유념하자.

### 스트림 파이프라인 병렬화하는 일이 잦은가?

스트림 파이프라인을 병렬화할 일이 적어질 것처럼 느껴진다면 이번 아이템을 잘 읽을것이다. 

스트림을 병렬화하지 말라는 뜻은 아니다. 조건이 잘 갖춰지면 parallel 메서드 호출하나로 거의 프로세서 코어 수에 비례하는 성능 향상을 만끽할 수 있다.   
(머신 러닝과 데이터 처리 같은 특정 분야에서는 이 성능 개선의 유혹을 뿌리치기 어려울 것이다.)

스트림 파이프 라인 병렬화가 효과를 제대로 발휘하는 예를 보자.   
아래 예시는 파이(n), 즉 n보다 작거나 같은 소수의 개수를 계산하는 함수다.

```java
public class StreamParallelEx {

    public static void main(String[] args) {
        long start1 = System.currentTimeMillis();
        pi(10);
        System.out.println(System.currentTimeMillis() - start1);

        long start2 = System.currentTimeMillis();
        parallelPi(10);
        System.out.println(System.currentTimeMillis() - start2);
    }

    static long pi(long n) {
        return LongStream.rangeClosed(2, n)
                .mapToObj(BigInteger::valueOf)
                .filter(i -> i.isProbablePrime(50))
                .count();
    }

    static long parallelPi(long n) {
        return LongStream.rangeClosed(2, n)
                .parallel()
                .mapToObj(BigInteger::valueOf)
                .filter(i -> i.isProbablePrime(50))
                .count();
    }
}
```

### 무작위 수의 병렬화

무작위 수들로 이뤄진 스트림을 병렬화하려거든 ThreadLocalRandom(혹은 구식인 Random)보다는 SplittableRandom 인스턴스를 이용하자.   
Splittable Random은 정확히 이럴 때 쓰고자 설계된 것이라 병렬화하면 성능이 선형으로 증가한다.

한편 ThreadLocalRandom은 단일 스레드에서 쓰고자 만들어졌다.   
병렬 스트림용 데이터 소스로도 사용할 수는 있지만 SplittableRandom만큼 빠르지는 않을 것이다.   

그냥 Random은 모든 연산을 동기화하기 때문에 병렬 처리하면 최악의 성능을 보일 것이다.


