FROM openjdk:21
COPY out /app
WORKDIR /app
CMD ["java", "application.Main"]
