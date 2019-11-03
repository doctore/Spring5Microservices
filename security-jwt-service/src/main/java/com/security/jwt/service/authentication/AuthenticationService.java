package com.security.jwt.service.authentication;

import com.security.jwt.configuration.Constants;
import com.security.jwt.configuration.security.JweConfiguration;
import com.security.jwt.dto.RawAuthenticationInformationDto;
import com.security.jwt.enums.AuthenticationConfigurationEnum;
import com.security.jwt.exception.ClientNotFoundException;
import com.security.jwt.exception.TokenExpiredException;
import com.security.jwt.exception.UnAuthorizedException;
import com.security.jwt.model.JwtClientDetails;
import com.security.jwt.service.JwtClientDetailsService;
import com.security.jwt.util.JweUtil;
import com.security.jwt.util.JwsUtil;
import com.spring5microservices.common.dto.AuthenticationInformationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.security.jwt.enums.TokenKeyEnum.AUDIENCE;
import static com.security.jwt.enums.TokenKeyEnum.EXPIRATION_TIME;
import static com.security.jwt.enums.TokenKeyEnum.ISSUED_AT;
import static com.security.jwt.enums.TokenKeyEnum.JWT_ID;
import static com.security.jwt.enums.TokenKeyEnum.REFRESH_JWT_ID;
import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;

@Service
public class AuthenticationService {

    private ApplicationContext applicationContext;
    private JwtClientDetailsService jwtClientDetailsService;
    private JweConfiguration jweConfiguration;
    private JweUtil jweUtil;
    private JwsUtil jwsUtil;
    private TextEncryptor encryptor;

