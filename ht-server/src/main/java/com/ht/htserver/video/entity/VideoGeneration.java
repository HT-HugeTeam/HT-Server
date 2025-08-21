package com.ht.htserver.video.entity;

import com.ht.htserver.common.entity.BaseEntity;
import com.ht.htserver.store.entity.Store;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "video_generations")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class  VideoGeneration extends BaseEntity {
    @Column
    private String text;

    @Column
    @OneToMany(mappedBy = "videoGeneration", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImageRequest> images = new ArrayList<>();

    @Column
    @OneToMany(mappedBy = "videoGeneration", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VideoRequest> videos = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id")
    private Store store;

    public void addImage(ImageRequest image) {
        images.add(image);
        image.setVideoGeneration(this);
    };

    public void addVideo(VideoRequest video) {
        videos.add(video);
        video.setVideoGeneration(this);
    };
}
