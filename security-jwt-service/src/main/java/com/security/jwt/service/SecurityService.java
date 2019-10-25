package com.security.jwt.service;

import com.security.jwt.enums.AuthenticationConfigurationEnum;
import com.security.jwt.exception.ClientNotFoundException;
import com.security.jwt.exception.UnAuthorizedException;
import com.security.jwt.model.JwtClientDetails;
import com.security.jwt.model.User;
import com.security.jwt.service.authentication.AuthenticationService;
import com.spring5microservices.common.dto.AuthenticationInformationDto;
import com.spring5microservices.common.dto.UsernameAuthoritiesDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.lang.String.format;
import static java.util.Optional.of;

@Log4j2
@Service
public class SecurityService {

    private ApplicationContext applicationContext;
    private AuthenticationService authenticationService;

    @Autowired
    public SecurityService(@Lazy ApplicationContext applicationContext, @Lazy AuthenticationService authenticationService) {
        this.applicationContext = applicationContext;
        this.authenticationService = authenticationService;
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
     * @throws AccountStatusException if the given {@link UserDetails} related with the given {@code username} is disabled
     * @throws ClientNotFoundException if the given {@code clientId} does not exists in database
     * @throws UnAuthorizedException if the given {@code password} does not mismatch with exists one related with given {@code username}
     * @throws UsernameNotFoundException if the given {@code username} does not exists in database
     */
    public Optional<AuthenticationInformationDto> login(String clientId, String username, String password) {
        return of(AuthenticationConfigurationEnum.getByClientId(clientId))
                .map(authConfig -> applicationContext.getBean(authConfig.getUserServiceClass()))
                .flatMap(userService -> {
                    UserDetails userDetails = userService.loadUserByUsername(username);
                    if (!userService.passwordsMatch(password, userDetails.getPassword()))
                        throw new UnAuthorizedException(format("The password given for the username: %s does not mismatch", username));

                    return authenticationService.getAuthenticationInformation(clientId, userDetails);
                });
    }


    public Optional<AuthenticationInformationDto> refreshToken(String refreshToken, String clientId) {

        return null;

    }


    public Optional<UsernameAuthoritiesDto> getAuthorizationInformation(String accessToken, String clientId) {

        return null;

    }

}
