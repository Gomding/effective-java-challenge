# 아이템39. 명명 패턴보다 애너테이션을 사용하라

전통적으로 도구나 프레임워크가 특별히 다뤄야 할 프로그램 요소에는 딱 구분되는 명명 패턴을 적용해왔다.

Junit3의 경우 테스트 메서드 이름을 test로 시작하게끔 했다. 효과적인 방법이지만 단점이 크다.
* 메서드 이름에 오타가 있으면 해당 메서드는 무시한다. ex) tsetSomething() -> tset 이라는 오타를 내서 무시되는 메서드가 된다.
* 올바른 프로그램 요소에서만 사용되리라 보증할 방법이 없다.
  * 메서드가 아닌 클래스 이름을 TestSomthing 으로 지었다면? 개발자는 이 클래스에 정의된 테스트 메서드들이 수행되길 기대하지만 Junit3은 클래스 이름에 관심이 없다.
* 프로그램 요소를 매개변수로 전달할 방법이 없다.
  * 특정 예외를 던져야만 성공하는 테스트를 원한다면? 기대하는 예외 타입을 테스트에 매개변수로 전달할 수 없는 상황이다.

Junit4 부터는 애너테이션을 사용해 Junit3의 단점을 극복했다.   
Test라는 애너테이션을 정의한다고 해보자. 자동으로 수행되는 간단한 테스트용 애너테이션으로, 예외가 발생하면 해당 테스트를 실패로 처리한다.

다음으로 @Test 애너테이션을 비슷하게 구현해본 @MyTest 애너테이션 예시다.

```java
import java.lang.annotation.*;

/**
 * 테스트 메서드임을 선언하는 애너테이션이다.
 * 매개변수 없는 정적 메서드 전용이다.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MyTest {
}
```

@MyTest 애너테이션에는 @Retention, @Target 두 가지 애너테이션이 달려 있다. 이는 **메타 애너테이션**으로 애너테이션 선언에 다는 애너테이션이다. 
쉽게 말해 **애너테이션을 위한 애너테이션**이다. 

@Retention : 애너테이션의 생명 범위를 설정한다. RetentionPolicy.Runtime이 의미하는 것은 런타임에도 해당 애너테이션을 유지하겠다는 뜻이다.   
@Target : 애너테이션을 적용하는 범위를 설정한다. ElementType.METHOD는 메서드 선언에만 사용돼야 한다고 알려준다.

