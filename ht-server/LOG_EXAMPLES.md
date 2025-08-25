# 📋 Production Log Examples

## 🎯 Real-World Log Examples

This document shows actual log outputs you'll see after deploying the enhanced logging system.

---

## 1. 🔐 **Successful Kakao OAuth Login Flow**

```log
2025-01-25 09:15:23.445 [http-nio-30001-exec-1] INFO  [c.h.h.config.ApiLoggingInterceptor] [req_7f2a1b3c,-] - 🚀 API REQUEST START: {"requestId":"req_7f2a1b3c","timestamp":"2025-01-25 09:15:23.445","method":"POST","uri":"/auth/kakao-login","queryString":null,"remoteAddr":"203.104.25.12","userAgent":"Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X)","controller":"AuthController","action":"kakaoLogin","headers":{"content-type":"application/json","accept":"application/json","user-agent":"Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X)","authorization":"***REDACTED***"}}

2025-01-25 09:15:23.450 [http-nio-30001-exec-1] INFO  [c.h.h.auth.controller.AuthController] [req_7f2a1b3c,-] - 🔐 Starting Kakao OAuth login process

2025-01-25 09:15:23.452 [http-nio-30001-exec-1] DEBUG [c.h.h.auth.controller.AuthController] [req_7f2a1b3c,-] - 📋 Kakao login request received with authorization code length: 32

2025-01-25 09:15:23.455 [http-nio-30001-exec-1] INFO  [c.h.h.auth.controller.AuthController] [req_7f2a1b3c,-] - 🎯 Processing Kakao authorization code authentication

2025-01-25 09:15:23.458 [http-nio-30001-exec-1] INFO  [c.h.h.auth.service.AuthService] [req_7f2a1b3c,-] - 🔐 Starting Kakao OAuth authentication process

2025-01-25 09:15:23.460 [http-nio-30001-exec-1] DEBUG [c.h.h.auth.service.AuthService] [req_7f2a1b3c,-] - 📋 Authorization code received with length: 32

2025-01-25 09:15:23.465 [http-nio-30001-exec-1] INFO  [c.h.h.auth.service.AuthService] [req_7f2a1b3c,-] - 🎫 Exchanging authorization code for Kakao access token

2025-01-25 09:15:23.468 [http-nio-30001-exec-1] INFO  [c.h.h.auth.service.KakaoApiService] [req_7f2a1b3c,-] - 🌐 Starting Kakao access token exchange

2025-01-25 09:15:23.470 [http-nio-30001-exec-1] DEBUG [c.h.h.auth.service.KakaoApiService] [req_7f2a1b3c,-] - 📝 Request URL: https://kauth.kakao.com/oauth/token

2025-01-25 09:15:23.472 [http-nio-30001-exec-1] DEBUG [c.h.h.auth.service.KakaoApiService] [req_7f2a1b3c,-] - 🔑 Client ID: a1b2***

2025-01-25 09:15:23.475 [http-nio-30001-exec-1] DEBUG [c.h.h.auth.service.KakaoApiService] [req_7f2a1b3c,-] - 📋 Authorization code length: 32

2025-01-25 09:15:23.478 [http-nio-30001-exec-1] DEBUG [c.h.h.auth.service.KakaoApiService] [req_7f2a1b3c,-] - 📤 Sending POST request to Kakao token endpoint

2025-01-25 09:15:23.480 [http-nio-30001-exec-1] DEBUG [c.h.h.auth.service.KakaoApiService] [req_7f2a1b3c,-] - 🚀 WebClient request subscribed

2025-01-25 09:15:23.723 [reactor-http-nio-2] INFO  [c.h.h.auth.service.KakaoApiService] [req_7f2a1b3c,-] - ✅ Kakao token response received in 245ms

2025-01-25 09:15:23.725 [reactor-http-nio-2] DEBUG [c.h.h.auth.service.KakaoApiService] [req_7f2a1b3c,-] - 📨 Access token length: 147

2025-01-25 09:15:23.727 [reactor-http-nio-2] DEBUG [c.h.h.auth.service.KakaoApiService] [req_7f2a1b3c,-] - ⏰ Token expires in: 7200 seconds

2025-01-25 09:15:23.729 [reactor-http-nio-2] DEBUG [c.h.h.auth.service.KakaoApiService] [req_7f2a1b3c,-] - 🔄 Refresh token length: 128

2025-01-25 09:15:23.735 [http-nio-30001-exec-1] INFO  [c.h.h.auth.service.KakaoApiService] [req_7f2a1b3c,-] - 🎉 Kakao access token exchange completed successfully in 257ms

2025-01-25 09:15:23.740 [http-nio-30001-exec-1] INFO  [c.h.h.auth.service.AuthService] [req_7f2a1b3c,-] - ✅ Kakao access token obtained in 275ms

2025-01-25 09:15:23.745 [http-nio-30001-exec-1] INFO  [c.h.h.auth.service.AuthService] [req_7f2a1b3c,-] - 👤 Fetching user information from Kakao API

2025-01-25 09:15:23.748 [http-nio-30001-exec-1] INFO  [c.h.h.auth.service.KakaoApiService] [req_7f2a1b3c,-] - 🌐 Starting Kakao user info retrieval

2025-01-25 09:15:23.750 [http-nio-30001-exec-1] DEBUG [c.h.h.auth.service.KakaoApiService] [req_7f2a1b3c,-] - 📝 Request URL: https://kapi.kakao.com/v2/user/me

2025-01-25 09:15:23.752 [http-nio-30001-exec-1] DEBUG [c.h.h.auth.service.KakaoApiService] [req_7f2a1b3c,-] - 🎫 Access token length: 147

2025-01-25 09:15:23.892 [reactor-http-nio-3] INFO  [c.h.h.auth.service.KakaoApiService] [req_7f2a1b3c,-] - ✅ Kakao user info response received in 144ms

2025-01-25 09:15:23.895 [reactor-http-nio-3] DEBUG [c.h.h.auth.service.KakaoApiService] [req_7f2a1b3c,-] - 👤 User ID: 2891023847

2025-01-25 09:15:23.897 [reactor-http-nio-3] DEBUG [c.h.h.auth.service.KakaoApiService] [req_7f2a1b3c,-] - 📧 Email available: true

2025-01-25 09:15:23.899 [reactor-http-nio-3] DEBUG [c.h.h.auth.service.KakaoApiService] [req_7f2a1b3c,-] - 👤 Nickname available: true

2025-01-25 09:15:23.901 [reactor-http-nio-3] DEBUG [c.h.h.auth.service.KakaoApiService] [req_7f2a1b3c,-] - 🖼️ Profile image available: true

2025-01-25 09:15:23.905 [http-nio-30001-exec-1] INFO  [c.h.h.auth.service.KakaoApiService] [req_7f2a1b3c,-] - 🎉 Kakao user info retrieval completed successfully in 157ms

2025-01-25 09:15:23.910 [http-nio-30001-exec-1] INFO  [c.h.h.auth.service.AuthService] [req_7f2a1b3c,-] - ✅ Kakao user info retrieved in 165ms

2025-01-25 09:15:23.912 [http-nio-30001-exec-1] DEBUG [c.h.h.auth.service.AuthService] [req_7f2a1b3c,-] - 🆔 Kakao user ID: 2891023847

2025-01-25 09:15:23.915 [http-nio-30001-exec-1] INFO  [c.h.h.auth.service.AuthService] [req_7f2a1b3c,-] - 🔍 Checking if user exists in database: 2891023847

Hibernate: select u1_0.id,u1_0.created_at,u1_0.email,u1_0.kakao_id,u1_0.location_service_accepted,u1_0.nickname,u1_0.privacy_policy_accepted,u1_0.profile_image_url,u1_0.role,u1_0.terms_of_service_accepted,u1_0.updated_at from users u1_0 where u1_0.kakao_id=?

2025-01-25 09:15:23.945 [http-nio-30001-exec-1] INFO  [c.h.h.auth.service.AuthService] [req_7f2a1b3c,-] - 👥 Existing user found, updating user information

2025-01-25 09:15:23.948 [http-nio-30001-exec-1] DEBUG [c.h.h.auth.service.AuthService] [req_7f2a1b3c,-] - 📝 User info updated for existing user: a1b2c3d4-e5f6-7890-abcd-ef1234567890

2025-01-25 09:15:23.950 [http-nio-30001-exec-1] DEBUG [c.h.h.auth.service.AuthService] [req_7f2a1b3c,-] - 💾 Saving user to database

Hibernate: update users set created_at=?,email=?,kakao_id=?,location_service_accepted=?,nickname=?,privacy_policy_accepted=?,profile_image_url=?,role=?,terms_of_service_accepted=?,updated_at=? where id=?

2025-01-25 09:15:23.978 [http-nio-30001-exec-1] INFO  [c.h.h.auth.service.AuthService] [req_7f2a1b3c,-] - ✅ User saved to database in 28ms

2025-01-25 09:15:23.980 [http-nio-30001-exec-1] INFO  [c.h.h.auth.service.AuthService] [req_7f2a1b3c,-] - 🎫 Generating JWT token for user: a1b2c3d4-e5f6-7890-abcd-ef1234567890

2025-01-25 09:15:23.995 [http-nio-30001-exec-1] INFO  [c.h.h.auth.service.AuthService] [req_7f2a1b3c,-] - ✅ JWT token generated in 15ms

2025-01-25 09:15:23.998 [http-nio-30001-exec-1] INFO  [c.h.h.auth.service.AuthService] [req_7f2a1b3c,-] - 🎉 Kakao OAuth authentication completed successfully in 540ms (Token: 275ms, UserInfo: 165ms, DB: 28ms, JWT: 15ms)

2025-01-25 09:15:24.001 [http-nio-30001-exec-1] INFO  [c.h.h.auth.controller.AuthController] [req_7f2a1b3c,-] - ✅ Kakao login successful in 546ms, JWT token generated

2025-01-25 09:15:24.003 [http-nio-30001-exec-1] DEBUG [c.h.h.auth.controller.AuthController] [req_7f2a1b3c,-] - 📤 Returning JWT token with length: 164

2025-01-25 09:15:24.008 [http-nio-30001-exec-1] INFO  [c.h.h.config.ApiLoggingInterceptor] [req_7f2a1b3c,-] - ✅ API REQUEST COMPLETED: {"requestId":"req_7f2a1b3c","timestamp":"2025-01-25 09:15:24.008","method":"POST","uri":"/auth/kakao-login","status":200,"durationMs":563,"success":true,"controller":"AuthController","action":"kakaoLogin","performance":"GOOD"}
```

