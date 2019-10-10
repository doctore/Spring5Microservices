package com.security.jwt.service.jwt;

import com.security.jwt.configuration.cache.CacheConfiguration;
import com.security.jwt.exception.ClientNotFoundException;
import com.security.jwt.model.JwtClientDetails;
import com.security.jwt.repository.JwtClientDetailsRepository;
import com.spring5microservices.common.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

@Service
public class JwtClientDetailsService {

    private CacheConfiguration cacheConfiguration;
    private CacheService cacheService;
    private JwtClientDetailsRepository jwtClientDetailsRepository;

    @Autowired
    public JwtClientDetailsService(@Lazy CacheConfiguration cacheConfiguration, @Lazy CacheService cacheService,
                                   @Lazy JwtClientDetailsRepository jwtClientDetailsRepository) {
        this.cacheConfiguration = cacheConfiguration;
        this.cacheService = cacheService;
        this.jwtClientDetailsRepository = jwtClientDetailsRepository;
    }


    /**
     * Return the {@link JwtClientDetails} with clientId matches with the given one.
     *
     * @param clientId
     *    ClientId to search
     *
     * @return {@link Optional} of {@link JwtClientDetails} is exists, {@link Optional#empty()} otherwise
     *
     * @throws ClientNotFoundException if the given username does not exists in database.
     */
    public JwtClientDetails findByClientId(String clientId) {
        return ofNullable(clientId)
                .map(id -> (JwtClientDetails)cacheService.get(cacheConfiguration.getJwtConfigurationCacheName(), clientId)
                                                .orElseGet(() ->
                                                        jwtClientDetailsRepository.findByClientId(clientId)
                                                                .map(c -> {
                                                                    cacheService.put(cacheConfiguration.getJwtConfigurationCacheName(), clientId, c);
                                                                    return c;
                                                                })
                                                )
                )
                .orElseThrow(() -> new ClientNotFoundException(format("The given clientId: %s was not found in database", clientId)));
    }

}
