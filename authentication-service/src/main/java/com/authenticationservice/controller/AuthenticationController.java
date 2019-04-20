package com.authenticationservice.controller;

import com.authenticationservice.configuration.rest.RestRoutes;
import com.authenticationservice.dto.AuthenticationRequestDto;
import com.authenticationservice.service.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Rest services related with authorization functionality
 */
@RestController
@RequestMapping(RestRoutes.AUTHENTICATION.ROOT)
@CrossOrigin(origins="*")
@Validated
public class AuthenticationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

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
    @PostMapping(RestRoutes.AUTHENTICATION.LOGIN)
    public ResponseEntity<String> login(@RequestBody @Valid AuthenticationRequestDto requestDto) {
        return authenticationService.generateJwtToken(requestDto)
                                    .map(t -> new ResponseEntity<>(t, HttpStatus.OK))
                                    .orElse(new ResponseEntity(HttpStatus.BAD_REQUEST));
    }


    /**
     * Checks if the given token is valid or not taking into account the secret key and expiration date.
     *
     * @param token
     *    JWT token to validate
     *
     * @return if there is no error, JWT token with {@link HttpStatus#OK}. The suitable Http error code otherwise.
     */
    @GetMapping(RestRoutes.AUTHENTICATION.VALIDATE + "/{token}")
    public ResponseEntity<Boolean> validateToken(@PathVariable @NotNull @Size(min=1) String token) {
        return new ResponseEntity<>(authenticationService.isJwtTokenValid(token), HttpStatus.OK);
    }

}
