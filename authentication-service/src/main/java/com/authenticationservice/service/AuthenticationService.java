package com.authenticationservice.service;

import com.authenticationservice.configuration.Constants;
import com.authenticationservice.dto.AuthenticationRequestDto;
import com.authenticationservice.model.User;
import com.authenticationservice.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class AuthenticationService {

    private PasswordEncoder passwordEncoder;
    private JWTUtil jwtUtil;
    private UserService userService;

    @Autowired
    public AuthenticationService(@Lazy PasswordEncoder passwordEncoder, @Lazy JWTUtil jwtUtil, @Lazy UserService userService) {
        this.passwordEncoder = passwordEncoder;
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
    public String generateJWTToken(AuthenticationRequestDto authenticationRequestDto) {
        return Optional.ofNullable(authenticationRequestDto)
                       .map(au -> userService.loadUserByUsername(au.getUsername()))
                       .filter(user -> passwordEncoder.matches(authenticationRequestDto.getPassword(), user.getPassword()))
                       .flatMap(user -> jwtUtil.generateJWTToken(user, Constants.JWT.SIGNATURE_ALGORITHM))
                       .orElseThrow(() -> new EntityNotFoundException(
                               String.format("Failed the JWT token generation of the user %s",
                                       null == authenticationRequestDto ? "null" : authenticationRequestDto.getUsername())));
    }

}
