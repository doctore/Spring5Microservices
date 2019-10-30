package com.security.jwt.configuration.rest;

import com.security.jwt.exception.ClientNotFoundException;
import com.security.jwt.exception.TokenExpiredException;
import com.security.jwt.exception.UnAuthorizedException;
import com.spring5microservices.common.enums.ExtendedHttpStatus;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ServerWebExchange;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

/**
 * Global exception handler to manage unhandled errors in the Rest layer (Controllers)
 */
@ControllerAdvice
@Log4j2
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalErrorWebExceptionHandler {

    @ExceptionHandler(AccountStatusException.class)
    public ResponseEntity<String> accountStatusException(AccountStatusException exception, WebRequest request) {
        log.error(getErrorMessageUsingHttpRequest(request), exception);
        return buildPlainTextResponse("The account of the user is disabled", FORBIDDEN);
    }


    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<String> clientNotFoundException(ClientNotFoundException exception, WebRequest request) {
        log.error(getErrorMessageUsingHttpRequest(request), exception);
        return buildPlainTextResponse("Given invalid client details identifier", NOT_FOUND);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> constraintViolationException(ConstraintViolationException exception, WebRequest request) {
        log.error(getErrorMessageUsingHttpRequest(request), exception);
        return buildPlainTextResponse(format("The following constraints have failed: %s", exception.getMessage()), BAD_REQUEST);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> methodArgumentNotValidException(MethodArgumentNotValidException exception, WebRequest request) {
        log.error(getErrorMessageUsingHttpRequest(request), exception);
        return buildListOfValidationErrorsResponse("Error in the given parameters: ", exception, UNPROCESSABLE_ENTITY);
    }


    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> nullPointerException(NullPointerException exception, WebRequest request) {
        log.error(getErrorMessageUsingHttpRequest(request), exception);
        return buildPlainTextResponse("Trying to access to a non existing property", INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<String> unAuthorizedException(UnAuthorizedException exception, WebRequest request) {
        log.error(getErrorMessageUsingHttpRequest(request), exception);
        return buildPlainTextResponse("The user has no permissions to execute the request", UNAUTHORIZED);
    }


    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> usernameNotFoundException(UsernameNotFoundException exception, WebRequest request) {
        log.error(getErrorMessageUsingHttpRequest(request), exception);
        return buildPlainTextResponse("Given invalid credentials", NOT_FOUND);
    }


    @ExceptionHandler(Throwable.class)
    public ResponseEntity<String> throwable(Throwable exception, WebRequest request) {
        log.error(getErrorMessageUsingHttpRequest(request), exception);
        return buildPlainTextResponse("Internal error in the application", INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<String> tokenExpiredException(TokenExpiredException exception, WebRequest request) {
        log.error(getErrorMessageUsingHttpRequest(request), exception);
        return buildPlainTextResponse("The provided token has expired", ExtendedHttpStatus.TOKEN_EXPIRED);
    }


    /**
     * Using the given {@link ServerWebExchange} builds a message with information about the Http request
     *
     * @param request
     *    {@link WebRequest} with the request information
     *
     * @return error message with Http request information
     */
    private String getErrorMessageUsingHttpRequest(WebRequest request) {
        HttpServletRequest httpRequest = ((ServletWebRequest)request).getRequest();

        return String.format("There was an error trying to execute the request with: %s "
                           + "Http method = %s %s "
                           + "Uri = %s",
                System.lineSeparator(),
                httpRequest.getMethod(), System.lineSeparator(),
                httpRequest.getRequestURI());
    }

    /**
     *    Builds the Http response with the given information, including {@link HttpStatus} and a custom message
     * to add in the content.
     *
     * @param responseMessage
     *    Information included in the returned response
     * @param httpStatus
     *    {@link HttpStatus} used in the Http response
     *
     * @return {@link ResponseEntity} with the suitable Http response
     */
    private ResponseEntity<String> buildPlainTextResponse(String responseMessage, HttpStatus httpStatus) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        return new ResponseEntity<>(responseMessage, headers, httpStatus);
    }

    /**
     *    Builds the Http response with the given information, including {@link ExtendedHttpStatus} and a custom message
     * to add in the content.
     *
     * @param responseMessage
     *    Information included in the returned response
     * @param httpStatus
     *    {@link ExtendedHttpStatus} used in the Http response
     *
     * @return {@link ResponseEntity} with the suitable Http response
     */
    private ResponseEntity<String> buildPlainTextResponse(String responseMessage, ExtendedHttpStatus httpStatus) {
        return ResponseEntity.status(httpStatus.value())
                             .contentType(MediaType.TEXT_PLAIN)
                             .body(responseMessage);
    }

    /**
     *    Builds the Http response with the given information, including {@link HttpStatus} and a custom message
     * with the "not verified" validations to include in the content.
     *
     * @param responseMessage
     *    Information included in the returned response
     * @param exception
     *    {@link MethodArgumentNotValidException} with the "not verified" validations
     * @param httpStatus
     *    {@link HttpStatus} used in the Http response
     *
     * @return {@link ResponseEntity} with the suitable Http response
     */
    private ResponseEntity<String> buildListOfValidationErrorsResponse(String responseMessage, MethodArgumentNotValidException exception,
                                                                       HttpStatus httpStatus) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        responseMessage += exception.getBindingResult()
                .getFieldErrors().stream()
                .map(fe -> "Field error in object '" + fe.getObjectName()
                         + "' on field '" + fe.getField()
                         + "' due to: " + fe.getDefaultMessage())
                .collect(Collectors.toList());

        return new ResponseEntity<>(responseMessage, headers, httpStatus);
    }

}
