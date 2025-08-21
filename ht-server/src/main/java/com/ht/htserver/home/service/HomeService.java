package com.ht.htserver.home.service;

import com.ht.htserver.home.dto.response.HomeResponse;
import com.ht.htserver.video.entity.Video;
import com.ht.htserver.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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

        return HomeResponse.builder().videos(videos).mostRecentViews(mostRecentViews).build();
    }
}
