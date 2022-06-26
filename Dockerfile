# syntax=docker/dockerfile:1
FROM openjdk:17-jdk-alpine
COPY target/classroom.jar classroom.jar
ENTRYPOINT ["java","-jar","/classroom.jar"]