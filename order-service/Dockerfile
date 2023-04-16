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
ENV SERVER_PORT 8081
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

# Launchs the application at Docker image
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]


# ------------------------------------------------------------------------------------------
# COMMANDS:
#
# 1. Build the image in local:
#
#      docker build -t order-service . -f ./Dockerfile
#
# 2. Build and launch the container:
#
#      docker run -p 8081:8081 --rm --name order-service --add-host=host.docker.internal:172.17.0.1 --network Spring5Microservices order-service
