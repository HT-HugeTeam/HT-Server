package com.ht.htserver.user.entity;

import com.ht.htserver.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "kakao_id", unique = true, nullable = false)
    private String kakaoId;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "nickname")
    private String nickname;
    
    @Column(name = "profile_image_url")
    private String profileImageUrl;
}