---

## 2. ❌ **Failed Kakao OAuth Login (Invalid Authorization Code)**

```log
2025-01-25 09:20:15.123 [http-nio-30001-exec-2] INFO  [c.h.h.config.ApiLoggingInterceptor] [req_9d8e7f6a,-] - 🚀 API REQUEST START: {"requestId":"req_9d8e7f6a","timestamp":"2025-01-25 09:20:15.123","method":"POST","uri":"/auth/kakao-login","remoteAddr":"203.104.25.12","controller":"AuthController","action":"kakaoLogin"}

2025-01-25 09:20:15.125 [http-nio-30001-exec-2] INFO  [c.h.h.auth.controller.AuthController] [req_9d8e7f6a,-] - 🔐 Starting Kakao OAuth login process

2025-01-25 09:20:15.127 [http-nio-30001-exec-2] DEBUG [c.h.h.auth.controller.AuthController] [req_9d8e7f6a,-] - 📋 Kakao login request received with authorization code length: 28

2025-01-25 09:20:15.130 [http-nio-30001-exec-2] INFO  [c.h.h.auth.service.KakaoApiService] [req_9d8e7f6a,-] - 🌐 Starting Kakao access token exchange

2025-01-25 09:20:15.245 [reactor-http-nio-4] ERROR [c.h.h.auth.service.KakaoApiService] [req_9d8e7f6a,-] - ❌ Kakao token request failed after 115ms: 400 Bad Request: [{"error":"invalid_grant","error_description":"authorization code not found","error_code":"KOE320"}]

2025-01-25 09:20:15.247 [reactor-http-nio-4] ERROR [c.h.h.auth.service.KakaoApiService] [req_9d8e7f6a,-] - 🔴 HTTP Status: 400 BAD_REQUEST, Response Body: {"error":"invalid_grant","error_description":"authorization code not found","error_code":"KOE320"}

2025-01-25 09:20:15.250 [http-nio-30001-exec-2] ERROR [c.h.h.auth.service.KakaoApiService] [req_9d8e7f6a,-] - 💥 Failed to get Kakao access token after 120ms

2025-01-25 09:20:15.252 [http-nio-30001-exec-2] ERROR [c.h.h.auth.service.KakaoApiService] [req_9d8e7f6a,-] - 🔴 Bad Request (400) - Invalid authorization code or client credentials

2025-01-25 09:20:15.255 [http-nio-30001-exec-2] ERROR [c.h.h.auth.service.AuthService] [req_9d8e7f6a,-] - ❌ Kakao OAuth authentication failed after 125ms: Failed to get Kakao access token

2025-01-25 09:20:15.257 [http-nio-30001-exec-2] ERROR [c.h.h.auth.service.AuthService] [req_9d8e7f6a,-] - 🔴 Kakao access token exchange failed - check authorization code validity

2025-01-25 09:20:15.260 [http-nio-30001-exec-2] ERROR [c.h.h.auth.controller.AuthController] [req_9d8e7f6a,-] - ❌ Kakao login failed after 135ms - Runtime Exception: Failed to login with Kakao

2025-01-25 09:20:15.262 [http-nio-30001-exec-2] ERROR [c.h.h.auth.controller.AuthController] [req_9d8e7f6a,-] - 🔴 Kakao API communication error detected

2025-01-25 09:20:15.265 [http-nio-30001-exec-2] ERROR [c.h.h.config.ApiLoggingInterceptor] [req_9d8e7f6a,-] - 🔥 API REQUEST COMPLETED: {"requestId":"req_9d8e7f6a","method":"POST","uri":"/auth/kakao-login","status":400,"durationMs":142,"success":false,"controller":"AuthController","action":"kakaoLogin","performance":"EXCELLENT"}
```

