# 아이템56 공개된 API 요소에는 항상 문서화 주석 작성하라

API를 사용자들이 더 쉽고, 쓸모 있게 사용하려면 잘 작성된 문서를 곁들여야 한다.   
사람이 직접 작성하는 API 문서는 코드가 변경되면 매번 함께 수정해줘야 하는 번거로움이 있다.

> 자바에서는 자바독(Javadoc)이라는 유틸리티가 이 귀찮은 작업을 도와준다.

자바독은 소스코드 파일에서 문서화 주석이라는 특수한 형태로 기술된 설명을 추출해 API 문서로 변환해준다.
아래는 일반적인 사용 예시다.

```java
/**
 * 문서화 주석 대상의 요약 설명이다.
 * 
 * @param
 * @param
 * @return
 * @throws
 */
```

### Javadoc 핵심 정리
본 내용에 앞서 핵심 정리를 모아놓겠다.

* 문서화 주석 작성하는 규칙은 [주석 작성법(How to Write Doc Comments)](https://www.oracle.com/kr/technical-resources/articles/java/javadoc-tool.html)를 참고하자
* API를 올바로 문서화하려면 공개된 모든 클래스, 인터페이스, 메서드, 필드 선언에 문서화 주석 달자
* 기본 생성자는 문서화 주석을 달 방법이 없으니 공개 클래스는 절대 기본 생성자를 사용하면 안된다.
* 상속용 클래스의 메서드는 문서화 주석에 어떻게 동작해야 하는지를 기술한다. 
* 상속용 클래스의 메서드가 아니라면 해당 메서드가 무엇을 하는지 기술한다.
  * 메서드를 호출하기 위한 전제조건, 메서드가 성공적으로 수행된 후 만족해야하는 사후조건을 나열한다.
  * 발생할 수 있는 부작용도 기술하자. 여기서 말하는 부작용이란 사후조건에 명확히 나타나지 않지만 시스템의 상태에 변화를 주는 것이다.
* 일반적으로 메서드의 제약조건은 @throws 태그로 비검사 예외를 선언하여 기술한다.
* 모든 매개변수에 @param 태그
* 반환타입이 void가 아니라면 @return 태그
* 발생할 가능성이 있는 모든 예외에 @throws 태그, @throws 태크의 설명은 '만약'으로 시작해 해당 예외를 던지는 조건을 설명하는 절이 뒤따른다.
* @param, @return, @throws 태그의 설명에는 마침표를 붙이지 않는다.
* 자바독 유틸리티는 문서화 주석 HTML로 변환하므로 문서화 주석 안의 HTML 요소들이 최종 HTML문서에 반영된다.
* {@code} 는 감싼 내용을 코드용 폰트로 변환해주고, 다른 HTML 요소나 다른 자바독 태그를 무시한다.
  * 단 @ 같은 경우는 탈출 문자를 써줘야 한다.
* 문서화 주석에서 "this"는 호출된 메서드가 있는 객체를 가리킨다.
* @implSpec 태그는 해당 메서드와 하위 클래스 사이의 계약을 설명한다.
  * 하위 클래스들이 그 메서드를 상속하거나 super 키워드를 이용해 호출할 때 그 메서드가 어떻게 동작하는지를 명확히 인지하고 사용하도록 하는게 목적이다.
* ```<, >, &```등의 HTML 메타문자를 문서화 주석에 포함하려면 {@literal} 태그로 감싸주자. HTML 마크업이나 자바독 태그를 무시한다.
* 문서화 주석은 코드와 변환된 API 문서 모두 읽기 쉬워야 한다는 게 일반 원칙이다.
  * 양쪽은 만족하지 못하면 API 문서에서의 가독성을 우선시 하자.
* 각 문서화 주석의 첫 번째 문장은 해당 요소의 요약 설명으로 간주된다. 대상의 기능을 설명한다.
  * 대상의 기능을 고유하게 기술하므로 한 클래스안에서 요약 설명이 똑같은 멤버가 둘 이상이면 안 된다.
  * ```<마침표><공백><다음 문자 시작>``` 의 패턴이 있으면 ```<마침표>```까지가 요약 설명이 된다.
  * 요약 설명의 대상이 메서드나 생성자라면 동작을 설명하는 (주어가 없는) 동사구 여야한다.
  * 요약 설명의 대상이 클래스, 인터페이스, 필드의 요약 설명이라면 대상을 설명하는 명사절이어야 한다.
* 자바 9부터는 {@index 색인 이름}을 활용해서 HTML에서 검색해 찾을 수 있다.
* 제네릭, 열거타입, 주석은 문서화 주석에서 특별히 주의해야 한다.
  * 제네릭 타입이나 제네릭 메서드를 문서화할 때는 모든 타입 매개변수에 주석을 달아야 한다.
  * 열거 타입을 문서화할 때는 상수들에도 주석을 달아야 한다.
  * 주석 타입을 문서화할 때는 멤버들에도 모두 주석을 달아야 한다. 주석 타입 자체도 물론이다. 필드 설명은 문사구로 한다.   
    주석 타입의 요약 설명은 이 주석을 사용하는 것이 어떤 의미인지를 설명하는 동사구로 한다.
* 패키지를 설명하는 문서화 주석은 package-info.java 파일에 작성한다. 
* (자바 9부터)모듈 시스템을 사용한다면 모듈 관련 설명은 module-info.java 파일에 작성하면 된다.
* 스레드 안전성과 직성화 가능성을 API 문서화에 포함시키자
* 문서화 주석이 없는 API 요소를 발견하면 자바독이 가장 가까운 문서화 주석을 찾아준다. 
  * 이때 상위 '클래스'보다 그 클래스가 구현한 '인터페이스'를 우선시 한다.
  * {@inherit} 태그를 활용해도 상위 클래스의 문서화 주석을 사용할 수 있지만, 사용법이 까다로워 잘 알고 써야한다.
* 자바독만으로 부족하다면 외부 문서의 링크도 활용하자
* 프로그램의 코드도 리팩터링이 필요하듯이, 자바독 문서도 지속적인 리팩터링이 필요하다.

### 문서화 주석의 규칙

문서화 주석 작성하는 규칙이 언어 명세에 속하진 않지만 **자바 프로그래머라면 알아야 하는 업계 표준 API**라 할 수 있다.

이 규칙은 문서화 [주석 작성법(How to Write Doc Comments)](https://www.oracle.com/kr/technical-resources/articles/java/javadoc-tool.html) 웹 페이지에 기술되어있다.   
이 문서는 자바 4이후로 갱신되지 않은 오래된 페이지지만, 가치는 여전하다.

자바 버전이 올라가며 추가된 중요한 자바독 태그로 @literal, @code, @implSpec, @index가 있다. (위에 링크로 남긴 웹 페이지에는 이에 대한 설명이 없다.)

### 공개된 모든 클래스, 인터페이스, 메서드, 필드 선언에 문서화 주석 달자

API를 올바로 문서화하려면 모든 클래스, 인터페이스, 메서드, 필드 선언에 문서화 주석 달아야 한다.

직렬화 할 수 있는 클래스라면 직렬화 형태에 관해서도 적어야 한다.   
문서화 주석이 없다면 자바독도 그저 공개 API 요소들의 '선언'만 나열해주는 게 전부다.

문서가 잘 갖춰지지 않은 API는 사용하기 헷갈려서 오류의 원인이 되기 쉽다.

기본 생성자에는 문서화 주석을 달 방법이 없으니 공개 클래스는 절대 기본 생성자를 사용하면 안된다.   
(생성자를 따로 선언하지 않으면 기본 생성자를 사용한다.)

```java
class Example {
    // 생성자를 하나도 선언하지 않으면 기본 생성자를 사용한다.
}

class Example {
    // 생성자를 선언했으므로 기본 생성자는 생성되지 않는다.
    public Example() {
        ...
    }
}
```

유지보수까지 고려한다면 대다수의 공개되지 않은 클래스, 인터페이스, 생성자, 메서드, 필드에도 문서화 주석을 달아주자.

### 메서드용 문서화 주석은 메서드와 클라이언트 사이의 규약을 기술하자

상속용으로 설계된 클래스의 메서드는 그 메서드가 어떻게 동작하는지를 기술한다.   
상속용 클래스의 메서드가 아니라면 메서드가 어떤 행동을 하는지 기술해야한다.   
즉 how가 아닌 what을 기술한다.    
문서화 주석에는 클라이언트가 해당 메서드를 호출하기 위한 전제조건을 모두 나열해야 한다.   
또 메서드가 성공적으로 수행된 후에 만족해야 하는 사후조건도 모두 나열해야 한다.

일반적으로 전제조건은 @throws 태그로 비검사 예외를 선언하여 암시적으로 기술한다.   
비검사 예외 하나가 전제조건 하나와 연결되는 것이다.

```java
/**
 * @throws IndexOutOfBoundsException if the index is out of range
 */
```

전제조건과 사후조건뿐만 아니라 부작용도 문서화한다. 부작용이란 사후조건으로 명확히 나타나지는 않지만 시스템의 상태에 어떠한 변화를 가져오는 것을 뜻한다.   
예를들어 백그라운드 스레드를 동작하는 메서드라면 그 사실을 꼭 문서에 기술하자.

메서드의 계약을 완벽히 기술하려면 다음을 모두 기술하자.
* 모든 매개변수에 @param 태그
* 반환타입이 void가 아니라면 @return 태그
* 발생할 가능성이 있는 모든 예외에 @throws 태그
* 예시가 궁금하다면 BigInteger API의 문서를 살펴보자

@throws 태그의 설명은 if로 시작해 해당 예외를 던지는 조건을 설명하는 절이 뒤따른다.

관례상 @param, @return, @throws 태그의 설명에는 마침표를 붙이지 않는다.

다음은 이상의 규칙을 모두 반영한 문서화 주석의 예시다.

```java
/**
 * 이 리스트에서 지정한 위치의 원소를 반환한다.
 *
 * <p>이 메서드는 상수 시간에 수행됨을 보장하지 <i>않는다</i>. 구현에 따라 원소의 위치에 비례해 시간이 걸릴 수도 있다.</p>
 *
 * @param index 반환할 원소의 인덱스; 0 이상이고 리스트 크기보다 작아야 한다
 * @return 이 리스트에서 지정한 위치의 원소
 * @throws IndexOutOfBoundsException index가 범위를 벗어나면, 
 *         즉, ({@code index < 0 || index >= this.size()})이면 발생한다.
 */
E get(int index);
```

문서화 주석에 HTML 태그```<p>와 <i>```를 쓴 점에 주목하자.   
자바독 유틸리티는 문서화 주석을 HTML로 변환하므로 문서화 주석 안의 HTML 요소들이 최종 HTML 문서에 반영된다.   
(드물기는 하지만 HTML table까지 집어넣는 프로그래머도 있다.)

### {@code}

@throws 절에 사용한 {@code}도 살펴보자. 두 가지 효과가 있다.   
* 태그로 감싼 내용을 코드용 폰트로 렌더링한다.
* 태그로 감싼 내용에 포함된 HTML 요소나 다른 자바독 태그를 무시한다.

두 번째 효과 덕분에 HTML 메타문자인 ```<``` 기호를 별다른 처리 없이 바로 사용할 수 있다.   
문서화 주석에 여러 줄로 된 코드 예시를 넣으려면 {@code} 태그를 다시 ```<pre>``` 형태로 쓰면 된다.   
이렇게 하면 HTML의 탈출 메타문자를 쓰지 않아도 코드의 줄바꿈이 그대로 유지된다.
```java
/**
 * 
 * <pre>{@code 예시로
 * 사용하는 코드
 * 줄바꿈이 자유롭다}</pre>
 */
```

단, @기호에는 무조건 탈출문자를 붙여야 하니 문서화 주석 안의 코드에서 주석 사용한다면 주의하자.

위의 자바독 문서화 주석의 원본인 영문 문서화 주석을 살펴보자

```java
/**
 * Returns the element at the specified position in this list.
 * 
 * <p>This method is <i>not</i> guaranteed to run in constant time.
 * In some implementations it may run time proportional to the element position</p>
 * 
 * @param – index of the element to return; must be non-negative and less than the size of this list
 * @return the element at the specified position in this list
 * @throws IndexOutOfBoundsException – if the index is out of range (index < 0 || index >= size())
 */
```

이 문서화 주석에서 쓴 "this list"라는 단어에 주목하자. 관례상, 인스턴스 메서드의 문서화 주석에 쓰인 "this"는 호출된 메서드가 자리하는 객체를 가리킨다.

### @implSpec

클래스를 상속용으로 설계하면 자기사용 패턴에 대해서도 문서에 남기자.   
상속용 클래스의 메서드는 문서화 주석에 자기사용 패턴에 대해서도 문서에 남겨 다른 프로그래머에게 그 메서드를 올바로 재정의하는 방법을 알려줘야 한다.   
자기 사용패턴은 자바8에 추가된 @implSpec 태그로 문서화한다.

@implSpec 태그는 해당 메서드와 하위 클래스 사이의 계약을 설명한다.   
하위 클래스들이 그 메서드를 상속하거나 super 키워드를 이용해 호출할 때 그 메서드가 어떻게 동작하는지를 명확히 인지하고 사용하도록 하는게 목적이다.

```java
/**
 * 이 컬렉션이 비었다면 true를 반환한다.
 * 
 * @implSpec 
 * 이 구현은 {@code this.size() == 0}의 결과를 반환한다.
 * 
 * @return 이 컬렉션이 비었다면 true, 그렇지 않다면 false
 */
```

자바 11까지도 자바독 명령줄에서 ```-tag "implSpec:a:Implementation Requirements:"``` 스위치를 켜주지 않으면 @implSpec 태그를 무시해버린다.


### {@literal}
API 설명에 ```<, >, &```등의 HTML 메타문자를 포함시키려면 특별한 처리를 해줘야 함을 잊지 말자.   
가장 좋은 방법은 {@literal} 태그로 감싸는 것이다. 이 태그는 HTML 마크업이나 자바독 태그를 무시하게 해준다.   
앞서 본 {@code} 태그와 비슷하지만 코드 폰트로 렌더링하지는 않는다.

```java
/**
 * {@literal |r| < 1}이면 기하 수열이 수렴한다.
 */
```

이 주석은 "|r| < 1이면 기하 수열이 수렴한다"로 변환된다. 사실 < 기호만 {@literal}로 감싸도 결과는 똑같지만, 그렇게하면 코드에서의 문서화 주석을 읽기 어려워진다.   
문서화 주석은 코드와 API문서 모두 읽기 쉬워야 한다는게 일반 원칙이다. 둘다 만족할 수 없다면 API 문서쪽의 가독성을 우선시하자.

### 문서화 주석의 첫번째 문장, 요약 설명

각 문서화 주석의 첫 번째 문장은 해당 요소의 요약 설명으로 간주된다. 요약 설명은 반드시 대상의 기능을 고유하게 기술해야 한다.   
헷갈리지 않으려면 한 클래스(혹은 인터페이스)안에서 요약 설명이 똑같은 멤버(혹은 생성자)가 둘 이상이면 안 된다.

다중정의된 메서드가 있다면 특히 더 조심하자. 다중 정의된 메서드들의 설명은 같은 문장으로 시작하는 게 자연스럽겠지만 문서화 주석에서는 허용되지 않는다.

요약 설명에는 마침표('.')에 주의해야 한다.   
예를들어 문서화 주석의 첫 문장이 "머스터드 대령이나 Mrs. 피콕 같은 용의자."라면 첫 번째 마침표가 나오는 "머스터드 대령이나 Mrs."까지만 요약 설명이 된다.

```java
/**
 * 머스터드 대령이나 Mrs. 피콕 같은 용의자.
 * 
 * 최종 HTML에는 머스터드 대령이나 Mrs. 까지만 나온다.
 */
```

> 요약 설명이 끝나는 판단 기준은 다음과 같다.   
> 처음 발견되는 <마침표><공백><다음 문장 시작> 의 <마침표>가 요약 설명의 끝이 된다.   
> <공백>은 스페이스, 탭, 줄바꿈이다.
> <다음 문장 시작>은 소문자가 아닌 문자다.

만약 의도치 않게 마침표로 인해 요약 설명이 끝난다면 {@literal}로 감싸주는 것이다.   
위의 예시를 개선하면 다음과 같다.
```java
/**
 * 머스터드 대령이나 {@literal Mrs. 피콕} 같은 용의자.
 */
```

주석 작성 규약에 따르면 요약 설명은 완전한 문장이 되는 경우가 드물다.   
요약 설명의 대상이 메서드나 클래스라면 동작을 설명하는 (주어가 없는)동사구 여야 한다.   
다음 예시를 보면서 느껴보자
```java
ArrayList(int initialCapacity) : 지정한 초기 용량을 갖는 빈 리스트를 생성한다.
Collection.size(): 이 컬렉션 안의 원소 개수를 반환한다.
```

만약 클래스, 인터페이스, 필드의 요약 설명이라면 대상을 설명하는 명사절이어야 한다.

```java
Instant: 타임라인상의 특정 순간(지점)
Math.PI: 원주율(pi)에 가장 가까운 double 값
```

### 자바9부터 추가된 자바독이 생성한 HTML 문서에 검색 기능

자바 9부터 추가된 {@index} 태그를 사용하면 자바독이 생성한 HTML에서 검색이 가능하다.

```java
/**
 * This method compiles with the {@index IEEE 754} standard
 */
```

### 제네릭, 열거타입, 주석은 문서화 주석에서 특별히 주의해야 한다.

제네릭 타입이나 제네릭 메서드를 문서화할 때는 모든 타입 매개변수에 주석을 달아야 한다.

```java
/**
 * 키와 값을 매핑하는 객체, 맵은 키를 중복해서 가질 수 없다
 * 즉, 키 하나가 가리킬 수 있는 값은 최대 1개다.
 * 
 * @param <K> 이 맵이 관리하는 키의 타입
 * @param <V> 매핑된 값의 타입
 */
public interface Map<K, V> { ... }
```

열거 타입을 문서화할 때는 상수들에도 주석을 달아야 한다.

열거 타입 자체와 그 열거 타입의 public 메서드도 물론이다.

```java
/**
 * 심포니 오케스트라의 악기 세션
 */
public enum OrchestraSection {
    /** 플루트, 클라리넷, 오보 같은 목관악기. */
    WOODWIND,
  
    /** 프렌치 호른, 트럼펫 같은 금관악기 */
    BRASS,
  
    /** 탐파니, 심벌즈 같은 타악기 */
    PERCUSSION,
  
    /** 바이올린, 첼로 같은 현악기 */
    STRING
}
```

애너테이션 타입을 문서화할 때는 멤버들에도 모두 주석을 달아야 한다.   
애너테이션 타입 자체도 물론이다. 필드 설명은 명사구로 한다.   
애너테이션 타입의 요약 설명은 이 애너테이션을 사용하는 것이 어떤 의미인지를 설명하는 동사구로 한다.

```java
/**
 * 이 주석이 달린 메서드는 명시한 예외를 던져야만 성공하는
 * 테스트 메서드임을 나타낸다.
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionTest {
    /**
     * 이 주석을 단 테스트 메서드가 성공하려면 던져야 하는 예외
     * (이 클래스의 하위 타입 예외는 모두 허용된다.)
     */
    Class<? extends Throwable> value();
}
```

### 패키지 문서화와 모듈 문서화

패키지를 설명하는 문서화 주석은 package-info.java 파일에 작성한다.   
이 파일은 패키지 선언을 반드시 포함해야 하며 패키지 선언 관련 주석을 추가로 포함할 수도 있다.

자바 9부터 지원하는 모듈 시스템도 이와 비슷하다.   
모듈 시스템을 사용한다면 모듈 관련 설명은 module-info.java 파일에 작성하면 된다.

### 스레드 안전성과 직렬화 가능성을 API 문서화에 포함시키자

클래스 혹은 정적 메서드가 스레드 안전하든 그렇지 않든, 스레드 안전 수준을 반드시 API 설명에 포함해야 한다.(아이템 82)   
직렬화 가능한 클래스라면 직렬화 형태도 API 설명에 기술해야 한다. (아이템 87)

### 자바독 상속
자바독은 메서드 주석을 '상속'시킬 수 있다.   
문서화 주석이 없는 API 요소를 발견하면 자바독이 가장 가까운 문서화 주석을 찾아준다. 이때 상위 '클래스'보다 그 클래스가 구현한 '인터페이스'를 우선시 한다.   

상세한 검색 알고리즘은 (The Javadoc Reference Guide)를 참고하자.   
또한 {@inheritDoc} 태그를 사용해 상위 타입의 문서화 주석 일부를 상속할 수 있다.   
클래스는 자신이 구현한 인터페이스의 문서화 주석을 (복사하지 않고)재사용할 수 있다는 뜻이다.   

> {@inheritDoc} 태그를 사용하면 똑같은 문서화 주석 여러 개를 유지보수하는 부담을 줄일 수 있지만,   
> 사용하기 까다롭고 제약도 조금 있다. 이를 참고하자 (http://bit.ly/2vqmCzj)

### 자바독만으로 부족하다면 외부 문서의 링크도 활용하자

복잡한 API라면 주석회에도 전체 아키텍쳐를 설명한다던지 내용이 많은 경우가 있다.   
이런 설명이 필요하다면 문서의 링크를 활용하자

> 잘 쓰인 문서인지 확인하기 위해서는 Javadoc 유틸리티가 생성한 웹페이지를 읽어보자

다른 사람이 사용할 API라면 반드시 모든 API요소를 검토하자. 프로그램을 테스트하면 어김없이 수정할 코드가 나오듯이,   
생성된 API 문서를 읽어보면 고쳐 써야 할 주석이 눈에 들어오게 마련이다.