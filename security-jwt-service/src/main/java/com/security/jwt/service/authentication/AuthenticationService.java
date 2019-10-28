package com.security.jwt.service.authentication;

import com.security.jwt.configuration.Constants;
import com.security.jwt.dto.RawAuthenticationInformationDto;
import com.security.jwt.enums.AuthenticationConfigurationEnum;
import com.security.jwt.enums.TokenKeyEnum;
import com.security.jwt.exception.ClientNotFoundException;
import com.security.jwt.exception.TokenExpiredException;
import com.security.jwt.exception.UnAuthorizedException;
import com.security.jwt.model.JwtClientDetails;
import com.security.jwt.service.JwtClientDetailsService;
import com.security.jwt.util.JwtUtil;
import com.spring5microservices.common.dto.AuthenticationInformationDto;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static java.lang.String.format;

@Service
public class AuthenticationService {

    private ApplicationContext applicationContext;
    private JwtClientDetailsService jwtClientDetailsService;
    private JwtUtil jwtUtil;
    private TextEncryptor encryptor;

    @Autowired
    public AuthenticationService(@Lazy ApplicationContext applicationContext, @Lazy JwtClientDetailsService jwtClientDetailsService,
                                 @Lazy JwtUtil jwtUtil, @Lazy TextEncryptor encryptor) {
        this.applicationContext = applicationContext;
        this.jwtClientDetailsService = jwtClientDetailsService;
        this.jwtUtil = jwtUtil;
        this.encryptor = encryptor;
    }


    /**
     *    Build the {@link AuthenticationInformationDto} with the specific information related with a {@link UserDetails}
     * and {@code clientId} (belongs to a {@link JwtClientDetails}).
     *
     * @param clientId
     *    {@link JwtClientDetails#getClientId()} used to know the details to include
     * @param userDetails
     *    {@link UserDetails} with the information about who is trying to authenticate
     *
     * @return {@link Optional} of {@link AuthenticationInformationDto}
     *
     * @throws ClientNotFoundException if the given {@code clientId} does not exists in database or {@link AuthenticationConfigurationEnum}
     */
    public Optional<AuthenticationInformationDto> getAuthenticationInformation(String clientId, UserDetails userDetails) {
        return ofNullable(userDetails)
                .map(user -> AuthenticationConfigurationEnum.getByClientId(clientId))
                .map(authConfig -> applicationContext.getBean(authConfig.getAuthenticationGeneratorClass()))
                .flatMap(authGen -> authGen.getRawAuthenticationInformation(userDetails))
                .map(authInfo -> {
                    JwtClientDetails clientDetails = jwtClientDetailsService.findByClientId(clientId);
                    return buildAuthenticationInformation(clientDetails, authInfo, UUID.randomUUID().toString());
                });
    }


    /**
     * Check if the given {@code token} related with the given {@link JwtClientDetails#getClientId()} is an access one.
     *
     * @param accessToken
     *    {@link String} with the access token to check
     * @param clientId
     *    {@link JwtClientDetails#getClientId()} used to know the details to include
     *
     * @return {@link Map} with the {@code payload} of the given token
     *
     * @throws ClientNotFoundException if the given {@code clientId} does not exists in database
     * @throws UnAuthorizedException if the given {@code token} is not a valid one
     * @throws TokenExpiredException if the given {@code token} has expired
     */
    public Map<String, Object> checkAccessToken(String accessToken, String clientId) {
        try {
            JwtClientDetails clientDetails = jwtClientDetailsService.findByClientId(clientId);
            String decryptedJwtSecret = decryptJwtSecret(clientDetails.getJwtSecret());

            Map<String, Object> payload = jwtUtil.getExceptGivenKeys(accessToken, decryptedJwtSecret, new HashSet<>());
            if (!isAccessToken(payload))
                throw new UnAuthorizedException(format("The given token: %s related with clientId: %s is not an access one", accessToken, clientId));

            return payload;
        } catch (JwtException ex) {
            throw throwRelatedExceptionIfRequired(ex, format("There was an error checking the access token: %s related with clientId: %s", accessToken, clientId));
        }
    }


    /**
     * Check if the given {@code token} related with the given {@link JwtClientDetails#getClientId()} is a refresh one.
     *
     * @param refreshToken
     *    {@link String} with the refresh token to check
     * @param clientId
     *    {@link JwtClientDetails#getClientId()} used to know the details to include
     *
     * @return {@link Map} with the {@code payload} of the given token
     *
     * @throws ClientNotFoundException if the given {@code clientId} does not exists in database
     * @throws UnAuthorizedException if the given {@code token} is not a valid one
     * @throws TokenExpiredException if the given {@code token} has expired
     */
    public Map<String, Object> checkRefreshToken(String refreshToken, String clientId) {
        try {
            JwtClientDetails clientDetails = jwtClientDetailsService.findByClientId(clientId);
            String decryptedJwtSecret = decryptJwtSecret(clientDetails.getJwtSecret());

            Map<String, Object> payload = jwtUtil.getExceptGivenKeys(refreshToken, decryptedJwtSecret, new HashSet<>());
            if (!isAccessToken(payload))
                throw new UnAuthorizedException(format("The given token: %s related with clientId: %s is not an refresh one", refreshToken, clientId));

            return payload;
        } catch (JwtException ex) {
            throw throwRelatedExceptionIfRequired(ex, format("There was an error checking the refresh token: %s related with clientId: %s", refreshToken, clientId));
        }
    }


