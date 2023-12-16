package com.gatewayserver.filter;

import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.util.List;
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

    private final List<String> ROUTE_ID_TO_LOG_BODY_REQUEST = List.of(
            "order-service",
            "pizza-service",
            "security-jwt-service",
            "security-oauth-service"
    );


    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {
        final long requestStartTimeInNanoSeconds = System.nanoTime();
        logRequestData(exchange);

        final ServerHttpRequest decoratedRequest = this.shouldLogRequestBody(exchange)
                ? getDecoratedRequestAndLogBody(exchange.getRequest())
                : exchange.getRequest();

        return chain
                .filter(
                        exchange.mutate()
                                .request(decoratedRequest)
                                .build()
                ).then(
                        manageResponse(
                                exchange,
                                requestStartTimeInNanoSeconds
                        )
                );
    }


    /**
     * Determines if the body of the request must be added to the logged information or not.
     *
     * @param exchange
     *    {@link ServerWebExchange} with incoming request
     *
     * @return {@code true} if request's body must be added to the logs, {@code false} otherwise
     */
    private boolean shouldLogRequestBody(final ServerWebExchange exchange) {
        final String routeId = getRouteId(exchange);
        final boolean isPostMethod = getRequestMethod(exchange)
                .map(HttpMethod.POST::equals)
                .orElse(false);

        return ROUTE_ID_TO_LOG_BODY_REQUEST.contains(routeId) &&
                isPostMethod;
    }


    /**
     * Decorates the incoming {@link ServerHttpRequest} to log the request's body.
     *
     * @param request
     *    {@link ServerHttpRequest} with incoming request
     *
     * @return {@link ServerHttpRequest} with the decorated request
     */
    private ServerHttpRequest getDecoratedRequestAndLogBody(ServerHttpRequest request) {
        return new ServerHttpRequestDecorator(request) {
            @Override
            public Flux<DataBuffer> getBody() {
                return super.getBody()
                        .publishOn(Schedulers.boundedElastic())
                        .doOnNext(dataBuffer -> {
                            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                                Channels.newChannel(baos)
                                        .write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
                                String body = baos.toString(StandardCharsets.UTF_8);
                                log.info(
                                        format(
                                                "Body of incoming request: %s",
                                                body
                                        )
                                );
                            } catch (Exception e) {
                                log.error(
                                        format(
                                                "There was an error creating the decorated request: %s",
                                                getRequestURI(request)
                                        ),
                                        e
                                );
                            }
                        });
            }
        };
    }


    /**
     * Logs incoming request data, adding which route was routed to.
     *
     * @param exchange
     *    {@link ServerWebExchange} with incoming request
     */
    private void logRequestData(final ServerWebExchange exchange) {
        log.info(
                format("Incoming request: %s with method: %s, is routed to id: %s and uri: %s",
                        getRequestURI(exchange),
                        getRequestMethodValue(exchange),
                        getRouteId(exchange),
                        getRouteURI(exchange)
                )
        );
    }


    /**
     * Handles the response from the external microservice, adding logs and how much time it took.
     *
     * @param exchange
     *    {@link ServerWebExchange} with outgoing response
     * @param requestStartTimeInNanoSeconds
     *    Start time the request arrived at the gateway in nanoseconds
     *
     * @return {@link Mono} after complete required final steps
     */
    private <T> Mono<T> manageResponse(final ServerWebExchange exchange,
                                       final long requestStartTimeInNanoSeconds) {
        return Mono.fromRunnable(
                () -> {
                    logResponseData(
                            exchange,
                            requestStartTimeInNanoSeconds
                    );
                    getResponse(exchange)
                            .map(ServerHttpResponse::getHeaders)
                            .ifPresent(h -> {
                                if (null == h.getAccessControlAllowOrigin()) {
                                    h.setAccessControlAllowOrigin(ALLOW_ORIGIN_VALUE);
                                }
                            });
                });
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
        final String requestURI = getRequestURI(exchange);
        final String method = getRequestMethodValue(exchange);

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


    private Optional<HttpMethod> getRequestMethod(final ServerWebExchange exchange) {
        return getRequest(exchange)
                .map(ServerHttpRequest::getMethod);
    }


    private String getRequestMethodValue(final ServerWebExchange exchange) {
        return getRequest(exchange)
                .map(ServerHttpRequest::getMethodValue)
                .orElse(NO_VALUE_FOUND);
    }


    private String getRequestURI(final ServerWebExchange exchange) {
        return ofNullable(exchange)
                .map(ServerWebExchange::getRequest)
                .map(this::getRequestURI)
                .orElse(NO_VALUE_FOUND);
    }


    private String getRequestURI(final ServerHttpRequest request) {
        return ofNullable(request)
                .map(ServerHttpRequest::getURI)
                .map(URI::toASCIIString)
                .orElse(NO_VALUE_FOUND);
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


    private String getRouteId(final ServerWebExchange exchange) {
        return getServerWebExchangeAttribute(exchange, GATEWAY_ROUTE_ATTR)
                .map(r -> ((Route) r).getId())
                .orElse(NO_VALUE_FOUND);
    }


    private String getRouteURI(final ServerWebExchange exchange) {
        return getServerWebExchangeAttribute(exchange, GATEWAY_REQUEST_URL_ATTR)
                .map(Object::toString)
                .orElse(NO_VALUE_FOUND);
    }

}
