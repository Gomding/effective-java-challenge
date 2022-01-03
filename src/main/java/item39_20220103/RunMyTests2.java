package item39_20220103;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RunMyTests2 {
    public static void main(String[] args) throws Exception {
        runTest(Sample2.class.getName());
    }

    private static void runTest(String className) throws ClassNotFoundException {
        int tests = 0;
        int passed = 0;
        Class<?> testClass = Class.forName(className);
        for (Method m : testClass.getDeclaredMethods()) {
            if (m.isAnnotationPresent(ExceptionMyTest.class)) {
                tests++;
                try {
                    m.invoke(null);
                    System.out.printf("테스트 %s 실패: 예외를 던지지 않음%n", m);
                } catch (InvocationTargetException wrappedExc) {
                    Throwable exc = wrappedExc.getCause();
                    Class<? extends Throwable> excType = m.getAnnotation(ExceptionMyTest.class).value();
                    if (excType.isInstance(exc)) {
                        passed++;
                        System.out.println("테스트 통과: " + m);
                    } else {
                        System.out.printf("테스트 %s 실패: 기대한 예외 %s, 발생한 예외 %s%n", m, excType.getName(), exc);
                    }
                } catch (Exception exc) {
                    System.out.println("잘못 사용한 @MyExceptionTest: " + m);
                }
            }
        }
        System.out.printf("성공: %d, 실패: %d%n", passed, tests - passed);
    }
}
