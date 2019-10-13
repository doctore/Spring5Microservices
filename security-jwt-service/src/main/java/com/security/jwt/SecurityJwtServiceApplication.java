package com.security.jwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SecurityJwtServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityJwtServiceApplication.class, args);

        /*
        ConfigurableApplicationContext context = SpringApplication.run(SecurityJwtServiceApplication.class, args);
        JwtClientDetailsService jwtClientDetailsService = context.getBean(JwtClientDetailsService.class);
        System.out.println("------------- CACHE VALUE " + jwtClientDetailsService.findByClientId("Spring5Microservices"));
         */
    }

}