    /**
     * Get the {@code username} included in the given JWT {@code token}.
     *
     * @param token
     *    JWT token to extract the required information
     * @param clientId
     *    {@link JwtClientDetails#getClientId()} used to know the details to include
     *
     * @return {@link Optional} with {@code username} if exists, {@link Optional#empty()} otherwise
     *
     * @throws ClientNotFoundException if the given {@code clientId} does not exists in database or {@link AuthenticationConfigurationEnum}
     */
    public Optional<String> getUsername(String token, String clientId) {
        return ofNullable(token)
                .map(t -> AuthenticationConfigurationEnum.getByClientId(clientId))
                .map(authConfig -> applicationContext.getBean(authConfig.getAuthenticationGeneratorClass()))
                .flatMap(authGen -> {
                    JwtClientDetails clientDetails = jwtClientDetailsService.findByClientId(clientId);
                    return jwtUtil.getUsername(token, decryptJwtSecret(clientDetails.getJwtSecret()), authGen.getUsernameKey());
                });
    }


    /**
     * Get the {@code roles} included in the given JWT {@code token}.
     *
     * @param token
     *    JWT token to extract the required information
     * @param clientId
     *    {@link JwtClientDetails#getClientId()} used to know the details to include
     *
     * @return {@link Set} with {@code roles}
     *
     * @throws ClientNotFoundException if the given {@code clientId} does not exists in database or {@link AuthenticationConfigurationEnum}
     */
    public Set<String> getRoles(String token, String clientId) {
        return ofNullable(token)
                .map(t -> AuthenticationConfigurationEnum.getByClientId(clientId))
                .map(authConfig -> applicationContext.getBean(authConfig.getAuthenticationGeneratorClass()))
                .map(authGen -> {
                    JwtClientDetails clientDetails = jwtClientDetailsService.findByClientId(clientId);
                    return jwtUtil.getRoles(token, decryptJwtSecret(clientDetails.getJwtSecret()), authGen.getRolesKey());
                })
                .orElse(new HashSet<>());
    }


    /**
     * Get the additional information (included in the given {@code token} but not related with standard JWT)
     *
     * @param token
     *    JWT token to extract the required information
     * @param clientId
     *    {@link JwtClientDetails#getClientId()} used to know the details to include
     *
     * @return {@link Map}
     *
     * @throws ClientNotFoundException if the given {@code clientId} does not exists in database or {@link AuthenticationConfigurationEnum}
     */
    public Map<String, Object> getAdditionalInformation(String token, String clientId) {
        return ofNullable(token)
                .map(t -> jwtClientDetailsService.findByClientId(clientId))
                .map(clientDetails -> {
                    Set<String> keysToFilter = new HashSet<>(asList(
                            TokenKeyEnum.AUTHORITIES.getKey(),
                            TokenKeyEnum.CLIENT_ID.getKey(),
                            TokenKeyEnum.EXPIRATION_TIME.getKey(),
                            TokenKeyEnum.ISSUED_AT.getKey(),
                            TokenKeyEnum.JWT_ID.getKey(),
                            TokenKeyEnum.USERNAME.getKey()));
                    return jwtUtil.getExceptGivenKeys(token, decryptJwtSecret(clientDetails.getJwtSecret()), keysToFilter);
                })
                .orElse(new HashMap<>());
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

    /**
     * Check if the given {@code payload} contains information related with an JWT access token.
     *
     * @param payload
     *    JWT token payload information
     *
     * @return {@code true} if the {@code payload} comes from an access token, {@code false} otherwise
     */
    private boolean isAccessToken(Map<String, Object> payload) {
        return ofNullable(payload)
                .map(p -> null == p.get(TokenKeyEnum.REFRESH_JWT_ID.getKey()))
                .orElse(true);
    }

    /**
     * Decrypt the given {@code jwtSecret} related with a {@link JwtClientDetails}.
     */
    private String decryptJwtSecret(String jwtSecret) {
        return encryptor.decrypt(jwtSecret.replace(Constants.JWT_SECRET_PREFIX, ""));
    }

    /**
     * Convert the given {@link JwtException} in another one managed by the application.
     *
     * @param exception
     *    {@link JwtException} to transform
     * @param errorMessage
     *
     * @throws TokenExpiredException if the given {@link JwtException} is a {@link ExpiredJwtException} one
     * @throws UnAuthorizedException for the other uses cases
     */
    private RuntimeException throwRelatedExceptionIfRequired(JwtException exception, String errorMessage) {
        if (exception instanceof ExpiredJwtException)
            return new TokenExpiredException(errorMessage, exception);

        return new UnAuthorizedException(errorMessage, exception);
    }

}
