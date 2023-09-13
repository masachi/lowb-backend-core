package io.github.masachi.annotation;


import io.github.masachi.config.JobRunrConfig;
import io.github.masachi.sentinel.config.SentinelWebConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(JobRunrConfig.class)
@Deprecated
public @interface EnableJobRunner {

}
