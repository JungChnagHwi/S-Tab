FROM openjdk:17

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} s3.jar

EXPOSE 8004

ENTRYPOINT ["java", "-jar", "-Duser.timezone=Asia/Seoul", "/s3.jar"]