package com.ht.htserver.video.dto.request;

import com.ht.htserver.store.entity.Store;
import com.ht.htserver.video.entity.ImageRequest;
import com.ht.htserver.video.entity.VideoRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class CreateVideoGenerationRequest {

    @NotBlank
    private String text;

    @NotNull
    private List<ImageRequest> images;

    @NotNull
    private List<VideoRequest> videos;

    @NotNull
    @org.hibernate.validator.constraints.UUID
    private UUID storeId;
}
