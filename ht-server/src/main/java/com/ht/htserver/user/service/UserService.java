package com.ht.htserver.user.service;

import com.ht.htserver.user.dto.request.UpdateUserOnboardingStatusRequest;
import com.ht.htserver.user.entity.User;
import com.ht.htserver.user.exception.UserNotFoundException;
import com.ht.htserver.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getUser(UUID userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    public User updateUserOnboarding(User user, UpdateUserOnboardingStatusRequest request){
        user.setNickname(request.getNickname());
        user.setTermsOfServiceAccepted(request.isTermsOfServiceAccepted());
        user.setPrivacyPolicyAccepted(request.isPrivacyPolicyAccepted());
        user.setLocationServiceAccepted(request.isLocationServiceAccepted());

        return userRepository.save(user);
    }
}
