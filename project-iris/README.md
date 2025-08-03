# Project Iris

## AI-Powered Threat Detection Engine

Project Iris is a proof-of-concept system using machine learning to analyze email metadata and content for anomalous patterns indicative of sophisticated, zero-day phishing attacks.

## Key Features

- **Advanced ML Analysis**: Uses deep learning algorithms to detect patterns that signature-based systems miss
- **Zero-Day Detection**: Identifies previously unknown attack patterns
- **Metadata Analysis**: Examines email header information, timing, sender patterns, and other metadata
- **Content Analysis**: Uses NLP to analyze email content for suspicious elements
- **Continuous Learning**: Improves detection accuracy over time

## Technical Stack

- Java 24
- Spring Boot 3.2
- Jakarta EE 10
- PostgreSQL
- DeepLearning4J
- Spring Security with JWT

## Getting Started

### Prerequisites

- JDK 24
- Maven 3.8+
- PostgreSQL 15+

### Setup

1. Clone the repository
   ```
   git clone https://github.com/mriridescent/project-iris.git
   cd project-iris
   ```

2. Configure database connection in `application.properties`

3. Build the application
   ```
   mvn clean install
   ```

4. Run the application
   ```
   mvn spring-boot:run
   ```

## API Documentation

Access Swagger UI at: http://localhost:8080/swagger-ui.html

## License

Copyright © 2025 @mriridescent
