# 아이템34. int 상수 대신 열거 타입을 사용하라

열거 타입은 일정 개수의 상수 값을 정의한 다음, 그 외의 값은 허용하지 않는 타입이다.   
(사계절, 태양계의 행성, 카드게임의 카드 종류 등이 좋은 예)

다음은 정수 열거 패턴의 예시다.

```java
public static final int APPLE_FUJI = 0;
public static final int APPLE_PIPPIN = 1;
public static final int APPLE_GRANNY_SMITH = 2;

public static final int ORANGE_NAVEL = 0;
public static final int ORANGE_TEMPLE = 1;
public static final int ORANGE_BLOOD = 2;
```

정수 열거 패턴에는 단점이 있다. 타입 안전을 보장할 방법이 없고 표현력도 안좋다.

```java
// 향긋한 오렌지 향의 사과 소스!!! 향도 좋고 맛있을지도..?
int i = (APPLE_FUJI - ORANGE_TEMPLE) / APPLE_PINPPLE;
```

사과용 상수 APPLE_, 오렌지용 상수 ORANGE_ 로 구분해서 정의했다. 정수 열거 패턴을 위한 이름 공간을 지원하지 않아 접두어로 이름 충돌을 방지하는 것이다.
단순하게 (원소의 수은 MERCURY), (행성의 수성 MERCURY) 를 구분하긴 힘들고 상수 이름을 중복으로 정의할 수도 없다.

```java
// 둘다 같은 이름의 변수가 선언됐으므로 컴파일 에러가 발생한다. 이름 중복 정의가 불가능!! 애초에 종류가 다른데 구분이 불가능하다
public static final int MERCURY = 0; // 원소의 수은
public static final int MERCURY = 1; // 행성의 수성
```

정수 상수는 문자열로 출력하기도 난간하다. 위의 예시에서 APPLE_FUJI 를 출력하면 어떤것이 출력되는가?

```java
System.out.println(APPLE_FUJI);

// 출력 내용
// 출력된 내용인 0만으로 APPLE_FUJI 인지 알 수 없다.
0
```

이런 단점들을 깔끔하게 해결해주는 열거 타입(enum type)이 있다.

```java
public enum Apple { FUJI, PIPPIN, GRANNY_SMITH }
public enum ORANGE { NAVEL, TEMPLE, BLOOD }
```

자바의 열거 타입은 완전한 형태의 클래스라서 다른 언어의 열거 타입보다 훨씬 강력하다.

* 열거 타입 자체가 클래스다.
* 상수 하나당 자신의 인스턴스를 하나씩 만들어 public static final 필드로 공개
* 열거 타입은 밖에서 접근할 수 있는 생성자를 제공하지 않는다.
* 싱글턴을 일반화한 형태다. (각 열거 타입의 요소들은 각각 하나의 객체만 존재하는 것을 보장한다.)

### 열거 타입은 컴파일타임 타입 안전성을 제공한다.   

Apple 열거 타입을 매개변수로 받는 메서드에 Orange 열거 타입을 인자로 건낼 수 없다.

```java
public void doSomething(Apple apple) {
    ...
}

public static void main(String[]args){
    doSomething(Orange.NAVEL); // Apple 열거 타입 매개변수를 받는 메서드에 Orange 열거 타입 인자를 건냈으므로 컴파일타임에 오류가 발생한다.
}
```

### 열거 타입에는 각자의 이름 공간이 있어서 이름이 같은 상수도 존재할 수 있다.

아래의 예시를 보면 이해가 쉽다.

행성(Planet)의 수성과 원소(Element)의 수은을 구현한 코드다. MERCURY 라는 이름이 같지만 공존할 수 있다.

```java
public enum Planet {
    MERCURY;
}

public enum Element {
    MERCURY;
}
```

마지막으로 열거 타입의 toString 메서드는 출력하기에 적합한 문자열을 내어준다.

### 열거 타입은 임의의 메서드나 필드를 추가할 수 있다.

다음은 행성의 열거 타입으로 행성의 질량과 반지름을 가지고 표면 무게를 구할 수 있다.

```java
public enum Planet {
    MERCURY(3.302e+23,2.439e6),
    VENUS(4.869e+24,6.052e6),
    EARTH(5.975e+24,6.378e6),
    MARS(6.419e+23,3.393e6),
    JUPITER(1.899e+27,7.149e7),
    SATURN(5.685e+26,6.027e7),
    URANUS(8.683e+25,2.556e7),
    NEPTUNE(1.024e+26,2.477e7);

    private final double mass;              // 질량 (단위: 킬로그램)
    private final double radius;            // 반지름 (단위: 미터)
    private final double surfaceGravity;    // 표면중력 (단위: m / s^2)

    private static final double G = 6.67300E-11;

    public Planet(double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
        this.surfaceGravity = G * mass / (radius * radius);
    }

    public double getMass() {
        return mass;
    }

    public double getRadius() {
        return radius;
    }

    public double getSurfaceGravity() {
        return surfaceGravity;
    }
    
    public double surfaceWeight(double mass) {
        return mass * surfaceGravity; // F = ma
    }
}
```

