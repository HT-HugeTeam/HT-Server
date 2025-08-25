# 📋 Comprehensive API Logging System

## 🎯 Overview
This document describes the comprehensive logging system implemented for the HT Server Spring Boot application. The system provides detailed, structured logging for all API calls, external service interactions, and internal operations to facilitate debugging and monitoring.

## 🏗️ Architecture

### 1. **Configuration Layer** (`application.yaml`)
- **Structured Logging**: Console and file output with consistent formatting
- **Log Levels**: 
  - `com.ht.htserver: DEBUG` - Application-specific detailed logging
  - `org.springframework.web.reactive.function.client: DEBUG` - External API call logging
  - `org.hibernate: DEBUG/TRACE` - Database query logging
- **File Logging**: 
  - Location: `logs/ht-server.log`
  - Rotation: 100MB max size, 30 days retention
- **Management Endpoints**: Health, metrics, and logger management enabled

### 2. **Request/Response Interceptor** (`ApiLoggingInterceptor`)
- **Automatic Request Tracking**: Every API call gets a unique request ID
- **Performance Monitoring**: Request duration tracking with performance classification
- **Security**: Sensitive headers (Authorization, passwords, tokens) are automatically redacted
- **Error Context**: Detailed error information with meaningful error messages
- **Emoji Indicators**: Visual status indicators for quick log scanning

### 3. **External API Logging** (Enhanced Services)
- **Kakao OAuth API**: Complete token exchange and user info retrieval logging
- **NestJS Video Service**: Video generation request/response tracking with retry logic
- **WebClient Integration**: Request/response timing, error handling, and payload inspection

## 📊 Logging Features

### 🎪 **Visual Indicators**
- 🚀 **API REQUEST START**: Beginning of API request processing
- ✅ **Success Operations**: Successful completions with timing
- ❌ **Errors**: Failed operations with detailed error context
- 🔄 **Retries**: Retry attempts with reasons and timing
- ⚠️ **Warnings**: Slow requests and potential issues
- 🔐 **Authentication**: OAuth and JWT operations
- 🌐 **External APIs**: Calls to external services
- 💾 **Database**: Database operations and queries
- 📊 **Performance**: Request timing and performance classification

### 📈 **Performance Monitoring**
- **EXCELLENT**: < 100ms
- **GOOD**: < 500ms  
- **FAIR**: < 1000ms
- **SLOW**: < 3000ms
- **VERY_SLOW**: ≥ 3000ms

### 🔒 **Security Features**
- **Header Redaction**: Sensitive headers automatically masked
- **Token Safety**: Access tokens and secrets shown only as length/presence
- **IP Tracking**: Client IP address resolution (X-Forwarded-For support)
- **Request Correlation**: Unique request IDs for tracing

## 📝 Log Examples

### Successful API Request
```
2025-01-15 14:23:45.123 [http-nio-8080-exec-1] INFO  [c.h.h.config.ApiLoggingInterceptor] [abc12345,-] - 🚀 API REQUEST START: {"requestId":"abc12345","timestamp":"2025-01-15 14:23:45.123","method":"POST","uri":"/auth/kakao-login","remoteAddr":"192.168.1.100","userAgent":"Mozilla/5.0..."}

2025-01-15 14:23:45.856 [http-nio-8080-exec-1] INFO  [c.h.h.config.ApiLoggingInterceptor] [abc12345,-] - ✅ API REQUEST COMPLETED: {"requestId":"abc12345","status":200,"durationMs":733,"performance":"GOOD","success":true}
```

### External API Call
```
2025-01-15 14:23:45.200 [http-nio-8080-exec-1] INFO  [c.h.h.auth.service.KakaoApiService] [abc12345,-] - 🌐 Starting Kakao access token exchange
2025-01-15 14:23:45.450 [http-nio-8080-exec-1] INFO  [c.h.h.auth.service.KakaoApiService] [abc12345,-] - ✅ Kakao token response received in 250ms
```

### Error Scenario
```
2025-01-15 14:23:45.500 [http-nio-8080-exec-1] ERROR [c.h.h.auth.service.KakaoApiService] [abc12345,-] - ❌ Kakao token request failed after 300ms: 400 Bad Request
2025-01-15 14:23:45.501 [http-nio-8080-exec-1] ERROR [c.h.h.auth.service.KakaoApiService] [abc12345,-] - 🔴 Bad Request (400) - Invalid authorization code or client credentials
```

## 🔧 Usage for Debugging

### 1. **Finding Request Flow**
Use the request ID to trace the complete flow:
```bash
grep "abc12345" logs/ht-server.log
```

### 2. **Performance Analysis**
Find slow requests:
```bash
grep "SLOW\|VERY_SLOW" logs/ht-server.log
```

### 3. **Error Analysis**
Find all errors in the last hour:
```bash
grep "ERROR" logs/ht-server.log | grep "$(date -d '1 hour ago' '+%Y-%m-%d %H')"
```

### 4. **External API Issues**
Monitor external service health:
```bash
grep "🌐\|❌.*API" logs/ht-server.log
```

## 🎛️ Configuration Management

### Runtime Log Level Changes
```bash
# Change log level without restart
curl -X POST http://localhost:30001/actuator/loggers/com.ht.htserver \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel": "INFO"}'

# Check current log levels
curl http://localhost:30001/actuator/loggers
```

### Environment-Specific Settings
Override log levels via environment variables:
```bash
LOGGING_LEVEL_COM_HT_HTSERVER=INFO
LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_WEB_REACTIVE=WARN
```

## 📋 Best Practices

### For Developers
1. **Use Request IDs**: Always include request IDs when investigating issues
2. **Monitor Performance**: Watch for SLOW/VERY_SLOW classifications
3. **Check External APIs**: Monitor external service response times
4. **Review Security**: Ensure no sensitive data appears in logs

### For Operations
1. **Set Log Rotation**: Configure appropriate retention policies
2. **Monitor Disk Space**: Logs can grow large with DEBUG level
3. **Use Log Aggregation**: Consider ELK stack or similar for production
4. **Set Alerts**: Create alerts for ERROR patterns and slow requests

## 🚀 Future Enhancements
- **Distributed Tracing**: Add Spring Cloud Sleuth for microservice tracing
- **Metrics Integration**: Integrate with Micrometer for application metrics
- **Log Aggregation**: Configure for centralized logging systems
- **Alert Integration**: Add webhook notifications for critical errors