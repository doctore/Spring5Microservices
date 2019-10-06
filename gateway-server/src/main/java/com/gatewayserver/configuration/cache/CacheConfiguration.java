package com.gatewayserver.configuration.cache;

import com.gatewayserver.configuration.Constants;
import lombok.Getter;
import org.cache2k.Cache2kBuilder;
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
@Getter
public class CacheConfiguration {

    @Value("${cache.entryCapacity}")
    private Long entryCapacity;

    @Value("${cache.expireInMinutes}")
    private Long expireInMinutes;

    @Value("${cache.name}")
    private String name;

    @Bean
    public CacheManager cacheManager() {
        return new SpringCache2kCacheManager().addCaches(
                c -> Cache2kBuilder.of(String.class, String.class)
                        .name(name)
                        .entryCapacity(entryCapacity)
                        .expireAfterWrite(expireInMinutes, TimeUnit.MINUTES)
                        .disableStatistics(true)
        );
    }

}
