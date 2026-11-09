FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
COPY database ./database
RUN mvn -B package
FROM tomcat:9.0-jdk17-temurin
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/target/crm.war /usr/local/tomcat/webapps/crm.war
