# 아이템37. ordinal 인덱싱 대신 EnumMap을 사용하라

배열이나 리스트에서 원소를 꺼낼 때 ordinal 메서드로 인덱스를 얻는 코드가 있다.

식물을 나타낸 클래스를 살펴보자

```java
public class Plant {
    // ANNUAL(한해살이), PERENNIAL(여러해살이), 두해살이(BIENNIAL)
    enum LifeCycle { ANNUAL, PERENNIAL, BIENNIAL }
    
    private final String name;
    private final LifeCycle lifeCycle;

    public Plant(String name, LifeCycle lifeCycle) {
        this.name = name;
        this.lifeCycle = lifeCycle;
    }

    @Override
    public String toString() {
        return name;
    }
}
```

다음으로 정원(garden)에 심은 식물들을 배열 하나로 관리하고 이들을 생애주기별로 묶어보자.

생애주기별로 총 3개의 집합을 만들고 정원을 한 바퀴 돌며 각 식물을 해당 집합에 넣는다.

아래 코드는 집합(Set)들을 배열 하나에 넣고 생애주기의 ordinal 값을 배열의 인덱스로 사용한다.   
(이는 잘못 사용하는 예시다.)

```java
public class GardenExample1 {
    public static void main(String[] args) {
        List<Plant> garden = Arrays.asList(
                new Plant("한해살이 식물", Plant.LifeCycle.ANNUAL),
                new Plant("여러해살이 식물", Plant.LifeCycle.PERENNIAL),
                new Plant("두해살이 식물", Plant.LifeCycle.BIENNIAL)
        );

        // ordinal 값을 배열의 인덱스로 사용하는 코드
        @SuppressWarnings("unchecked") Set<Plant>[] plantsByLifeCycle = (Set<Plant>[]) new Set[Plant.LifeCycle.values().length];
        for (int i = 0; i < plantsByLifeCycle.length; i++) {
            plantsByLifeCycle[i] = new HashSet<>();
        }
        for (Plant plant : garden) {
            plantsByLifeCycle[plant.lifeCycle().ordinal()].add(plant);
        }

        // 결과 출력
        for (int i = 0; i < plantsByLifeCycle.length; i++) {
            System.out.printf("%s: %s%n", Plant.LifeCycle.values()[i], plantsByLifeCycle[i]);
        }
    }
}
```

위 코드는 동작은 하지만 문제가 많다.
* 배열은 제네릭과 호환되지 않으니 비검사 형변환을 수행해야 한다. (@SuppressWarnings를 사용해서 경고는 억제했다.)
* 배열은 각 인덱스의 의미를 모르니 출력 결과에 직접 레이블을 달아야한다. (디버깅도 어렵게 만든다.)
* 가장 심각한 문제는 정확한 정숫값을 사용한다는 것을 우리가 직접 보증해야한다.
  * 정수는 열거 타입과 달리 타입 안전하지 않다.
  * 잘못된 값을 사용하면 잘못된 동작을 아무런 예외없이 정상 동작시킨다.

### EnumMap을 이용한 해결

위에서 배열은 실질적으로 열거 타입 상수를 값으로 매핑하는 일을 한다.   
매핑하는 것은 Map을 사용할 수 있다.

열거 타입을 키로 사용하도록 설계한 아주 빠른 Map 구현체가 존재하는데 바로 **EnumMap**이 그 주인공이다.

```java
public class GardenExample2 {
    public static void main(String[] args) {
        List<Plant> garden = Arrays.asList(
                new Plant("한해살이 식물", Plant.LifeCycle.ANNUAL),
                new Plant("여러해살이 식물", Plant.LifeCycle.PERENNIAL),
                new Plant("두해살이 식물", Plant.LifeCycle.BIENNIAL)
        );

        Map<Plant.LifeCycle, Set<Plant>> plantByLifeCycle =
                new EnumMap<>(Plant.LifeCycle.class);
        for (Plant.LifeCycle lifeCycle : Plant.LifeCycle.values()) {
            plantByLifeCycle.put(lifeCycle, new HashSet<>());
        }
        for (Plant plant : garden) {
            plantByLifeCycle.get(plant.lifeCycle()).add(plant);
        }
        System.out.println(plantByLifeCycle);
    }
}
```

더 짧고 명료하고 안전하고 성능도 원래 버전과 비등하다.

안전하지 않은 형변환은 쓰지 않고, 맵의 키인 열거 타입이 그 자체로 출력용 문자열을 제공하니 출력 결과에 직접 레이블을 달 일도 없다.

> 배열 인덱스를 계산하는 과정에서 오류가 날 가능성도 원천봉쇄된다.