> 열거 타입 상수 각각을 특정 데이터와 연결지으려면 생성자에서 데이터를 받아 인스턴스 필드에 저장하면 된다.

### 열거 타입은 자신 안에 정의된 상수들의 값을 배열에 담아 반환하는 정적 메서드인 values 메서드를 제공한다.

values() 메서드는 값들이 선언된 순서로 저장된다.

```java
public class WeightTable {
    public static void main(String[] args) {
        double mass = 185.0d / Planet.EARTH.getSurfaceGravity();
        for (Planet p : Planet.values()) {
            System.out.printf("%s에서의 무게는 %f이다.%n", p, p.surfaceWeight(mass));
        }
    }
}
```

### 널리 쓰이는 열거 타입은 톱레벨 클래스, 특정 톱레벨 클래스에서만 쓰인다면 멤버 클래스로 만들어라

```java
// 톱레벨 열거 타입
public enum Apple {
}

// 멤버 클래스로 만든 열거 타입
public class Something {
    enum Apple {
    }
}
```

### 열거 타입 상수들이 각자의 기능을 제공하도록 구현할 수 있다.

상수마다 동작이 달라져야 하는 상황이 있을 것이다.

좋은 예로 계산기의 연산자가 있다.

```java
public enum Operation {
    PLUS, MINUS, TIMES, DIVIDE;
    
    // 상수가 뜻하는 연산을 수행한다.
    public double apply(double x, double y) {
        switch (this) {
            case PLUS: return x + y;
            case MINUS: return x - y;
            case TIMES: return x * y;
            case DIVIDE: return x / y;
        }
        throw new AssertionError("알 수 없는 연산: " + this);
    }
}
```

열거 타입에 따라 switch 문의 분기로 동작을 정의할 수 있다. 
하지만 이런 방법은 연산자가 추가될 때 분기를 추가하는걸 깜빡할 수 있다. 즉 실수를 예방할 수 없다.   
(런타임이 되어야 알 수 있다.)

열거 타입은 상수별로 다르게 동작하는 코드를 구현하는 더 나은 수단을 제공한다.   
열거 타입에 apply라는 추상 메서드를 선언하고 각 상수별 클래스 몸체, 즉 각 상수에서 자신에 맞게 재정의하는 방법이다.

> 이를 상수별 메서드 구현이라 한다.

```java
public enum Operation {
    PLUS { public double apply(double x, double y) {return x + y;} }, 
    MINUS { public double apply(double x, double y) {return x + y;} }, 
    TIMES { public double apply(double x, double y) {return x + y;} },
    DIVIDE { public double apply(double x, double y) {return x + y;} };

    // 상수가 뜻하는 연산을 수행한다.
    public abstract double apply(double x, double y); 
}
```

이제 새로운 연산자를 추가해도 apply 메서드를 구현하지 않으면 컴파일타임에 오류가 발생한다.

### 열거 타입에는 상수 이름을 입력받아 그 이름에 해당하는 상수를 반환하는 valueOf(String) 메서드가 자동 생성된다.

```java
public enum Operation {
    PLUS { public double apply(double x, double y) {return x + y;} }, 
    MINUS { public double apply(double x, double y) {return x + y;} }, 
    TIMES { public double apply(double x, double y) {return x + y;} },
    DIVIDE { public double apply(double x, double y) {return x + y;} };

    // 상수가 뜻하는 연산을 수행한다.
    public abstract double apply(double x, double y); 
}

public static void main(String[] args) {
    Operation plus = Operation.valueOf("PLUS");
}
```

위와 같은 방식으로 열거 타입의 valueOf메서드를 사용할 수 있다. 특정 열거 타입의 요소를 이름으로 찾아낼 수 있는 것이다. 

### 열거 타입의 toString 이 반환하는 문자열로 해당 열거 타입 상수로 변환해주는 fromString 메서드도 제공하는 걸 고려해보자.

열거 타입의 toString 메서드를 재정의하려거든 toString으로 반환받는 문자열로 해당 열거 타입 상수로 반환해주는 fromString 메서드를 제공해보자.

```java
public enum Operation3 {
    PLUS("+") { public double apply(double x, double y) {return x + y;} },
    MINUS("-") { public double apply(double x, double y) {return x + y;} },
    TIMES("*") { public double apply(double x, double y) {return x + y;} },
    DIVIDE("/") { public double apply(double x, double y) {return x + y;} };

    private final String symbol;

    Operation3(String symbol) {
        this.symbol = symbol;
    }

    // 상수가 뜻하는 연산을 수행한다.
    public abstract double apply(double x, double y);

    public String toString() {
        return this.symbol;
    }
}
```

모든 열거 타입에서 다음과 같이 fromString 을 구현할 수 있다.

