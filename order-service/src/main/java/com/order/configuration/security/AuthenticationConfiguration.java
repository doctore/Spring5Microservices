package com.order.configuration.security;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties related with Authentication: web service used for it, etc
 */
@Getter
@Configuration
public class AuthenticationConfiguration {

    @Value("${authentication.restApi.authenticationInformation}")
    private String authenticationInformation;

}