
# Application Intake Service

A Java Spring Boot service for initial loan application intake.


## Features
- Collects loan application information from the end-user
- Handles the file collection and local storage
- Publishes loan submitted event to solace cloud.

## How to run
mvn clean package
java -jar target/application-intake-service-0.1.0.jar
