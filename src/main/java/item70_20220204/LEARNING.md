# 아이템70. 복구할 수 있는 상황에는 검사 예외를, 프로그래밍 오류에는 런타임 예외를 사용하라

자바는 문제 상황을 알리는 throwable 을 알리는 타입으로 세 가지가 있다.
* 검사 예외 (Checked Exception)
* 비검사 예외 (Unchecked Exception)
* 에러 (Error)

세 가지나 있으니 언제 무엇을 사용해야하는지 헷갈릴 수 있다.   
**이럴 때 참고하면 좋은 멋진 지침들이 있으니 함께 살펴보자**

## 호출하는 쪽에서 복구하리라 여겨지는 상황이라면 검사 예외를 사용하라

**이것이 검사와 비검사 예외를 구분하는 기본 규칙**이다.

검사 예외를 던지면 **호출자가** 그 예외를 catch로 잡거나, 다시 외부로 전파시키는걸 강제하게 된다.   
따라서 메서드 선언에 포함된 검사 예외 각각은 그 메서드를 호출했을 때 발생할 수 있는 유력한 결과임을 API 사용자에게 알려주는 것이다.
```java
public class ExceptionEx1 {
    public static void main(String[] args) throws Exception {
        Car car = new Car(0);
        try {
            car.move();
        } catch (Exception e) {
            car.refuel(10);
            car.move();
        }
    }
}

class Car {
    private int oil;

    public Car(int oil) {
        this.oil = oil;
    }

    public void move() throws Exception {
        if (this.oil <= 0) {
            throw new Exception("연료가 부족합니다!");
        }
    }

    public void refuel(int oil) {
        this.oil += oil;
    }
}
```

다르게 말하면, API 설계자는 API 사용자에게 검사 예외를 던져주어 그 상황에서 회복해내라고 요구한 것이다.   
사용자는 예외를 바깥으로 되던지거나, 예외를 잡기만하고 별다른 조치를 취하지 않을 수 있지만, 이는 매우 좋지않은 생각이다.

```java
// 검사 예외를 처리하는 잘못된 예시
public class ExceptionEx1 {
    public static void main(String[] args) throws Exception {
        Car car = new Car(0);
        try {
            car.move();
        } catch (Exception e) {
            // 검사 예외를 잡고 아무런 처리를 하지않음. (복구가 되지 않는다.)
        }
    }
}
```

비검사 throwable은 두 가지로, 바로 ***런타임 예외**와 **에러**다.   

둘 다 동작하는 측면에서는 다르지 않다.   
이 둘은 프로그램에서 catch할 필요가 없거나 혹은 통상적으로 잡지 말아야 한다.

**프로그램에서 비검사 예외나 에러를 던졌다는 것은 복구가 불가능하거나 더 실행해봐야 득보다는 실이 많다는 뜻이다.**

비검사 throwable을 잡지않은 스레드는 적절한 오류 메세지를 내뱉으로 중단된다.   
(모든 예외는 적절한 오류 메세지를 내뱉도록 구현해야한다.)

### 프로그래밍 오류를 나타낼 때는 런타임 예외(비검사 예외)를 사용하자.

런타임 예외의 대부분은 전제조건을 만족하지 못했을 때 발생한다.   
전제조건 위배란 단순히 클라이언트가 해당 API의 명세에 기록된 제약을 지키지 못했다는 뜻이다.   
예를들어, 로또의 번호는 1~45 사이여야한다 같은 것이 제약조건이다.   
또는 배열의 인덱스는 0에서 (배열의 크기 - 1) 사이여야 한다. ArrayIndexOutOfBoundsException이 발생했다는 건 이 전제조건이 지켜지지 않았다는 뜻이다.

### 복구할 수 있는 상황인지 구분하는 법

복구할 수 있는 상황인지 프로그래밍 오류인지가 항상 명확히 구분되지는 않는다. (검사 예외를 사용할 지, 비검사 예외를 사용할 지 헷갈린다!)

예를 들어 자원 고갈은 말도 안 되는 크기의 배열을 할당해 생긴 프로그래밍 오류일 수도 있고,   
진짜로 자원이 부족해서 발생한 문제일 수도 있다.

