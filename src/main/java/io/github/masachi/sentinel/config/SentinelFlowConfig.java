package io.github.masachi.sentinel.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreakerStrategy;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import io.github.masachi.sentinel.annotation.EnableSentinelDegrade;
import io.github.masachi.sentinel.annotation.EnableSentinelFlow;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Configuration
@ConditionalOnBean(annotation = EnableSentinelFlow.class)
public class SentinelFlowConfig {

    @PostConstruct
    public void createFlowRule() {
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule("DEFAULT")
                .setGrade(RuleConstant.FLOW_GRADE_QPS)
                .setCount(10)
                .setStrategy(RuleConstant.STRATEGY_DIRECT)
                .setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_WARM_UP)
                .setWarmUpPeriodSec(10)
                .setMaxQueueingTimeMs(500);
        rules.add(rule);

        FlowRuleManager.loadRules(rules);
    }
}
