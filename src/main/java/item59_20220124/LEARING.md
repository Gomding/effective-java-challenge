# 아이템59. 라이브러리를 익히고 사용하라

### 핵심
어떤 기능이 필요할 때   
가능하면 자바의 기본 라이브러리에 있는 기능인지 찾아보자,   
없다면 서드파티 라이브러리가 있는지 확인해보자,   
없다면 직접 구현하자!

### 직접 구현하는 기능의 단점

무작위 정수 하나를 생성하는 기능을 만들어보자.   
무작위 값의 범위는 0부터 인자로 넣는 n 사이의 숫자다.

```java
public class MyRandom {
    static Random rnd = new Random();

    static int random(int n) {
        return Math.abs(rnd.nextInt()) % n;
    }
}
```

괜찮은 듯 보여도 문제를 3가지나 가지고 있다.
* 첫 번째. n이 그리 크지 않은 2의 제곱수라면 얼마 지나지 않아 같은 수열이 반복된다.   
* 두 번째. n이 2의 제곱수가 아니라면 몇몇 숫자가 평균적으로 더 자주 반환된다. n 값이 크면 이 현상은 더 자주 발생한다.

```java
    public static void main(String[] args) {
        int n = 2 * (Integer.MAX_VALUE / 3);
        int low = 0;
        for (int i = 0; i < 1000000; i++) {
            if (random(n) < n/2)
                low++;
        }
        System.out.println(low);
    }
```

이 random 메서드는 이상적으로 동작한다면 약 50만을 출력할 것이다.   
하지만 실제로 여러번 실행하면 666666에 근접한 숫자가 나온다.   
생성된 숫자 중 2/3가 중간값보다 낮은 쪽으로 쏠린 것이다.

* 세 번째. 지정한 범위 '바깥'의 수가 종종 튀어나올 수 있다.

rnd.nextInt() 가 반환한 값을 Math.abs를 이용해 음수가 아닌 정수로 매핑하기 때문이다.   
nextInt()가 Integer.MIN_VALUE를 반환하면 Math.abs도 Integer.MIN_VALUE를 반환하고, 나머지 연산자(%)는 음수를 반환해버린다.   
(인수로 주어지는 n이 2의 제곱수가 아닐 때의 시나리오다.)

```java
// 아래는 무엇을 출력할까?
System.out.println(Math.abs(Integer.MIN_VALUE));

// -2147483648 을 출력한다. Integer에서 2147483648은 양수의 범위를 초과해서 overflow가 발생한다. 
// 즉 -2147483648의 절대값을 하면 -2147483648이 나온다
```

음수가 나온다는 것은 우리의 프로그램이 의도한 0부터 n 사이의 값을 벗어나는 값을 가지게 되는 셈이다.(음수가 나오기 때문)

### 결함 해결하기

이 프로그램의 결함을 해결하려면 의사난수 생성기, 정수론, 2의 보수 계산 등에 조예가 깊어야 한다.   
하지만 우리가 직접 구현할 필요가 없다.   
자바에 이미 해당 분야에 조예가 싶은 개발자가 구현한 라이브러리가 존재한다.

> Random.nextInt(int) 를 사용하면 의도한대로 랜덤값을 얻을 수 있다.

이 메서드의 내부 자세한 동작은 몰라도 된다. (궁금하다면 API문서나 소스 코드를 살펴볼 수 있다.)

알고리즘에 능통한 개발자가 설계와 구현과 검증에 시간을 들여 개발했고, 이 분야의 여러 전문가가 잘 동작함을 검증해줬다.   
이 라이브러리가 릴리스된 후 20여 년 가까이 수백만의 개발자가 열심히 사용했지만 버그가 보고된 적이 없다.   

혹시 버그가 발견되더라도 다음 릴리스에서 수정될 것이다.   

> 표준 라이브러리를 사용하면 그 코드를 작성한 전문가의 지식과 여러분보다 앞서 사용한 다른 프로그래머들의 경험을 활용할 수 있다.

자바 7부터는 Random을 더 이상 사용하지 않는 게 좋다. ThreadLocalRandom으로 대체하면 대부분 잘 작동한다.   

