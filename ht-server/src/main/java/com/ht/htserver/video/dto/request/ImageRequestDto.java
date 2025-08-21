package com.ht.htserver.video.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "이미지 요청 정보")
public class ImageRequestDto {
    @Schema(description = "이미지 이름", example = "맛있는 떡볶이.jpg")
    @NotBlank(message = "이미지 이름은 필수입니다.")
    private String name;

    @Schema(description = "이미지 URL", example = "https://example.com/image.jpg")
    @NotBlank(message = "이미지 URL은 필수입니다.")
    private String imageUrl;
}