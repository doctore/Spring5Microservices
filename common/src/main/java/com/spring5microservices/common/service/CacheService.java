package com.spring5microservices.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CacheService {

    private CacheManager cacheManager;

    @Autowired
    public CacheService(@Lazy CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }


    public <K, V> boolean put(String cacheName, K key, V value) {
        return Optional.ofNullable(cacheName)
                       .map(cacheManager::getCache)
                       .map(c -> {
                           c.put(key, value);
                           return true;
                       })
                       .orElse(false) ;
    }


    public <K, V> Optional<V> get(String cacheName, K key) {
        return Optional.ofNullable(cacheName)
                       .map(cacheManager::getCache)
                       .map(c -> c.get(key))
                       .map(v -> (V)v.get());
    }


    public <K> boolean contains(String cacheName, K key) {
        return Optional.ofNullable(cacheName)
                       .map(cacheManager::getCache)
                       .map(c -> c.get(key))
                       .isPresent();
    }

}
