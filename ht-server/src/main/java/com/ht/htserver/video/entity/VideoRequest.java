package com.ht.htserver.video.entity;

import com.ht.htserver.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class VideoRequest extends BaseEntity {

    @Column(length = 2048)
    private String videoUrl;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "video_generation_id")
    private VideoGeneration videoGeneration;
}
