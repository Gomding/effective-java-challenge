# 아이템66. 네이티브 메서드는 신중히 사용하라

자바 네이티브 인터페이스(JNI, Java Native Interface)는 자바 프로그램이 네이티브 메서드를 호출하는 기술이다.

> 네이티브 메서드란?   
> C나 C++ 같은 네이티브 프로그래밍 언어로 작성한 메서드를 말한다.

네이티브 메서드의 주요 쓰임 세 가지
* 레지스트리 같은 플랫폼 특화 기능을 사용
* 네이티브 코드로 작성된 기존 라이브러리를 사용
  * 예를들면, 레거시 데이터를 사용하는 레거시 라이브러리
* 성능 개선을 목적으로 성능에 결정적인 영향을 주는 영역만 따로 네이티브 언어로 작성

### 줄어드는 자바의 네이티브 메서드

플랫폼 특화 기능을 활용하려면 네이티브 메서드를 사용해야 한다.   
하지만 자바가 성숙해가면서 하부 플랫폼(OS 같은)의 기능들을 점차 흡수하고 있다.
그래서 네이티브 메서드를 사용할 필요가 계속 줄어들고 있다.   

자바 9는 process API를 추가해서 OS 프로세스에 접근하는 길을 열어주었다.   
대체할 만한 자바 라이브러리가 없는 네이티브 라이브러리를 사용해야 할 때도 네이티브 메서드를 써야 한다.

### 오로지 성능 개선을 목적으로한 네이티브 메서드는 권장하지 않는다.

자바 초기 시절(자바 3 전)이라면 이야기가 다르지만, JVM은 그동안 엄청난 속도로 발전했다.   
대부분 작업에서 지금의 자바는 다른 플랫폼에 견줄만한 성능을 보인다.

java.math가 처음 추가된 자바 1.1 시절 BigInteger는 C로 작성한 고성능 라이브러리에 의지했다.   
그러다 자바 3 때 순수 자바로 다시 구현되면서 세심히 튜닝한 결과, 원래의 네이티브 구현보다도 빨라졌다.

네이티브 라이브러리 쪽은 GNU 다중 정밀 연산 라이브러리(GMP)를 필두로 개선 작업이 계속돼왔다.   
정말로 고성능의 다중 정밀 연산이 필요한 자바 프로그래머라면 이제 네이티브 메서드를 통해 GMP를 사용하는 걸 고려해도 좋다.

### 네이티브 메서드의 단점

네이티브 메서드에는 심각한 단점이 있다.

네이티브 언어가 안전하지 않으므로 네이티브 메서드를 사용하는 애플리케이션도 메모리 훼손 오류로부터 더 이상 안전하지 않다.
* 네이티브 언어는 자바보다 플랫폼을 많이 타서 이식성이 낮다. (특정 플랫폼에서 빌드 후 다른 플랫폼에서 실행 한다면? 정상 동작하지 않을 것이다.)
* 디버깅이 어렵다.
* 주의하지 않으면 속도가 더 떨어진다.
* 가비지 컬렉터가 네이티브 메모리는 자동 회수하지 못하고, 추적조차 불가능하다.
* 자바 코드와 네이티브 코드의 경계를 넘나들 때마다 비용이 추가된다.
* 네이티브 메서드와 자바 코드 사이에 '접착 코드(glue code)'를 작성해야 하는데, 귀찮은 작업이고 가독성도 떨어진다.

### 핵심정리

네이티브 메서드를 사용하려거든 한번 더 생각하자.

네이티브 메서드가 성능을 개선해주는 일은 많지 않다.   
저수준 자원이나 네이티브 라이브러리를 사용해야만 해서 어쩔 수 없더라도 네이티브 코드는 최소한만 사용하고 철저히 테스트하자.   
네이티브 코드안에 숨은 단 하나의 버그가 여러분 애플리에키션 전체를 훼손할 수도 있다.
