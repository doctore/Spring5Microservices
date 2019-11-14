package com.pizza.controller;

import com.pizza.annotation.RoleAdmin;
import com.pizza.configuration.rest.RestRoutes;
import com.pizza.service.cache.UserBlacklistCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.constraints.Size;

/**
 * Rest services to work with users
 */
@RestController
@RequestMapping(RestRoutes.USER.ROOT)
@CrossOrigin(origins="*")
@Validated
public class UserController {

    private UserBlacklistCacheService userBlackListCacheService;

    @Autowired
    public UserController(UserBlacklistCacheService userBlackListCacheService) {
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
    @PostMapping(RestRoutes.USER.BLACKLIST + "/{username}")
    @RoleAdmin
    public Mono<ResponseEntity<String>> addToBlacklist(@PathVariable @Size(min = 1) String username) {
        if (userBlackListCacheService.put(username))
            return Mono.just(new ResponseEntity(username, HttpStatus.OK));
        else
            return Mono.just(new ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY));
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
    @DeleteMapping(RestRoutes.USER.BLACKLIST + "/{username}")
    @RoleAdmin
    public Mono<ResponseEntity<String>> removeFromBlacklist(@PathVariable @Size(min = 1) String username) {
        if (userBlackListCacheService.remove(username))
            return Mono.just(new ResponseEntity(username, HttpStatus.OK));
        else
            return Mono.just(new ResponseEntity(HttpStatus.NOT_FOUND));
    }

}
