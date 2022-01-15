# 아이템52. 다중정의는 신중히 사용하라

### 자중정의(overloading)의 메서드 선택과정

다음 예시는 컬렉션을 집합(Set), 리스트(List), 그 외(집합과 리스트에 해당하지 않는 컬렉션)으로 구분하고자 만든 프로그램이다.

```java
public class CollectionClassifier {
    public static String classify(Set<?> s) {
        return "집합";
    }

    public static String classify(List<?> s) {
        return "리스트";
    }

    public static String classify(Collection<?> s) {
        return "그 외";
    }

    public static void main(String[] args) {
        Collection<?>[] collections = {
                new HashSet<String>(),
                new ArrayList<String>(),
                new HashMap<String, String>().values()
        };

        for (Collection<?> collection : collections) {
            System.out.println(classify(collection));
        }
    }
}
```

이 프로그램은 "집합", "리스트", "그 외"를 출력할 것 같지만, 실제로 수행해보면 "그 외", "그 외", "그 외" 이렇게 출력된다.

이유는 다중정의(Overloading)된 세 classify 중 어느 메서드를 호출할지가 컴파일타임에 정해지기 때문이다.   
컴파일타임에는 for 문 안의 collection은 항상 ```Collection<?>``` 타입이다.   
런타임에는 타입이 매번 달라지지만, 호출할 메서드를 선택하는 데는 영향을 주지 못한다.

따라서 컴파일타임의 매개변수 타입을 기준으로 항상 세 번째 메서드인 ```classify(Collection<?>)```만 호출하는 것이다.

이처럼 예상했던 동작과 다른 이유는 **재정의(override)한 메서드는 동적으로 선택**되고, **다중정의(overloading)한 메서드는 정적으로 선택**되기 때문이다.

### 메서드 재정의와 다중정의 메서드 선택

메서드를 재정의했다면 해당 객체의 런타임 타입이 어떤 메서드를 호출할지의 기준이 된다.   
메서드를 재정의한 다음 '하위 클래스의 인스턴스'에서 그 메서드를 호출하면 재정의한 메서드가 실행된다.   
컴파일 타임에 그 인스턴스의 타입이 무엇이었냐는 상관없다.

```java
public class WineEx {
    public static void main(String[] args) {
        List<Wine> wines = Arrays.asList(
                new Wine(),
                new SparklingWine(),
                new Champagne()
        );

        for (Wine wine : wines) {
            System.out.println(wine.name());
        }
    }
}

class Wine {
    String name() {
        return "포도주";
    }
}

class SparklingWine extends Wine {
    @Override
    String name() {
        return "스파클링 포도주";
    }
}

class Champagne extends SparklingWine {
    @Override
    String name() {
        return "샴페인";
    }
}
```

이 프로그램은 예상되는 것처럼 "포도주", "스파클링 포도주", "샴페인"을 차례로 출력한다.   
for문에서의 컴파일타임 타입이 모두 Wine인것과는 무관하게 해당 객체 타입의 인스턴스 타입에서 재정의한 메서드를 호출하고 있다.

> 정리   
> 재정의(override)한 메서드는 런타임 타입이 어떤 메서드를 호출할 지 기준이 된다.   
> 다중정의는 매개변수의 컴파일타임 타입을 기준으로 메서드가 호출된다. 

제일 처음 예시인 CollectionClassifier 예시에서 프로그램의 원래 의도는 매개변수의 런타임 타입에 기초해 적절한 다중정의 메서드로 자동 분배하는 것이다.   
다중정의는 이렇게 동작하지 않는다. 이 문제는 모든 classify 메서드를 하나로 합친 후 instanceof 로 명시적 검사를 수행하면 깔끔하게 해결된다.   
(다중정의를 사용하지 않고)

```java
public static String classify(Collection<?> collection) {
    return c instanceof Set ? "집합" :
        c instanceof List ? "리스트" : "그 외";
}
```

### 다중정의로 사용자에게 혼돈을 주는지 고려해보라

프로그래머에게는 재정의가 정상적인 동작 방식이고, 다중정의가 예외적인 동작으로 보일 것이다.   
재정의한 메서드는 의도한대로 동작하지만, 다중정의한 메서드는 이러한 기대를 무시할 수 있다.

> 헷갈릴 수 있는 코드는 작성하지 않는 게 좋다.(특히나 공개 API라면 더욱더)

API 사용자가 매개변수를 넘기면서 어떤 다중정의 메서드가 호출될지 모르거나 한번 더 생가해봐야 한다면 프로그램이 오동작할 확률이 올라간다.   
런타임에 이상하게 동작할 것이며 API사용자들은 문제를 진단하느라 긴 시간을 허비할 것이다.

**다중정의가 혼동을 일으키는 상황을 피해야한다.**

### 다중정의를 최대한 안전하게 사용하려면 어떤 규칙을 적용해야할까?

