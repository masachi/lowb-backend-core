package io.github.masachi.annotation.login;

import java.lang.annotation.*;

/**
 * 要使该注解生效，需要在Application上加@EnableLogin
 * 如需清除某个需要排除一个被注解的类的方法，使用@ClearLogin
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface NeedLogin {

    boolean checkPolicy() default false;
}
