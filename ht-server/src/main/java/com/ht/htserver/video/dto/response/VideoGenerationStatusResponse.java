package com.ht.htserver.video.dto.response;

import com.ht.htserver.video.entity.VideoGenerationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Schema(description = "영상 생성 상태 응답")
public class VideoGenerationStatusResponse {
    @Schema(description = "영상 생성 요청 ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID videoGenerationId;

    @Schema(description = "영상 생성 상태", example = "IN_PROGRESS")
    private VideoGenerationStatus status;

    @Schema(description = "요청 생성 시간", example = "2025-08-01T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "생성된 영상 URL (완료 시에만 제공)", example = "https://f002.backblazeb2.com/file/creatomate-c8xg3hsxdu/video-id.mp4")
    private String generatedVideoUrl;

    @Schema(description = "생성된 영상 정보 (완료 시에만 제공)")
    private VideoResponse video;

    @Schema(description = "오류 메시지 (실패 시에만 제공)", example = "Video generation failed due to invalid input")
    private String errorMessage;
}
