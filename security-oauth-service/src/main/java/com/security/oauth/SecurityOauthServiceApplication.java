package com.security.oauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SecurityOauthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityOauthServiceApplication.class, args);

        /*
        ConfigurableApplicationContext context = SpringApplication.run(SecurityServiceApplication.class, args);
        PasswordEncoder passwordEncoder = context.getBean(PasswordEncoder.class);
        System.out.println("------------- PASSWORD " + passwordEncoder.encode("Spring5Microservices"));
         */
    }

}
