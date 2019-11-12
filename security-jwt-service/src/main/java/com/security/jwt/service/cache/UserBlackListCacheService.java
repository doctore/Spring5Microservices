package com.security.jwt.service.cache;

import com.security.jwt.configuration.cache.CacheConfiguration;
import com.security.jwt.model.JwtClientDetails;
import com.spring5microservices.common.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import static java.util.Optional.ofNullable;

@Service
public class UserBlackListCacheService {

    private static final String SEPARATOR = "/";
    private static final boolean DEFAULT_VALUE = true;

    private CacheConfiguration cacheConfiguration;
    private CacheService cacheService;

    @Autowired
    public UserBlackListCacheService(@Lazy CacheConfiguration cacheConfiguration, @Lazy CacheService cacheService) {
        this.cacheConfiguration = cacheConfiguration;
        this.cacheService = cacheService;
    }


    /**
     * Check if exists the given {@link JwtClientDetails#getClientId()} and {@code username} inside the related cache.
     *
     * @param clientId
     *    {@link JwtClientDetails#getClientId()} to search
     * @param username
     *    {@code username} to search
     *
     * @return {@code true} if the {@code clientId} and {@code username} exists, {@code false} otherwise
     */
    public boolean contains(String clientId, String username) {
        return ofNullable(clientId)
                .map(id -> cacheService.contains(cacheConfiguration.getUserBlackListCacheName(), buildKey(clientId, username)))
                .orElse(false);
    }


    /**
     * Include a pair of {@link JwtClientDetails#getClientId()} - {@code username} inside the related cache.
     *
     * @param clientId
     *    {@link JwtClientDetails#getClientId()} to store
     * @param username
     *    {@code username} to store
     *
     * @return {@code true} if the data was stored, {@code false} otherwise
     */
    public boolean put(String clientId, String username) {
        return cacheService.put(cacheConfiguration.getUserBlackListCacheName(), buildKey(clientId, username), DEFAULT_VALUE);
    }


    private String buildKey(String clientId, String username) {
        return clientId + SEPARATOR + username;
    }

}
