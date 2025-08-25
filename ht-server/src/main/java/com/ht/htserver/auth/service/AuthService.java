package com.ht.htserver.auth.service;

import com.ht.htserver.auth.dto.KakaoTokenResponse;
import com.ht.htserver.auth.dto.KakaoUserInfoResponse;
import com.ht.htserver.user.entity.Role;
import com.ht.htserver.user.entity.User;
import com.ht.htserver.user.exception.UserNotFoundException;
import com.ht.htserver.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final KakaoApiService kakaoApiService;
    
    public String login(String kakaoId) {
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(UserNotFoundException::new);
        
        return jwtService.generateToken(user.getId());
    }
    
    @Transactional
    public String loginWithAuthorizationCode(String authorizationCode) {
        log.info("🔐 Starting Kakao OAuth authentication process");
        log.debug("📋 Authorization code received with length: {}", authorizationCode != null ? authorizationCode.length() : 0);
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Step 1: Exchange authorization code for access token
            log.info("🎫 Exchanging authorization code for Kakao access token");
            KakaoTokenResponse tokenResponse = kakaoApiService.getAccessToken(authorizationCode);
            long tokenDuration = System.currentTimeMillis() - startTime;
            log.info("✅ Kakao access token obtained in {}ms", tokenDuration);
            log.debug("📝 Token response received with access_token length: {}", 
                     tokenResponse.getAccessToken() != null ? tokenResponse.getAccessToken().length() : 0);
            
            // Step 2: Get user info from Kakao
            log.info("👤 Fetching user information from Kakao API");
            long userInfoStartTime = System.currentTimeMillis();
            KakaoUserInfoResponse userInfo = kakaoApiService.getUserInfo(tokenResponse.getAccessToken());
            long userInfoDuration = System.currentTimeMillis() - userInfoStartTime;
            log.info("✅ Kakao user info retrieved in {}ms", userInfoDuration);
            
            String kakaoId = userInfo.getId().toString();
            log.debug("🆔 Kakao user ID: {}", kakaoId);
            
            // Step 3: Check if user exists in database
            log.info("🔍 Checking if user exists in database: {}", kakaoId);
            Optional<User> existingUser = userRepository.findByKakaoId(kakaoId);
            
            User user;
            if (existingUser.isPresent()) {
                log.info("👥 Existing user found, updating user information");
                user = existingUser.get();
                updateUserInfo(user, userInfo);
                log.debug("📝 User info updated for existing user: {}", user.getId());
            } else {
                log.info("🆕 New user detected, creating user account");
                user = createNewUser(userInfo);
                log.debug("👤 New user created with Kakao ID: {}", kakaoId);
            }
            
            // Step 4: Save user to database
            log.debug("💾 Saving user to database");
            long dbStartTime = System.currentTimeMillis();
            userRepository.save(user);
            long dbDuration = System.currentTimeMillis() - dbStartTime;
            log.info("✅ User saved to database in {}ms", dbDuration);
            
            // Step 5: Generate JWT token
            log.info("🎫 Generating JWT token for user: {}", user.getId());
            long jwtStartTime = System.currentTimeMillis();
            String jwtToken = jwtService.generateToken(user.getId());
            long jwtDuration = System.currentTimeMillis() - jwtStartTime;
            log.info("✅ JWT token generated in {}ms", jwtDuration);
            
            long totalDuration = System.currentTimeMillis() - startTime;
            log.info("🎉 Kakao OAuth authentication completed successfully in {}ms (Token: {}ms, UserInfo: {}ms, DB: {}ms, JWT: {}ms)", 
                     totalDuration, tokenDuration, userInfoDuration, dbDuration, jwtDuration);
            
            return jwtToken;
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("❌ Kakao OAuth authentication failed after {}ms: {}", duration, e.getMessage(), e);
            
            // Log specific error types for better debugging
            if (e.getMessage().contains("access token")) {
                log.error("🔴 Kakao access token exchange failed - check authorization code validity");
            } else if (e.getMessage().contains("user info")) {
                log.error("🔴 Kakao user info retrieval failed - check access token validity");
            } else if (e.getMessage().contains("database") || e.getMessage().contains("save")) {
                log.error("🔴 Database operation failed during user save");
            } else if (e.getMessage().contains("JWT") || e.getMessage().contains("token")) {
                log.error("🔴 JWT token generation failed");
            }
            
            throw new RuntimeException("Failed to login with Kakao", e);
        }
    }
    
    private User createNewUser(KakaoUserInfoResponse userInfo) {
        User user = new User();
        user.setKakaoId(userInfo.getId().toString());
        user.setEmail(userInfo.getKakaoAccount() != null ? userInfo.getKakaoAccount().getEmail() : null);
        user.setNickname(userInfo.getProperties() != null ? userInfo.getProperties().getNickname() : null);
        user.setProfileImageUrl(userInfo.getProperties() != null ? userInfo.getProperties().getProfileImage() : null);
        user.setRole(Role.USER);
        
        return user;
    }
    
    private void updateUserInfo(User user, KakaoUserInfoResponse userInfo) {
        if (userInfo.getKakaoAccount() != null && userInfo.getKakaoAccount().getEmail() != null) {
            user.setEmail(userInfo.getKakaoAccount().getEmail());
        }
        if (userInfo.getProperties() != null) {
            if (userInfo.getProperties().getNickname() != null) {
                user.setNickname(userInfo.getProperties().getNickname());
            }
            if (userInfo.getProperties().getProfileImage() != null) {
                user.setProfileImageUrl(userInfo.getProperties().getProfileImage());
            }
        }
    }
}