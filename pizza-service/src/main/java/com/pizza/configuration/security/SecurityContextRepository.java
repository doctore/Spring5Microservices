package com.pizza.configuration.security;

import com.pizza.configuration.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 *    Gets the token included in {@code Authorization} Http header and
 * forwarded to {@link SecurityManager} to verify it.
 */
@Component
public class SecurityContextRepository implements ServerSecurityContextRepository {

    private final SecurityManager securityManager;


    @Autowired
    public SecurityContextRepository(@Lazy final SecurityManager securityManager) {
        this.securityManager = securityManager;
    }


    @Override
    public Mono<Void> save(final ServerWebExchange swe,
                           final SecurityContext sc) {
        throw new UnsupportedOperationException("Not supported operation");
    }


    @Override
    public Mono<SecurityContext> load(final ServerWebExchange swe) {
        ServerHttpRequest request = swe.getRequest();
        String authHeader = request.getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (null != authHeader) {
            authHeader = authHeader.replace(Constants.TOKEN_PREFIX, "");
            Authentication auth = new UsernamePasswordAuthenticationToken(authHeader, authHeader);
            return this.securityManager
                    .authenticate(auth)
                    .map(SecurityContextImpl::new);
        } else {
            return Mono.empty();
        }
    }

}
