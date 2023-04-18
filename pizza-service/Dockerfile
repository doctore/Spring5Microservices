# Source Docker image from https://hub.docker.com/
FROM eclipse-temurin:17-jdk-alpine as builder

# Working directory in Docker
WORKDIR /app

# Copy the application to the Docker working directory
COPY target/*.jar app.jar

# Extract layers of application's jar
RUN java -Djarmode=layertools -jar app.jar extract


# Multi-Stage Build
FROM eclipse-temurin:17-jre-alpine

# Port used to connect with the application
ENV SERVER_PORT 8080
EXPOSE $SERVER_PORT

# Default symmetric encryption key
ARG ENCRYPT_KEY=ENCRYPT_KEY
ENV ENCRYPT_KEY ${ENCRYPT_KEY}

# Default Spring profile
ENV SPRING_PROFILES_ACTIVE docker

VOLUME /tmp

ARG DEPENDENCY=/app

COPY --from=builder ${DEPENDENCY}/dependencies/ ./
COPY --from=builder ${DEPENDENCY}/spring-boot-loader/ ./
COPY --from=builder ${DEPENDENCY}/snapshot-dependencies/ ./
COPY --from=builder ${DEPENDENCY}/application/ ./

# Copy the Spring Boot fat JarLauncher into the image and use it to run the application
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]


# ------------------------------------------------------------------------------------------
# COMMANDS:
#
# 1. Build the image in local:
#
#      docker build -t pizza-service . -f ./Dockerfile
#
# 2. Build and launch the container:
#
#      docker run -p 8080:8080 --rm --name pizza-service --add-host=host.docker.internal:host-gateway --network Spring5Microservices pizza-service
