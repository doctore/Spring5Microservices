package com.security.jwt.util;

import io.jsonwebtoken.ExpiredJwtException;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.security.WeakKeyException;

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
        String secretKey = "Spring5Microservices_signatureSecret";
        return Stream.of(
                //@formatter:off
                //            informationToInclude,   signatureAlgorithm,   signatureSecret,   expirationTimeInSeconds,  expectedException,                isTokenGenerated
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
                                           String signatureSecret, long expirationTimeInSeconds, Class<? extends Exception> expectedException,
                                           boolean isTokenGenerated) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> jwtUtil.generateJwtToken(informationToInclude, signatureAlgorithm, signatureSecret, expirationTimeInSeconds));
        }
        else {
            assertEquals(isTokenGenerated, jwtUtil.generateJwtToken(informationToInclude, signatureAlgorithm, signatureSecret, expirationTimeInSeconds).isPresent());
        }
    }


    static Stream<Arguments> getKeyTestCases() {
        String expiredJwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6InVzZXJuYW1lIHZhbHVlIiwicm9sZXMi"
                               + "OlsiYWRtaW4iLCJ1c2VyIl0sIm5hbWUiOiJuYW1lIHZhbHVlIiwiYWdlIjoyMywiZXhwIjoxMDAwMDAwMDAwf"
                               + "Q.sWzGvgQ4WmKSZywhERz0hcDXJtMd-SU9qS_tYC3DDs8";
        String notExpiredJwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6InVzZXJuYW1lIHZhbHVlIiwicm9sZ"
                                  + "XMiOlsiYWRtaW4iLCJ1c2VyIl0sIm5hbWUiOiJuYW1lIHZhbHVlIiwiYWdlIjoyMywiZXhwIjo1MDAwMDA"
                                  + "wMDAwfQ.vho0aXdvrfGADAuYI89WU_adwocDAX3SlOKJ6i4qqCc";
        String signatureSecret = "Jwt_Secret_Password_12345_ForTest";
        return Stream.of(
                //@formatter:off
                //            token,                signatureSecret,          keyToSeach,        expectedValueClass,   expectedException,                expectedResult
                Arguments.of( null,                 "ItDoesNotCare",          "ItDoesNotCare",   null,                 IllegalArgumentException.class,   null ),
                Arguments.of( null,                 "ItDoesNotCare",          null,              null,                 null,                             empty() ),
                Arguments.of( "ItDoesNotCare",      null,                     "ItDoesNotCare",   null,                 IllegalArgumentException.class,   null ),
                Arguments.of( "ItDoesNotCare",      null,                     null,              null,                 null,                             empty() ),
                Arguments.of( "NotValidToken",      "ItDoesNotCare",          null,              null,                 null,                             empty() ),
                Arguments.of( "NotValidToken",      "ItDoesNotCare",          "ItDoesNotCare",   null,                 WeakKeyException.class,           null ),
                Arguments.of( expiredJwtToken,      signatureSecret + "V2",   "ItDoesNotCare",   null,                 SignatureException.class,         null ),
                Arguments.of( expiredJwtToken,      signatureSecret,          "ItDoesNotCare",   null,                 ExpiredJwtException.class,        null ),
                Arguments.of( notExpiredJwtToken,   signatureSecret,          "ItDoesNotCare",   null,                 null,                             empty() ),
                Arguments.of( notExpiredJwtToken,   signatureSecret,          "NotFoundKey",     String.class,         null,                             empty() ),
                Arguments.of( notExpiredJwtToken,   signatureSecret,          "username",        String.class,         null,                             of("username value") ),
                Arguments.of( notExpiredJwtToken,   signatureSecret,          "age",             Integer.class,        null,                             of(23) ),
                Arguments.of( notExpiredJwtToken,   signatureSecret,          "roles",           List.class,           null,                             of(asList("admin", "user")) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getKeyTestCases")
    @DisplayName("getKey: test cases")
    public <T> void getKey_testCases(String token, String signatureSecret, String keyToSearch, Class<T> expectedValueClass,
                                     Class<? extends Exception> expectedException, T expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> jwtUtil.getKey(token, signatureSecret, keyToSearch, expectedValueClass));
        }
        else {
            assertEquals(expectedResult, jwtUtil.getKey(token, signatureSecret, keyToSearch, expectedValueClass));
        }
    }


    static Stream<Arguments> getKeysTestCases() {
        String expiredJwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InVzZXJuYW1lIHZhbHVlIiwicm9sZXMiOlsiYWRtaW4iLCJ1c2VyIl0sIm5hbWUiOiJuYW1lI"
                               + "HZhbHVlIiwiYWdlIjoyMywiaWF0IjoxMDAwMDAwMDAwLCJleHAiOjEwMDAwMDAwMDB9.u1xbNQTk1Z_fq6FMK6qyKSmhwhU1MLvvBSMAfYM3FDs";
        String notExpiredEmptyJwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI4Y2JjY2ZlMS04ZDg5LTRmNGYtODRmMS0yMGY5MGEyNjg5ODYiLCJpY"
                                       + "XQiOjUwMDAwMDAwMDAsImV4cCI6NTAwMDAwMDAwMH0.E1OZCZ-e2nDJ9J5EoDgU7xdnjidKdv28LATNXpLJNhc";
        String notExpiredJwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InVzZXJuYW1lIHZhbHVlIiwicm9sZXMiOlsiYWRtaW4iLCJ1c2VyIl0sIm5hbWUiOiJuYW1lI"
                                  + "HZhbHVlIiwiYWdlIjoyMywiaWF0Ijo1MDAwMDAwMDAwLCJleHAiOjUwMDAwMDAwMDB9.mLy5Kf1HX20YFiFpTCz6birHbDtmMXGGw3h9Q9xMHAs";
        String signatureSecret = "secretKey_ForTestingPurpose@12345#";
        Set<String> keysToInclude = new HashSet<>(asList("username", "roles", "age"));
        Map<String, Object> expectedResultClaims = new HashMap<String, Object>() {{
            put("username", "username value");
            put("roles", asList("admin", "user"));
            put("age", 23);
        }};
        return Stream.of(
                //@formatter:off
                //            token,                     signatureSecret,          expectedException,                keysToInclude,   expectedResult
                Arguments.of( null,                      "ItDoesNotCare",          IllegalArgumentException.class,   keysToInclude,   null ),
                Arguments.of( null,                      "ItDoesNotCare",          null,                             null,            new HashMap<>() ),
                Arguments.of( "ItDoesNotCare",           null,                     IllegalArgumentException.class,   keysToInclude,   null ),
                Arguments.of( "ItDoesNotCare",           null,                     null,                             null,            new HashMap<>() ),
                Arguments.of( "ItDoesNotCare",           null,                     IllegalArgumentException.class,   keysToInclude,   null ),
                Arguments.of( "NotValidToken",           "ItDoesNotCare",          null,                             null,            new HashMap<>() ),
                Arguments.of( "NotValidToken",           "ItDoesNotCare",          WeakKeyException.class,           keysToInclude,   null ),
                Arguments.of( expiredJwtToken,           signatureSecret + "V2",   SignatureException.class,         keysToInclude,   null ),
                Arguments.of( expiredJwtToken,           signatureSecret,          ExpiredJwtException.class,        keysToInclude,   null ),
                Arguments.of( notExpiredEmptyJwtToken,   signatureSecret,          null,                             keysToInclude,   new HashMap<>() ),
                Arguments.of( notExpiredJwtToken,        signatureSecret,          null,                             null,            new HashMap<>() ),
                Arguments.of( notExpiredJwtToken,        signatureSecret,          null,                             keysToInclude,   expectedResultClaims )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getKeysTestCases")
    @DisplayName("getKeys: test cases")
    public void getKeys_testCases(String token, String signatureSecret, Class<? extends Exception> expectedException,
                                             Set<String> keysToInclude, Map<String, Object> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> jwtUtil.getKeys(token, signatureSecret, keysToInclude));
        }
        else {
            assertEquals(expectedResult, jwtUtil.getKeys(token, signatureSecret, keysToInclude));
        }
    }


    static Stream<Arguments> getExceptGivenKeysTestCases() {
        String expiredJwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InVzZXJuYW1lIHZhbHVlIiwicm9sZXMiOlsiYWRtaW4iLCJ1c2VyIl0sIm5hbWUiOiJuYW1lI"
                               + "HZhbHVlIiwiYWdlIjoyMywiaWF0IjoxMDAwMDAwMDAwLCJleHAiOjEwMDAwMDAwMDB9.u1xbNQTk1Z_fq6FMK6qyKSmhwhU1MLvvBSMAfYM3FDs";
        String notExpiredEmptyJwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI4Y2JjY2ZlMS04ZDg5LTRmNGYtODRmMS0yMGY5MGEyNjg5ODYiLCJpY"
                                       + "XQiOjUwMDAwMDAwMDAsImV4cCI6NTAwMDAwMDAwMH0.E1OZCZ-e2nDJ9J5EoDgU7xdnjidKdv28LATNXpLJNhc";
        String notExpiredJwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InVzZXJuYW1lIHZhbHVlIiwicm9sZXMiOlsiYWRtaW4iLCJ1c2VyIl0sIm5hbWUiOiJuYW1lI"
                                  + "HZhbHVlIiwiYWdlIjoyMywiaWF0Ijo1MDAwMDAwMDAwLCJleHAiOjUwMDAwMDAwMDB9.mLy5Kf1HX20YFiFpTCz6birHbDtmMXGGw3h9Q9xMHAs";
        String signatureSecret = "secretKey_ForTestingPurpose@12345#";
        Set<String> keysToExclude = new HashSet<>(asList("username", "roles", "iat", "exp", "jti"));
        Map<String, Object> expectedResultClaims = new HashMap<String, Object>() {{
            put("name", "name value");
            put("age", 23);
        }};
        return Stream.of(
                //@formatter:off
                //            token,                     signatureSecret,          expectedException,                keysToExclude,   expectedResult
                Arguments.of( null,                      "ItDoesNotCare",          IllegalArgumentException.class,   keysToExclude,   null ),
                Arguments.of( null,                      "ItDoesNotCare",          null,                             null,            new HashMap<>() ),
                Arguments.of( "ItDoesNotCare",           null,                     IllegalArgumentException.class,   keysToExclude,   null ),
                Arguments.of( "ItDoesNotCare",           null,                     null,                             null,            new HashMap<>() ),
                Arguments.of( "ItDoesNotCare",           null,                     IllegalArgumentException.class,   keysToExclude,   null ),
                Arguments.of( "NotValidToken",           "ItDoesNotCare",          null,                             null,            new HashMap<>() ),
                Arguments.of( "NotValidToken",           "ItDoesNotCare",          WeakKeyException.class,           keysToExclude,   null ),
                Arguments.of( expiredJwtToken,           signatureSecret + "V2",   SignatureException.class,         keysToExclude,   null ),
                Arguments.of( expiredJwtToken,           signatureSecret,          ExpiredJwtException.class,        keysToExclude,   null ),
                Arguments.of( notExpiredEmptyJwtToken,   signatureSecret,          null,                             keysToExclude,   new HashMap<>() ),
                Arguments.of( notExpiredJwtToken,        signatureSecret,          null,                             null,            new HashMap<>() ),
                Arguments.of( notExpiredJwtToken,        signatureSecret,          null,                             keysToExclude,   expectedResultClaims )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getExceptGivenKeysTestCases")
    @DisplayName("getExceptGivenKeys: test cases")
    public void getExceptGivenKeys_testCases(String token, String signatureSecret, Class<? extends Exception> expectedException,
                                             Set<String> keysToExclude, Map<String, Object> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> jwtUtil.getExceptGivenKeys(token, signatureSecret, keysToExclude));
        }
        else {
            assertEquals(expectedResult, jwtUtil.getExceptGivenKeys(token, signatureSecret, keysToExclude));
        }
    }

}