```java
private static final Map<String, Operation> stringToEnum =
    Stream.of(values()).collect(Collectors.toMap(Object::toString, e -> e));

public static Optional<Operation> fromString(String symbol) {
    return Optional.ofNullable(stringToEnum.get(symbol));
}
```

fromString 이 ```Optional<Operation>```을 반환하는 점도 주의하자. 이는 주어진 문자열이 가리키는 열거 타입 요소가 존재하지 않을 수 있음을 클라이언트에 알리고, 그 상황을 클라이언트에서 대처하도록 한 것이다.

### 상수별 메서드 구현에는 열거 타입 상수끼리 코드를 공유하기 어렵다는 단점이 있다.

요일별 일당을 위한 열거 타입을 구현해보자

직원의 (시간당) 기본 임금과 그날 일한 시간(분 단위)이 주어진면 일당을 계산해주는 메서드를 갖고 있다.   
주중에 오버타임이 발생하면 잔업수당이 주어지고, 주말에는 무조건 잔업수당이 주어진다.

```java
public enum PayrollDay {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;
    
    private static final int MINS_PER_SHIFT = 8 * 60;
    
    int pay(int minutesWorked, int payRate) {
        int basePay = minutesWorked * payRate;
        
        int overtimePay;
        switch (this) {
            case SATURDAY: case SUNDAY:
                overtimePay = basePay / 2;
                break;
            default:
                overtimePay = minutesWorked <= MINS_PER_SHIFT ?
                        0 : (minutesWorked - MINS_PER_SHIFT) * payRate / 2;
        }
        
        return basePay + overtimePay;
    }
}
```

간결한 코드지만 위험한 코드다. 휴가와 같은 새로운 요소를 추가하면 case 문에 추가하는걸 깜빡할 수 있다.

이런 문제를 '전략 열거 타입 패턴'을 사용해서 해결할 수 있다.

잔업수당 계산을 private 중첩 열거 타입(다음 코드의 PayType)으로 옮기고 PayrollDay 열거 타입의 생성자에서 이 중 적당한 것을 선택한다.   
PayrollDay 열거 타입은 잔업 수당 계산을 그 전략 열거 타입에 위임하여, switch 문이나 상수별 메서드 구현이 필요 없게 된다.

> 이 패턴은 switch문보다 복잡하지만 더 안전하고 유연하다.

```java
import static item34_20211225.PayrollDay2.PayType.WEEKDAY;
import static item34_20211225.PayrollDay2.PayType.WEEKEND;

public enum PayrollDay2 {
    MONDAY(WEEKDAY), TUESDAY(WEEKDAY), WEDNESDAY(WEEKDAY), THURSDAY(WEEKDAY), FRIDAY(WEEKDAY), 
    SATURDAY(WEEKEND), SUNDAY(WEEKEND);

    private final PayType payType;

    PayrollDay2(PayType payType) {
        this.payType = payType;
    }
    
    int pay(int minutesWorked, int payRate) {
        return payType.pay(minutesWorked, payRate);
    
    }
    
    enum PayType {
        WEEKDAY {
            int overtimePay(int minsWorked, int payRate) {
                return minsWorked <= MINS_PER_SHIFT ? 0 :
                        (minsWorked - MINS_PER_SHIFT) * payRate / 2;
            }
        },
        WEEKEND {
            int overtimePay(int minsWorked, int payRate) {
                return minsWorked * payRate / 2;
            }
        };
        
        abstract int overtimePay(int mins, int payRate);
        private static final int MINS_PER_SHIFT = 8 * 60;
        
        int pay(int minsWorked, int payRate) {
            int basePay = minsWorked * payRate;
            return basePay + overtimePay(minsWorked, payRate);
        }
    }
}
```

### 기존 열거 타입에 상수별 동작을 혼합해 넣을 때는 switch문이 좋은 선택이 될 수 있다.

Operation(계산기 연산자) 열거 타입이 있을 때 각 연산의 반대 연산을 반환하는 메서드가 필요하다면?

```java
public static Operation inverse(Operation op) {
    switch(op) {
        case PLUS: return Oeration.MINUS;
        case MINUS: return Operation.PLUS;
        case TIMES: return Operation.DIVIDE;
        case DIVIDE: return Operation.TIMES;
        
        default: throw new AssertionError("알 수 없는 연산: " + op);
    }
}
```

추가하려는 메서드가 의미상 열거 타입에 속하지 않는다면 직접 만든 열거 타입이라도 이 방식을 적용하는 게 좋다.

### 열거 타입 언제 쓰는가?
* 필요한 원소를 컴파일 타임에 다 알 수 있는 상수 집합이라면 항상 열거 타입을 사용하자.
* 열거 타입에 정의된 상수 개수가 영원히 고정 불변일 필요는 없다.
  * 열거 타입은 나중에 상수가 추가돼도 바이너리 수준에서 호환되도록 설계되었다.
