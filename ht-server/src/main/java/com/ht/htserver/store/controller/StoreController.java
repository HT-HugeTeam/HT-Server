package com.ht.htserver.store.controller;

import com.ht.htserver.auth.service.JwtService;
import com.ht.htserver.store.dto.request.CreateStoreRequest;
import com.ht.htserver.store.dto.request.UpdateStoreRequest;
import com.ht.htserver.store.dto.response.CreateStoreResponse;
import com.ht.htserver.store.dto.response.StoreResponse;
import com.ht.htserver.store.entity.Store;
import com.ht.htserver.store.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
@Tag(name = "Store", description = "가게 관리 API")
@SecurityRequirement(name = "Bearer Authentication")
public class StoreController {

    private final StoreService storeService;
    private final JwtService jwtService;


    @GetMapping
    @Operation(summary = "사용자의 가게 목록 조회", description = "현재 인증된 사용자가 소유한 가게 목록을 조회합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "가게 목록 조회 성공",
                content = @Content(schema = @Schema(implementation = StoreResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content)
    })
    public ResponseEntity<List<StoreResponse>> getUserStores(
            HttpServletRequest httpServletRequest
    ) {
        UUID userId = jwtService.getUserIdFromRequest(httpServletRequest);

        List<Store> stores = storeService.getUserStores(userId);

        return ResponseEntity.ok(stores.stream().map((StoreResponse::toDto)).toList());
    }

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
    @Operation(summary = "가게 정보 조회", description = "가게 ID로 가게 정보를 조회합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "가게 조회 성공",
                content = @Content(schema = @Schema(implementation = StoreResponse.class))),
        @ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음", content = @Content)
    })
    public ResponseEntity<StoreResponse> getStore(
            @PathVariable(name = "store_id") UUID storeId
    ) {
        Store store = storeService.getStore(storeId);
        return ResponseEntity.ok(StoreResponse.toDto(store));
    }

    @PutMapping("/{store_id}")
    @Operation(summary = "가게 정보 수정", description = "기존 가게의 정보를 수정합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "가게 수정 성공",
                content = @Content(schema = @Schema(implementation = StoreResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 본문", content = @Content),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
        @ApiResponse(responseCode = "403", description = "가게 수정 권한 없음", content = @Content),
        @ApiResponse(responseCode = "404", description = "가게를 찾을 수 없음", content = @Content)
    })
    public ResponseEntity<StoreResponse> updateStore(
            HttpServletRequest httpServletRequest,
            @PathVariable(name = "store_id") UUID storeId,
            @Valid @RequestBody UpdateStoreRequest updateStoreRequest
    ) {
        UUID userId = jwtService.getUserIdFromRequest(httpServletRequest);
        
        Store store = storeService.updateStore(storeId, updateStoreRequest, userId);
        
        return ResponseEntity.ok(StoreResponse.toDto(store));
    }
}
