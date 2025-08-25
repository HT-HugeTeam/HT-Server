package com.ht.htserver.auth.controller;

import com.ht.htserver.auth.dto.KakaoLoginRequest;
import com.ht.htserver.auth.dto.LoginRequest;
import com.ht.htserver.auth.dto.LoginResponse;
import com.ht.htserver.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "인증 관리 API")
public class AuthController {
    
    private final AuthService authService;
    
//    @PostMapping("/login")
//    @Operation(summary = "카카오 ID로 로그인", description = "카카오 ID로 사용자를 인증하고 JWT 액세스 토큰을 반환합니다")
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "인증 성공",
//                content = @Content(schema = @Schema(implementation = LoginResponse.class))),
//        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content),
//        @ApiResponse(responseCode = "400", description = "잘못된 요청 본문", content = @Content)
//    })
//    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
//        try {
//            String accessToken = authService.login(request.getKakaoId());
//            return ResponseEntity.ok(new LoginResponse(accessToken));
//        } catch (RuntimeException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }
    
    @PostMapping("/kakao-login")
    @Operation(summary = "카카오 Authorization Code로 로그인", 
               description = "카카오 OAuth Authorization Code를 사용하여 사용자 정보를 가져오고 로그인 처리합니다. 새로운 사용자라면 자동으로 회원가입됩니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "인증 성공",
                content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 Authorization Code", content = @Content),
        @ApiResponse(responseCode = "500", description = "카카오 API 통신 실패", content = @Content)
    })
    public ResponseEntity<LoginResponse> kakaoLogin(@Valid @RequestBody KakaoLoginRequest request) {
        log.info("🔐 Starting Kakao OAuth login process");
        log.debug("📋 Kakao login request received with authorization code length: {}", 
                  request.getAuthorizationCode() != null ? request.getAuthorizationCode().length() : 0);
        
        long startTime = System.currentTimeMillis();
        
        try {
            log.info("🎯 Processing Kakao authorization code authentication");
            String accessToken = authService.loginWithAuthorizationCode(request.getAuthorizationCode());
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("✅ Kakao login successful in {}ms, JWT token generated", duration);
            log.debug("📤 Returning JWT token with length: {}", accessToken != null ? accessToken.length() : 0);
            
            return ResponseEntity.ok(new LoginResponse(accessToken));
            
        } catch (RuntimeException e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("❌ Kakao login failed after {}ms - Runtime Exception: {}", duration, e.getMessage(), e);
            
            if (e.getMessage().contains("Failed to login with Kakao")) {
                log.error("🔴 Kakao API communication error detected");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("💥 Unexpected error during Kakao login after {}ms: {}", duration, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
