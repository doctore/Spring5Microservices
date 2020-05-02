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
    - [sql](#sql)
- [Previous steps](#previous-steps)
- [Security services](#security-services)
- [How to use it?](#how-to-use-it)
- [Future additions](#future-additions)

## Why was this project created?

Basically to know how to create a project using the microservices approach with the last Spring version. Due to there are several options we can use for different features included
in a "microservices architecture", the main purpose of this project is explore the most widely used creating a good base we will be able to use in a real one.

## Elements included in this project

Below is shown a brief introduction to the subprojects included in this one:

### registry-server

Server used to register all microservices included in this project. In this case, using Netflix Eureka each client can simultaneously act as a server, to replicate its status to a
connected peer. In other words, a client retrieves a list of all connected peers of a service registry and makes all further requests to any other services through a load-balancing
algorithm (Ribbon by default).
<br><br> 

### config-server

Configuration server used by the microservices included to get their required initial values like database configuration, for example. Those configuration values have been added
into the project:

* [Spring5Microservices_ConfigServerData](https://github.com/doctore/Spring5Microservices_ConfigServerData)

As we can see, there is an specific folder for every microservice and the important information is encoded (the next code is part of *pizza-service/pizza-service.yml* file):

```
spring:
  ## Spring DATASOURCE (DataSourceAutoConfiguration & DataSourceProperties)
  datasource:
    url: jdbc:postgresql://localhost:5432/microservice
    username: microservice
    # Using environment variable ENCRYPT_KEY=ENCRYPT_KEY
    # Getting the value with POST localhost:8888/encrypt and the password in its body
    password: "{cipher}c5c54009a56a0f215a208067a2b13189091c13480306c81ab68edfb22a6251ca"
```

To increase the security level, in *bootstrap.yml* file I have deactivated the decryption on **config.server**, sending the information encrypted and delegating in every microservice
the labour of decrypt it. That is the reason to include in their *pom.xml* file, the dependency:

```
<dependency>
   <groupId>org.springframework.security</groupId>
   <artifactId>spring-security-rsa</artifactId>
</dependency>
```
<br>

### gateway-server

Using Zuul, this is the gateway implementation used by the other microservices included in this proof of concept. This module contains a filter to registry every web service invoked,
helping to debug every request.
<br><br>

### security-oauth-service

Full integration with Oauth 2.0 + Jwt functionality provided by Spring, used to have an option to manage authentication/authorization functionalities through access and refresh
tokens. With this microservice working as Oauth server we will be able to configure the details of every allowed application using the table in database:
**security.oauth_client_details**. On the other hand, several customizations have been included to the manage the creation of both JWT tokens and how to append additional information
too.
 
The technologies used are the following ones:

* **Hibernate** as ORM to deal with the PostgreSQL database.
* **JPA** for accessing, persisting, and managing data between Java objects and database.
* **Lombok** to reduce the code development in entities and DTOs.
* **Cache2k** as cache to reduce the invocations to the database.

In this subproject the layer's division is:

* **repository** layer used to access to the database.
* **service** containing the business logic.

On the other hand, there are other "important folders": 

* **configuration** with several classes used to manage several areas such: security, exception handlers, cache, etc.
* **model** to store the entities.
* **dto** custom objects to contain specific data.
<br><br>

### security-jwt-service

Based on JWT token, this module was created to centralize the management of authentication/authorization functionalities. Its main purpose is provide a completely multi-application
platform to generate/manage their own access and refresh tokens (including additional information), choosing between JWS or JWE token type. Every application will be able to manage
its own token configuration/generation adding a new row in the database table: **security.jwt_client_details** and implementing the interface `IAuthenticationGenerator`.
 
The technologies used are the following ones:

* **Hibernate** as ORM to deal with the PostgreSQL database.
* **JPA** for accessing, persisting, and managing data between Java objects and database.
* **Lombok** to reduce the code development in entities and DTOs.
* **Hazelcast** as cache to reduce the invocations to the database.
* **NimbusJoseJwt** to work with JWS/JWE tokens.
* **MVC** a traditional Spring MVC Rest API to manage the authentication/authorization requests.

In this subproject the layer's division is:

* **repository** layer used to access to the database.
* **service** containing the business logic.
* **controller** REST Api using Spring MVC.

On the other hand, there are other "important folders": 

* **configuration** with several classes used to manage several areas such: security, exception handlers, cache, etc.
* **model** to store the entities.
* **dto** custom objects to contain specific data.
* **util** to manage the JWS/JWE functionality.
<br><br>

The existing two git branches are related with this microservice:

* **master** there is only one datasource due to all applications use the same database.
* **multi-datasource-security** every application has its own datasource, so different persistent context are defined for every one.

### pizza-service

One pizza has several ingredients, this is the summary of the entities/DTOs included on this microservices. The main purpose of this microservice is the creation of an small one
on which I am using the following technologies:

* **Hibernate** as ORM to deal with the PostgreSQL database.
* **JPA** for accessing, persisting, and managing data between Java objects and database.
* **QueryDSL** allowing us to create type-safe queries as an alternative to the "potential problematic" ones development with HQL and/or Spring JPA repository. 
* **Lombok** to reduce the code development in entities and DTOs.
* **Hazelcast** as cache to store temporary banned users.
* **MapStruct** used to conversion between Entities <--> DTOs in an easy way.
* **Webflux** creating a reactive REST Api as alternative to the traditional Spring MVC.

In this subproject the layer's division is:

* **repository** layer used to access to the database.
* **service** containing the business logic.
* **controller** REST Api using Webflux.

On the other hand, there are other "important folders": 

* **configuration** with several classes used to manage several areas such: persistence, exception handlers, etc.
* **model** to store the entities.
* **dto** custom objects to contain specific data.
* **util/converter** to translate from entities to dtos and vice versa.

Using **Hazelcast** for that purpose, this microservices provides functionality to banned users temporally. That is the way we can use to disable any JWT active token related
with a user we just disabled in database (through admin web page or similar tool). `UserController` resource provides the required web services.
<br><br>

### order-service

One order has several order lines and one order line contains a pizza. The main purpose of this microservice is the creation of an small one on which I am using the following
technologies:

* **jOOQ** replacing to the traditional pair Hibernate/JPA. Allowing us to create type-safe queries and improve the performance between the microservice and the database.
* **Lombok** to reduce the code development in models and DTOs.
* **MapStruct** used to conversion between Models <--> DTOs in an easy way.
* **SimpleFlatMapper** due to its integration with jOOQ, used to convert the some custom query results into a known Java object.
* **Webflux** creating a reactive REST Api as alternative to the traditional Spring MVC.

In this subproject the layer's division is:

* **dao** layer used to access to the database.
* **service** containing the business logic.
* **controller** REST Api using Webflux.

On the other hand, there are other "important folders": 

* **configuration** with several classes used to manage several areas such: exception handlers, etc.
* **model** to store the Java objects that match with the tables in database.
* **dto** custom objects to contain specific data.
* **util/converter** to translate from models to dtos and vice versa.
<br><br>

### common

Maven project that includes common code used in several microservices.

### sql

With SQL files included in the main database and the one used for testing purpose. In both cases, there is one file with the structure of the tables and another one with the
information initially included.
<br><br>

In the next picture you will see a communication diagram of all microservices described above:

![Alt text](/documentation/CommunitationDiagram.png?raw=true "Communication diagram")


## Previous steps

Due to every microservice has to decrypt the information sent by **config-server**, some steps are required:

#### Setting up an encryption key

In this project a symmetric encryption key has been used. The symmetric encryption key is nothing more than a shared secret that's used by the encrypter to encrypt a value
and the decrypter to decrypt a value. With the Spring Cloud configuration server, the symmetric encryption key is a string of characters you select that is passed to the
service via an operating system environment variable called **ENCRYPT_KEY**. For those microservices, I have used:

```
ENCRYPT_KEY=ENCRYPT_KEY
```

#### JDK and Oracle JCE

If you are using Oracle JDK instead of OpenJDK, you need to download and install Oracle's Unlimited Strength Java Cryptography Extension (JCE). This isn't available through
Maven and must be downloaded from Oracle Corporation. Once you've downloaded the zip files containing the JCE jars, you must do the following:

- Locate your `$JAVA_HOME/jre/lib/security` directory

- Back up the `local_policy.jar` and `US_export_policy.jar` files in the `$JAVA_HOME/jre/lib/security` directory to a different location.

- Unzip the JCE zip file you downloaded from Oracle

- Copy the `local_policy.jar` and `US_export_policy.jar` to your `$JAVA_HOME/jre/lib/security` directory.

#### Problems resolution

If you receive some errors related with encryption like:

```
IllegalStateException: Cannot decrypt: ...
```

Please, take a look to the previous steps in this section, maybe one of them is missing. If you still see same error messages, the best way to solve it is changing the
"cipher values" added in the microservices configuration files included in: 

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

- Run **registry-server** and **config-server**

- Encrypt required values using the provided endpoint for that purpose, as follows: 

![Alt text](/documentation/Encryption.png?raw=true "Encryption endpoint")

- Overwrite current values by the provided ones.

## Security services

As you read previously, there are two different microservices you can use to manage the authentication/authorization functionality: **security-oauth-service** and
**security-jwt-service**, in this proof of concept I have used the first one in **order-service** and the second one to securize **pizza-service**.

Regarding to every microservice, in this section I will explain the web services provided by every one and how to use them, starting by **security-oauth-service**. Before
enter in details about this security service, it is important to know that, for every request we have to include the Oauth 2.0 credentials:

![Alt text](/documentation/SecurityOauthService_Credentials.png?raw=true "Oauth 2.0 credentials")
   
You can see the *raw password* in the SQL file `MasterDatabase_Data.sql`, when the information about this application is included in the table `security.oauth_client_details`.
In this case, the password is `Spring5Microservices`.
   
So, the list of web services is the following one:

**1.** Get the authentication information:

![Alt text](/documentation/SecurityOauthService_Login.png?raw=true "Login")

In the previous image, I have used for this example `admin/admin`, there is another option: `user/user`, included in the SQL file `MasterDatabase_Data.sql` (in the inserts
related with the table `eat.user`).

**2.** Refresh authentication information after the access token expiration:

![Alt text](/documentation/SecurityOauthService_Refresh.png?raw=true "Refresh token")

**3.** Get authorization information using access token:

![Alt text](/documentation/SecurityOauthService_AuthorizationInfo.png?raw=true "Authorization information")

Regarding to **security-jwt-service**, it has an equivalent list of web services to provide the same funcionality, starting with the required credentials for every request:

![Alt text](/documentation/SecurityJwtService_Credentials.png?raw=true "Security Jwt credentials")

**1.** Get the authentication information:

![Alt text](/documentation/SecurityJwtService_Login.png?raw=true "Login")

**2.** Refresh authentication information after the access token expiration:

![Alt text](/documentation/SecurityJwtService_Refresh.png?raw=true "Refresh token")

**3.** Get authorization information using access token:

![Alt text](/documentation/SecurityJwtService_AuthorizationInfo.png?raw=true "Authorization information")

## How to use it?

The first step is adding in our databases: `main` and `test` ones, the SQL files included in the `sql` folder. Once we have finished, it will be necessary to run the following
services (following the displayed ordination):

1. **registry-server**
2. **config-server**
3. **gateway-server**
4. **security-oauth-service** (if we want to use `order-service`)
4. **security-jwt-service** (if we want to use `pizza-service`)

And finally any of the other ones (or both): **pizza-service** and **order-service**.

So, once you have obtained the required JWT access token (as I explained you in the previous section), you can use it to invoke the required web services:

![Alt text](/documentation/PizzaService.png?raw=true "Example of pizza service")

or:

![Alt text](/documentation/OrderService.png?raw=true "Example of order service")

From now, using the **gateway-server** URL, we can read the Swagger documentation included in the microservices.

![Alt text](/documentation/Swagger.png?raw=true "Swagger documentation")
