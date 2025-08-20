package com.ht.htserver.store.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class UpdateStoreRequest {
    @NotBlank(message = "가게 이름은 필수입니다.")
    private String name;

    @NotBlank(message = "가게 주소는 필수입니다.")
    private String address;

    @NotBlank(message = "가게 설명은 필수입니다.")
    private String description;

    @NotBlank(message = "가게 네이버 지도 링크는 필수입니다.")
    @URL
    private String naverUrl;
}