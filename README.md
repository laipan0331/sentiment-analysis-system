Sentiment Analysis System 

A web application for analyzing sentiment in user comments with administrative capabilities for content management, containerized with Docker. 

Description 

This Sentiment Analysis System is a Spring Boot application designed to analyze the sentiment of user comments and provide visual representations of sentiment distribution. It features user authentication, comment submission and analysis, and administrative tools for user and comment management. The application is containerized with Docker for easy deployment and scalability. 

Features 

User Authentication: Secure login and registration system 

Sentiment Analysis: Analyzes comments and categorizes them as very positive, positive, neutral, negative, or very negative 

Visualization: Displays sentiment distribution in charts 

Admin Dashboard: Allows administrators to manage users and comments 

Role-Based Access Control: Differentiates between regular users and administrators 

Containerization: Packaged as a Docker container for consistent deployment across environments 

Technologies Used 

Backend: Java 17, Spring Boot 3.5.0-M3 

Frontend: HTML, CSS, Thymeleaf 

Database: H2 Database (embedded) 

Security: Spring Security 

NLP: Stanford CoreNLP for sentiment analysis 

Visualization: Chart.js 

Containerization: Docker 

Project Structure 

src/ 

├── main/ 

│   ├── java/ 

│   │   └── com/ 

│   │       └── lmz/ 

│   │           └── sentiment_analysis/ 

│   │               ├── config/ 

│   │               ├── controller/ 

│   │               ├── model/ 

│   │               ├── repository/ 

│   │               ├── security/ 

│   │               ├── service/ 

│   │               └── SentimentAnalysisApplication.java 

│   └── resources/ 

│       ├── templates/ 

│       │   ├── admin/ 

│       │   └── [HTML templates] 

│       └── application.properties 

├── Dockerfile 

├── docker-compose.yml 

└── [other project files] 

Docker Setup 

Prerequisites 

Docker installed on your system 

Docker Compose (included with Docker Desktop for Windows/Mac) 

Docker Build and Run 

Build the Docker image  

docker build -t sentiment-analysis-system . 

Run the container  

docker run -p 8080:8080 sentiment-analysis-system 

Using Docker Compose 

Start the application  

docker-compose up 

Stop the application  

docker-compose down 

Dockerfile 

FROM eclipse-temurin:17-jdk-alpine as build 

WORKDIR /workspace/app 

 

COPY mvnw . 

COPY .mvn .mvn 

COPY pom.xml . 

COPY src src 

 

RUN ./mvnw install -DskipTests 

RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar) 

 

FROM eclipse-temurin:17-jre-alpine 

VOLUME /tmp 

ARG DEPENDENCY=/workspace/app/target/dependency 

COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib 

COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF 

COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app 

ENTRYPOINT ["java","-cp","app:app/lib/*","com.lmz.sentiment_analysis.SentimentAnalysisApplication"] 

Docker Compose Configuration 

yaml 

version: '3.8' 

services: 

  app: 

    build: . 

    ports: 

      - "8080:8080" 

    environment: 

      - SPRING_PROFILES_ACTIVE=prod 

    volumes: 

      - ./data:/app/data 

Usage 

Accessing the Application 

Once the Docker container is running: 

Open a web browser and navigate to http://localhost:8080 

Default admin credentials:  

Username: admin 

Password: admin123 

User Functions 

Register a new account at /register 

Log in using your credentials at /login 

Submit comments on the main page 

View sentiment analysis results after submitting a comment 

View global sentiment distribution by clicking "View Sentiment Distribution" 

Admin Functions 

Access admin dashboard by logging in as an admin and clicking "Admin Dashboard" 

Manage users: View all users and delete users if necessary 

Manage comments: View all comments and delete inappropriate content 

Persistent Storage 

The application uses an H2 database file stored in a Docker volume (./data:/app/data) to ensure data persistence between container restarts. 

License 

This project is licensed under the Apache License 2.0. 

Acknowledgments 

Stanford CoreNLP for providing powerful natural language processing capabilities 

Spring Boot team for the excellent framework 

Chart.js for visualization components 

 

Deployment Considerations 

The default configuration uses an embedded H2 database. For production, consider using an external database service 

Configure proper authentication for production use 

Set up proper logging and monitoring when deployed in a production environment 

 