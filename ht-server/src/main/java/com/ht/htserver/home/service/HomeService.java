package com.ht.htserver.home.service;

import com.ht.htserver.home.dto.response.HomeResponse;
import com.ht.htserver.video.dto.response.VideoResponse;
import com.ht.htserver.video.entity.Video;
import com.ht.htserver.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final VideoService videoService;

    @Transactional(readOnly = true)
    public HomeResponse getHomeData(UUID userId){
        List<Video> videos = videoService.getVideosByUserId(userId);

        Long mostRecentViews = 0L;
        if(videos.stream().findFirst().isPresent()){
            mostRecentViews = videos.stream().findFirst().get().getViews();
        }

        List<VideoResponse> videoResponses = videos.stream()
                .map(VideoResponse::toDto)
                .collect(Collectors.toList());

        return HomeResponse.builder().videos(videoResponses).mostRecentViews(mostRecentViews).build();
    }
}
