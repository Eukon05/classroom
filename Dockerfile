# syntax=docker/dockerfile:1

FROM maven:3.8.4-openjdk-17

WORKDIR /app

COPY pom.xml ./
RUN mvn dependency:go-offline

COPY src ./src

CMD ["mvn", "spring-boot:run"]