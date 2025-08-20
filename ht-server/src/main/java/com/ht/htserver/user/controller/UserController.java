package com.ht.htserver.user.controller;

import com.ht.htserver.auth.service.JwtService;
import com.ht.htserver.user.dto.request.UpdateUserOnboardingStatusRequest;
import com.ht.htserver.user.dto.response.UserOnboardingStatusResponse;
import com.ht.htserver.user.entity.User;
import com.ht.htserver.user.repository.UserRepository;
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
@Tag(name = "User", description = "User management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final JwtService jwtService;
    private final UserService userService;

    @GetMapping("/onboarding")
    @Operation(summary = "Get user onboarding status", description = "Retrieve current user's onboarding information and agreement status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved onboarding status",
                content = @Content(schema = @Schema(implementation = UserOnboardingStatusResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
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

    @PostMapping("/onboarding")
    @Operation(summary = "Update user onboarding status", description = "Update user's nickname and agreement status for terms of service, privacy policy, and location services")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated onboarding status",
                content = @Content(schema = @Schema(implementation = UserOnboardingStatusResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
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
                        .termsOfServiceAccepted(user.getTermsOfServiceAccepted())
                        .privacyPolicyAccepted(user.getPrivacyPolicyAccepted())
                        .locationServiceAccepted(user.getLocationServiceAccepted())
                        .build()
        );
    }
}
