![Arcghitecture Diagram] https://www.figma.com/board/lUSLTNd6bkNYCwGmHZawGJ/Loan-Processing-Flow-with-Solace?node-id=0-1&t=vbeEO5UROfnxwhhf-1


# Application Intake Service

A Java Spring Boot service for initial loan application intake.


## Features
- Collects loan application information from the end-user
- Handles the file collection and local storage
- Publishes loan submitted event to solace cloud.

## How to run
- Update Configuration

  Open src/main/resources/application.yml.

Change Solace connection variables (host, msgVpn, clientUsername, clientPassword) to match your environment.
Change file upload directory to match yours

 - Build the Project

   mvn clean install -DskipTests


 - Run the Application

   mvn spring-boot:run
