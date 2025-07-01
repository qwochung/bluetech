package com.example.bluetech.messaging.consumer;

import com.example.bluetech.config.RabbitMQConfig;
import com.example.bluetech.constant.ReferencesType;
import com.example.bluetech.constant.Status;
import com.example.bluetech.dto.request.PredictRequest;
import com.example.bluetech.dto.respone.Response;
import com.example.bluetech.entity.Predict;
import com.example.bluetech.messaging.message.PredictMessage;
import com.example.bluetech.service.ImageService;
import com.example.bluetech.service.PostService;
import com.example.bluetech.service.PredictService;
import com.example.bluetech.service.client.PredictClientService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PredictConsumer {
    PredictClientService predictClientService;
    PredictService predictService;
    PostService postService;
    ImageService imageService;

    @RabbitListener(queues = RabbitMQConfig.PREDICT_QUEUE)
    public void receive(PredictMessage msg) {
        log.info("Message : " + msg);
        try {
            ReferencesType type = msg.getReferencesType();
            switch (type) {
                case POST -> handlePredictPost(msg);
                case IMAGE -> handlePredictImage(msg);
//                    case COMMENT -> handlePredictComment(msg);
                default -> log.warn("Unhandled ReferencesType: {}", type);
            }


        } catch (Exception e) {
            log.error("Error in consuming message", e);
        }
    }


    private void handlePredictPost(PredictMessage msg) throws BadRequestException {

        if (postService.findById(msg.getReferencesId()).isEmpty()) {
            throw new BadRequestException("Post does not exist");
        }
        PredictRequest request =  new PredictRequest();
        request.setContent(msg.getContent());

        Response response = predictClientService.predictContent(request).block();
        Predict predict = new Predict();


        if (response != null && (response.getCode() == 200) && response.getData() != null) {
            predict = predict.convert( predict ,response.getData());
            log.info("Predict : " + predict);
            predict.setStatus(Status.SUCCESS);
        }
        else {
            predict.setStatus(Status.FAILURE);
        }

        predict.setReferencesId(msg.getReferencesId());
        predict.setReferencesType(msg.getReferencesType());
        predict=  predictService.create(predict);


    }

    private void handlePredictImage( PredictMessage msg) throws BadRequestException {
        if (imageService.findActiveIamgeById(msg.getReferencesId()).isEmpty()) {
            throw new BadRequestException("Post does not exist");
        }

        PredictRequest request =  new PredictRequest();
        request.setUrl(msg.getUrl());

        Response response = predictClientService.predictImageFromUrl(request).block();
        Predict predict = new Predict();


        if (response != null && (response.getCode() == 200) && response.getData() != null) {
            predict = predict.convert( predict ,response.getData());
            log.info("Predict : " + predict);
            predict.setStatus(Status.SUCCESS);
        }
        else {
            predict.setStatus(Status.FAILURE);
        }

        predict.setReferencesId(msg.getReferencesId());
        predict.setReferencesType(msg.getReferencesType());
        predict=  predictService.create(predict);

    }

//    private void handlePredictComment(PredictMessage msg) throws BadRequestException {
//        predict = predictService.create(predict);
//
//    }


    }
