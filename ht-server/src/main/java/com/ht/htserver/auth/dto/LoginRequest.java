package com.ht.htserver.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "카카오 ID로 로그인 요청")
public class LoginRequest {
    
    @NotBlank(message = "카카오 ID는 필수입니다")
    @Schema(description = "카카오 사용자 ID", example = "12345678")
    private String kakaoId;
}