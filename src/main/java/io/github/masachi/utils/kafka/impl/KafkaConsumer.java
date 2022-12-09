package io.github.masachi.utils.kafka.impl;

import io.github.masachi.condition.KafkaCondition;
import io.github.masachi.utils.BaseUtil;
import io.github.masachi.utils.kafka.IConsumer;
import io.github.masachi.utils.kafka.IKafkaConsumer;
import io.github.masachi.utils.kafka.KafkaFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Deprecated
@Conditional(KafkaCondition.class)
class KafkaConsumer implements IKafkaConsumer {

    @Autowired
    private KafkaFactory kafkaFactory;

    @KafkaListener(topics = "${kafka.topic}")
    public void processMessage(String message,
                               @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                               @Header(KafkaHeaders.RECEIVED_TOPIC) List<String> topics,
                               @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        System.out.printf("%s-%d[%d] \"%s\"\n", topics.get(0), partitions.get(0), offsets.get(0), message);
    }

    @Override
    public void subscribe(String topic, IConsumer iConsumer) throws RuntimeException {
        kafkaFactory.subscribeCheck(topic);

        if (BaseUtil.isEmpty(iConsumer)) {
            throw new RuntimeException("consumer must not be null");
        }

        org.apache.kafka.clients.consumer.KafkaConsumer consumer = kafkaFactory.getConsumer(topic);
        consumer.subscribe(Arrays.asList(topic));
    }
}
