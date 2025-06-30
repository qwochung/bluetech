package com.example.bluetech.repository.client;

import com.example.bluetech.dto.request.PredictRequest;
import com.example.bluetech.dto.respone.Response;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

@Repository
@HttpExchange(url = "/api/v1/predict") // prefix chung

public interface PredictClientRepository {
    @PostExchange(url = "/content", contentType = MediaType.APPLICATION_JSON_VALUE)
    Mono<Response> predictContent(@RequestBody PredictRequest request );

    @PostExchange(url = "/image", contentType = MediaType.APPLICATION_JSON_VALUE)
    Mono<Response> predictImage(@RequestBody PredictRequest request );
}


