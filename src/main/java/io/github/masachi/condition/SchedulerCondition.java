package io.github.masachi.condition;

import io.github.masachi.utils.BaseUtil;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.type.AnnotatedTypeMetadata;

@PropertySource({"classpath:core.properties"})
public class SchedulerCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String enableScheduling = context.getEnvironment().getProperty("spring.scheduler.enable");
        if(BaseUtil.isEmpty(enableScheduling)) {
            return false;
        }
        else {
            return Boolean.parseBoolean(enableScheduling);
        }
    }
}
