package com.security.jwt.service.jwt.generator;

import com.security.jwt.dto.RawAuthenticationInformationDto;
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
    public RawAuthenticationInformationDto getTokenInformation(String username) {
        User user = (User) userService.loadUserByUsername(username);
        return RawAuthenticationInformationDto.builder()
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