EnumMap의 성능이 ordinal을 쓴 배열에 비견되는 이유는 그 내부에서 배열을 사용하기 때문이다.   
내부 구현 방식을 안으로 숨겨서 Map의 타입 안전성과 배열의 성능을 모두 얻어낸 것이다.

```java
//EnumMap의 내부 get 메서드 구현
// 내부적으로 ordinal()을 사용해서 요소를 구하고 있다.
public V get(Object key) {
    return (isValidKey(key) ?
        unmaskNull(vals[((Enum<?>)key).ordinal()]) : null);
}
```

EnumMap의 생성자가 받는 키 타입 **Class 객체는 한정적 타입 토큰**으로, **런타임 제네릭 타입 정보를 제공한다**.(아이템 33)

```java
// EnumMap의 생성자 코드
// 인자로 받은 Class를 keyType 필드에 저장해둔다.
public EnumMap(Class<K> keyType) {
    this.keyType = keyType;
    keyUniverse = getKeyUniverse(keyType);
    vals = new Object[keyUniverse.length];
}
```

### 스트림을 사용하여 간략하게 리팩터링

스트림을 사용해 맵을 관리하면 코드를 더 줄일 수 있다.   
아래 코드는 위 예제의 동작을 거의 그대로 모방한 가장 단순한 형태의 스트림 기반 코드다.

```java
public class GardenExample3 {
    public static void main(String[] args) {
        List<Plant> garden = Arrays.asList(
                new Plant("한해살이 식물", Plant.LifeCycle.ANNUAL),
                new Plant("여러해살이 식물", Plant.LifeCycle.PERENNIAL),
                new Plant("두해살이 식물", Plant.LifeCycle.BIENNIAL)
        );

        Map<Plant.LifeCycle, List<Plant>> plantsByLifeCycle = garden.stream()
                                                                    .collect(groupingBy(Plant::lifeCycle));
        System.out.println(plantsByLifeCycle);
    }
}
```

위 코드는 한가지 다른점이 있다. EnumMap이 아닌 고유한 맵 구현체를 사용했기 때문에 EnumMap을 써서 얻은 공간과 성능 이점이 사라진다는 문제가 있다.

매개 변수 3개짜리 Collectors.groupingBy 메서드는 mapFactory매개변수에 원하는 맵 구현체를 명시해 호출할 수 있다.

```java
public class GardenExample4 {
    public static void main(String[] args) {
        List<Plant> garden = Arrays.asList(
                new Plant("한해살이 식물", Plant.LifeCycle.ANNUAL),
                new Plant("여러해살이 식물", Plant.LifeCycle.PERENNIAL),
                new Plant("두해살이 식물", Plant.LifeCycle.BIENNIAL)
        );

        EnumMap<Plant.LifeCycle, Set<Plant>> plantsByLifeCycle = garden.stream()
                .collect(groupingBy(Plant::lifeCycle, () -> new EnumMap<>(Plant.LifeCycle.class), toSet()));
        System.out.println(plantsByLifeCycle);
    }
}
```

스트림을 사용하면 EnumMap만 사용했을 때와는 살짝 다르게 동작한다.
* EnumMap 버전은 언제나 식물의 생애주기당 하나씩의 중첩 맵을 만든다.
  * 생애주기에 해당하는 식물이 정원에 없을 때도 모든 생애주기에 해당하는 집합(Set)을 하나씩 만들어 둔다. 비어있는 집합이라도!
* Stream 버전은 해당 생애주기에 속하는 식물이 있을 때만 만든다.

정원에 한해살이, 여러해살이 식물만 있다면?   
* 2종류의 생애주기 식물밖에 없지만 EnumMap은 전체에 해당하는 3개의 생애주기 매핑을 준비한다.
* Stream은 정원에 있는 2종류의 생애주기 식물만 매핑한다.

### 두 열거 타입 매핑

두 열거 타입 매핑을 위해 ordinal을 두번이나 쓴 배열들의 배열을 사용하는 코드도 있다.

다음 예시는 두 가지 상태(Phase)를 전이(Transition)와 매핑하도록 구현한 프로그램이다. 
* 액체(LIQUID)에서 고체(SOLID)로의 전이는 응고(FREEZE)
* 액체(LIQUID)에서 기체(GAS)로의 전이는 기화(BOIL)

