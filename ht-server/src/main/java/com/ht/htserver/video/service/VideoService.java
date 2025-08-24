package com.ht.htserver.video.service;

import com.ht.htserver.store.entity.Store;
import com.ht.htserver.store.service.StoreService;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoService {

    private final VideoGenerationRepository videoGenerationRepository;
    private final StoreService storeService;
    private final VideoRepository videoRepository;
    private final VideoGenerationApiService videoGenerationApiService;
    private final VideoStatusCheckService videoStatusCheckService;

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
            videoStatusCheckService.checkVideoGenerationStatus(videoGeneration.getId());
            
            // Refetch the potentially updated entity
            videoGeneration = videoGenerationRepository.findById(videoGenerationId)
                    .orElseThrow(VideoGenerationNotFoundException::new);
        }

        return videoGeneration;
    }

    private NestJSVideoRequest mapToNestJSRequest(CreateVideoGenerationRequest request) {
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
                .build();
    }
}
