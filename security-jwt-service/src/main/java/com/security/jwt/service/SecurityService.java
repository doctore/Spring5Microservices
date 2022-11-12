package com.security.jwt.service;

import com.security.jwt.enums.AuthenticationConfigurationEnum;
import com.security.jwt.exception.ClientNotFoundException;
import com.security.jwt.model.JwtClientDetails;
import com.spring5microservices.common.dto.AuthenticationInformationDto;
import com.spring5microservices.common.dto.UsernameAuthoritiesDto;
import com.spring5microservices.common.exception.TokenExpiredException;
import com.spring5microservices.common.exception.UnauthorizedException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Optional.of;

@AllArgsConstructor
@Log4j2
@Service
public class SecurityService {

    @Lazy
    private final ApplicationContext applicationContext;

    @Lazy
    private final AuthenticationService authenticationService;


    /**
     *    Build the {@link AuthenticationInformationDto} with the specific information related with a {@code username}
     * and {@code clientId} (belongs to a {@link JwtClientDetails}).
     *
     * @param clientId
     *    {@link JwtClientDetails#getClientId()} used to know the details to include
     * @param username
     *    Identifier of the user who is trying to authenticate
     *
     * @return {@link Optional} of {@link AuthenticationInformationDto}
     *
     * @throws AccountStatusException if the {@link UserDetails} related with the given {@code username} is disabled
     * @throws ClientNotFoundException if the given {@code clientId} does not exist in database
     * @throws UnauthorizedException if the given {@code password} does not mismatch with exists one related with given {@code username}
     * @throws UsernameNotFoundException if the given {@code username} does not exist in database
     */
    public Optional<AuthenticationInformationDto> login(final String clientId,
                                                        final String username,
                                                        final String password) {
        return of(AuthenticationConfigurationEnum.getByClientId(clientId))
                .map(authConfig -> applicationContext.getBean(authConfig.getUserServiceClass()))
                .flatMap(userService -> {
                    UserDetails userDetails = userService.loadUserByUsername(username);
                    if (!userService.passwordsMatch(password, userDetails)) {
                        throw new UnauthorizedException(
                                format("The password given for the username: %s does not mismatch",
                                        username
                                )
                        );
                    }
                    return authenticationService.getAuthenticationInformation(clientId, userDetails);
                });
    }


    /**
     *    Build the {@link AuthenticationInformationDto} with the specific information using the given {@code refreshToken}
     * and {@code clientId} (belongs to a {@link JwtClientDetails}).
     *
     * @param refreshToken
     *    {@link String} with the refresh token to check
     * @param clientId
     *    {@link JwtClientDetails#getClientId()} used to know the details to include
     *
     * @return {@link Optional} of {@link AuthenticationInformationDto}
     *
     * @throws AccountStatusException if the {@link UserDetails} related with the given {@code username} included in the token is disabled
     * @throws ClientNotFoundException if the given {@code clientId} does not exist in database
     * @throws UnauthorizedException if the given {@code refreshToken} is not a valid one
     * @throws UsernameNotFoundException if the {@code refreshToken} does not contain a {@code username} or the included one does not exist in database
     * @throws TokenExpiredException if the given {@code refreshToken} has expired
     */
    public Optional<AuthenticationInformationDto> refresh(final String refreshToken,
                                                          final String clientId) {
        Map<String, Object> payload = authenticationService.getPayloadOfToken(refreshToken, clientId, false);
        String username = getUsernameFromPayload(payload, clientId);

        return of(AuthenticationConfigurationEnum.getByClientId(clientId))
                .map(authConfig -> applicationContext.getBean(authConfig.getUserServiceClass()))
                .flatMap(userService -> {
                    UserDetails userDetails = userService.loadUserByUsername(username);
                    return authenticationService.getAuthenticationInformation(
                            clientId,
                            userDetails
                    );
                });
    }


    /**
     * Extract from the given {@code accessToken} the following information:
     *  - Username
     *  - Roles
     *  - Additional information: included in {@code accessToken} but not related with standard JWT and included specifically
     *                            by every application.
     * @param accessToken
     *    {@link String} with the access token to use
     * @param clientId
     *    {@link JwtClientDetails#getClientId()} used to know the details to include
     *
     * @return {@link UsernameAuthoritiesDto}
     *
     * @throws ClientNotFoundException if the given {@code clientId} does not exist in database
     * @throws UnauthorizedException if the given {@code accessToken} is not a valid one
     * @throws UsernameNotFoundException if the {@code accessToken} does not contain a {@code username}
     * @throws TokenExpiredException if the given {@code accessToken} has expired
     */
    public UsernameAuthoritiesDto getAuthorizationInformation(final String accessToken,
                                                              final String clientId) {
        Map<String, Object> payload = authenticationService.getPayloadOfToken(accessToken, clientId, true);
        String username = getUsernameFromPayload(payload, clientId);

        return UsernameAuthoritiesDto.builder()
                .username(username)
                .authorities(
                        authenticationService.getRoles(
                                payload,
                                clientId
                        )
                )
                .additionalInfo(
                        authenticationService.getCustomInformationIncludedByClient(
                                payload,
                                clientId
                        )
                )
                .build();
    }


    /**
     * Extract the {@code username} included in the given {@code payload}
     *
     * @param payload
     *    {@link Map} with the content of a Jwt token
     * @param clientId
     *    {@link JwtClientDetails#getClientId()} used to know the details to include
     *
     * @return {@link String}
     *
     * @throws UsernameNotFoundException if the {@code payload} does not contain a {@code username}
     */
    private String getUsernameFromPayload(final Map<String, Object> payload,
                                          final String clientId) {
        Optional<String> username = authenticationService.getUsername(
                payload,
                clientId
        );
        return username.
                orElseThrow(() ->
                        new UsernameNotFoundException(
                                format("In the given payload with the keys: %s and related with the clientId: %s, there is no a username",
                                        null == payload
                                                ? "null"
                                                : StringUtils.collectionToCommaDelimitedString(payload.keySet()),
                                        clientId
                                )
                        )
                );
    }

}
