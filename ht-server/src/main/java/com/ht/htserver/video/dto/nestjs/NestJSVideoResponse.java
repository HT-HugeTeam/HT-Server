package com.ht.htserver.video.dto.nestjs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NestJSVideoResponse {
    private String id;
    private String status;
    private String url;
    
    @JsonProperty("template_id")
    private String templateId;
    
    @JsonProperty("template_name")
    private String templateName;
    
    @JsonProperty("template_tags")
    private List<String> templateTags;
    
    @JsonProperty("output_format")
    private String outputFormat;
    
    private Map<String, Object> modifications;
}