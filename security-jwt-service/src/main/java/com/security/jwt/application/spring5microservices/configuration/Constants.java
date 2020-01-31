package com.security.jwt.application.spring5microservices.configuration;

/**
 * Global values related with Spring5Microservices application
 */
public class Constants {

    public static final String APPLICATION_NAME = "SecurityJWT-Spring5Microservices";

    // Database information on which the entities have been included
    public static final class DATABASE {
        public static final String DATASOURCE_CONFIGURATION = "spring.datasource";

        public static final class SCHEMA {
            public static final String EAT = "eat";
        }
    };

    // Path of the folders in the application
    public static final class PATH {
        public static final String MODEL = "com.security.jwt.application.spring5microservices.model";
        public static final String REPOSITORY = "com.security.jwt.application.spring5microservices.repository";
    }

}
