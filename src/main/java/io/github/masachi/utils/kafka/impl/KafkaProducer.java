package io.github.masachi.utils.kafka.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.annotations.Beta;
import io.github.masachi.condition.KafkaCondition;
import io.github.masachi.utils.BaseUtil;
import io.github.masachi.utils.RetryUtil;
import io.github.masachi.utils.kafka.IKafkaProducer;
import io.github.masachi.utils.kafka.KafkaInitializer;
import io.github.masachi.utils.kafka.KafkaMessage;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@Log4j2
@Conditional(KafkaCondition.class)
public class KafkaProducer implements IKafkaProducer
{

    @Autowired
    private KafkaTemplate<String, KafkaMessage> kafkaTemplate;

//    @Autowired
//    private ReplyingKafkaTemplate<String, KafkaMessage, String> replyingKafkaTemplate;

    @Autowired
    private KafkaInitializer kafkaInitializer;

    @Value("${kafka.topic}")
    private String defaultTopic;

    @Override
    public void sendMessage(KafkaMessage message) {
        this.kafkaTemplate.send(defaultTopic, message);
        log.info(new Date() + " Send mq Producer message. Message is:" + message.toString() + " Topic is " + defaultTopic);
    }

    @Override
    public void sendMessage(KafkaMessage message, String topic) {
        RetryUtil.retry(
                () -> this.kafkaTemplate.send(topic, message),
                (attempt) -> {
                    if (attempt.hasException()) {
                        kafkaInitializer.initTopic(topic);
                    }
                }
        ).orElse(null);
        log.info(new Date() + " Send mq Producer message. Message is:" + message.toString() + " Topic is " + topic);
    }


//    @Beta
//    public void sendMessageWithReply(KafkaMessage message) {
//        ProducerRecord<String, KafkaMessage> record = new ProducerRecord<>(defaultTopic, message);
//        RequestReplyFuture<String, KafkaMessage, String> replyFuture = this.replyingKafkaTemplate.sendAndReceive(record);
//        try {
//            SendResult<String, KafkaMessage> sendResult = replyFuture.getSendFuture().get(10, TimeUnit.SECONDS);
//            System.out.println("Sent ok: " + sendResult.getRecordMetadata());
//            ConsumerRecord<String, String> consumerRecord = replyFuture.get(10, TimeUnit.SECONDS);
//        }
//        catch (Exception e) {
//            log.error("sendMessageWithReply with error: " + e.getMessage());
//        }
//    }

}
