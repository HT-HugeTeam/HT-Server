package com.ht.htserver.home.dto.response;

import com.ht.htserver.video.dto.response.VideoResponse;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class HomeResponse {
    @Builder.Default
    private List<VideoResponse> videos = new ArrayList<>();

    private Long mostRecentViews;
}
