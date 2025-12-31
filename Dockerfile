# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
# RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
# Use a JRE (Java Runtime Environment) for a smaller, more secure final image.
# The 'jammy' base image already includes curl and ca-certificates.
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Create a non-root user for security
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

# Copy the JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port the application runs on
EXPOSE 8080

# Environment variables for Java 21
ENV JAVA_OPTS="-Xmx512m -Xms256m -server -XX:+UseZGC -XX:+UnlockExperimentalVMOptions --enable-preview"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Start command
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dreactor.netty.ioWorkerCount=4 -Dreactor.netty.pool.maxConnections=500 --add-opens java.base/java.nio=ALL-UNNAMED -jar app.jar"]