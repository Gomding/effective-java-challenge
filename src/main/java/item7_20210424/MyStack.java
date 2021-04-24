package item7_20210424;

import java.util.Arrays;
import java.util.EmptyStackException;

public class MyStack {

    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public MyStack() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object o) {
        ensureCapacity();
        elements[size++] = o;
    }

    public Object pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        Object result = elements[size--];
        elements[size] = null;
        return result;
    }

    /*
    * 원소를 위한 공간을 적어도 하나 이상 확보한다.
    * 배열 크기를 늘려야 할 때마다 대략 두배씩 늘려준다.
    * */
    private void ensureCapacity() {
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, 2 * size + 1);
        }
    }
}
