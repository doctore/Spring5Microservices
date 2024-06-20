package com.security.jwt.controller;

import com.security.jwt.configuration.rest.RestRoutes;
import com.security.jwt.model.JwtClientDetails;
import com.security.jwt.service.cache.JwtClientDetailsCacheService;
import com.spring5microservices.common.dto.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Log4j2
@RestController
@RequestMapping(value = RestRoutes.CACHE.ROOT)
@Validated
public class CacheController extends BaseController {

    private final JwtClientDetailsCacheService jwtClientDetailsCacheService;


    @Autowired
    public CacheController(@Lazy final JwtClientDetailsCacheService jwtClientDetailsCacheService) {
        this.jwtClientDetailsCacheService = jwtClientDetailsCacheService;
    }


    /**
     * Clear the cache used to store {@link JwtClientDetails} information.
     *
     * @return if it was possible to clear the cache: {@link HttpStatus#OK},
     *         {@link HttpStatus#NOT_FOUND} otherwise.
     */
    @Operation(summary = "Clear the cache")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "The cache was cleared successfully"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "As part of the Basic Auth, the username does not exists or the given password does not belongs to this one.",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "The cache could not be cleared."
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
    @PutMapping(value = RestRoutes.CACHE.CLEAR)
    public Mono<ResponseEntity> clear() {
        log.info("Cleaning cache");
        return jwtClientDetailsCacheService.clear()
                ? Mono.just(
                        new ResponseEntity<>(OK)
                )
                : Mono.just(
                        new ResponseEntity<>(NOT_FOUND)
                );
    }

}
