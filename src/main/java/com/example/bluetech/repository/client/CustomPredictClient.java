package com.example.bluetech.repository.client;

import com.example.bluetech.dto.respone.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Repository
@RequiredArgsConstructor

public class CustomPredictClient {
    private final WebClient webClient;
    public Mono<Response> predictImage(List<MultipartFile> imageFiles) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        for (MultipartFile file : imageFiles) {
            builder.part("files", file.getResource())
                    .filename(file.getOriginalFilename())
                    .contentType(MediaType.parseMediaType(file.getContentType()));
        }
        return webClient.post()
                .uri("/api/v1/predict/image")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(Response.class);
    }
}
