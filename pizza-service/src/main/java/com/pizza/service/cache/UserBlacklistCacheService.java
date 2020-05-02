package com.pizza.service.cache;

import com.pizza.configuration.cache.CacheConfiguration;
import com.spring5microservices.common.service.CacheService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import static java.util.Optional.ofNullable;

@AllArgsConstructor
@Service
public class UserBlacklistCacheService {

    private static final boolean DEFAULT_VALUE = true;

    @Lazy
    private final CacheConfiguration cacheConfiguration;

    @Lazy
    private final CacheService cacheService;


    /**
     * Check if exists the given {@code username} inside the related cache.
     *
     * @param username
     *    {@code username} to search
     *
     * @return {@code true} if the {@code username} exists, {@code false} otherwise
     */
    public boolean contains(String username) {
        return ofNullable(username)
                .map(id -> cacheService.contains(cacheConfiguration.getUserBlacklistCacheName(), username))
                .orElse(false);
    }


    /**
     * Include a {@code username} inside the related cache.
     *
     * @param username
     *    {@code username} to store
     *
     * @return {@code true} if the data was stored, {@code false} otherwise
     */
    public boolean put(String username) {
        return cacheService.put(cacheConfiguration.getUserBlacklistCacheName(), username, DEFAULT_VALUE);
    }


    /**
     * Remove the given {@code username} of the related cache.
     *
     * @param username
     *    {@code username} to remove
     *
     * @return {@code true} if the data was removed, {@code false} otherwise
     */
    public boolean remove(String username) {
        return cacheService.remove(cacheConfiguration.getUserBlacklistCacheName(), username);
    }

}
