package com.ht.htserver.user.dto.response;

import com.ht.htserver.user.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "유저 온보딩 정보 반환값")
public class UserOnboardingStatusResponse {
    
    @Schema(description = "유저 닉네임", example = "복돌")
    private String nickname;

    @Schema(description = "유저 권한", example = "USER")
    private Role role;

    @Schema(description = "쇼츠테이블 서비스 이용약관")
    private boolean termsOfServiceAccepted;

    @Schema(description = "개인정보 수집 및 이용동의")
    private boolean privacyPolicyAccepted;

    @Schema(description = "위치기반 서비스 이용동의")
    private boolean locationServiceAccepted;
}
