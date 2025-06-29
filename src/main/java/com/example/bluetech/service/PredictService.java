package com.example.bluetech.service;

import com.example.bluetech.dto.request.PredictRequest;
import com.example.bluetech.dto.respone.Response;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

public interface PredictService {
    public Mono<Response> predictContent(@RequestBody PredictRequest request);
}
