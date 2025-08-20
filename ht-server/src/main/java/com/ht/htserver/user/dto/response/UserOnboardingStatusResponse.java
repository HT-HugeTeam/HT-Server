package com.ht.htserver.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "User onboarding status response")
public class UserOnboardingStatusResponse {
    
    @Schema(description = "User nickname", example = "복돌")
    private String nickname;

    @Schema(description = "Terms of service accepted status")
    private boolean termsOfServiceAccepted;

    @Schema(description = "Privacy policy accepted status")
    private boolean privacyPolicyAccepted;

    @Schema(description = "Location service accepted status")
    private boolean locationServiceAccepted;
}
