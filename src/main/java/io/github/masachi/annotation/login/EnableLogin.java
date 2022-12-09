package io.github.masachi.annotation.login;

import io.github.masachi.filter.login.LoginConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启登录验证，使用在Application上
 * <p>
 * 开启后，在需要登录的类或者方法上添加@NeedLogin注解
 * 如需清除某个需要登录类中的某个方法的登录，使用@ClearLogin注解
 */
@Documented
@Target({ElementType.TYPE})
@Import(LoginConfiguration.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableLogin {
}
