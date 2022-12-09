package io.github.masachi.condition;

import io.github.masachi.utils.BaseUtil;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class KafkaCondition implements Condition {
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        String kafka = conditionContext.getEnvironment().getProperty("spring.kafka.bootstrap-servers");
        return BaseUtil.isNotEmpty(kafka);
    }
}
