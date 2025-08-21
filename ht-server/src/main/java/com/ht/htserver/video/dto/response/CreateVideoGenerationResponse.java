package com.ht.htserver.video.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CreateVideoGenerationResponse {

    private UUID videoGenerationId;
}