@MyTest의 예시 코드를 보면 '매개변수 없는 정적 메서드 전용이다.'라고 쓰여있다.   
이 제약을 컴파일러가 강제할 수 있으면 좋겠지만, 이를 위해서는 적절한 애너테이션 처리기를 직접 구현해야 한다.   
인스턴스 처리기 업이 정적 메서드가 아닌 인스턴스 메서드에 @MyTest를 달면 컴파일은 잘 되지만, 테스트 도구를 실행할 때 문제가 된다.   
관련 방법
* [javax.annotation.processing API](https://docs.oracle.com/javase/7/docs/api/javax/annotation/processing/package-summary.html)
* [Baeldung님의 annotation processing 글](https://www.baeldung.com/java-annotation-processing-builder)

다음 코드는 @MyTest 애너테이션을 실제 적용한 모습이다.   

```java
public class Sample {
    @MyTest
    public static void m1() {} // 성공해야 한다.
    
    public static void m2() {}
    
    @MyTest
    public static void m3() {  // 실패해야 한다.
        throw new RuntimeException("실패");
    }
    
    public static void m4() {}
    
    @MyTest
    public void m5() {}        // 잘못 사용한 예 : 정적 메서드가 아닌 인스턴스 메서드다.
 
    public static void m6() {}
    
    @MyTest
    public static void m7() {  // 실패해야 한다.
        throw new RuntimeException("실패");
    }
    
    public static void m8() {}
}
```

이와 같은 애너테이션을 "아무 매개변수 없이 단순히 대상에 마킹(marking)한다"는 뜻에서 마킹 애너테이션이라 한다.   
이 애너테이션을 사용하면 애너테이션의 이름을 틀리거나 메서드 선언 외의 요소에 달면 컴파일 오류가 발생한다.

Sample 클래스에는 정적 메서드가 7개, 4개에 @MyTest 애너테이션을 달았다. **(m1, m3, m5, m7)**      
m3와 m7 메서드는 예외를 던진다. m5는 인스턴스 메서드 이므로 @MyTest를 잘못 사용한 경우다.   
결국 4개의 테스트 메서드 중 ```성공 1개, 실패 2개, 잘못 사용한 경우 1개``` 이다.

@MyTest 애너테이션이 Sample 클래스의 의미에 직접적인 영향을 주지는 않는다.   
**그저 이 애너테이션에 관심 있는 프로그램에게 추가 정보를 제공할 뿐이다.**

> 대상 코드의 의미는 그대로 둔 채 그 애너테이션에 관심 있는 도구에서 특별한 처리를 할 기회를 준다.

다음은 @MyTest 마커 애너테이션을 사용하는 예시다.

```java
public class RunMyTests {
    public static void main(String[] args) throws Exception {
        int tests = 0;
        int passed = 0;
        Class<?> testClass = Class.forName(Sample.class.getName());
        for (Method m : testClass.getDeclaredMethods()) {
            if (m.isAnnotationPresent(MyTest.class)) {
                tests++;
                try {
                    m.invoke(null);
                    passed++;
                    System.out.println(m + " 통과 : ");
                } catch (InvocationTargetException wrappedExc) {
                    Throwable exc = wrappedExc.getCause();
                    System.out.println(m + " 실패 : " + exc);
                } catch (Exception exc) {
                    System.out.println("잘못 사용한 @MyTest: " + m);
                }
            }
        }
        System.out.printf("성공: %d, 실패: %d%n", passed, tests - passed);
    }
}
```

RunTests 클래스는 Sample 클래스 이름을 사용해서 해당 클래스 내부의 @MyTest 애너테이션이 달린 메서드를 차례로 호출한다.

> 런타임에 클래스의 정보를 읽고 메서드를 호출하는 등의 작업은 Reflection을 사용하면 가능하다.      
> [Oracle Reflection 문서](https://www.oracle.com/technical-resources/articles/java/javareflection.html) 를 통해서 어렵지 않게 학습이 가능하다.

* isAnnotationPresent : 실행할 메서드를 찾아주는 메서드
* invoke() : Method 객체 자신을 호출(실행)하는 메서드
* InvocationTargetException : 테스트 메서드가 예외를 던지면 리플렉션 매커니즘이 InvocationTargetException으로 감싸서 다시 던진다.
  * InvacationTargetException이 발생했다면 @MyTest 애너테이션을 잘못 사용했다는 뜻이다.
    * 인스턴스 메서드에 사용
    * 매개변수가 있는 메서드
    * 호출할 수 없는 메서드
    * 등등에 사용했을 것이다.

아래는 RunTests의 실행 결과다

```java
public static void item39_20220103.Sample.m7() 실패 : java.lang.RuntimeException: 실패
public static void item39_20220103.Sample.m3() 실패 : java.lang.RuntimeException: 실패
잘못 사용한 @MyTest: public void item39_20220103.Sample.m5()
public static void item39_20220103.Sample.m1() 통과 : 
성공: 1, 실패: 3
```

> 참고로 실행시 마다 출력되는 순서는 다르다. testClass.getDeclaredMethods()는 클래스에 명시한 순서대로 메서드를 가져오지 않고 임의의 순서로 가져온다.

이제 특정 예외를 던져야만 성공하는 테스트를 지원하도록 해보자.   
아래는 새로운 애너테이션 ExceptionMyTest다.

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionMyTest {
    Class<? extends Throwable> value();
}
```

이 애너테이션의 매개변수 타입은 Class<? extends Throwable>이다.

여기서의 와일드카드 타입은 많은 의미를 담고 있다. "Throwable을 확장한 클래스의 Class 객체"라는 뜻이며, 따라서 모든 예외 타입을 다 수용한다.   
(이는 한정적 타입토큰의 또 하나의 사례다.)

@ExceptionMyTest을 활용해보자.

```java
public class RunMyTests2 {
    public static void main(String[] args) throws Exception {
        runTest(Sample2.class.getName());
    }

    private static void runTest(String className) throws ClassNotFoundException {
        int tests = 0;
        int passed = 0;
        Class<?> testClass = Class.forName(className);
        for (Method m : testClass.getDeclaredMethods()) {
            if (m.isAnnotationPresent(ExceptionMyTest.class)) {
                tests++;
                try {
                    m.invoke(null);
                    System.out.printf("테스트 %s 실패: 예외를 던지지 않음%n", m);
                } catch (InvocationTargetException wrappedExc) {
                    Throwable exc = wrappedExc.getCause();
                    Class<? extends Throwable> excType = m.getAnnotation(ExceptionMyTest.class).value();
                    if (excType.isInstance(exc)) {
                        passed++;
                        System.out.println("테스트 통과: " + m);
                    } else {
                        System.out.printf("테스트 %s 실패: 기대한 예외 %s, 발생한 예외 %s%n", m, excType.getName(), exc);
                    }
                } catch (Exception exc) {
                    System.out.println("잘못 사용한 @MyExceptionTest: " + m);
                }
            }
        }
        System.out.printf("성공: %d, 실패: %d%n", passed, tests - passed);
    }
}
```

@MyTest 애너테이션을 사용하는 코드와 비슷해 보이지만, 이 코드는 애너테이션의 매개변수 값을 추출하여 테스트 메서드가 올바른 예외를 던지는지 확인하는데 사용한다.

테스트 프로그램이 문제없이 컴파일되면 애너테이션 매개변수가 가리키는 예외가 올바른 타입이라는 뜻이다.   
단, 해당 예외의 클래스 파일이 컴파일타임에 존재했으나 런타임에는 존재하지 않을 수 있다. 이런 경우 테스트 러너가 TypeNotPresentException을 던질 것이다.

이 예외 테스트에서 더 발전시켜서, 예외를 여러 개 명시하고 그 중 하나가 발생하면 성공하게 만들 수도 있다.

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionMyTest2 {
    Class<? extends Throwable>[] value();
    // 애너테이션의 매개변수를 배열로 받는다. 
}
```

```java
public class Sample3 {
    @ExceptionMyTest2({IndexOutOfBoundsException.class, NullPointerException.class})
    public static void doublyBad() {
        List<String> list = new ArrayList<>();
        
        // 자바 API 명세에 따르면 다음 메서드는 IndexOutOfBoundException이나 
        // NullPointerException을 던질 수 있다.
        list.add(5, null);
    }
}
```

다음은 새로운 @ExceptionMyTest2를 지원하도록 테스트 러너를 수정한 모습이다.

```java
public class RunMyTests3 {
    public static void main(String[] args) throws Exception {
        runTest(Sample3.class.getName());
    }

    private static void runTest(String className) throws ClassNotFoundException {
        int tests = 0;
        int passed = 0;
        Class<?> testClass = Class.forName(className);
        for (Method m : testClass.getDeclaredMethods()) {
            if (m.isAnnotationPresent(ExceptionMyTest2.class)) {
                tests++;
                try {
                    m.invoke(null);
                    System.out.printf("테스트 %s 실패: 예외를 던지지 않음%n", m);
                } catch (InvocationTargetException wrappedExc) {
                    Throwable exc = wrappedExc.getCause();
                    int oldPassed = passed;
                    Class<? extends Throwable>[] excTypes = m.getAnnotation(ExceptionMyTest2.class).value();
                    for (Class<? extends Throwable> excType : excTypes) {
                        if (excType.isInstance(exc)) {
                            passed++;
                            System.out.println("테스트 통과: " + m);
                            break;
                        }
                    }
                    if (passed == oldPassed) {
                        System.out.printf("테스트 %s 실패: %s, %n", m, exc);
                    }
                } catch (Exception exc) {
                    System.out.println("잘못 사용한 @MyExceptionTest: " + m);
                }
            }
        }
        System.out.printf("성공: %d, 실패: %d%n", passed, tests - passed);
    }
}
```

### @Repeatable 메타 애너테이션과 컨테이너 애너테이션

자바8에서는 여러 개의 값을 받는 애너테이션을 다른 방식으로도 만들 수 있다.

배열 매개변수를 사용하는 대신 애너테이션에 @Repeatable 메타매너테이션을 다는 방식이다.   
@Repeatable을 단 애너테이션은 하나의 프로그램 요소에 여러 번 달 수 있다.

단, 주의할 점이 있다. 
1. @Repeatable을 단 애너테이션을 반환하는 '컨테이너 애너테이션'을 하나 더 정의하고, @Repeatable에 이 컨테이너 애너테이션의 class객체를 매개변수로 전달해야 한다.
2. 컨테이너 애너테이션은 내부 애너테이션 타입의 배열을 반환하는 value 메서드를 정의해야 한다.
3. 컨테이너 애너테이션 타입에는 적절한 보존 정책(@Retention)과 적용 대상(@Target)을 명시해야 한다. 그렇지 않으면 컴파일 되지 않을 것이다.

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(ExceptionMyTestContainer.class)
public @interface ExceptionMyTest3 {
    Class<? extends Throwable> value();
}

// 컨테이너의 대상 애너테이션의 Retention, Target과 동일하게 맞춰야 한다. 그렇지 않으면 컴파일 오류 발생!!
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionMyTestContainer {
  ExceptionMyTest3[] value();
}

// 반복가능 애너테이션의 예제 코드
public class Sample4 {
  @ExceptionMyTest3(IndexOutOfBoundsException.class)
  @ExceptionMyTest3(NullPointerException.class)
  public static void doublyBad() {
    List<String> list = new ArrayList<>();

    // 자바 API 명세에 따르면 다음 메서드는 IndexOutOfBoundException이나
    // NullPointerException을 던질 수 있다.
    list.add(5, null);
  }
}
```

반복 가능 애너테이션은 처리할 때도 주의를 요한다. 반복 가능 애너테이션을 여러 개 달면 하나만 달았을 때와 구분하기 위해 해당 '컨테이너' 애너테이션 타입이 적용된다.

**getAnnotationByType 메서드는 이 둘을 구분하지 않아서** 반복 가능 애너테이션과 그 컨테이너 애너테이션을 모두 가져오지만,   
**isAnnotationPresent 메서드는 둘을 명확히 구분한다.** 따라서 반복 가능 애너테이션을 여러 번 단 다음 isAnnotationPresent로 반복 가능 애너테이션이 달렸는지 검사한다면 "그렇지 않다"라고 알려준다.   
(컨테이너 애너테이션이 달렸기 때문이다.)

isAnnotationPresent로 @Repeatable을 사용한 애너테이션을 검사할때는 해당 애너테이션과 컨테이너 애너테이션 둘을 따로따로 확인해야 한다.

```java
public class RunMyTests4 {
  public static void main(String[] args) throws Exception {
    runTest(Sample4.class.getName());
  }

  private static void runTest(String className) throws ClassNotFoundException {
    int tests = 0;
    int passed = 0;
    Class<?> testClass = Class.forName(className);
    for (Method m : testClass.getDeclaredMethods()) {
      // 해당 애너테이션과 컨테이너 애너테이션 각각 검사한다. 
      if (m.isAnnotationPresent(ExceptionMyTest3.class) || m.isAnnotationPresent(ExceptionMyTestContainer.class)) {
        tests++;
        try {
          m.invoke(null);
          System.out.printf("테스트 %s 실패: 예외를 던지지 않음%n", m);
        } catch (InvocationTargetException wrappedExc) {
          Throwable exc = wrappedExc.getCause();
          int oldPassed = passed;
          // getAnnotationsByType은 기존 애너테이션과 컨테이너 애너테이션의 타입을 구분하지 않는다.
          ExceptionMyTest3[] excTypes = m.getAnnotationsByType(ExceptionMyTest3.class);
          for (ExceptionMyTest3 excType : excTypes) {
            if (excType.value().isInstance(exc)) {
              passed++;
              System.out.println("테스트 통과: " + m);
              break;
            }
          }
          if (passed == oldPassed) {
            System.out.printf("테스트 %s 실패: %s, %n", m, exc);
          }
        } catch (Exception exc) {
          System.out.println("잘못 사용한 @MyExceptionTest: " + m);
        }
      }
    }
    System.out.printf("성공: %d, 실패: %d%n", passed, tests - passed);
  }
}
```

반복 가능 애너테이션을 사용해 하나의 프로그램 요소에 같은 애너테이션을 여러 번 달 때의 코드 가독성을 높여보았다.   
@Repeatable 애너테이션은 어디에 사용하냐에 따라 가독성이 좋아지거나 나빠질 수 있으니 잘 선택해서 적용하도록 하자.