# Construction
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Execution
FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/foodhub-project-backend-0.0.1-SNAPSHOT.jar app.jar
COPY credentials.json /app/credentials.json
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]