package com.security.oauth.configuration.security;

import com.security.oauth.configuration.rest.RestRoutes;
import com.security.oauth.configuration.security.oauth.CustomJdbcClientDetailsService;
import com.security.oauth.service.UserService;
import com.security.oauth.service.cache.ClientDetailsCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.sql.DataSource;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private ClientDetailsCacheService clientDetailsCacheService;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private UserService userService;


    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.pathMapping(
                        RestRoutes.ACCESS_TOKEN_URI.getFirst(),
                        RestRoutes.ACCESS_TOKEN_URI.getSecond()
                )
                .pathMapping(
                        RestRoutes.CHECK_TOKEN_URI.getFirst(),
                        RestRoutes.CHECK_TOKEN_URI.getSecond()
                )
                .pathMapping(
                        RestRoutes.USER_AUTHORIZATION_URI.getFirst(),
                        RestRoutes.USER_AUTHORIZATION_URI.getSecond()
                )
                .tokenStore(tokenStore)
                .accessTokenConverter(jwtAccessTokenConverter)
                .userDetailsService(userService)
                .authenticationManager(authManager);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security.checkTokenAccess("isAuthenticated()");
    }


    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.jdbc(dataSource)
               .passwordEncoder(passwordEncoder);

        clients.withClientDetails(new CustomJdbcClientDetailsService(dataSource, clientDetailsCacheService));
    }

}
