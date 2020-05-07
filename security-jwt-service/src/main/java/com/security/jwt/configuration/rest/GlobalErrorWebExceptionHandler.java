package com.security.jwt.configuration.rest;

import com.security.jwt.exception.ClientNotFoundException;
import com.security.jwt.exception.TokenInvalidException;
import com.spring5microservices.common.exception.TokenExpiredException;
import com.spring5microservices.common.exception.UnauthorizedException;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
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
import java.util.List;
import java.util.stream.Collectors;

import static com.spring5microservices.common.enums.ExtendedHttpStatus.TOKEN_EXPIRED;
import static java.lang.String.format;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

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
        return buildPlainTextResponse("The account of the user is disabled", FORBIDDEN.value());
    }


    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<String> clientNotFoundException(ClientNotFoundException exception, WebRequest request) {
        log.error(getErrorMessageUsingHttpRequest(request), exception);
        return buildPlainTextResponse("Given invalid client details identifier", NOT_FOUND.value());
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> constraintViolationException(ConstraintViolationException exception, WebRequest request) {
        log.error(getErrorMessageUsingHttpRequest(request), exception);
        return buildListOfValidationErrorsResponse("The following constraints have failed: ", exception, BAD_REQUEST);
    }


    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<String> httpMessageConversionException(HttpMessageConversionException exception, WebRequest request) {
        log.error(getErrorMessageUsingHttpRequest(request), exception);
        return buildPlainTextResponse("The was a problem in the parameters of the current request", BAD_REQUEST.value());
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> methodArgumentNotValidException(MethodArgumentNotValidException exception, WebRequest request) {
        log.error(getErrorMessageUsingHttpRequest(request), exception);
        return buildListOfValidationErrorsResponse("Error in the given parameters: ", exception, BAD_REQUEST);
    }


    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> nullPointerException(NullPointerException exception, WebRequest request) {
        log.error(getErrorMessageUsingHttpRequest(request), exception);
        return buildPlainTextResponse("Trying to access to a non existing property", INTERNAL_SERVER_ERROR.value());
    }


    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> unAuthorizedException(UnauthorizedException exception, WebRequest request) {
        log.error(getErrorMessageUsingHttpRequest(request), exception);
        return buildPlainTextResponse("The user has no permissions to execute the request", UNAUTHORIZED.value());
    }


    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> usernameNotFoundException(UsernameNotFoundException exception, WebRequest request) {
        log.error(getErrorMessageUsingHttpRequest(request), exception);
        return buildPlainTextResponse("Given invalid credentials", NOT_FOUND.value());
    }


    @ExceptionHandler(Throwable.class)
    public ResponseEntity<String> throwable(Throwable exception, WebRequest request) {
        log.error(getErrorMessageUsingHttpRequest(request), exception);
        return buildPlainTextResponse("Internal error in the application", INTERNAL_SERVER_ERROR.value());
    }


    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<String> tokenExpiredException(TokenExpiredException exception, WebRequest request) {
        log.error(getErrorMessageUsingHttpRequest(request), exception);
        return buildPlainTextResponse("The provided token has expired", TOKEN_EXPIRED.value());
    }


    @ExceptionHandler(TokenInvalidException.class)
    public ResponseEntity<String> tokenInvalidException(TokenInvalidException exception, WebRequest request) {
        log.error(getErrorMessageUsingHttpRequest(request), exception);
        return buildPlainTextResponse("The provided token is invalid", FORBIDDEN.value());
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
        return format("There was an error trying to execute the request with: %s"
                        + "Http method = %s %s "
                        + "Uri = %s",
                System.lineSeparator(), httpRequest.getMethod(),
                System.lineSeparator(), httpRequest.getRequestURI());
    }

    /**
     *    Builds the Http response with the given information, including {@code httpStatusValue} and a custom message
     * to add in the content.
     *
     * @param responseMessage
     *    Information included in the returned response
     * @param httpStatusValue
     *    Used in the response as Http code to return
     *
     * @return {@link ResponseEntity} with the suitable Http response
     */
    private ResponseEntity<String> buildPlainTextResponse(String responseMessage, int httpStatusValue) {
        return ResponseEntity.status(httpStatusValue)
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
     *    {@link Exception} with the "not verified" validations or constraint exceptions
     * @param httpStatus
     *    {@link HttpStatus} used in the Http response
     *
     * @return {@link ResponseEntity} with the suitable Http response
     */
    private ResponseEntity<String> buildListOfValidationErrorsResponse(String responseMessage, Exception exception,
                                                                       HttpStatus httpStatus) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);

        if (exception instanceof MethodArgumentNotValidException)
            responseMessage += getErrors((MethodArgumentNotValidException) exception);
        if (exception instanceof ConstraintViolationException)
            responseMessage += getErrors((ConstraintViolationException)exception);

        return new ResponseEntity<>(responseMessage, headers, httpStatus);
    }

    private List<String> getErrors(MethodArgumentNotValidException exception) {
        return exception.getBindingResult().getFieldErrors().stream()
                .map(fe -> "Field error in object '" + fe.getObjectName()
                        + "' on field '" + fe.getField()
                        + "' due to: " + fe.getDefaultMessage())
                .collect(Collectors.toList());
    }

    private List<String> getErrors(ConstraintViolationException exception) {
        return exception.getConstraintViolations().stream()
                .map(ce -> "Error in path '" + ce.getPropertyPath()
                        + "' due to: " + ce.getMessage()
                )
                .collect(Collectors.toList());
    }

}