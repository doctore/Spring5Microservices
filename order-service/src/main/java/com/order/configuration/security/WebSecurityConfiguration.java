package com.order.configuration.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class WebSecurityConfiguration {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SecurityContextRepository securityContextRepository;


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http.csrf().disable()
                   .formLogin().disable()
                   .httpBasic().disable()
                   // Handle an authorized attempts
                   .exceptionHandling()
                   // There is no a logged user
                   .authenticationEntryPoint((swe, e) -> Mono.fromRunnable(
                           () -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED))
                   // Logged user has not the required authorities
                   ).accessDeniedHandler((swe, e) -> Mono.fromRunnable(
                        () -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN))
                   )
                   .and()
                   // Include our custom AuthenticationManager
                   .authenticationManager(authenticationManager)
                   // Include our custom SecurityContextRepository
                   .securityContextRepository(securityContextRepository)
                   .authorizeExchange()
                   // List of services do not require authentication
                   .pathMatchers(HttpMethod.OPTIONS).permitAll()
                   // Any other request must be authenticated
                   .anyExchange().authenticated()
                   .and().build();
    }

}
