# 아이템27. 비검사 경고를 제거하라

제네릭을 사용하면 수많은 컴파일러 경고를 보게된다.

```java
// 대표적인 경고문
uses unchecked or unsafe operations.
Note: Recompile with -Xlint:unchecked for details.
```

* 비검사 형변환 경고
* 비검사 메서드 호출 경고
* 비검사 매개변수화 가변인수 타입 경고
* 비검사 변환 경고

제네릭에 익숙해질수록 경고를 마추지는일은 줄어들것이다. 그럼에도 새로 작성한 코드가 한번에 깔끔하게 컴파일 되는것은 힘들다.

### 비검사 경고 제거

다음 예시는 컴파일러가 경고문을 보여준다.

```java
Set<Lark> exaltation = new HashSet();

// 컴파일러가 무엇이 잘못됐는지 천천히 설명해준다.
uses unchecked or unsafe operations.
Note: Recompile with -Xlint:unchecked for details.
```

타입 매개변수를 선언하거나, 자바7 부터 지원하는 다이아몬드 연산자'<>'만으로 해결이 가능하다.   
이렇게 하면 컴파일러가 올바른 실제 타입 매개변수를 추론해준다.

```java
Set<Lark> exaltation = new HashSet<>();
```

> 곧바로 해결되지 않는 경고가 나타나도 포기하지 말자! 할 수 있는 모든 비검사 경고를 제거하라.

모두 제거한다면 그 코드는 타입 안정성이 보장된다.   
즉, 런타임에 ClassCastException이 발생할 일이 없고, 의도한 대로 잘 동작하리라 확신할 수 있다.

### 타입이 안전하다는 확신이 있다면 @SuppressWarnings("unchecked") 애너테이션을 사용하자

@SuppressWarnings("unchecked") 애너테이션을 사용하면 경고를 숨길 수 있다.

> 단, 타입 안전함을 검증하지 않은 채 경고를 숨기면 스스로에게 잘못된 보안 인식을 심어주는 꼴이다.

이는 경고 없이 컴파일되지만, 런타임에는 여전히 ClassCastException이 발생할 수 있다.

또한 안전하다고 검증된 비검증 경고를 그대로 노출시키면, 진짜 문제를 알리는 새로운 경고가 나와도 눈치채지 못할 수 있다.
(원래 발생하던 경고라고 생각하고 넘어갈 수 있다.)   
거짓 경고 속에 진짜 경고가 묻히는 경우가 생긴다.

### @SuppressWarnings는 가능한 좁은 범위에 적용시키자

@SuppressWarnings는 개별 지역변수 선언부터 클래스 전체까지 **어떤 선언**에도 달 수 있다.

> 보통은 변수 선언, 아주 짧은 메서드, 혹은 생성자가 될 것이다.    
> 자칫 심각한 경고를 놓칠 수 있으니 절대로 클래스 전체에 적용해서는 안 된다.

한줄이 넘는 메서드나 생성자에 달린 @SuppressWarnings 를 발견하면 지역변수 선언 쪽으로 옮기자.

이를 위해 지역변수를 새로 선언해야 하지만, 그만한 가치가 있다.

ArrayList의 toArray 메서드를 한번 살펴보자

```java
public <T> T[] toArray(T[] a) {
    if (a.length < size)
        return (T[]) Arrays.copyOf(elements, size, a.getClass());
    System.arraycopy(elements, 0, a, 0, size);
    if (a.length > size)
        a[size] = null;
    return a;
}
```

해당 예제를 컴파일 시키면 아래와 같은 경고가 발생한다.

```java
ArrayList.java:305: warning: [unchecked] unchecked cast
        return (T[]) Arrays.copyOf(elements, size, a.getClass());
                                   ^
required: T[]
found:    Object[]
```

애너테이션은 선언에만 달 수 있기 때문에 return 문에는 @SuppressWarnings를 다는 게 불가능하다.   
```java
@SuppressWarnings("unchecked") return (T[]) Arrays.copyOf(elements,size,a.getClass()); // 불가능
```
그렇다면 이제 메서드 전체에 달고 싶겠지만, 범위가 필요 이상으로 넓어진다.

앞서 말했듯이 @SuppressWarnings 적용 범위는 최대한 좁은게 좋다.

> 따라서 반환값을 담을 지역변수를 하나 새로 선언하고 그 변수에 애너테이션을 달아주자.

```java
public <T> T[] toArray(T[] a) {
    if (a.length < size){
        // 지역 변수를 새로 할당해서 애너테이션 달아주기
        @SuppressWarnings("unchecked") T[] result = 
            (T[]) Arrays.copyOf(elements,size,a.getClass());
        return result;
    }
    System.arraycopy(elements, 0, a, 0, size);
    if (a.length > size)
        a[size] = null;
    return a;
}
```

```@SuppressWarnings("unchecked")``` 애너테이션을 사용할 때면 그 경고를 무시해도 안전한 이유를 항상 주석으로 남겨야 한다.
* 다른 사람이 그 코드를 이해하는 데 도움이 된다. 
* 다른 사람이 그 코드를 잘못 수정하여 타입 안정성을 잃는 상황을 줄여준다.

```java
// @SuppressWarnings("unchecked")을 사용하는 근거를 설명한 예시
public <T> T[] toArray(T[] a) {
    if (a.length < size){
        // 생성한 배열과 매개변수로 받은 배열의 타입이 모두 T[]로 같으므로
        // 올바른 형 변환이다.
        @SuppressWarnings("unchecked") T[] result = 
            (T[]) Arrays.copyOf(elements,size,a.getClass());
        return result;
    }
    System.arraycopy(elements, 0, a, 0, size);
    if (a.length > size)
        a[size] = null;
    return a;
}
```

### 핵심정리
* 비검사 경고는 중요하니 무시하지 말자.
* 모든 비검사 경고는 런타임에 ClassCastException을 일으킬 수 있는 잠재적 가능성을 뜻하니 최선을 다해 제거하라.
* 경고를 없앨 방법을 찾지 못하겠다면, 그 코드가 타입 안전함을 증명하고 가능한 한 범위를 좁혀서 @SuppressWarnings("unchecked") 애너테이션으로 경고를 숨겨라
  * 그리고 반드시 경고를 숨기기로 한 근거를 주석으로 남겨라