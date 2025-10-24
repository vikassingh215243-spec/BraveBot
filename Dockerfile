# Base image with Java
FROM openjdk:17-jdk-slim

# Install dependencies and Google Chrome
RUN apt-get update && apt-get install -y wget gnupg unzip && \
    wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - && \
    sh -c 'echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list' && \
    apt-get update && apt-get install -y google-chrome-stable && \
    rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy all files
COPY . /app

# Download Selenium Java libraries
RUN wget -q https://github.com/SeleniumHQ/selenium/releases/download/selenium-4.37.0/selenium-java-4.37.0.zip && \
    unzip selenium-java-4.37.0.zip -d /app/lib && \
    rm selenium-java-4.37.0.zip

# Set environment variables for Chrome
ENV CHROME_BIN=/usr/bin/google-chrome
ENV CHROME_DRIVER=/usr/bin/chromedriver
ENV PATH=$PATH:/usr/bin

# Set CLASSPATH for Java
ENV CLASSPATH="/app:/app/lib/*"

# Run the Java bot
CMD ["java", "-cp", "/app:/app/lib/*", "BraveScreenshotBot"]
