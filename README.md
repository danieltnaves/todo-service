# Todo Service 

A RESTful API to manage Todo items and their status. The API supports the following operations:

* Add a new item
* Change the description of an item
* Mark an item as done
* Mark an item as not done
* Get all items with pagination support
* Get all not-done items with pagination support
* Get details of a specific item
* Automatically change the status of past-due items to PAST_DUE

### Assumptions

I made certain assumptions when implementing the Todo item status lifecycle.

* A new Todo item can be created only with the initial status NOT_DONE. In order to change the Todo item to other status the PATCH endpoint must be called. Thus, the only required information to create a new Todo item is the description field. The createdAt value is automatically populated.
* Changing the status to PAST_DUE with a future date is not allowed.
* Changing a Todo item marked as DONE isn't allowed. It needs to be updated to NOT_DONE first.
* Changing an information from a PAST_DUE item isn't allowed.
* Changing the status of an item to PAST_DUE requires an expired date.
* Chaging an item with an expired date and the status different of PAST_DUE isn't not allowed.

### Tech Stack

This project uses the following tech stack:

* Spring Boot
* Spring Data JPA
* Spring MVC
* Spring AOP
* Lombok
* Hamcrest
* H2 Database
* OpenApi
* Checkstyle
* Gradle
* Docker

### Runtime environment

This project uses Java 17 and Spring Boot as a runtime environment. It's possible to execute it locally using either Java or Docker. The bulding instructions are described on the **How to build and run the service**.

### Frameworks and libraries

The main framework of this project is Spring Boot and its other frameworks like Data JPA, MVC, and AOP. The Lombok library is also used across the project. Aditionally, this project uses JUnit and the assertion library Hamcrest.
The documentation for the REST endpoints is written using the OpenApi library programmatically and is available using the web browser via Swagger UI.

### How to build the service

The project can be built with Java 17 and the Gradle wrapper. The following command needs to be executed:

```
./gradlew clean build
```

### How to run automated tests

To run both integration tests and unit tests execute:

```
./gradlew clean check
```
This command will execute unit tests first and then integration tests.

To run only unit tests:
```
./gradlew clean test
```

To run only integration tests:
```
./gradlew clean integrationTest
```

### How to run the service locally

There are two ways to build and run this project:

Using Java 17 and Gradle: With JDK 17 installed go to the root directory using the console and run the following command:

```
./gradlew bootRun
```

When the application is started the following message will be displayed:

```
Started ApiApplication in 1.926 seconds (process running for 2.114)
```

From now on the application can be used via port 8080.

The Swagger UI is available at:
http://localhost:8080/todo-service/swagger-ui/index.html

The H2 web console is available at:

http://localhost:8080/todo-service/h2-console

```
JDBC URL: jdbc:h2:mem:testdb
User Name: sa
Password: password
```

To use the application and start doing calls there are some handy examples on the Swagger UI, especially for the /todo/{id} PATCH endpoint.

Additionally, this application can also be executed using Docker. There are two shell scripts to execute the Docker commands:

```
./build-and-run.sh
```

there is also a version for ARM platforms:

```
./build-and-run-arm.sh
```

Similarly to the approach using Java this script will build the Docker image, spin up a new container, and make it available on port 8080. 