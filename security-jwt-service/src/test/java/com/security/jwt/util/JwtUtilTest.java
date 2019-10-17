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
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
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


    static Stream<Arguments> isTokenValidTestCases() {
        String expiredJwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSIsInJvbGVzIjpbeyJhdXRob3JpdHkiOiJVU0VSIn1dLCJ"
                               + "pYXQiOjEwMDAwMDAwMDAsImV4cCI6MTAwMDAwMDAwMH0.bqHsralkKGy06yQkkM3B3rIkn--AF3cxiXYDOFcCLbg";
        String notExpiredJwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSIsInJvbGVzIjpbeyJhdXRob3JpdHkiOiJVU0VSIn1dLCJ"
                                  + "pYXQiOjUwMDAwMDAwMDAsImV4cCI6NTAwMDAwMDAwMH0.AvYhkUHQtHQ3bXdf_lUar1iXHR025iWxsTeSLNKi3Ms";
        String jwtSecretKey = "secretKey_ForTestingPurpose@12345#";
        return Stream.of(
                //@formatter:off
                //            token,                jwtSecretKey,          expectedException,                expectedResult
                Arguments.of( null,                 "ItDoesNotCare",       IllegalArgumentException.class,   false ),
                Arguments.of( "ItDoesNotCare",      null,                  IllegalArgumentException.class,   false ),
                Arguments.of( "NotValidToken",      "ItDoesNotCare",       null,                             false ),
                Arguments.of( expiredJwtToken,      jwtSecretKey + "V2",   null,                             false ),
                Arguments.of( expiredJwtToken,      jwtSecretKey,          null,                             false ),
                Arguments.of( notExpiredJwtToken,   jwtSecretKey,          null,                             true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("isTokenValidTestCases")
    @DisplayName("isTokenValid: test cases")
    public void isTokenValid_testCases(String token, String jwtSecretKey, Class<? extends Exception> expectedException, boolean expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> jwtUtil.isTokenValid(token, jwtSecretKey));
        }
        else {
            assertEquals(expectedResult, jwtUtil.isTokenValid(token, jwtSecretKey));
        }
    }

}
