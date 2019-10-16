package com.security.jwt.util;

import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {JwtUtil.class})
public class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;


    static Stream<Arguments> generateJwtTokenTestCases() {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;
        Map<String, Object> informationToInclude = new HashMap<>();
        String secretKey = "Spring5Microservices_jwtSecretKey";
        return Stream.of(
                //@formatter:off
                //            informationToInclude,   signatureAlgorithm,   jwtSecretKey,      expirationTimeInSeconds,  expectedException,                isTokenGenerated
                Arguments.of( null,                   signatureAlgorithm,   "ItDoesNotCare",   90,                       null,                             false ),
                Arguments.of( informationToInclude,   null,                 "ItDoesNotCare",   90,                       IllegalArgumentException.class,   false ),
                Arguments.of( informationToInclude,   signatureAlgorithm,   null,              90,                       IllegalArgumentException.class,   false ),
                Arguments.of( informationToInclude,   signatureAlgorithm,   secretKey,         90,                       null,                             true )
        ); //@formatter:on
    }


    @ParameterizedTest
    @MethodSource("generateJwtTokenTestCases")
    @DisplayName("generateJwtToken: test cases")
    public void generateJwtToken_testCases(Map<String, Object> informationToInclude, SignatureAlgorithm signatureAlgorithm,
                                           String jwtSecretKey, long expirationTimeInSeconds, Class<? extends Exception> expectedException,
                                           boolean isTokenGenerated) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> jwtUtil.generateJwtToken(informationToInclude, signatureAlgorithm, jwtSecretKey, expirationTimeInSeconds));
        }
        else {
            assertEquals(isTokenGenerated, jwtUtil.generateJwtToken(informationToInclude, signatureAlgorithm, jwtSecretKey, expirationTimeInSeconds).isPresent());
        }
    }


}
