package com.security.jwt.service.jwt.generator;

import com.security.jwt.dto.RawTokenInformationDto;
import com.security.jwt.interfaces.ITokenInformation;
import com.security.jwt.model.User;
import com.security.jwt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.security.jwt.enums.TokenKeyEnum.AUTHORITIES;
import static com.security.jwt.enums.TokenKeyEnum.NAME;
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
        User user = (User) userService.loadUserByUsername(username);
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

    private Map<String, Object> getAccessTokenInformation(User user) {
        Map<String, Object> accessTokenInformation = new HashMap<>();
        accessTokenInformation.put(USERNAME.getKey(), user.getUsername());
        accessTokenInformation.put(AUTHORITIES.getKey(), user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(toList()));
        accessTokenInformation.put(NAME.getKey(), user.getName());
        return accessTokenInformation;
    }

    private Map<String, Object> getRefreshTokenInformation(User user) {
        Map<String, Object> refreshTokenInformation = new HashMap<>();
        refreshTokenInformation.put(USERNAME.getKey(), user.getUsername());
        return refreshTokenInformation;
    }

    private Map<String, Object> getAdditionalTokenInformation(User user) {
        Map<String, Object> additionalTokenInformation = new HashMap<>();
        additionalTokenInformation.put(USERNAME.getKey(), user.getUsername());
        additionalTokenInformation.put(AUTHORITIES.getKey(), user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(toList()));
        return additionalTokenInformation;
    }

}
