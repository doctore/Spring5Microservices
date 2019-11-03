package com.security.jwt;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jwt.SignedJWT;
import com.security.jwt.util.JweUtil;
import com.security.jwt.util.JwsUtil;
import com.security.jwt.util.JwtUtil;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static java.util.Arrays.asList;

@SpringBootApplication
public class SecurityJwtServiceApplication {

    public static void main(String[] args) {
        //SpringApplication.run(SecurityJwtServiceApplication.class, args);

        /*
        ConfigurableApplicationContext context = SpringApplication.run(SecurityJwtServiceApplication.class, args);
        SecurityService securityService = context.getBean(SecurityService.class);
        Optional<AuthenticationInformationDto> authenticationInfo = securityService.login("Spring5Microservices", "admin", "admin");
        UsernameAuthoritiesDto usernameAuthorities = securityService.getAuthorizationInformation(authenticationInfo.get().getAccessToken(), "Spring5Microservices");
        int a = 1;
         */


        ConfigurableApplicationContext context = SpringApplication.run(SecurityJwtServiceApplication.class, args);

        JwtUtil jwtUtil = context.getBean(JwtUtil.class);
        JwsUtil jwsUtil = context.getBean(JwsUtil.class);
        JweUtil jweUtil = context.getBean(JweUtil.class);

        String jwtSignatureSecret = "secretKey_ForTestingPurpose@12345#";
        Map<String, Object> toInclude = new HashMap<String, Object>() {{
            put("username", "username value");
            put("roles", asList("admin", "user"));
            put("age", 23);
        }};
        Map<String, Object> toInclude2 = new HashMap<String, Object>() {{
            put("username", "username value");
        }};
        try {
            String jwt = jwtUtil.generateJwtToken(toInclude, SignatureAlgorithm.HS256, jwtSignatureSecret, 600).get();
            String jws = jwsUtil.generateToken(toInclude, JWSAlgorithm.HS256, jwtSignatureSecret, 600);

            String jwe = jweUtil.generateToken(toInclude2, JWSAlgorithm.HS256, jwtSignatureSecret, "841d8A6C80C#A4FcAf32D5367t1!C53b", 100);

            /*
            String jwtTokenJwtUtil = jwtUtil.generateJwtToken(toInclude, SignatureAlgorithm.HS256, jwtSignatureSecret, 100).get();
            String jwtTokenJwtUtil2 = jwtUtil2.generateJwtToken(toInclude, JWSAlgorithm.HS256, jwtSignatureSecret, 100).get();

            Map<String, Object> payloadJwtUtil = jwtUtil.getExceptGivenKeys(jwtTokenJwtUtil, jwtSignatureSecret, new HashSet<>());
            Map<String, Object> payloadJwtUtil2 = jwtUtil2.getExceptGivenKeys(jwtTokenJwtUtil, jwtSignatureSecret, new HashSet<>());
             */

            /*
            SignedJWT jwtBefore = jwtUtil2.generateJwtToken2(toInclude, JWSAlgorithm.HS256, jwtSignatureSecret, 100);
            String jweToken = jwtUtil2.jwtEncrypt(jwtBefore);
            SignedJWT jwtAfter = jwtUtil2.jwtDecrypt(jweToken);

            jwtUtil2.testToken(jwtBefore.serialize());
            jwtUtil2.testToken(jweToken);
            jwtUtil2.testToken(jwtAfter.serialize());

             */

            int a = 1;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
