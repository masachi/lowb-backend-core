package io.github.masachi.sentinel.config;

import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreakerStrategy;
import io.github.masachi.sentinel.annotation.EnableSentinelDegrade;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Configuration
@ConditionalOnBean(annotation = EnableSentinelDegrade.class)
public class SentinelDegradeConfig {

    @PostConstruct
    public void createDegradeRule() {
        List<DegradeRule> rules = new ArrayList<>();
        DegradeRule rule = new DegradeRule("DEFAULT")
                .setGrade(CircuitBreakerStrategy.ERROR_COUNT.getType())
                // Max allowed response time
                .setCount(1)
                // Retry timeout (in second)
                .setTimeWindow(10)
                // Circuit breaker opens when slow request ratio > 60%
                .setSlowRatioThreshold(0.6)
                .setMinRequestAmount(2)
                .setStatIntervalMs(20000);
        rules.add(rule);

        DegradeRuleManager.loadRules(rules);
    }
}
