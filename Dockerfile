FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x gradlew
RUN sed -i 's/\r$//' ./gradlew # removes \r which windows somehow adds to eol

RUN --mount=type=cache,target=/home/gradle/.gradle/caches \
    ./gradlew dependencies --no-daemon

COPY ./src ./src

RUN --mount=type=cache,target=/home/gradle/.gradle/caches \
    ./gradlew clean build -x test --no-daemon

# reduces memory usage
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

RUN addgroup -S wise-task && adduser -S wise-task-profile -G wise-task # security
USER wise-task-profile

ENTRYPOINT ["java","-jar","app.jar"]