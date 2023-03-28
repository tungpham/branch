# Getting Started

### Quick start
* Prerequisite: Java 17
* check out project from `git clone https://github.com/tungpham/branch.git`
* `cd branch`
* `gradlew build`
* `gradlew bootRun`
* The service should be available at http://localhost:8080/usergit/octocat

### Organization

The packages are organized as followed:
* `config` - all configurations, such as database, web service clients, etc. configurations. In depth configuration on 
each client should be done here. 
* `controller` - define all the REST APIs. The Controller should only have to worry about transform/serialize the 
application model into the REST API contract. 
* `entity` - representation of the response from dependencies, such as web services, database, etc.
* `dto` - the model of the service API - external facing client. 
* `model` - the application internal model where all business logic should happen upon.
* `sao` - low level dependencies access layer (database, or dao, web services, etc.) 
* `service` - business logic of the application. 

### Architecture choice
* We should have `external facing client model (dto)` - `application model (model)` - `dependencies 
model (entity)` so that we are extensible in all directions. 
  1. Update API interface: only need to change the adapter between `dto` and `model`
  2. Dependency API got update: only need to change the adapter between `model` and `entity`
  3. Internal application refactoring won't impact the existing contract `dto` and `entity` 

  Note: The potential drawback is we can have a lot of "mapping" layers with seemingly redundant codes (thus, the like of 
[MapStruct](https://mapstruct.org/) was born, which we should never use), and as a result of this mapping activities, 
it can be error-prone. Therefore, this principal should be applied with care.

* Model `timestamp` field such as created_at is unmarshalled to `java.time.Instant` to make it explicit clear on time 
processing down the road (ie. save to database, compare, query on range, etc.) 


* To consume REST api (github.com), there are multiple choices for client. Since Spring's RestTemplate is being 
deprecated, WebClient is chosen as it's a non-blocking client which makes it well-suited for use in high-performance and 
scalable applications. The 2 service call in this exercise was done in parallel to reduce a bit of response time.


* Exception handling: A global exception handler is created catering for 404. Why? Because when the error is client
error, it's not the server fault. Without this, Spring will return 500 HTTP error code which implies otherwise. Having 
correct client error code sent back is beneficial in 2 fronts:
  * Client can audit their request
  * Signals our clients that the request cannot be retried - if client implement any retry logic, thus reduce our load 
  * Service owner doesn't need to get paged in the middle the night

  That is not to say we ignore 404 error. But rather we're cognizant of the subtle difference between this and 500 so 
the load on support operation is reduced. 


* Testing: Since `WebClient` was chosen as the Rest client, `MockWebServer` was chosen as the testing framework for 
remote call since it makes it easier for us to test WebClient instead of mocking several intermediate steps. It's the 
framework [recommended](https://github.com/spring-projects/spring-framework/issues/19852#issuecomment-453452354) by 
Spring team

### Reference Documentation
For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.0.5/gradle-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.0.5/gradle-plugin/reference/html/#build-image)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.0.5/reference/htmlsingle/#web)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

### Additional Links
These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