    @Autowired
    public AuthenticationService(@Lazy ApplicationContext applicationContext, @Lazy JwtClientDetailsService jwtClientDetailsService,
                                 @Lazy JweConfiguration jweConfiguration, @Lazy JweUtil jweUtil, JwsUtil jwsUtil,
                                 @Lazy TextEncryptor encryptor) {
        this.applicationContext = applicationContext;
        this.jwtClientDetailsService = jwtClientDetailsService;
        this.jweConfiguration = jweConfiguration;
        this.jweUtil = jweUtil;
        this.jwsUtil = jwsUtil;
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
     * Get the {@code payload} included in the given {@code token} related with the given {@link JwtClientDetails#getClientId()}.
     *
     * @param token
     *    {@link String} with the token of which to extract the payload
     * @param clientId
     *    {@link JwtClientDetails#getClientId()} used to know the details to include
     * @param isAccessToken
     *    {@code true} if {@code token} is an access one, {@code false} if it is a refresh token
     *
     * @return {@link Map} with the {@code payload} of the given token
     *
     * @throws ClientNotFoundException if the given {@code clientId} does not exists in database
     * @throws UnAuthorizedException if the given {@code token} is not a valid one
     * @throws TokenExpiredException if the given {@code token} has expired
     */
    public Map<String, Object> getPayloadOfToken(String token, String clientId, boolean isAccessToken) {
        JwtClientDetails clientDetails = jwtClientDetailsService.findByClientId(clientId);
        Map<String, Object> payload = getVerifiedPayloadOfToken(token, clientDetails);
        if (isAccessToken != isAccessToken(payload))
            throw new UnAuthorizedException(format("The given token: %s related with clientId: %s is not an "
                                                + (isAccessToken ? "access " : "refresh ") + "one", token, clientId));
        return payload;
    }


    /**
     * Get the {@code username} included in the given {@code payload}.
     *
     * @param payload
     *    {@link Map} with the content of a Jwt token
     * @param clientId
     *    {@link JwtClientDetails#getClientId()} used to know the details to include
     *
     * @return {@link Optional} with {@code username} if exists, {@link Optional#empty()} otherwise
     *
     * @throws ClientNotFoundException if the given {@code clientId} does not exists in {@link AuthenticationConfigurationEnum}
     */
    public Optional<String> getUsername(Map<String, Object> payload, String clientId) {
        return ofNullable(payload)
                .map(t -> AuthenticationConfigurationEnum.getByClientId(clientId))
                .map(authConfig -> applicationContext.getBean(authConfig.getAuthenticationGeneratorClass()))
                .map(authGen -> (String)payload.get(authGen.getUsernameKey()));
    }


    /**
     * Get the {@code roles} included in the given {@code payload}.
     *
     * @param payload
     *    {@link Map} with the content of a Jwt token
     * @param clientId
     *    {@link JwtClientDetails#getClientId()} used to know the details to include
     *
     * @return {@link Set} with {@code roles}
     *
     * @throws ClientNotFoundException if the given {@code clientId} does not exists in {@link AuthenticationConfigurationEnum}
     */
    public Set<String> getRoles(Map<String, Object> payload, String clientId) {
        return ofNullable(payload)
                .map(t -> AuthenticationConfigurationEnum.getByClientId(clientId))
                .map(authConfig -> applicationContext.getBean(authConfig.getAuthenticationGeneratorClass()))
                .map(authGen -> null == payload.get(authGen.getRolesKey()) ? null : new HashSet<>((List<String>)payload.get(authGen.getRolesKey())))
                .orElse(new HashSet<>());
    }


    /**
     * Get the additional information included in the given {@code payload} but not related with standard JWT
     *
     * @param payload
     *    {@link Map} with the content of a Jwt token
     * @param clientId
     *    {@link JwtClientDetails#getClientId()} used to know the details to include
     *
     * @return {@link Map}
     *
     * @throws ClientNotFoundException if the given {@code clientId} does not exists in {@link AuthenticationConfigurationEnum}
     */
    public Map<String, Object> getAdditionalInformation(Map<String, Object> payload, String clientId) {
        return ofNullable(payload)
                .map(t -> AuthenticationConfigurationEnum.getByClientId(clientId))
                .map(authConfig -> applicationContext.getBean(authConfig.getAuthenticationGeneratorClass()))
                .map(authGen -> {
                    Set<String> keysToFilter = new HashSet<>(asList(
                            authGen.getUsernameKey(),
                            authGen.getRolesKey(),
                            AUDIENCE.getKey(),
                            EXPIRATION_TIME.getKey(),
                            ISSUED_AT.getKey(),
                            JWT_ID.getKey(),
                            REFRESH_JWT_ID.getKey()));

                    return payload.entrySet().stream()
                            .filter(e -> !keysToFilter.contains(e.getKey()))
                            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
                })
                .orElse(new HashMap<>());
    }


    /**
     *    Build the information returned as response when a {@code username} trying to authenticate in a specific {@code application}
     * ({@link JwtClientDetails}).
     *
     * @param clientDetails
     *    {@link JwtClientDetails} with the details about how to generate JWS/JWE tokens
     * @param jwtRawInformation
     *    {@link RawAuthenticationInformationDto} with the information that should be included
     * @param jti
     *    JWS/JWE token identifier
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
     *    Return the access JWS/JWE token, merging the information should be included in this one with the given {@link JwtClientDetails}
     * wants to be include on it (stored in {@link RawAuthenticationInformationDto#getAccessTokenInformation()}).
     *
     * @param clientDetails
     *    {@link JwtClientDetails} with the details about how to generate JWS/JWE tokens
     * @param jwtRawInformation
     *    {@link RawAuthenticationInformationDto} with the information that should be included
     * @param jti
     *    JWS/JWE token identifier
     *
     * @return JWS/JWE access token
     */
    private String buildAccessToken(JwtClientDetails clientDetails, RawAuthenticationInformationDto jwtRawInformation,
                                    String jti) {
        Map<String, Object> tokenInformation = new HashMap<>(addToAccessToken(clientDetails.getClientId(), jti));
        if (null != jwtRawInformation)
            tokenInformation.putAll(jwtRawInformation.getAccessTokenInformation());
        return generateToken(tokenInformation, clientDetails, clientDetails.getAccessTokenValidity());
    }

    /**
     *    Return the refresh JWS/JWE token, merging the information should be included in this one with the given {@link JwtClientDetails}
     * wants to be include on it (stored in {@link RawAuthenticationInformationDto#getRefreshTokenInformation()}).
     *
     * @param clientDetails
     *    {@link JwtClientDetails} with the details about how to generate JWS/JWE tokens
     * @param jwtRawInformation
     *    {@link RawAuthenticationInformationDto} with the information that should be included
     * @param jti
     *    JWS/JWE token identifier
     *
     * @return JWS/JWE refresh token
     */
    private String buildRefreshToken(JwtClientDetails clientDetails, RawAuthenticationInformationDto jwtRawInformation,
                                     String jti) {
        Map<String, Object> tokenInformation = new HashMap<>(addToRefreshToken(clientDetails.getClientId(), jti));
        if (null != jwtRawInformation)
            tokenInformation.putAll(jwtRawInformation.getRefreshTokenInformation());
        return generateToken(tokenInformation, clientDetails, clientDetails.getRefreshTokenValidity());
    }

    /**
     * Return the standard information should be included in JWS/JWE access token.
     */
    private Map<String, Object> addToAccessToken(String clientId, String jti) {
        return new HashMap<String, Object>() {{
            put(AUDIENCE.getKey(), clientId);
            put(JWT_ID.getKey(), jti);
        }};
    }

    /**
     * Return the standard information should be included in JWS/JWE refresh token.
     */
    private Map<String, Object> addToRefreshToken(String clientId, String jti) {
        return new HashMap<String, Object>() {{
            put(AUDIENCE.getKey(), clientId);
            put(JWT_ID.getKey(), UUID.randomUUID().toString());
            put(REFRESH_JWT_ID.getKey(), jti);
        }};
    }

    /**
     * Check if the given {@code payload} contains information related with an JWS/JWE access token.
     *
     * @param payload
     *    JWS/JWE token payload information
     *
     * @return {@code true} if the {@code payload} comes from an access token, {@code false} otherwise
     */
    private boolean isAccessToken(Map<String, Object> payload) {
        return ofNullable(payload)
                .map(p -> null == p.get(REFRESH_JWT_ID.getKey()))
                .orElse(true);
    }

    /**
     * Decrypt the given {@code signatureSecret} related with a {@link JwtClientDetails}.
     */
    private String decryptSignatureSecret(String signatureSecret) {
        return encryptor.decrypt(signatureSecret.replace(Constants.CIPHER_SECRET_PREFIX, ""));
    }

    /**
     * Generate JWS or JWE token taking into account the information included in {@link JwtClientDetails#isUseJwe()}
     *
     * @param informationToInclude
     *    {@link Map} with the information to include in the returned JWS token
     * @param clientDetails
     *    {@link JwtClientDetails} with the details about how to generate JWS/JWE tokens
     * @param tokenValidityInSeconds
     *    How many seconds the JWS toke will be valid
     *
     * @return JWS/JWE token
     */
    private String generateToken(Map<String, Object> informationToInclude, JwtClientDetails clientDetails, int tokenValidityInSeconds) {
        if (clientDetails.isUseJwe())
            return jweUtil.generateToken(informationToInclude, clientDetails.getSignatureAlgorithm().getAlgorithm(),
                    decryptSignatureSecret(clientDetails.getSignatureSecret()), jweConfiguration.getEncryptionSecret(),
                    tokenValidityInSeconds);
        else
            return jwsUtil.generateToken(informationToInclude, clientDetails.getSignatureAlgorithm().getAlgorithm(),
                    decryptSignatureSecret(clientDetails.getSignatureSecret()), tokenValidityInSeconds);
    }

    /**
     * Get from the given JWS or JWE token its verified payload information.
     *
     * @param token
     *    {@link String} with the token of which to extract the payload
     * @param clientDetails
     *    {@link JwtClientDetails} with the details about if this one is using JWS or JWE tokens
     *
     * @return {@link Map} with the {@code payload} of the given token
     */
    private Map<String, Object> getVerifiedPayloadOfToken(String token, JwtClientDetails clientDetails) {
        if (clientDetails.isUseJwe())
            return jweUtil.getPayloadExceptGivenKeys(token, decryptSignatureSecret(clientDetails.getSignatureSecret()),
                    jweConfiguration.getEncryptionSecret(),new HashSet<>());
        else
            return jwsUtil.getPayloadExceptGivenKeys(token, decryptSignatureSecret(clientDetails.getSignatureSecret()), new HashSet<>());
    }

}
