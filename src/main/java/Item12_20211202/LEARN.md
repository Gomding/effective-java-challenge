# toString을 항상 재정의하라

Object의 기본 toString 메서드가 우리가 작성한 클래스에 적합한 문자열을 반환하는 경우는 거의 없다.

Object의 toString 메서드는 ```클래스이름_@16진수로_표시한_해시코드```를 반환할 뿐이다.

toString의 일반 규약에 따르면 **'간결하면서 사람이 읽기 쉬운 형태의 유익한 정보'** 를 반환해야한다.

PhoneNumber 클래스를 예로들면
* PhoneNumber@12349871284
* 010-0000-0000

위 두 가지의 문자열 중 어떤것이 PhoneNumber의 '간결하면서 사람이 읽기 쉬운 형태의 유익한 정보'를 주는지는 말하자면 '010-0000-0000'일것이다.

> 또한 toString의 규약은 "모든 하위 클래스에서 이 메서드를 재정의하는 것이 좋다"고 한다.

```java
// Object의 toString 메서드의 주석에 적힌 내용 
It is recommended that all subclasses override this method.

// Object의 toString메서드 전체 내용
/*
Returns a string representation of the object. In general, 
the toString method returns a string that "textually represents" this object. 
The result should be a concise but informative representation that is easy for a person to read. 
It is recommended that all subclasses override this method.
The toString method for class Object returns a string consisting of the name of the class of which the object is an instance, 
the at-sign character `@', and the unsigned hexadecimal representation of the hash code of the object. 
In other words, this method returns a string equal to the value of:

getClass().getName() + '@' + Integer.toHexString(hashCode())
       
Returns:
a string representation of the object.
 */
public String toString() { 
    return getClass().getName() + "@" + Integer.toHexString(hashCode());
}
```

toString 메서드가 equals 와 hashCode 규약만큼 대단히 중요하진 않지만,   

> toString을 잘 구현한 클래스는 사용하기에 훨씬 즐겁고, 그 클래스를 사용한 시스템은 디버깅 하기 쉽다.

print, println, printf, 문자열 연결 연산자(+), assert 구문에 넘길 때, 혹은 디버거가 객체를 출력할 때 toString 메서드가 자동으로 호출된다.   
우리가 직접 사용하지 않더라도 어디선가 쓰일 가능성이 있다는 의미다.   
우리가 작성한 객체를 참조하는 컴포넌트가 오류 메세지를 로깅할 때 자동으로 호출할 수 있다.   
toString을 재정의하지 않으면 쓸모없는 메세지만 로그에 남을것이다.

만약 PhoneNumber용 toString을 재정의했다면 다음 코드만으로 문제를 진단하기 충분한 메세지를 남길 수 있을것이다.

> System.out.println(phoneNumber + "에 연결할 수 없습니다.");

### 좋은 toString

좋은 toString은 (특히 컬렉션처럼) 이 인스턴스를 포함하는 객체에서 유용하게 쓰인다.   
map 객체를 출력했을 때 {Jenny=PhoneNumber@1233214u3242} 보다는 {Jenny=010-0000-0000}라는 메세지가 나오는 게 훨씬 낫지 않은가?

> toString은 그 객체가 가진 주요 정보를 모두 반환하는 게 좋다. 앞서의 전화번호 처럼   
> 이상적으로는 스스로를 완벽히 설명하는 문자열이어야 한다.

### toString의 문서화

toString을 구현할 때면 반환값의 포맷을 문서화할지 정해야한다. 이는 아주 중요한 선택이다.   
전화번호나 행렬같은 값 클래스라면 문서화하기를 권한다.

포맷을 명시하면 그 객체는 표준적이고, 명확하고, 사람이 읽을 수 있게 된다. 따라서 그 값 그대로 입출력에 사용하거나 CSV 파일처럼 사람이 읽을 수 있는 데이터 객체로 저장할 수도 있다.

포맷을 명시하기로 했다면, 명시한 포맷에 맞는 문자열과 객체를 상호 전환할 수 있는 정적 팩터리나 생성자를 함께 제공해주면 좋다. 자바 플랫폼의 많은 값 클래스가 따르는 방식이기도 하다.   
ex) BigInteger, BigDecimal 등등 대부분의 기본 타입 클래스가 해당됨

단점으로 포맷을 한번 명시하면 평생 그 포맷에 얽매이게 된다.   
이를 사용하는 프로그래머들이 그 포맷에 맞춰 파싱하고, 새로운 객체를 만들고, 영속 데이터로 저장하는 코드를 작성할 것이다.

### 포맷을 명시하려거든 정확하게

포맷을 명시하든 아니든 우리의 의도를 명확히 밝혀야 한다.   
포맷을 명시하려면 아주 정확하게 해야한다.   

포맷의 명시 여부와 상관없이 toString이 반환한 값에 포함된 정보를 얻어올 수 있는 API를 제공하자.   
PhoneNumber 클래스는 지역 코드, 프리픽스, 가입자 번호 용 접근자를 제공해야한다.(Getter 같은)   
그렇지 않으면 이 정보가 피랴요한 프로그래머는 toString의 반환값을 파싱할 수 밖에 없다. 이것은 성능이 나빠지고, 필요하지도 않은 작업이다.

정적 유틸리티 클래스는 toString을 제공할 이유가 없다. 또한, 대부분의 열거타입도 자바가 이미 완벽한 toString을 제공하니 따로 재정의하지 않아도 된다.

하위 클래스들이 공유해야할 문자열 표현이 있는 추상클래스라면 toString을 재정의해줘야 한다.   
예컨대 대다수의 컬렉션 구현체는 추상 컬렉션 클래스들의 toString 메서드를 상속해 쓴다.

> 핵심정리   
> 모든 구체 클래스에서 Object의 toString을 재정의하자. 상위 클래스에서 이미 알맞게 재정의한 경우는 예외다.
> toString을 재정의한 클래스는 사용하기도 즐겁고 그 클래스를 사용한 시스템을 디버깅하기 쉽게 해준다. 
> toString은 해당 객체에 관한 명확하고 유용한 정보를 읽기 좋은 형태로 반환해야한다.
