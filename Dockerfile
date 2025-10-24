# Stage 1: Use a reliable Java 17 base image
FROM openjdk:17-slim

# 1. Install dependencies & Chrome repository (FIXED for exit code 100)
RUN apt-get update && apt-get install -y wget curl unzip gnupg fonts-liberation libappindicator3-1 xdg-utils lsb-release \
    # Google Chrome Signing Key को Securely Add करें
    && wget -q -O /tmp/google.pub https://dl.google.com/linux/linux_signing_key.pub \
    && gpg --no-default-keyring --keyring /usr/share/keyrings/google-chrome.gpg --import /tmp/google.pub \
    # Chrome Repository Source को Add करें
    && echo "deb [arch=amd64 signed-by=/usr/share/keyrings/google-chrome.gpg] http://dl.google.com/linux/chrome/deb/ stable main" \
      > /etc/apt/sources.list.d/google-chrome.list \
    # Chrome और बाकी डिपेंडेंसी इंस्टॉल करें
    && apt-get update && apt-get install -y google-chrome-stable \
    # Temporary files हटाएं
    && rm -rf /var/lib/apt/lists/* /tmp/google.pub

# 2. Install ChromeDriver (Using version 141)
RUN wget -O /tmp/chromedriver.zip https://storage.googleapis.com/chrome-for-testing-public/141.0.7390.0/linux64/chromedriver-linux64.zip \
    && unzip /tmp/chromedriver.zip -d /usr/local/bin/ \
    && mv /usr/local/bin/chromedriver-linux64/chromedriver /usr/local/bin/chromedriver \
    && chmod +x /usr/local/bin/chromedriver \
    && rm -rf /tmp/chromedriver.zip

# 3. Install ALL required Selenium dependencies (FIX for NoClassDefFoundError)
RUN mkdir -p /app/lib && \
    wget -q -P /app/lib https://repo1.maven.org/maven2/org/seleniumhq/selenium/selenium-java/4.37.0/selenium-java-4.37.0.jar && \
    wget -q -P /app/lib https://repo1.maven.org/maven2/org/seleniumhq/selenium/selenium-api/4.37.0/selenium-api-4.37.0.jar && \
    wget -q -P /app/lib https://repo1.maven.org/maven2/org/seleniumhq/selenium/selenium-chrome-driver/4.37.0/selenium-chrome-driver-4.37.0.jar && \
    wget -q -P /app/lib https://repo1.maven.org/maven2/org/seleniumhq/selenium/selenium-chromium-driver/4.37.0/selenium-chromium-driver-4.37.0.jar && \
    wget -q -P /app/lib https://repo1.maven.org/maven2/org/seleniumhq/selenium/selenium-remote-driver/4.37.0/selenium-remote-driver-4.37.0.jar && \
    wget -q -P /app/lib https://repo1.maven.org/maven2/org/seleniumhq/selenium/selenium-support/4.37.0/selenium-support-4.37.0.jar && \
    wget -q -P /app/lib https://repo1.maven.org/maven2/com/google/guava/guava/33.2.0-jre/guava-33.2.0-jre.jar

# 4. Environment Variables
ENV CHROME_BIN=/usr/bin/google-chrome
ENV PATH="/usr/local/bin:$PATH"

# 5. Copy application and set working directory
WORKDIR /app
# COPY . /app will copy your JAR file and any other files present in the repo.
COPY . /app

# 6. Command to run the application
# -cp /app/lib/* ensures all downloaded selenium JARs are on the classpath.
CMD ["java", "-cp", "/app:/app/lib/*", "BraveScreenshotBot"]
