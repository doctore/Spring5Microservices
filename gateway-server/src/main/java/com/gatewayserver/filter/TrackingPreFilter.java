package com.gatewayserver.filter;

import com.gatewayserver.enums.HttpHeaderEnum;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.DEBUG_FILTER_ORDER;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
 *    {@link ZuulFilter} used to track every microservice invocation with an unique identification.
 * If more than one microservice has to be invoked to complete a flow, the same identification will
 * be included the same identification will be included in all of them.
 */
@Component
public class TrackingPreFilter extends ZuulFilter {

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
        return !ctx.containsKey(HttpHeaderEnum.CORRELATION_ID.getHttpHeader());
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.addZuulRequestHeader(HttpHeaderEnum.CORRELATION_ID.getHttpHeader(), generateCorrelationId());
        return null;
    }

    /**
     * Generate the unique identification of every microservice invocation
     *
     * @return {@link String} with an unique identification
     */
    private String generateCorrelationId(){
        return UUID.randomUUID().toString();
    }

}
