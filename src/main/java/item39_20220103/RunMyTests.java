package item39_20220103;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
