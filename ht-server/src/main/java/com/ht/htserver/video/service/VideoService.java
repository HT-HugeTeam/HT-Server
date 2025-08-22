package com.ht.htserver.video.service;

import com.ht.htserver.store.entity.Store;
import com.ht.htserver.store.service.StoreService;
import com.ht.htserver.video.dto.request.CreateVideoGenerationRequest;
import com.ht.htserver.video.dto.request.ImageRequestDto;
import com.ht.htserver.video.dto.request.VideoRequestDto;
import com.ht.htserver.video.entity.*;
import com.ht.htserver.video.exception.VideoNotFoundException;
import com.ht.htserver.video.exception.VideoGenerationNotFoundException;
import com.ht.htserver.video.repository.VideoGenerationRepository;
import com.ht.htserver.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoGenerationRepository videoGenerationRepository;
    private final StoreService storeService;
    private final VideoRepository videoRepository;

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

        // Call the ai server with the provided images, videos, text, store info

        return videoGenerationRepository.save(videoGeneration);
    }

    @Transactional(readOnly = true)
    public VideoGeneration getVideoGenerationStatus(UUID videoGenerationId) {

        // call the creatomate api with the video generation id, check what status it's in currently.
        // update the database with the returned info

        return videoGenerationRepository.findById(videoGenerationId)
                .orElseThrow(VideoGenerationNotFoundException::new);
    }
}
