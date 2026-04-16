FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon

COPY src src
RUN ./gradlew clean bootJar --no-daemon

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar

EXPOSE 8080

ENV TZ=Asia/Seoul

ENTRYPOINT ["java", "-jar", "app.jar"]