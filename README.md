# Meeting Room Management Application

### Overview

This is an interview assignment in the context of the hiring process for Harbor Lab. The project is about delivering a web application that
allows the employees of the company ACME Lab Inc to easily book time slots in meeting rooms. This is an MVP and the use cases that 
need to be supported, are the following:

* As an ACME Lab Inc. employee, I need to be able to view a meeting room's bookings for a given day.
* As an ACME Lab Inc. employee, I need to be able to book a meeting room.
* As an ACME Lab Inc. employee, I need to be able to cancel a booking.

Some notes:

* Only the backend API is delivered with this project
* Authentication and authorization is out of scope for this project
* H2 was used as a DB for this project

### Tech stack

* Spring boot 3.3.4
* Java 21

Gradle is used as the build tool.

### How to run

In order to run the application, import it on Intellij IDEA (or the IDE of choice), create a Run Configuration and run it.

Alternatively, from the command line, you can run it as follows:

`./gradlew bootRun`

Another way would be to create the jar and then run it directly with java 21:

`./gradlew bootJar` -- this will create the jar in build/libs

`java -jar build/libs/meeting-room-management-system-1.0.0.jar`

All these ways will start the application locally. There is Swagger configured, so you can access it on port 8080:

[Swagger link](http://localhost:8080/swagger-ui/index.html)

Alternatively, a request to the booking creation endpoint can be done as follows using curl:

`curl -X POST http://localhost:8080/api/v1/booking 
  -H 'accept: */*' 
  -H 'Content-Type: application/json' 
  -d '{
          "userEmail": "aris@test.com",
          "roomName": "Athens",
          "date": "2024-11-23",
          "startHour": 10,
          "startMinute": 30,
          "endHour": 11,
          "endMinute": 30,
          "zoneId": "Europe/Zurich"
        }`

### Notes

The service is implemented as a Spring boot web service with three main endpoints. These three endpoints support the operations that the assignment specifies.
* A POST endpoint, accessible at /api/v1/booking. This endpoint creates a booking on a meeting room
* A GET endpoint, accessible at /api/v1/booking. This endpoint retrieves the bookings of a specific meeting room for a specific date
* A DELETE endpoint, accessible at /api/v1/booking/{id}. This endpoint cancels a booking by id. The id can be retrieved through the GET endpoint
* An H2 database is used. There are 3 tables, one holding the users, one the rooms and one the bookings. There are admin endpoints in order to easily add data on the user and room tables for testing.
* There is a data.sql file that inserts some data on startup to the database, upon schema creation. This data is used for manual testing and exhibition through Swagger.
* A zoneId is also needed at the controller level. This helps us handle conflicts and bookings across timezones.
* Multiple validations are being made on the input data. If something is not valid, a 400 is returned to the user (frontend).
* We could use startDate and endDate for bookings spanning across multiple days but I felt that this is an overkill for this project.
* The id of the booking is used to cancel it for simplicity - ideally there should be another externalId so that we do not expose the ids of the internal database to the frontend.

#### Project structure
There are three services, two admin controllers and one API controller on the project.

#### Testing
There is an integration test for each Controller - using MockMvc to perform the actual requests. The main testing is being done on the service level. 
There all input combinations are tried as well as conflicts, null values, wrong ZoneIds etc.
Each service has its own integration test to verify that everything works as expected. In the future, we could add a bit more testing on the Controller level. In any case, there is no business logic in the Controllers.
