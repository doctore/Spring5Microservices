package com.security.jwt.configuration;

/**
 * Global values used in different part of the application
 */
public class Constants {

    // Applications included in the project to manage their security functionality (used for bean definitions)
    public static final class APPLICATIONS {
        public static final String SPRING5_MICROSERVICES = "Spring5Microservices";
    }

    // Global cache configuration
    public static final String CACHE_INSTANCE_NAME = "SecurityJwtCacheInstance";

    /**
     * Prefix used to store the cipher passwords in database
     */
    public static final String CIPHER_SECRET_PREFIX = "{cipher}";

    // Database schemas on which the entities have been included
    public static final class DATABASE_SCHEMA {
        public static final String EAT = "eat";
        public static final String SECURITY = "security";
    };

    // Path of the folders in the application
    public static final class PATH {

        // External path
        public static final class EXTERNAL {
            public static final String COMMON = "com.spring5microservices.common";
        }
    }

}
