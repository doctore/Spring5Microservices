package com.security.jwt.application.spring5microservices.service;

import com.security.jwt.dto.RawAuthenticationInformationDto;
import com.security.jwt.interfaces.IAuthenticationGenerator;
import com.security.jwt.application.spring5microservices.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.security.jwt.enums.TokenKeyEnum.AUTHORITIES;
import static com.security.jwt.enums.TokenKeyEnum.NAME;
import static com.security.jwt.enums.TokenKeyEnum.USERNAME;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@Service
public class Spring5MicroserviceAuthenticationGenerator implements IAuthenticationGenerator {

    @Override
    public Optional<RawAuthenticationInformationDto> getRawAuthenticationInformation(UserDetails userDetails) {
        return ofNullable(userDetails)
                .map(user -> buildAuthenticationInformation((User)user));
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
