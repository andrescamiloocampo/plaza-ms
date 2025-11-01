FROM openjdk:17-jdk-slim AS build

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

COPY src src

RUN apt-get update && apt-get install -y dos2unix \
    && dos2unix gradlew \
    && chmod +x gradlew

RUN sh ./gradlew bootJar -x test

FROM eclipse-temurin:17-jre-focal

WORKDIR /app

ARG JAR_FILE=/app/build/libs/*.jar

COPY --from=build ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]