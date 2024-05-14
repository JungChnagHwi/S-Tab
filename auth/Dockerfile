FROM openjdk:17

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} auth.jar

EXPOSE 8002

ENTRYPOINT ["java", "-jar", "-Duser.timezone=Asia/Seoul", "/auth.jar"]