package com.order.configuration.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * Global exception handler to manage unhandler errors in the Rest layer (Controllers)
 */
@Component
@Order(-2)
public class GlobalErrorWebExceptionHandler implements ErrorWebExceptionHandler {

    Logger logger = LoggerFactory.getLogger(GlobalErrorWebExceptionHandler.class);


    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (ex instanceof NullPointerException) {
            return nullPointerException(exchange, (NullPointerException) ex);
        }
        else if (ex instanceof WebExchangeBindException) {
            return webExchangeBindException(exchange, (WebExchangeBindException) ex);
        }
        else if (ex instanceof ConstraintViolationException) {
            return constraintViolationException(exchange, (ConstraintViolationException) ex);
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
        logger.error(getErrorMessageUsingHttpRequest(exchange), exception);
        return buildListOfValidationErrorsResponse("Error in the given parameters: ", exchange, exception,
                                                   HttpStatus.UNPROCESSABLE_ENTITY);
    }


    /**
     * Method used to manage when a Rest request throws a {@link NullPointerException}
     *
     * @param exchange
     *    {@link ServerWebExchange} with the request information
     * @param exception
     *    {@link NullPointerException} thrown
     *
     * @return {@link Mono} with the suitable response
     */
    private Mono<Void> nullPointerException(ServerWebExchange exchange, NullPointerException exception) {
        logger.error("There was a NullPointerException. " + getErrorMessageUsingHttpRequest(exchange), exception);
        return buildPlainTestResponse("Trying to access to a non existing property", exchange,
                                      HttpStatus.INTERNAL_SERVER_ERROR);
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
        logger.error("There was a ConstraintViolationException. " + getErrorMessageUsingHttpRequest(exchange), exception);
        return buildPlainTestResponse("The following constraints have failed: " + exception.getMessage(),
                                      exchange, HttpStatus.BAD_REQUEST);
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
        logger.error(getErrorMessageUsingHttpRequest(exchange), exception);
        return buildPlainTestResponse("Internal error in the application", exchange,
                HttpStatus.INTERNAL_SERVER_ERROR);
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
        return String.format("There was an error trying to execute the request with:%s" +
                        "Http method = %s %s" +
                        "Uri = %s %s" +
                        "Header = %s",
                System.lineSeparator(),
                exchange.getRequest().getMethod(), System.lineSeparator(),
                exchange.getRequest().getURI().toString(), System.lineSeparator(),
                exchange.getRequest().getHeaders().entrySet());
    }


    /**
     *    Builds the Http response with the given information, including {@link HttpStatus} and a custom message
     * to include in the content.
     *
     * @param responseMessage
     *    Information included in the returned response
     * @param exchange
     *    {@link ServerWebExchange} with the request information
     * @param httpStatus
     *    {@link HttpStatus} used in the Http response
     *
     * @return {@link Mono} with the suitable Http response
     */
    private Mono<Void> buildPlainTestResponse(String responseMessage, ServerWebExchange exchange, HttpStatus httpStatus) {

        exchange.getResponse().setStatusCode(httpStatus);
        exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);

        byte[] responseMessageBytes = responseMessage.getBytes(StandardCharsets.UTF_8);
        DataBuffer bufferResponseMessage = exchange.getResponse().bufferFactory().wrap(responseMessageBytes);
        return exchange.getResponse().writeWith(Mono.just(bufferResponseMessage));
    }


    /**
     *    Builds the Http response with the given information, including {@link HttpStatus} and a custom message
     * to include in the content.
     *
     * @param responseMessage
     *    Information included in the returned response
     * @param exchange
     *    {@link ServerWebExchange} with the request information
     * @param exception
     *    {@link WebExchangeBindException} with the error information
     * @param httpStatus
     *    {@link HttpStatus} used in the Http response
     *
     * @return {@link Mono} with the suitable Http response
     */
    private Mono<Void> buildListOfValidationErrorsResponse(String responseMessage, ServerWebExchange exchange,
                                                           WebExchangeBindException exception, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);

        responseMessage += exception.getBindingResult()
                .getFieldErrors().stream()
                .map(fe -> "Field error in object '" + fe.getObjectName()
                        + "' on field '" + fe.getField()
                        + "' due to: " + fe.getDefaultMessage())
                .collect(Collectors.toList());

        byte[] responseMessageBytes = responseMessage.getBytes(StandardCharsets.UTF_8);
        DataBuffer bufferResponseMessage = exchange.getResponse().bufferFactory().wrap(responseMessageBytes);
        return exchange.getResponse().writeWith(Mono.just(bufferResponseMessage));
    }

}