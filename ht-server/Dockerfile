# Build stage
FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /app

# Copy gradle wrapper and build files
COPY gradle/ gradle/
COPY gradlew build.gradle settings.gradle ./

# Copy source code
COPY src/ src/

# Build the application
RUN chmod +x gradlew && ./gradlew build -x test

# Runtime stage
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy the built jar from builder stage
COPY --from=builder /app/build/libs/*SNAPSHOT.jar project.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "project.jar"]