---

## 3. 🎬 **Successful Video Generation Request**

```log
2025-01-25 10:30:45.678 [http-nio-30001-exec-3] INFO  [c.h.h.config.ApiLoggingInterceptor] [req_3f4e5d6c,-] - 🚀 API REQUEST START: {"requestId":"req_3f4e5d6c","timestamp":"2025-01-25 10:30:45.678","method":"POST","uri":"/videos/generations","controller":"VideoController","action":"createVideoGeneration"}

2025-01-25 10:30:45.682 [http-nio-30001-exec-3] INFO  [c.h.h.video.service.VideoGenerationApiService] [req_3f4e5d6c,-] - 🎬 Starting NestJS video generation request

2025-01-25 10:30:45.685 [http-nio-30001-exec-3] DEBUG [c.h.h.video.service.VideoGenerationApiService] [req_3f4e5d6c,-] - 📝 Endpoint: /

2025-01-25 10:30:45.687 [http-nio-30001-exec-3] DEBUG [c.h.h.video.service.VideoGenerationApiService] [req_3f4e5d6c,-] - ⏱️ Timeout configured: 300000ms

2025-01-25 10:30:45.690 [http-nio-30001-exec-3] DEBUG [c.h.h.video.service.VideoGenerationApiService] [req_3f4e5d6c,-] - 📋 Request details - Template: template_3, Images: 3, Store: Present

2025-01-25 10:30:45.695 [http-nio-30001-exec-3] INFO  [c.h.h.video.service.VideoGenerationApiService] [req_3f4e5d6c,-] - 📤 Sending video generation request to NestJS service

2025-01-25 10:30:45.700 [http-nio-30001-exec-3] DEBUG [c.h.h.video.service.VideoGenerationApiService] [req_3f4e5d6c,-] - 🚀 NestJS video generation request subscribed

2025-01-25 10:30:45.705 [http-nio-30001-exec-3] DEBUG [c.h.h.video.service.VideoGenerationApiService] [req_3f4e5d6c,-] - ⏳ Blocking for NestJS video generation response...

2025-01-25 10:31:23.234 [reactor-http-nio-5] INFO  [c.h.h.video.service.VideoGenerationApiService] [req_3f4e5d6c,-] - ✅ NestJS video generation response received in 37534ms

2025-01-25 10:31:23.237 [reactor-http-nio-5] DEBUG [c.h.h.video.service.VideoGenerationApiService] [req_3f4e5d6c,-] - 📨 Response list size: 1

2025-01-25 10:31:23.240 [reactor-http-nio-5] DEBUG [c.h.h.video.service.VideoGenerationApiService] [req_3f4e5d6c,-] - 🎬 Video response - Video URL available: true

2025-01-25 10:31:23.243 [reactor-http-nio-5] DEBUG [c.h.h.video.service.VideoGenerationApiService] [req_3f4e5d6c,-] - 🔢 Response ID: video_abc123def456

2025-01-25 10:31:23.248 [http-nio-30001-exec-3] INFO  [c.h.h.video.service.VideoGenerationApiService] [req_3f4e5d6c,-] - 🎉 NestJS video generation completed successfully in 37553ms

2025-01-25 10:31:23.251 [http-nio-30001-exec-3] DEBUG [c.h.h.video.service.VideoGenerationApiService] [req_3f4e5d6c,-] - 📤 Returning first response from list of 1 items

2025-01-25 10:31:23.254 [http-nio-30001-exec-3] DEBUG [c.h.h.video.service.VideoGenerationApiService] [req_3f4e5d6c,-] - ✅ Final result - Video URL: Present, ID: video_abc123def456

2025-01-25 10:31:23.260 [http-nio-30001-exec-3] INFO  [c.h.h.config.ApiLoggingInterceptor] [req_3f4e5d6c,-] - ✅ API REQUEST COMPLETED: {"requestId":"req_3f4e5d6c","method":"POST","uri":"/videos/generations","status":201,"durationMs":37582,"success":true,"performance":"VERY_SLOW"}

2025-01-25 10:31:23.263 [http-nio-30001-exec-3] WARN  [c.h.h.config.ApiLoggingInterceptor] [req_3f4e5d6c,-] - ⚠️ SLOW API REQUEST DETECTED: /videos/generations took 37582ms
```

