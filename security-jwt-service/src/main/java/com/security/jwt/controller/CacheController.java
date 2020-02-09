package com.security.jwt.controller;

import com.security.jwt.configuration.rest.RestRoutes;
import com.security.jwt.model.JwtClientDetails;
import com.security.jwt.service.cache.JwtClientDetailsCacheService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = RestRoutes.CACHE.ROOT)
@CrossOrigin(origins="*")
@Validated
public class CacheController {

    private JwtClientDetailsCacheService jwtClientDetailsCacheService;

    @Autowired
    public CacheController(@Lazy JwtClientDetailsCacheService jwtClientDetailsCacheService) {
        this.jwtClientDetailsCacheService = jwtClientDetailsCacheService;
    }


    /**
     * Clear the cache used to store {@link JwtClientDetails} information.
     *
     * @return if it was possible to clear the cache: {@link HttpStatus#OK},
     *         {@link HttpStatus#NOT_FOUND} otherwise.
     */
    @ApiOperation(value = "Clear the cache")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The cache was cleared successfully"),
            @ApiResponse(code = 401, message = "As part of the Basic Auth, the username does not exists or the given password does not belongs to this one."),
            @ApiResponse(code = 404, message = "The cache could not be cleared."),
            @ApiResponse(code = 500, message = "Any other internal server error")})
    @PutMapping(value = RestRoutes.CACHE.CLEAR)
    public ResponseEntity clear() {
        return jwtClientDetailsCacheService.clear() ? new ResponseEntity(HttpStatus.OK)
                : new ResponseEntity(HttpStatus.NOT_FOUND);
    }

}
