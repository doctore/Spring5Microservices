package com.gatewayserver.configuration.security;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties related with Authentication: web service used for it, etc
 */
@Configuration
@Getter
public class AuthenticationConfiguration {

    @Value("${authentication.restApi.allowedRequestURI}")
    private String allowedRequestURI;

    @Value("${authentication.restApi.validateToken}")
    private String validateTokenWebService;

}