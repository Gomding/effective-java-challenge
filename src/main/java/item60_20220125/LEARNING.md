# 아이템60. 정확한 답이 필요하다면 float와 double은 피하라

float과 double 타입은 과학과 공학 계산용으로 설계되었다.   
**이진 부동소수 연산에 쓰이며**, 넓은 범위의 수를 빠르게 정밀한 '**근사치**'로 계산하도록 세심하게 설계되었다.

> 소수의 계산에서 정확한 결과가 필요하다면 float 과 double 타입을 사용하지 말자.   
> float와 double 타입은 특히 금융 관련 계산과는 맞지 않는다.   
> 0.1 혹은 10의 음의 거듭 제곱수(10^-1, 10^-2)를 표현할 수 없기 때문

다음은 float과 double 타입을 사용한 연산이다.

주머니에 1.03 달러가 있었는데 그 중 42센트를 썼다고 해보자.   
남은 돈은 얼마인가? 문제의 답을 구하기 위해 '어설프게' 작성해본 코드다.

```java
System.out.println(1.03 - 0.42);

// 출력 결과
// 0.6100000000000001
```

0.61 같은 값을 원했겠지만 결과는 0.6100000000000001 이라는 값이 나온다. 특별한 사례도 아니고 **평범한 사례**다.   

이번엔 주머니에 1달러가 있었는데 10센트짜리 사탕 9개를 샀다고 해보자.

```java
System.out.println(1.00 - (9 * 0.10));

// 출력 결과
// 0.09999999999999998
```

역시나 0.1 이라는 값을 원했지만 0.09999999999999998이라는 값이 나온다.   
(반올림하면 해결될거라 생각하지만, 반올림해도 해결되지 않는 경우도 있다.)

주머니에 1달러가 있고, 선반에 10센트, 20센트, 30센트 ... 1달러짜리의 맛있는 사탕이 놓여있다고 해보자.   
10센트부터 하나씩 주머니의 돈이 허락할 때까지 사보자.   
사탕을 몇 개나 살 수 있고, 잔돈은 얼마나 남을까?

```java
double funds = 1.00;
int itemsBought = 0;
for(double price = 0.10; funds >= price; price += 0.10) {
    funds -= price;
    itemsBought++;
}
System.out.println(itemsBought + "개 구입");
System.out.println("잔돈(달러):" + funds);

// 출력 결과
// 3개 구입
// 잔돈(달러):0.3999999999999999
```

실행해보면 사탕 3개를 구입했고 잔돈은 0.3999999999999999달러가 남아있다.   
당연히 원하는 결과가 아니다.(4개 구입에 0달러가 남기를 예상했을 것이다. 즉, 반올림한다고 해결되지 않는다는걸 보여준다.)

### BigDecimal을 사용하자

금융 계산같이 정확한 계산을 원하면 BigDecimal, int 혹은 long을 사용해야 한다.

이제 앞서의 코드를 BigDecimal로 변경해보자.
double 타입을 BigDecimal로 교체만 했다. BigDecimal의 생성자 중 문자열을 받는 생성자를 사용했음에 주목하자.   
계산시 부정확한 값이 사용되는 것을 막기 위해 필요한 조치다.

```java
public class BigDecimalEx {
    private static final BigDecimal TEN_CENTS = new BigDecimal(".10");

    public static void main(String[] args) {
        int itemsBought = 0;
        BigDecimal funds = new BigDecimal("1.00");
        for (BigDecimal price = TEN_CENTS; funds.compareTo(price) >= 0; price = price.add(TEN_CENTS)) {
            funds = funds.subtract(price);
            itemsBought++;
        }
        System.out.println(itemsBought + "개 구입");
        System.out.println("잔돈(달러): " + funds);
    }
}

// 출력결과
// 4개 구입
// 잔돈(달러): 0.00
```

이제 우리가 예상했던 대로 4개 구입과 잔돈 0.00달러가 나온다.

### BigDecimal의 단점

BigDecimal에도 단점 두 가지가 있다.   
기본 타입보다 쓰기가 훨씬 불편하고, 훨씬 느리다.   
단발성 계산이라면 느리다는 문제는 무시할 수 있지만, 쓰기 불편하다는 점은 못내 아쉬울 것이다.

### BigDecimal 대신 int나 long 활용하기

BigDecimal의 단점 두 가지가 아쉽다면 int나 long을 활용할 수 있다.

하지만 다룰 수 있는 값의 크기가 제한되고, 소수점은 직접 관리해야 한다.

이전의 예시를 int를 사용하는 쪽으로 구현해보자. 이번 예시에서는 단위를 달러대신 센트로 사용한다.(int를 사용하기 때문에)

```java
public static void main(String[] args) {
    int itemsBought = 0;
    int funds = 100;
    for (int price = 10; funds >= price; price += 10) {
        funds -= price;
        itemsBought++;
    }
    System.out.println(itemsBought + "개 구입");
    System.out.println("잔돈(달러): " + funds);
}
```

### 핵심정리

정확한 답이 필요한 계산에는 float나 double을 피하라. 

소수점 추적은 시스템에 맡기고, 코딩시의 불편함이나 성능 저하를 신경쓰지 않겠다면 BigDecimal을 사용하자.   
BigDecimal이 제공하는 여덞가지 반올림 모드를 이용하여 반올림을 완벽히 제어할 수 있다.   
법으로 정해진 반올림을 수행해야 하는 상황에서 상당히 유용한 기능이다.

하지만 다음의 상황이라면 int나 long을 사용하면서 소수점을 직접 관리해보자
* 성능이 중요한 상황
* 소수점을 직접 추적할 수 있음
* 숫자가 너무 크지않다.

숫자를 9 자리 십진수로 표현할 수 있다면 int를 사용
숫자를 18 자리 십진수로 표현할 수 있다면 long을 사용

하지만 18 자리 이상의 십진수라면 BigDecimal을 사용할 수 밖에 없다.