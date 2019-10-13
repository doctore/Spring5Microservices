package com.security.jwt.service.jwt.generator;

import com.security.jwt.dto.RawTokenInformationDto;
import com.security.jwt.enums.TokenKeyEnum;
import com.security.jwt.interfaces.ITokenInformation;
import com.security.jwt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.security.jwt.enums.TokenKeyEnum.AUTHORITIES;
import static com.security.jwt.enums.TokenKeyEnum.USERNAME;
import static java.util.stream.Collectors.toList;

@Service
public class Spring5MicroserviceJwtGenerator implements ITokenInformation {

    private UserService userService;

    @Autowired
    public Spring5MicroserviceJwtGenerator(@Lazy UserService userService) {
        this.userService = userService;
    }

    @Override
    public RawTokenInformationDto getTokenInformation(String username) {
        UserDetails user = userService.loadUserByUsername(username);
        return RawTokenInformationDto.builder()
                .accessTokenInformation(getAccessTokenInformation(user))
                .refreshTokenInformation(getRefreshTokenInformation(user))
                .additionalTokenInformation(getAdditionalTokenInformation(user))
                .build();
    }


    @Override
    public String getUsernameKey() {
        return USERNAME.getKey();
    }


    @Override
    public String getRolesKey() {
        return AUTHORITIES.getKey();
    }

    private Map<String, Object> getAccessTokenInformation(UserDetails userDetails) {
        Map<String, Object> accessTokenInformation = new HashMap<>();
        accessTokenInformation.put(USERNAME.getKey(), userDetails.getUsername());
        accessTokenInformation.put(AUTHORITIES.getKey(), userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(toList()));
        return accessTokenInformation;
    }

    private Map<String, Object> getRefreshTokenInformation(UserDetails userDetails) {
        Map<String, Object> refreshTokenInformation = new HashMap<>();
        refreshTokenInformation.put(USERNAME.getKey(), userDetails.getUsername());
        return refreshTokenInformation;
    }

    private Map<String, Object> getAdditionalTokenInformation(UserDetails userDetails) {
        Map<String, Object> additionalTokenInformation = new HashMap<>();
        additionalTokenInformation.put(USERNAME.getKey(), userDetails.getUsername());
        additionalTokenInformation.put(AUTHORITIES.getKey(), userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(toList()));
        return additionalTokenInformation;
    }

}
