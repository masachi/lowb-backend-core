package io.github.masachi.condition;


import io.github.masachi.utils.BaseUtil;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class MongoDBCondition implements Condition {
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        String dbname = conditionContext.getEnvironment().getProperty("spring.data.mongodb.uri");
        return BaseUtil.isNotEmpty(dbname);
    }
}
