package com.spring5microservices.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.util.Optional.ofNullable;

/**
 * Common service used by other microservices to deal with a cache.
 */
@Service
public class CacheService {

    private final CacheManager cacheManager;

    @Autowired
    public CacheService(@Lazy CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }


    /**
     * Remove all the elements included in the given {@code cacheName}
     *
     * @param cacheName
     *    Cache to clean
     *
     * @return {@code true} if the {@code cacheName} exists and its elements were removed, {@code false} otherwise
     */
    public boolean clear(final String cacheName) {
        return ofNullable(cacheName)
                .map(cacheManager::getCache)
                .map(c -> {
                    c.clear();
                    return true;
                })
                .orElse(false);
    }


    /**
     * Check if exists the given {@code key} inside the cache.
     *
     * @param cacheName
     *    Cache on which the {@code key} will be searched
     * @param key
     *    Identifier to search in the cache
     *
     * @return {@code true} if the {@code key} exists, {@code false} otherwise
     */
    public <K> boolean contains(final String cacheName,
                                final K key) {
        return ofNullable(cacheName)
                .map(cacheManager::getCache)
                .map(c -> c.get(key))
                .isPresent();
    }


    /**
     * Return the {@code value} related with the given {@code key} inside the cache.
     *
     * @param cacheName
     *    Cache on which the {@code key} will be searched
     * @param key
     *    Identifier to search in the cache
     *
     * @return {@link Optional} with the {@code value} if it was found, {@link Optional#empty()} otherwise
     */
    @SuppressWarnings("unchecked")
    public <K, V> Optional<V> get(final String cacheName,
                                  final K key) {
        return ofNullable(cacheName)
                .map(cacheManager::getCache)
                .map(c -> c.get(key))
                .map(v -> (V)v.get());
    }


    /**
     * Include a pair of {@code key} - {@code value} inside the cache.
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
    public <K, V> boolean put(final String cacheName,
                              final K key,
                              final V value) {
        return ofNullable(cacheName)
                .map(cacheManager::getCache)
                .map(c -> {
                    c.put(key, value);
                    return true;
                })
                .orElse(false);
    }


    /**
     * Include a pair of {@code key} - {@code value} inside the cache, ONLY if the provided key does not exist.
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
    public <K, V> boolean putIfAbsent(final String cacheName,
                                      final K key,
                                      final V value) {
        return contains(cacheName, key)
                ? false
                : put(cacheName, key, value);
    }


    /**
     * Include a pair of {@code key} - {@code value} inside the cache, ONLY if the provided key exists.
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
    public <K, V> boolean putIfPresent(final String cacheName,
                                       final K key,
                                       final V value) {
        return contains(cacheName, key)
                ? put(cacheName, key, value)
                : false;
    }


    /**
     * Remove the given {@code key} of the cache.
     *
     * @param cacheName
     *    Cache on which the information will be removed
     * @param key
     *    Identifier of the {@code value} we want to remove
     *
     * @return {@code true} if no problem was found during the operation, {@code false} otherwise
     */
    public <K> boolean remove(final String cacheName,
                              final K key) {
        return ofNullable(cacheName)
                .map(cacheManager::getCache)
                .map(c -> {
                    c.evict(key);
                    return true;
                })
                .orElse(false);
    }

}
