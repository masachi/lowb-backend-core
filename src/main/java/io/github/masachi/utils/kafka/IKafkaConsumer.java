package io.github.masachi.utils.kafka;

public interface IKafkaConsumer {

    void subscribe(String topic, IConsumer consumer) throws RuntimeException;
}
