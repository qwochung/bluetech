package com.example.bluetech.service.client;

import com.example.bluetech.dto.request.PredictRequest;
import com.example.bluetech.dto.respone.Response;
import com.example.bluetech.repository.client.CustomPredictClientRepository;
import com.example.bluetech.repository.client.PredictClientRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PredictClientService {
    PredictClientRepository predictClientRepository;
    CustomPredictClientRepository customPredictClientRepository;



    public Mono<Response> predictContent(PredictRequest request) {
        return predictClientRepository.predictContent(request);
    }

    public Mono<Response> predictImage(List<MultipartFile> files) {
        return customPredictClientRepository.predictImage(files);
    }

    public Mono<Response> predictImageFromUrl(PredictRequest request) {
        return predictClientRepository.predictImageFromUrl(request);
    }
}
