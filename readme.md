# EventLinkr Backend

## Project Overview
EventLinkr is a modern, scalable event management platform built with cutting-edge Java technologies.

## Tech Stack
- Java 21
- Spring Boot 3.x
- Reactive Programming
- Microservices Architecture
- AWS Deployment

## Prerequisites
- Java 21
- Docker
- Kubernetes (optional)
- AWS Account (for deployment)

## Local Development Setup

### Prerequisites
- Install Java 21 JDK
- Install Docker
- Install Docker Compose

### Steps
1. Clone the repository
   ```bash
   git clone https://github.com/[YOUR_USERNAME]/eventlinkr-backend.git
   cd eventlinkr-backend
   ```

2. Build the project
   ```bash
   ./mvnw clean install
   ```

3. Run with Docker Compose
   ```bash
   docker-compose up --build
   ```

## Microservices
- User Service: Manages user accounts and authentication
- Event Service: Handles event creation, management, and discovery
- Notification Service: Manages event communications

## Contributing
1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License
Distributed under the MIT License. See `LICENSE` for more information.

## Contact
Your Name - your.email@example.com

Project Link: [https://github.com/[YOUR_USERNAME]/eventlinkr-backend](https://github.com/[YOUR_USERNAME]/eventlinkr-backend)