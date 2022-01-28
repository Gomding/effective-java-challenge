# 아이템63. 문자열 연결은 느리니 주의하라

문자열 연결 연산자(+)는 여러 문자열을 하나로 합쳐주는 편리한 수단이다.   

한 줄짜리  출력값 혹은 작고 크기가 고정된 객체의 문자열 표현을 만들때라면 괜찮지만, 본격적으로 사용하기 시작하면 성능 저하를 감당하기 어렵다.

문자열 연결 연산자를 사용하면 문자열을 다하기 위해 새로운 String 객체를 할당한다.

```java
public class StringEx {

    public static void main(String[] args) {
        String a = "a";
        System.out.println(a);
        a += "bcd";
        System.out.println(a);
    }
}
```

String을 연결 연산자로 더하면, StringBuilder로 append하는 과정이 생깁니다.      
즉, String -> StringBuilder 변환 -> 다시 String으로 변환
위 과정을 반복하게 되는데. 그렇게되면 쓸모없는 변환과정이 생깁니다.   

문자열 연결 연산자로 문자열 n개를 잇는 시간은 n^2에 비례한다.   
문자열은 불변이라서 두 문자열을 연결할 경우 양쪽의 내용을 모두 복사해야 하므로 성능 저하는 피할 수 없는 결과다.

다음 예시는 청구서의 품목(item)을 전부 하나의 문자열로 연결해준다.

```java
public class ItemStatementEx {
    
    private final List<String> items = new ArrayList<>();
    
    public String statement() {
        String result = "";
        for (String item : items) 
            result += item;
        
        return result;
    }
}
```

품목이 많을 수록 이 메서드는 심각하게 느려질 수 있다.

품목을 하나의 문자열로 만들어야하고, 성능도 포기하고 싶지 않다면 StringBuilder를 사용하자.

```java
public class ItemStatementEx2 {

    private final List<String> items = new ArrayList<>();

    public String statement() {
        StringBuilder result = new StringBuilder();
        for (String item : items)
            result.append(item);
        
        return result.toString();
    }
}
```

두 메서드를 비교하면 성능차이가 6배 정도 난다.(환경에 따라 다를 수 있음)   
자바 6에서는 문자열 연결 연산자의 성능을 어느정도 개선했지만(위에서 설명한 StringBuilder를 활용한 문자열 열결)
여전히 성능 차이는 크다.


