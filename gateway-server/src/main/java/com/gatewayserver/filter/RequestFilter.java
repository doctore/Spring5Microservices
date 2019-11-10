package com.gatewayserver.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import java.util.Arrays;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.DEBUG_FILTER_ORDER;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
 * Used to log all the request.
 */
@Log4j2
@Component
public class RequestFilter extends ZuulFilter {

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
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        log.info(getRequestInformation(ctx.getRequest()));
        return null;
    }

    private String getRequestInformation(HttpServletRequest httpRequest) {
        return format("Invoked the Http request: %s"
                    + "Method = %s %s "
                    + "Uri = %s %s"
                    + "Parameters = %s",
                System.lineSeparator(), httpRequest.getMethod(),
                System.lineSeparator(), httpRequest.getRequestURI(),
                System.lineSeparator(), getParametersInformation(httpRequest));
    }

    private String getParametersInformation(HttpServletRequest httpRequest) {
        return httpRequest.getParameterMap()
                .entrySet().stream()
                .map(entry -> format("Name: %s value: %s %s", entry.getKey(),
                        Arrays.stream(entry.getValue()).collect(joining()),
                        System.lineSeparator()))
                .collect(joining());
    }

}
