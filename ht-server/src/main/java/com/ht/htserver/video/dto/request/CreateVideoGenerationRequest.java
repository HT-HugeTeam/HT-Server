package com.ht.htserver.video.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Schema(description = "영상 생성 요청")
public class CreateVideoGenerationRequest {

    @Schema(description = "영상에 대한 상세 설명", example = "맛있는 떡볶이를 만드는 과정을 보여주는 영상")
    @NotBlank(message = "상세 설명은 필수입니다.")
    private String text;

    @Schema(description = "사용할 이미지 목록")
    @NotEmpty(message = "사진은 필수입니다.")
    @Valid
    private List<ImageRequestDto> images;

    @Schema(description = "사용할 비디오 목록")
    @NotEmpty(message = "영상은 필수입니다.")
    @Valid
    private List<VideoRequestDto> videos;

    @Schema(description = "가게 ID", example = "123e4567-e89b-12d3-a456-426614174000")
    @NotNull(message = "가게 id는 필수입니다.")
    private UUID storeId;
}
