# Build stage - using verified Maven image
FROM maven:3.8.7-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage - using verified JDK image
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/student-portal-*.jar ./app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]