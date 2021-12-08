# 상속보다는 컴포지션을 사용하라

상속은 코드를 재사용하는 강력한 수단이지만, 항상 최선은 아니다.

상위 클래스와 하위클래스 모두 같은 프로그래머가 통제하는 패키지안에 있다면 안전한 방법이다.   
또 확장할 목적으로 설계되었고 문서화도 잘 된 클래스도 마찬가지로 안전하다.

일반적인 구체 클래스를 패키지 경계를 넘어, 다른 패키지의 구체 클래스를 상속하는 일은 위험하다.

> 여기서 말하는 '상속'은 클래스가 다른 클래스를 확장하는 구현 상속을 말한다.   
> 인터페이스 상속(클래스가 인터페이스를 구현, 인터페이스가 다른 인터페이스를 확장)과는 무관하다.

### 상속은 캡슐화를 깨뜨린다.

메서드 호출과 달리 상속은 캡슐화를 꺠뜨린다.   
다르게 말하면 상위 클래스가 어떻게 구현되느냐에 따라 하위 클래스의 동작에 이상이 생길 수 있다.

상위 클래스에서의 변경으로 인해 가만히 있는 하위 클래스에 문제가 생길 수 있음을 뜻한다.   
이런 이유로 상위 클래스 설계자가 확장을 충분히 고려하고 문서화도 제대로 해두지 않으면 하위 클래스는 상위 클래스의 변화에 맞춰 변경해줘야 한다.

예를 들어 HashSet을 사용하는 프로그램이 있는데 성능을 높이기위해 HashSet은 처음 생성된 원소가 몇개 더해졌는지 알 수 있어야한다.   
따라서 추가된 원소의 수를 저장하는 변수와 접근자 메서드를 추가했다. 그런 다음 HashSet에 원소를 추가하는 메서드인 add와 addAll을 재정의했다.

```java
public class InstrumentedHashSet<E> extends HashSet<E> {
    // 추가된 원소의 수
    private int addCount = 0;
    
    public InstrumentedHashSet() {
    }
    
    // initCap : 해시맵의 초기 용량
    // loadFactor : 맵의 용량을 언제 늘릴지에 대한 척도
    public InstrumentedHashSet(int initCap, float loadFactor) {
        super(initCap, loadFactor);
    }

    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }

    public int getAddCount() {
        return this.addCount;
    }
}
```

이 클래스는 원하는대로 동작하지 않는다.

addAll 메서드를 실제로 호출해보면 원하는 값의 2배가 되어있다.

```java
InstrumentedHashSet<String> set = new InstrumentedHashSet<>();
set.addAll(Arrays.asList("틱", "틱틱", "틱틱틱"));

System.out.printf("set의 addCount 값은 :  %d", set.getAddCount());

// 결과 -> set의 addCount 값은 :  6
```

원인은 HashSet 내부에서 addAll 메서드가 add 메서드를 호출하는데 있다. 이런 내부 구현 방식은 HashSet 문서에는 나와있지 않다.

즉, 흐름은 다음과 같다.
1. InstrumentedHashSet의 addAll 메서드를 호출 ```Arrays.asList("틱", "틱틱", "틱틱틱")``` 요소를 추가
2. addCount에 3을 더함
3. HashSet의 addAll을 호출
4. 내부에서 각 원소에 대해 InstrumentedHashSet의 add 메서드가 호출됨
5. InstrumentedHashSet의 add 메서드에서 각 원소에 대해 addCount를 1씩 증가시킴
6. HashSet의 add 메서드 호출

위 과정을 통해 각 원소마다 addCount가 2씩 증가하는 것을 볼 수 있다.

이런 경우 하위 클래스(InstrumentedSet)에서 addAll 메서드를 재정의하지 않으면 문제를 고칠 수 있다.   
이런 해법은 당장은 제대로 동작하지만 HashSet의 addAll이 내부에서 add를 호출한다는 가정하에 구현하는 것이다.   
이런 가정이 다음 버전에도 유지될 지 수정될 지 알 수 없으므로 이에 기대어 구현한 InstrumentedSet도 깨지기 쉽다.

addAll을 다른 식으로 재정의할 수도 있다. (HashSet의 addAll을 호출하는 방식 말고)   
조금 더 나은 해법이지만 다음의 문제가 있다.
* 상위 클래스의 메서드 동작을 다시 구현하는것은 어려움
* 시간이 많이 소비됨
* 자칫 오류를 내거나 성능을 떨어뜨릴 수도 있다.
* 하위 클래스에서는 접근할 수 없는 상위 클래스의 private 필드를 써야 하는 상황이라면 이 방식으로는 구현 자체가 불가능하다.

하위 클래스는 상위 클래스에서 요소 추가에 대한 새로운 제약사항이 생겨버리면 
하위 클래스에서 요소를 추가할 때 상위 클래스에서는 혀용하지 않는 원소 추가가 가능하게 될 수 있다.

> 실제로도 컬렉션 프레임워크 이전부터 존재하던 Hashtable과 Vector를 컬렉션 프레임워크에 포함시키자 이와 관련한 보안 구멍들을 수정해야 하는 사태가 벌어졌다.

메서드를 재정의 하는 대신 새로운 메서드를 추가하면 괜찮으리라 생각 할 수 있다.   
훨씬 안전한 방식은 맞지만 위험이 존재한다.
상위 클래스에 새 메서드가 추가됐는데, 운 없게도 하필 하위 클래스에 추가한 메서드와 시그니처가 같고 반환타입은 다르다면 컴파일 에러가 발생한다.   
또는 반환타입마저 같다면 재정의 메서드가 되어버린다. 추가로 상위 클래스의 규약을 만족하지 못할 가능성이 크다.

