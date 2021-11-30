# equals를 재정의하려거든 hashCode도 재정의하라

equals를 재정의한 클래스 모두에서 hashCode도 재정의해야 한다.   
그렇지 않으면 hashCode 일반 규악을 어기게 된다.
해당 클래스의 인스턴스를 HashMap 이나 HashSet같은 컬렉션의 원소로 사용할 때 문제가 발생한 것이다.

* Object 명세에서의 규약
  1. equals 비교에 사용되는 정보가 변경되지 않았다면, 애플리케이션이 실행되는 동안 그 객체의 hashCode 메서드는 몇 번을 호출해도 일관되게 항상 같은 값을 반환해야한다. 단, 애플리케이션을 다시 실행한다면 이 값은 달라져도 상관없다.
  2. equals(Object)가 두 객체를 같다고 판단했다면, 두 객체의 hashCode는 똑같은 값을 반환해야 한다.
    * x.equals(y) => true
    * x.hashCode() == y.hashCode() => true
  3. equals(Object)가 두 객체를 다르다고 판단했더라도, 두 객체의 hashCode가 서로 다른 값을 반환할 필요는 없다. 단, 댜른 객체에 대해서는 다른 값을 반환해야 해시테이블의 성능이 좋아진다.
     * x.equals(y) => false
     * x.hashCode() == y.hashCode() => true 가능 

hashCode 재정의를 잘못했을 때 크게 문제가 되는 조항은 두번째   
> equals(Object)가 두 객체를 같다고 판단했다면, 두 객체의 hashCode는 똑같은 값을 반환해야 한다.

equals는 물리적으로 다른 두 객체를 같다고 할 수 있지만 Object의 기본 hashCode는 이 둘이 전혀 다르다고 판단하여 규약과 달리 서로 다른 값을 반환할 수 있다.

> 절대 사용하면 안될 최악의 hashCode 메서드 재정의   
> @Override    
> public int hashCode() { return 42; }
> 
> 하나의 해시 버킷만 사용하게 되므로 해시 테이블이 O(n)으로 느려짐   
> 마치 LinkedList 처럼 동작   
> 좋은 해시 코드란 서로 다른 인스턴스에 다른 해시코드를 반환 -> 3번째 규약

### 좋은 hashCode를 작성하는 요령
1. int 변수 result를 선언한 후 값 c로 초기화한다. 이 때 c는 해당 객체의 첫 번째 핵심 필드를 단계 2.i 방식으로 계산한 해시코드(여기서 핵심 필드란 equals 비교에 사용되는 필드를 말한다.)
2. 해당 객체의 나머지 핵심 필드 f 각각에 대해 다음 작업을 수행한다.
   1. 해당 필드의 해시코드 c를 계산한다.
      1. 기본 타입 필드라면, Type.hashCode(f)를 수행한다. 여기서 Type은 해당 기본 타입의 박싱 클래스다.
         * int 필드라면 Integer.hashCode(f)를 수행한다.
      2. 참조 타입 필드면서 이 클래스의 equals 메서드가 이 필드의 equals를 재귀적으로 호출해 비교한다면, 이 필드의 hashCode를 재귀적으로 호출한다. 계산이 더 복잡해질 것 같으면, 이 필드의 표준형(canonical representation)을 만들어 그 표준형의 hashCode를 호출한다. 필드의 값이 null이면 0을 사용한다.(다른 상수도 괜찮지만 전통적으로 0을 사용)
      3. 필드가 배열이라면, 핵심 원소 각각을 별도 필드처럼 다룬다. 이상의 규칙을 재귀적으로 적용해 각 핵심 원소의 해시코드를 계산한 다음 단계 2.ii 방식으로 갱신한다. 배열에 핵심 원소가 하나도 없다면 단순히 상수(0을 추천)를 사용한다. 모든 원소가 핵심 원소라면 Arrays.hashCode를 사용한다.
   2. 단계 2.i에서 계산한 해시코드 c로 result를 갱신한다.
      * result = 31 * result + c;
3. result 반환

파생 필드는 해시코드 계싼에서 제외해도 된다. 즉, 다른 필드로부터 계산해낼 수 있는 필드는 모두 무시해도 된다.   
equals에 사용되지 않은 필드는 **반드시** 제외 해야한다. -> 제외하지 않으면 두번째 규약을 어기게됨

클래스가 불변이고 해시코드를 계산하는 비용이 크다면, 매번 새로 계산하기 보다는 캐싱하는 방식도 고려해볼 수 있다.   
이 타입의 객체가 주로 해시의 키로 사용될 것 같다면 인스턴스가 만들어질 때 해시코드를 계산해둬야 한다.   
해시의 키로 사용되지 않는 경우라면 hashCode가 처음 불릴 때 계싼하는 지연 초기화 전략을 사용해볼 수 있다.

```java
private int hashCode;

@Override
public int hashCode() {
    int reuslt = hashCode;
    if (result == 0) {
        result = Short.hashCode(areaCode);
        result = 31 * result + Short.hashCode(prefix);
        result = 31 * result + Short.hashCode(lineNum);
        hashCode = result;
    }
    return result;
}
```

선능을 높이기 위해 해시코드를 계산할 때 필드를 생략해서는 안 된다.   
-> 이는 해시 품질을 떨어뜨려 해시테이블의 성능을 심각하게 떨어뜨릴 수 있다.

hashCode가 반환하는 값의 생성 규칙을 API 사용자에게 자세히 공표하지 말자.   
그래야 클라이언트가 이 값에 의지하지 않게 되고, 추후에 계산 방식을 바꿀 수도 있다.   