```java
public enum Phase {
    SOLID, LIQUID, GAS;

    public enum Transition {
        MELT, FREEZE, BOIL, CONDENSE, SUBLIME, DEPOSIT;

        // 행은 from의 ordinal을, 열은 to의 ordinal을 인덱스로 쓴다.
        private static final Transition[][] TRANSITIONS = {
                { null, MELT, SUBLIME },
                { FREEZE, null, BOIL },
                { DEPOSIT, CONDENSE, null }
        };

        // 한 상태에서 다른 상태로의 전이를 반환
        public static Transition from(Phase from, Phase to) {
            return TRANSITIONS[from.ordinal()][to.ordinal()];
        }
    }
}
```

컴파일러는 ordinal과 배열 인덱스의 관계를 알 도리가 없다.   

> 즉, 잘못된 ordinal 값이 주어져도 런타임에 잘못된 결과를 받는다는 것을 알게되거나, 오류가 발생한다.

Phase 나 Phase.Transition 열거 타입을 수정하면서 상전이 표 TRANSITIONS를 함께 수정하지 않거나 잘못 수정하면 런타임 오류가 나거나 제대로된 결과를 받을 수 없다.

### EnumMap을 이용한 중첩 맵을 사용하도록 리팩터링

EnumMap을 사용하는 편이 훨씬 낫고, 타입 안전하게 사용할 수 있다. 유지보수 측면에서도 훨씬 좋다.

전이(Transition)을 하나 얻으려면 상태(Phase)의 from과 to가 필요하다.

이는 맵 2개를 중첩하면 쉽게 해결할 수 있다.

안쪽 맵은 이전 상태(Phase, from)와 전이(Transition)를 연결하고 바깥 맵은 이후 상태(Phase, to)와 안쪽 맵을 연결한다.

```java
public enum Phase2 {
    SOLID, LIQUID, GAS;

    public enum Transition2 {
        MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID),
        BOIL(LIQUID, GAS), CONDENSE(GAS, LIQUID),
        SUBLIME(SOLID, GAS), DEPOSIT(GAS, SOLID);

        private final Phase2 from;
        private final Phase2 to;

        Transition2(Phase2 from, Phase2 to) {
            this.from = from;
            this.to = to;
        }

        // 상전이 맵을 초기화한다.
        private static final Map<Phase2, Map<Phase2, Phase2.Transition2>> map =
                Arrays.stream(values())
                        .collect(groupingBy(
                                t -> t.from,
                                () -> new EnumMap<>(Phase2.class),
                                toMap(t -> t.to, t -> t, (x, y) -> y, () -> new EnumMap<>(Phase2.class))
                                )
                        );

        public static Phase2.Transition2 from(Phase2 from, Phase2 to) {
            return map.get(from).get(to);
        }
    }
}
```

첫 번째 수집기인 groupingBy에서는 전이를 이전 상태를 기준으로 묶고   
두 번째 수집기인 toMap 에서는 이후 상태를 전이에 대응시키는 EnumMap을 생성한다.

두 번째 수집기의 병합 함수인 (x, y) -> y 는 선언만 하고 실제로는 쓰이지 않는다. 이는 단지 EnumMap을 얻으려면 맵 팩터리가 필요하고 수집기들은 점층적 팩터리를 제공하기 때문이다.

#### 유지보수에도 유리함

이제 새로운 상태인 플라스마(PLASMA)를 추가해보자.

이 상태와 연결된 전이는 2개다.
* 첫 번째는 기체에서 플라스마로 변하는 이온화(IONIZE)   
* 두 번째는 플라스마에서 기체로 변하는 탈이온화(DEIONIZE)

**배열로 만든 코드 였다면?**   
새로운 상수를 Phase 1개, Phase.Transition에 2개를 추가, 원소 9개 짜리인 배열들의 배열을 원소 16개짜리(4X4)로 변경해야 한다.

**EnumMap으로 구현한 코드라면?**   
상태 목록에 PLASMA를 추가하고, 전이 목록에 IONIZE(GAS, PLASME) 와 DEIONIZE(PLASMA, GAS)만 추가하면 끝이다.

```java
public enum Phase2 {
  SOLID, LIQUID, GAS, PLASMA;

  public enum Transition2 {
    MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID),
    BOIL(LIQUID, GAS), CONDENSE(GAS, LIQUID),
    SUBLIME(SOLID, GAS), DEPOSIT(GAS, SOLID),
    IONIZE(GAS, PLASMA), DEIONIZE(PLASMA, GAS);
  }
  
  ...
}
```

나머지는 Map을 만드는 코드에서 잘 처리해준다.

> Map을 만드는 코드에는 수정할 것이 없으니 실수할 가능성 자체가 차단된다.   
> 실제 내부에서는 맵들의 맵이 배열들의 배열로 구현되니 낭비되는 공간과 시간도 거의 없이 명확하고 안전하고 유지보수하기 좋다.