@echo off
echo JavaCV Video Transition Engine - Compile and Run Script
echo ========================================================

REM Check if JavaCV JAR files exist
if not exist "javacv-platform-1.5.8.jar" (
    echo Error: javacv-platform-1.5.8.jar not found!
    echo Please download JavaCV from: https://github.com/bytedeco/javacv/releases
    echo Place javacv-platform-1.5.8.jar in this directory.
    pause
    exit /b 1
)

echo Compiling Java files...
javac -cp "javacv-platform-1.5.8.jar" *.java

if %ERRORLEVEL% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Compilation successful!
echo.

REM Check command line arguments
if "%~3"=="" (
    echo Usage: compile_and_run.bat ^<video1^> ^<video2^> ^<output_directory^>
    echo.
    echo Example:
    echo   compile_and_run.bat input1.mp4 input2.mp4 ./output
    echo.
    echo This will create demo videos for all available transitions.
    pause
    exit /b 1
)

REM Create output directory if it doesn't exist
if not exist "%~3" mkdir "%~3"

echo Running TransitionDemo...
echo Input Video 1: %~1
echo Input Video 2: %~2
echo Output Directory: %~3
echo.

java -cp ".;javacv-platform-1.5.8.jar" TransitionDemo "%~1" "%~2" "%~3"

if %ERRORLEVEL% neq 0 (
    echo Demo execution failed!
    pause
    exit /b 1
)

echo.
echo Demo completed successfully!
echo Check the output directory for all transition examples.
pause
