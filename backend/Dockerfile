FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Add a spring user to run our application so that it doesn't run as root
RUN addgroup --system spring && adduser --system spring --ingroup spring 

# Copy the source code to the working directory
COPY . .

# Change the mvn wrapper file into an executable
RUN chmod +655 ./mvnw

# Change the ownership of the application to the spring user
RUN chown -R spring:spring /app

# Build the application
RUN ./mvnw clean package -DskipTests

# Set the jar file path
ARG JAR_FILE=target/backend-0.0.1.jar

# Change the ownership of the created jar executables directory
RUN chown -R spring:spring /app/target

# Change the user to spring
USER spring

# Change the jar file name to the app.jar
RUN mv ${JAR_FILE} app.jar

# Expose the port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
