package com.ht.htserver.video.service;

import com.ht.htserver.video.dto.creatomate.CreatomateRenderResponse;
import com.ht.htserver.video.entity.Video;
import com.ht.htserver.video.entity.VideoGeneration;
import com.ht.htserver.video.entity.VideoGenerationStatus;
import com.ht.htserver.video.repository.VideoGenerationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoStatusCheckService {
    
    private final VideoGenerationRepository videoGenerationRepository;
    private final CreatomateApiService creatomateApiService;
    private final VideoService videoService;

    // Removed scheduled polling - now only checks on-demand via GET requests
    
    @Transactional
    public void checkSingleVideoGeneration(VideoGeneration generation) {
        String externalJobId = generation.getExternalJobId();
        log.debug("Checking status for video generation ID: {} with external job ID: {}", 
                 generation.getId(), externalJobId);
        
        Optional<CreatomateRenderResponse> responseOpt = creatomateApiService.getRenderStatus(externalJobId);
        
        if (responseOpt.isEmpty()) {
            log.warn("Failed to get status for video generation ID: {} with external job ID: {}", 
                    generation.getId(), externalJobId);
            return;
        }
        
        CreatomateRenderResponse response = responseOpt.get();
        updateVideoGenerationStatus(generation, response);
    }
    
    private void updateVideoGenerationStatus(VideoGeneration generation, CreatomateRenderResponse response) {
        String creatomateStatus = response.getStatus();
        log.debug("Creatomate status for generation ID {}: {}", generation.getId(), creatomateStatus);
        
        switch (creatomateStatus.toLowerCase()) {
            case "queued", "processing" -> {
                // Keep status as IN_PROGRESS, no update needed
                log.debug("Video generation ID {} still in progress ({})", generation.getId(), creatomateStatus);
            }
            case "succeeded" -> {
                if (response.getUrl() != null && !response.getUrl().trim().isEmpty()) {
                    // Create Video entity instead of just storing URL
                    Video video = videoService.createVideoFromGeneration(generation, response.getUrl());
                    
                    // Update generation status and link to the created video
                    generation.updateStatusWithVideoUrl(VideoGenerationStatus.FINISHED, response.getUrl());
                    generation.setVideo(video);
                    videoGenerationRepository.save(generation);
                    
                    log.info("Video generation ID {} completed successfully. Created Video ID {} with URL: {}", 
                            generation.getId(), video.getId(), response.getUrl());
                } else {
                    log.warn("Video generation ID {} succeeded but no URL provided", generation.getId());
                }
            }
            case "failed" -> {
                String errorMessage = response.getErrorMessage() != null ? 
                    response.getErrorMessage() : "Video generation failed in Creatomate";
                generation.updateStatusWithError(VideoGenerationStatus.FAILED, errorMessage);
                videoGenerationRepository.save(generation);
                log.error("Video generation ID {} failed: {}", generation.getId(), errorMessage);
            }
            default -> {
                log.warn("Unknown Creatomate status '{}' for video generation ID {}", 
                        creatomateStatus, generation.getId());
            }
        }
    }
    
    /**
     * Check video generation status on-demand (called from GET endpoint)
     */
    @Transactional
    public void checkVideoGenerationStatus(UUID videoGenerationId) {
        VideoGeneration generation = videoGenerationRepository.findById(videoGenerationId)
                .orElse(null);
                
        if (generation == null) {
            log.warn("Video generation with ID {} not found", videoGenerationId);
            return;
        }
        
        if (generation.getStatus() != VideoGenerationStatus.IN_PROGRESS || 
            generation.getExternalJobId() == null) {
            log.debug("Video generation ID {} is not eligible for status check (status: {}, jobId: {})", 
                     videoGenerationId, generation.getStatus(), generation.getExternalJobId());
            return;
        }
        
        checkSingleVideoGeneration(generation);
    }
}