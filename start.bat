@echo off
REM Sentiment & Threat Text Analysis System - Quick Start Script for Windows

echo ========================================
echo  Sentiment ^& Threat Text Analysis System
echo  Quick Start Launcher
echo ========================================
echo.

REM Check if Java is available
java -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java is not installed or not in PATH
    echo Please install Java 17 or higher
    echo Download from: https://adoptium.net/
    exit /b 1
)

echo [INFO] Java detected
java -version
echo.

REM Check if Docker is running
docker info >nul 2>&1
if errorlevel 1 (
    echo [WARNING] Docker is not running
    echo.
    echo Choose your startup mode:
    echo   1. Quick Mode (H2 database, no Docker needed)
    echo   2. Exit and start Docker first
    echo.
    set /p choice="Enter choice (1 or 2): "
    
    if "!choice!"=="1" (
        goto :quick_mode
    ) else (
        echo.
        echo Please start Docker Desktop and run this script again
        pause
        exit /b 1
    )
) else (
    echo [INFO] Docker detected
    echo.
    echo Choose your startup mode:
    echo   1. Quick Mode (H2 only, starts immediately)
    echo   2. Full Mode (MySQL + Redis + App, Docker Compose)
    echo.
    set /p choice="Enter choice (1 or 2): "
    
    if "!choice!"=="2" goto :docker_mode
)

:quick_mode
echo.
echo ========================================
echo  Starting in Quick Mode
echo  - Database: H2 (in-memory)
echo  - Cache: Disabled
echo  - LLM: CoreNLP only
echo ========================================
echo.

if not exist target\sentiment-analysis-0.0.1-SNAPSHOT.jar (
    echo [ERROR] JAR file not found
    echo Please run: mvn clean package
    exit /b 1
)

echo [INFO] Starting application...
start "Sentiment Analysis" java -jar target/sentiment-analysis-0.0.1-SNAPSHOT.jar --spring.datasource.url=jdbc:h2:mem:testdb --spring.datasource.driver-class-name=org.h2.Driver --spring.jpa.database-platform=org.hibernate.dialect.H2Dialect --spring.cache.type=none --openai.api.key=test-key

echo.
echo [INFO] Application is starting...
echo [INFO] Please wait 20-30 seconds for startup
echo.
timeout /t 5 /nobreak >nul
echo Opening browser...
start http://localhost:8080
echo.
echo ========================================
echo  Application Started Successfully!
echo ========================================
echo.
echo Access: http://localhost:8080
echo Login:  admin / admin123
echo.
echo Press Ctrl+C in the application window to stop
echo.
pause
exit /b 0

:docker_mode
echo.
echo ========================================
echo  Starting in Full Mode (Docker)
echo  - Database: MySQL
echo  - Cache: Redis
echo  - LLM: OpenAI (if configured)
echo ========================================
echo.

REM Check if .env file exists
if not exist .env (
    echo [INFO] Creating .env file from template...
    copy .env.example .env >nul
    echo [WARNING] Please edit .env file and set your OPENAI_API_KEY
    echo           ^(Optional: System will work without it^)
    echo.
)

REM Start services
echo [INFO] Starting Docker Compose services...
docker compose up -d

REM Wait for services to be ready
echo.
echo [INFO] Waiting for services to be ready...
timeout /t 15 /nobreak >nul

REM Check service health
echo.
echo [INFO] Service Status:
docker compose ps

echo.
timeout /t 5 /nobreak >nul
echo Opening browser...
start http://localhost:8080

echo.
echo ========================================
echo  Application Started Successfully!
echo ========================================
echo.
echo Access: http://localhost:8080
echo Login:  admin / admin123
echo.
echo Useful commands:
echo   View logs:     docker compose logs -f app
echo   Stop services: docker compose down
echo   Restart:       docker compose restart
echo.
pause
exit /b 0
