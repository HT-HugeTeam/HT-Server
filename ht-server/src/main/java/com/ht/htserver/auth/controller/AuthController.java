package com.ht.htserver.auth.controller;

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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "인증 관리 API")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    @Operation(summary = "카카오 ID로 로그인", description = "카카오 ID로 사용자를 인증하고 JWT 액세스 토큰을 반환합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "인증 성공",
                content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 본문", content = @Content)
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            String accessToken = authService.login(request.getKakaoId());
            return ResponseEntity.ok(new LoginResponse(accessToken));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
