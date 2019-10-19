package com.security.jwt.configuration;

import com.security.jwt.model.JwtClientDetails;

/**
 * Global values used in different part of the application
 */
public class Constants {

    // Global cache configuration
    public static final String CACHE_INSTANCE_NAME = "CacheInstance";

    // Database schemas on which the entities have been included
    public static final class DATABASE_SCHEMA {
        public static final String EAT = "eat";
        public static final String SECURITY = "security";
    };

    // Documentation API version
    public static final String DOCUMENTATION_API_VERSION = "1.0";

    // External path
    public static final class EXTERNAL_PATH {
        public static final String COMMON = "com.spring5microservices.common";
    }

    /**
     * Prefix used to store the JWT secret key of all {@link JwtClientDetails}
     */
    public static final String JWT_SECRET_PREFIX = "{cipher}";

}
