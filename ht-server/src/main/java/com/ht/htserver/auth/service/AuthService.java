package com.ht.htserver.auth.service;

import com.ht.htserver.user.entity.User;
import com.ht.htserver.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final JwtService jwtService;
    
    public String login(String kakaoId) {
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return jwtService.generateToken(user.getId());
    }
}