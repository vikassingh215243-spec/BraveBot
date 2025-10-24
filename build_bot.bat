@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

echo 🔧 Compiling BraveScreenshotBot.java...

REM Include all JAR files in selenium-java-4.37.0 folder
set CLASSPATH=.
for %%f in (selenium-java-4.37.0\*.jar) do (
    set CLASSPATH=!CLASSPATH!;%%f
)

javac -encoding UTF-8 -cp "!CLASSPATH!" BraveScreenshotBot.java
if %errorlevel% neq 0 (
    echo ❌ Compilation failed.
    pause
    exit /b
)

echo ⚙️ Creating JAR package...
jar cfe BraveScreenshotBot.jar BraveScreenshotBot *.class

echo ✅ Build complete! BraveScreenshotBot.jar ready.
pause
endlocal
