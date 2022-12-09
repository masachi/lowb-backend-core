package io.github.masachi.utils.kafka;

import io.github.masachi.condition.KafkaCondition;
import io.github.masachi.utils.BaseUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ExecutionException;

@Component
@Log4j2
@Conditional(KafkaCondition.class)
public class KafkaInitializer {

    @Autowired
    private KafkaAdmin kafkaAdmin;

    public void initTopic(String topic,Integer partitions, Integer replicas) {
        TopicBuilder.name(topic)
                .partitions(partitions)
                .replicas(replicas)
                .config(TopicConfig.COMPRESSION_TYPE_CONFIG, "ztsd")
                .compact()
                .build();
    }

    public void initTopic(String topic, Integer partitions) {
        initTopic(topic, partitions, 1);
    }

    public void initTopic(String topic) {
        initTopic(topic, 5, 1);
    }

    public Boolean checkTopicExist(String topic) {
        if(BaseUtil.isEmpty(topic)) {
            return true;
        }

        AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties());
        ListTopicsOptions listTopicsOptions = new ListTopicsOptions();
        listTopicsOptions.listInternal(true);

        try {
            Set<String> currentTopics = adminClient.listTopics(listTopicsOptions).names().get();
            return currentTopics.contains(topic);
        }
        catch (InterruptedException | ExecutionException e) {
            log.error("Kafka Check Topic Error:" + e.getMessage());
        }

        return false;
    }
}
