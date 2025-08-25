package com.ht.htserver.auth.service;

import com.ht.htserver.auth.dto.KakaoTokenResponse;
import com.ht.htserver.auth.dto.KakaoUserInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoApiService {
    
    private final WebClient webClient = WebClient.builder().build();
    
    @Value("${kakao.client-id}")
    private String clientId;
    
    @Value("${kakao.client-secret}")
    private String clientSecret;
    
    @Value("${kakao.token-url}")
    private String tokenUrl;
    
    @Value("${kakao.user-info-url}")
    private String userInfoUrl;
    
    public KakaoTokenResponse getAccessToken(String authorizationCode) {
        log.info("🌐 Starting Kakao access token exchange");
        log.debug("📝 Request URL: {}", tokenUrl);
        log.debug("🔑 Client ID: {}", clientId != null ? clientId.substring(0, Math.min(4, clientId.length())) + "***" : "null");
        log.debug("📋 Authorization code length: {}", authorizationCode != null ? authorizationCode.length() : 0);
        
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("code", authorizationCode);
        
        long startTime = System.currentTimeMillis();
        
        try {
            log.debug("📤 Sending POST request to Kakao token endpoint");
            Mono<KakaoTokenResponse> responseMono = webClient.post()
                    .uri(tokenUrl)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(KakaoTokenResponse.class)
                    .doOnSubscribe(subscription -> log.debug("🚀 WebClient request subscribed"))
                    .doOnNext(response -> {
                        long duration = System.currentTimeMillis() - startTime;
                        log.info("✅ Kakao token response received in {}ms", duration);
                        log.debug("📨 Access token length: {}", 
                                response.getAccessToken() != null ? response.getAccessToken().length() : 0);
                        log.debug("⏰ Token expires in: {} seconds", response.getExpiresIn());
                        log.debug("🔄 Refresh token length: {}", 
                                response.getRefreshToken() != null ? response.getRefreshToken().length() : 0);
                    })
                    .doOnError(error -> {
                        long duration = System.currentTimeMillis() - startTime;
                        log.error("❌ Kakao token request failed after {}ms: {}", duration, error.getMessage());
                        
                        if (error instanceof org.springframework.web.reactive.function.client.WebClientResponseException) {
                            org.springframework.web.reactive.function.client.WebClientResponseException webEx = 
                                (org.springframework.web.reactive.function.client.WebClientResponseException) error;
                            log.error("🔴 HTTP Status: {}, Response Body: {}", webEx.getStatusCode(), webEx.getResponseBodyAsString());
                        }
                    });
            
            KakaoTokenResponse response = responseMono.block();
            
            long totalDuration = System.currentTimeMillis() - startTime;
            log.info("🎉 Kakao access token exchange completed successfully in {}ms", totalDuration);
            
            return response;
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("💥 Failed to get Kakao access token after {}ms", duration, e);
            
            // Enhanced error context
            if (e.getMessage().contains("400")) {
                log.error("🔴 Bad Request (400) - Invalid authorization code or client credentials");
            } else if (e.getMessage().contains("401")) {
                log.error("🔴 Unauthorized (401) - Invalid client credentials");
            } else if (e.getMessage().contains("timeout")) {
                log.error("🔴 Request timeout - Kakao API is slow or unreachable");
            } else if (e.getMessage().contains("connection")) {
                log.error("🔴 Connection error - Network issue or Kakao API is down");
            }
            
            throw new RuntimeException("Failed to get Kakao access token", e);
        }
    }
    
    public KakaoUserInfoResponse getUserInfo(String accessToken) {
        log.info("🌐 Starting Kakao user info retrieval");
        log.debug("📝 Request URL: {}", userInfoUrl);
        log.debug("🎫 Access token length: {}", accessToken != null ? accessToken.length() : 0);
        
        long startTime = System.currentTimeMillis();
        
        try {
            log.debug("📤 Sending GET request to Kakao user info endpoint");
            Mono<KakaoUserInfoResponse> responseMono = webClient.get()
                    .uri(userInfoUrl)
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(KakaoUserInfoResponse.class)
                    .doOnSubscribe(subscription -> log.debug("🚀 WebClient user info request subscribed"))
                    .doOnNext(response -> {
                        long duration = System.currentTimeMillis() - startTime;
                        log.info("✅ Kakao user info response received in {}ms", duration);
                        
                        if (response != null) {
                            log.debug("👤 User ID: {}", response.getId());
                            log.debug("📧 Email available: {}", 
                                    response.getKakaoAccount() != null && response.getKakaoAccount().getEmail() != null);
                            log.debug("👤 Nickname available: {}", 
                                    response.getProperties() != null && response.getProperties().getNickname() != null);
                            log.debug("🖼️ Profile image available: {}", 
                                    response.getProperties() != null && response.getProperties().getProfileImage() != null);
                        }
                    })
                    .doOnError(error -> {
                        long duration = System.currentTimeMillis() - startTime;
                        log.error("❌ Kakao user info request failed after {}ms: {}", duration, error.getMessage());
                        
                        if (error instanceof org.springframework.web.reactive.function.client.WebClientResponseException) {
                            org.springframework.web.reactive.function.client.WebClientResponseException webEx = 
                                (org.springframework.web.reactive.function.client.WebClientResponseException) error;
                            log.error("🔴 HTTP Status: {}, Response Body: {}", webEx.getStatusCode(), webEx.getResponseBodyAsString());
                        }
                    });
            
            KakaoUserInfoResponse response = responseMono.block();
            
            long totalDuration = System.currentTimeMillis() - startTime;
            log.info("🎉 Kakao user info retrieval completed successfully in {}ms", totalDuration);
            
            return response;
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("💥 Failed to get Kakao user info after {}ms", duration, e);
            
            // Enhanced error context
            if (e.getMessage().contains("401")) {
                log.error("🔴 Unauthorized (401) - Invalid or expired access token");
            } else if (e.getMessage().contains("403")) {
                log.error("🔴 Forbidden (403) - Insufficient permissions for user info");
            } else if (e.getMessage().contains("timeout")) {
                log.error("🔴 Request timeout - Kakao API is slow or unreachable");
            } else if (e.getMessage().contains("connection")) {
                log.error("🔴 Connection error - Network issue or Kakao API is down");
            }
            
            throw new RuntimeException("Failed to get Kakao user info", e);
        }
    }
}