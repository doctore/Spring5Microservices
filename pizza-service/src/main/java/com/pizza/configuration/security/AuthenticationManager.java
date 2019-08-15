package com.pizza.configuration.security;

import com.pizza.dto.UsernameAuthoritiesDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *    Manages the validation of the token related with a logged user, using the {@link Authentication}
 * to get and fill the required {@link UsernamePasswordAuthenticationToken} used later to know if the
 * user has the correct {@link GrantedAuthority}.
 */
@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationManager.class);

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Autowired
    private RestTemplate restTemplate;


    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();
        Optional<UsernameAuthoritiesDto> authInformation = getAuthenticationInformation(authenticationConfiguration.getAuthenticationInformation(),
                                                                                        authToken);
        return Mono.justOrEmpty(authInformation.map(au -> getFromUsernameAuthoritiesDto(au))
                                               .orElse(null));
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
    private Optional<UsernameAuthoritiesDto> getAuthenticationInformation(String authenticationInformationWebService, String token) {
        try {
            ResponseEntity<UsernameAuthoritiesDto> restResponse = restTemplate.getForEntity(authenticationInformationWebService,
                                                                                            UsernameAuthoritiesDto.class, token);
            return Optional.of(restResponse.getBody());
        } catch(Exception ex) {
            LOGGER.error("There was an error trying to validate the authentication token", ex);
            return Optional.empty();
        }
    }


    /**
     * Converts a given {@link UsernameAuthoritiesDto} into an {@link UsernamePasswordAuthenticationToken}
     *
     * @param usernameAuthoritiesDto
     *    {@link UsernameAuthoritiesDto} to convert
     *
     * @return {@link UsernamePasswordAuthenticationToken}
     */
    private UsernamePasswordAuthenticationToken getFromUsernameAuthoritiesDto(UsernameAuthoritiesDto usernameAuthoritiesDto) {

        Collection<? extends GrantedAuthority> authorities = usernameAuthoritiesDto.getAuthorities()
                                                                                   .stream()
                                                                                   .map(a -> new SimpleGrantedAuthority(a))
                                                                                   .collect(Collectors.toList());
        return new UsernamePasswordAuthenticationToken(usernameAuthoritiesDto.getUsername(), null, authorities);
    }

}
