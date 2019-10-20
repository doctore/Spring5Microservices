package com.security.jwt.service.generator;

import com.security.jwt.dto.RawAuthenticationInformationDto;
import com.security.jwt.exception.UnauthorizedException;
import com.security.jwt.interfaces.IAuthenticationGenerator;
import com.security.jwt.model.User;
import com.security.jwt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.security.jwt.enums.TokenKeyEnum.AUTHORITIES;
import static com.security.jwt.enums.TokenKeyEnum.NAME;
import static com.security.jwt.enums.TokenKeyEnum.USERNAME;
import static java.lang.String.format;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;

@Service
public class Spring5MicroserviceAuthenticationGenerator implements IAuthenticationGenerator {

    private UserService userService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public Spring5MicroserviceAuthenticationGenerator(@Lazy UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public Optional<RawAuthenticationInformationDto> getRawAuthenticationInformation(String username, String password) {
        return of((User)userService.loadUserByUsername(username))
                .filter(user -> {
                    if (!passwordEncoder.matches(password, user.getPassword()))
                        throw new UnauthorizedException(format("The password given for the username: %s does not mismatch", username));
                    return true;
                })
                .map(user -> buildAuthenticationInformation(user));
    }


    @Override
    public Optional<RawAuthenticationInformationDto> refreshRawAuthenticationInformation(String username) {
        return of((User)userService.loadUserByUsername(username))
                .map(user -> buildAuthenticationInformation(user));
    }


    @Override
    public String getUsernameKey() {
        return USERNAME.getKey();
    }


    @Override
    public String getRolesKey() {
        return AUTHORITIES.getKey();
    }


    private RawAuthenticationInformationDto buildAuthenticationInformation(User user) {
        return RawAuthenticationInformationDto.builder()
                .accessTokenInformation(getAccessTokenInformation(user))
                .refreshTokenInformation(getRefreshTokenInformation(user))
                .additionalTokenInformation(getAdditionalTokenInformation(user))
                .build();
    }

    private Map<String, Object> getAccessTokenInformation(User user) {
        return new HashMap<String, Object>() {{
            put(USERNAME.getKey(), user.getUsername());
            put(NAME.getKey(), user.getName());
            put(AUTHORITIES.getKey(), user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(toList()));
        }};
    }

    private Map<String, Object> getRefreshTokenInformation(User user) {
        return new HashMap<String, Object>() {{
            put(USERNAME.getKey(), user.getUsername());
        }};
    }

    private Map<String, Object> getAdditionalTokenInformation(User user) {
        return new HashMap<String, Object>() {{
            put(USERNAME.getKey(), user.getUsername());
            put(AUTHORITIES.getKey(), user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(toList()));
        }};
    }

}
