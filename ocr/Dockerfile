FROM openjdk:17

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} ocr.jar

EXPOSE 8005

ENTRYPOINT ["java", "-jar", "-Duser.timezone=Asia/Seoul", "/ocr.jar"]