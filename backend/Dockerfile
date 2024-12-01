FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Add a spring user to run our application so that it doesn't run as root
RUN addgroup --system spring && adduser --system spring --ingroup spring 

# Copy the source code to the working directory
COPY . .

# Build the application
RUN ./mvnw clean package -DskipTests

# Change the ownership of the application to the spring user
RUN chown -R spring:spring /app

# Set the jar file path
ARG JAR_FILE=target/*.jar

# Change the user to spring
USER spring

# Move the jar file to the app.jar
RUN mv ${JAR_FILE} app.jar

# Expose the port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
