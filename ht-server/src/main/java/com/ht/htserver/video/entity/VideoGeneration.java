package com.ht.htserver.video.entity;

import com.ht.htserver.common.entity.BaseEntity;
import com.ht.htserver.store.entity.Store;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "video_generations")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class  VideoGeneration extends BaseEntity {
    @Column
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private VideoGenerationStatus status = VideoGenerationStatus.IDLE;

    @OneToMany(mappedBy = "videoGeneration", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ImageRequest> images = new ArrayList<>();

    @OneToMany(mappedBy = "videoGeneration", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VideoRequest> videos = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", unique = true, nullable = true)
    private Video video;

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
