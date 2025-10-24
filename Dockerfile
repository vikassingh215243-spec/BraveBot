# Base image with Java
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy all files
COPY . /app

# Include all Selenium jars in classpath automatically
RUN mkdir -p /app/lib
COPY selenium-java-4.37.0/ /app/lib

# Set the CLASSPATH including all jars in lib
ENV CLASSPATH="/app:/app/lib/*"

# Run the jar file
CMD ["java", "-cp", "/app:/app/lib/*", "BraveScreenshotBot"]
