package com.security.jwt.controller;

import com.security.jwt.configuration.rest.RestRoutes;
import com.security.jwt.dto.AuthenticationRequestDto;
import com.security.jwt.service.SecurityService;
import com.spring5microservices.common.dto.AuthenticationInformationDto;
import com.spring5microservices.common.dto.ErrorResponseDto;
import com.spring5microservices.common.dto.UsernameAuthoritiesDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@AllArgsConstructor
@Log4j2
@RestController
@RequestMapping(value = RestRoutes.SECURITY.ROOT)
@Validated
public class SecurityController extends BaseController {

    @Lazy
    private final SecurityService service;


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
    @Operation(
            summary = "Login a user into a given application",
            description = "Returns the authentication information used to know if the user is authenticated (which includes his/her roles)"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful operation with the authentication information in the response",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = AuthenticationInformationDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid username or password supplied in the body taking into account included format validations",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "In the body, the user is not active or the given password does not belongs to the username. As part "
                                        + "of the Basic Auth, the username does not exists or the given password does not belongs to this one.",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "The generated response is empty"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Any other internal server error",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    )
            }
    )
    @PostMapping(value = RestRoutes.SECURITY.LOGIN)
    public Mono<ResponseEntity<AuthenticationInformationDto>> login(@RequestBody @Valid final AuthenticationRequestDto authenticationRequestDto) {
        log.info(
                format("Requesting login with: %s",
                        authenticationRequestDto)
        );
        return getPrincipal()
                .map(userDetails ->
                        service.login(
                                userDetails.getUsername(),
                                authenticationRequestDto.getUsername(),
                                authenticationRequestDto.getPassword()
                        )
                        .map(ai ->
                                new ResponseEntity<>(
                                        ai,
                                        OK
                                )
                        )
                        .orElseGet(() ->
                                new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY)
                        )
                );
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
    @Operation(
            summary = "Refresh the authentication information of a user into a given application",
            description = "Returns the authentication information used to know if the user is authenticated (which includes his/her roles)"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful operation with the authentication information in the response",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = AuthenticationInformationDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Given token does not verify included format validations",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "In the body, the user is not active, refresh token is not valid or not belongs "
                                        + "to given username in the Basic Auth. As part of the Basic Auth, the username "
                                        + "does not exists or the given password does not belongs to this one.",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "440",
                            description = "Authorization information (provided token) has expired"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Any other internal server error",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    )
            }
    )
    @PostMapping(value = RestRoutes.SECURITY.REFRESH)
    public Mono<ResponseEntity<AuthenticationInformationDto>> refresh(@RequestBody @Size(min = 1) final String refreshToken) {
        log.info(
                format("Requesting refresh action using the provided token: %s",
                        refreshToken)
        );
        return getPrincipal()
                .map(userDetails ->
                        service.refresh(
                                refreshToken,
                                userDetails.getUsername()
                        )
                        .map(ai ->
                                new ResponseEntity<>(
                                        ai,
                                        OK
                                )
                        )
                        .orElseGet(() ->
                                new ResponseEntity<>(HttpStatus.UNAUTHORIZED)
                        )
                );
    }


    @Operation(
            summary = "Get the authorization data of the user included in the given access token",
            description = "First validates the given token and then returns his/her: username, roles and additional information"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful operation with the authorization information in the response",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UsernameAuthoritiesDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Given token does not verify included format validations",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "In the body, the user is not active, access token is not valid or not belongs "
                                        + "to given username in the Basic Auth. As part of the Basic Auth, the username "
                                        + "does not exists or the given password does not belongs to this one.",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "440",
                            description = "Authorization information (provided token) has expired"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Any other internal server error",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    )
            }
    )
    @PostMapping(RestRoutes.SECURITY.AUTHORIZATION_INFO)
    public Mono<ResponseEntity<UsernameAuthoritiesDto>> authorizationInformation(@RequestBody @Size(min = 1) final String accessToken) {
        log.info(
                format("Extracting authorization information of the token: %s",
                        accessToken)
        );
        return getPrincipal()
                .map(userDetails ->
                        new ResponseEntity<>(
                                service.getAuthorizationInformation(
                                        accessToken,
                                        userDetails.getUsername()
                                ),
                                OK
                        )
                );
    }

}
