package com.example.bluetech.messaging.consumer;

import com.example.bluetech.config.RabbitMQConfig;
import com.example.bluetech.dto.request.PredictRequest;
import com.example.bluetech.dto.respone.Response;
import com.example.bluetech.entity.Predict;
import com.example.bluetech.messaging.message.PostPredictMessage;
import com.example.bluetech.service.client.PredictClientService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostPredictConsumer {
    PredictClientService predictClientService;

    @RabbitListener(queues = RabbitMQConfig.PREDICT_QUEUE)
    public void receive(PostPredictMessage msg) {
        log.info("Message : " + msg);

        try {
            Response response =  predictClientService.predictContent(new PredictRequest(msg.getContent())).block();
            if (response != null &&  (response.getCode() ==200) && response.getData() != null) {
                Predict predict = Predict.convert(response.getData());
                log.info("Predict received: " + predict);
                if (predict != null && predict.getViolationDetected()) {

                }
            }

        } catch (Exception e){
            log.error("Error in consuming message", e);
        }
    }



}
