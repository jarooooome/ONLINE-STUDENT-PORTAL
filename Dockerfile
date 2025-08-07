# Use Java 23 base image (compatible with your project)
FROM eclipse-temurin:23-jdk

# Set working directory inside the container
WORKDIR /app

# Copy the built JAR into the container and rename it
COPY target/student-portal-0.0.1-SNAPSHOT.jar app.jar

# Expose the Spring Boot default port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
