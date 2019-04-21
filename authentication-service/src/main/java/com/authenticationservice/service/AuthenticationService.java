package com.authenticationservice.service;

import com.authenticationservice.configuration.Constants;
import com.authenticationservice.configuration.security.JwtConfiguration;
import com.authenticationservice.dto.AuthenticationRequestDto;
import com.authenticationservice.dto.UsernameAuthoritiesDto;
import com.authenticationservice.model.User;
import com.authenticationservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {

    private PasswordEncoder passwordEncoder;
    private JwtConfiguration jwtConfiguration;
    private JwtUtil jwtUtil;
    private UserService userService;

    @Autowired
    public AuthenticationService(@Lazy PasswordEncoder passwordEncoder, @Lazy JwtConfiguration jwtConfiguration,
                                 @Lazy JwtUtil jwtUtil, @Lazy UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.jwtConfiguration = jwtConfiguration;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }


    /**
     *    Generates the JWT token using the information included in the given {@link AuthenticationRequestDto},
     * with the {@code username} and {@code password} of an existing {@link User} in database.
     *
     * @param authenticationRequestDto
     *    {@code username} and {@code password} of an existing {@link User}
     *
     * @return JWT token
     *
     * @throws UsernameNotFoundException if the given {@link AuthenticationRequestDto#username} does not exists in database.
     */
    public Optional<String> generateJwtToken(AuthenticationRequestDto authenticationRequestDto) {
        return Optional.ofNullable(authenticationRequestDto)
                       .map(au -> userService.loadUserByUsername(au.getUsername()))
                       .filter(user -> passwordEncoder.matches(authenticationRequestDto.getPassword(), user.getPassword()))
                       .flatMap(user -> jwtUtil.generateJwtToken(user, Constants.JWT.SIGNATURE_ALGORITHM,
                                                                 this.jwtConfiguration.getSecretKey(),
                                                                 this.jwtConfiguration.getExpirationTimeInMilliseconds()));
    }


    /**
     * Checks if the given token is valid or not taking into account the secret key and expiration date.
     *
     * @param token
     *    JWT token to validate (included Http authentication scheme)
     *
     * @return {@code false} if the given token is expired or is not valid, {@code true} otherwise.
     */
    public boolean isJwtTokenValid(String token) {
        return Optional.ofNullable(token)
                       .map(t -> t.replace(this.jwtConfiguration.getAuthorizationPrefix(), ""))
                       .map(t -> jwtUtil.isTokenValid(t, this.jwtConfiguration.getSecretKey()))
                       .orElse(Boolean.FALSE);
    }


    /**
     * Using the given JWT token returns the {@link User#username} and {@link User#getAuthorities()} data.
     *
     * @param token
     *    JWT token (included Http authentication scheme)
     *
     * @return {@link Optional} with {@link UsernameAuthoritiesDto}
     *
     * @throws UsernameNotFoundException if the {@code username} included in the JWT token does not exists in database
     * @see {@link AccountStatusUserDetailsChecker#check(UserDetails)} for more information about the other ones
     */
    public Optional<UsernameAuthoritiesDto> getAuthenticationInformation(String token) {
        return Optional.ofNullable(token)
                       .map(t -> t.replace(this.jwtConfiguration.getAuthorizationPrefix(), ""))
                       .filter(t -> jwtUtil.isTokenValid(t, this.jwtConfiguration.getSecretKey()))
                       .map(t -> userService.loadUserByUsername(jwtUtil.getUsernameFromToken(t, this.jwtConfiguration.getSecretKey()).get()))
                       .map(u -> new UsernameAuthoritiesDto(u.getUsername(), u.getAuthorities()));
    }

}
