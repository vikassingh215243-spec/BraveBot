# Use official OpenJDK image
FROM openjdk:17-slim

# Install Chrome and dependencies
RUN apt-get update && apt-get install -y wget gnupg unzip \
    && wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && sh -c 'echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list' \
    && apt-get update && apt-get install -y google-chrome-stable \
    && rm -rf /var/lib/apt/lists/*

# Install ChromeDriver
RUN LATEST=$(wget -q -O - https://chromedriver.storage.googleapis.com/LATEST_RELEASE) \
    && wget -O /tmp/chromedriver.zip https://chromedriver.storage.googleapis.com/$LATEST/chromedriver_linux64.zip \
    && unzip /tmp/chromedriver.zip -d /usr/local/bin/ \
    && rm /tmp/chromedriver.zip

# Set Chrome binary path for Selenium
ENV CHROME_BINARY=/usr/bin/google-chrome
ENV PATH="$PATH:/usr/local/bin"

# Create working directory
WORKDIR /app

# Copy files
COPY . /app

# Set classpath for Selenium jars (if any)
ENV CLASSPATH="/app/lib/*"

# Run the bot
CMD ["java", "-cp", "/app:/app/lib/*", "BraveScreenshotBot"]
