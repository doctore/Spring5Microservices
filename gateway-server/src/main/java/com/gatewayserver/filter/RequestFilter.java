package com.gatewayserver.filter;

import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

/**
 * Used to log all the request.
 */
@Component
@Log4j2
public class RequestFilter implements GlobalFilter {

    private final String NO_VALUE_FOUND = "no value found";
    private final String ALLOW_ORIGIN_VALUE = "*";


    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {
        final long requestStartTimeInNanoSeconds = System.nanoTime();
        logRequestData(exchange);
        return chain
                .filter(exchange)
                .then(
                        Mono.fromRunnable(
                                () -> {
                                    logResponseData(exchange, requestStartTimeInNanoSeconds);
                                    getResponse(exchange)
                                            .map(ServerHttpResponse::getHeaders)
                                            .ifPresent(h -> {
                                               if (null == h.getAccessControlAllowOrigin()) {
                                                   h.setAccessControlAllowOrigin(ALLOW_ORIGIN_VALUE);
                                               }
                                            });
                                })
                );
    }


    /**
     * Logs incoming request data, adding which route was routed to.
     *
     * @param exchange
     *    {@link ServerWebExchange} with incoming request
     */
    private void logRequestData(final ServerWebExchange exchange) {
        final String requestURI = getRequest(exchange)
                .map(ServerHttpRequest::getURI)
                .map(URI::toASCIIString)
                .orElse(NO_VALUE_FOUND);

        final String method = getRequest(exchange)
                .map(ServerHttpRequest::getMethodValue)
                .orElse(NO_VALUE_FOUND);

        final String routeId = getServerWebExchangeAttribute(exchange, GATEWAY_ROUTE_ATTR)
                .map(r -> ((Route) r).getId())
                .orElse(NO_VALUE_FOUND);

        final String routeUri = getServerWebExchangeAttribute(exchange, GATEWAY_REQUEST_URL_ATTR)
                .map(Object::toString)
                .orElse(NO_VALUE_FOUND);

        log.info(
                format("Incoming request: %s with method: %s, is routed to id: %s and uri: %s",
                        requestURI,
                        method,
                        routeId,
                        routeUri
                )
        );
    }


    /**
     * Logs the outgoing response, adding how long it took to handle the request
     *
     * @param exchange
     *    {@link ServerWebExchange} with outgoing response
     * @param requestStartTimeInNanoSeconds
     *    Start time the request arrived at the gateway in nanoseconds
     */
    private void logResponseData(final ServerWebExchange exchange,
                                 final long requestStartTimeInNanoSeconds) {

        final String requestURI = getRequest(exchange)
                .map(ServerHttpRequest::getURI)
                .map(URI::toASCIIString)
                .orElse(NO_VALUE_FOUND);

        final String method = getRequest(exchange)
                .map(ServerHttpRequest::getMethodValue)
                .orElse(NO_VALUE_FOUND);

        final String httpStatus = getResponseHttpStatus(exchange)
                .map(HttpStatus::value)
                .map(Objects::toString)
                .orElse(NO_VALUE_FOUND);

        final double requestFinalTimeInMilliseconds = (System.nanoTime() - requestStartTimeInNanoSeconds) * 0.000001;
        final String requestFinalTimeInMillisecondsText = String.format("%.03f", requestFinalTimeInMilliseconds);

        log.info(
                format("Outgoing response with method: %s, uri: %s, returned HTTP status: %s, required time in milliseconds to manage it: %s",
                        method,
                        requestURI,
                        httpStatus,
                        requestFinalTimeInMillisecondsText
                )
        );
    }


    private Optional<ServerHttpRequest> getRequest(final ServerWebExchange exchange) {
        return ofNullable(exchange)
                .map(ServerWebExchange::getRequest);
    }


    private Optional<ServerHttpResponse> getResponse(final ServerWebExchange exchange) {
        return ofNullable(exchange)
                .map(ServerWebExchange::getResponse);
    }


    private Optional<HttpStatus> getResponseHttpStatus(final ServerWebExchange exchange) {
        return getResponse(exchange)
                .map(ServerHttpResponse::getStatusCode);
    }


    private <T> Optional<T> getServerWebExchangeAttribute(final ServerWebExchange exchange,
                                                          final String attribute) {
        return ofNullable(exchange)
                .map(ex -> ex.getAttribute(attribute));
    }

}
