package com.security.jwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SecurityJwtServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityJwtServiceApplication.class, args);

        /*
        ConfigurableApplicationContext context = SpringApplication.run(SecurityServiceApplication.class, args);
        PasswordEncoder passwordEncoder = context.getBean(PasswordEncoder.class);
        System.out.println("------------- PASSWORD " + passwordEncoder.encode("Spring5Microservices"));
         */
    }

}
