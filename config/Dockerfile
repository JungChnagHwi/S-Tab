FROM openjdk:17

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} config.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "-Duser.timezone=Asia/Seoul", "/config.jar"]