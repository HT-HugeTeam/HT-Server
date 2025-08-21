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
        try {
            KakaoTokenResponse tokenResponse = kakaoApiService.getAccessToken(authorizationCode);
            KakaoUserInfoResponse userInfo = kakaoApiService.getUserInfo(tokenResponse.getAccessToken());
            
            String kakaoId = userInfo.getId().toString();
            
            Optional<User> existingUser = userRepository.findByKakaoId(kakaoId);
            
            User user;
            if (existingUser.isPresent()) {
                user = existingUser.get();
                updateUserInfo(user, userInfo);
            } else {
                user = createNewUser(userInfo);
            }
            
            userRepository.save(user);
            
            return jwtService.generateToken(user.getId());
            
        } catch (Exception e) {
            log.error("Failed to login with Kakao authorization code", e);
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