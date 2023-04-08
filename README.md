# Spring5Microservices

- [Why was this project created?](#why-was-this-project-created)
- [Elements included in this project](#elements-included-in-this-project)
    - [registry-server](#registry-server)
    - [config-server](#config-server)
    - [gateway-server](#gateway-server)
    - [security-oauth-service](#security-oauth-service)
    - [security-jwt-service](#security-jwt-service)
    - [pizza-service](#pizza-service)
    - [order-service](#order-service)
    - [common](#common)
    - [grpc-api](#grpc-api)
    - [sql](#sql)
    - [Communication diagram](#communication-diagram)
- [Previous steps](#previous-steps)
- [Security services](#security-services)
    - [security-oauth-service endpoints](#security-oauth-service-endpoints)
    - [security-jwt-service endpoints](#security-jwt-service-endpoints)
- [How to use it?](#how-to-use-it)
- [gRPC communication](#grpc-communication)
  - [Security in gRPC](#security-in-grpc)
  - [Request identifier in gRPC](#request-identifier-grpc)
  - [gRPC example request](#grpc-example-request)
- [Rest API documentation](#rest-api-documentation)
- [Previous versions of the project](#previous-versions-of-the-project)



## Why was this project created?

Basically to know how to create a project using the microservices approach with 5th version of Spring framework. Due to there are several options we
can use for different features included in a microservice architecture, the main purpose of this project is explore the most widely used creating a
good base we will be able to use in a real one.
<br><br>



## Elements included in this project

Below is shown a brief introduction to the subprojects included in this one:
<br><br>


### registry-server

Server used to register all microservices included in this project. In this case, using [Netflix Eureka](https://cloud.spring.io/spring-cloud-netflix/reference/html/)
each client can simultaneously act as a server, to replicate its status to a connected peer. In other words, a client retrieves a list of all connected
peers of a service registry and makes all further requests to any other services through a load-balancing algorithm (Ribbon by default).
<br><br> 


### config-server

[Configuration server](https://docs.spring.io/spring-cloud-config/docs/current/reference/html/#_spring_cloud_config_server) used by the included microservices
to get their required initial values like database configuration, for example. Those configuration values have been added into the project:

* [Spring5Microservices_ConfigServerData](https://github.com/doctore/Spring5Microservices_ConfigServerData)

As you can see, there is a specific folder for every microservice and the important information is encoded (the next code is part of
[pizza-service-dev.yml](https://github.com/doctore/Spring5Microservices_ConfigServerData/blob/master/pizza-service/pizza-service-dev.yml) file):

```
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/microservice
    username: microservice
    # Using environment variable ENCRYPT_KEY=ENCRYPT_KEY
    # Getting the value with POST localhost:8888/encrypt and the password in its body
    password: "{cipher}c5c54009a56a0f215a208067a2b13189091c13480306c81ab68edfb22a6251ca"
```

To increase the security level, in the [config-server](#config-server) microservice I have deactivated the decryption in [application.yml](https://github.com/doctore/Spring5Microservices/blob/master/config-server/src/main/resources/application.yml),
sending the information encrypted and delegating in every microservice the labour of decrypt it. That is the reason to include in their *pom.xml* file,
the dependency:

```
<dependency>
   <groupId>org.springframework.security</groupId>
   <artifactId>spring-security-rsa</artifactId>
</dependency>
```
<br><br>


### gateway-server

Using [Spring Gateway](https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html), this is the gateway implementation used by the other
microservices included in this proof of concept. This module contains a filter to registry every web service invoked, helping to debug each request.
<br><br>


### security-oauth-service

Full integration with Oauth 2.0 + Jwt functionality provided by Spring, used to have an option to manage authentication/authorization functionalities
through access and refresh tokens. With this microservice working as Oauth server we will be able to configure the details of every allowed application
using the table in database: **security.oauth_client_details**. On the other hand, several customizations have been included to the manage the creation
of both JWT tokens and how to append additional information too.
 
The technologies used are the following ones:

* **[Hibernate](https://hibernate.org)** as ORM to deal with the PostgreSQL database.
* **[JPA](https://en.wikipedia.org/wiki/Jakarta_Persistence)** for accessing, persisting, and managing data between Java objects and database.
* **[Lombok](https://projectlombok.org/features)** to reduce the code development in entities and DTOs.
* **[Cache2k](https://cache2k.org)** as cache to reduce the invocations to the database.

In this microservice, the layer's division is:

* **repository** layer used to access to the database.
* **service** containing the business logic.

On the other hand, there are other important folders: 

* **configuration** with several classes used to manage several areas such: security, exception handlers, cache, etc.
* **model** to store the entities.
* **dto** custom objects to contain specific data.
<br><br>


### security-jwt-service

Based on JWT token, this module was created to centralize the management of authentication/authorization functionalities. Its main purpose is provided
a completely multi-application platform to generate/manage their own access and refresh tokens (including additional information), choosing between JWS
or JWE token type. Every application will be able to manage its own token configuration/generation adding a new row in the database table: **security.jwt_client_details**
and implementing the interface [IAuthenticationGenerator](https://github.com/doctore/Spring5Microservices/blob/master/security-jwt-service/src/main/java/com/security/jwt/interfaces/IAuthenticationGenerator.java).
 
The technologies used are the following ones:

* **[Hibernate](https://hibernate.org)** as ORM to deal with the PostgreSQL database.
* **[JPA](https://en.wikipedia.org/wiki/Jakarta_Persistence)** for accessing, persisting, and managing data between Java objects and database.
* **[Lombok](https://projectlombok.org/features)** to reduce the code development in entities and DTOs.
* **[Hazelcast](https://hazelcast.com)** as cache to reduce the invocations to the database.
* **[NimbusJoseJwt](https://connect2id.com/products/nimbus-jose-jwt)** to work with JWS/JWE tokens.
* **[Webflux](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)** creating a reactive REST Api to manage the authentication/authorization requests.

In this microservice, the layer's division is:

* **repository** layer used to access to the database.
* **service** containing the business logic.
* **controller** REST Api using Webflux.

On the other hand, there are other important folders: 

* **configuration** with several classes used to manage several areas such: security, exception handlers, cache, etc.
* **model** to store the entities.
* **dto** custom objects to contain specific data.
* **util** to manage the JWS/JWE functionality.
<br><br>


### pizza-service

One pizza has several ingredients, this is the summary of the entities/DTOs included on this microservice. The main purpose of this microservice is
the creation of a small one on which I am using the following technologies:

* **[Hibernate](https://hibernate.org)** as ORM to deal with the PostgreSQL database.
* **[JPA](https://en.wikipedia.org/wiki/Jakarta_Persistence)** for accessing, persisting, and managing data between Java objects and database.
* **[QueryDSL](http://querydsl.com)** allowing us to create type-safe queries as an alternative to the "potential problematic" ones development with HQL and/or Spring JPA repository. 
* **[Lombok](https://projectlombok.org/features)** to reduce the code development in entities and DTOs.
* **[Hazelcast](https://hazelcast.com)** as cache to store temporary banned users.
* **[MapStruct](https://mapstruct.org)** used to conversion between Entities <--> DTOs in an easy way.
* **[Webflux](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)** creating a reactive REST Api as alternative to the traditional Spring MVC.

In this microservice, the layer's division is:

* **repository** layer used to access to the database.
* **service** containing the business logic.
* **controller** REST Api using Webflux.

On the other hand, there are other important folders: 

* **configuration** with several classes used to manage several areas such: persistence, exception handlers, etc.
* **model** to store the entities.
* **dto** custom objects to contain specific data.
* **util/converter** to translate from entities to dtos and vice versa.

Using **[Hazelcast](https://hazelcast.com)** for that purpose, this microservice provides functionality to banned users temporally. That is the way we can use to disable any
JWT active token related with a user we just disabled in database (through admin web page or similar tool). [UserController](https://github.com/doctore/Spring5Microservices/blob/master/pizza-service/src/main/java/com/pizza/controller/UserController.java)
class provides the required web services.

This microservice includes a [gRPC](https://grpc.io/docs/what-is-grpc/introduction/) server, more information in [gRPC communication](#grpc-communication).
<br><br>


### order-service

One order has several order lines and one order line contains a pizza. The main purpose of this microservice is the creation of a small one on which
I am using the following technologies:

* **[jOOQ](https://www.jooq.org)** replacing to the traditional pair Hibernate/JPA. Allowing us to create type-safe queries and improve the performance between the microservice and the database.
* **[Lombok](https://projectlombok.org/features)** to reduce the code development in models and DTOs.
* **[MapStruct](https://mapstruct.org)** used to conversion between Models <--> DTOs in an easy way.
* **[SimpleFlatMapper](https://simpleflatmapper.org)** due to its integration with jOOQ, used to convert some custom query results into a known Java object.
* **[OpenFeign](https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/)** integrated with Spring Cloud to communicate this microservice and [security-oauth-service](#security-oauth-service). 
* **[MVC](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html)** a traditional Spring MVC Rest API to manage the included requests.


In this microservice, the layer's division is:

* **dao** layer used to access to the database.
* **service** containing the business logic.
* **controller** REST Api using Spring MVC.

On the other hand, there are other important folders: 

* **configuration** with several classes used to manage several areas such: exception handlers, etc.
* **model** to store the Java objects that match with the tables in database.
* **dto** custom objects to contain specific data.
* **util/converter** to translate from models to dtos and vice versa.

This microservice includes a [gRPC](https://grpc.io/docs/what-is-grpc/introduction/) client, more information in [gRPC communication](#grpc-communication).
<br><br>


### grpc-api

Common functionality used by developed gRPC server and client. This one contains:

* [ingredient.proto](https://github.com/doctore/Spring5Microservices/blob/master/grpc-api/src/main/resources/proto/ingredient.proto) with the contract 
which includes defining the gRPC service and the method request and response types using [protocol buffers](https://developers.google.com/protocol-buffers/docs/reference/java-generated)
specification.

* [BasicCredential](https://github.com/doctore/Spring5Microservices/blob/master/grpc-api/src/main/java/com/spring5microservices/grpc/security/BasicCredential.java) 
which carries the Basic Authentication that will be propagated from gRPC client to the server in the request metadata with the `Authorization` key.

* [GrpcErrorHandlerUtil](https://github.com/doctore/Spring5Microservices/blob/master/grpc-api/src/main/java/com/spring5microservices/grpc/util/GrpcErrorHandlerUtil.java) 
helper class with several methods to manage errors both on gRPC client and server side. 

More information about how gRPC server and client uses it in [gRPC communication](#grpc-communication).
<br><br>


### common

Maven project that includes common code used in several microservices, with different useful helper classes like:

* [CollectionUtil](https://github.com/doctore/Spring5Microservices/blob/master/common/src/main/java/com/spring5microservices/common/util/CollectionUtil.java)
* [CollectorsUtil](https://github.com/doctore/Spring5Microservices/blob/master/common/src/main/java/com/spring5microservices/common/util/CollectorsUtil.java)
* [ComparatorUtil](https://github.com/doctore/Spring5Microservices/blob/master/common/src/main/java/com/spring5microservices/common/util/ComparatorUtil.java) 
* [DateTimeUtil](https://github.com/doctore/Spring5Microservices/blob/master/common/src/main/java/com/spring5microservices/common/util/DateTimeUtil.java)
* [ExceptionUtil](https://github.com/doctore/Spring5Microservices/blob/master/common/src/main/java/com/spring5microservices/common/util/ExceptionUtil.java) 
* [JsonUtil](https://github.com/doctore/Spring5Microservices/blob/master/common/src/main/java/com/spring5microservices/common/util/JsonUtil.java) 
* [MapUtil](https://github.com/doctore/Spring5Microservices/blob/master/common/src/main/java/com/spring5microservices/common/util/MapUtil.java)
* [NumberUtil](https://github.com/doctore/Spring5Microservices/blob/master/common/src/main/java/com/spring5microservices/common/util/NumberUtil.java)
* [ObjectUtil](https://github.com/doctore/Spring5Microservices/blob/master/common/src/main/java/com/spring5microservices/common/util/ObjectUtil.java)
* [StringUtil](https://github.com/doctore/Spring5Microservices/blob/master/common/src/main/java/com/spring5microservices/common/util/StringUtil.java) 

Generic interfaces used to provide common conversion functionality using [MapStruct](https://mapstruct.org): 

* [BaseConverter](https://github.com/doctore/Spring5Microservices/blob/master/common/src/main/java/com/spring5microservices/common/converter/BaseConverter.java)
* [BaseEnumConverter](https://github.com/doctore/Spring5Microservices/blob/master/common/src/main/java/com/spring5microservices/common/converter/enums/BaseEnumConverter.java) 

And functional programming structures and useful classes like:

* [Function](https://github.com/doctore/Spring5Microservices/tree/master/common/src/main/java/com/spring5microservices/common/interfaces/functional) improvements
* [Either](https://github.com/doctore/Spring5Microservices/tree/master/common/src/main/java/com/spring5microservices/common/util/either)
* [Lazy](https://github.com/doctore/Spring5Microservices/blob/master/common/src/main/java/com/spring5microservices/common/util/Lazy.java)
* [Try](https://github.com/doctore/Spring5Microservices/tree/master/common/src/main/java/com/spring5microservices/common/util/Try) 
* [Tuple](https://github.com/doctore/Spring5Microservices/tree/master/common/src/main/java/com/spring5microservices/common/collection/tuple)
* [Validation](https://github.com/doctore/Spring5Microservices/tree/master/common/src/main/java/com/spring5microservices/common/util/validation)
<br><br>


### sql

With SQL files included in the main database and the one used for testing purpose. In both cases, there is one file with the structure of the tables
and another one with the information initially included.
<br><br>


### Communication diagram

In the next picture you will see a communication diagram of all microservices described above:

![Alt text](/documentation/CommunitationDiagram.png?raw=true "Communication diagram")
<br><br>



## Previous steps

Due to every microservice has to decrypt the information sent by [config-server](#config-server), some steps are required:
<br><br>


### Setting up an encryption key

In this project a symmetric encryption key has been used. The symmetric encryption key is nothing more than a shared secret that's used by the encrypter
to encrypt a value and the decrypter to decrypt a value. With the Spring Cloud configuration server developed in [config-server](#config-server), the
symmetric encryption key is a string of characters you select that is passed to the service via an operating system environment variable called
**ENCRYPT_KEY**. For those microservices, I have used:

```
ENCRYPT_KEY=ENCRYPT_KEY
```
<br><br>


### JDK and Oracle JCE

If you are using [Oracle JDK](https://www.oracle.com/java/technologies/downloads) instead of [OpenJDK](https://openjdk.org), you need to download and
install Oracle's Unlimited Strength Java Cryptography Extension (JCE). This isn't available through Maven and must be downloaded from Oracle Corporation.
Once you've downloaded the zip files containing the JCE jars, you must do the following:

- Locate your `$JAVA_HOME/jre/lib/security` directory

- Back up the `local_policy.jar` and `US_export_policy.jar` files in the `$JAVA_HOME/jre/lib/security` directory to a different location.

- Unzip the JCE zip file you downloaded from Oracle

- Copy the `local_policy.jar` and `US_export_policy.jar` to your `$JAVA_HOME/jre/lib/security` directory.
<br><br>


### Problems resolution

If you receive some errors related with encryption like:

```
IllegalStateException: Cannot decrypt: ...
```

Please, take a look to the previous steps in this section, maybe one of them is missing. If you still see same error messages, the best way to solve
it is changing the *cipher values* added in the microservices configuration files included in: 

* [Spring5Microservices_ConfigServerData](https://github.com/doctore/Spring5Microservices_ConfigServerData)

Like:

```
spring:
  datasource:
    # Raw password: microservice
    password: "{cipher}c5c54009a56a0f215a208067a2b13189091c13480306c81ab68edfb22a6251ca"
```

And database table `security.jwt_client_details`, in the column `signature_secret`.

To do it:

- Run [registry-server](#registry-server) and [config-server](#config-server)

- Encrypt required values using the provided endpoint for that purpose, as follows: 

![Alt text](/documentation/Encryption.png?raw=true "Encryption endpoint")

- Overwrite current values by the provided ones.
<br><br>



## Security services

As you read previously, there are two different microservices you can use to manage the authentication/authorization functionality: [security-oauth-service](#security-oauth-service)
and [security-jwt-service](#security-jwt-service), in this proof of concept I have used the first one in [order-service](#order-service) and the second
one to securize [pizza-service](#pizza-service).

Regarding every microservice, in this section I will explain the web services provided by every one and how to use them, starting by [security-oauth-service](#security-oauth-service).
<br><br>


### security-oauth-service endpoints

Before enter in details about this security service, it is important to know that, for every request we have to include the Oauth 2.0 credentials.

![Alt text](/documentation/SecurityOauthService_Credentials.png?raw=true "Oauth 2.0 credentials")
   
You can see the *raw password* in the SQL file [MasterDatabase_Data.sql](https://github.com/doctore/Spring5Microservices/blob/master/sql/MasterDatabase_Data.sql),
when the information about this application is included in the table `security.oauth_client_details`. In this case, the password is `Spring5Microservices`. 

So, the list of web services is the following one:

**1.** Get the authentication information:

![Alt text](/documentation/SecurityOauthService_Login.png?raw=true "Login")

In the previous image, I have used for this example `admin/admin`, there is another option: `user/user`, included in the SQL file
[MasterDatabase_Data.sql](https://github.com/doctore/Spring5Microservices/blob/master/sql/MasterDatabase_Data.sql) (in the inserts related with the
table `eat.user`).

**2.** Refresh authentication information after the access token expiration:

![Alt text](/documentation/SecurityOauthService_Refresh.png?raw=true "Refresh token")

**3.** Get authorization information using access token:

![Alt text](/documentation/SecurityOauthService_AuthorizationInfo.png?raw=true "Authorization information")
<br><br>


### security-jwt-service endpoints

This microservice has an equivalent list of web services to provide the same functionality, starting with the required credentials for every request:

![Alt text](/documentation/SecurityJwtService_Credentials.png?raw=true "Security Jwt credentials")

And in a similar way to the previous one, the table in database to contain that information is `security.jwt_client_details`. 

So, the list of web services is the following one:

**1.** Get the authentication information:

![Alt text](/documentation/SecurityJwtService_Login.png?raw=true "Login")

**2.** Refresh authentication information after the access token expiration:

![Alt text](/documentation/SecurityJwtService_Refresh.png?raw=true "Refresh token")

**3.** Get authorization information using access token:

![Alt text](/documentation/SecurityJwtService_AuthorizationInfo.png?raw=true "Authorization information")
<br><br>



## How to use it?

The first step is adding in our databases: `main` and `test` ones, the SQL files included in the [sql](https://github.com/doctore/Spring5Microservices/tree/master/sql)
folder. Once we have finished, it will be necessary to run the following services (following the displayed ordination):

1. [registry-server](#registry-server)
2. [config-server](#config-server)
3. [gateway-server](#gateway-server)
4. [security-oauth-service](#security-oauth-service) (if we want to use [order-service](#order-service))
5. [security-jwt-service](#security-jwt-service) (if we want to use [pizza-service](#pizza-service))

And finally any of the other ones (or both): [pizza-service](#pizza-service) and [order-service](#order-service).

So, as I explained you in [Security services](#security-services), once you have obtained the required JWT access token, you can use it to invoke the
required web services:

![Alt text](/documentation/PizzaService.png?raw=true "Example of pizza service")

or:

![Alt text](/documentation/OrderService.png?raw=true "Example of order service")
<br><br>



## gRPC communication

Besides the REST API developed in:

* [pizza-service](#pizza-service)
* [order-service](#order-service)
* [security-oauth-service](#security-oauth-service)
* [security-jwt-service](#security-jwt-service)

In this project has been added a [gRPC](https://grpc.io/docs/what-is-grpc/introduction/) communication channel between:

* **gRPC client:** in [order-service](https://github.com/doctore/Spring5Microservices/tree/master/order-service/src/main/java/com/order/grpc)
* **gRPC server:** in [pizza-service](https://github.com/doctore/Spring5Microservices/tree/master/pizza-service/src/main/java/com/pizza/grpc)

Both use the same approach to run server and client instances:

* The instance definition:
  - [Client instance](https://github.com/doctore/Spring5Microservices/blob/master/order-service/src/main/java/com/order/grpc/client/GrpcClient.java)
  - [Server instance](https://github.com/doctore/Spring5Microservices/blob/master/pizza-service/src/main/java/com/pizza/grpc/server/GrpcServer.java)
<br>

* The Spring functionality used to run it:
  - [Client runner](https://github.com/doctore/Spring5Microservices/blob/master/order-service/src/main/java/com/order/grpc/client/GrpcClientRunner.java)
  - [Server runner](https://github.com/doctore/Spring5Microservices/blob/master/pizza-service/src/main/java/com/pizza/grpc/server/GrpcServerRunner.java)
<br>


### Security in gRPC

The internal communication between a microservice and its related security server, that is:

* [order-service](#order-service) => [security-oauth-service](#security-oauth-service)
* [pizza-service](#pizza-service) => [security-jwt-service](#security-jwt-service)

Uses [Basic Authentication](https://en.wikipedia.org/wiki/Basic_access_authentication) to include the required credentials:

* [order-service](#order-service) in the class [SecurityManager](https://github.com/doctore/Spring5Microservices/blob/master/order-service/src/main/java/com/order/configuration/security/SecurityManager.java)
* [pizza-service](#pizza-service) in the class [SecurityManager](https://github.com/doctore/Spring5Microservices/blob/master/pizza-service/src/main/java/com/pizza/configuration/security/SecurityManager.java)

In the current gRPC development I have followed the same approach:

* In the **gRPC client** creating a new [BasicCredential](https://github.com/doctore/Spring5Microservices/blob/master/grpc-api/src/main/java/com/spring5microservices/grpc/security/BasicCredential.java)
instance and adding it in the `Authorization` header sent to the server, using the method `buildCallCredentials` of the class [GrpcClient](https://github.com/doctore/Spring5Microservices/blob/master/order-service/src/main/java/com/order/grpc/client/GrpcClient.java).

* In the **gRPC server**, the interceptor [AuthenticationInterceptor](https://github.com/doctore/Spring5Microservices/blob/master/pizza-service/src/main/java/com/pizza/grpc/interceptor/AuthenticationInterceptor.java)
manages required verifications.
<br><br>


### Request identifier in gRPC

This project uses [Spring Sleuth](https://spring.io/projects/spring-cloud-sleuth) for distributed tracing, to simulate the same behaviour two interceptors
have been defined:

* At **gRPC client** with [RequestIdInterceptor](https://github.com/doctore/Spring5Microservices/blob/master/order-service/src/main/java/com/order/grpc/interceptor/RequestIdInterceptor.java)
* At **gRPC server** with [RequestIdInterceptor](https://github.com/doctore/Spring5Microservices/blob/master/pizza-service/src/main/java/com/pizza/grpc/interceptor/RequestIdInterceptor.java)
<br><br>


### gRPC example request

[order-service](#order-service) contains an endpoint to get the summary of ingredients related with an order. Such endpoint receives the order's
identifier as parameter and uses it to obtain the identifiers of the related pizzas from its own database. To get the list of ingredients use
the developed [gRPC](https://grpc.io/docs/what-is-grpc/introduction/) communication channel to receive from [pizza-service](#pizza-service) the
requested information.

The communication diagram including also the invocation of [security-oauth-service](#security-oauth-service) is the following:

![Alt text](/documentation/GrpcCommunicationDiagram.png?raw=true "gRPC Communication diagram")
<br>

So, as I explained you in [security-oauth-service endpoints](#security-oauth-service-endpoints), once you have obtained the required JWT access token,
you can use it to invoke the web service that uses the developed gRPC channel:

![Alt text](/documentation/WebServiceWithGRPC.png?raw=true "Example of web service using gRPC channel")
<br><br>



## Rest API documentation

The following microservices have a well documented Rest API:

* [security-jwt-service](#security-jwt-service)
* [pizza-service](#pizza-service)
* [order-service](#order-service)
 
[Swagger](https://swagger.io) has been used in all cases, however two different libraries have been included:

* **[SpringFox](http://springfox.github.io/springfox/docs/current)** in [gateway-server](#gateway-server) to unify the webpage used to access to the rest of documented microservices.
* **[Springdoc-OpenApi](https://springdoc.org)** in every documented microservice.

To facilitate access to this documentation, we can use the [gateway-server](#gateway-server) URL. On that way, using the upper selector, we will be able
to choose between all existing microservices.

![Alt text](/documentation/Swagger.png?raw=true "Swagger documentation")
<br><br>



## Previous versions of the project

There are several archive git branches with previous versions of the current project:

* [Archive master](https://github.com/doctore/Spring5Microservices/tree/archive/v0/master) equivalent to the current `master` but using previous
versions of Spring and other libraries.
<br><br>

* [Archive multi datasource security](https://github.com/doctore/Spring5Microservices/tree/archive/v0/multi-datasource-security) specialization of
[security-jwt-service](#security-jwt-service) on which every application has its own datasource, so different persistent context are defined for
every one. Uses previous versions of Spring and other libraries.
<br><br>

* [Archive Spring Jdbc security](https://github.com/doctore/Spring5Microservices/tree/archive/v0/spring-jdbc-security) specialization of
[security-jwt-service](#security-jwt-service) but in this case there is only one datasource but Hibernate + JPA have been replaced by Spring JDBC
template to improve the performance. Uses previous versions of Spring and other libraries. 
