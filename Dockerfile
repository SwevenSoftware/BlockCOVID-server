FROM openjdk:15-jdk-alpine
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
EXPOSE 8091
