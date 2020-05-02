package com.security.jwt.configuration;

/**
 * Global values used in different part of the application
 */
public class Constants {

    public static final String APPLICATION_NAME = "SecurityJWT";

    // Global cache configuration
    public static final String CACHE_INSTANCE_NAME = "SecurityJwtCacheInstance";

    /**
     * Prefix used to store the cipher passwords in database
     */
    public static final String CIPHER_SECRET_PREFIX = "{cipher}";

    // Database information on which the entities have been included
    public static final class DATABASE {
        public static final String DATASOURCE_CONFIGURATION = "spring.datasource";

        public static final class SCHEMA {
            public static final String SECURITY = "security";
        }
    };

    // Path of the folders in the application and external ones
    public static final class PATH {
        public static final String CONTROLLER = "com.security.jwt.controller";
        public static final String MODEL = "com.security.jwt.model";
        public static final String REPOSITORY = "com.security.jwt.repository";

        public static final class EXTERNAL {
            public static final String COMMON = "com.spring5microservices.common";
        }
    }

}
