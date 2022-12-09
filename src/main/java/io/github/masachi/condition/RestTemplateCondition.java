package io.github.masachi.condition;

import io.github.masachi.utils.BaseUtil;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.type.AnnotatedTypeMetadata;

@PropertySource({"classpath:core.properties"})
public class RestTemplateCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String fetchRegistry = context.getEnvironment().getProperty("eureka.client.fetchRegistry");
        return BaseUtil.isNotEmpty(fetchRegistry) && !"false".equalsIgnoreCase(fetchRegistry);
    }

}
