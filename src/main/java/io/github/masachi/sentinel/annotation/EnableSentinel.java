package io.github.masachi.sentinel.annotation;


import io.github.masachi.sentinel.config.SentinelWebConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(SentinelWebConfig.class)
public @interface EnableSentinel {

}
