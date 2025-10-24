# Base image with Java
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy all files
COPY . /app

# Download Selenium Java and dependencies automatically
RUN apt-get update && apt-get install -y wget unzip && \
    wget -q https://github.com/SeleniumHQ/selenium/releases/download/selenium-4.37.0/selenium-java-4.37.0.zip && \
    unzip selenium-java-4.37.0.zip -d /app/lib && \
    rm selenium-java-4.37.0.zip

# Set the CLASSPATH including all jars in lib
ENV CLASSPATH="/app:/app/lib/*"

# Run the jar file
CMD ["java", "-cp", "/app:/app/lib/*", "BraveScreenshotBot"]