---

## 4. 🔄 **Video Generation with Retry Logic**

```log
2025-01-25 11:15:30.456 [http-nio-30001-exec-4] INFO  [c.h.h.video.service.VideoGenerationApiService] [req_8g7f6e5d,-] - 🎬 Starting NestJS video generation request

2025-01-25 11:15:30.890 [reactor-http-nio-6] ERROR [c.h.h.video.service.VideoGenerationApiService] [req_8g7f6e5d,-] - ❌ NestJS video generation request failed after 434ms: Connection refused: /127.0.0.1:30000

2025-01-25 11:15:30.893 [reactor-http-nio-6] INFO  [c.h.h.video.service.VideoGenerationApiService] [req_8g7f6e5d,-] - 🔄 Will retry on error: Connection refused: /127.0.0.1:30000

2025-01-25 11:15:31.920 [http-nio-30001-exec-4] WARN  [c.h.h.video.service.VideoGenerationApiService] [req_8g7f6e5d,-] - 🔄 Retrying NestJS video generation request (attempt: 1) after 1464ms due to: Connection refused: /127.0.0.1:30000

2025-01-25 11:15:33.125 [reactor-http-nio-7] ERROR [c.h.h.video.service.VideoGenerationApiService] [req_8g7f6e5d,-] - ❌ NestJS video generation request failed after 1669ms: Connection refused: /127.0.0.1:30000

2025-01-25 11:15:35.350 [http-nio-30001-exec-4] WARN  [c.h.h.video.service.VideoGenerationApiService] [req_8g7f6e5d,-] - 🔄 Retrying NestJS video generation request (attempt: 2) after 4894ms due to: Connection refused: /127.0.0.1:30000

2025-01-25 11:15:37.567 [reactor-http-nio-8] INFO  [c.h.h.video.service.VideoGenerationApiService] [req_8g7f6e5d,-] - ✅ NestJS video generation response received in 7111ms

2025-01-25 11:15:37.570 [http-nio-30001-exec-4] INFO  [c.h.h.video.service.VideoGenerationApiService] [req_8g7f6e5d,-] - 🎉 NestJS video generation completed successfully in 7114ms
```

