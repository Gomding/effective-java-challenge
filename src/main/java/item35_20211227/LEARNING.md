# 아이템35. ordinal 메서드 대신 인스턴스 필드를 사용해라

열거 타입은 해당 상수가 그 열거 타입에서 몇 번째 위치인지를 반환하는 ```ordinal```이라는 메서드를 제공한다.

열거 타입 상수와 연결된 정수값이 필요하면 ordinal 메서드를 이용하고 싶은 유혹에 빠진다.

다음 코드는 합주단의 종류를 연주자가 1명인 솔로(solo)부터 10명인 디텍트(dectet)까지 정의한 열거 타입이다.

```java
// ordinal의 잘못된 사용 예시
public enum Ensemble {
    SOLO, DUET, TRIO, QUARTET, QUINTET,
    SEXTET, SEPTET, OCTET, NONET, DECTET;
    
    // ordinal을 사용해서 각 상수가 의미하는 연주자의 숫자를 반환한다.
    // ex) SOLO 는 순서가 0이므로 ordinal이 0을 반환. 이를 이용해서 ordinal + 1 을 해서 연주자의 숫자를 반환해준다. 
    public int numberOfMusicians() {
        return ordinal() + 1;
    }
}
```

### 상수 선언 순서를 바꾸면 ordinal을 활용한 코드가 오동작 한다.

위 코드는 의도한대로 정상 동작하는 코드다.

**하지만** 상수 선언 순서를 바꾸는 순간 numberOfMusicians가 오동작한다.

```java
public enum Ensemble {
    // DUET 과 SOLO의 순서를 바꾼다.
    DUET, SOLO, TRIO, QUARTET, QUINTET,
    SEXTET, SEPTET, OCTET, NONET, DECTET;
    
    // DUET 과 SOLO의 순서가 변경됐다.
    // DUET 상수에서 numberOfMusicians 를 호출하면 1이 반환된다.
    // DUET은 2명의 연주자가 반환될 것을 기대했지만 잘못된 값을 반환받는다.
    public int numberOfMusicians() {
        return ordinal() + 1;
    }
}
```

이는 데이터베이스에 Enum 상수 데이터를 저장할 때도 주의해야한다.

데이터베이스에 ordinal 값을 저장하게되면 상수의 순서가 변경됐을 때 데이터베이스에 저장된 ordinal은 그대로 이므로 조회시 잘못된 상수를 가져올 수 있다.

SOLO의 ordinal 값이 0이므로 데이터베이스에 0을 저장했다고 가정하자.   
하지만 위의 예시처럼 순서가 변경돼서 DUET의 ordinal값이 0으로 변경된다면?

ordinal 을 통해서 상수값을 찾을 때 데이터베이스에 저장될 때는 SOLO를 의미했지만 조회할 때는 DUET을 의미할 수 있는 것이다.

### 이미 사용 중인 정수와 값이 같은 상수는 추가할 방법이 없다.   
이미 8중주를 의미하는 OCTET 상수가 있는데 똑같이 8명이 연주하는 복4중주 DOUBLE_QUARTET은 추가할 수 없다.

```java
public enum Ensemble {
    // 8명이 연주하는 복4중주 DOUBLE_QUARTET을 추가했다.
    SOLO, DUET, TRIO, QUARTET, DOUBLE_QUARTET, QUINTET,
    SEXTET, SEPTET, OCTET, NONET, DECTET;
    
    // DOUBLE_QUARTET을 추가함으로 이전에 말했던 순서가 깨진다.
    // 심지어 DOUBLE_QUARTET도 제대로된 연주자 숫자를 반환받을 수 없다.
    public int numberOfMusicians() {
        return ordinal() + 1;
    }
}
```

### 값을 중간에 비워둘 수 없다.   

12명이 연주하는 3중 4중주 TRIPLE_QUARTET을 추가한다고 해보자.

그러면 11명이 연주하는 상수도 채워야한다.   
(현재 합주단은 10명까지 정의된 상태인데 ordinal은 순서를 반환하기 때문에 12명을 추가하기 위해선 11명 연주자도 채워야한다.)

하지만 11명으로 구성된 연주를 일컫는 이름은 없다.

3중 4중주를 추가하려면 쓰이지 않는 더미 상수를 같이 추가해야만 한다.

```java
public enum Ensemble {
    SOLO, DUET, TRIO, QUARTET, QUINTET,
    SEXTET, SEPTET, OCTET, NONET, DECTET,
    // 12명 연주자인 TRIPLE_QUARTET을 추가하기 위해 11명을 의미하는 더미인 DUMMY 상수를 함께 추가한다.
    // DUMMY는 추가 설명이 없으면 사용자에게 혼란을 주는 상수다. 즉 좋지 못한 코드다. 
    // 또한 사용되지 않는 코드이므로 실용성도 떨어진다.
    DUMMY, TRIPLE_QUARTET;
    
    public int numberOfMusicians() {
        return ordinal() + 1;
    }
}
```

### 열거 상수와 연결된 값은 ordinal을 통해 얻지말고 인스턴스 필드로 저장해 나타내자.

```java
public enum Ensemble {
    SOLO(1), DUET(2), TRIO(3), QUARTET(4), QUINTET(5),
    SEXTET(6), SEPTET(7), OCTET(8), NONET(9), DECTET(10);

    private final int numberOfMusicians;

    Ensemble(int numberOfMusicians) {
        this.numberOfMusicians = numberOfMusicians;
    }

    public int numberOfMusicians() {
        return this.numberOfMusicians;
    }
}
```

Enum의 API 문서를 보면 ordinal에 대해 이렇게 쓰여 있다.

> 대부분 프로그래머는 이 메서드를 쓸 일이 없다.   
> ordinal 메서드는 EnumSet과 EnumMap 같이 열거 타입 기반의 범용 자료구조에 쓸 목적으로 설계되었다.

#### 따라서 이런 용도가 아니라면 ordinal 메서드는 절대 사용하지 말자.