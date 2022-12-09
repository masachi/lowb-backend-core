package io.github.masachi.utils.kafka;

import lombok.*;

import java.io.Serializable;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KafkaMessage<T> implements Serializable {

    private String event;

    private T message;
}
