package io.github.masachi.condition;

import io.github.masachi.utils.BaseUtil;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.type.AnnotatedTypeMetadata;

@PropertySource({"classpath:core.properties"})
public class MinIOCondition implements Condition {
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        String minio = conditionContext.getEnvironment().getProperty("minio.endpoint");
        return BaseUtil.isNotEmpty(minio);
    }
}
