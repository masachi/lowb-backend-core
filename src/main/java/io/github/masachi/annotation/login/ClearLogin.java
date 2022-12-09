package io.github.masachi.annotation.login;

import java.lang.annotation.*;

/**
 * 配合@NeedLogin使用
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface ClearLogin {

}
