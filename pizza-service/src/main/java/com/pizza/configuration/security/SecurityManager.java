package com.pizza.configuration.security;

import com.pizza.service.cache.UserBlacklistCacheService;
import com.spring5microservices.common.dto.UsernameAuthoritiesDto;
import com.spring5microservices.common.enums.ExtendedHttpStatus;
import com.spring5microservices.common.exception.UnauthorizedException;
import com.spring5microservices.common.util.HttpUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 *    Manages the validation of the token related with a logged user, using the {@link Authentication}
 * to get and fill the required {@link UsernamePasswordAuthenticationToken} used later to know if the
 * user has the correct {@link GrantedAuthority}.
 */
@AllArgsConstructor
@Component
@Log4j2
public class SecurityManager implements ReactiveAuthenticationManager {

    @Lazy
    private final SecurityConfiguration securityConfiguration;

    @Lazy
    private final UserBlacklistCacheService userBlacklistCacheService;

    @Lazy
    private final WebClient webClient;


    @Override
    public Mono<Authentication> authenticate(final Authentication authentication) {
        String authToken = authentication.getCredentials().toString();
        return getAuthenticationInformation(
                securityConfiguration.getAuthenticationInformationWebService(),
                authToken
        )
        .map(this::getFromUsernameAuthoritiesDto);
    }


    /**
     * Using the given token gets the authentication information related with the logged user.
     *
     * @param authenticationInformationWebService
     *    Web service used to get authentication information
     * @param token
     *    Token (included Http authentication scheme)
     *
     * @return {@link Optional} of {@link UsernameAuthoritiesDto}
     */
    private Mono<UsernameAuthoritiesDto> getAuthenticationInformation(final String authenticationInformationWebService,
                                                                      final String token) {
        return webClient.post()
                .uri(authenticationInformationWebService)
                .header(
                        HttpHeaders.AUTHORIZATION,
                        buildAuthorizationHeader(
                                securityConfiguration.getClientId(),
                                securityConfiguration.getClientPassword()
                        )
                )
                .body(BodyInserters.fromValue(token))
                .retrieve()
                .onStatus(
                        httpStatus ->
                                List.of(BAD_REQUEST, UNAUTHORIZED, FORBIDDEN, NOT_FOUND).contains(httpStatus),
                        clientResponse -> {
                            log.warn(
                                    format(
                                            "There was an error invoking authorization server using provided token: %s. The response was: %s",
                                            token,
                                            clientResponse.rawStatusCode()
                                    )
                            );
                            return Mono.empty();
                        }
                )
                .onRawStatus(
                        httpStatus ->
                                ExtendedHttpStatus.TOKEN_EXPIRED.value() == httpStatus,
                        clientResponse -> {
                            log.warn(
                                    format(
                                            "The provided authentication token: %s has expired",
                                            token
                                    )
                            );
                            /**
                             *    {@link DefaultErrorAttributes#determineHttpStatus(Throwable, MergedAnnotation)} transforms
                             * the information of the thrown exception into the suitable HTTP status to return.
                             */
                            throw new ResponseStatusException(
                                    UNAUTHORIZED,
                                    "Provided token has expired"
                            );
                        }
                )
                .bodyToMono(UsernameAuthoritiesDto.class);
    }


    /**
     * Converts a given {@link UsernameAuthoritiesDto} into an {@link UsernamePasswordAuthenticationToken}
     *
     * @param usernameAuthoritiesDto
     *    {@link UsernameAuthoritiesDto} to convert
     *
     * @return {@link UsernamePasswordAuthenticationToken}
     *
     * @throws UnauthorizedException is the given {@code username} has been included in the black list.
     */
    private UsernamePasswordAuthenticationToken getFromUsernameAuthoritiesDto(final UsernameAuthoritiesDto usernameAuthoritiesDto) {
        if (userBlacklistCacheService.contains(usernameAuthoritiesDto.getUsername())) {
            throw new UnauthorizedException(
                    format("The given username: %s has been included in the blacklist",
                            usernameAuthoritiesDto.getUsername()
                    )
            );
        }
        Collection<? extends GrantedAuthority> authorities = ofNullable(usernameAuthoritiesDto.getAuthorities())
                .map(auth ->
                        auth.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(toList())
                )
                .orElseGet(ArrayList::new);

        UsernamePasswordAuthenticationToken authenticationInfo = new UsernamePasswordAuthenticationToken(
                usernameAuthoritiesDto.getUsername(),
                null,
                authorities
        );
        authenticationInfo.setDetails(usernameAuthoritiesDto.getAdditionalInfo());
        return authenticationInfo;
    }

    /**
     * Build the required Basic Authentication header to send requests to the security server.
     *
     * @param username
     *    Security server client identifier
     * @param password
     *    Security server client password
     *
     * @return {@link String}
     */
    private String buildAuthorizationHeader(final String username,
                                            final String password) {
        return HttpUtil.encodeBasicAuthentication(
                username,
                password
        );
    }

}
