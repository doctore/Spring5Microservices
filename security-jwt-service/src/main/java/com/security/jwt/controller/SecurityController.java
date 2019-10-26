package com.security.jwt.controller;

import com.security.jwt.configuration.rest.RestRoutes;
import com.security.jwt.dto.AuthenticationRequestDto;
import com.security.jwt.model.JwtClientDetails;
import com.security.jwt.service.SecurityService;
import com.spring5microservices.common.dto.AuthenticationInformationDto;
import com.spring5microservices.common.dto.UsernameAuthoritiesDto;
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
     * @param clientId
     *    {@link JwtClientDetails} identifier used to generate the required response
     *
     * @return if there is no error, the {@link AuthenticationInformationDto} with {@link HttpStatus#OK},
     *         {@link HttpStatus#BAD_REQUEST} otherwise.
     */
    @ApiOperation(value = "Login a user into a given application",
            notes = "Returns the authentication information used to know if the user is authenticated (which includes his/her roles)",
            response = AuthenticationInformationDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful operation with the authentication information in the response", response = AuthenticationInformationDto.class),
            @ApiResponse(code = 400, message = "Invalid username, password or clientId supplied taking into account included format validations"),
            @ApiResponse(code = 401, message = "The user is not active or the given password does not belongs to the username"),
            @ApiResponse(code = 404, message = "The given username or clientId does not exist"),
            @ApiResponse(code = 500, message = "Any other internal server error")})
    @PostMapping(value = RestRoutes.SECURITY.LOGIN + "/{clientId}")
    public ResponseEntity<AuthenticationInformationDto> login(
              @ApiParam(value = "Username and password used to login the user", required = true)
              @RequestBody @Valid AuthenticationRequestDto authenticationRequestDto,
              @ApiParam(value = "Client identifier used to know what is the application the user belongs", required = true)
              @PathVariable @Size(min = 1, max = 64) String clientId) {

        return securityService.login(clientId, authenticationRequestDto.getUsername(), authenticationRequestDto.getPassword())
                .map(ai -> new ResponseEntity<>(ai, HttpStatus.OK))
                .orElse(new ResponseEntity(HttpStatus.BAD_REQUEST));
    }


    /**
     * Generate the suitable {@link AuthenticationInformationDto} using the given {@code refresh token}.
     *
     * @param refreshToken
     *    Refresh token used to regenerate the authentication information
     * @param clientId
     *    {@link JwtClientDetails} identifier used to generate the required response
     *
     * @return if there is no error, the {@link AuthenticationInformationDto} with {@link HttpStatus#OK},
     *         {@link HttpStatus#BAD_REQUEST} otherwise.
     */
    @ApiOperation(value = "Refresh the authentication information of a user into a given application",
            notes = "Returns the authentication information used to know if the user is authenticated (which includes his/her roles)",
            response = AuthenticationInformationDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful operation with the authentication information in the response", response = AuthenticationInformationDto.class),
            @ApiResponse(code = 400, message = "Given token or clientId does not verify included format validations"),
            @ApiResponse(code = 401, message = "The user is not active, refresh token is not valid or not belongs to given clientId"),
            @ApiResponse(code = 404, message = "The username included in the token or clientId does not exist"),
            @ApiResponse(code = 440, message = "Refresh token has expired"),
            @ApiResponse(code = 500, message = "Any other internal server error")})
    @PostMapping(value = RestRoutes.SECURITY.REFRESH_TOKEN + "/{clientId}")
    public ResponseEntity<AuthenticationInformationDto> refreshToken(
              @ApiParam(value = "Refresh token used to generate a new authentication information", required = true)
              @RequestBody @Size(min = 1) String refreshToken,
              @ApiParam(value = "Client identifier used to know what is the application the user belongs", required = true)
              @PathVariable @Size(min = 1, max = 64) String clientId) {

        return securityService.refreshToken(refreshToken, clientId)
                .map(ai -> new ResponseEntity<>(ai, HttpStatus.OK))
                .orElse(new ResponseEntity(HttpStatus.UNAUTHORIZED));
    }


    /**
     * Using the given JWT token returns the {@link UsernameAuthoritiesDto} without {@code password} information.
     *
     * @param accessToken
     *    Access token used to extract the authorization information
     * @param clientId
     *    {@link JwtClientDetails} identifier used to generate the required response
     *
     * @return if there is no error, {@link UsernameAuthoritiesDto} with {@link HttpStatus#OK},
     *         {@link HttpStatus#UNAUTHORIZED} otherwise.
     */
    @ApiOperation(value = "Get the authorization data of the user included in the given access token",
            notes = "First validates the given token and then returns his/her: username, roles and additional information",
            response = UsernameAuthoritiesDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful operation with the authorization information in the response", response = UsernameAuthoritiesDto.class),
            @ApiResponse(code = 400, message = "Given token or clientId does not verify included format validations"),
            @ApiResponse(code = 401, message = "The user is not active, access token is not valid or not belongs to given clientId"),
            @ApiResponse(code = 404, message = "The username included in the token or clientId does not exist"),
            @ApiResponse(code = 440, message = "Access token has expired"),
            @ApiResponse(code = 500, message = "Any other internal server error")})
    @GetMapping(RestRoutes.SECURITY.AUTHORIZATION_INFO + "/{clientId}" + "/{accessToken}")
    public ResponseEntity<UsernameAuthoritiesDto> authorizationInformation(
            @ApiParam(value = "Access token used to get the authorization information", required = true)
            @PathVariable @Size(min = 1) String accessToken,
            @ApiParam(value = "Client identifier used to know what is the application the user belongs", required = true)
            @PathVariable @Size(min = 1, max = 64) String clientId) {

        return new ResponseEntity<>(securityService.getAuthorizationInformation(accessToken, clientId), HttpStatus.OK);
    }

}
