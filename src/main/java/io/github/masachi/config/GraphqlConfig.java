package io.github.masachi.config;

import graphql.scalars.ExtendedScalars;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

//@Configuration
@Deprecated
public class GraphqlConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> {
            wiringBuilder.scalar(ExtendedScalars.DateTime);
            wiringBuilder.scalar(ExtendedScalars.Json);
        };
    }
}
