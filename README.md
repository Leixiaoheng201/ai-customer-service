# AI Customer Service

A Java-based intelligent customer service system with routing, compression, and data closure capabilities.

## Features
- **Smart Request Routing**: Efficiently routes customer inquiries to appropriate handlers
- **Data Compression**: Reduces network payload size while maintaining data integrity
- **Secure Data Closure**: Ensures sensitive customer information is properly handled and secured

## Technical Stack
- **Language**: Java 17
- **Framework**: Spring Boot 3.2
- **Build Tool**: Maven
- **AI Integration**: TensorFlow Lite for on-device inference
- **API Gateway**: Spring Cloud Gateway
- **Database**: PostgreSQL with JDBC Template
- **Logging**: SLF4J with Logback

## Installation

```bash
# Clone the repository
git clone https://github.com/Leixiaoheng201/ai-customer-service.git

# Build the project
mvn clean install

# Run the application
java -jar target/ai-customer-service.jar
```

## Usage

After starting the application, the service will be available at `http://localhost:8080`.

- Submit customer inquiries via POST to `/api/v1/inquiries`
- View system status at `/actuator/health`

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.