FROM openjdk:17

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} note.jar

EXPOSE 8001

ENTRYPOINT ["java", "-jar", "-Duser.timezone=Asia/Seoul", "/note.jar"]