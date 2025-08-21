package com.ht.htserver.video.service;

import com.ht.htserver.store.entity.Store;
import com.ht.htserver.store.repository.StoreRepository;
import com.ht.htserver.store.service.StoreService;
import com.ht.htserver.video.dto.request.CreateVideoGenerationRequest;
import com.ht.htserver.video.entity.ImageRequest;
import com.ht.htserver.video.entity.VideoGeneration;
import com.ht.htserver.video.entity.VideoRequest;
import com.ht.htserver.video.repository.VideoGenerationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoGenerationRepository videoGenerationRepository;
    private final StoreService storeService;

    @Transactional
    public VideoGeneration createVideoGeneration(CreateVideoGenerationRequest request) {
        Store store = storeService.getStore(request.getStoreId());

        VideoGeneration videoGeneration = VideoGeneration.builder()
                .text(request.getText())
                .store(store).build();

        request.getVideos().forEach(videoGeneration::addVideo);
        request.getImages().forEach(videoGeneration::addImage);

        return videoGenerationRepository.save(videoGeneration);
    }
}
