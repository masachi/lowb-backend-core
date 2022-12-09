package io.github.masachi.config;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger2的配置类
 */
@Configuration
public class Swagger2Config {

    /**
     * @return Docket
     */
    @Bean
    public GroupedOpenApi createRestApi() {

        return GroupedOpenApi.builder()
                .group("")
                .displayName("Spring Template")
                .packagesToScan("io.github.masachi")
                .pathsToMatch("/*")
                .build();
    }
}

