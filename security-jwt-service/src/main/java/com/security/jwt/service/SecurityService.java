package com.security.jwt.service;

import com.security.jwt.dto.AuthenticationRequestDto;
import com.security.jwt.model.JwtClientDetails;
import com.security.jwt.model.User;
import com.security.jwt.service.authentication.AuthenticationGeneratorService;
import com.spring5microservices.common.dto.AuthenticationInformationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.util.Optional.ofNullable;

@Service
public class SecurityService {

    private AuthenticationGeneratorService authenticationGeneratorService;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @Autowired
    public SecurityService(@Lazy AuthenticationGeneratorService authenticationGeneratorService, @Lazy PasswordEncoder passwordEncoder,
                           @Lazy UserService userService) {
        this.authenticationGeneratorService = authenticationGeneratorService;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }


    /**
     *    Verify the existence of the equivalent {@link User} using the given {@link AuthenticationRequestDto}, generating
     * the {@link AuthenticationInformationDto} related with the given {@code clientId}.
     *
     * @param authenticationRequestDto
     *    {@code username} and {@code password} of an existing {@link User}
     * @param clientId
     *    {@clientId} of an existing {@link JwtClientDetails}
     *
     * @return {@link AuthenticationInformationDto}
     */
    public Optional<AuthenticationInformationDto> login(AuthenticationRequestDto authenticationRequestDto, String clientId) {
        return ofNullable(authenticationRequestDto)
                .map(au -> userService.loadUserByUsername(au.getUsername()))
                .filter(user -> passwordEncoder.matches(authenticationRequestDto.getPassword(), user.getPassword()))
                .flatMap(user -> authenticationGeneratorService.getAuthenticationInformation(clientId, user.getUsername()));
    }

}