만약 자원이 일시적으로만 부족하거나 수요가 순간적으로 몰린 것이라면 충분히 복구할 수 있는 상황일 것이다.   
따라서 해당 자원 고갈 **상황이 복구될 수 있는 것인지는 API 설계자의 판단**에 달렸다.

> 예외 상황이 복구 가능하다면 검사 예외를, 그렇지 않다면 런타임 예외를 사용하자.   
> 확신하기 어려운 상황이라면 비검사 예외를 선택하는 편이 나을 것이다.

### 에러(Error)는 언제 사용하는 것인가?

에러는 보통 **JVM이** 자원 부족, 불변식 깨짐 등 **더 이상 수행을 계속할 수 없는 상황을 나타낼 때 사용**한다.   

자바 언어 명세가 요구하는 것은 아니지만 업계에 널리 퍼진 규약이다. Error 클래스를 상속해 하위 클래스를 만드는 일은 자제하기 바란다.   
따라서 우리가 구현하는 비검사 throwable은 모두 RuntimeException의 하위 클래스여야 한다.(직접적이든 간접적이든)

Error는 상속하지 말아야 할 뿐 아니라, throw 문으로 직접 던지는 일도 없어야 한다.(AssertionException은 예외로 사용한다.)

### Exception, RuntimeException, Error를 상속하지 않는 throwable 만들기

Exception, RuntimeException, Error를 상속하지 않는 throwable 만들 수도 있다.

자바 언어 명세에서 이런 throwable을 직접 다루지는 않지만, 암묵적으로 일반적인 검사 예외(Checked Exception)처럼 다룬다.   
이런 throwable을 만드는 방법은 Throwable 클래스를 상속받으면 된다.
```java
public class ExceptionEx3 {

    public static void main(String[] args) throws MyThrowable {
        
        throw new MyThrowable();

    }
}

class MyThrowable extends Throwable {

}
```

그렇다면 이런 나만의 throwable은 언제 사용하면 좋을까?   
**이로울게 없으니 절대로 사용하지 말자!** thrwoable은 정상적인 검사 예외보다 나을 게 하나도 없으면서 API 사용자를 헷갈리게 할 뿐이다.

### 예외 메세지로 사용자에게 충분한 정보를 제공하자

API 설계자들도 예외 역시 어떤 메서드라도 정의할 수 있는 완벽한 객체라는 사실을 잊곤 한다.   

예외의 메서드는 주로 그 예외를 일으킨 상황에 관한 정보를 코드 형태로 전달하는 데 쓰인다.   
이런 메서드가 없다면 프로그래머들은 오류 메세지를 직접 파싱해 정보를 빼내야 하는데, 대단히 나쁜 습관이다.

throwable 클래스들은 대부분 오류 메시지 포맷을 상세히 기술하지 않는데, 
이는 **JVM이나 릴리스에 따라 포맷이 달라질 수 있다는 뜻이다**.   
따라서 메시지 문자열을 파싱해 얻은 코드는 **깨지기 쉽고 다른 환경에서 동작하지 않을 수 있다.**

**검사 예외는 일반적으로 복구할 수 있는 조건일 때 발생**한다.
따라서 호출자가 예외 상황을 벗어나는 데 필요한 정보를 알려주는 메서드를 함께 제공하는 것이 중요하다.   
예를들어 쇼핑몰에서 물건을 구입하려는 데 카드 잔고가 부족하여 검사 예외가 발생했다고 해보자.   
그렇다면 이 예외는 잔고가 얼마나 부족한지를 알려주는 접근자 메서드를 제공해야 한다.

```java
class CheckCard {
    private int balance;

    public CheckCard(int balance) {
        this.balance = balance;
    }

    public void payment(int price) throws LackOfBalanceException {
        if (balance - price < 0) {
            throw new LackOfBalanceException(balance, price);
        }
        balance -= price;
    }

    public int getBalance() {
        return balance;
    }
}

class LackOfBalanceException extends Exception {
    private final int balance;
    private final int price;

    public LackOfBalanceException(int balance, int price) {
        super();
        this.balance = balance;
        this.price = price;
    }

    // 잔액 부족에 대한 접근자 메서드
    public int lackOfBalance() {
        return balance - price;
    }
}
```
