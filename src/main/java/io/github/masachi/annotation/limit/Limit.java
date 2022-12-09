package io.github.masachi.annotation.limit;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Limit {

    LimitType type() default LimitType.GLOBAL;

    String keyword() default "";

    int qps() default 100;

    int timeout() default 60;
}
