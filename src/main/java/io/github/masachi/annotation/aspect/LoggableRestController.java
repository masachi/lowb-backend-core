package io.github.masachi.annotation.aspect;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

/**
 * @description: 自定义注解, 用于控制层统一处理封装结果, 异常, 日志和告警功能;
 * 同时集成了@RestController注解功能,后续接口开发只需使用@LoggableRestController注解用于Controller上
 **/
//集成@RestController
@RestController
//适用于类
@Target({ElementType.TYPE})
//只在运行时保留
@Retention(RetentionPolicy.RUNTIME)
//被javadoc记录
@Documented
public @interface LoggableRestController {
    @AliasFor(
            annotation = RestController.class
    )
    String value() default "";
}
