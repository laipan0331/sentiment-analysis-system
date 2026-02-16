#!/bin/bash

# Sentiment & Threat Text Analysis System - Quick Start Script

echo "========================================"
echo " Sentiment & Threat Text Analysis System"
echo " Quick Start Launcher"
echo "========================================"
echo ""

# Check if Java is available
if ! command -v java &> /dev/null; then
    echo "❌ Error: Java is not installed or not in PATH"
    echo "Please install Java 17 or higher"
    echo "Download from: https://adoptium.net/"
    exit 1
fi

echo "✅ Java detected:"
java -version
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "⚠️  Warning: Docker is not running"
    echo ""
    echo "Choose your startup mode:"
    echo "  1. Quick Mode (H2 database, no Docker needed)"
    echo "  2. Exit and start Docker first"
    echo ""
    read -p "Enter choice (1 or 2): " choice
    
    if [ "$choice" = "1" ]; then
        mode="quick"
    else
        echo ""
        echo "Please start Docker Desktop and run this script again"
        exit 1
    fi
else
    echo "✅ Docker detected"
    echo ""
    echo "Choose your startup mode:"
    echo "  1. Quick Mode (H2 only, starts immediately)"
    echo "  2. Full Mode (MySQL + Redis + App, Docker Compose)"
    echo ""
    read -p "Enter choice (1 or 2): " choice
    
    if [ "$choice" = "2" ]; then
        mode="docker"
    else
        mode="quick"
    fi
fi

if [ "$mode" = "quick" ]; then
    echo ""
    echo "========================================"
    echo " Starting in Quick Mode"
    echo " - Database: H2 (in-memory)"
    echo " - Cache: Disabled"
    echo " - LLM: CoreNLP only"
    echo "========================================"
    echo ""
    
    if [ ! -f target/sentiment-analysis-0.0.1-SNAPSHOT.jar ]; then
        echo "❌ Error: JAR file not found"
        echo "Please run: mvn clean package"
        exit 1
    fi
    
    echo "🚀 Starting application..."
    java -jar target/sentiment-analysis-0.0.1-SNAPSHOT.jar \
        --spring.datasource.url=jdbc:h2:mem:testdb \
        --spring.datasource.driver-class-name=org.h2.Driver \
        --spring.jpa.database-platform=org.hibernate.dialect.H2Dialect \
        --spring.cache.type=none \
        --openai.api.key=test-key &
    
    APP_PID=$!
    echo ""
    echo "⏳ Application is starting (PID: $APP_PID)..."
    echo "⏳ Please wait 20-30 seconds for startup"
    echo ""
    sleep 5
    
    if command -v xdg-open &> /dev/null; then
        xdg-open http://localhost:8080
    elif command -v open &> /dev/null; then
        open http://localhost:8080
    fi
    
    echo ""
    echo "========================================"
    echo " Application Started Successfully!"
    echo "========================================"
    echo ""
    echo "🌐 Access: http://localhost:8080"
    echo "🔑 Login:  admin / admin123"
    echo ""
    echo "To stop: kill $APP_PID"
    echo ""
    
elif [ "$mode" = "docker" ]; then
    echo ""
    echo "========================================"
    echo " Starting in Full Mode (Docker)"
    echo " - Database: MySQL"
    echo " - Cache: Redis"
    echo " - LLM: OpenAI (if configured)"
    echo "========================================"
    echo ""
    
    # Check if .env file exists
    if [ ! -f .env ]; then
        echo "📝 Creating .env file from template..."
        cp .env.example .env
        echo "⚠️  Please edit .env file and set your OPENAI_API_KEY"
        echo "   (Optional: System will work without it)"
        echo ""
    fi
    
    # Start services
    echo "🐳 Starting Docker Compose services..."
    docker compose up -d
    
    # Wait for services to be ready
    echo ""
    echo "⏳ Waiting for services to be ready..."
    sleep 15
    
    # Check service health
    echo ""
    echo "📊 Service Status:"
    docker compose ps
    
    echo ""
    sleep 3
    
    if command -v xdg-open &> /dev/null; then
        xdg-open http://localhost:8080
    elif command -v open &> /dev/null; then
        open http://localhost:8080
    fi
    
    echo ""
    echo "========================================"
    echo " Application Started Successfully!"
    echo "========================================"
    echo ""
    echo "🌐 Access: http://localhost:8080"
    echo "🔑 Login:  admin / admin123"
    echo ""
    echo "📚 Useful commands:"
    echo "   View logs:     docker compose logs -f app"
    echo "   Stop services: docker compose down"
    echo "   Restart:       docker compose restart"
    echo ""
fi
