package com.ht.htserver.video.repository;

import com.ht.htserver.video.entity.VideoGeneration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VideoGenerationRepository extends JpaRepository<VideoGeneration, UUID> {
    // Repository methods will be added as needed
}
