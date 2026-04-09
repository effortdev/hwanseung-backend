# 1단계: 빌드 스테이지
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

# Gradle 캐시 효율을 위해 설정 파일 먼저 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 라이브러리 미리 다운로드 (소스 코드 변경 시 빌드 속도 향상)
RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon

# 전체 소스 복사 및 빌드
# bootJar는 실행 가능한 단일 JAR 파일만 생성합니다.
COPY src src
RUN ./gradlew clean bootJar --no-daemon

# 2단계: 실행 스테이지 (JRE만 사용하여 이미지 용량 최소화)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# 빌드 스테이지에서 생성된 '실행 가능한' JAR만 복사
# Spring Boot 3 버전은 빌드 시 plain jar가 같이 생성될 수 있어 명확히 지정합니다.
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar

# Spring Boot 3.2.5 기본 포트
EXPOSE 8080

# 환경 변수 설정 (나중에 docker-compose에서 덮어쓰기 가능)
ENV TZ=Asia/Seoul

# 어플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]