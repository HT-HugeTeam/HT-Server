package com.ht.htserver.home.controller;

import com.ht.htserver.auth.service.JwtService;
import com.ht.htserver.home.dto.response.HomeResponse;
import com.ht.htserver.home.service.HomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
@Tag(name = "Home", description = "홈 화면 API")
@Slf4j
public class HomeController {

    private final HomeService homeService;
    private final JwtService jwtService;

    @GetMapping
    @Operation(summary = "홈 화면 데이터 조회", description = "인증된 사용자의 홈 화면 데이터를 조회합니다")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "홈 화면 데이터 조회 성공",
                content = @Content(schema = @Schema(implementation = HomeResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증 실패 - 유효하지 않거나 누락된 JWT 토큰", content = @Content),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
    })
    public HomeResponse getHomeData(
            HttpServletRequest httpServletRequest
    ) {
        log.info("🏠 Fetching home data for authenticated user");
        long startTime = System.currentTimeMillis();
        
        try {
            log.debug("🔍 Extracting user ID from JWT token");
            UUID userId = jwtService.getUserIdFromRequest(httpServletRequest);
            log.debug("👤 User ID extracted: {}", userId);
            
            log.info("📊 Retrieving home data for user: {}", userId);
            HomeResponse homeData = homeService.getHomeData(userId);
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("✅ Home data retrieved successfully in {}ms", duration);
            log.debug("📤 Home response prepared with data: {}", 
                     homeData != null ? "Present" : "Null");
            
            return homeData;
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("❌ Failed to fetch home data after {}ms: {}", duration, e.getMessage(), e);
            throw e;
        }
    }
}
