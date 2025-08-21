package com.ht.htserver.video.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@Schema(description = "영상 생성 요청 응답")
public class CreateVideoGenerationResponse {
    @Schema(description = "영상 생성 요청 ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID videoGenerationId;
}
