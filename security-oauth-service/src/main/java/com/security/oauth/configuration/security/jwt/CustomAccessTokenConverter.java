package com.security.oauth.configuration.security.jwt;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;

/**
 * Manages the information included in access and refresh tokens.
 */
public class CustomAccessTokenConverter extends JwtAccessTokenConverter {

    private static Map<String, Object> oAuth2AccessTokenAdditionalInformation = new HashMap<>();

    private static final String AUTHORITIES = "authorities";
    private static final String SCOPE = "scope";
    private static final String USER_NAME = "user_name";
    private static final String USERNAME = "username";
    private static final String ADDITIONAL_INFO = "additionalInfo";


    public CustomAccessTokenConverter() {
        super();
    }


    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        OAuth2AccessToken result = super.enhance(accessToken, authentication);
        result.getAdditionalInformation().putAll(getAdditionalInformation());
        return result;
    }


    @Override
    public Map<String, ?> convertAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        Map<String, Object> defaultInformation = (Map<String, Object>) super.convertAccessToken(token, authentication);
        return this.isRefreshToken(token) ? getRefreshTokenInformation(defaultInformation)
                                          : getAccessTokenInformation(defaultInformation);
    }

    /**
     * Filter the data included in the JWT access token
     */
    private Map<String, ?> getAccessTokenInformation(Map<String, Object> sourceInformation) {
        Map<String, Object> accessTokenInformation = new HashMap<>(sourceInformation);
        accessTokenInformation.keySet().removeIf(k -> asList(SCOPE).contains(k));
        updateAdditionalInformation(sourceInformation);
        return accessTokenInformation;
    }

    /**
     * Filter the data included in the JWT refresh token
     */
    private Map<String, ?> getRefreshTokenInformation(Map<String, Object> sourceInformation) {
        Map<String, Object> refreshTokenInformation = new HashMap<>(sourceInformation);
        refreshTokenInformation.keySet().removeIf(k -> asList(AUTHORITIES, SCOPE).contains(k));
        return refreshTokenInformation;
    }

    /**
     * Extra information included in the returned {@link OAuth2AccessToken}
     */
    private void updateAdditionalInformation(Map<String, Object> accessTokenInformation) {
        oAuth2AccessTokenAdditionalInformation.put(USERNAME, accessTokenInformation.get(USER_NAME));
        oAuth2AccessTokenAdditionalInformation.put(AUTHORITIES, accessTokenInformation.get(AUTHORITIES));
    }

    /**
     * Include an specific section with extra information in the returned {@link OAuth2AccessToken}
     */
    private Map<String, Object> getAdditionalInformation() {
        Map<String, Object> additionalInformation = new HashMap<>();
        additionalInformation.put(ADDITIONAL_INFO, oAuth2AccessTokenAdditionalInformation);
        return additionalInformation;
    }

}
