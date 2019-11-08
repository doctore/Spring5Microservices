package com.gatewayserver.filter;

import com.gatewayserver.configuration.security.AuthenticationConfiguration;
import com.gatewayserver.configuration.cache.CacheConfiguration;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
//import com.spring5microservices.common.service.CacheService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.DEBUG_FILTER_ORDER;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
 *    {@link ZuulFilter} used to be sure every JWT token included in the Authorization Http header
 * is valid and it has not expired.
 */
@Log4j2
@Component
public class AuthenticationFilter extends ZuulFilter {

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Autowired
    private RestTemplate restTemplate;


@Autowired
private CacheConfiguration cacheConfiguration;

//@Autowired
//private CacheService cacheService;


    @Override
    public String filterType() {
        return PRE_TYPE;
    }


    @Override
    public int filterOrder() {
        return DEBUG_FILTER_ORDER;
    }


    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        return !ctx.containsKey(HttpHeaders.AUTHORIZATION);
    }


    @Override
    public Object run() {
        return null;


        /*

        // TODO: Removes this filter, the security will be managed by every application
        // There will be only a cache to manage generated token - real one for: security-oauth-service

        RequestContext ctx = RequestContext.getCurrentContext();

        // If we are dealing with a call to the authentication service, let the call go through without authenticating
        if (ctx.getRequest().getRequestURI().contains(authenticationConfiguration.getAllowedRequestURI())){
            return null;
        }
        if (!shouldFilter()) {
            log.debug("Authentication token is not present");
            ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
            ctx.setSendZuulResponse(false);
            return null;
        }
        if (!isAuthenticationTokenValid(authenticationConfiguration.getValidateTokenWebService(),
                                        ctx.getRequest().getHeader(HttpHeaders.AUTHORIZATION))) {
            log.debug("Authentication token is not valid");
            ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
            ctx.setSendZuulResponse(false);
            return null;
        }
        return null;
         */
    }


    /**
     * Checks if the given authentication token is valid or not
     *
     * @param validateTokenWebService
     *    Web service used to validate the given token
     * @param token
     *    Token (included Http authentication scheme)
     *
     * @return {@code true} if the given {@code token} is valid, {@code false} otherwise
     */
    private boolean isAuthenticationTokenValid(String validateTokenWebService, String token) {
        try {
            ResponseEntity<Boolean> restResponse = restTemplate.getForEntity(validateTokenWebService, Boolean.class, token);
            return restResponse.getBody();
        } catch(Exception ex) {
            log.error("There was an error trying to validate the authentication token", ex);
            return false;
        }
    }

}