package item39_20220103;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(ExceptionMyTestContainer.class)
public @interface ExceptionMyTest3 {
    Class<? extends Throwable> value();
}
