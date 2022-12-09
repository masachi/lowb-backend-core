package io.github.masachi.annotation.login;

import java.lang.annotation.*;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface ParseUserInfo {

    /**
     * 只校验APPID和secret
     * 场景：要么就不登录访问，要么就只能是来自内部服务访问，带token的访问一律无效
     *
     * @return 布尔值
     */
    boolean onlyCheckAppId() default false;

}
