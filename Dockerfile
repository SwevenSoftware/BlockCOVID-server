FROM openjdk:15-jdk-alpine
LABEL maintiner="SwevenSoftware"
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
EXPOSE 8091
