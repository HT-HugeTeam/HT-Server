package com.ht.htserver.store.repository;

import com.ht.htserver.store.entity.Store;
import com.ht.htserver.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID> {
    List<Store> findByUserId(UUID userId);
}
