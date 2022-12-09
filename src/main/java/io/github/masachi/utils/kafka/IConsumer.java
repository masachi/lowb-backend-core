package io.github.masachi.utils.kafka;


import org.apache.kafka.common.protocol.Message;

@FunctionalInterface
public interface IConsumer<T> {

    T consumer(Message message);

}
