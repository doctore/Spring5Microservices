package com.gatewayserver.filter;

import com.gatewayserver.enums.HttpHeaderEnum;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.stereotype.Component;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.DEBUG_FILTER_ORDER;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;

/**
 *    {@link ZuulFilter} used to track every microservice invocation with an unique identification.
 * If more than one microservice has to be invoked to complete a flow, the same identification will
 * be included the same identification will be included in all of them.
 */
@Component
public class TrackingPostFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return POST_TYPE;
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
        ctx.getResponse().addHeader(HttpHeaderEnum.CORRELATION_ID.getHttpHeader(), getCorrelationId(ctx));
        return null;
    }

    /**
     * Get the unique identification of every microservice invocation
     *
     * @param ctx
     *    {@link RequestContext} with the information about the request
     *
     * @return {@link String} with an unique identification
     */
    private String getCorrelationId(RequestContext ctx) {
        String correlationIdHeader = HttpHeaderEnum.CORRELATION_ID.getHttpHeader();

        return null != ctx.getRequest().getHeader(correlationIdHeader)
                ? ctx.getRequest().getHeader(correlationIdHeader)
                : ctx.getZuulRequestHeaders().get(correlationIdHeader);
    }

}
