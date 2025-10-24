# Use OpenJDK 17 as the base image
FROM openjdk:17-jdk-slim

# Create working directory
WORKDIR /app

# Copy all files from the current folder into the container
COPY . /app

# Run the JAR file
CMD ["java", "-jar", "BraveScreenshotBot.jar"]
