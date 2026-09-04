# Spring Boot Deployment Dashboard

A simple Spring Boot backend + browser dashboard for displaying paginated deployment/service data.

## Features

- REST API: `GET /api/deployments`
- Pagination
- Search by service name
- Filter by environment
- Filter by status
- Dashboard summary counts
- 125 sample deployment records generated at startup
- Simple HTML/CSS/JavaScript frontend
- Spring Boot Actuator health endpoint
- No database required
- Maven build

## Requirements

- Java 17+
- Maven 3.9+

Spring Boot itself can also be generated through Spring Initializr; this project follows the standard Spring Web REST approach. See the official Spring REST guide:
https://spring.io/guides/gs/rest-service/

## Run

Build:

```bash
mvn clean package
```

Run:

```bash
mvn spring-boot:run
```

Or run the packaged JAR:

```bash
java -jar target/deployment-dashboard-1.0.0.jar
```

Then open:

```text
http://localhost:8080/
```

API examples:

```bash
curl "http://localhost:8080/api/deployments?page=0&size=10"
curl "http://localhost:8080/api/deployments?page=0&size=10&environment=UAT"
curl "http://localhost:8080/api/deployments?page=0&size=10&status=FAILED"
curl "http://localhost:8080/api/deployments?page=0&size=10&search=claims"
curl "http://localhost:8080/api/deployments/summary"
```

## API response

`GET /api/deployments?page=0&size=10`

```json
{
  "page": 0,
  "pageSize": 10,
  "totalRecords": 125,
  "totalPages": 13,
  "data": [
    {
      "id": 1001,
      "name": "Claims Service",
      "status": "ACTIVE",
      "version": "v2.4.1",
      "environment": "UAT",
      "lastDeployment": "2026-09-04T10:30:00",
      "deployedBy": "DevOps",
      "buildNumber": "BUILD-4582"
    }
  ]
}
```

## Project structure

```text
deployment-dashboard/
├── build.gradle
├── settings.gradle
├── README.md
├── .gitignore
└── src/
    ├── main/
    │   ├── java/com/example/deploymentdashboard/
    │   │   ├── DeploymentDashboardApplication.java
    │   │   ├── controller/DeploymentController.java
    │   │   ├── model/Deployment.java
    │   │   ├── model/DeploymentPageResponse.java
    │   │   ├── model/SummaryResponse.java
    │   │   └── service/DeploymentService.java
    │   └── resources/
    │       ├── application.properties
    │       └── static/
    │           ├── index.html
    │           ├── app.js
    │           └── styles.css
    └── test/
        └── java/com/example/deploymentdashboard/
            └── DeploymentControllerTest.java
```
# spring-dashboard
