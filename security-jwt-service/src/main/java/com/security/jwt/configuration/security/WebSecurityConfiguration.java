package com.security.jwt.configuration.security;

import com.security.jwt.configuration.rest.RestRoutes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${springfox.documentation.swagger.v2.path}")
    private String documentationPath;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            // Make sure we use stateless session; session won't be used to store user's state.
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            // Handle an authorized attempts
            .exceptionHandling().authenticationEntryPoint((req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED))
            .and()
            // Authorization requests config
            .authorizeRequests()
            // List of services do not require authentication
            .antMatchers(GET, documentationPath).permitAll()
            .antMatchers(POST, RestRoutes.SECURITY.ROOT + "/**").permitAll()
            // Any other request must be authenticated
            .anyRequest().authenticated();
    }

}
