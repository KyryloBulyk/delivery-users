# User Microservice

## Overview
The User Microservice is a fundamental part of the **Delivery** application, designed to enhance and streamline operations within a comprehensive delivery system. This microservice is tasked with managing all user-related operations such as registration, authentication, profile management, and account deletion.

## Architecture Interaction
As a pivotal element of the Delivery application's microservice architecture, the User Microservice interacts with several other components to ensure a unified and efficient user experience. These interactions include:

- **Delivery Api Gateway**: Serves as the primary entry point for user authentication and session management. It routes user authentication requests to the User Microservice and handles session tokens. [GitHub Repository](https://github.com/KyryloBulyk/delivery-api-gateway).

- **Delivery Configuration**: This service is responsible for centralizing and managing configurations across all microservices, including user-related settings and preferences. [GitHub repository](https://github.com/KyryloBulyk/delivery-configuration).

- **Delivery Discovery**: Facilitates service discovery within the microservices architecture, allowing the User Microservice to communicate seamlessly with other services in the ecosystem. [GitHub Repository](https://github.com/KyryloBulyk/delivery-discovery).

The above interactions highlight the User Microservice's crucial role within the Delivery application's ecosystem, contributing to its overall functionality and user-centric approach.

## Getting Started

### Prerequisites
Ensure you have the following prerequisites installed and configured on your system before proceeding:
- Java 11 or later.
- Maven (for managing project dependencies and executions).
- Access to a PostgreSQL database for data persistence.

### Configuration
Configure your environment by setting up the `application.properties` file with the necessary database connection details and any other environment-specific configurations.

### Running the Application
To launch the User Microservice, follow these steps:

1. Open your terminal and navigate to the root directory of the project.
2. Execute the Maven command below to compile the project, apply any necessary database migrations, and start the Spring Boot application:

```bash
mvn spring-boot:run
