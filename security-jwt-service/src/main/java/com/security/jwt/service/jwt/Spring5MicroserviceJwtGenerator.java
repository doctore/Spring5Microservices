package com.security.jwt.service.jwt;

import com.security.jwt.service.UserService;
import com.spring5microservices.common.interfaces.ITokenInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class Spring5MicroserviceJwtGenerator implements ITokenInformation {

    private JwtClientDetailsService jwtClientDetailsService;
    private UserService userService;

    @Autowired
    public Spring5MicroserviceJwtGenerator(@Lazy JwtClientDetailsService jwtClientDetailsService, @Lazy UserService userService) {
        this.jwtClientDetailsService = jwtClientDetailsService;
        this.userService = userService;
    }


    // TODO: include two private different ones: access and refresh
    @Override
    public Map<String, Object> getTokenInformation() {
        return null;
    }


    /*
    public boolean isRefreshToken(OAuth2AccessToken token) {
        return token.getAdditionalInformation().containsKey("ati");
    }
     */

}