```java
public class ThreadLocalRandomEx {
    public static void main(String[] args) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        System.out.println(random.nextInt());
        System.out.println(random.nextInt(10));
    }
}
```

ThreadLocalRandom은 Random보다 더 고품질의 무작위 수를 생성할 뿐 아니라 속도도 더 빠르다.

아래의 코드로 속도 측정을 해본 결과 평균적으로 ThreadLocalRandom이 3배이상 빨랐다.

```java
public class ThreadLocalRandomEx {
    public static void main(String[] args) {
        Random random = new Random();
        ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();

        long start1 = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            random.nextInt();
        }
        System.out.println(System.currentTimeMillis() - start1);

        long start2 = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            threadLocalRandom.nextInt();
        }
        System.out.println(System.currentTimeMillis() - start2);
    }
}
```

### 표준 라이브러리의 이점

* 코드를 작성한 전문가의 지식과 여러분보다 앞서 사용한 다른 프로그래머들의 경험을 활용할 수 있다.
* 핵심적인 일과 크게 관련 없는 문제를 해결하느라 시간을 허비하지 않아도 된다는 것이다. 
  * 어떤 도구를 만드는것 보다 핵심 비지니스 기능에 집중할 수 있다.
* 따로 노력하지 않아도 성능이 계속 개선된다.
  * 사용자가 많고, 업계 표준 벤치마크를 사용해 성능을 확인하므로 표준 라이브러리 제작자들은 더 나은 방법을 꾸준히 모색할 수 밖에 없다.
  * 자바 플랫폼 라이브러리의 많은 부분이 수 년에 걸쳐 지속해서 다시 작성되며, 때론 성능이 극적으로 개선되기도 한다.
* 기능이 점점 많아진다.
  * 라이브러리에 부족한 부분이나 기능이 있다면 개발자 커뮤니티에서 이야기가 나오고, 논의된 후 다음 릴리스에 해당 기능이 추가되곤 한다.
* 우리가 작성한 코드가 많은 사람에게 낯익은 코드가 된다.(표준 라이브러리가 모두에게 익숙하기 때문)
  * 자연스럽게 다른 개발자들이 더 읽기 좋고, 유지보수하기 좋고, 재활용하기 쉬운 코드가 된다.

이상의 장점들을 보면 표준 라이브러리를 쓰는것이 당연히 좋아보인다.   
직접 기능을 구현하기 전에 라이브러리에 해당 기능이 있는지 확인하고 사용하자.

> 메이저 릴리스마다 주목할 만한 수많은 기능이 라이브러리에 추가된다.

라이브러리가 너무 방대하여 모든 API 문서를 공부하기는 힘들다.   
자바 프로그래머라면 적어도 java.lang, java.util, java.io와 그 하위 패키지들에는 익숙해져야 한다.   
(다른 라이브러리들은 필요할 때 마다 익히는게 좋다)

컬렉션 프레임워크와 스트림 라이브러리 그리고 java.util.concurrent의 동시성 기능은 알아두면 큰 도움이 된다.

java.util.concurrent의 경우는 멀티 스레드 프로그래밍 작업을 단순화해주는 고수준의 편의 기능은 물론, 
능숙한 개발자가 자신만의 고수준 개념을 직접 구현할 수 있도록 도와주는 저수준 요소들을 제공한다.

### 언제 라이브러리를 쓰고, 언제 직접 구현해야할까?

라이르러리가 여러분에게 필요한 기능을 충분히 제공하지 못할 수 있다.   
더 전문적인 기능을 요구할수록 이런 일이 더 자주 생길 것이다.   

우선은 라이브러리를 사용하려고 시도하자. 어떤 영역의 기능을 제공하는지 살펴보고, 원하는 기능이 아니라 판단되면 대안을 사용하자.

> 어떤 라이브러리든 빈 구멍이 있기 마련이다.

자바 표준 라이브러리에서 원하는 기능을 찾지 못하면, 그 다음 선택지는 고품질의 서드파티 라이브러리가 될 것이다.(구글의 구아바 라이브러리)

서드파티 라이브러리도 찾지 못하면, 다른 선택지가 없으니 직접 구현하자!

