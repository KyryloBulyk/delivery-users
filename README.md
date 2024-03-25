# 🚹 Delivery Users Microservice

## 📌 Overview

The Delivery Users Microservice 🧑‍💼 is an integral component of the **Delivery** microservices architecture, dedicated to handling all aspects of user management. This includes user registration 📝, authentication 🔐, profile management 🔄, and account deletions 🗑️. It serves as the backbone for managing user data and ensuring secure access across the Delivery application.

## 🌐 Architecture Interaction

As a core element of the Delivery microservices architecture, the Users Microservice interacts closely with several other services to provide a comprehensive and seamless user experience:

- [Delivery API Gateway](https://github.com/KyryloBulyk/delivery-api-gateway) 🚪: Acts as the entry point for routing user-related requests to this microservice, handling user authentication and authorization via JWT tokens.

- [Delivery Configuration](https://github.com/KyryloBulyk/delivery-configuration) ⚙️: Provides centralized configuration management, enabling the Users Microservice to adapt dynamically to configuration changes.

- [Delivery Discovery](https://github.com/KyryloBulyk/delivery-discovery) 🔍: Enables efficient service discovery, allowing the Users Microservice to communicate and interact with other microservices within the ecosystem.

This collaborative environment ensures that user-related functionalities are handled efficiently, contributing to the robustness of the Delivery application.

## 🚀 Features

- **User Management**: Comprehensive handling of user data, including registration, authentication, and profile updates.
- **Security**: Utilizes Spring Security and JWT for robust authentication and authorization mechanisms.
- **Unit Testing**: Includes a suite of unit tests to ensure the reliability and functionality of the service.
- **Continuous Integration**: Integrated with GitHub Actions for continuous testing and deployment workflows.
- **Swagger Documentation**: Offers detailed API documentation accessible via [Swagger UI](http://localhost:8080/swagger-ui/), providing insights into the available endpoints and their usage.

## 📦 Running via Docker Compose

The Users Microservice can be launched as part of the Delivery application using Docker Compose, orchestrated through the Delivery API Gateway. This setup simplifies the deployment process and ensures seamless interaction between services.

1. Ensure Docker 🐳 and Docker Compose are installed on your machine.
2. Clone the Delivery API Gateway application repository containing the `docker-compose.yml` file.
3. Run the following command in the root directory of the cloned repository:

   ```bash
   docker-compose up
    ```

This will start all the microservices, including the Users Microservice, as defined in the Docker Compose configuration.

## 🤝 Contributing

Contributions to improve the Users Microservice or the Delivery application as a whole are highly appreciated. Whether it's enhancing features, fixing bugs, or improving documentation, your contributions are welcome. Please check the project's GitHub repository for contribution guidelines.