# 아이템29. 이왕이면 제네릭 타입으로 만들라

```java
public class MyStack {
    private Object[] elements;
    private int size = 0;
    public static final int DEFAULT_INITIAL_CAPACITY = 16;

    public MyStack() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }
    
    public void push(Object e) {
        ensureCapacity();
        elements[size++] = e;
    }
    
    public Object pop() {
        if (size == 0)
            throw new EmptyStackException();
        Object result = elements[--size];
        elements[size] = null;
        return result;
    }
    
    public boolean isEmpty() {
        return size == 0;
    }

    private void ensureCapacity() {
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, 2 * size + 1);
        }
    }
}
```

위 클래스는 원래 제네릭 타입이어야 한다. 지금 상태로도 사용하는데 문제는 없다.   
하지만 클라이언트가 스택에서 요소를 꺼낼 때 형변환을 해야하는데, 이때 **런타임 오류가 발생할 수 있다.**

일반 클래스를 제네릭 클래스로 만드는 첫 단계는 클래스 선언에 타입 매개변수를 추가하는 일이다.

```java
public class MyStackGeneric<E> {
    private E[] elements;
    private int size = 0;
    public static final int DEFAULT_INITIAL_CAPACITY = 16;

    public MyStackGeneric() {
        elements = new E[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(E e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public E pop() {
        if (size == 0)
            throw new EmptyStackException();
        E result = elements[--size];
        elements[size] = null;
        return result;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void ensureCapacity() {
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, 2 * size + 1);
        }
    }
}
```

위 처럼 제네릭을 추가했을 때 컴파일 오류가 발생한다.
타입 매개변수 E는 실체화 불가 타입이므로 배열을 만들 수 없다. (아이템 28)

해결방법은 두 가지다.

### 첫 번째 제네릭 배열 생성을 금지하는 제약을 대놓고 우회하기

Object 배열을 생성한 다음 제네릭 배열로 형변환해보자.

```java
// 변경 전
public MyStackGeneric() {
    elements = new E[DEFAULT_INITIAL_CAPACITY];
}

// 변경 후
public MyStackGeneric() {
    elements = (E[]) new Object[DEFAULT_INITIAL_CAPACITY];
}
```

이제 컴파일러는 오류 대신 경고를 내보낸다.

```java
warning: [unchecked] unchecked cast
        elements = (E[]) new Object[DEFAULT_INITIAL_CAPACITY];
                         ^
  required: E[]
  found:    Object[]
  where E is a type-variable:
    E extends Object declared in class MyStackGeneric
```

컴파일러는 이 프로그램이 타입 안전한지 증명할 방법이 없다. 따라서 비검사 형변환이 프로그램의 타입 안전성을 해치지 않음을 우리 스스로 확인해야 한다.

* 비검사 형변환이 안전한지 증명하는 예시
  * 문제의 배열 elements는 private 필드에 저장되고, 클라이언트로 반환되거나 다른 메서드로 전달되는 일이 전혀 없다.
  * push 메서드를 통해 배열에 저장되는 원소의 타입은 항상 E다.

> 따라서 이 비검사 형변환은 확실히 안전하다.

비검사 형변환이 안전함을 직접 증명하면 범위를 최소로 좁혀 @SuppressWarning 애너테이션으로 해당 경고를 숨긴다.(비검사 형변환이 안전하다는 이유의 주석을 함께 포함시킨다.)

```java
// 배열을 사용한 코드를 제네릭으로 만드는 방법
// 배열 elements는 push(E)로 넘어온 E 인스턴스만 담는다.
// 따라서 타입 안정성을 보장하지만,
// 이 배열의 런타임 타입은 E[]가 아닌 Object[]다!
@SuppressWarnings("unchecked")
public MyStackGeneric() {
    elements = (E[]) new Object[DEFAULT_INITIAL_CAPACITY];
}
```

### 두 번째 필드의 타입을 E[] 에서 Object[] 로 변경하는 것

이렇게 하면 첫 번째와 다르게 오류가 발생한다.

```java
public class MyStackGeneric<E> {

    public static void main(String[] args) {
        
    }
    private Object[] elements; // 타입을 E[] -> Object[] 로 변경
    private int size = 0;
    public static final int DEFAULT_INITIAL_CAPACITY = 16;

    public MyStackGeneric() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY]; // 생성자로 마찬가지로 타입을 E[] -> Object[] 로 변경
    }

    public void push(E e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public E pop() {
        if (size == 0)
            throw new EmptyStackException();
        E result = elements[--size]; // 컴파일 오류 발생
        elements[size] = null;
        return result;
    }
    ...
}
```

컴파일 오류 나는 부분을 E로 형변환하면 오류 대신 경고가 뜬다.
```java
public E pop() {
    if (size == 0)
        throw new EmptyStackException();
    E result = (E) elements[--size]; // 경고 발생
    elements[size] = null;
    return result;
}
```

타입 매개변수 E는 실체화 불가 타입이므로 컴파일러는 런타임에 이뤄지는 형변환이 안전한지 증명할 방법이 없다.

이번에도 직접 증명하고 경고를 숨길 수 있다. 
@SuppressWarnings 애너테이션의 범위는 최소한으로 적용하는게 좋다는 말을 떠올리며 진행하자. 비검사 형변환을 수행하는 할당문에서만 숨겨보자

```java
public E pop() {
    if (size == 0)
        throw new EmptyStackException();
    
    // push에서 E 타입만 허용하므로 이 형변환은 안전하다.
    @SuppressWarnings("unchecked") E result = (E) elements[--size];
    
    elements[size] = null; // 다 쓴 참조 해제
    return result;
}
```

제네릭 배열 생성을 제거하는 두 방법 모두 나름의 지지를 얻고 있다.

첫 번째 방법은 가독성이 더 좋다.   
-> 배열의 타입을 E[]로 선언하여 오직 E 타입 인스턴스만 받음을 확실히 어필한다.

첫 번째 방식에서는 형변환을 배열 생성 시 단 한 번만 해주면 되지만, 두 번째 방식에서는 배열에서 원소를 읽을 때마다 해줘야 한다.
(현업에서는 첫 번째 방식을 선호한다.)

하지만 첫 번째 방식은 배열의 런타임 타입이 컴파일타임 타입과 달라 힙 오염(heap pollution, 아이템 32)을 일으킨다. 힙 오염이 맘에 걸리면 두 번째 방식으로 구현하기도 한다.

### "배열보다 리스트를 우선하라" 와의 모순

지금까지의 설명은 아이템 28(배열보다 리스트를 우선하라)과 모순돼 보인다.

사실 제네릭 타입 안에서 리스트를 사용하는 게 항상 가능하지도, 꼭 더 좋은 것도 아니다.

자바가 리스트를 기본 타입으로 제공하지 않으므로 ArrayList 같은 제네릭 타입도 결국은 기본 타입인 배열을 사용해 구현해야 한다.

### 핵심 정리

클라이언트에서 직접 형변환 해야 하는 타입보다 제네릭 타입이 더 안전하고 쓰기 편하다.(컴파일에 오류를 발생시켜줌)

그러니 새로운 타입 설계할 때는 형변환 없이도 사용할 수 있도록 하자.   
그렇게 하려면 제네릭 타입으로 만들어야 할 경우가 많다. 기존 타입 중 제네릭이었어야 하는 게 있다면 제네릭 타입으로 변경하자.

> 기존 클라이언트에는 아무 영향을 주지 않으면서, 새로운 사용자를 훨씬 편하게 해준다.

