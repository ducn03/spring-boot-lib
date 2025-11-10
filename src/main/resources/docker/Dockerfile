# =========================
# Stage 1: Build Jar
# =========================
FROM maven:3.9.1-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy config Lombok + pom + source
COPY lombok.config .
COPY pom.xml .
COPY src ./src

# Build jar, bỏ qua test để nhanh hơn
RUN mvn clean package -DskipTests

# =========================
# Stage 2: Runtime
# =========================
FROM eclipse-temurin:17-jdk AS runtime

WORKDIR /app

# Copy jar từ stage build
COPY --from=builder /app/target/*.jar app.jar

# Mở cổng Spring Boot
EXPOSE 9000

# Chạy app
ENTRYPOINT ["java", "-jar", "app.jar"]
