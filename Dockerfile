# Stage 1: Use a reliable Java 17 base image
FROM openjdk:17-slim

# 1. Install dependencies & Chrome repository (FIXED for exit code 100 using the most stable key method)
RUN apt-get update && apt-get install -y wget curl unzip gnupg lsb-release fonts-liberation libappindicator3-1 xdg-utils \
    # Google Chrome Key और Repo को जोड़ें (सबसे विश्वसनीय तरीका)
    && curl -fsSL https://dl.google.com/linux/linux_signing_key.pub | gpg --dearmor -o /usr/share/keyrings/google-chrome-keyring.gpg \
    && echo "deb [arch=amd64 signed-by=/usr/share/keyrings/google-chrome-keyring.gpg] http://dl.google.com/linux/chrome/deb/ stable main" | tee /etc/apt/sources.list.d/google-chrome.list > /dev/null \
    \
    # Chrome और उसकी dependencies इंस्टॉल करें
    && apt-get update && apt-get install -y google-chrome-stable \
    \
    # Cleanup
    && rm -rf /var/lib/apt/lists/*

# 2. Install ChromeDriver (Using version 141, which is compatible with Chrome 127/128)
RUN wget -O /tmp/chromedriver.zip https://storage.googleapis.com/chrome-for-testing-public/141.0.7390.0/linux64/chromedriver-linux64.zip \
    && unzip /tmp/chromedriver.zip -d /usr/local/bin/ \
    && mv /usr/local/bin/chromedriver-linux64/chromedriver /usr/local/bin/chromedriver \
    && chmod +x /usr/local/bin/chromedriver \
    && rm -rf /tmp/chromedriver.zip

# 3. Install ALL required Selenium dependencies (FIX for all NoClassDefFoundErrors)
RUN mkdir -p /app/lib && \
    wget -q -P /app/lib https://repo1.maven.org/maven2/org/seleniumhq/selenium/selenium-java/4.37.0/selenium-java-4.37.0.jar && \
    wget -q -P /app/lib https://repo1.maven.org/maven2/org/seleniumhq/selenium/selenium-api/4.37.0/selenium-api-4.37.0.jar && \
    wget -q -P /app/lib https://repo1.maven.org/maven2/org/seleniumhq/selenium/selenium-chrome-driver/4.37.0/selenium-chrome-driver-4.37.0.jar && \
    wget -q -P /app/lib https://repo1.maven.org/maven2/org/seleniumhq/selenium/selenium-chromium-driver/4.37.0/selenium-chromium-driver-4.37.0.jar && \
    wget -q -P /app/lib https://repo1.maven.org/maven2/org/seleniumhq/selenium/selenium-remote-driver/4.37.0/selenium-remote-driver-4.37.0.jar && \
    wget -q -P /app/lib https://repo1.maven.org/maven2/org/seleniumhq/selenium/selenium-http/4.37.0/selenium-http-4.37.0.jar && \
    wget -q -P /app/lib https://repo1.maven.org/maven2/org/seleniumhq/selenium/selenium-support/4.37.0/selenium-support-4.37.0.jar && \
    wget -q -P /app/lib https://repo1.maven.org/maven2/com/google/guava/guava/33.2.0-jre/guava-33.2.0-jre.jar

# 4. Environment Variables
ENV CHROME_BIN=/usr/bin/google-chrome
ENV PATH="/usr/local/bin:$PATH"

# 5. Copy application and set working directory
WORKDIR /app
# COPY . /app will copy your JAR file (BraveScreenshotBot.jar) and any other files present in the repo.
COPY . /app

# 6. Command to run the application (Uses the full lib directory in classpath)
CMD ["java", "-cp", "/app:/app/lib/*", "BraveScreenshotBot"]
