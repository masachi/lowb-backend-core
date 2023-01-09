package io.github.masachi.condition;

import io.github.masachi.utils.BaseUtil;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 没有redis的时候初始化caffeine 作为缓存
 */
public class CaffeineCondition implements Condition {
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        String redis = conditionContext.getEnvironment().getProperty("spring.redis.host");
        return BaseUtil.isEmpty(redis);
    }
}
