package com.ht.htserver.store.controller;

import com.ht.htserver.auth.service.JwtService;
import com.ht.htserver.store.dto.request.CreateStoreRequest;
import com.ht.htserver.store.dto.response.CreateStoreResponse;
import com.ht.htserver.store.dto.response.StoreResponse;
import com.ht.htserver.store.entity.Store;
import com.ht.htserver.store.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
@Tag(name = "Store", description = "가게 관리 API")
public class StoreController {

    private final StoreService storeService;
    private final JwtService jwtService;

    @PostMapping
    @Operation(summary = "가게 생성", description = "새로운 가게를 생성합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "가게 생성 성공",
                content = @Content(schema = @Schema(implementation = CreateStoreResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 본문", content = @Content),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content)
    })
    public ResponseEntity<CreateStoreResponse> createStore(
            HttpServletRequest httpServletRequest,
            @Valid @RequestBody CreateStoreRequest createStoreRequest
            ) {
        UUID userId = jwtService.getUserIdFromRequest(httpServletRequest);

        Store store = storeService.createStore(createStoreRequest, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(CreateStoreResponse.toDto(store));
    }

    @GetMapping("/{store_id}")
    public ResponseEntity<StoreResponse> getStore(
            @PathVariable(name = "store_id") UUID storeId
    ) {
        Store store = storeService.getStore(storeId);
        return ResponseEntity.ok(StoreResponse.toDto(store));
    }
}
