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

## Routes url and goal

### Person related
```shell
http://localhost:8080/person
```
You can use Post/Put/Delete HTTP request on this url to add, modify or delete a person and it's related informations from datas

```shell
http://localhost:8080/communityEmail?city=city
```
You can use Get HTTP request on this url to request all the emails from the persons living in the city used as parameter

```shell
http://localhost:8080/personInfo?firstName=firstName&lastName=lastName
```
You can use Get HTTP request on this url to request name, address, age, email and medical record of the person requested in paremeter

```shell
http://localhost:8080/childAlert?address=address
```
You can use Get HTTP request on this url to request the list of the childs living at an address (containing their firstname, name and age) 
plus the list of other people living at this address.

```shell
http://localhost:8080/fire?address=address
```
You can use Get HTTP request on this url to request name, phone number, age and medical record of people living at the address plus the firestation number.

### Firestation related
```shell
http://localhost:8080/firestation
```
You can use Post/Put/Delete HTTP request on this url to add, modify or delete a firesation and it's related informations from datas

```shell
http://localhost:8080/flood/stations?stations=<a list of station_numbers>
```
You can use Get HTTP request on this url to get all the address covered by the stations used in parameter.

Each address has to contain informations related to the persons living at the address (name, number, age and medical record).

```shell
http://localhost:8080/phoneAlert?stationNumberstationNumber
```
You can use Get HTTP request on this url to get all the phone numbers of the people living at the addresses covered by the station use in parameter.

```shell
http://localhost:8080/firestation?stationNumber=<station_number>
```
You can use Get HTTP request on this url to get all the address covered by the station used in parameter.

It has to return the name, address, number and a count of people over and under eighteen living at the address.

### Medical Record related
```shell
http://localhost:8080/medicalRecord
```
You can use Post/Put/Delete HTTP request on this url to add, modify or delete a medical record and it's related informations from datas

### Application related
[/trace](http://localhost:8080/actuator/httptrace)

It is used to display trace information.

[/metrics](http://localhost:8080/actuator/metrics)

It is used to show metrics information for the current application.

[/health](http://localhost:8080/actuator/health)

It is used to show application health information.


[/info](http://localhost:8080/actuator/info)

It is used to display arbitrary application info.
