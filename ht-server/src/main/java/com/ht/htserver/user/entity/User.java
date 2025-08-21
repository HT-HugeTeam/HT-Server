package com.ht.htserver.user.entity;

import com.ht.htserver.common.entity.BaseEntity;
import com.ht.htserver.store.entity.Store;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {
    @Column(name = "kakao_id", unique = true, nullable = false)
    private String kakaoId;
    
    @Column(name = "email")
    private String email;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "nickname")
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.USER;

    @Column(name = "terms_of_service_accepted", nullable = false)
    private Boolean termsOfServiceAccepted = false;

    @Column(name = "privacy_policy_accepted", nullable = false)
    private Boolean privacyPolicyAccepted = false;

    @Column(name = "location_service_accepted", nullable = false)
    private Boolean locationServiceAccepted = false;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Store> stores = new ArrayList<>();

    public void addStore(Store s) {
        stores.add(s);
        s.setUser(this);
    }
}
