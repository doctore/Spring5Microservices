package com.security.jwt;

import com.security.jwt.model.JwtClientDetails;
import com.security.jwt.service.jwt.JwtClientDetailsService;
import com.security.jwt.util.JwtUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.HashMap;
import java.util.Optional;

@SpringBootApplication
public class SecurityJwtServiceApplication {

    public static void main(String[] args) {
        //SpringApplication.run(SecurityJwtServiceApplication.class, args);

        /*
        ConfigurableApplicationContext context = SpringApplication.run(SecurityJwtServiceApplication.class, args);
        JwtClientDetailsService jwtClientDetailsService = context.getBean(JwtClientDetailsService.class);
        System.out.println("------------- CACHE VALUE " + jwtClientDetailsService.findByClientId("Spring5Microservices"));
         */

        ConfigurableApplicationContext context = SpringApplication.run(SecurityJwtServiceApplication.class, args);
        JwtClientDetailsService jwtClientDetailsService = context.getBean(JwtClientDetailsService.class);
        JwtUtil jwtUtil = context.getBean(JwtUtil.class);

        JwtClientDetails jwtClientDetails = jwtClientDetailsService.findByClientId("Spring5Microservices");
        Optional<String> jwtToken = jwtUtil.generateJwtToken(new HashMap<>(), jwtClientDetails.getJwtAlgorithm(),
                jwtClientDetails.getJwtSecret().replace("{cipher}", ""), jwtClientDetails.getAccessTokenValidity());

        boolean isValid = jwtUtil.isTokenValid(jwtToken.get(), jwtClientDetails.getJwtAlgorithm(),
                jwtClientDetails.getJwtSecret().replace("{cipher}", ""));

        int a = 1;

    }

}
