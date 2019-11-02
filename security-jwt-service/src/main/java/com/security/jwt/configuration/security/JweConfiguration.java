package com.security.jwt.configuration.security;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties related with JWE token
 */
@Getter
@Configuration
public class JweConfiguration {

    @Value("${security.jwe.encryptionSecret}")
    private String encryptionSecret;

}
