FROM gradle:jdk17 AS builder

WORKDIR /libs

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x ./gradlew

RUN ./gradlew dependencies --no-daemon || true

COPY ../core ./core

COPY src src

RUN ./gradlew build --no-daemon -x test

FROM eclipse-temurin:17.0.10_7-jre

WORKDIR /app

COPY --from=builder /libs/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]