package com.spring5microservices.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Common service used by other microservices to deal with a cache.
 */
@Service
public class CacheService {

    private CacheManager cacheManager;

    @Autowired
    public CacheService(@Lazy CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }


    /**
     * Include a pair of {@code key} - {@code value} in the given cache name.
     *
     * @param cacheName
     *    Cache on which the information will be included
     * @param key
     *    Identifier of the {@code value} we want to store
     * @param value
     *    Information to store
     *
     * @return {@code true} if the data was stored, {@code false} otherwise
     */
    public <K, V> boolean put(String cacheName, K key, V value) {
        return Optional.ofNullable(cacheName)
                       .map(cacheManager::getCache)
                       .map(c -> {
                           c.put(key, value);
                           return true;
                       })
                       .orElse(false) ;
    }


    /**
     * Return the {@code value} related with the given {@code key} inside the given cache.
     *
     * @param cacheName
     *    Cache on which the {@code key} will be searched
     * @param key
     *    Identifier to search in the cache
     *
     * @return {@link Optional} with the {@code value} if it was found, {@link Optional#empty()} otherwise
     */
    public <K, V> Optional<V> get(String cacheName, K key) {
        return Optional.ofNullable(cacheName)
                       .map(cacheManager::getCache)
                       .map(c -> c.get(key))
                       .map(v -> (V)v.get());
    }


    /**
     * Checks if the given {@code key} inside the given cache.
     *
     *    Cache on which the {@code key} will be searched
     * @param key
     *    Identifier to search in the cache
     *
     * @return {@code true} if the {@code key} exits, {@code false} otherwise
     */
    public <K> boolean contains(String cacheName, K key) {
        return Optional.ofNullable(cacheName)
                       .map(cacheManager::getCache)
                       .map(c -> c.get(key))
                       .isPresent();
    }

}
