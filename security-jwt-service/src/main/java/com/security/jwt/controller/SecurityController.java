package com.security.jwt.controller;

import com.security.jwt.configuration.rest.RestRoutes;
import com.security.jwt.dto.AuthenticationRequestDto;
import com.security.jwt.service.SecurityService;
import com.spring5microservices.common.dto.AuthenticationInformationDto;
import com.spring5microservices.common.dto.UsernameAuthoritiesDto;
import com.spring5microservices.common.exception.UnauthorizedException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import static java.util.Optional.ofNullable;

@AllArgsConstructor
@RestController
@RequestMapping(value = RestRoutes.SECURITY.ROOT)
@Validated
public class SecurityController {

    @Lazy
    private final SecurityService securityService;


    /**
     *    Generate the suitable {@link AuthenticationInformationDto} using the given user's login information and
     * Basic Auth data to extract the application is trying to login the provided user.
     *
     * @param authenticationRequestDto
     *    {@link AuthenticationRequestDto}
     *
     * @return if there is no error, the {@link AuthenticationInformationDto} with {@link HttpStatus#OK},
     *         {@link HttpStatus#BAD_REQUEST} otherwise.
     */
    @ApiOperation(value = "Login a user into a given application",
            notes = "Returns the authentication information used to know if the user is authenticated (which includes his/her roles)",
            response = AuthenticationInformationDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful operation with the authentication information in the response", response = AuthenticationInformationDto.class),
            @ApiResponse(code = 400, message = "Invalid username or password supplied in the body taking into account included format validations"),
            @ApiResponse(code = 401, message = "In the body, the user is not active or the given password does not belongs to the username."
                    + "As part of the Basic Auth, the username does not exists or the given password does not belongs to this one."),
            @ApiResponse(code = 404, message = "The given username provided in the body does not exist."),
            @ApiResponse(code = 422, message = "The generated response is empty"),
            @ApiResponse(code = 500, message = "Any other internal server error")})
    @PostMapping(value = RestRoutes.SECURITY.LOGIN)
    public ResponseEntity<AuthenticationInformationDto> login(
            @ApiParam(value = "Username and password used to login the user", required = true)
            @RequestBody @Valid AuthenticationRequestDto authenticationRequestDto) {
        return securityService.login(getPrincipal().getUsername(), authenticationRequestDto.getUsername(), authenticationRequestDto.getPassword())
                .map(ai -> new ResponseEntity<>(ai, HttpStatus.OK))
                .orElse(new ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY));
    }


    /**
     *    Generate the suitable {@link AuthenticationInformationDto} using the given {@code refresh token} and
     * Basic Auth data to extract the application is trying to refresh the provided token.
     *
     * @param refreshToken
     *    Refresh token used to regenerate the authentication information
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
            @ApiResponse(code = 401, message = "In the body, the user is not active, refresh token is not valid or not belongs to given username in the Basic Auth."
                    + "As part of the Basic Auth, the username does not exists or the given password does not belongs to this one."),
            @ApiResponse(code = 404, message = "The given username provided in the body does not exist."),
            @ApiResponse(code = 440, message = "Refresh token has expired"),
            @ApiResponse(code = 500, message = "Any other internal server error")})
    @PostMapping(value = RestRoutes.SECURITY.REFRESH)
    public ResponseEntity<AuthenticationInformationDto> refresh(
            @ApiParam(value = "Refresh token used to generate a new authentication information", required = true)
            @RequestBody @Size(min = 1) String refreshToken) {
        return securityService.refresh(refreshToken, getPrincipal().getUsername())
                .map(ai -> new ResponseEntity<>(ai, HttpStatus.OK))
                .orElse(new ResponseEntity(HttpStatus.UNAUTHORIZED));
    }


    /**
     *    Using the given JWT {@code access token} and Basic Auth data to extract the application is trying to access,
     * returns the {@link UsernameAuthoritiesDto} without {@code password} information.
     *
     * @param accessToken
     *    Access token used to extract the authorization information
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
            @ApiResponse(code = 401, message = "In the body, the user is not active, access token is not valid or not belongs to given username in the Basic Auth."
                    + "As part of the Basic Auth, the username does not exists or the given password does not belongs to this one."),
            @ApiResponse(code = 404, message = "The given username provided in the body does not exist."),
            @ApiResponse(code = 440, message = "Access token has expired"),
            @ApiResponse(code = 500, message = "Any other internal server error")})
    @PostMapping(RestRoutes.SECURITY.AUTHORIZATION_INFO)
    public ResponseEntity<UsernameAuthoritiesDto> authorizationInformation(
            @ApiParam(value = "Access token used to get the authorization information", required = true)
            @RequestBody @Size(min = 1) String accessToken) {
        return new ResponseEntity<>(securityService.getAuthorizationInformation(accessToken, getPrincipal().getUsername()), HttpStatus.OK);
    }


    /**
     * Get the authenticated {@link UserDetails} to know the application is trying to use the provided web services.
     *
     * @return {@link UserDetails}
     *
     * @throws UnauthorizedException if the given {@code clientId} does not exists in database
     */
    private UserDetails getPrincipal() {
        return (UserDetails) ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .orElseThrow(() -> new UnauthorizedException("There is no an authenticated used"));
    }

}