package com.ht.htserver.store.service;

import com.ht.htserver.store.dto.request.CreateStoreRequest;
import com.ht.htserver.store.entity.Store;
import com.ht.htserver.store.exception.StoreNotFoundException;
import com.ht.htserver.store.repository.StoreRepository;
import com.ht.htserver.user.entity.User;
import com.ht.htserver.user.repository.UserRepository;
import com.ht.htserver.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserService userService;

    @Transactional()
    public Store createStore(CreateStoreRequest request, UUID userId) {
        User user = userService.getUser(userId);

        Store store = Store.builder()
                .name(request.getName())
                .address(request.getAddress())
                .description(request.getDescription())
                .naverUrl(request.getNaverUrl())
                .build();

        user.addStore(store);

        return storeRepository.save(store);
    }

    @Transactional(readOnly = true)
    public Store getStore(UUID storeId) {
        return storeRepository.findById(storeId).orElseThrow(StoreNotFoundException::new);
    }
}
