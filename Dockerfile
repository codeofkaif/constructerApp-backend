# ==========================================
# Stage 1: Build stage with Maven and JDK 17
# ==========================================
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build final jar artifact
COPY src ./src
RUN mvn clean package -DskipTests

# ==========================================
# Stage 2: Runtime stage with lightweight JRE 17
# ==========================================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Environment variable for port (Render provides PORT env var)
ENV PORT=8080
EXPOSE ${PORT}

ENTRYPOINT ["java", "-jar", "app.jar"]
