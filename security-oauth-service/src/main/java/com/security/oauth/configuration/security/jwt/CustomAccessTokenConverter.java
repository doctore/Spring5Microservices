package com.security.oauth.configuration.security.jwt;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Map.entry;
import static java.util.stream.Collectors.toSet;

/**
 * Manages the information included in access and refresh tokens.
 */
public class CustomAccessTokenConverter extends JwtAccessTokenConverter {

    private static final String AUTHORITIES = "authorities";
    private static final String SCOPE = "scope";
    private static final String USERNAME = "username";
    private static final String ADDITIONAL_INFO = "additionalInfo";

    public CustomAccessTokenConverter() {
        super();
    }

    @Override
    public OAuth2AccessToken enhance(final OAuth2AccessToken accessToken,
                                     final OAuth2Authentication authentication) {
        OAuth2AccessToken result = super.enhance(
                accessToken,
                authentication
        );
        result.getAdditionalInformation()
                .putAll(
                        getAdditionalInformation(authentication)
                );
        return result;
    }


    @Override
    public Map<String, ?> convertAccessToken(final OAuth2AccessToken token,
                                             final OAuth2Authentication authentication) {
        Map<String, Object> defaultInformation = (Map<String, Object>) super.convertAccessToken(
                token,
                authentication
        );
        return this.isRefreshToken(token)
                ? getRefreshTokenInformation(defaultInformation)
                : getAccessTokenInformation(defaultInformation);
    }

    /**
     * Filter the data included in the JWT access token
     */
    private Map<String, ?> getAccessTokenInformation(final Map<String, Object> sourceInformation) {
        Map<String, Object> accessTokenInformation = new HashMap<>(sourceInformation);
        accessTokenInformation.keySet()
                .removeIf(k ->
                        Objects.equals(
                                SCOPE,
                                k
                        )
                );
        return accessTokenInformation;
    }

    /**
     * Filter the data included in the JWT refresh token
     */
    private Map<String, ?> getRefreshTokenInformation(final Map<String, Object> sourceInformation) {
        Map<String, Object> refreshTokenInformation = new HashMap<>(sourceInformation);
        refreshTokenInformation.keySet()
                .removeIf(k ->
                        List.of(AUTHORITIES, SCOPE)
                                .contains(k)
        );
        return refreshTokenInformation;
    }

    /**
     * Include a specific section with extra information in the returned {@link OAuth2AccessToken}
     */
    private Map<String, Object> getAdditionalInformation(OAuth2Authentication authentication) {
        Map<String, Object> authenticationAdditionalInformation = Map.ofEntries(
                entry(
                        USERNAME,
                        authentication.getUserAuthentication().getName()
                ),
                entry(
                        AUTHORITIES,
                        authentication.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(toSet())
                )
        );
        return Map.of(
                ADDITIONAL_INFO,
                authenticationAdditionalInformation
        );
    }

}
