package com.gatewayserver.controller;

import com.gatewayserver.configuration.rest.RestRoutes;
import com.spring5microservices.common.dto.ErrorResponseDto;
import com.spring5microservices.common.enums.RestApiErrorCode;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.spring5microservices.common.enums.RestApiErrorCode.INTERNAL;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

/**
 * Rest services to work with problems in accessible microservices
 */
@AllArgsConstructor
@Log4j2
@RestController
@RequestMapping(RestRoutes.CIRCUIT_BREAKER.ROOT)
public class CircuitBreakerController {

    @RequestMapping(RestRoutes.CIRCUIT_BREAKER.ORDER_SERVICE)
    public Mono<ResponseEntity<ErrorResponseDto>> orderServiceFailureRedirect() {
        String errorMessage = "Sorry, orders service is down at this moment. Please try again later";
        log.error(errorMessage);
        return Mono.just(
                buildErrorResponse(
                      INTERNAL,
                      errorMessage,
                      SERVICE_UNAVAILABLE
                )
        );
    }


    @RequestMapping(RestRoutes.CIRCUIT_BREAKER.PIZZA_SERVICE)
    public Mono<ResponseEntity<ErrorResponseDto>> pizzaServiceFailureRedirect() {
        String errorMessage = "Sorry, pizza service is down at this moment. Please try again later";
        log.error(errorMessage);
        return Mono.just(
                buildErrorResponse(
                        INTERNAL,
                        errorMessage,
                        SERVICE_UNAVAILABLE
                )
        );
    }


    @RequestMapping(RestRoutes.CIRCUIT_BREAKER.SECURITY_SERVICE)
    public Mono<ResponseEntity<ErrorResponseDto>> securityServiceFailureRedirect() {
        String errorMessage = "Sorry, security service is down at this moment. Please try again later";
        log.error(errorMessage);
        return Mono.just(
                buildErrorResponse(
                        INTERNAL,
                        errorMessage,
                        SERVICE_UNAVAILABLE
                )
        );
    }


    @RequestMapping(RestRoutes.CIRCUIT_BREAKER.SECURITY_OAUTH_SERVICE)
    public Mono<ResponseEntity<ErrorResponseDto>> securityOauthServiceFailureRedirect() {
        String errorMessage = "Sorry, security Oauth service is down at this moment. Please try again later";
        log.error(errorMessage);
        return Mono.just(
                buildErrorResponse(
                        INTERNAL,
                        errorMessage,
                        SERVICE_UNAVAILABLE
                )
        );
    }


    /**
     * Builds the {@link ResponseEntity} response related with an error, using the provided parameters.
     *
     * @param errorCode
     *    {@link RestApiErrorCode} included in the response
     * @param errorMessage
     *    {@link String} of error message to include
     * @param httpStatus
     *    Http code used in the response
     *
     * @return {@link ResponseEntity} with the suitable response
     */
    private ResponseEntity<ErrorResponseDto> buildErrorResponse(final RestApiErrorCode errorCode,
                                                                final String errorMessage,
                                                                final HttpStatus httpStatus) {
        ErrorResponseDto error = new ErrorResponseDto(
                errorCode,
                List.of(errorMessage)
        );
        return new ResponseEntity<>(
                error,
                httpStatus
        );
    }

}
