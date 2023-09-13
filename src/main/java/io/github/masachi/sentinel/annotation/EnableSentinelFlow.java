package io.github.masachi.sentinel.annotation;


import io.github.masachi.sentinel.config.SentinelDegradeConfig;
import io.github.masachi.sentinel.config.SentinelFlowConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(SentinelFlowConfig.class)
public @interface EnableSentinelFlow {

}
