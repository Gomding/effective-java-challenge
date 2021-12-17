package item31_20211217;

import java.util.*;

public class MyStack<E> {
    public static void main(String[] args) {
        // pushAll 메서드 사용 예제
        MyStack<Number> numberMyStack = new MyStack<>();
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4);
        numberMyStack.pushAll(numbers);

        // popAll 메서드 사용 예제
        MyStack<Number> numberMyStack2 = new MyStack<>();
        numberMyStack2.pushAll(numbers);
        List<Object> objects = new ArrayList<>();
        numberMyStack2.popAll(objects);
    }

    private E[] elements;
    private int size = 0;
    public static final int DEFAULT_INITIAL_CAPACITY = 16;

    @SuppressWarnings("unchecked")
    public MyStack() {
        elements = (E[]) new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(E e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public E pop() {
        if (size == 0)
            throw new EmptyStackException();
        E result = elements[--size];
        elements[size] = null;
        return result;
    }

    public void pushAll(Iterable<? extends E> src) {
        for (E e : src)
            push(e);
    }

    public void popAll(Collection<? super E> dst) {
        while(!isEmpty())
            dst.add(pop());
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void ensureCapacity() {
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, 2 * size + 1);
        }
    }
}
