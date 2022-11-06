package com.gatewayserver.configuration.rest;

import com.spring5microservices.common.dto.ErrorResponseDto;
import com.spring5microservices.common.enums.RestApiErrorCode;
import com.spring5microservices.common.util.JsonUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.spring5microservices.common.enums.RestApiErrorCode.INTERNAL;
import static com.spring5microservices.common.enums.RestApiErrorCode.VALIDATION;
import static java.lang.String.format;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * Global exception handler to manage unhandler errors in the Rest layer (Controllers)
 */
@RestControllerAdvice
@Log4j2
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalErrorWebExceptionHandler {

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
    @ExceptionHandler(ServerWebInputException.class)
    public Mono<Void> serverWebInputException(final ServerWebExchange exchange,
                                              final ServerWebInputException exception) {
        log.error(getErrorMessageUsingHttpRequest(exchange), exception);
        List<String> errorMessages = getServerWebInputExceptionErrorMessages(exception);
        return buildErrorResponse(
                VALIDATION,
                errorMessages,
                exchange,
                BAD_REQUEST
        );
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
    @ExceptionHandler(Throwable.class)
    public Mono<Void> throwable(final ServerWebExchange exchange,
                                final Throwable exception) {
        log.error(getErrorMessageUsingHttpRequest(exchange), exception);
        return buildErrorResponse(
                INTERNAL,
                List.of("Internal error in the application"),
                exchange,
                INTERNAL_SERVER_ERROR
        );
    }


    /**
     * Using the given {@link ServerWebExchange} builds a message with information about the Http request
     *
     * @param exchange
     *    {@link ServerWebExchange} with the request information
     *
     * @return error message with Http request information
     */
    private String getErrorMessageUsingHttpRequest(final ServerWebExchange exchange) {
        return format("There was an error trying to execute the request with:%s"
                + "Http method = %s %s"
                + "Uri = %s %s"
                + "Header = %s",
                System.lineSeparator(),
                exchange.getRequest().getMethod(),
                System.lineSeparator(),
                exchange.getRequest().getURI(),
                System.lineSeparator(),
                exchange.getRequest().getHeaders().entrySet()
        );
    }


    /**
     * Get the list of internal errors included in the given exception
     *
     * @param exception
     *    {@link ServerWebInputException} with the error information
     *
     * @return {@link List} of {@link String} with the error messages
     */
    private List<String> getServerWebInputExceptionErrorMessages(final ServerWebInputException exception) {
        if (exception.getCause() instanceof TypeMismatchException) {
            TypeMismatchException ex = (TypeMismatchException)exception.getCause();
            return List.of(
                    format("There was an type mismatch error in %s. The provided value was %s and required type is %s",
                            exception.getMethodParameter(),
                            ex.getValue(),
                            ex.getRequiredType())
            );
        }
        else {
            return List.of(
                    format("There was an error in %s due to %s",
                            exception.getMethodParameter(),
                            exception.getReason())
            );
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
     *    Http code used in the response
     *
     * @return {@link Mono} with the suitable Http response
     */
    private Mono<Void> buildErrorResponse(final RestApiErrorCode errorCode,
                                          final List<String> errorMessages,
                                          final ServerWebExchange exchange,
                                          final HttpStatus httpStatus) {
        exchange.getResponse()
                .setRawStatusCode(httpStatus.value());
        exchange.getResponse()
                .getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ErrorResponseDto error = new ErrorResponseDto(errorCode, errorMessages);
        byte[] responseMessageBytes = JsonUtil
                .toJson(error)
                .orElse("")
                .getBytes(StandardCharsets.UTF_8);

        DataBuffer bufferResponseMessage = exchange.getResponse()
                .bufferFactory()
                .wrap(responseMessageBytes);

        return exchange.getResponse()
                .writeWith(
                        Mono.just(bufferResponseMessage)
                );
    }

}