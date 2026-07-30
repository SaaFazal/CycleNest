# CycleNest API

![Java](https://img.shields.io/badge/Java-Backend-ED8B00?style=for-the-badge&logo=java)
![Azure Cosmos DB](https://img.shields.io/badge/Azure-Cosmos%20DB-0089D6?style=for-the-badge&logo=microsoft-azure)
![JMeter](https://img.shields.io/badge/Apache-JMeter-D22128?style=for-the-badge&logo=apache)

A multi-tenant e-commerce RESTful API built for high availability and extremely low latency. Designed to handle complex geospatial data and scalable product inventories for modern retail applications.

## 🚀 Key Features

*   **Azure Cosmos DB Integration:** Utilizes a highly scalable NoSQL backend designed for massive throughput and global distribution.
*   **Geospatial Queries:** Executes real-time distance calculations and location-based searches utilizing spatial indexing, the Haversine formula, and Azure Maps integration.
*   **Secure Authentication:** Employs JWT-based (JSON Web Token) user authentication, route protection, and role-based access management.
*   **High Performance Architecture:** Architected from the ground up to maintain low latency under heavy load. The system was rigorously load tested using Apache JMeter and validated to handle 5,000+ requests per second without degradation.

## 🛠️ Technology Stack

*   **Backend Core:** Java
*   **Database:** Azure Cosmos DB (NoSQL)
*   **Cloud Services:** Azure Maps API
*   **Security:** JWT (JSON Web Tokens)
*   **Performance Testing:** Apache JMeter

## ⚙️ Installation & Configuration

1. **Clone the repository:**
   ```bash
   git clone https://github.com/SaaFazal/CycleNest.git
   ```
2. **Azure Configuration:**
   * You will need an active Azure Cosmos DB instance.
   * Update the connection strings and keys within `src/java/orchestrator/CosmosConfig.java` or provide them via Environment Variables (recommended).
3. **Build the Project:**
   Open the project in your IDE (NetBeans/IntelliJ) or compile via Ant/Maven.
   ```bash
   ant build
   ```
4. **Deploy:**
   Deploy the compiled WAR file to a servlet container such as Apache Tomcat or GlassFish.

## 📊 Load Testing Evidence
The `/jmeter-tests` directory contains the `.jmx` test plans and visual evidence (graphs/aggregates) of the API successfully sustaining 5,000 req/sec during stress testing.
