package io.github.masachi.condition;

import io.github.masachi.utils.BaseUtil;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.type.AnnotatedTypeMetadata;

@PropertySource({"classpath:core.properties"})
public class WebSocketConditional implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String enableWebSocket = context.getEnvironment().getProperty("spring.websocket.enable");
        if(BaseUtil.isEmpty(enableWebSocket)) {
            return false;
        }
        else {
            return Boolean.parseBoolean(enableWebSocket);
        }
    }
}