* 안전하고 보수적으로 가려면 매개변수 수가 같은 다중정의는 만들지 말자.
  * 메서드의 매개변수 수가 다르게 다중정의를 만들어라.
* 가변인수(varargs)를 사용하는 메서드라면 다중정의를 아예 하지말아야 한다.

이 규칙만 잘 따르면 어떤 다중정의 메서드가 호출될 지 헷갈릴 일은 없을 것이다.   

### 다중정의가 혼란스럽다면 다중정의를 피하고 메서드 이름을 다르게 만들어라

이는 각각 다른 메서드를 만들라는 의미다.

자바의 ObjectOutputStream 클래스를 살펴보자. 이 클래스의 write 메서드는 모든 기본 타입과 일부 참조 타입용 변형을 가지고 있다.   
그런데 다중정의가 아닌, 모든 메서드에 다른 이름을 지어주는 길을 선택했다.

* writeBoolean
* writeInt
* writeLong

이 방식이 다중정의보다 나은 또 다른 점은 read 메서드의 이름과 짝지어주기 좋다. 이는 직관적일 수 있다.   
다음은 ObjectInputStream의 메서드다.

* readBoolean
* readInt
* readLong

### 생성자의 다중정의는 어떤가?

생성자는 이름을 다르게 지을 수 없으니 두 번째 생성자부터는 무조건 다중정의가 된다.   
하지만 정적 팩터리 메서드라는 대안을 활용할 수 있다.

생성자는 재정의(override)할 수 없으니 다중정의와 재정의가 혼용될 걱정도 없다.

그래도 여러 생성자가 같은 수의 매개변수를 받아야 하는 경우를 완전히 피해갈 수는 없을 테니, 그럴 때를 대비해 안전 대책을 배워두면 도움이 될 것이다.

### 다중정의를 사용한다면 주어진 매개변수에 따라 명확히 구분할 수 있도록 구현하라

매개변수 수가 같은 다중정의 메서드가 많더라도, 그 중 어느 것이 주어진 매개변수 집합을 처리할지가 명확히 구분된다면 헷갈릴 일은 없을 것이다.

즉, 매개변수 중 하나 이상이 근본적으로 다르다면 안전하다.   
근본적으로 다르다는 건 두 타입의 값을 서로 어느쪽으로든 형변활할 수 없다는 뜻이다.

이 조건만 충족하면 어느 다중정의 메서드를 호출할지가 매개변수들의 런타임 타입만으로 결졍된다.   
컴파일타임 타입에는 영향을 받지 않게 되고, 혼란을 주는 주된 원인이 사라진다.

ArrayList에는 int를 받는 생성자와 Collection을 받는 생성자가 있는데, 어떤 상황에서든 두 생성자 중 어느것이 호출될지 헷갈릴 일은 없을 것이다.

### 자바5부터 기본형의 오토박싱이 나오면서 생긴 혼란

자바 4까지는 기본형 타입과 참조 타입이 근본적으로 달랐다. 하지만 자바 5부터 오토박싱이 생기면서 혼란이 생겼다.

다음 프로그램을 살펴보자.

```java
public class SetListEx {
    public static void main(String[] args) {
        Set<Integer> set = new TreeSet<>();
        List<Integer> list = new ArrayList<>();

        // int 타입 i를 활용해서 요소를 넣었다.
        for (int i = -3; i < 3; i++) {
            set.add(i);
            list.add(i);
        }

        // 위와 마찬가리로 int타입 i를 활용해서 요소를 삭제한다.
        for (int i = 0; i < 3; i++) {
            set.remove(i);
            list.remove(i);
        }

        System.out.println("set = " + set + " list = " + list);
    }
}
```

예상한 출력결과는 "set = [-3, -2, -1] list = [-3, -2, -1]"을 예상했을 것이다.       
하지만 실제 결과는 "set = [-3, -2, -1] list = [-2, 0, 2]" 이다.

set.remove(i)의 시그니처는 remove(Object obj)이다. 따라서 set은 의도한대로 동작을 한다.

list.remove(i)는 다중정의된 remove(int index), remove(Object obj) 메서드가 있는데 이 중 remove(int index)를 선택한다.   
선택된 list의 remove는 '지정한 위치'의 원소를 제거하는 기능을 수행한다.
처음 상태가 [-3, -2, -1, 0, 1, 2]이고, 0번째, 1번째, 2번쨰 순서대로 수행되면

* 0번째 삭제 : [-3, -2, -1, 0, 1, 2] -> [-2, -1, 0, 1, 2]
* 1번째 삭제 : [-2, -1, 0, 1, 2] -> [-2, 0, 1, 2]
* 2번째 삭제 : [-2, 0, 1, 2] -> [-2, 0, 2] 

이 문제는 list.remove의 인수를 Integer로 형변환하여 올바른 다중정의 메서드를 선택하게 하면 해결된다.   
혹은 Integer.valueOf를 이용해 i를 Integer로 변환한 후 list.remove에 전달해도 된다.

