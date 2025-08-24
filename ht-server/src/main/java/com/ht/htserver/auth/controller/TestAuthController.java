package com.ht.htserver.auth.controller;

import com.ht.htserver.auth.dto.LoginResponse;
import com.ht.htserver.auth.service.JwtService;
import com.ht.htserver.user.entity.Role;
import com.ht.htserver.user.entity.User;
import com.ht.htserver.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/test/auth")
@RequiredArgsConstructor
@Tag(name = "Test Auth", description = "테스트용 인증 API")
public class TestAuthController {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @PostMapping("/login")
    @Operation(summary = "테스트 로그인", description = "테스트용 액세스 토큰을 생성합니다 (Kakao 인증 없이)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "테스트 로그인 성공",
                content = @Content(schema = @Schema(implementation = LoginResponse.class)))
    })
    public ResponseEntity<LoginResponse> testLogin(@RequestParam(defaultValue = "test@example.com") String email) {
        // Find or create test user
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createTestUser(email, "Test User", Role.USER));

        // Generate JWT token
        String accessToken = jwtService.generateToken(user.getId());

        LoginResponse response = new LoginResponse(accessToken);

        return ResponseEntity.ok(response);
    }


    private User createTestUser(String email, String nickname, Role role) {
        User user = new User();
        user.setKakaoId("test_" + UUID.randomUUID().toString()); // Set required field
        user.setEmail(email);
        user.setNickname(nickname);
        user.setProfileImageUrl("https://example.com/test-profile.jpg");
        user.setRole(role);
        user.setTermsOfServiceAccepted(true);
        user.setPrivacyPolicyAccepted(true);
        user.setLocationServiceAccepted(true);

        return userRepository.save(user);
    }
}