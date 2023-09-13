package io.github.masachi.sentinel.annotation;


import io.github.masachi.sentinel.config.SentinelDegradeConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(SentinelDegradeConfig.class)
public @interface EnableSentinelDegrade {

}