```java
for (int i = 0; i < 3; i++) {
    set.remove(i);
    list.remove((Integer)i);
}
```

이렇게 수정하면 의도한 대로 동작한다.

이 예제가 혼란스러웠던 이유는 ```List<E>```인터페이스가 remove(Object)와 remove(int)를 다중정의했기 때문이다.   
제네릭이 도입되기 전인 자바 4까지의 List에서는 Object와 int가 근본적으로 달라서 문제가 없었다.   
하지만 제네릭과 오토박싱이 등장하면서 두 메서드의 매개변수 타입이 더는 근복적으로 다르지 않게 되었다.

### 메서드 다중정의와 자바 8의 람다와 메서드 참조에서 오는 혼란

다음 두 코드를 보자

```java
// 1번. Thread의 생성자 호출
new Thread(System.out::println).start();

// 2번. ExecutorService의 submit 메서드 호출
ExecutorService exec = Executors.newCachedThreadPool();
exec.submit(System.out::println);
```

1번과 2번이 모습은 비슷하지만, 2번만 컴파일 오류가 난다.   
넘겨진 인수는 모두 System.out::println으로 똑같고, 양쪽 모두 Runnable을 받는 형제 메서드를 다중정의하고 있다.

왜 2번만 실패할까? 원인은 바로 submit 다중정의 메서드 중에는 ```Collable<T>```를 받는 메서드도 있다는 데 있다.   
하지만 모든 pringln이 void를 반환하니, 반환값이 있는 Callable과 헷갈릴 리는 없다고 생각할지도 모르겠다.    
이는 합리적인 추론이지만 다중정의 해소(적절한 다중정의 메서드를 찾는 알고리즘)는 이렇게 동작하지 않는다.

놀라운 사실 하나는, 만약 println이 다중정의 없이 단 하나만 존재한다면 이 submit 메서드 호출이 제대로 컴파일됐을 거라는 사실이다.

지금은 참조된 메서드(println)와 호출한 메서드(submit) 양쪽 다 다중정의되어, 다중정의 해소 알고리즘이 우리의 기대처럼 동작하지 않는 상황이다.

기술적으로 말하면 System.out::println은 부정확한 메서드 참조다.   
"암시적 타입 람다식"이나 부정확한 메서드 참조같은 인수 표현식은 목표 타입이 선택되기 전에는 그 의미가 정해지지 않기 때문에 적용성 테스트 때 무시된다.   
**이것이 문제의 원인이다**

다중정의된 메서드들이 함수형 인터페이스를 인수로 받을 때, 비록 서로 다른 함수형 인터페이스라도 인수 위치가 같으면 혼란이 생긴다.

> 메서드를 다중정의할 때, 서로 다른 함수형 인터페이스라도 같은 위치의 인수로 받아서는 안 된다.   
> 서로 다른 함수형 인터페이스라도 서로 근복적으로 다르지 않다는 뜻이다.

컴파일 옵션에 -Xlint:overloads를 지정하면 이런 종류의 다중정의를 경고해줄 것이다.

다중 정의된 메서드 중 하나를 선택하는 규칙은 매우 복잡하며, 자바가 버전업될수록 더 복잡해지고 있다. 이 모두를 이해하고 사용하는 프로그래머는 극히 드물 것이다.

### 다중정의를 사용헤야할 때 안전하게 사용하는 방법

만약 이번 아이템에서 설명한 규칙을 지킬 수 없는 상황도 있을 것이다.

이미 만들어진 클래스에 기능을 추가하면 그런 상황이 올 수 있다.

String은 자바 4 시절부터 contentEquals(StringBuffer) 메서드를 가지고 있었다.   
그런데 자바 5에서 StringBuffer, StringBuilder, String, CharBuffer 등의 비슷한 부류의 타입을 위한 공통 인터페이스로 CharSequence가 등장하였고,   
자연스럽게 String에도 CharSequence를 받은 contentEquals가 다중정의되었다.

이는 이번 아이템의 지침을 대놓고 어기는 상황이다. 다행히 이 두 메서드는 같은 객체를 입력하면 완전히 같은 작업을 수행한다.   
따라서 단점은 없고 혼란스러울 이유도 없다. 

> 어떤 다중정의 메서드가 호출되는지 몰라도 기능이 똑같다면 신경 쓸 게 전혀없다.   

가장 일반적인 방법은 상대적으로 더 특수한 다중정의 메서드에서 덜 특수한(더 일반적인) 다중정의 메서드로 일을 넘겨버리는 것이다.

```java
public boolean contentEquals(StringBuffer sb){
    return contentEquals((CharSequence)sb);
}
```

자바 라이브러리는 이번 아이템의 정신을 지켜내려 애쓰고 있지만, 실패한 클래스도 몇 개 있다.   
예컨대 String 클래스의 valueOf(char[])과 valueOf(Object)는 같은 객체를 건네더라도 전혀 다른 일을 수행한다.