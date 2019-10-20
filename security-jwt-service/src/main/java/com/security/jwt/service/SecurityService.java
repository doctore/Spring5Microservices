package com.security.jwt.service;

import com.security.jwt.configuration.Constants;
import com.security.jwt.dto.RawAuthenticationInformationDto;
import com.security.jwt.enums.AuthenticationGeneratorEnum;
import com.security.jwt.enums.TokenKeyEnum;
import com.security.jwt.exception.ClientNotFoundException;
import com.security.jwt.interfaces.IAuthenticationGenerator;
import com.security.jwt.model.JwtClientDetails;
import com.security.jwt.model.User;
import com.security.jwt.service.JwtClientDetailsService;
import com.security.jwt.util.JwtUtil;
import com.spring5microservices.common.dto.AuthenticationInformationDto;
import com.spring5microservices.common.dto.UsernameAuthoritiesDto;
import io.jsonwebtoken.JwtException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

@Log4j2
@Service
public class SecurityService {

    private ApplicationContext applicationContext;
    private JwtClientDetailsService jwtClientDetailsService;
    private JwtUtil jwtUtil;
    private TextEncryptor encryptor;

    @Autowired
    public SecurityService(@Lazy ApplicationContext applicationContext, @Lazy JwtClientDetailsService jwtClientDetailsService,
                           @Lazy JwtUtil jwtUtil, @Lazy TextEncryptor encryptor) {
        this.applicationContext = applicationContext;
        this.jwtClientDetailsService = jwtClientDetailsService;
        this.jwtUtil = jwtUtil;
        this.encryptor = encryptor;
    }


    /**
     *    Build the {@link AuthenticationInformationDto} with the specific information related with a {@code username}
     * and {@code clientId} (belongs to a {@link JwtClientDetails}).
     *
     * @param clientId
     *    {@link JwtClientDetails#getClientId()} used to know the details to include
     * @param username
     *    {@link User#getUsername()} who is trying to authenticate
     *
     * @return {@link Optional} of {@link AuthenticationInformationDto}
     *
     * @throws ClientNotFoundException if the given {@code clientId} does not exists in database.
     * @throws UsernameNotFoundException if the given {@code username} does not exists in database.
     */
    public Optional<AuthenticationInformationDto> login(String clientId, String username, String password) {
        return AuthenticationGeneratorEnum.getByClientId(clientId)
                .map(generatorConfiguration -> applicationContext.getBean(generatorConfiguration.getAuthenticationGeneratorClass()))
                .flatMap(generator -> generator.getRawAuthenticationInformation(username, password))
                .map(authInfo -> {
                    JwtClientDetails clientDetails = jwtClientDetailsService.findByClientId(clientId);
                    return buildAuthenticationInformation(clientDetails, authInfo, UUID.randomUUID().toString());
                });
    }


    public Optional<AuthenticationInformationDto> refreshToken(String refreshToken, String clientId) {
        return null;

        /*
        return ofNullable(refreshToken)
                .


         */
    }


    public Optional<UsernameAuthoritiesDto> getAuthorizationInformation(String accessToken, String clientId) {

        return null;
    }





    /**
     *    Build the information returned as response when a {@code username} trying to authenticate in a specific {@code application}
     * ({@link JwtClientDetails}).
     *
     * @param clientDetails
     *    {@link JwtClientDetails} with the details about how to generate JWT tokens
     * @param jwtRawInformation
     *    {@link RawAuthenticationInformationDto} with the information that should be included
     * @param jti
     *    JWT token identifier
     *
     * @return {@link AuthenticationInformationDto}
     */
    private AuthenticationInformationDto buildAuthenticationInformation(JwtClientDetails clientDetails, RawAuthenticationInformationDto jwtRawInformation,
                                                                        String jti) {
        return AuthenticationInformationDto.builder()
                .accessToken(buildAccessToken(clientDetails, jwtRawInformation, jti))
                .refreshToken(buildRefreshToken(clientDetails, jwtRawInformation, jti))
                .tokenType(clientDetails.getTokenType())
                .jwtId(jti)
                .expiresIn(clientDetails.getAccessTokenValidity())
                .additionalInfo(null != jwtRawInformation ? jwtRawInformation.getAdditionalTokenInformation() : null)
                .build();
    }

