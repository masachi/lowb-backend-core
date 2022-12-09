package io.github.masachi.condition;

import io.github.masachi.utils.BaseUtil;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class ElasticsearchCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String elasticSearch = context.getEnvironment().getProperty("elasticsearch.host");
        return BaseUtil.isNotEmpty(elasticSearch);
    }
}
