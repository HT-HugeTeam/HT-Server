package com.ht.htserver.video.entity;

import com.ht.htserver.common.entity.BaseEntity;
import com.ht.htserver.store.entity.Store;
import com.ht.htserver.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "videos")
@Getter
@Setter
public class Video extends BaseEntity {

    @Column
    private String videoUrl;

    @Column
    private Long views = 0L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id")
    private Store store;
}
