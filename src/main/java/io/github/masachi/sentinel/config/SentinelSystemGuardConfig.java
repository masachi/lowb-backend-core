package io.github.masachi.sentinel.config;

import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreakerStrategy;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import io.github.masachi.sentinel.annotation.EnableSentinelDegrade;
import io.github.masachi.sentinel.annotation.EnableSentinelSystemGuard;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
@ConditionalOnBean(annotation = EnableSentinelSystemGuard.class)
public class SentinelSystemGuardConfig {

    @PostConstruct
    public void createSystemRule() {
        SystemRule rule = new SystemRule();
        // max load is 3
        rule.setHighestSystemLoad(3);
        // max cpu usage is 60%
        rule.setHighestCpuUsage(0.6);
        // max avg rt of all request is 10 ms
        rule.setAvgRt(10);
        // max total qps is 20
        rule.setQps(50);
        // max parallel working thread is 10
        rule.setMaxThread(10);

        SystemRuleManager.loadRules(Collections.singletonList(rule));
    }
}
