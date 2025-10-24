# Stage 1: Use a reliable Java 17 base image
FROM openjdk:17-slim

# 1. Install dependencies & Chrome repository (using modern keyrings method)
RUN apt-get update && apt-get install -y wget curl unzip gnupg fonts-liberation libappindicator3-1 xdg-utils \
    && mkdir -p /etc/apt/keyrings \
    && wget -q -O /usr/share/keyrings/google-chrome-keyring.gpg https://dl.google.com/linux/linux_signing_key.pub \
    && echo "deb [arch=amd64 signed-by=/usr/share/keyrings/google-chrome-keyring.gpg] http://dl.google.com/linux/chrome/deb/ stable main" \
      > /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update && apt-get install -y google-chrome-stable \
    && rm -rf /var/lib/apt/lists/*

# 2. Install ChromeDriver (Using your specific version, 141)
# Note: For production stability, you might want a more dynamic way to get the latest compatible version.
RUN wget -O /tmp/chromedriver.zip https://storage.googleapis.com/chrome-for-testing-public/141.0.7390.0/linux64/chromedriver-linux64.zip \
    && unzip /tmp/chromedriver.zip -d /usr/local/bin/ \
    && mv /usr/local/bin/chromedriver-linux64/chromedriver /usr/local/bin/chromedriver \
    && chmod +x /usr/local/bin/chromedriver \
    && rm -rf /tmp/chromedriver.zip

# 3. Setup Classpath for dependencies (assuming /app/lib has all the necessary JARs)
# Your build system should ensure these JARs are present. Since you listed them in the original Dockerfile, 
# I am removing the manual wget lines to simplify, assuming you copy them correctly.

# 4. Environment Variables
# Set the CHROME_BIN for the Java code to detect the path.
ENV CHROME_BIN=/usr/bin/google-chrome
ENV PATH="/usr/local/bin:$PATH"
ENV CLASSPATH="/app/lib/*"

# 5. Copy application and dependencies
WORKDIR /app
# We assume 'BraveScreenshotBot.jar' and the 'lib' folder with all selenium JARs are in the root directory.
COPY BraveScreenshotBot.jar /app/
COPY selenium-java-4.37.0 /app/lib 

# 6. Command to run the application
# Run with classpath including the current directory (for BraveScreenshotBot.jar) and the lib folder.
CMD ["java", "-cp", "/app/BraveScreenshotBot.jar:/app/lib/*", "BraveScreenshotBot"]