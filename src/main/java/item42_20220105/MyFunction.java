package item42_20220105;

@FunctionalInterface
public interface MyFunction<T, R> {
    R apply(T t);
}
