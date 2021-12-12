# 인터페이스는 타입을 정의하는 용도로만 사용하라

인터페이스는 자신을 구현한 클래스의 인스턴스를 참조할 수 있는 타입 역할을 한다.

달리 말해, 클래스가 어떤 인터페이스를 구현한다는 것은 자신의 인스턴스로 무엇을 할 수 있는지를 클라이언트에게 얘기해주는 것이다.

```java
// 티비 리모컨에는 이런 기능들이 있다는 것을 클라이언트에게 
// 내부 구현은 클라이언트가 몰라도 된다.
public interface TvRemoteControl {

    void turnOn();

    void turnOff();

    void upChannel();

    void downChannel();

    void upVolume();

    void downVolume();
}
```

> 인터페이스는 오직 이 용도로만 사용해야 한다.

### 잘못된 사용의 예시 [상수 인터페이스]

상수 인터페이스란 메서드 없이, 상수를 뜻하는 static final 필드로만 가득 찬 인터페이스를 말한다.

이 상수들을 사용하려는 클래스에서는 정규화된 이름을 쓰는 걸 피하고자 그 인터페이스를 구현하곤 한다.

```java
// 사용해서는 안되는 상수 인터페이스!
public interface PhysicalConstants {

    // 아보가드로 수 (1/몰)
    static final double AVOGADROS_NUMBER = 6.022_140_857e23;
    
    // 볼츠만 상수 (J/K)
    static final double BOLTZMANN_CONSTANT = 1.380_648_52e-23;
    
    // 전자 질량 (kg)
    static final double ELECTRON_MASS = 9.109_383_56e-31;
}
```

클래스 내부에서 사용하는 상수는 외부 인터페이스가 아니라 내부 구현에 해당한다.   

```java
public class EngineeringCalculator {
    
    public double somethingCalc(double number) {
        return number * PhysicalConstants.AVOGADROS_NUMBER;
    }
}
```

따라서 상수 인터페이스를 구현하는 것은 이 내부 구현을 클래스의 API로 노출하는 행위다.
(외부에서 상수 인터페이스를 볼 수 있다.)

클래스가 어떤 상수 인터페이스를 사용하든 사용자에게는 아무런 의미가 없다. 오히려 사용자에게 혼란을 줄 수 있다.

> 심지어는 클라이언트 코드가 내부 구현에 해당하는 상수들에 종속되게 할 수 있다.

만약 상수 인터페이스를 더이상 사용하지 않게 되더라도 바이너리 호환성을 위해 여전히 상수 인터페이스를 쓰고 있어야한다.   
final이 아닌 클래스가 상수 인터페이스를 구현한다면 모든 하위 클래스의 이름 공간이 그 인터페이스가 정의한 상수들로 오염되어 버린다.

### 자바 내부의 잘못된 상수 인터페이스 사용

java.io.ObjectStreamConstants 등, 자바 플랫폼 라이브러리에도 상수 인터페이스가 몇 개 있다.

이는 인터페이스를 잘못 활용한 예시이다. **따라해서는 안되는 안티 패턴이다!!**

### 상수를 올바르게 정의하는 법

상수를 공개하려면 다른 방법들이 있다.

* 사용하는 상수와 강한 연관성이 있다면 해당 클래스나 인터페이스 내부에 추가한다.
  * 자바에서 사용하는 대표적인 예로 Integer, Double과 같은 박싱 클래스에 선언된 MIN_VALUE, MAX_VALUE 상수가 있다.
```java
public class EngineeringCalculator {
    
    // 상수와 연관이 깊은 클래스에 직접 선언한다.
    public static final double AVOGADROS_NUMBER = 6.022_140_857e23;
    public static final double BOLTZMANN_CONSTANT = 1.380_648_52e-23;
    public static final double ELECTRON_MASS = 9.109_383_56e-31;
    
    public double somethingCalc(double number) {
        return number * PhysicalConstants.AVOGADROS_NUMBER;
    }
}
```
* 열거 타입으로 나타내기 적합한 상수라면 열거 타입으로 만들어 공개하면 된다.

```java
// 열거타입으로 제공
public enum PhysicalConstants2 {

  AVOGADROS_NUMBER(6.022_140_857e23),
  BOLTZMANN_CONSTANT(1.380_648_52e-23),
  ELECTRON_MASS(9.109_383_56e-31);

  private final double value;

  PhysicalConstants2(double value) {
    this.value = value;
  }

  public double getValue() {
    return value;
  }
}
```

* 위의 차선책을 모두 사용하지 않는다면 최소한 인스턴스화 할 수 없는 유틸리티 클래스로 공개하자.
```java
public class PhysicalConstants {
    // 인스턴스화 방지
    private PhysicalConstants() {
    }
    
    static final double AVOGADROS_NUMBER = 6.022_140_857e23;
    static final double BOLTZMANN_CONSTANT = 1.380_648_52e-23;
    static final double ELECTRON_MASS = 9.109_383_56e-31;
}
```

유틸리티 클래스에 정의된 상수를 클라이언트에서 사용하려면 클래스명까지 명시해야한다.   
ex) PhysicalConstants.AVOGADROS_NUMBER 처럼 말이다. 유틸리티 클래스의 상수를 빈번히 사용한다면 정적 임포트(static import)하여 클래스 이름은 생략할 수 있다.

```java
// static import 로 선언하면 상수에 클래스 이름을 생략할 수 있다.
import static item22_20211212.PhysicalConstants.*;

public class PhysicalConstantsEX1 {

    public static void main(String[] args) {
        System.out.println(BOLTZMANN_CONSTANT);
    }
}
```

### 핵심 정리

> 인터페이스는 타입을 정의하는 용도로만 사용해야한다. 상수 공개용으로 인터페이스를 사용하는것은 안티패턴이다!