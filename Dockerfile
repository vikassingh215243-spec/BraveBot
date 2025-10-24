# Base image with Java
FROM openjdk:17-slim

# Install Chrome and dependencies
RUN apt-get update && apt-get install -y wget gnupg unzip curl fonts-liberation libnss3 libxss1 libappindicator3-1 libatk-bridge2.0-0 libasound2 && \
    wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - && \
    sh -c 'echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list' && \
    apt-get update && apt-get install -y google-chrome-stable && \
    rm -rf /var/lib/apt/lists/*

# Install ChromeDriver
RUN LATEST=$(curl -s https://chromedriver.storage.googleapis.com/LATEST_RELEASE) && \
    wget -O /tmp/chromedriver.zip https://chromedriver.storage.googleapis.com/${LATEST}/chromedriver_linux64.zip && \
    unzip /tmp/chromedriver.zip -d /usr/local/bin/ && \
    rm /tmp/chromedriver.zip

# Create app directory
WORKDIR /app

# Copy all files
COPY . /app

# Download Selenium + dependencies directly into lib/
RUN mkdir -p /app/lib && \
    wget -q -P /app/lib https://repo1.maven.org/maven2/org/seleniumhq/selenium/selenium-java/4.37.0/selenium-java-4.37.0.jar && \
    wget -q -P /app/lib https://repo1.maven.org/maven2/org/seleniumhq/selenium/selenium-api/4.37.0/selenium-api-4.37.0.jar && \
    wget -q -P /app/lib https://repo1.maven.org/maven2/org/seleniumhq/selenium/selenium-chrome-driver/4.37.0/selenium-chrome-driver-4.37.0.jar && \
    wget -q -P /app/lib https://repo1.maven.org/maven2/com/google/guava/guava/33.2.0-jre/guava-33.2.0-jre.jar

# Set environment vars
ENV CHROME_BINARY=/usr/bin/google-chrome
ENV PATH="$PATH:/usr/local/bin"
ENV CLASSPATH="/app:/app/lib/*"

# Default command
CMD ["java", "-cp", "/app:/app/lib/*", "BraveScreenshotBot"]
