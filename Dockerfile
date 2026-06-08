FROM ghcr.io/graalvm/native-image-community:25 AS builder

WORKDIR /app

COPY build.gradle .
COPY gradlew .
COPY gradle gradle/
COPY settings.gradle .

RUN ./gradlew dependencies --no-daemon

COPY src src/

RUN chmod +x gradlew
RUN ./gradlew bootNativeImage --no-daemon

FROM debian:latest
COPY --from=builder /app/build/native/nativeCompile/ /app/

EXPOSE 8080

CMD ["/app/CoHab"]
