package io.github.masachi.utils.kafka;

import io.github.masachi.condition.KafkaCondition;
import io.github.masachi.utils.BaseUtil;
import io.github.masachi.utils.RetryUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.*;

@Component
@Log4j2
@Deprecated
@Conditional(KafkaCondition.class)
public class KafkaFactory {

    @Autowired
    private KafkaInitializer kafkaInitializer;

    @Autowired
    private KafkaAdmin kafkaAdmin;

    private static HashMap<String, KafkaConsumer> consumerMap = new HashMap<>();

    public synchronized Boolean subscribeCheck(String topicName) {

        String cacheKey = topicName;

        KafkaConsumer consumer = consumerMap.get(cacheKey);

        Assert.isTrue(BaseUtil.isEmpty(consumer), "--->【" + cacheKey + "】已经订阅对应的Topic，无法重复订阅。请整合代码，在唯一地方统一订阅。");


        if(!kafkaInitializer.checkTopicExist(topicName)) {
            kafkaInitializer.initTopic(topicName);
        }

        return true;
    }

    public KafkaConsumer getConsumer(String topicName) {
        if(BaseUtil.isEmpty(topicName)) {
            return null;
        }

        if(BaseUtil.isNotEmpty(consumerMap.get(topicName))) {
            return consumerMap.get(topicName);
        }

        return new KafkaConsumer<>(kafkaAdmin.getConfigurationProperties());
    }
}
