FROM ghcr.io/graalvm/native-image-community:25 AS builder

WORKDIR /app

COPY build.gradle .
COPY gradlew .
COPY gradle gradle/
COPY settings.gradle .

COPY src src/

RUN chmod +x gradlew
RUN ./gradlew nativeCompile --no-daemon

FROM debian:latest
COPY --from=builder /app/build/native/nativeCompile/ /app/

EXPOSE 8080
WORKDIR /app
CMD ["/app/CoHab"]
