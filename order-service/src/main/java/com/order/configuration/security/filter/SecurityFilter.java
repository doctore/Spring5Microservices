package com.order.configuration.security.filter;

import com.order.configuration.security.SecurityConfiguration;
import com.order.configuration.security.SecurityManager;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * Invoke security validations for the given Http requests
 */
@AllArgsConstructor
@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Lazy
    private final SecurityManager securityManager;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(AUTHORIZATION);
        if (!StringUtils.isEmpty(token)) {
            String tokenData = token.replace(SecurityConfiguration.TOKEN_PREFIX, "");
            securityManager.authenticate(tokenData).ifPresent(SecurityContextHolder.getContext()::setAuthentication);
        }
        filterChain.doFilter(request, response);
    }

}
