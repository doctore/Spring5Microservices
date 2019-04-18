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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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
     * @return if any error happens, JWT token with {@link HttpStatus#OK}. The suitable Http error code otherwise.
     */
    @PostMapping(RestRoutes.AUTHENTICATION.LOGIN)
    public ResponseEntity<String> login(@RequestBody @Valid AuthenticationRequestDto requestDto) {
        return new ResponseEntity<>(authenticationService.generateJWTToken(requestDto), HttpStatus.OK);
    }

}
