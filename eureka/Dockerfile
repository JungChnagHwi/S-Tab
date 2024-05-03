FROM openjdk:17

CMD ["./gradlew", "clean", "build", "-x", "test"]

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "/app.jar"]