package com.security.oauth.configuration.security;

import com.security.oauth.configuration.rest.RestRoutes;
import com.security.oauth.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpMethod.GET;

@AllArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final String SPRING_ACTUATOR_PATH = "/actuator";
    private final String ALLOW_ALL_ENDPOINTS = "/**";

    @Lazy
    private final UserService userService;


    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            // Make sure we use stateless session; session won't be used to store user's state.
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            // Handle an authorized attempts
            .exceptionHandling().authenticationEntryPoint(
                    (req, rsp, e) ->
                            rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                )
            .and()
            // Authorization requests config
            .authorizeRequests()
            // List of services do not require authentication
            .antMatchers(
                    GET,
                    allowedGetEndpoints()
            ).permitAll()
            // Any other request must be authenticated
            .anyRequest().authenticated();
    }


    private String[] allowedGetEndpoints() {
        return new String[] {
                SPRING_ACTUATOR_PATH + ALLOW_ALL_ENDPOINTS,
                RestRoutes.SECURITY_OAUTH.ROOT + ALLOW_ALL_ENDPOINTS
        };
    }

}
