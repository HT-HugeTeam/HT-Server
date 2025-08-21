package com.ht.htserver.video.controller;

import com.ht.htserver.video.dto.request.CreateVideoGenerationRequest;
import com.ht.htserver.video.dto.response.CreateVideoGenerationResponse;
import com.ht.htserver.video.dto.response.VideoGenerationStatusResponse;
import com.ht.htserver.video.dto.response.VideoResponse;
import com.ht.htserver.video.entity.Video;
import com.ht.htserver.video.entity.VideoGeneration;
import com.ht.htserver.video.exception.VideoNotFoundException;
import com.ht.htserver.video.repository.VideoRepository;
import com.ht.htserver.video.service.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/videos")
@RequiredArgsConstructor
@Tag(name = "Video", description = "영상 관리 API")
public class VideoController {
    private final VideoService videoService;

    @GetMapping("/{video_id}")
    @Operation(summary = "영상 조회", description = "영상 ID로 영상 정보를 조회합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "영상 조회 성공",
                content = @Content(schema = @Schema(implementation = VideoResponse.class))),
        @ApiResponse(responseCode = "404", description = "영상을 찾을 수 없음", content = @Content)
    })
    public ResponseEntity<VideoResponse> getVideo(
            @PathVariable(name = "video_id") UUID videoId
    ) {
        Video video = videoService.getVideo(videoId);

        return ResponseEntity.ok(VideoResponse.toDto(video));
    }

    @DeleteMapping("/{video_id}")
    @Operation(summary = "영상 삭제", description = "영상 ID로 영상을 삭제합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "영상 삭제 성공", content = @Content),
        @ApiResponse(responseCode = "404", description = "영상을 찾을 수 없음", content = @Content)
    })
    public ResponseEntity<Void> deleteVideo(
            @PathVariable(name = "video_id") UUID videoId
            ) {
        videoService.deleteVideo(videoId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/generations")
    @Operation(summary = "영상 생성 요청", description = "새로운 영상 생성을 요청합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "영상 생성 요청 성공",
                content = @Content(schema = @Schema(implementation = CreateVideoGenerationResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 본문", content = @Content),
        @ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음", content = @Content)
    })
    public ResponseEntity<CreateVideoGenerationResponse> createVideoGeneration(
            @Valid @RequestBody CreateVideoGenerationRequest request
            ){
        VideoGeneration videoGeneration = videoService.createVideoGeneration(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(CreateVideoGenerationResponse.builder().videoGenerationId(videoGeneration.getId()).build());
    }

    @GetMapping("/generations/{video_generation_id}")
    @Operation(summary = "영상 생성 상태 조회", description = "영상 생성 ID로 생성 상태를 조회합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "영상 생성 상태 조회 성공",
                content = @Content(schema = @Schema(implementation = VideoGenerationStatusResponse.class))),
        @ApiResponse(responseCode = "404", description = "영상 생성 요청을 찾을 수 없음", content = @Content)
    })
    public ResponseEntity<VideoGenerationStatusResponse> getVideoGenerationStatus(
            @PathVariable(name = "video_generation_id") UUID videoGenerationId
    ) {
        VideoGeneration videoGeneration = videoService.getVideoGenerationStatus(videoGenerationId);
        
        return ResponseEntity.ok(VideoGenerationStatusResponse.builder()
                .videoGenerationId(videoGeneration.getId())
                .status(videoGeneration.getStatus())
                .createdAt(videoGeneration.getCreatedAt())
                .build());
    }
}
