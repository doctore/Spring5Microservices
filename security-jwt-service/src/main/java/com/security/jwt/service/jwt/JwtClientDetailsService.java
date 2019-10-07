package com.security.jwt.service.jwt;

import com.security.jwt.repository.JwtClientDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class JwtClientDetailsService {

    private JwtClientDetailsRepository jwtClientDetailsRepository;

    @Autowired
    public JwtClientDetailsService(@Lazy JwtClientDetailsRepository jwtClientDetailsRepository) {
        this.jwtClientDetailsRepository = jwtClientDetailsRepository;
    }

    // TODO: Use Cache

}