---

## 5. 🏠 **Home Data Retrieval**

```log
2025-01-25 12:45:12.789 [http-nio-30001-exec-5] INFO  [c.h.h.config.ApiLoggingInterceptor] [req_5h6g7f8e,-] - 🚀 API REQUEST START: {"requestId":"req_5h6g7f8e","method":"GET","uri":"/home","controller":"HomeController","action":"getHomeData","headers":{"authorization":"***REDACTED***"}}

2025-01-25 12:45:12.792 [http-nio-30001-exec-5] INFO  [c.h.h.home.controller.HomeController] [req_5h6g7f8e,-] - 🏠 Fetching home data for authenticated user

2025-01-25 12:45:12.795 [http-nio-30001-exec-5] DEBUG [c.h.h.home.controller.HomeController] [req_5h6g7f8e,-] - 🔍 Extracting user ID from JWT token

2025-01-25 12:45:12.812 [http-nio-30001-exec-5] DEBUG [c.h.h.home.controller.HomeController] [req_5h6g7f8e,-] - 👤 User ID extracted: a1b2c3d4-e5f6-7890-abcd-ef1234567890

2025-01-25 12:45:12.815 [http-nio-30001-exec-5] INFO  [c.h.h.home.controller.HomeController] [req_5h6g7f8e,-] - 📊 Retrieving home data for user: a1b2c3d4-e5f6-7890-abcd-ef1234567890

Hibernate: select s1_0.id,s1_0.address,s1_0.category,s1_0.created_at,s1_0.description,s1_0.name,s1_0.owner_id,s1_0.phone,s1_0.updated_at from stores s1_0 where s1_0.owner_id=?

2025-01-25 12:45:12.856 [http-nio-30001-exec-5] INFO  [c.h.h.home.controller.HomeController] [req_5h6g7f8e,-] - ✅ Home data retrieved successfully in 64ms

2025-01-25 12:45:12.859 [http-nio-30001-exec-5] DEBUG [c.h.h.home.controller.HomeController] [req_5h6g7f8e,-] - 📤 Home response prepared with data: Present

2025-01-25 12:45:12.862 [http-nio-30001-exec-5] INFO  [c.h.h.config.ApiLoggingInterceptor] [req_5h6g7f8e,-] - ✅ API REQUEST COMPLETED: {"requestId":"req_5h6g7f8e","method":"GET","uri":"/home","status":200,"durationMs":73,"success":true,"performance":"EXCELLENT"}
```

