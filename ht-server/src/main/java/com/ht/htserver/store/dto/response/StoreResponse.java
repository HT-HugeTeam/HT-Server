package com.ht.htserver.store.dto.response;

import com.ht.htserver.store.entity.Store;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class StoreResponse {
    private UUID id;

    private String name;

    private String address;

    private String description;

    private String naverUrl;

    public static StoreResponse toDto(Store store) {
        return StoreResponse.builder()
                .id(store.getId())
                .name(store.getName())
                .address(store.getAddress())
                .description(store.getDescription())
                .naverUrl(store.getNaverUrl())
                .build();
    }
}
