package com.pizza.configuration.security;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties related with authentication/authorization (web service used for it)
 */
@Getter
@Configuration
public class SecurityConfiguration {

    @Value("${security.restApi.authenticationInformation}")
    private String authenticationInformationWebService;

    @Value("${security.restApi.clientId}")
    private String clientId;

    @Value("${security.restApi.clientPassword}")
    private String clientPassword;

}
