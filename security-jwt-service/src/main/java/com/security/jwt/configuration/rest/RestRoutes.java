package com.security.jwt.configuration.rest;

/**
 * Used to define the REST API routes included in the project
 */
public final class RestRoutes {

    public static final class SECURITY {
        public static final String ROOT = "/security";
        public static final String AUTHENTICATION_INFO = "/authinfo";
        public static final String LOGIN = "/login";
        public static final String REFRESH_TOKEN = "/refreshToken";
    }

}
