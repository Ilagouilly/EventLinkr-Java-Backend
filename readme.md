# EventLinkr: Revolutionizing Event Networking

## 🌟 Project Overview

EventLinkr is an innovative event management platform designed to transform networking experiences by leveraging cutting-edge technology and intelligent connection recommendations.

![version](https://img.shields.io/badge/version-0.0.1-blue) ![Build Status](https://img.shields.io/github/actions/workflow/status/ilagouilly/EventLinkr-Java-Backend/.github/workflows/user-service-ci.yml) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Ilagouilly_EventLinkr-Java-Backend&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Ilagouilly_EventLinkr-Java-Backend) [![CodeFactor](https://www.codefactor.io/repository/github/ilagouilly/eventlinkr-java-backend/badge/main)](https://www.codefactor.io/repository/github/ilagouilly/eventlinkr-java-backend/overview/main) ![Coverage](https://codecov.io/gh/ilagouilly/EventLinkr-Java-Backend/branch/main/graph/badge.svg) ![Issues](https://img.shields.io/github/issues/ilagouilly/EventLinkr-Java-Backend) ![Pull Requests](https://img.shields.io/github/issues-pr/ilagouilly/EventLinkr-Java-Backend) <a href="https://sonarcloud.io/summary/new_code?id=Ilagouilly_EventLinkr-Java-Backend">
  <img src="https://sonarcloud.io/images/project_badges/sonarcloud-light.svg" width="90">
</a>


## 🛠 Tech Stack

![Java](https://img.shields.io/badge/Java-21-red?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green?style=for-the-badge&logo=springboot)
![Reactive](https://img.shields.io/badge/Reactive-WebFlux-blue?style=for-the-badge)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![GraphQL](https://img.shields.io/badge/-GraphQL-E10098?style=for-the-badge&logo=graphql&logoColor=white)
![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-000?style=for-the-badge&logo=apachekafka)
![Redis](https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)

### Architecture

- Microservices Architecture
- Domain-Driven Design (DDD)
- Reactive Programming
- Event-Driven Communication

## 🔧 Key Technologies

- **Language**: Java 21
- **Framework**: Spring Boot 3.4.0
- **Reactive Programming**: Spring WebFlux
- **Database**: PostgreSQL
- **ORM**: Spring Data R2DBC
- **Authentication**: Spring Security, OAuth 2.0
- **API**: GraphQL, REST
- **Messaging**: Apache Kafka
- **Caching**: Redis

## 📦 Microservices

| Service              | Responsibility                   |
| -------------------- | -------------------------------- |
| User Service         | Authentication & User Management |
| Event Service        | Event Creation & Management      |
| Notification Service | Communication & Alerts           |
| Auth Service         | Security & Access Control        |

## 🚀 Getting Started

### Prerequisites

- Java 21 JDK
- Docker
- Docker Compose

### Local Development Setup

1. Clone the repository

   ```bash
   git clone https://github.com/ilagouilly/EventLinkr-Java-Backend.git
   cd EventLinkr-Java-Backend
   ```

2. Build the project

   ```bash
   ./mvnw clean install
   ```

3. Run with Docker Compose
   ```bash
   docker-compose up --build
   ```

## 🔒 Security Features

- JWT Token-Based Authentication
- OAuth 2.0 Integration
- Role-Based Access Control
- Secure Microservices Communication

## 🤝 Contributing

![Contributions Welcome](https://img.shields.io/badge/contributions-welcome-brightgreen?style=for-the-badge&logo=github)

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/NetworkingInnovation`)
3. Commit your changes (`git commit -m 'Add intelligent matching algorithm'`)
4. Push to the branch (`git push origin dev/NetworkingInnovation`)
5. Open a Pull Request

## 📄 License

[![Licence](https://img.shields.io/github/license/Ileriayo/markdown-badges?style=for-the-badge)](./LICENSE)

Distributed under the MIT License. See `LICENSE` for more information.

---
