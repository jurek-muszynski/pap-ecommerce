# Use a base image with OpenJDK 17
FROM openjdk:17-jdk-slim

# Defining variables for the JAR file and the application name
ARG JAR_FILE=target/*.jar

# Create a user to run the Spring Boot application
RUN addgroup --system spring && adduser --system spring --ingroup spring

# Change to the spring user
USER spring

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file into the container
COPY ${JAR_FILE} app.jar

# Expose port 8080 for the Spring Boot application
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]

