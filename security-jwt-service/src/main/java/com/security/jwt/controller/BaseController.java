package com.security.jwt.controller;

import com.spring5microservices.common.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

public abstract class BaseController {

    /**
     * Get the authenticated {@link UserDetails} to know the application is trying to use the provided web services.
     *
     * @return {@link UserDetails}
     *
     * @throws UnauthorizedException if the given {@code clientId} does not exist in database
     */
    protected Mono<UserDetails> getPrincipal() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .cast(UserDetails.class);
    }

}
