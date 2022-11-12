package com.security.jwt.service.cache;

import com.security.jwt.configuration.cache.CacheConfiguration;
import com.security.jwt.model.JwtClientDetails;
import com.spring5microservices.common.service.CacheService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.util.Optional.ofNullable;

@AllArgsConstructor
@Service
public class JwtClientDetailsCacheService {

    @Lazy
    private final CacheConfiguration cacheConfiguration;

    @Lazy
    private final CacheService cacheService;


    /**
     * Clear the cache used to store {@link JwtClientDetails} information.
     *
     * @return {@code true} if the cache was cleared, {@code false} otherwise
     */
    public boolean clear() {
        return cacheService.clear(cacheConfiguration.getJwtConfigurationCacheName());
    }


    /**
     * Check if exists the given {@link JwtClientDetails#getClientId()} inside the related cache.
     *
     * @param clientId
     *    {@link JwtClientDetails#getClientId()} to search
     *
     * @return {@code true} if the {@code clientId} exists, {@code false} otherwise
     */
    public boolean contains(final String clientId) {
        return ofNullable(clientId)
                .map(id ->
                        cacheService.contains(
                                cacheConfiguration.getJwtConfigurationCacheName(),
                                id
                        )
                )
                .orElse(false);
    }


    /**
     * Return the {@link JwtClientDetails} related with the given {@code clientId} inside the related cache.
     *
     * @param clientId
     *    {@link JwtClientDetails#getClientId()} to search
     *
     * @return @return {@link Optional} with the {@link JwtClientDetails} if it was found, {@link Optional#empty()} otherwise
     */
    public Optional<JwtClientDetails> get(final String clientId) {
        return cacheService.get(
                cacheConfiguration.getJwtConfigurationCacheName(),
                clientId
        );
    }


    /**
     * Include a pair of {@link JwtClientDetails#getClientId()} - {@link JwtClientDetails} inside the related cache.
     *
     * @param clientId
     *    {@link JwtClientDetails#getClientId()} used to identify the {@link JwtClientDetails} to store
     * @param jwtClientDetails
     *    {@link JwtClientDetails} to store
     *
     * @return {@code true} if the data was stored, {@code false} otherwise
     */
    public boolean put(final String clientId,
                       final JwtClientDetails jwtClientDetails) {
        return cacheService.put(
                cacheConfiguration.getJwtConfigurationCacheName(),
                clientId,
                jwtClientDetails
        );
    }

}
