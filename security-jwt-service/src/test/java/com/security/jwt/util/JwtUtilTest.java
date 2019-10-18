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
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
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


    static Stream<Arguments> getUsernameTestCases() {
        String expiredJwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InVzZXJuYW1lIHZhbHVlIiwicm9sZXMiOlsiYWRtaW4iLCJ1c2VyIl0sIm5hbWUiOiJuYW1lI"
                               + "HZhbHVlIiwiYWdlIjoyMywiaWF0IjoxMDAwMDAwMDAwLCJleHAiOjEwMDAwMDAwMDB9.u1xbNQTk1Z_fq6FMK6qyKSmhwhU1MLvvBSMAfYM3FDs";
        String notExpiredEmptyJwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI4Y2JjY2ZlMS04ZDg5LTRmNGYtODRmMS0yMGY5MGEyNjg5ODYiLCJpY"
                                       + "XQiOjUwMDAwMDAwMDAsImV4cCI6NTAwMDAwMDAwMH0.E1OZCZ-e2nDJ9J5EoDgU7xdnjidKdv28LATNXpLJNhc";
        String notExpiredJwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InVzZXJuYW1lIHZhbHVlIiwicm9sZXMiOlsiYWRtaW4iLCJ1c2VyIl0sIm5hbWUiOiJuYW1lI"
                                  + "HZhbHVlIiwiYWdlIjoyMywiaWF0Ijo1MDAwMDAwMDAwLCJleHAiOjUwMDAwMDAwMDB9.mLy5Kf1HX20YFiFpTCz6birHbDtmMXGGw3h9Q9xMHAs";
        String jwtSecretKey = "secretKey_ForTestingPurpose@12345#";
        String usernameValue = "username value";
        return Stream.of(
                //@formatter:off
                //            token,                     jwtSecretKey,          expectedException,                expectedResult
                Arguments.of( null,                      "ItDoesNotCare",       IllegalArgumentException.class,   null ),
                Arguments.of( "ItDoesNotCare",           null,                  IllegalArgumentException.class,   null ),
                Arguments.of( "NotValidToken",           "ItDoesNotCare",       null,                             empty() ),
                Arguments.of( expiredJwtToken,           jwtSecretKey + "V2",   null,                             empty() ),
                Arguments.of( expiredJwtToken,           jwtSecretKey,          null,                             empty() ),
                Arguments.of( notExpiredEmptyJwtToken,   jwtSecretKey,          null,                             empty() ),
                Arguments.of( notExpiredJwtToken,        jwtSecretKey,          null,                             of(usernameValue) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getUsernameTestCases")
    @DisplayName("getUsername: test cases")
    public void getUsername_testCases(String token, String jwtSecretKey, Class<? extends Exception> expectedException, Optional<String> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> jwtUtil.getUsername(token, jwtSecretKey, "ItDoesNotCare"));
        }
        else {
            assertEquals(expectedResult, jwtUtil.getUsername(token, jwtSecretKey, "username"));
        }
    }


    static Stream<Arguments> getRolesTestCases() {
        String expiredJwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InVzZXJuYW1lIHZhbHVlIiwicm9sZXMiOlsiYWRtaW4iLCJ1c2VyIl0sIm5hbWUiOiJuYW1lI"
                               + "HZhbHVlIiwiYWdlIjoyMywiaWF0IjoxMDAwMDAwMDAwLCJleHAiOjEwMDAwMDAwMDB9.u1xbNQTk1Z_fq6FMK6qyKSmhwhU1MLvvBSMAfYM3FDs";
        String notExpiredEmptyJwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI4Y2JjY2ZlMS04ZDg5LTRmNGYtODRmMS0yMGY5MGEyNjg5ODYiLCJpY"
                                       + "XQiOjUwMDAwMDAwMDAsImV4cCI6NTAwMDAwMDAwMH0.E1OZCZ-e2nDJ9J5EoDgU7xdnjidKdv28LATNXpLJNhc";
        String notExpiredJwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InVzZXJuYW1lIHZhbHVlIiwicm9sZXMiOlsiYWRtaW4iLCJ1c2VyIl0sIm5hbWUiOiJuYW1lI"
                                  + "HZhbHVlIiwiYWdlIjoyMywiaWF0Ijo1MDAwMDAwMDAwLCJleHAiOjUwMDAwMDAwMDB9.mLy5Kf1HX20YFiFpTCz6birHbDtmMXGGw3h9Q9xMHAs";
        String jwtSecretKey = "secretKey_ForTestingPurpose@12345#";
        Set<String> rolesValue = new HashSet<>(asList("admin", "user"));
        return Stream.of(
                //@formatter:off
                //            token,                     jwtSecretKey,          expectedException,                expectedResult
                Arguments.of( null,                      "ItDoesNotCare",       IllegalArgumentException.class,   null ),
                Arguments.of( "ItDoesNotCare",           null,                  IllegalArgumentException.class,   null ),
                Arguments.of( "NotValidToken",           "ItDoesNotCare",       null,                             new HashSet<>() ),
                Arguments.of( expiredJwtToken,           jwtSecretKey + "V2",   null,                             new HashSet<>() ),
                Arguments.of( expiredJwtToken,           jwtSecretKey,          null,                             new HashSet<>() ),
                Arguments.of( notExpiredEmptyJwtToken,   jwtSecretKey,          null,                             new HashSet<>() ),
                Arguments.of( notExpiredJwtToken,        jwtSecretKey,          null,                             rolesValue )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getRolesTestCases")
    @DisplayName("getRoles: test cases")
    public void getRoles_testCases(String token, String jwtSecretKey, Class<? extends Exception> expectedException, Set<String> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> jwtUtil.getRoles(token, jwtSecretKey, "ItDoesNotCare"));
        }
        else {
            assertEquals(expectedResult, jwtUtil.getRoles(token, jwtSecretKey, "roles"));
        }
    }


    static Stream<Arguments> getInformationExceptGivenClaimsTestCases() {
        String expiredJwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InVzZXJuYW1lIHZhbHVlIiwicm9sZXMiOlsiYWRtaW4iLCJ1c2VyIl0sIm5hbWUiOiJuYW1lI"
                               + "HZhbHVlIiwiYWdlIjoyMywiaWF0IjoxMDAwMDAwMDAwLCJleHAiOjEwMDAwMDAwMDB9.u1xbNQTk1Z_fq6FMK6qyKSmhwhU1MLvvBSMAfYM3FDs";
        String notExpiredEmptyJwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI4Y2JjY2ZlMS04ZDg5LTRmNGYtODRmMS0yMGY5MGEyNjg5ODYiLCJpY"
                                       + "XQiOjUwMDAwMDAwMDAsImV4cCI6NTAwMDAwMDAwMH0.E1OZCZ-e2nDJ9J5EoDgU7xdnjidKdv28LATNXpLJNhc";
        String notExpiredJwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InVzZXJuYW1lIHZhbHVlIiwicm9sZXMiOlsiYWRtaW4iLCJ1c2VyIl0sIm5hbWUiOiJuYW1lI"
                                  + "HZhbHVlIiwiYWdlIjoyMywiaWF0Ijo1MDAwMDAwMDAwLCJleHAiOjUwMDAwMDAwMDB9.mLy5Kf1HX20YFiFpTCz6birHbDtmMXGGw3h9Q9xMHAs";
        String jwtSecretKey = "secretKey_ForTestingPurpose@12345#";
        Set<String> claimsToExclude = new HashSet<>(asList("username", "roles", "iat", "exp", "jti"));
        Map<String, Object> expectedResultClaims = new HashMap<String, Object>() {{
            put("name", "name value");
            put("age", 23);
        }};
        return Stream.of(
                //@formatter:off
                //            token,                     jwtSecretKey,          expectedException,                claimsToExclude,   expectedResult
                Arguments.of( null,                      "ItDoesNotCare",       IllegalArgumentException.class,   null,              null ),
                Arguments.of( "ItDoesNotCare",           null,                  IllegalArgumentException.class,   null,              null ),
                Arguments.of( "ItDoesNotCare",           null,                  IllegalArgumentException.class,   claimsToExclude,   null ),
                Arguments.of( "NotValidToken",           "ItDoesNotCare",       null,                             null,              new HashMap<>() ),
                Arguments.of( "NotValidToken",           "ItDoesNotCare",       null,                             claimsToExclude,   new HashMap<>() ),
                Arguments.of( expiredJwtToken,           jwtSecretKey + "V2",   null,                             claimsToExclude,   new HashMap<>() ),
                Arguments.of( expiredJwtToken,           jwtSecretKey,          null,                             claimsToExclude,   new HashMap<>() ),
                Arguments.of( notExpiredEmptyJwtToken,   jwtSecretKey,          null,                             claimsToExclude,   new HashMap<>() ),
                Arguments.of( notExpiredJwtToken,        jwtSecretKey,          null,                             claimsToExclude,   expectedResultClaims )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getInformationExceptGivenClaimsTestCases")
    @DisplayName("getInformationExceptGivenClaims: test cases")
    public void getInformationExceptGivenClaims_testCases(String token, String jwtSecretKey, Class<? extends Exception> expectedException,
                                                          Set<String> claimsToExclude, Map<String, Object> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> jwtUtil.getInformationExceptGivenClaims(token, jwtSecretKey, claimsToExclude));
        }
        else {
            assertEquals(expectedResult, jwtUtil.getInformationExceptGivenClaims(token, jwtSecretKey, claimsToExclude));
        }
    }

}
