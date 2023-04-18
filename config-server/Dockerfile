# Source Docker image from https://hub.docker.com/
FROM eclipse-temurin:17-jdk-alpine as builder

# Working directory in Docker
WORKDIR /app

# Copy the local maven and the application to the Docker working directory
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN ./mvnw install -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)


# Multi-Stage Build
FROM eclipse-temurin:17-jre-alpine

# Port used to connect with the application
ENV SERVER_PORT 8888
EXPOSE $SERVER_PORT

# Default Spring profile
ENV SPRING_PROFILES_ACTIVE docker

VOLUME /tmp

ARG DEPENDENCY=/app/target/dependency

COPY --from=builder ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=builder ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=builder ${DEPENDENCY}/BOOT-INF/classes /app

# Use the Spring Boot main application class to run it
ENTRYPOINT ["java", "-cp", "app:app/lib/*", "com.configserver.ConfigServerApplication"]


# ------------------------------------------------------------------------------------------
# COMMANDS:
#
# 1. Build the image in local:
#
#      docker build -t config-server . -f ./Dockerfile
#
# 2. Build and launch the container:
#
#      docker run -p 8888:8888 --rm --name config-server --network Spring5Microservices config-server
