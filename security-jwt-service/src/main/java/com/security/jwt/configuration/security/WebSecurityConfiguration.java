package com.security.jwt.configuration.security;

import com.security.jwt.configuration.documentation.DocumentationConfiguration;
import com.security.jwt.service.JwtClientDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.OPTIONS;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class WebSecurityConfiguration {

    private final String SPRING_ACTUATOR_PATH = "/actuator";
    private final String ALLOW_ALL_ENDPOINTS = "/**";

    private final DocumentationConfiguration documentationConfiguration;

    private final JwtClientDetailsService jwtClientDetailsService;

    private final PasswordEncoder passwordEncoder;


    public WebSecurityConfiguration(@Lazy final DocumentationConfiguration documentationConfiguration,
                                    @Lazy final JwtClientDetailsService jwtClientDetailsService,
                                    @Lazy final PasswordEncoder passwordEncoder) {
        this.documentationConfiguration = documentationConfiguration;
        this.jwtClientDetailsService = jwtClientDetailsService;
        this.passwordEncoder = passwordEncoder;
    }


    @Bean
    public ReactiveAuthenticationManager authenticationManager() {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(jwtClientDetailsService);
        authenticationManager.setPasswordEncoder(passwordEncoder);
        return authenticationManager;
    }


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(final ServerHttpSecurity http) {
        return http.csrf().disable()
                .formLogin().disable()
                // Authorization requests config using Basic Auth
                .httpBasic().and()
                // Make sure we use stateless session; session won't be used to store user's state
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                // Handle an authorized attempts
                .exceptionHandling()
                // There is no a logged user
                .authenticationEntryPoint(
                        (swe, e) ->
                                Mono.fromRunnable(
                                        () -> swe.getResponse().setStatusCode(UNAUTHORIZED)
                                )
                // Logged user has not the required authorities
                ).accessDeniedHandler(
                        (swe, e) ->
                                Mono.fromRunnable(
                                        () -> swe.getResponse().setStatusCode(FORBIDDEN)
                                )
                )
                .and()
                // Include our custom AuthenticationManager
                .authenticationManager(authenticationManager())
                .authorizeExchange()
                // List of services do not require authentication
                .pathMatchers(OPTIONS).permitAll()
                .pathMatchers(
                        GET,
                        allowedGetEndpoints()
                ).permitAll()
                // Any other request must be authenticated
                .anyExchange().authenticated()
                .and().build();
    }


    private String[] allowedGetEndpoints() {
        return new String[] {
                SPRING_ACTUATOR_PATH + ALLOW_ALL_ENDPOINTS,
                documentationConfiguration.getApiDocsPath() + ALLOW_ALL_ENDPOINTS,
                documentationConfiguration.getApiUiUrl() + ALLOW_ALL_ENDPOINTS,
                documentationConfiguration.getWebjarsUrl() + ALLOW_ALL_ENDPOINTS
        };
    }

}