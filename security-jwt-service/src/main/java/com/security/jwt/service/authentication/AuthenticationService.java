package com.security.jwt.service.authentication;

import com.security.jwt.configuration.Constants;
import com.security.jwt.dto.RawAuthenticationInformationDto;
import com.security.jwt.enums.AuthenticationConfigurationEnum;
import com.security.jwt.enums.TokenKeyEnum;
import com.security.jwt.enums.TokenVerificationEnum;
import com.security.jwt.exception.ClientNotFoundException;
import com.security.jwt.exception.TokenExpiredException;
import com.security.jwt.exception.UnAuthorizedException;
import com.security.jwt.model.JwtClientDetails;
import com.security.jwt.service.JwtClientDetailsService;
import com.security.jwt.util.JwtUtil;
import com.spring5microservices.common.dto.AuthenticationInformationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
     * @throws ClientNotFoundException if the given {@code clientId} does not exists in database
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
     * @param clientId
     *    {@link JwtClientDetails#getClientId()} used to know the details to include
     * @param accessToken
     *    {@link String} with the access token to check
     *
     * @throws ClientNotFoundException if the given {@code clientId} does not exists in database
     * @throws UnAuthorizedException if the given {@code token} is not a valid one
     * @throws TokenExpiredException if the given {@code token} has expired
     */
    public void checkAccessToken(String clientId, String accessToken) {
        JwtClientDetails clientDetails = jwtClientDetailsService.findByClientId(clientId);
        String decryptedJwtSecret = decryptJwtSecret(clientDetails.getJwtSecret());

        TokenVerificationEnum verificationResult = jwtUtil.isTokenValid(accessToken, decryptedJwtSecret);
        verificationResult.throwRelatedExceptionIfRequired(
                format("The validation of the token: %s related with clientId: %s returns the following result: %s",
                        accessToken, clientId, verificationResult.name()));

        if (!isAccessToken(accessToken, decryptedJwtSecret))
            throw new UnAuthorizedException(format("The given token: %s related with clientId: %s is not an access one",
                    accessToken, clientId));
    }


    /**
     * Check if the given {@code token} related with the given {@link JwtClientDetails#getClientId()} is a refresh one.
     *
     * @param clientId
     *    {@link JwtClientDetails#getClientId()} used to know the details to include
     * @param refreshToken
     *    {@link String} with the refresh token to check
     *
     * @throws ClientNotFoundException if the given {@code clientId} does not exists in database
     * @throws UnAuthorizedException if the given {@code token} is not a valid one
     * @throws TokenExpiredException if the given {@code token} has expired
     */
    public void checkRefreshToken(String clientId, String refreshToken) {
        JwtClientDetails clientDetails = jwtClientDetailsService.findByClientId(clientId);
        String decryptedJwtSecret = decryptJwtSecret(clientDetails.getJwtSecret());

        TokenVerificationEnum verificationResult = jwtUtil.isTokenValid(refreshToken, decryptedJwtSecret);
        verificationResult.throwRelatedExceptionIfRequired(
                format("The validation of the token: %s related with clientId: %s returns the following result: %s",
                        refreshToken, clientId, verificationResult.name()));

        if (isAccessToken(refreshToken, decryptedJwtSecret))
            throw new UnAuthorizedException(format("The given token: %s related with clientId: %s is not a refresh one",
                    refreshToken, clientId));
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
     * Check if the given Jwt token is or not an access one.
     *
     * @param token
     *    JWT token to extract the required information
     * @param jwtSecretKey
     *    {@link String} used to encrypt the JWT token
     *
     * @return {@code true} if the token is an access one, {@code false} otherwise
     */
    private boolean isAccessToken(String token, String jwtSecretKey) {
        return !jwtUtil.getKey(token, jwtSecretKey, TokenKeyEnum.REFRESH_JWT_ID.getKey(), String.class).isPresent();
    }


    private String decryptJwtSecret(String jwtSecret) {
        return encryptor.decrypt(jwtSecret.replace(Constants.JWT_SECRET_PREFIX, ""));
    }

}
