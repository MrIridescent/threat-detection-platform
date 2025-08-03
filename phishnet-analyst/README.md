# PhishNet Analyst

## Phishing Campaign Visualization Tool

PhishNet Analyst is an interactive tool developed to map out the infrastructure and logical flow of complex phishing campaigns, aiding in strategic takedowns and defensive posture planning.

## Key Features

- **Campaign Mapping**: Visualize connections between phishing infrastructure nodes
- **Attack Vector Analysis**: Track and analyze different attack vectors used in campaigns
- **Real-time Monitoring**: Track campaign status and evolution
- **Tactical Planning**: Develop and document mitigation strategies
- **Interactive Visualizations**: Explore campaign infrastructure through interactive graphs

## Technical Stack

- Java 24
- Spring Boot 3.2
- Jakarta EE 10
- PostgreSQL
- D3.js for visualization
- Vis.js Network for interactive graphs
- Spring Security with JWT

## Getting Started

### Prerequisites

- JDK 24
- Maven 3.8+
- PostgreSQL 15+

### Setup

1. Clone the repository
   ```
   git clone https://github.com/mriridescent/phishnet-analyst.git
   cd phishnet-analyst
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

## Web Interface

Access the visualization dashboard at: http://localhost:8081/

## API Documentation

Access Swagger UI at: http://localhost:8081/swagger-ui.html

## License

Copyright © 2025 @mriridescent
