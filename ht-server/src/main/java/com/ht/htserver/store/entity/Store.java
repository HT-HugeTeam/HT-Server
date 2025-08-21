package com.ht.htserver.store.entity;

import com.ht.htserver.common.entity.BaseEntity;
import com.ht.htserver.user.entity.User;
import com.ht.htserver.video.entity.Video;
import com.ht.htserver.video.entity.VideoGeneration;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "stores")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Store extends BaseEntity {
    @Column()
    private String name;

    @Column()
    private String address;

    @Column()
    private String description;

    @Column()
    private String naverUrl;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Video> videos = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VideoGeneration> videoGenerations = new ArrayList<>();
}
