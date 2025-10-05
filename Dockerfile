FROM openjdk:17-jdk-slim AS build

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

COPY src src

RUN chmod +x gradlew

RUN ./gradlew bootJar -x test

FROM eclipse-temurin:17-jre-focal

WORKDIR /app

ARG JAR_FILE=/app/build/libs/*.jar

COPY --from=build ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]