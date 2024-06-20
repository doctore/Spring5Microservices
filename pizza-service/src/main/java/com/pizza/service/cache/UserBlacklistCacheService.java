package com.pizza.service.cache;

import com.pizza.configuration.cache.CacheConfiguration;
import com.spring5microservices.common.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import static java.util.Optional.ofNullable;

@Service
public class UserBlacklistCacheService {

    private static final boolean DEFAULT_BOOLEAN_VALUE = true;

    private final CacheConfiguration cacheConfiguration;

    private final CacheService cacheService;


    @Autowired
    public UserBlacklistCacheService(@Lazy final CacheConfiguration cacheConfiguration,
                                     @Lazy final CacheService cacheService) {
        this.cacheConfiguration = cacheConfiguration;
        this.cacheService = cacheService;
    }


    /**
     * Check if exists the given {@code username} inside the related cache.
     *
     * @param username
     *    {@code username} to search
     *
     * @return {@code true} if the {@code username} exists, {@code false} otherwise
     */
    public boolean contains(final String username) {
        return ofNullable(username)
                .map(id ->
                        cacheService.contains(
                                cacheConfiguration.getUserBlacklistCacheName(),
                                username
                        )
                )
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
    public boolean put(final String username) {
        return cacheService.put(
                cacheConfiguration.getUserBlacklistCacheName(),
                username,
                DEFAULT_BOOLEAN_VALUE
        );
    }


    /**
     * Remove the given {@code username} of the related cache.
     *
     * @param username
     *    {@code username} to remove
     *
     * @return {@code true} if the data was removed, {@code false} otherwise
     */
    public boolean remove(final String username) {
        return cacheService.remove(
                cacheConfiguration.getUserBlacklistCacheName(),
                username
        );
    }

}
