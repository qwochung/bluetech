package com.example.bluetech.messaging.producer;

import com.example.bluetech.config.RabbitMQConfig;
import com.example.bluetech.constant.ReferencesType;
import com.example.bluetech.entity.Comment;
import com.example.bluetech.entity.Image;
import com.example.bluetech.entity.Post;
import com.example.bluetech.messaging.message.PredictMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class PredictProducer {

    RabbitTemplate rabbitTemplate;


    public void addToMessagesQueue(Image image) {
        PredictMessage msg =  PredictMessage.builder()
                .referencesId(image.getId())
                .referencesType(ReferencesType.IMAGE)
                .url(image.getUrl())
                .build();
        log.info("Send message: {}", msg);
        send(msg);

    }

    public void addToMessagesQueue(Post post) {
        PredictMessage msg =  PredictMessage.builder()
                .referencesId(post.getId())
                .referencesType(ReferencesType.POST)
                .content(post.getTextContent())
                .build();
        log.info("Send message: {}", msg);
        send(msg);

    }

    public void addToMessagesQueue(Comment cmt) {
        PredictMessage msg =  PredictMessage.builder()
                .referencesId(cmt.getId())
                .referencesType(ReferencesType.COMMENT)
                .comment(cmt.getTextContent())
                .build();
        log.info("Send message: {}", msg);
//        send(msg);

    }





    private void send (PredictMessage msg) {
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_DELAY_KEY,
                msg,
                correlationData
        );
    }

}
