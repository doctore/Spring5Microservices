package com.security.jwt.controller;

import com.security.jwt.configuration.rest.RestRoutes;
import com.security.jwt.dto.AuthenticationRequestDto;
import com.security.jwt.service.SecurityService;
import com.spring5microservices.common.dto.AuthenticationInformationDto;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Size;

@RestController
@RequestMapping(value = RestRoutes.SECURITY.ROOT)
@CrossOrigin(origins="*")
@Validated
public class SecurityController {

    private SecurityService securityService;

    @Autowired
    public SecurityController(@Lazy SecurityService securityService) {
        this.securityService = securityService;
    }

    /**
     * Generate the suitable {@link AuthenticationInformationDto} using the given user's login information.
     *
     * @param authenticationRequestDto
     *    {@link AuthenticationRequestDto}
     *
     * @return if there is no error, the {@link AuthenticationInformationDto} with {@link HttpStatus#OK},
     *         {@link HttpStatus#BAD_REQUEST} otherwise.
     */
    @ApiOperation(value = "Logs a user into the system",
            notes = "Returns the authentication information used to know if the user is authenticated (which includes his/her roles)",
            response = AuthenticationInformationDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful operation with the authentication information in the response", response = AuthenticationInformationDto.class),
            @ApiResponse(code = 400, message = "Invalid username/password/clientId supplied taking into account included format validations"),
            @ApiResponse(code = 401, message = "User with given username/password or clientId does not exist"),
            @ApiResponse(code = 500, message = "There is a problem in the user account or any other internal server error")})
    @PostMapping(value = RestRoutes.SECURITY.LOGIN + "/{clientId}")
    public ResponseEntity<AuthenticationInformationDto> login(
            @ApiParam(value = "Username and password used to login the user", required = true)
            @RequestBody @Valid AuthenticationRequestDto authenticationRequestDto,
            @ApiParam(value = "Client identifier used to know what is the application the user belongs", required = true)
            @PathVariable @Size(min = 1, max = 64) String clientId) {

        return securityService.login(authenticationRequestDto, clientId)
                .map(t -> new ResponseEntity<>(t, HttpStatus.OK))
                .orElse(new ResponseEntity(HttpStatus.BAD_REQUEST));
    }

}
