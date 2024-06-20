package com.pizza.controller;

import com.pizza.annotation.RoleAdmin;
import com.pizza.configuration.rest.RestRoutes;
import com.pizza.service.cache.UserBlacklistCacheService;
import com.spring5microservices.common.dto.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.constraints.Size;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

/**
 * Rest services to work with users
 */
@Log4j2
@RestController
@RequestMapping(RestRoutes.USER.ROOT)
@Validated
@Tag(name = "UserController", description = "Endpoints to manage operations related blacklist of users")
public class UserController {

    private final UserBlacklistCacheService userBlackListCacheService;


    @Autowired
    public UserController(@Lazy final UserBlacklistCacheService userBlackListCacheService) {
        this.userBlackListCacheService = userBlackListCacheService;
    }


    /**
     * Include the given {@code username} into the blacklist.
     *
     * @param username
     *    {@code username} to include in the blacklist
     *
     * @return if username is not {@code Null}: {@link HttpStatus#OK} and added {@code username}
     *         if username is {@code Null}: {@link HttpStatus#UNPROCESSABLE_ENTITY} and {@code Null}
     */
    @Operation(
            summary = "Add a user to the blacklist",
            description = "Add a user to the blacklist (only allowed to user with role admin)"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "The given username was successfully added",
                            content = @Content(
                                    mediaType = TEXT_PLAIN_VALUE,
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "There was a problem in the given request, the given parameters have not passed the required validations",
                            content = @Content(
                                    mediaType = TEXT_PLAIN_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "The user has not authorization to execute this request or provided authorization has expired",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "The user could not be included in the blacklist"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "There was an internal problem in the server",
                            content = @Content(
                                    mediaType = TEXT_PLAIN_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    )
            }
    )
    @PostMapping(RestRoutes.USER.BLACKLIST + "/{username}")
    @RoleAdmin
    public Mono<ResponseEntity<String>> addToBlacklist(@PathVariable @Size(min = 1) final String username) {
        log.info(
                format("Adding to the blacklist the username: %s",
                        username)
        );
        return userBlackListCacheService.put(username)
                ? Mono.just(
                        new ResponseEntity<>(
                                username,
                                OK
                        )
                )
                : Mono.just(new ResponseEntity<>(UNPROCESSABLE_ENTITY));
    }


    /**
     * Remove the given {@code username} from the blacklist.
     *
     * @param username
     *    {@code username} to remove from the blacklist
     *
     * @return if username is not {@code Null}: {@link HttpStatus#OK} and removed {@code username}
     *         if username is {@code Null}: {@link HttpStatus#NOT_FOUND} and {@code Null}
     */
    @Operation(
            summary = "Remove a user to the blacklist",
            description = "Remove a user to the blacklist (only allowed to user with role admin)"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "The given username was successfully removed",
                            content = @Content(
                                    mediaType = TEXT_PLAIN_VALUE,
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "There was a problem in the given request, the given parameters have not passed the required validations",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "The user has not authorization to execute this request or provided authorization has expired",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "The provided username does not exists in the blacklist"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "There was an internal problem in the server",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    )
            }
    )
    @DeleteMapping(RestRoutes.USER.BLACKLIST + "/{username}")
    @RoleAdmin
    public Mono<ResponseEntity<String>> removeFromBlacklist(@PathVariable @Size(min = 1) final String username) {
        log.info(
                format("Removing from the blacklist the username: %s",
                        username)
        );
        return userBlackListCacheService.remove(username)
                ? Mono.just(
                        new ResponseEntity<>(
                                username,
                                OK
                        )
                )
                : Mono.just(new ResponseEntity<>(NOT_FOUND));
    }

}
