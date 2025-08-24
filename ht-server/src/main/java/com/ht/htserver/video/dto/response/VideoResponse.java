package com.ht.htserver.video.dto.response;

import com.ht.htserver.video.entity.Video;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Schema(description = "영상 정보 응답")
public class VideoResponse {
    @Schema(description = "영상 ID", example = "ef646d62-3eaf-4550-ac9c-01383bf0dd8c")
    private UUID id;

    @Schema(description = "영상 URL", example = "https://example.com/video.mp4")
    private String videoUrl;
    
    @Schema(description = "가게 주소", example = "서울시 강남구 역삼동 123")
    private String address;

    @Schema(description = "가게 이름", example = "맛있는 식당")
    private String storeName;

    @Schema(description = "조회수", example = "150")
    private Long views;

    @Schema(description = "영상 생성 시간", example = "2025-08-01T10:30:00")
    private LocalDateTime createdAt;

    public static VideoResponse toDto(Video video) {
        return VideoResponse.builder()
                .id(video.getId())
                .videoUrl(video.getVideoUrl())
                .address(video.getStore().getAddress())
                .storeName(video.getStore().getName())
                .views(video.getViews())
                .createdAt(video.getCreatedAt())
                .build();
    }
}
