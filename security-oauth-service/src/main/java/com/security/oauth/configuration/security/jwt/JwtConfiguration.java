package com.security.oauth.configuration.security.jwt;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.jwt.crypto.sign.SignerVerifier;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import javax.crypto.spec.SecretKeySpec;

/**
 * Configuration properties related with JWT token
 */
@Getter
@Configuration
public class JwtConfiguration {

    @Value("${security.jwt.token.authorizationPrefix}")
    private String authorizationPrefix;

    @Value("${security.jwt.token.expirationTimeInMilliseconds}")
    private long expirationTimeInMilliseconds;

    @Value("${security.jwt.token.secretKey}")
    private String secretKey;

    @Value("${security.jwt.token.signatureAlgorithm}")
    private String signatureAlgorithm;


    /**
     * The storage mechanism to store the OAuth access token.
     *
     * @return {@link TokenStore}
     */
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }


    /**
     * Translates between JWT-encoded token values and default OAuth authentication information.
     *
     * @return {@link JwtAccessTokenConverter}
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new CustomAccessTokenConverter();
        converter.setSigner(buildSigner());
        converter.setVerifier(buildSigner());
        converter.setAccessTokenConverter(new CustomAccessTokenConverter());
        return converter;
    }


    /**
     * Base implementation for token services using JWT-encoded token values for the access and refresh ones.
     *
     * @return {@link DefaultTokenServices}
     */
    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        return defaultTokenServices;
    }


    private SignerVerifier buildSigner() {
        return new MacSigner(signatureAlgorithm, new SecretKeySpec(secretKey.getBytes(), signatureAlgorithm));
    }

}
