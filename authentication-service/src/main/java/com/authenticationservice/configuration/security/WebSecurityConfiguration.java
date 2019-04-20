package com.authenticationservice.configuration.security;

import com.authenticationservice.configuration.rest.RestRoutes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            // make sure we use stateless session; session won't be used to store user's state.
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            // handle an authorized attempts
            .exceptionHandling().authenticationEntryPoint((req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED))
            .and()
            // Authorization requests config
            .authorizeRequests()
            // List of services do not require authentication
            .antMatchers(HttpMethod.POST, RestRoutes.AUTHENTICATION.ROOT + RestRoutes.AUTHENTICATION.LOGIN).permitAll()
            .antMatchers(HttpMethod.GET, RestRoutes.AUTHENTICATION.ROOT + RestRoutes.AUTHENTICATION.VALIDATE + "/**").permitAll()
            // Any other request must be authenticated
            .anyRequest().authenticated();
    }

}
