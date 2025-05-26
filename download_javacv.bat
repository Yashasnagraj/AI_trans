@echo off
echo JavaCV Download Script
echo ======================

REM Check if JavaCV already exists
if exist "javacv-platform-1.5.8.jar" (
    echo JavaCV already exists in this directory.
    echo File: javacv-platform-1.5.8.jar
    pause
    exit /b 0
)

echo Downloading JavaCV platform JAR...
echo This may take a few minutes depending on your internet connection.
echo.

REM Try to download using PowerShell (Windows 10+)
powershell -Command "& {try { Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/bytedeco/javacv-platform/1.5.8/javacv-platform-1.5.8.jar' -OutFile 'javacv-platform-1.5.8.jar' -UseBasicParsing; Write-Host 'Download completed successfully!'; } catch { Write-Host 'Download failed. Please download manually.'; exit 1; }}"

if %ERRORLEVEL% neq 0 (
    echo.
    echo Download failed. Please download JavaCV manually:
    echo.
    echo 1. Go to: https://github.com/bytedeco/javacv/releases
    echo 2. Download: javacv-platform-1.5.8.jar
    echo 3. Place it in this directory
    echo.
    echo Alternative direct link:
    echo https://repo1.maven.org/maven2/org/bytedeco/javacv-platform/1.5.8/javacv-platform-1.5.8.jar
    echo.
    pause
    exit /b 1
)

echo.
echo JavaCV downloaded successfully!
echo You can now run: compile_and_run.bat
pause
