package com.authenticationservice.configuration.rest;

import io.jsonwebtoken.MalformedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * Global exception handler to manage unhandler errors in the Rest layer (Controllers)
 */
@ControllerAdvice
@Order(-2)
public class GlobalErrorWebExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalErrorWebExceptionHandler.class);


    /**
     * Method used to manage when a Rest request throws a {@link ConstraintViolationException}
     *
     * @param exception
     *    {@link ConstraintViolationException} thrown
     * @param request
     *    {@link WebRequest} with the request information
     *
     * @return {@link ResponseEntity} with the suitable response and error message
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> constraintViolationException(ConstraintViolationException exception, WebRequest request) {
        LOGGER.error(getErrorMessageUsingHttpRequest(request), exception);
        return buildPlainTextResponse("The following constraints have failed: " + exception.getMessage(),
                                      HttpStatus.BAD_REQUEST);
    }


    /**
     * Method used to manage when a Rest request throws a {@link MalformedJwtException}
     *
     * @param exception
     *    {@link MalformedJwtException} thrown
     * @param request
     *    {@link WebRequest} with the request information
     *
     * @return {@link ResponseEntity} with the suitable response and error message
     */
    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<String> malformedJwtException(MalformedJwtException exception, WebRequest request) {
        LOGGER.error(getErrorMessageUsingHttpRequest(request), exception);
        return buildPlainTextResponse("There was an error related with JWT token", HttpStatus.BAD_REQUEST);
    }


    /**
     * Method used to manage when a Rest request throws a {@link MethodArgumentNotValidException}
     *
     * @param exception
     *    {@link MethodArgumentNotValidException} thrown
     * @param request
     *    {@link WebRequest} with the request information
     *
     * @return {@link ResponseEntity} with the suitable response and error message
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> methodArgumentNotValidException(MethodArgumentNotValidException exception, WebRequest request) {
        LOGGER.error(getErrorMessageUsingHttpRequest(request), exception);
        return buildListOfValidationErrorsResponse("Error in the given parameters: ", exception,
                                                   HttpStatus.UNPROCESSABLE_ENTITY);
    }


    /**
     * Method used to manage when a Rest request throws a {@link NullPointerException}
     *
     * @param exception
     *    {@link NullPointerException} thrown
     * @param request
     *    {@link WebRequest} with the request information
     *
     * @return {@link ResponseEntity} with the suitable response and error message
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> nullPointerException(NullPointerException exception, WebRequest request) {
        LOGGER.error(getErrorMessageUsingHttpRequest(request), exception);
        return buildPlainTextResponse("Trying to access to a non existing property", HttpStatus.INTERNAL_SERVER_ERROR);
    }


    /**
     * Method used to manage when a Rest request throws a {@link UsernameNotFoundException}
     *
     * @param exception
     *    {@link UsernameNotFoundException} thrown
     * @param request
     *    {@link WebRequest} with the request information
     *
     * @return {@link ResponseEntity} with the suitable response and error message
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> usernameNotFoundException(UsernameNotFoundException exception, WebRequest request) {
        LOGGER.error(getErrorMessageUsingHttpRequest(request), exception);
        return buildPlainTextResponse("Invalid credentials given", HttpStatus.UNAUTHORIZED);
    }


    /**
     * Method used to manage when a Rest request throws a {@link Throwable}
     *
     * @param exception
     *    {@link Throwable} thrown
     * @param request
     *    {@link WebRequest} with the request information
     *
     * @return {@link ResponseEntity} with the suitable response and error message
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<String> throwable(Throwable exception, WebRequest request) {
        LOGGER.error(getErrorMessageUsingHttpRequest(request), exception);
        return buildPlainTextResponse("Internal error in the application", HttpStatus.INTERNAL_SERVER_ERROR);
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

        return String.format("There was an error trying to execute the request with:%s"
                        + "Http method = %s %s"
                        + "Uri = %s",
                System.lineSeparator(),
                httpRequest.getMethod(), System.lineSeparator(),
                httpRequest.getRequestURI());
    }


    /**
     *    Builds the Http response with the given information, including {@link HttpStatus} and a custom message
     * to include in the content.
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