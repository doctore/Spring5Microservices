package com.pizza.configuration.cache;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import com.pizza.configuration.Constants;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static com.pizza.configuration.Constants.CACHE_INSTANCE_NAME;

@Configuration
@ComponentScan(basePackages = {Constants.EXTERNAL_PATH.COMMON})
@EnableCaching
public class CacheConfiguration {

    @Value("${cache.userBlackList.entryCapacity}")
    private int userBlackListCacheEntryCapacity;

    @Value("${cache.userBlackList.expireInSeconds}")
    private int userBlackListCacheExpireInSeconds;

    @Value("${cache.userBlackList.name}")
    @Getter
    private String userBlackListCacheName;


    /**
     * Centralized cache configuration to manage the information we want to cache
     *
     * @return {@link CacheManager}
     */
    @Bean
    public CacheManager cacheManager() {
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(hazelCastConfig());
        return new HazelcastCacheManager(hazelcastInstance);
    }

    /**
     * Include all configuration options and different caches used in the application
     *
     * @return {@link Config}
     */
    private Config hazelCastConfig(){
        Config config = new Config();
        config.setInstanceName(CACHE_INSTANCE_NAME)
                .addMapConfig(new MapConfig()
                        .setName(userBlackListCacheName)
                        .setMaxSizeConfig(new MaxSizeConfig(userBlackListCacheEntryCapacity, MaxSizeConfig.MaxSizePolicy.FREE_HEAP_SIZE))
                        .setEvictionPolicy(EvictionPolicy.LRU)
                        .setTimeToLiveSeconds(userBlackListCacheExpireInSeconds));
        return config;
    }

}
