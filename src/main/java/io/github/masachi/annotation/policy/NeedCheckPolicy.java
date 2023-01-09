package io.github.masachi.annotation.policy;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface NeedCheckPolicy {

    boolean checkPolicy() default true;
}