package com.ht.htserver.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApiLoggingInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;
    
    private static final String START_TIME_ATTR = "startTime";
    private static final String REQUEST_ID_ATTR = "requestId";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        
        // Set MDC for trace logging
        MDC.put("requestId", requestId);
        
        request.setAttribute(START_TIME_ATTR, startTime);
        request.setAttribute(REQUEST_ID_ATTR, requestId);
        
        // Skip logging for actuator endpoints
        if (request.getRequestURI().startsWith("/actuator/")) {
            return true;
        }
        
        logIncomingRequest(request, handler);
        return true;
    }
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // This method can be used for additional post-processing if needed
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        try {
            // Skip logging for actuator endpoints
            if (request.getRequestURI().startsWith("/actuator/")) {
                return;
            }
            
            logOutgoingResponse(request, response, handler, ex);
        } finally {
            // Clear MDC
            MDC.clear();
        }
    }
    
    private void logIncomingRequest(HttpServletRequest request, Object handler) {
        try {
            String requestId = (String) request.getAttribute(REQUEST_ID_ATTR);
            
            Map<String, Object> requestInfo = new HashMap<>();
            requestInfo.put("requestId", requestId);
            requestInfo.put("timestamp", LocalDateTime.now().format(DATE_FORMATTER));
            requestInfo.put("method", request.getMethod());
            requestInfo.put("uri", request.getRequestURI());
            requestInfo.put("queryString", request.getQueryString());
            requestInfo.put("remoteAddr", getClientIpAddress(request));
            requestInfo.put("userAgent", request.getHeader("User-Agent"));
            
            // Add controller and method info if available
            if (handler instanceof HandlerMethod) {
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                Method method = handlerMethod.getMethod();
                requestInfo.put("controller", method.getDeclaringClass().getSimpleName());
                requestInfo.put("action", method.getName());
            }
            
            // Add headers (excluding sensitive ones)
            Map<String, String> headers = getSafeHeaders(request);
            requestInfo.put("headers", headers);
            
            log.info("🚀 API REQUEST START: {}", objectMapper.writeValueAsString(requestInfo));
            
        } catch (Exception e) {
            log.warn("Failed to log incoming request", e);
        }
    }
    
    private void logOutgoingResponse(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        try {
            Long startTime = (Long) request.getAttribute(START_TIME_ATTR);
            String requestId = (String) request.getAttribute(REQUEST_ID_ATTR);
            long duration = startTime != null ? System.currentTimeMillis() - startTime : 0;
            
            Map<String, Object> responseInfo = new HashMap<>();
            responseInfo.put("requestId", requestId);
            responseInfo.put("timestamp", LocalDateTime.now().format(DATE_FORMATTER));
            responseInfo.put("method", request.getMethod());
            responseInfo.put("uri", request.getRequestURI());
            responseInfo.put("status", response.getStatus());
            responseInfo.put("durationMs", duration);
            responseInfo.put("success", ex == null && response.getStatus() < 400);
            
            // Add controller and method info if available
            if (handler instanceof HandlerMethod) {
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                Method method = handlerMethod.getMethod();
                responseInfo.put("controller", method.getDeclaringClass().getSimpleName());
                responseInfo.put("action", method.getName());
            }
            
            // Add exception info if present
            if (ex != null) {
                responseInfo.put("exception", ex.getClass().getSimpleName());
                responseInfo.put("errorMessage", ex.getMessage());
            }
            
            // Performance classification
            String performanceLevel = getPerformanceLevel(duration);
            responseInfo.put("performance", performanceLevel);
            
            String logLevel = response.getStatus() >= 400 || ex != null ? "ERROR" : "INFO";
            String emoji = getStatusEmoji(response.getStatus(), ex != null);
            
            if ("ERROR".equals(logLevel)) {
                log.error("{} API REQUEST COMPLETED: {}", emoji, objectMapper.writeValueAsString(responseInfo));
            } else {
                log.info("{} API REQUEST COMPLETED: {}", emoji, objectMapper.writeValueAsString(responseInfo));
            }
            
            // Log performance warning for slow requests
            if (duration > 5000) {
                log.warn("⚠️ SLOW API REQUEST DETECTED: {} took {}ms", request.getRequestURI(), duration);
            }
            
        } catch (Exception e) {
            log.warn("Failed to log outgoing response", e);
        }
    }
    
    private Map<String, String> getSafeHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            
            // Exclude sensitive headers
            if (!isSensitiveHeader(headerName)) {
                headers.put(headerName, headerValue);
            } else {
                headers.put(headerName, "***REDACTED***");
            }
        }
        
        return headers;
    }
    
    private boolean isSensitiveHeader(String headerName) {
        String lowerCaseName = headerName.toLowerCase();
        return lowerCaseName.contains("authorization") || 
               lowerCaseName.contains("password") ||
               lowerCaseName.contains("token") ||
               lowerCaseName.contains("secret") ||
               lowerCaseName.contains("key");
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIP = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(xRealIP)) {
            return xRealIP;
        }
        
        return request.getRemoteAddr();
    }
    
    private String getPerformanceLevel(long duration) {
        if (duration < 100) return "EXCELLENT";
        if (duration < 500) return "GOOD";
        if (duration < 1000) return "FAIR";
        if (duration < 3000) return "SLOW";
        return "VERY_SLOW";
    }
    
    private String getStatusEmoji(int status, boolean hasException) {
        if (hasException) return "💥";
        if (status >= 500) return "🔥";
        if (status >= 400) return "⚠️";
        if (status >= 300) return "↩️";
        return "✅";
    }
}