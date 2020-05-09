package com.order.configuration.security;

import com.order.dto.UsernameAuthoritiesDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *    Manages the validation of the token related with a logged user, using the {@link Authentication}
 * to get and fill the required {@link UsernamePasswordAuthenticationToken} used later to know if the
 * user has the correct {@link GrantedAuthority}.
 */
@Component
@Log4j2
public class SecurityManager {

    @Autowired
    private SecurityConfiguration securityConfiguration;

    @Autowired
    private RestTemplate restTemplate;


    public Optional<Authentication> authenticate(String authToken) {
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
    private Optional<UsernameAuthoritiesDto> getAuthenticationInformation(String authenticationInformationWebService, String token) {
        try {
            HttpEntity<String> request = new HttpEntity<>(createHeaders(securityConfiguration.getClientId(), securityConfiguration.getClientPassword()));
            ResponseEntity<UsernameAuthoritiesDto> restResponse = restTemplate.exchange(authenticationInformationWebService, HttpMethod.GET,
                    request, UsernameAuthoritiesDto.class, token);
            return Optional.of(restResponse.getBody());
        } catch(Exception ex) {
            log.error("There was an error trying to validate the authentication token", ex);
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

    /**
     * Build the required Basic Authentication to send requests to the Oauth 2.0 security server
     *
     * @param username
     *    Oauth 2.0 server client identifier
     * @param password
     *    Oauth 2.0 server client password
     *
     * @return {@link HttpHeaders}
     */
    private HttpHeaders createHeaders(String username, String password){
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
            String authHeader = "Basic " + new String(encodedAuth);
            set("Authorization", authHeader);
        }};
    }

}
