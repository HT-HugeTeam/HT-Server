package com.ht.htserver.video.dto.creatomate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreatomateRenderResponse {
    private String id;
    private String status; // "queued", "processing", "succeeded", "failed"
    private String url;
    private String errorMessage;
    private Integer progress; // 0-100
    private Long createdAt;
    private Long updatedAt;
}