---

## 6. ⚠️ **Slow Database Query Warning**

```log
2025-01-25 14:20:05.234 [http-nio-30001-exec-6] INFO  [c.h.h.config.ApiLoggingInterceptor] [req_7i8h9g0f,-] - 🚀 API REQUEST START: {"requestId":"req_7i8h9g0f","method":"GET","uri":"/stores","controller":"StoreController","action":"getUserStores"}

Hibernate: select s1_0.id,s1_0.address,s1_0.category,s1_0.created_at,s1_0.description,s1_0.name,s1_0.owner_id,s1_0.phone,s1_0.updated_at,(select count(v1_0.id) from videos v1_0 where v1_0.store_id=s1_0.id) from stores s1_0 left join users u1_0 on u1_0.id=s1_0.owner_id where s1_0.owner_id=? order by s1_0.created_at desc

2025-01-25 14:20:10.789 [http-nio-30001-exec-6] INFO  [c.h.h.config.ApiLoggingInterceptor] [req_7i8h9g0f,-] - ✅ API REQUEST COMPLETED: {"requestId":"req_7i8h9g0f","method":"GET","uri":"/stores","status":200,"durationMs":5555,"success":true,"performance":"VERY_SLOW"}

2025-01-25 14:20:10.792 [http-nio-30001-exec-6] WARN  [c.h.h.config.ApiLoggingInterceptor] [req_7i8h9g0f,-] - ⚠️ SLOW API REQUEST DETECTED: /stores took 5555ms
```

