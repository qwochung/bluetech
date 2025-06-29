package com.example.bluetech.service.imp;

import com.example.bluetech.dto.request.PredictRequest;
import com.example.bluetech.dto.respone.Response;
import com.example.bluetech.repository.client.PredictClient;
import com.example.bluetech.service.PredictService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PredictServiceImpl implements PredictService {
    PredictClient predictClient;



    @Override
    public Mono<Response> predictContent(PredictRequest request) {
        return predictClient.predictContent(request);
    }
}
