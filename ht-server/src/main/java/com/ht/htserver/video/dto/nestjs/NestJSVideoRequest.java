package com.ht.htserver.video.dto.nestjs;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NestJSVideoRequest {
    private String image1Url;
    private String image2Url;
    private String image3Url;
    private String videoUrl;
    private String text;
}