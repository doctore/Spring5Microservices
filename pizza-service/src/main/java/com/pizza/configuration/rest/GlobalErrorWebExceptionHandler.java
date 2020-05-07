package com.pizza.configuration.rest;

import com.spring5microservices.common.dto.ErrorResponseDto;
import com.spring5microservices.common.enums.RestApiErrorCode;
import com.spring5microservices.common.exception.TokenExpiredException;
import com.spring5microservices.common.exception.UnauthorizedException;
import com.spring5microservices.common.util.JsonUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.TypeMismatchException;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.spring5microservices.common.enums.RestApiErrorCode.INTERNAL;
import static com.spring5microservices.common.enums.RestApiErrorCode.SECURITY;
import static com.spring5microservices.common.enums.RestApiErrorCode.VALIDATION;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.PRECONDITION_FAILED;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

/**
 * Global exception handler to manage unhandler errors in the Rest layer (Controllers)
 */
@Component
@Log4j2
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalErrorWebExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (ex instanceof WebExchangeBindException) {
            return webExchangeBindException(exchange, (WebExchangeBindException) ex);
        }
        else if (ex instanceof ConstraintViolationException) {
            return constraintViolationException(exchange, (ConstraintViolationException) ex);
        }
        else if (ex instanceof ServerWebInputException) {
            return serverWebInputException(exchange, (ServerWebInputException) ex);
        }
        else if (ex instanceof TokenExpiredException) {
            return tokenExpiredException(exchange, (TokenExpiredException) ex);
        }
        else if (ex instanceof UnauthorizedException) {
            return unauthorizedException(exchange, (UnauthorizedException) ex);
        }
        else {
            return throwable(exchange, ex);
        }
    }


    /**
     * Method used to manage when a Rest request throws a {@link WebExchangeBindException}
     *
     * @param exchange
     *    {@link ServerWebExchange} with the request information
     * @param exception
     *    {@link WebExchangeBindException} thrown
     *
     * @return {@link Mono} with the suitable response
     */
    private Mono<Void> webExchangeBindException(ServerWebExchange exchange, WebExchangeBindException exception) {
        log.error(getErrorMessageUsingHttpRequest(exchange), exception);
        List<String> errorMessages = exception.getBindingResult().getFieldErrors()
                .stream()
                .map(fe -> "Field error in object '" + fe.getObjectName()
                        + "' on field '" + fe.getField()
                        + "' due to: " + fe.getDefaultMessage())
                .collect(toList());
        return buildErrorResponse(VALIDATION, errorMessages, exchange,
                null != exception.getStatus() ? exception.getStatus() : UNPROCESSABLE_ENTITY);
    }


    /**
     * Method used to manage when a Rest request throws a {@link ConstraintViolationException}
     *
     * @param exchange
     *    {@link ServerWebExchange} with the request information
     * @param exception
     *    {@link ConstraintViolationException} thrown
     *
     * @return {@link Mono} with the suitable response
     */
    private Mono<Void> constraintViolationException(ServerWebExchange exchange, ConstraintViolationException exception) {
        log.error(getErrorMessageUsingHttpRequest(exchange), exception);
        List<String> errorMessages = exception.getConstraintViolations()
                .stream()
                .map(c -> {
                    String rawParameterName = c.getPropertyPath().toString();
                    return rawParameterName.substring(rawParameterName.lastIndexOf(".") + 1) + ": " + c.getMessage();
                })
                .collect(toList());
        return buildErrorResponse(VALIDATION, errorMessages, exchange, BAD_REQUEST);
    }


    /**
     * Method used to manage when a Rest request throws a {@link ServerWebInputException}
     *
     * @param exchange
     *    {@link ServerWebExchange} with the request information
     * @param exception
     *    {@link ServerWebInputException} thrown
     *
     * @return {@link Mono} with the suitable response
     */
    private Mono<Void> serverWebInputException(ServerWebExchange exchange, ServerWebInputException exception) {
        log.error(getErrorMessageUsingHttpRequest(exchange), exception);
        List<String> errorMessages = getServerWebInputExceptionErrorMessages(exception);
        return buildErrorResponse(VALIDATION, errorMessages, exchange, BAD_REQUEST);
    }


    /**
     * Method used to manage when a Rest request throws a {@link TokenExpiredException}
     *
     * @param exchange
     *    {@link ServerWebExchange} with the request information
     * @param exception
     *    {@link TokenExpiredException} thrown
     *
     * @return {@link Mono} with the suitable response
     */
    private Mono<Void> tokenExpiredException(ServerWebExchange exchange, TokenExpiredException exception) {
        log.error(getErrorMessageUsingHttpRequest(exchange), exception);
        return buildErrorResponse(SECURITY, asList("The given authorization token has expired"), exchange, PRECONDITION_FAILED);
    }


    /**
     * Method used to manage when a Rest request throws a {@link UnauthorizedException}
     *
     * @param exchange
     *    {@link ServerWebExchange} with the request information
     * @param exception
     *    {@link UnauthorizedException} thrown
     *
     * @return {@link Mono} with the suitable response
     */
    private Mono<Void> unauthorizedException(ServerWebExchange exchange, UnauthorizedException exception) {
        log.error(getErrorMessageUsingHttpRequest(exchange), exception);
        return buildErrorResponse(SECURITY, asList(exception.getMessage()), exchange, UNAUTHORIZED);
    }


    /**
     * Method used to manage when a Rest request throws a {@link Throwable}
     *
     * @param exchange
     *    {@link ServerWebExchange} with the request information
     * @param exception
     *    {@link Throwable} thrown
     *
     * @return {@link Mono} with the suitable response
     */
    private Mono<Void> throwable(ServerWebExchange exchange, Throwable exception) {
        log.error(getErrorMessageUsingHttpRequest(exchange), exception);
        return buildErrorResponse(INTERNAL, asList("Internal error in the application"), exchange, INTERNAL_SERVER_ERROR);
    }


    /**
     * Using the given {@link ServerWebExchange} builds a message with information about the Http request
     *
     * @param exchange
     *    {@link ServerWebExchange} with the request information
     *
     * @return error message with Http request information
     */
    private String getErrorMessageUsingHttpRequest(ServerWebExchange exchange) {
        return format("There was an error trying to execute the request with:%s"
                        + "Http method = %s %s"
                        + "Uri = %s %s"
                        + "Header = %s",
                System.lineSeparator(),
                exchange.getRequest().getMethod(), System.lineSeparator(),
                exchange.getRequest().getURI().toString(), System.lineSeparator(),
                exchange.getRequest().getHeaders().entrySet());
    }


    /**
     * Get the list of internal errors included in the given exception
     *
     * @param exception
     *    {@link ServerWebInputException} with the error information
     *
     * @return {@link List} of {@link String} with the error messages
     */
    private List<String> getServerWebInputExceptionErrorMessages(ServerWebInputException exception) {
        if (exception.getCause() instanceof TypeMismatchException) {
            TypeMismatchException ex = (TypeMismatchException)exception.getCause();
            return asList(format("There was an type mismatch error in %s. The provided value was %s and required type is %s",
                    exception.getMethodParameter(),
                    ex.getValue(),
                    ex.getRequiredType()));
        }
        else {
            return asList(format("There was an error in %s due to %s", exception.getMethodParameter(), exception.getReason()));
        }
    }


    /**
     * Builds the Http response related with an error, using the provided parameters.
     *
     * @param errorCode
     *    {@link RestApiErrorCode} included in the response
     * @param errorMessages
     *    {@link List} of error messages to include
     * @param exchange
     *    {@link ServerWebExchange} with the request information
     * @param httpStatus
     *    {@link HttpStatus} used in the Http response
     *
     * @return {@link Mono} with the suitable Http response
     */
    private Mono<Void> buildErrorResponse(RestApiErrorCode errorCode, List<String> errorMessages, ServerWebExchange exchange,
                                          HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ErrorResponseDto error = new ErrorResponseDto(errorCode, errorMessages);

        byte[] responseMessageBytes = JsonUtil.toJson(error).orElse("").getBytes(StandardCharsets.UTF_8);
        DataBuffer bufferResponseMessage = exchange.getResponse().bufferFactory().wrap(responseMessageBytes);
        return exchange.getResponse().writeWith(Mono.just(bufferResponseMessage));
    }

}