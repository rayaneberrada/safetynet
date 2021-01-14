# Safetynet
Application's purpose is to send informations to the emergency services

## Requirements

For building and running the application you need:

- [JDK 11](http://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-2133151.html)
- [Maven 3](https://maven.apache.org)

## Running the application locally

There are several ways to run a Spring Boot application on your local machine. One way is to execute the `main` method in the `fr.openclassroom.safetynet.SafetynetApplication` class from your IDE.

Alternatively you can use the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:

```shell
mvn spring-boot:run
```