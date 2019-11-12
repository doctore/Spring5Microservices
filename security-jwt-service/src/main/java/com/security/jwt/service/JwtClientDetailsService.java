package com.security.jwt.service;

import com.security.jwt.exception.ClientNotFoundException;
import com.security.jwt.model.JwtClientDetails;
import com.security.jwt.repository.JwtClientDetailsRepository;
import com.security.jwt.service.cache.JwtClientDetailsCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.lang.String.format;

@Service
public class JwtClientDetailsService {

    private JwtClientDetailsCacheService jwtClientDetailsCacheService;
    private JwtClientDetailsRepository jwtClientDetailsRepository;

    @Autowired
    public JwtClientDetailsService(JwtClientDetailsCacheService jwtClientDetailsCacheService,
                                   @Lazy JwtClientDetailsRepository jwtClientDetailsRepository) {
        this.jwtClientDetailsCacheService = jwtClientDetailsCacheService;
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
     * @throws ClientNotFoundException if the given {@code clientId} does not exists in database
     */
    public JwtClientDetails findByClientId(String clientId) {
        return jwtClientDetailsCacheService.get(clientId)
                .orElseGet(() ->
                    jwtClientDetailsRepository.findByClientId(clientId)
                            .map(c -> {
                                jwtClientDetailsCacheService.put(clientId, c);
                                return c;
                            })
                            .orElseThrow(() -> new ClientNotFoundException(format("The given clientId: %s was not found in database", clientId)))
                );
    }

}
