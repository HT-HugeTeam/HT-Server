# HT - Video Automation Platform

A microservices-based video automation platform combining AI-powered video generation with robust backend infrastructure.

## Architecture Overview

### Services
- **ht-ai**: NestJS-based AI service for video processing and generation
- **ht-server**: Spring Boot REST API server with JWT authentication
- **PostgreSQL**: Database for data persistence

### Technology Stack

#### AI Service (ht-ai)
- **Framework**: NestJS (Node.js)
- **Language**: TypeScript
- **AI Integrations**:
  - OpenAI GPT for content generation
  - Creatomate for video automation
  - TwelveLabs for video analysis
  - Music metadata extraction
- **Storage**: AWS S3 integration
- **Port**: 3000

#### Backend Server (ht-server)
- **Framework**: Spring Boot 3.5.4
- **Language**: Java 17
- **Security**: Spring Security with JWT
- **Database**: JPA/Hibernate with PostgreSQL
- **Migration**: Flyway
- **Documentation**: Swagger/OpenAPI
- **Port**: 8080

#### Database
- **Engine**: PostgreSQL
- **Features**: JPA Auditing, Flyway migrations
- **Port**: 5432

## Project Structure

```
ht/
├── ht-ai/
│   ├── src/
│   │   ├── app.controller.ts
│   │   ├── app.module.ts
│   │   ├── app.service.ts
│   │   ├── constant/prompts.ts
│   │   ├── main.ts
│   │   └── types.ts
│   ├── package.json
│   ├── nest-cli.json
│   └── Dockerfile
├── ht-server/
│   ├── src/main/java/com/ht/htserver/
│   │   ├── auth/
│   │   ├── common/entity/BaseEntity.java
│   │   ├── config/
│   │   ├── home/
│   │   ├── store/
│   │   ├── user/
│   │   ├── video/
│   │   └── HtServerApplication.java
│   ├── src/main/resources/
│   │   ├── application.yaml
│   │   └── db/migration/
│   ├── build.gradle
│   └── Dockerfile
├── docker-compose.yaml
├── docker-compose.prod.yaml
└── ht-db-data/
```

## API Endpoints

### Authentication
- `POST /auth/login` - JWT authentication
- JWT token validation for protected routes

### User Management
- `PUT /users/onboarding-status` - Update user onboarding status
- `GET /users/onboarding-status` - Get user onboarding status

### Store Management
- `POST /stores` - Create store
- `GET /stores` - Get stores
- `PUT /stores/{id}` - Update store

### Video Management
- Video and VideoGeneration entities
- Video processing through AI service

## Dependencies

### ht-ai Dependencies
```json
{
  "@nestjs/common": "^11.0.1",
  "@nestjs/core": "^11.0.1",
  "@aws-sdk/client-s3": "^3.864.0",
  "creatomate": "^1.2.1",
  "openai": "^5.12.2",
  "twelvelabs-js": "^1.0.0",
  "music-metadata": "^11.8.2"
}
```

### ht-server Dependencies
```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.flywaydb:flyway-core'
    implementation 'io.jsonwebtoken:jjwt-api:0.12.3'
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0'
    runtimeOnly 'org.postgresql:postgresql'
}
```

## Development Setup

### Prerequisites
- Node.js 16+
- Java 17+
- Docker & Docker Compose
- PostgreSQL (if running without Docker)

### Quick Start
```bash
# Start all services
docker-compose up -d

# Access points:
# - AI Service: http://localhost:3000
# - Spring Boot API: http://localhost:8080
# - Swagger UI: http://localhost:8080/swagger-ui.html
# - PostgreSQL: localhost:5432
```

### Manual Development
```bash
# Database
docker-compose up db -d

# AI Service
cd ht-ai
npm install
npm run start:dev

# Spring Boot Server
cd ht-server
./gradlew bootRun
```

## Environment Configuration

### Database
```env
POSTGRES_DB=db
POSTGRES_USER=root
POSTGRES_PASSWORD=password
```

### AI Service
```env
PORT=3000
AWS_ACCESS_KEY_ID=your_key
AWS_SECRET_ACCESS_KEY=your_secret
OPENAI_API_KEY=your_key
CREATOMATE_API_KEY=your_key
TWELVELABS_API_KEY=your_key
```

### Spring Boot
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/db
    username: root
    password: password
```

## Testing

### AI Service
```bash
cd ht-ai
npm run test          # Unit tests
npm run test:e2e      # E2E tests
npm run test:cov      # Coverage
```

### Spring Boot
```bash
cd ht-server
./gradlew test        # All tests
./gradlew build       # Build with tests
```

## Production Deployment

```bash
# Production mode
docker-compose -f docker-compose.prod.yaml up -d
```