    /**
     *    Return the access JWT token, merging the information should be included in this one with the given {@link JwtClientDetails}
     * wants to be include on it (stored in {@link RawAuthenticationInformationDto#getAccessTokenInformation()}).
     *
     * @param clientDetails
     *    {@link JwtClientDetails} with the details about how to generate JWT tokens
     * @param jwtRawInformation
     *    {@link RawAuthenticationInformationDto} with the information that should be included
     * @param jti
     *    JWT token identifier
     *
     * @return JWT access token
     */
    private String buildAccessToken(JwtClientDetails clientDetails, RawAuthenticationInformationDto jwtRawInformation,
                                    String jti) {
        Map<String, Object> tokenInformation = new HashMap<>(addToAccessToken(clientDetails.getClientId(), jti));
        if (null != jwtRawInformation)
            tokenInformation.putAll(jwtRawInformation.getAccessTokenInformation());

        return jwtUtil.generateJwtToken(tokenInformation, clientDetails.getJwtAlgorithm(),
                                        decryptJwtSecret(clientDetails.getJwtSecret()), clientDetails.getAccessTokenValidity())
                .orElse("");
    }

    /**
     *    Return the refresh JWT token, merging the information should be included in this one with the given {@link JwtClientDetails}
     * wants to be include on it (stored in {@link RawAuthenticationInformationDto#getRefreshTokenInformation()}).
     *
     * @param clientDetails
     *    {@link JwtClientDetails} with the details about how to generate JWT tokens
     * @param jwtRawInformation
     *    {@link RawAuthenticationInformationDto} with the information that should be included
     * @param jti
     *    JWT token identifier
     *
     * @return JWT refresh token
     */
    private String buildRefreshToken(JwtClientDetails clientDetails, RawAuthenticationInformationDto jwtRawInformation,
                                     String jti) {
        Map<String, Object> tokenInformation = new HashMap<>(addToRefreshToken(clientDetails.getClientId(), jti));
        if (null != jwtRawInformation)
            tokenInformation.putAll(jwtRawInformation.getRefreshTokenInformation());

        return jwtUtil.generateJwtToken(tokenInformation, clientDetails.getJwtAlgorithm(),
                                        decryptJwtSecret(clientDetails.getJwtSecret()), clientDetails.getRefreshTokenValidity())
                .orElse("");
    }

    /**
     * Return the standard information should be included in JWT access token.
     */
    private Map<String, Object> addToAccessToken(String clientId, String jti) {
        return new HashMap<String, Object>() {{
            put(TokenKeyEnum.CLIENT_ID.getKey(), clientId);
            put(TokenKeyEnum.JWT_ID.getKey(), jti);
        }};
    }

    /**
     * Return the standard information should be included in JWT refresh token.
     */
    private Map<String, Object> addToRefreshToken(String clientId, String jti) {
        return new HashMap<String, Object>() {{
            put(TokenKeyEnum.CLIENT_ID.getKey(), clientId);
            put(TokenKeyEnum.JWT_ID.getKey(), UUID.randomUUID().toString());
            put(TokenKeyEnum.REFRESH_JWT_ID.getKey(), jti);
        }};
    }

    private String decryptJwtSecret(String jwtSecret) {
        return encryptor.decrypt(jwtSecret.replace(Constants.JWT_SECRET_PREFIX, ""));
    }


    /*
    private boolean isAccessToken(String token, String jwtSecretKey) {
        try {
            return


                    ofNullable(getClaimFromToken(token, jwtSecretKey,
                    (claims) -> claims.get(usernameKeyInToken, String.class)));
        } catch (JwtException ex) {
            log.error(String.format("There was an error checking in the given token: %s is an access one", token), ex);
            return empty();
        }

    }

     */

}
