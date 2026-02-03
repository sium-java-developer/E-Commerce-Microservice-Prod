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

## Production Deployment (Render + Aiven)

This project is configured for deployment on Render using `render.yaml` as Infrastructure as Code (IaC).

### Services Configuration

*   **Database**: Managed PostgreSQL on Render (Free Tier).
*   **Kafka**: Aiven for Kafka (Startup/Free Tier).
*   **Backend & Frontend**: Render Web Services (Docker runtime).

### Deployment Steps

1.  **Aiven Setup**:
    *   Create a Kafka service on Aiven.
    *   Download the `ca.pem` certificate and place it in the root of this project (`project/product/ca.pem`).
    *   Note your Service URI, Username (`avnadmin`), and Password.

2.  **Render Setup**:
    *   Create a new **Blueprint** on Render and connect this repository.
    *   Render will detect `render.yaml`.

3.  **Environment Variables (Secrets)**:
    You will be prompted to provide the following secrets during Blueprint creation:
    *   `SPRING_KAFKA_BOOTSTRAP_SERVERS`: Your Aiven Service URI (e.g., `kafka-xyz.aivencloud.com:21248`).
    *   `SPRING_KAFKA_PROPERTIES_SASL_JAAS_CONFIG`:
        ```
        org.apache.kafka.common.security.scram.ScramLoginModule required username="avnadmin" password="YOUR_AIVEN_PASSWORD";
        ```

### SSL Configuration

The project uses a custom `ca.pem` for connecting to Aiven's Kafka. The `Dockerfile`s include steps to import this certificate into the container's Java Truststore.