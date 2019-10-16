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
        TextEncryptor encryptor = context.getBean(TextEncryptor.class);
        JwtClientDetailsService jwtClientDetailsService = context.getBean(JwtClientDetailsService.class);
        JwtUtil jwtUtil = context.getBean(JwtUtil.class);

        JwtClientDetails jwtClientDetails = jwtClientDetailsService.findByClientId("Spring5Microservices");
        //String jwtSecret = encryptor.decrypt(jwtClientDetails.getJwtSecret().replace("{cipher}", ""));

        // TODO: Increment the size of jwt secret keys in database
        String jwtSecret = "Spring5Microservices_jwtSecretKehkjhkjhkhkvhsdkhviufediyfiugfkjdvnkdfhkuehk34873894798327498732hfdkjhfkdshfksdhjkfhdsy";

        Optional<String> jwtToken = jwtUtil.generateJwtToken(new HashMap<>(), jwtClientDetails.getJwtAlgorithm(),
                jwtSecret, jwtClientDetails.getAccessTokenValidity());

        // TODO: REVISAR UTILIZACION DEL ALGORITMO. ESTA FALLANDO
        boolean isValid = jwtUtil.isTokenValid(jwtToken.get(), jwtSecret);

        int a = 1;

    }

}
