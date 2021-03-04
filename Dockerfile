FROM ubuntu:latest
FROM openjdk:15-jdk-alpine
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java","-Dspring.data.mongodb.uri=mongodb://mongo:27017/blockcovid-test","-jar","app.jar"]
EXPOSE 8080
