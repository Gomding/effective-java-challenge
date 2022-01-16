# 아이템53. 가변인수는 신중히 사용하라

가변인수 메서드는 명시한 타입의 인수를 0개 이상 받을 수 있다.

```java
// 가변인수는 아래와 같이 '...' 키워드를 사용한다.
public void doSomething(int... numbers) {
    // do something action
}
```

가변인수 메서드를 호출하면, 가장 먼저 **인수의 개수와 길이가 같은 배열을 만든다.** 이후 인수들을 이 배열에 저장하여 가변인수 메서드에 건네준다.

```java
public static void main(String[] args) {
    doSomething(1, 2, 3); 
    // 인수의 개수가 3개이므로 해당 가변인수 메서드는 길이가 3인 배열을 만들어 인수들을 담는다. 해당 배열을 메서드에 넘겨준다.
}

public static void doSomething(int... numbers) {
    // do something action
}
```

다음은 입력받은 int 인수들의 합을 계산해주는 가변인수 메서드다.

```java
public class VarargsEx {
    public static void main(String[] args) {
        System.out.println(sum(1, 2, 3)); // 6을 출력
        System.out.println(sum()); // 0을 출력
    }
    
    static int sum(int... args) {
        int sum = 0;
        for (int arg : args) {
            sum += arg;
        }
        return sum;
    }
}
```

인수가 1개 이상이어야 하는 경우도 있다. 최솟값을 찾는 메서드인데 인수를 0개도 받을 수 있도록 설계하는 건 좋지않다.   
(건네진 인수가 0개인데 최소값 0을 반환한다. 라는건 이상하다. 건네진 값이 없으면 최소값이란게 존재할 수 없는것이다. 특별한 제약조건이 있는게 아니라면)   
**인수 개수는 런타임에 (자동 생성된)배열의 길이로 알 수 있다.**

```java
    static int min(int... args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("인수가 1개 이상 필요합니다.");
        }
        int min = args[0];
        for (int i = 1; i < args.length; i++) {
            min = Math.min(min, args[i]);
        }
        return min;
    }
```

이 방식에는 문제가 몇 개 있다.
심각한 문제는 0개의 인수를 받아들일 수 있으며 런타임에 오류를 발생시킨다는 점이다.   
애초에 0개의 인수를 받아들이지 못하게 하고싶다.

args 유효성 검사를 명시적으로 해야 하고, min의 초깃값을 Integer.MAX_VALUE로 설정하지 않고는 더 명료한 for-each문도 사용할 수 없다.

다행히 훨씬 나은 방법은 있다. 매개변수를 2개 받는 것이다.   
첫 번째 매개변수는 평범한 매개변수를 받고, 두 번쨰로 가변인수를 받으면 문제가 사라진다.

```java
    static int min(int firstArg, int... remainingArgs) {
        int min = firstArg;
        for (int arg : remainingArgs) {
            min = Math.min(min, arg);
        }
        return min;
    }
```

가변인수는 인수 개수가 정해지지 않았을 때 아주 유용하다. printf는 가변인수와 한 묶음으로 자바에 도입됐다. 이와 함께 핵심 리플렉션 기능도 재정비 되었다.   
pringf와 리플렉션 모두 가변인수 덕을 톡톡히 보고 있다.

### 성능이 중요하다면 가변인수를 가능한 피하라

성능에 민감한 상황이라면 가변인수가 걸림돌이 될 수 있다. 가변인수 메서드는 호출될 때마다 배열을 새로 하나 할당하고 초기화한다.

다행히, 이 비용을 감당할 수 없지만 가변인수의 유연성이 필요할 때 선택할 수 있는 멋진 패턴이 있다.   
예를 들어 해당 메서드 호출의 95%가 인수를 3개 이하로 사용한다고 해보자.   
다음처럼 인수가 0개인것 부터 4개인것까지 총 5개의 메서드를 다중정의하자.

```java
public class VarargsEx3 {
    
    static void doSomething() { }

    static void doSomething(int a1) { }

    static void doSomething(int a1, int a2) { }

    static void doSomething(int a1, int a2, int a3) { }

    static void doSomething(int a1, int a2, int a3, int... rest) { }
}
```

이제 95%의 메서드 호출은 매개변수 0개 ~ 3개를받는 메서드는 매개변수 숫자에 맞는 메서드를 사용한다. 따라서 배열이 생성될 일도 없다.   
나머지 5%의 호출은 인자 4개(가변인수 포함)를 받는 메서드에서 처리할 것이다.

이 기법은 보통 때는 별 이득이 없지만, 꼭 필요한 특수 상황에서는 사막의 오아시스가 되어줄 것이다.

EnumSet의 정적 팩터리도 이 기법을 사용해 열거 타입 집합 생성 비용을 최소화한다.   
EnumSet은 비트 필드를 대체하면서 성능까지 유지해야 하므로 아주 적절하게 활용한 예라 할 수 있다.

### 핵심 정리
* 인수 개수가 일정하지 않은 메서드를 정의해야 한다면 가변인수가 반드시 필요하다.   
* 메서드를 정의할 때 필수 매개변수는 가변인수 앞에 두자.   
* 가변인수를 사용할 떄는 성능 문제까지 고려하자.