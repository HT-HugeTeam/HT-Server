package com.ht.htserver.video.service;

import com.ht.htserver.video.dto.nestjs.NestJSVideoRequest;
import com.ht.htserver.video.dto.nestjs.NestJSVideoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoGenerationApiService {

    private final WebClient nestjsVideoClient;

    @Value("${video-generation.nestjs.timeout}")
    private int timeoutMillis;

    public NestJSVideoResponse createVideo(NestJSVideoRequest request) {
        return createVideoInternal(request, "/");
    }

    public NestJSVideoResponse createVideoTemplate3(NestJSVideoRequest request) {
        return createVideoInternal(request, "/template3");
    }

    private NestJSVideoResponse createVideoInternal(NestJSVideoRequest request, String endpoint) {
        try {
            log.info("Calling NestJS video generation service at endpoint: {}", endpoint);
            log.debug("Request payload: {}", request);

            Mono<List<NestJSVideoResponse>> responseMono = nestjsVideoClient
                    .post()
                    .uri(endpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<NestJSVideoResponse>>() {})
                    .timeout(Duration.ofMillis(timeoutMillis))
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                            .filter(throwable -> !(throwable instanceof WebClientResponseException.BadRequest)))
                    .doOnSuccess(response -> log.info("Successfully received response from NestJS: {}", response))
                    .doOnError(error -> log.error("Error calling NestJS video generation service", error));

            List<NestJSVideoResponse> responseList = responseMono.block();
            
            if (responseList == null || responseList.isEmpty()) {
                throw new RuntimeException("No video generation response received from NestJS service");
            }
            
            return responseList.get(0);

        } catch (Exception e) {
            log.error("Failed to call NestJS video generation service at endpoint: {}", endpoint, e);
            throw new RuntimeException("Failed to generate video: " + e.getMessage(), e);
        }
    }
}