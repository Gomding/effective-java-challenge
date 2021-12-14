# 아이템23. 태그 달린 클래스보다는 클래스 계층구조를 활용하라

두 가지 이상의 의미를 표현하며, 그중 현재 표현하는 의미를 태그 값으로 알려주는 클래스가 있다.
다음 예시는 원과 사각형을 표현할 수 있는 클래스다.

```java
public class Figure {
    enum Shape { RECTANGLE, CIRCLE }
    
    // 태그 필드 - 현재 모양을 나타낸다.
    final Shape shape;
    
    // 다음 필드들은 모양이 사각형(RECTANGLE)일 때만 쓰인다.
    double length;
    double width;
    
    // 다음 필드는 모양이 원(CIRCLE)일 때만 쓰인다.
    double radius;
    
    // 원용 생성자
    Figure(double radius) {
        this.shape = Shape.CIRCLE;
        this.radius = radius;
    }
    
    // 사각형용 생성자
    Figure(double length, double width) {
        this.shape = Shape.RECTANGLE;
        this.length = length;
        this.width = width;
    }
    
    double area() {
        switch (shape) {
            case RECTANGLE:
                return length * width;
            case CIRCLE:
                return Math.PI * (radius * radius);
            default:
                throw new AssertionError(shape);
        }
    }
}
```

위의 예시가 태그 달린 클래스이다.

단점이 한가득 있다.
* 열거 타입 선언, 태그 필드, switch문 등 쓸데없는 코드가 많다.
* 여러 구현이 한 클래스에 혼합돼 있어서 가독성도 나쁘다.
* 여러 구현이 한 클래스에 있다는 뜻은 SRP(단일 책임 원칙)을 위반한 것이다.
* 다른 의미를 위한 코드가 함께 있으니 메모리도 많이 사용한다.
* 필드들을 final로 선언하려면 해당 의미에 쓰이지 않는 필드들까지 생성자에서 초기화해야 한다.
* 각각의 생성자가 적절한 태그 필드를 설정하고 알맞은 데이터 필드들을 초기화 하는데 컴파일러가 이를 체크할 수 없고 런타임이 되서야 문제가 수면위로 드러난다.
* 새로운 의미를 추가하려면 코드를 수정해야한다.(여러 구현 코드가 한곳에 있기 때문에 수정해야할 부분이 많다. 이 과정에서 휴먼 에러가 발생할 가능성도 있다.)
* 인스턴스 타입만으로는 현재 나타내는 의미를 알 길이 전혀 없다.(사각형인지 원인지 인스턴스 타입만 보고는 당장 알 수 없다.)

> 태그 달린 클래스는 장황하고, 오류를 내기 쉽고, 비효율적이다.

### 태그 달린 클래스 보다 클래스 계층 구조 활용

자바와 같은 객체 지향 언어는 타입 하나로 다양한 의미의 객체를 표현하는 훨씬 나은 수단을 제공한다.

바로 클래스 계층구조를 활용하는 서브 타이핑(subtyping)이다.

태그 달린 클래스 -> 클래스 계층구조로 바꾸는 방법을 알아보자
1. 계층구조의 루트(root)가 될 추상 클래스를 정의하고, 태그 값에 따라 동작이 달라지는 메서드들을 루트 클래스의 추상 메서드로 정의한다.
```java
// 아까 전 예제에서는 area 메서드가 1번에 해당한다.
public abstract class Figure {
    double area();
}
```
2. 태그 값에 상관없이 동작이 일정한 메서드들을 루트 클래스에 일반 메서드로 추가한다.
```java
public abstract class Figure {
    double area();
    
    void commonMethod() {
        // 공통적으로 사용할 로직 구현
        // Figure 클래스는 동작이 일정한 메서드가 없으므로 추가할게 없어서 commonMethod()는 예시를 위해 만든 메서드
    }
}
```
3. 루트 클래스를 확장한 구체 클래스를 의미별로 하나씩 정의한다. (단일 책임 원칙을 준수)
```java
public class Circle extends Figure {
    final double radius;
    
    Circle(double radius) {
        this.radius = radius;
    }
    
    @Override
    public double area() {
        return Math.PI * (radius * radius);
    }
} 
```

다음은 클래스 계층구조로 구현한 전체 코드입니다.

```java
public abstract class Figure2 {
    
    abstract double area();
}

class Circle extends Figure2 {
    private final double radius;

    public Circle(double radius) {
        this.radius = radius;
    }

    @Override
    public double area() {
        return Math.PI * (radius * radius);
    }
}

class Rectangle extends Figure2 {
    private final double length;
    private final double width;

    public Rectangle(double length, double width) {
        this.length = length;
        this.width = width;
    }
    
    @Override
    public double area() {
        return length * width;
    }
}
```

클래스 계층구조는 태그 달린 클래스의 단점을 모두 날려버린다.

간결하고 명확하며, 처음 Figure예제에 있던 쓸데없는 코드도((switch문 같은 분기처리) 모두 사라졌다.
* 살아남은 필드들은 모두 final이다.
* 각 클래스의 생성자가 모든 필드를 남김없이 초기화한다. 
* 추상 메서드를 모두 구현했는지 컴파일러가 확인해준다.
* switch문에서 case를 빼먹는 일로 인한 런타임 에러가 발생할 일이 없다.(휴먼 에러 방지)
* 루트 클래스를 건드리지 않고 새로운 타입을 추가할 수 있다.


타입 사이의 자연스러운 계층 관계를 반영할 수 있어서 유연성은 물론 컴파일타임 타입 검사 능력을 높여준다는 장점도 있다.
```java
public class Square extends Rectangle {
    Square(double side) {
        super(side, side);
    }
}
```

### 핵심 정리
태그 달린 클래스를 써야하는 상황은 거의 없다. 새로운 클래스를 작성하는 데 태그 필드가 등장한다면 태그를 없애고 계층구조로 리팩터링하는걸 고민해보자.