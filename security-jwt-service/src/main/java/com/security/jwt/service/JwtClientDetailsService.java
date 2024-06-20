package com.security.jwt.service;

import com.security.jwt.application.spring5microservices.model.User;
import com.security.jwt.exception.ClientNotFoundException;
import com.security.jwt.model.JwtClientDetails;
import com.security.jwt.repository.JwtClientDetailsRepository;
import com.security.jwt.service.cache.JwtClientDetailsCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static java.lang.String.format;

@Service
public class JwtClientDetailsService implements ReactiveUserDetailsService {

    private final JwtClientDetailsCacheService jwtClientDetailsCacheService;

    private final JwtClientDetailsRepository jwtClientDetailsRepository;


    @Autowired
    public JwtClientDetailsService(@Lazy final JwtClientDetailsCacheService jwtClientDetailsCacheService,
                                   @Lazy final JwtClientDetailsRepository jwtClientDetailsRepository) {
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
     * @throws ClientNotFoundException if the given {@code clientId} does not exist in database
     */
    public JwtClientDetails findByClientId(final String clientId) {
        return jwtClientDetailsCacheService.get(clientId)
                .orElseGet(() ->
                    jwtClientDetailsRepository.findByClientId(clientId)
                            .map(c -> {
                                jwtClientDetailsCacheService.put(clientId, c);
                                return c;
                            })
                            .orElseThrow(() ->
                                    new ClientNotFoundException(
                                            format("The given clientId: %s was not found in database",
                                                    clientId)
                                    )
                            )
                );
    }


    /**
     * Gets {@link UserDetails} information in database related with the given {@link User#getUsername()}
     *
     * @param clientId
     *    Identifier to search a coincidence in {@link JwtClientDetails#getUsername()}
     *
     * @return {@link Mono} of {@link UserDetails}
     *
     * @throws ClientNotFoundException if the given {@code clientId} does not exist in database
     * @see {@link AccountStatusUserDetailsChecker#check(UserDetails)} for more information about the other ones.
     */
    @Override
    public Mono<UserDetails> findByUsername(final String clientId) {
        UserDetails userDetails = findByClientId(clientId);
        new AccountStatusUserDetailsChecker().check(userDetails);
        return Mono.just(userDetails);
    }

}
