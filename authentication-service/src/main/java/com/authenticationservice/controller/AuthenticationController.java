package com.authenticationservice.controller;

import com.authenticationservice.configuration.Constants;
import com.authenticationservice.configuration.rest.RestRoutes;
import com.authenticationservice.dto.AuthenticationRequestDto;
import com.authenticationservice.dto.UsernameAuthoritiesDto;
import com.authenticationservice.service.AuthenticationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Size;

/**
 * Rest services related with authorization functionality
 */
@RestController
@RequestMapping(value = RestRoutes.AUTHENTICATION.ROOT)
@CrossOrigin(origins="*")
@Validated
public class AuthenticationController {

    private AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(@Lazy AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }


    /**
     * Generate an suitable JWT token using the given user's login information
     *
     * @param requestDto
     *    {@link AuthenticationRequestDto}
     *
     * @return if there is no error, JWT token with {@link HttpStatus#OK},
     *         {@link HttpStatus#BAD_REQUEST} otherwise.
     */
    @ApiOperation(value = "Logs a user into the system",
                  notes = "Returns the token used to know if the user is authenticated (which includes his/her roles)",
                  response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful operation with the token in the response", response = String.class),
            @ApiResponse(code = 400, message = "Invalid username/password supplied taking into account included format validations"),
            @ApiResponse(code = 401, message = "User with given username/password does not exist"),
            @ApiResponse(code = 500, message = "There is a problem in the user account or any other internal server error")})
    @PostMapping(value = RestRoutes.AUTHENTICATION.LOGIN, produces = Constants.TEXT_PLAIN_UTF8_VALUE)
    public ResponseEntity<String> login(
            @ApiParam(value = "Username and password used to login the user", required = true)
            @RequestBody @Valid AuthenticationRequestDto requestDto) {
        return authenticationService.generateJwtToken(requestDto)
                                    .map(t -> new ResponseEntity<>(t, HttpStatus.OK))
                                    .orElse(new ResponseEntity(HttpStatus.BAD_REQUEST));
    }


    /**
     * Checks if the given token is valid or not taking into account the secret key and expiration date.
     *
     * @param token
     *    JWT token to validate (included Http authentication scheme)
     *
     * @return if there is no error, JWT token with {@link HttpStatus#OK}. The suitable Http error code otherwise.
     */
    @ApiOperation(value = "Checks the authenticity of the given token",
                  notes = "Returns true if the given token is valid and it is not expired",
                  response = Boolean.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful operation", response = Boolean.class),
            @ApiResponse(code = 400, message = "Given token does not verify included format validations"),
            @ApiResponse(code = 500, message = "Internal server error")})
    @GetMapping(RestRoutes.AUTHENTICATION.VALIDATE + "/{token}")
    public ResponseEntity<Boolean> validateToken(
            @ApiParam(value = "Token used to know if a user is authenticated", required = true)
            @PathVariable @Size(min=2) String token) {
        return new ResponseEntity<>(authenticationService.isJwtTokenValid(token), HttpStatus.OK);
    }


    /**
     * Using the given JWT token returns the {@link UsernameAuthoritiesDto} without {@code password} information.
     *
     * @param token
     *    JWT token to validate (included Http authentication scheme)
     *
     * @return if there is no error, {@link UsernameAuthoritiesDto} with {@link HttpStatus#OK},
     *         {@link HttpStatus#UNAUTHORIZED} otherwise.
     */
    @ApiOperation(value = "Gets the authentication data of the user included in the given token",
                  notes = "First validates the given token and then returns the username and roles of the given user",
                  response = UsernameAuthoritiesDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful operation with the authorization information in the response", response = UsernameAuthoritiesDto.class),
            @ApiResponse(code = 400, message = "Given token does not verify included format validations"),
            @ApiResponse(code = 401, message = "User included in the given token does not exist"),
            @ApiResponse(code = 500, message = "There is a problem in the user account or any other internal server error")})
    @GetMapping(RestRoutes.AUTHENTICATION.AUTHENTICATION_INFO + "/{token}")
    public ResponseEntity<UsernameAuthoritiesDto> getAuthenticationInformation(
            @ApiParam(value = "Token used to know if a user is authenticated", required = true)
            @PathVariable @Size(min=2) String token) {
        return authenticationService.getAuthenticationInformation(token)
                                    .map(authToken -> new ResponseEntity<>(authToken, HttpStatus.OK))
                                    .orElse(new ResponseEntity(HttpStatus.UNAUTHORIZED));
    }

}
