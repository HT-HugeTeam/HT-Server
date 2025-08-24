package com.ht.htserver.video.service;

import com.ht.htserver.video.dto.creatomate.CreatomateRenderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreatomateApiService {

    @Value("${creatomate.api-key:}")
    private String apiKey;
    
    @Value("${creatomate.base-url:https://api.creatomate.com/v1}")
    private String baseUrl;
    
    private final WebClient webClient = WebClient.builder()
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
            .build();

    public Optional<CreatomateRenderResponse> getRenderStatus(String renderId) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.warn("Creatomate API key is not configured. Cannot check render status.");
            return Optional.empty();
        }
        
        try {
            log.debug("Checking Creatomate render status for ID: {}", renderId);
            
            CreatomateRenderResponse response = webClient
                    .get()
                    .uri(baseUrl + "/renders/{renderId}", renderId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .bodyToMono(CreatomateRenderResponse.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();
            
            log.debug("Received render status for ID {}: {}", renderId, response != null ? response.getStatus() : "null");
            return Optional.ofNullable(response);
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                log.warn("Render not found in Creatomate API for ID: {}", renderId);
            } else {
                log.error("Failed to get render status from Creatomate API for ID: {}. Status: {}, Body: {}", 
                         renderId, e.getStatusCode(), e.getResponseBodyAsString());
            }
            return Optional.empty();
            
        } catch (Exception e) {
            log.error("Error calling Creatomate API for render ID: {}", renderId, e);
            return Optional.empty();
        }
    }
}