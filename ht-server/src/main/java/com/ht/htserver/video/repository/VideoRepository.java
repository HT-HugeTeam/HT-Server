package com.ht.htserver.video.repository;

import com.ht.htserver.video.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface VideoRepository extends JpaRepository<Video, UUID> {
    @Query("SELECT video FROM Video video WHERE video.store.user.id = :userId ORDER BY video.createdAt DESC")
    List<Video> findByUserId(@Param("userId") UUID userId);
}