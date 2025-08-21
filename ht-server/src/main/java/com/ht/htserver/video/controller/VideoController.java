package com.ht.htserver.video.controller;

import com.ht.htserver.video.dto.request.CreateVideoGenerationRequest;
import com.ht.htserver.video.dto.response.CreateVideoGenerationResponse;
import com.ht.htserver.video.entity.VideoGeneration;
import com.ht.htserver.video.service.VideoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class VideoController {
    private final VideoService videoService;

//    @DeleteMapping("/videos/{video_id}")
//    public ResponseEntity<Void> deleteVideo(
//            @PathVariable(name = "video_id") UUID videoId
//            ) {
//        return;
//    }


    @PostMapping("/video-generations")
    public ResponseEntity<CreateVideoGenerationResponse> createVideoGeneration(
            @Valid @RequestBody CreateVideoGenerationRequest request
            ){
        VideoGeneration videoGeneration = videoService.createVideoGeneration(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(CreateVideoGenerationResponse.builder().videoGenerationId(videoGeneration.getId()).build());
    }
}
