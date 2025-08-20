package com.ht.htserver.video.entity;

import com.ht.htserver.store.entity.Store;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "video_generations")
public class VideoGeneration {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id")
    private Store store;
}
