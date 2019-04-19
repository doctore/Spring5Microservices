package com.authenticationservice.service;

import com.authenticationservice.configuration.Constants;
import com.authenticationservice.configuration.security.JwtConfiguration;
import com.authenticationservice.dto.AuthenticationRequestDto;
import com.authenticationservice.model.User;
import com.authenticationservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
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
     */
    public String generateJwtToken(AuthenticationRequestDto authenticationRequestDto) {
        return Optional.ofNullable(authenticationRequestDto)
                       .map(au -> userService.loadUserByUsername(au.getUsername()))
                       .filter(user -> passwordEncoder.matches(authenticationRequestDto.getPassword(), user.getPassword()))
                       .flatMap(user -> jwtUtil.generateJwtToken(user, Constants.JWT.SIGNATURE_ALGORITHM,
                                                                 this.jwtConfiguration.getSecretKey(),
                                                                 this.jwtConfiguration.getExpirationTimeInMilliseconds()))
                       .orElseThrow(() -> new EntityNotFoundException(
                               String.format("Failed the JWT token generation of the user %s",
                                       null == authenticationRequestDto ? "null" : authenticationRequestDto.getUsername())));
    }


    /**
     * Checks if the given token is valid or not taking into account the secret key and expiration date.
     *
     * @param token
     *    JWT token to validate
     *
     * @return {@code false} if the given token is expired or is not valid, {@code true} otherwise.
     */
    public boolean isJwtTokenValid(String token) {
        return Optional.ofNullable(token)
                       .map(t -> jwtUtil.isTokenValid(t, this.jwtConfiguration.getSecretKey()))
                       .orElse(Boolean.FALSE);
    }

}
