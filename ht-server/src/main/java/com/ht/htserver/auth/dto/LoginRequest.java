package com.ht.htserver.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Login request with Kakao ID")
public class LoginRequest {
    
    @NotBlank(message = "Kakao ID is required")
    @Schema(description = "Kakao user ID", example = "12345678")
    private String kakaoId;
}