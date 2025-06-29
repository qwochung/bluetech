package com.example.bluetech.service;

import com.example.bluetech.dto.request.PredictRequest;
import com.example.bluetech.dto.respone.Response;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PredictService {
    public Mono<Response> predictContent(@RequestBody PredictRequest request);
    public Mono<Response> predictImage(List<MultipartFile> files);
}
