# Spring5Microservices

- [Why was this project created?](#why-was-this-project-created)
- [Elements included in this project](#elements-included-in-this-project)
    - [registry-server](#registry-server)
    - [config-server](#config-server)
    - [gateway-server](#gateway-server)
    - [pizza-service](#pizza-service)
    - [order-service](#order-service)
    - [sql](#sql)
- [Previous steps](#previous-steps)
- [Future additions](#previous-steps)

## Why was this project created?

Basically to know how to create a project using the microservices approach with the last Spring version. Due to there are several options we can use for different features included
in a "microservices architecture", the main purpose of this project is explore the most widely used creating a good base we will be able to use in a real one.

## Elements included in this project

Below is shown a brief introduction to the subprojects included in this one:

### registry-server

Server used to register all microservices included in this project. In this case, using Netflix Eureka each client can simultaneously act as a server, to replicate its status to a
connected peer. In other words, a client retrieves a list of all connected peers of a service registry and makes all further requests to any other services through a load-balancing
algorithm (Ribbon by default). 

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

### gateway-server

Using Zuul, this is the gateway implementation used by the other microservices included in this proof of concept.  

### pizza-service

One pizza has several ingredients, this is the summary of the entities/DTOs included on this microservices. The main purpose of this microservice is the creation of an small one
on which I am using the following technologies:

* **Hibernate** as ORM to deal with the PostgreSQL database.
* **JPA** for accessing, persisting, and managing data between Java objects and database.
* **QueryDSL** allowing us to create type-safe queries as an alternative to the "potential problematic" ones development with HQL and/or Spring JPA repository. 
* **Lombok** to reduce the code development in entities and DTOs.
* **MapStruct** used to conversion between Entities <--> DTOs in an easy way.
* **Webflux** creating a reactive REST Api as alternative to the traditional Spring MVC.

In this subproject the layer's division is:

* **repository** layer used to access to the database.
* **service** containing the business logic.
* **controller** REST Api using Webflux.

On the other hand, there are other "important folders": 

* **model** to store the entities.
* **dto** custom objects to contain specific data.
* **util/converter** to translate from entities to dtos and vice versa.

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

* **model** to store the Java objects that match with the tables in database.
* **dto** custom objects to contain specific data.
* **util/converter** to translate from models to dtos and vice versa.

### sql

With SQL files included in the main database and the one used for testing purpose. In both cases, there is one file with the structure of the tables and another one with the
information initially included.

## Previous steps

Due to every microservice has to decrypt the information sent by **config-server**, some steps are required:

#### Download and install Oracle JCE jars needed for encryption

To begin, you need to download and install Oracle’s Unlimited Strength Java Cryptography Extension (JCE). This isn’t available through Maven and must be downloaded from Oracle
Corporation. 1 Once you’ve downloaded the zip files containing the JCE
jars, you must do the following:

- Locate your $JAVA_HOME/jre/lib/security directory

- Back up the local_policy.jar and US_export_policy.jar files in the $JAVA_HOME/jre/lib/security directory to a different location.

- Unzip the JCE zip file you downloaded from Oracle

- Copy the local_policy.jar and US_export_policy.jar to your $JAVA_HOME/jre/lib/security directory.

#### Setting up an encryption key

Once the JAR files are in place, you need to set a symmetric encryption key. The symmetric encryption key is nothing more than a shared secret that’s used by the encrypter
to encrypt a value and the decrypter to decrypt a value. With the Spring Cloud configuration server, the symmetric encryption key is a string of characters you select
that is passed to the service via an operating system environment variable called **ENCRYPT_KEY**. For those microservices, I have used:

```
ENCRYPT_KEY=ENCRYPT_KEY
```

## Future additions

- Authentication and authorization using JWT token
- Documentation of the REST Api with Swagger
