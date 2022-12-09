package io.github.masachi.utils.kafka;

import io.github.masachi.utils.RetryUtil;

import java.util.Date;

public interface IKafkaProducer {

    void sendMessage(KafkaMessage message);

    void sendMessage(KafkaMessage message, String topic);
}
