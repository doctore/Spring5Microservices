package com.order.configuration.security.filter;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class SecurityFilterConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    @Lazy
    private final SecurityFilter securityFilter;

    @Override
    public void configure(HttpSecurity http) {
        http.addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
