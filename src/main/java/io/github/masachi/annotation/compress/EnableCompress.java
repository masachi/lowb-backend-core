package io.github.masachi.annotation.compress;


import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Import(CompressFilter.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableCompress {
}
