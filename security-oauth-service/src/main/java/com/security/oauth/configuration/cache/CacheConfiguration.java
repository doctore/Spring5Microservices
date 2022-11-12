package com.security.oauth.configuration.cache;

import com.security.oauth.configuration.Constants;
import lombok.Getter;
import org.cache2k.extra.spring.SpringCache2kCacheManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@ComponentScan(basePackages = {Constants.EXTERNAL_PATH.COMMON})
@EnableCaching
public class CacheConfiguration {

    @Value("${cache.oauthClient.entryCapacity}")
    private long oauthClientCacheEntryCapacity;

    @Value("${cache.oauthClient.expireInMinutes}")
    private long oauthClientCacheExpireInMinutes;

    @Value("${cache.oauthClient.name}")
    @Getter
    private String oauthClientCacheName;

    /**
     * Centralized cache configuration to manage the information we want to cache
     *
     * @return {@link CacheManager}
     */
    @Bean
    public CacheManager cacheManager() {
        return new SpringCache2kCacheManager()
                .addCaches(
                        c ->
                                c.name(oauthClientCacheName)
                                        .entryCapacity(oauthClientCacheEntryCapacity)
                                        .expireAfterWrite(
                                                oauthClientCacheExpireInMinutes,
                                                TimeUnit.MINUTES
                                        )
                                        .disableStatistics(true)
                );
    }

}
