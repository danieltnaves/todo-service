FROM gradle:8.3-jdk17 AS build
WORKDIR /app
COPY build.gradle settings.gradle /app/
COPY config /app/config
COPY src /app/src
RUN gradle build

FROM openjdk:17-alpine
WORKDIR /app
COPY --from=build /app/build/libs/todo-service.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]