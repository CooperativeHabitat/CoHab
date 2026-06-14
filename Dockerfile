FROM gradle:8.7-jdk21 AS build-stage
WORKDIR /app

COPY build.gradle .
COPY settings.gradle .
COPY gradle gradle/
COPY src src/

USER root
RUN chown -R gradle:gradle /app
USER gradle

RUN gradle build -x test

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY --from=build-stage /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
