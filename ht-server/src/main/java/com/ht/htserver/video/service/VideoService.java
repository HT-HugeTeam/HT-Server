package com.ht.htserver.video.service;

import com.ht.htserver.store.entity.Store;
import com.ht.htserver.store.service.StoreService;
import com.ht.htserver.video.dto.creatomate.CreatomateRenderResponse;
import com.ht.htserver.video.dto.nestjs.NestJSVideoRequest;
import com.ht.htserver.video.dto.nestjs.NestJSVideoResponse;
import com.ht.htserver.video.dto.request.CreateVideoGenerationRequest;
import com.ht.htserver.video.dto.request.ImageRequestDto;
import com.ht.htserver.video.dto.request.VideoRequestDto;
import com.ht.htserver.video.entity.*;
import com.ht.htserver.video.exception.VideoNotFoundException;
import com.ht.htserver.video.exception.VideoGenerationNotFoundException;
import com.ht.htserver.video.repository.VideoGenerationRepository;
import com.ht.htserver.video.repository.VideoRepository;
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
public class VideoService {

    private final VideoGenerationRepository videoGenerationRepository;
    private final StoreService storeService;
    private final VideoRepository videoRepository;
    private final VideoGenerationApiService videoGenerationApiService;
    private final CreatomateApiService creatomateApiService;

    @Transactional(readOnly = true)
    public List<Video> getVideosByUserId(UUID userId) {
        return videoRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Video getVideo(UUID videoId) {
        return videoRepository.findById(videoId).orElseThrow(VideoNotFoundException::new);
    }

    @Transactional
    public void deleteVideo(UUID videoId) {
        Video video = videoRepository.findById(videoId).orElseThrow(VideoNotFoundException::new);

        videoRepository.delete(video);
    }


    @Transactional
    public VideoGeneration createVideoGeneration(CreateVideoGenerationRequest request) {
        Store store = storeService.getStore(request.getStoreId());

        VideoGeneration videoGeneration = VideoGeneration.builder()
                .text(request.getText())
                .store(store).build();

        videoGenerationRepository.save(videoGeneration);

        request.getVideos().forEach(videoDto -> {
            VideoRequest videoRequest = VideoRequest.builder()
                    .videoUrl(videoDto.getVideoUrl())
                    .build();
            videoGeneration.addVideo(videoRequest);
        });
        
        request.getImages().forEach(imageDto -> {
            ImageRequest imageRequest = new ImageRequest(
                    imageDto.getName(),
                    imageDto.getImageUrl(),
                    null
            );
            videoGeneration.addImage(imageRequest);
        });

        // Call the NestJS video generation service
        try {
            // Map Spring Boot request to NestJS format
            NestJSVideoRequest nestjsRequest = mapToNestJSRequest(request);
            
            // Update status to IN_PROGRESS before making the call
            videoGeneration.updateStatus(VideoGenerationStatus.IN_PROGRESS);
            videoGenerationRepository.save(videoGeneration);
            
            // Call NestJS service (determine which endpoint based on number of images)
            NestJSVideoResponse response;

            log.info("Using standard endpoint for video generation with {} images", request.getImages().size());
            response = videoGenerationApiService.createVideo(nestjsRequest);
            
            // Update with job ID if available
            if (response != null && response.getId() != null) {
                videoGeneration.updateStatusWithJobId(VideoGenerationStatus.IN_PROGRESS, response.getId());
                log.info("Video generation started successfully with job ID: {}", response.getId());
            }
            
        } catch (Exception e) {
            log.error("Failed to start video generation for videoGeneration ID: {}", videoGeneration.getId(), e);
            videoGeneration.updateStatusWithError(VideoGenerationStatus.FAILED, e.getMessage());
        }

        return videoGenerationRepository.save(videoGeneration);
    }

    @Transactional
    public VideoGeneration getVideoGenerationStatus(UUID videoGenerationId) {
        VideoGeneration videoGeneration = videoGenerationRepository.findById(videoGenerationId)
                .orElseThrow(VideoGenerationNotFoundException::new);

        // If generation is in progress and we have an external job ID, check status immediately
        if (videoGeneration.getStatus() == VideoGenerationStatus.IN_PROGRESS && 
            videoGeneration.getExternalJobId() != null) {
            log.debug("Video generation {} is in progress with external job ID: {}", 
                     videoGenerationId, videoGeneration.getExternalJobId());
            
            // Trigger immediate status check
            checkVideoGenerationStatus(videoGeneration.getId());
            
            // Refetch the potentially updated entity
            videoGeneration = videoGenerationRepository.findById(videoGenerationId)
                    .orElseThrow(VideoGenerationNotFoundException::new);
        }

        return videoGeneration;
    }

    @Transactional
    public Video createVideoFromGeneration(VideoGeneration videoGeneration, String videoUrl) {
        log.info("Creating Video entity for VideoGeneration ID: {} with URL: {}", 
                videoGeneration.getId(), videoUrl);
        
        Video video = new Video();
        video.setVideoUrl(videoUrl);
        video.setStore(videoGeneration.getStore());
        video.setViews(0L);
        
        Video savedVideo = videoRepository.save(video);
        log.info("Created Video entity with ID: {} for VideoGeneration ID: {}", 
                savedVideo.getId(), videoGeneration.getId());
        
        return savedVideo;
    }

    private NestJSVideoRequest mapToNestJSRequest(CreateVideoGenerationRequest request) {
        Store store = storeService.getStore(request.getStoreId());
        // Extract URLs from the request
        List<String> imageUrls = request.getImages().stream()
                .map(ImageRequestDto::getImageUrl)
                .toList();
        
        String videoUrl = request.getVideos().isEmpty() ? "" : request.getVideos().get(0).getVideoUrl();
        
        // NestJS expects exactly 3 images, so pad with first image if needed
        String image1Url = imageUrls.size() > 0 ? imageUrls.get(0) : "";
        String image2Url = imageUrls.size() > 1 ? imageUrls.get(1) : image1Url;
        String image3Url = imageUrls.size() > 2 ? imageUrls.get(2) : image1Url;
        
        return NestJSVideoRequest.builder()
                .image1Url(image1Url)
                .image2Url(image2Url)
                .image3Url(image3Url)
                .videoUrl(videoUrl)
                .text(request.getText())
                .store(store.toString())
                .build();
    }

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
                    Video video = createVideoFromGeneration(generation, response.getUrl());
                    
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
