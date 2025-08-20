package com.ht.htserver.user.dto.request;

import com.ht.htserver.user.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "유저 온보딩 정보 업데이트 요청")
public class UpdateUserOnboardingStatusRequest {
    @NotBlank(message = "유저 닉네임은 필수입니다.")
    @Schema(description = "유저 닉네임", example = "복돌")
    private String nickname;

    @NotNull(message = "유저 권한은 필수입니다.")
    @Schema(description = "유저 권한", example = "USER")
    private Role role;

    @NotNull(message = "쇼츠테이블 서비스 이용약관 정보는 필수입니다.")
    @Schema(description = "쇼츠테이블 서비스 이용약관")
    private boolean termsOfServiceAccepted;

    @NotNull(message = "개인정보 수집 및 이용동의 정보는 필수입니다.")
    @Schema(description = "개인정보 수집 및 이용동의")
    private boolean privacyPolicyAccepted;

    @Schema(description = "위치기반 서비스 이용동의")
    private boolean locationServiceAccepted;
}
