package com.ht.htserver.store.dto.response;

import com.ht.htserver.store.entity.Store;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class CreateStoreResponse {
    private UUID id;

    private String name;

    private String address;

    private String description;

    private String naverUrl;

    public static CreateStoreResponse toDto(Store store) {
        return CreateStoreResponse.builder()
                .id(store.getId())
                .name(store.getName())
                .address(store.getAddress())
                .description(store.getDescription())
                .naverUrl(store.getNaverUrl())
                .build();
    }
}
