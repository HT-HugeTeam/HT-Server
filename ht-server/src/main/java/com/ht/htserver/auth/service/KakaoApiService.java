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
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("code", authorizationCode);
        
        try {
            Mono<KakaoTokenResponse> response = webClient.post()
                    .uri(tokenUrl)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(KakaoTokenResponse.class);
            
            return response.block();
        } catch (Exception e) {
            log.error("Failed to get Kakao access token", e);
            throw new RuntimeException("Failed to get Kakao access token", e);
        }
    }
    
    public KakaoUserInfoResponse getUserInfo(String accessToken) {
        try {
            Mono<KakaoUserInfoResponse> response = webClient.get()
                    .uri(userInfoUrl)
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(KakaoUserInfoResponse.class);
            
            return response.block();
        } catch (Exception e) {
            log.error("Failed to get Kakao user info", e);
            throw new RuntimeException("Failed to get Kakao user info", e);
        }
    }
}