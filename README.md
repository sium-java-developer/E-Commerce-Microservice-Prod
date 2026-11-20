# E-Commerce Microservice Project

This project is a microservice-based E-Commerce application designed to handle product management. It leverages a modern tech stack including Spring Boot, Apache Kafka, and PostgreSQL, containerized with Docker for easy development and deployment.

## Project Architecture

The system consists of the following components:

*   **Product Server (`productServer`)**: The backend microservice exposing REST APIs for product operations. It connects to PostgreSQL for data persistence and Kafka for event messaging.
*   **Product Web (`productWeb`)**: A frontend web application that interacts with the Product Server and consumes Kafka events.
*   **Kafka Channel (`kafkaChannel`)**: A shared library/module handling Kafka configuration and request-reply patterns.
*   **Infrastructure**:
    *   **PostgreSQL**: Relational database for storing product data.
    *   **Apache Kafka & Zookeeper**: Message broker for asynchronous communication between services.

## Prerequisites

*   Java 17 or higher
*   Docker and Docker Compose
*   Maven

## Local Development Setup

You can run the entire stack locally using Docker Compose.

1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    cd project/product
    ```

2.  **Start the services:**
    ```bash
    docker-compose up --build
    ```
    This command will build the Docker images for the backend and frontend, and start them alongside Kafka, Zookeeper, and PostgreSQL containers.

3.  **Access the application:**
    *   **Frontend:** http://localhost:8099
    *   **Backend API:** http://localhost:8098

