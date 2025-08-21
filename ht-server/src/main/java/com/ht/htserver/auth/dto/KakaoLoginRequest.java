package com.ht.htserver.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "카카오 Authorization Code로 로그인 요청")
public class KakaoLoginRequest {
    
    @NotBlank(message = "Authorization code는 필수입니다")
    @Schema(description = "카카오 OAuth Authorization Code", example = "abcd1234efgh5678")
    private String authorizationCode;
}