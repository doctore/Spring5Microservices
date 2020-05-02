package com.pizza.configuration.security;

import com.pizza.service.cache.UserBlacklistCacheService;
import com.spring5microservices.common.dto.UsernameAuthoritiesDto;
import com.spring5microservices.common.enums.ExtendedHttpStatus;
import com.spring5microservices.common.exception.TokenExpiredException;
import com.spring5microservices.common.exception.UnauthorizedException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 *    Manages the validation of the token related with a logged user, using the {@link Authentication}
 * to get and fill the required {@link UsernamePasswordAuthenticationToken} used later to know if the
 * user has the correct {@link GrantedAuthority}.
 */
@Component
@Log4j2
public class SecurityManager implements ReactiveAuthenticationManager {

    @Autowired
    private SecurityConfiguration securityConfiguration;

    @Autowired
    private UserBlacklistCacheService userBlacklistCacheService;

    @Autowired
    private WebClient webClient;


    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();
        return getAuthenticationInformation(securityConfiguration.getAuthenticationInformationWebService(), authToken)
                        .map(au -> getFromUsernameAuthoritiesDto(au));
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
    private Mono<UsernameAuthoritiesDto> getAuthenticationInformation(String authenticationInformationWebService, String token) {
        return webClient.post()
                .uri(authenticationInformationWebService)
                .header(HttpHeaders.AUTHORIZATION,
                        buildAuthorizationHeader(securityConfiguration.getClientId(), securityConfiguration.getClientPassword()))
                .body(BodyInserters.fromObject(token))
                .retrieve()
                .onStatus(httpStatus -> asList(BAD_REQUEST, UNAUTHORIZED, FORBIDDEN, NOT_FOUND).contains(httpStatus),
                        clientResponse -> Mono.empty())
                .onRawStatus(httpStatus -> ExtendedHttpStatus.TOKEN_EXPIRED.value() == httpStatus,
                        clientResponse -> {
                            throw new TokenExpiredException("The provided authentication token has expired");
                })
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
    private UsernamePasswordAuthenticationToken getFromUsernameAuthoritiesDto(UsernameAuthoritiesDto usernameAuthoritiesDto) {
        if (userBlacklistCacheService.contains(usernameAuthoritiesDto.getUsername())) {
            throw new UnauthorizedException(format("The given username: %s has been included in the blacklist",
                    usernameAuthoritiesDto.getUsername()));
        }
        Collection<? extends GrantedAuthority> authorities = usernameAuthoritiesDto.getAuthorities()
                .stream()
                .map(a -> new SimpleGrantedAuthority(a))
                .collect(Collectors.toList());

        UsernamePasswordAuthenticationToken authenticationInfo = new UsernamePasswordAuthenticationToken(
                usernameAuthoritiesDto.getUsername(), null, authorities);
        authenticationInfo.setDetails(usernameAuthoritiesDto.getAdditionalInfo());
        return authenticationInfo;
    }

    /**
     * Build the required Basic Authentication header to send requests to the security server
     *
     * @param username
     *    Security server client identifier
     * @param password
     *    Security server client password
     *
     * @return {@link HttpHeaders}
     */
    private String buildAuthorizationHeader(String username, String password) {
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
        return "Basic " + new String(encodedAuth);
    }

}
