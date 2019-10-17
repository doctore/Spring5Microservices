package com.security.jwt;

import com.security.jwt.model.JwtClientDetails;
import com.security.jwt.service.jwt.JwtClientDetailsService;
import com.security.jwt.service.jwt.JwtGeneratorService;
import com.security.jwt.util.JwtUtil;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Arrays.asList;

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
        TextEncryptor encryptor = context.getBean(TextEncryptor.class);
        JwtClientDetailsService jwtClientDetailsService = context.getBean(JwtClientDetailsService.class);
        JwtUtil jwtUtil = context.getBean(JwtUtil.class);

        JwtClientDetails jwtClientDetails = jwtClientDetailsService.findByClientId("Spring5Microservices");
        String jwtSecret = encryptor.decrypt(jwtClientDetails.getJwtSecret().replace("{cipher}", ""));
        Map<String, Object> informationToInclude = new HashMap<>();
        informationToInclude.put("username", "username_value");
        informationToInclude.put("name", "name_value");
        informationToInclude.put("age", 21);
        informationToInclude.put("roles", asList("admin", "user"));

        Optional<String> jwtToken = jwtUtil.generateJwtToken(informationToInclude, jwtClientDetails.getJwtAlgorithm(),
                jwtSecret, jwtClientDetails.getAccessTokenValidity());

        Set<String> informationToExclude = new HashSet<>();
        informationToExclude.add("username");

        Map<String, Object> finalClaims = jwtUtil.getInformationExceptGivenClaims(jwtToken.get(), jwtSecret, informationToExclude);
        System.out.println((List)finalClaims.get("roles"));


        // TODO: REVISAR UTILIZACION DEL ALGORITMO. ESTA FALLANDO
        boolean isValid = jwtUtil.isTokenValid(jwtToken.get(), jwtSecret);

        int a = 1;

    }

}
