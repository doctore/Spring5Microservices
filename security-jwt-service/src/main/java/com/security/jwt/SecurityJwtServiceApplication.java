package com.security.jwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SecurityJwtServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityJwtServiceApplication.class, args);

        /*
        ConfigurableApplicationContext context = SpringApplication.run(SecurityJwtServiceApplication.class, args);
        SecurityService securityService = context.getBean(SecurityService.class);
        Optional<AuthenticationInformationDto> authenticationInfo = securityService.login("Spring5Microservices", "admin", "admin");
        UsernameAuthoritiesDto usernameAuthorities = securityService.getAuthorizationInformation(authenticationInfo.get().getAccessToken(), "Spring5Microservices");
        int a = 1;
         */
    }

}
