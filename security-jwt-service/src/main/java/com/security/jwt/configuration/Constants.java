package com.security.jwt.configuration;

/**
 * Global values used in different part of the application
 */
public class Constants {

    // Documentation API version
    public static final String DOCUMENTATION_API_VERSION = "1.0";

    // Default charset for plain text
    public static final String TEXT_PLAIN_UTF8_VALUE = "text/plain;charset=UTF-8";

    // Database schemas on which the entities have been included
    public static final class DATABASE_SCHEMA {
        public static final String EAT = "eat";
        public static final String SECURITY = "security";
    };

    // External path
    public static final class EXTERNAL_PATH {
        public static final String COMMON = "com.spring5microservices.common";
    }

    // Global cache configuration
    public static final String CACHE_INSTANCE_NAME = "CacheInstance";

}
