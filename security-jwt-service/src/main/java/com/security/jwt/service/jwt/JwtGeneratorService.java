package com.security.jwt.service.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class JwtGeneratorService {

    private ApplicationContext applicationContext;

    @Autowired
    public JwtGeneratorService(@Lazy ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    // TODO: Use applicationContext to load required class from enum

}