### 상속 대신 컴포지션으로

기존 클래스를 확장하는 대신 새로운 클래스를 만들고 private 필드로 기존 클래스의 인스턴스를 참조하게 하는 방법이 있다.

> 기존 클래스가 새로운 클래스의 구성요소로 쓰인다는 뜻에서 이러한 설계를 컴포지션(composition)이라 한다.

새 클래스의 인스턴스 메서드들은 (private 필드로 참조하는) 기존 클래스의 대응하는 메서드를 호출해 그 결과를 반환한다.   
이 방식을 전달 메서드(forwarding method)라 부른다.

이 방식의 장점으로 기존 클래스의 내부 구현 방식의 영향에서 벗어나며, 기존 클래스에 새로운 메서드가 추가되더라도 전혀 영향을 받지 않는다.

```java
// 래퍼 클래스
public class InstrumentedHashSet2<E> extends ForwardingSet<E> {
    
    private int addCount = 0;

    public InstrumentedHashSet2(Set<E> s) {
        super(s);
    }
    
    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }
    
    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }

    public int getAddCount() {
        return this.addCount;
    }
}
```

```java
// 재사용 할 수 있는 전달 클래스
public class ForwardingSet<E> implements Set<E> {
    private final Set<E> s;

    public ForwardingSet(Set<E> s) {
        this.s = s;
    }

    @Override
    public int size() {
        return s.size();
    }

    @Override
    public boolean isEmpty() {
        return s.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return s.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return s.iterator();
    }

    @Override
    public Object[] toArray() {
        return s.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return s.toArray(a);
    }

    @Override
    public boolean add(E e) {
        return s.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return s.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return s.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return s.addAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return s.retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return s.removeAll(c);
    }

    @Override
    public void clear() {
        s.clear();
    }
}
```

상속 방식은 구체 클래스 각각을 따로 확장해야 하며, 지원하고 싶은 상위 클래스의 생성자 각각에 대응하는 생성자를 별도로 정의해야 한다.

컴포지션 방식은 한 번만 구현해두면 어떤 Set 구현체라도 계측할 수 있으며, 기존 생성자들과도 함께 사용할 수 있다.

다른 Set 인스턴스를 감싸고(wrap) 있다는 뜻에서 InstrumentedSet 같은 클래스를 래퍼 클래스라 하며, 
다른 Set에 계측 기능을 덧씌운다는 뜻에서 데코레이터 패턴이라고 한다.

> 컴포지션과 전달의 조합은 넓은 의미로 위임(delegation)이라고 부른다.

단, 엄밀히 따지면 래퍼 객체가 내부 객체에 자기 자신의 참조를 넘기는 경우만 위임에 해당한다.

### 래퍼 클래스의 단점

래퍼 클래스의 단점은 거의 없지만 한 가지, 해퍼 클래스가 콜백 프레임 워크와 어울리지 않는다는 점만 주의하면 된다.

콜백 프레임워크에서는 자기 자신의 참조를 다른 객체에 넘겨서 다음 호출(콜백) 때 사용하도록 한다.

내부 객체는 자신을 감싸고 있는 래퍼의 존재를 모르니 대신 자신(this)의 참조를 넘기고, 콜백 때는 래퍼가 아닌 내부 객체를 호출하게 된다.   
이를 SELF 문제라고 한다.

상속은 반드시 하위 클래스가 상위 클래스의 **'진짜' 하위 타입인 상황**에서만 쓰여야 한다.

즉, 클래스 B가 클래스 A와 is-a 관계일 때만 클래스 A를 상속해야 한다.

클래스 A를 상속하는 클래스 B를 작성하려 한다면 "B가 정말 A인가?" 라고 질문해봐야한다.   
그렇다 라고 확신할 수 없다면 B는 A를 상속해서는 안된다. 

> 대답이 "아니다" 라면 private 인스턴스로 두고, A와는 다른 API를 제공해야 하는 상황이 대다수다.   
> 즉, A는 B의 필수 구성요소가 아니라 구현 하는 방법 중 하나일 뿐이다.

컴포지션 대신 상속을 사용하기로 결정하기 전에 마지막으로 자문해야할 질문
* 확장하려는 클래스의 API에 아무런 결함이 없는가?
* 결함이 있다면, 이 결함이 여러분 클래스의 API까지 전파돼도 괜찮은가?
  * 컴포지션으로는 이런 결함을 숨기는 새로운 API를 설계할 수 있지만, 상속은 상위 클래스의 API를 '그 결함까지도' 그대로 상속받는다.

### 핵심 정리

> * 상속은 강력하지만 캡슐화를 해친다는 문제가 있다.   
> * 상속은 상위 클래스와 하위 클래스가 순수한 is-a 관계일 때만 써야한다.   
> * is-a 관계일 때도 안심할 수많은 없다. 하위 클래스의 패키지가 상위 클래스와 다르고, 상위 클래스가 확장을 고려해 설계되지 않았다면 여전히 문제가 생길 수 있다.
> * 상속의 취약점을 피하려면 상속 대신 컴포지션과 전달을 사용하자.
> * 래퍼 클래스로 구현할 적당한 인터페이스가 있다면 더욱 그렇다. (예시로 구현한 ForwardingSet)
> * 래퍼 클래스는 하위 클래스보다 견고하고 강력하다. 