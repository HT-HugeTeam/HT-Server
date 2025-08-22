package com.ht.htserver.user.controller;

import com.ht.htserver.auth.service.JwtService;
import com.ht.htserver.user.dto.request.UpdateUserOnboardingStatusRequest;
import com.ht.htserver.user.dto.response.UserOnboardingStatusResponse;
import com.ht.htserver.user.entity.Role;
import com.ht.htserver.user.entity.User;
import com.ht.htserver.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 관리 API")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final JwtService jwtService;
    private final UserService userService;

    @GetMapping("/onboarding")
    @Operation(summary = "사용자 온보딩 상태 조회", description = "현재 사용자의 온보딩 정보 및 동의 상태를 조회합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "온보딩 상태 조회 성공",
                content = @Content(schema = @Schema(implementation = UserOnboardingStatusResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증 실패 - 유효하지 않거나 누락된 JWT 토큰", content = @Content),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
    })
    public ResponseEntity<UserOnboardingStatusResponse> getOnboardingStatus(HttpServletRequest httpServletRequest) {

        UUID userId = jwtService.getUserIdFromRequest(httpServletRequest);
        User user = userService.getUser(userId);

        return ResponseEntity.ok(
                UserOnboardingStatusResponse.builder()
                        .nickname(user.getNickname())
                        .termsOfServiceAccepted(user.getTermsOfServiceAccepted())
                        .privacyPolicyAccepted(user.getPrivacyPolicyAccepted())
                        .locationServiceAccepted(user.getLocationServiceAccepted())
                        .build()
        );
    }

    @PutMapping("/onboarding")
    @Operation(summary = "사용자 온보딩 상태 업데이트", description = "사용자의 닉네임 및 서비스 이용약관, 개인정보보호정책, 위치기반서비스 동의 상태를 업데이트합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "온보딩 상태 업데이트 성공",
                content = @Content(schema = @Schema(implementation = UserOnboardingStatusResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 본문", content = @Content),
        @ApiResponse(responseCode = "401", description = "인증 실패 - 유효하지 않거나 누락된 JWT 토큰", content = @Content),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
    })
    public ResponseEntity<UserOnboardingStatusResponse> updateUserOnboardingStatus(
            HttpServletRequest httpServletRequest,
            @Valid @RequestBody UpdateUserOnboardingStatusRequest request
            ) {

        UUID userId = jwtService.getUserIdFromRequest(httpServletRequest);
        User user = userService.getUser(userId);

        user = userService.updateUserOnboarding(user, request);

        return ResponseEntity.ok(
                UserOnboardingStatusResponse.builder()
                        .nickname(user.getNickname())
                        .role(Role.USER)
                        .termsOfServiceAccepted(user.getTermsOfServiceAccepted())
                        .privacyPolicyAccepted(user.getPrivacyPolicyAccepted())
                        .locationServiceAccepted(user.getLocationServiceAccepted())
                        .build()
        );
    }
}
