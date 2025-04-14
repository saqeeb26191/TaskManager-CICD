FROM openjdk:21

WORKDIR /app

COPY target/task-manager-1.0-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]

