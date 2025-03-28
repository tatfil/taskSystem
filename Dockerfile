FROM amazoncorretto:17-alpine

WORKDIR /app

# Copy the JAR file from the build output to the container
COPY target/taskManagementSystem-1.0-SNAPSHOT.jar app.jar

# Expose port 8080 for the application
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]