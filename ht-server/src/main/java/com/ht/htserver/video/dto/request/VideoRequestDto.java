package com.ht.htserver.video.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "비디오 요청 정보")
public class VideoRequestDto {
    @Schema(description = "비디오 URL", example = "https://example.com/video.mp4")
    @NotBlank(message = "영상 URL은 필수입니다.")
    private String videoUrl;
}