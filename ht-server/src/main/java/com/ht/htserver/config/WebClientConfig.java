package com.ht.htserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Value("${video-generation.nestjs.base-url}")
    private String nestjsBaseUrl;

    @Value("${video-generation.nestjs.timeout}")
    private int timeout;

    @Bean
    public WebClient nestjsVideoClient() {
        return WebClient.builder()
                .baseUrl(nestjsBaseUrl)
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024)) // 16MB
                .build();
    }
}