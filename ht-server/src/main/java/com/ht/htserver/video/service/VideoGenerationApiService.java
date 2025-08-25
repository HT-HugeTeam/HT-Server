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


    private NestJSVideoResponse createVideoInternal(NestJSVideoRequest request, String endpoint) {
        log.info("🎬 Starting NestJS video generation request");
        log.debug("📝 Endpoint: {}", endpoint);
        log.debug("⏱️ Timeout configured: {}ms", timeoutMillis);
        
        if (request != null) {
            log.debug("📋 Request details - Template: {}, Images: {}, Store: {}", 
                     request.getTemplate() != null ? request.getTemplate() : "null",
                     request.getImages() != null ? request.getImages().size() : 0,
                     request.getStore() != null ? "Present" : "null");
        }
        
        long startTime = System.currentTimeMillis();
        
        try {
            log.info("📤 Sending video generation request to NestJS service");
            
            Mono<List<NestJSVideoResponse>> responseMono = nestjsVideoClient
                    .post()
                    .uri(endpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<NestJSVideoResponse>>() {})
                    .timeout(Duration.ofMillis(timeoutMillis))
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                            .filter(throwable -> {
                                boolean shouldRetry = !(throwable instanceof WebClientResponseException.BadRequest);
                                if (!shouldRetry) {
                                    log.warn("🚫 Not retrying due to BadRequest (400) - {}", throwable.getMessage());
                                } else {
                                    log.info("🔄 Will retry on error: {}", throwable.getMessage());
                                }
                                return shouldRetry;
                            })
                            .doBeforeRetry(retrySignal -> {
                                long currentDuration = System.currentTimeMillis() - startTime;
                                log.warn("🔄 Retrying NestJS video generation request (attempt: {}) after {}ms due to: {}", 
                                        retrySignal.totalRetries() + 1, currentDuration, retrySignal.failure().getMessage());
                            }))
                    .doOnSubscribe(subscription -> log.debug("🚀 NestJS video generation request subscribed"))
                    .doOnNext(response -> {
                        long duration = System.currentTimeMillis() - startTime;
                        log.info("✅ NestJS video generation response received in {}ms", duration);
                        
                        if (response != null) {
                            log.debug("📨 Response list size: {}", response.size());
                            if (!response.isEmpty()) {
                                NestJSVideoResponse firstResponse = response.get(0);
                                log.debug("🎬 Video response - Video URL available: {}", 
                                         firstResponse.getVideoUrl() != null);
                                log.debug("🔢 Response ID: {}", firstResponse.getId());
                            }
                        }
                    })
                    .doOnError(error -> {
                        long duration = System.currentTimeMillis() - startTime;
                        log.error("❌ NestJS video generation request failed after {}ms: {}", duration, error.getMessage());
                        
                        if (error instanceof WebClientResponseException) {
                            WebClientResponseException webEx = (WebClientResponseException) error;
                            log.error("🔴 HTTP Status: {}, Response Body: {}", webEx.getStatusCode(), webEx.getResponseBodyAsString());
                            
                            // Log specific error types
                            if (webEx.getStatusCode().is4xxClientError()) {
                                log.error("🔴 Client Error (4xx) - Check request payload and endpoint");
                            } else if (webEx.getStatusCode().is5xxServerError()) {
                                log.error("🔴 Server Error (5xx) - NestJS service internal error");
                            }
                        } else if (error instanceof java.util.concurrent.TimeoutException) {
                            log.error("🔴 Request timeout after {}ms - NestJS service is taking too long", timeoutMillis);
                        }
                    });

            log.debug("⏳ Blocking for NestJS video generation response...");
            List<NestJSVideoResponse> responseList = responseMono.block();
            
            long totalDuration = System.currentTimeMillis() - startTime;
            
            if (responseList == null || responseList.isEmpty()) {
                log.error("💥 No video generation response received from NestJS service after {}ms", totalDuration);
                throw new RuntimeException("No video generation response received from NestJS service");
            }
            
            log.info("🎉 NestJS video generation completed successfully in {}ms", totalDuration);
            log.debug("📤 Returning first response from list of {} items", responseList.size());
            
            NestJSVideoResponse result = responseList.get(0);
            log.debug("✅ Final result - Video URL: {}, ID: {}", 
                     result.getVideoUrl() != null ? "Present" : "null", result.getId());
            
            return result;

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("💥 Failed to call NestJS video generation service after {}ms at endpoint: {}", duration, endpoint, e);
            
            // Enhanced error context based on exception type
            if (e.getMessage().contains("Connection refused")) {
                log.error("🔴 Connection refused - NestJS service is down or unreachable");
            } else if (e.getMessage().contains("timeout")) {
                log.error("🔴 Request timeout - NestJS service is overloaded or processing is taking too long");
            } else if (e.getMessage().contains("400")) {
                log.error("🔴 Bad Request - Invalid request payload sent to NestJS service");
            } else if (e.getMessage().contains("500")) {
                log.error("🔴 Internal Server Error - NestJS service encountered an error");
            }
            
            throw new RuntimeException("Failed to generate video: " + e.getMessage(), e);
        }
    }
}