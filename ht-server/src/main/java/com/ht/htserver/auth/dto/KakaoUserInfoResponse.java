package com.ht.htserver.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoUserInfoResponse {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;
    
    @JsonProperty("properties")
    private Properties properties;
    
    @Getter
    @Setter
    public static class KakaoAccount {
        @JsonProperty("email")
        private String email;
        
        @JsonProperty("email_verified")
        private Boolean emailVerified;
        
        @JsonProperty("is_email_valid")
        private Boolean isEmailValid;
        
        @JsonProperty("is_email_verified")
        private Boolean isEmailVerified;
    }
    
    @Getter
    @Setter
    public static class Properties {
        @JsonProperty("nickname")
        private String nickname;
        
        @JsonProperty("profile_image")
        private String profileImage;
        
        @JsonProperty("thumbnail_image")
        private String thumbnailImage;
    }
}