---

## 7. 💥 **System Error with Stack Trace**

```log
2025-01-25 15:30:20.456 [http-nio-30001-exec-7] INFO  [c.h.h.config.ApiLoggingInterceptor] [req_9j0i1h2g,-] - 🚀 API REQUEST START: {"requestId":"req_9j0i1h2g","method":"POST","uri":"/videos/generations"}

2025-01-25 15:30:20.789 [http-nio-30001-exec-7] ERROR [c.h.h.config.ApiLoggingInterceptor] [req_9j0i1h2g,-] - 💥 API REQUEST COMPLETED: {"requestId":"req_9j0i1h2g","method":"POST","uri":"/videos/generations","status":500,"durationMs":333,"success":false,"exception":"RuntimeException","errorMessage":"Database connection failed","performance":"GOOD"}

2025-01-25 15:30:20.792 [http-nio-30001-exec-7] ERROR [org.springframework.web.servlet.DispatcherServlet] [req_9j0i1h2g,-] - Handler execution resulted in exception: Database connection failed
java.lang.RuntimeException: Database connection failed
	at com.ht.htserver.video.service.VideoService.createVideoGeneration(VideoService.java:45)
	at com.ht.htserver.video.controller.VideoController.createVideoGeneration(VideoController.java:74)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	...
```

---

## 8. 🔍 **Request Tracing Example**

When you need to trace a specific request, search by request ID:

```bash
# Find all logs for a specific request
grep "req_7f2a1b3c" logs/ht-server.log

# Find all slow requests today
grep "SLOW\|VERY_SLOW" logs/ht-server.log | grep "$(date '+%Y-%m-%d')"

# Find all Kakao API errors in the last hour
grep "Kakao.*ERROR\|❌.*Kakao" logs/ht-server.log | grep "$(date -d '1 hour ago' '+%Y-%m-%d %H')"

# Find all 5xx errors
grep "status\":5[0-9][0-9]" logs/ht-server.log

# Monitor video generation performance
grep "🎬\|video.*generation" logs/ht-server.log | grep -E "(completed|failed)"
```

---

## 9. 📊 **Performance Analysis Examples**

```bash
# Performance distribution for today
grep "performance" logs/ht-server.log | grep "$(date '+%Y-%m-%d')" | \
  grep -o '"performance":"[^"]*"' | sort | uniq -c | sort -nr

# Output:
#  1245 "performance":"EXCELLENT"
#   432 "performance":"GOOD" 
#   123 "performance":"FAIR"
#    45 "performance":"SLOW"
#    12 "performance":"VERY_SLOW"

# Top slowest endpoints today
grep "SLOW\|VERY_SLOW" logs/ht-server.log | grep "$(date '+%Y-%m-%d')" | \
  grep -o '"uri":"[^"]*"' | sort | uniq -c | sort -nr

# External API response times
grep "completed successfully" logs/ht-server.log | grep "Kakao\|NestJS" | \
  grep -o "[0-9]\+ms" | sort -n
```

---

## 10. 🚨 **Error Monitoring Examples**

```bash
# Real-time error monitoring
tail -f logs/ht-server.log | grep --color=always "ERROR\|❌\|💥\|🔥"

# Database connection issues
grep -i "database\|connection\|hibernate.*error" logs/ht-server.log

# External service failures
grep "🔴\|Failed.*API\|timeout.*API" logs/ht-server.log

# Authentication failures
grep "🔐.*failed\|❌.*login\|Unauthorized" logs/ht-server.log
```

These examples show the comprehensive visibility you'll have into your application's behavior in production, making debugging and monitoring much more effective!