package com.ht.htserver.video.entity;

import com.ht.htserver.store.entity.Store;
import com.ht.htserver.user.entity.User;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "videos")
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id")
    private Store store;
}
