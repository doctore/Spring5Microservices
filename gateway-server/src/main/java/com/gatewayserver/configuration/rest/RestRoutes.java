package com.gatewayserver.configuration.rest;

/**
 * Used to define the REST API routes included in the project
 */
public final class RestRoutes {

    public static final class CIRCUIT_BREAKER {
        public static final String ROOT = "/failed";
        public static final String ORDER_SERVICE = "/order-service/redirect";
        public static final String PIZZA_SERVICE = "/pizza-service/redirect";
        public static final String SECURITY_SERVICE = "/security-jwt-service/redirect";
        public static final String SECURITY_OAUTH_SERVICE = "/security-oauth-service/redirect";
    }

}
