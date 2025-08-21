package com.ht.htserver.home.controller;

import com.ht.htserver.auth.service.JwtService;
import com.ht.htserver.home.dto.response.HomeResponse;
import com.ht.htserver.home.service.HomeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomeController {

    private final HomeService homeService;
    private final JwtService jwtService;

    @GetMapping
    public HomeResponse getHomeData(
            HttpServletRequest httpServletRequest
    ) {
        UUID userId = jwtService.getUserIdFromRequest(httpServletRequest);

        return homeService.getHomeData(userId);
    }
}
