package com.security.jwt.util;

import com.nimbusds.jose.JWSAlgorithm;
import com.security.jwt.exception.TokenInvalidException;
import com.spring5microservices.common.exception.TokenExpiredException;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = JwsUtil.class)
public class JwsUtilTest {

    @Autowired
    private JwsUtil jwsUtil;

    static Stream<Arguments> generateTokenTestCases() {
        JWSAlgorithm signatureAlgorithm = JWSAlgorithm.HS256;
        Map<String, Object> informationToInclude = new HashMap<>();
        String signatureSecret = "Spring5Microservices_signatureSecret";
        return Stream.of(
                //@formatter:off
                //            informationToInclude,   signatureAlgorithm,   signatureSecret,   expirationTimeInSeconds,  expectedException
                Arguments.of( null,                   null,                 null,              90,                       IllegalArgumentException.class ),
                Arguments.of( null,                   null,                 "",                90,                       IllegalArgumentException.class ),
                Arguments.of( null,                   signatureAlgorithm,   "ItDoesNotCare",   90,                       IllegalArgumentException.class ),
                Arguments.of( informationToInclude,   null,                 "ItDoesNotCare",   90,                       IllegalArgumentException.class ),
                Arguments.of( informationToInclude,   signatureAlgorithm,   null,              90,                       IllegalArgumentException.class ),
                Arguments.of( informationToInclude,   signatureAlgorithm,   signatureSecret,   90,                       null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("generateTokenTestCases")
    @DisplayName("generateToken: test cases")
    public void generateToken_testCases(Map<String, Object> informationToInclude, JWSAlgorithm signatureAlgorithm,
                                        String signatureSecret, long expirationTimeInSeconds, Class<? extends Exception> expectedException) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> jwsUtil.generateToken(informationToInclude, signatureAlgorithm, signatureSecret, expirationTimeInSeconds));
        }
        else {
            assertNotNull(jwsUtil.generateToken(informationToInclude, signatureAlgorithm, signatureSecret, expirationTimeInSeconds));
        }
    }


    static Stream<Arguments> getPayloadKeysTestCases() {
        String expiredJwsToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InVzZXJuYW1lIHZhbHVlIiwicm9sZXMiOlsiYWRtaW4iLCJ1c2VyIl0sIm5hbWUiOiJuYW1lI"
                               + "HZhbHVlIiwiYWdlIjoyMywiaWF0IjoxMDAwMDAwMDAwLCJleHAiOjEwMDAwMDAwMDB9.u1xbNQTk1Z_fq6FMK6qyKSmhwhU1MLvvBSMAfYM3FDs";
        String notExpiredEmptyJwsToken = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI4Y2JjY2ZlMS04ZDg5LTRmNGYtODRmMS0yMGY5MGEyNjg5ODYiLCJpY"
                                       + "XQiOjUwMDAwMDAwMDAsImV4cCI6NTAwMDAwMDAwMH0.E1OZCZ-e2nDJ9J5EoDgU7xdnjidKdv28LATNXpLJNhc";
        String notExpiredJwsToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InVzZXJuYW1lIHZhbHVlIiwicm9sZXMiOlsiYWRtaW4iLCJ1c2VyIl0sIm5hbWUiOiJuYW1lI"
                                  + "HZhbHVlIiwiYWdlIjoyMywiaWF0Ijo1MDAwMDAwMDAwLCJleHAiOjUwMDAwMDAwMDB9.mLy5Kf1HX20YFiFpTCz6birHbDtmMXGGw3h9Q9xMHAs";
        String notJwsToken = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiZGlyIn0..B5boNIFOF9N3QKNEX8CPDA.Xd3_abfHI-5CWvQy9AiGI"
                           + "B6-1tZ_EUp5ZhrldrZrj49mX9IU7S09FXbPXTCW6r_E_DrhE1fVXoKBTbjEG2F-s-UcpGvpPOBJmQoK0qtAfuo8YlonXGHNDs8f-TtQG0E4lO"
                           + "EU3ZPGofPNxa1E-HJvs7rsYbjCsgzw5sHaLuIZDIgpES_pVYntdUHK4RlY3jHCqsu8_asM7Gxsmo-RVGPuvg._FJDglnteTQWNFbunQ0aYg";
        String signatureSecret = "secretKey_ForTestingPurpose@12345#";
        Set<String> keysToInclude = new HashSet<>(asList("username", "roles", "age"));
        Map<String, Object> expectedResultClaims = new HashMap<String, Object>() {{
            put("username", "username value");
            put("roles", new JSONArray().appendElement("admin").appendElement("user"));
            put("age", 23L);
        }};
        return Stream.of(
                //@formatter:off
                //            jwsToken,                  signatureSecret,          expectedException,                keysToInclude,   expectedResult
                Arguments.of( null,                      "ItDoesNotCare",          IllegalArgumentException.class,   keysToInclude,   null ),
                Arguments.of( "",                        "ItDoesNotCare",          IllegalArgumentException.class,   keysToInclude,   null ),
                Arguments.of( null,                      "ItDoesNotCare",          null,                             null,            new HashMap<>() ),
                Arguments.of( "ItDoesNotCare",           null,                     IllegalArgumentException.class,   keysToInclude,   null ),
                Arguments.of( "ItDoesNotCare",           null,                     null,                             null,            new HashMap<>() ),
                Arguments.of( "ItDoesNotCare",           null,                     IllegalArgumentException.class,   keysToInclude,   null ),
                Arguments.of( "NotValidToken",           "ItDoesNotCare",          null,                             null,            new HashMap<>() ),
                Arguments.of( "NotValidToken",           "ItDoesNotCare",          IllegalArgumentException.class,   keysToInclude,   null ),
                Arguments.of( expiredJwsToken,           signatureSecret + "V2",   TokenInvalidException.class,      keysToInclude,   null ),
                Arguments.of( notJwsToken,               signatureSecret,          TokenInvalidException.class,      keysToInclude,   null ),
                Arguments.of( expiredJwsToken,           signatureSecret,          TokenExpiredException.class,      keysToInclude,   null ),
                Arguments.of( notExpiredEmptyJwsToken,   signatureSecret,          null,                             keysToInclude,   new HashMap<>() ),
                Arguments.of( notExpiredJwsToken,        signatureSecret,          null,                             null,            new HashMap<>() ),
                Arguments.of( notExpiredJwsToken,        signatureSecret,          null,                             keysToInclude,   expectedResultClaims )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getPayloadKeysTestCases")
    @DisplayName("getPayloadKeys: test cases")
    public void getPayloadKeys_testCases(String jwsToken, String signatureSecret, Class<? extends Exception> expectedException,
                                         Set<String> keysToInclude, Map<String, Object> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> jwsUtil.getPayloadKeys(jwsToken, signatureSecret, keysToInclude));
        }
        else {
            assertEquals(expectedResult, jwsUtil.getPayloadKeys(jwsToken, signatureSecret, keysToInclude));
        }
    }


    static Stream<Arguments> getPayloadExceptGivenKeysTestCases() {
        String expiredJwsToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InVzZXJuYW1lIHZhbHVlIiwicm9sZXMiOlsiYWRtaW4iLCJ1c2VyIl0sIm5hbWUiOiJuYW1lI"
                               + "HZhbHVlIiwiYWdlIjoyMywiaWF0IjoxMDAwMDAwMDAwLCJleHAiOjEwMDAwMDAwMDB9.u1xbNQTk1Z_fq6FMK6qyKSmhwhU1MLvvBSMAfYM3FDs";
        String notExpiredEmptyJwsToken = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI4Y2JjY2ZlMS04ZDg5LTRmNGYtODRmMS0yMGY5MGEyNjg5ODYiLCJpY"
                                       + "XQiOjUwMDAwMDAwMDAsImV4cCI6NTAwMDAwMDAwMH0.E1OZCZ-e2nDJ9J5EoDgU7xdnjidKdv28LATNXpLJNhc";
        String notExpiredJwsToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InVzZXJuYW1lIHZhbHVlIiwicm9sZXMiOlsiYWRtaW4iLCJ1c2VyIl0sIm5hbWUiOiJuYW1lI"
                                  + "HZhbHVlIiwiYWdlIjoyMywiaWF0Ijo1MDAwMDAwMDAwLCJleHAiOjUwMDAwMDAwMDB9.mLy5Kf1HX20YFiFpTCz6birHbDtmMXGGw3h9Q9xMHAs";
        String notJwsToken = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiZGlyIn0..B5boNIFOF9N3QKNEX8CPDA.Xd3_abfHI-5CWvQy9AiGI"
                           + "B6-1tZ_EUp5ZhrldrZrj49mX9IU7S09FXbPXTCW6r_E_DrhE1fVXoKBTbjEG2F-s-UcpGvpPOBJmQoK0qtAfuo8YlonXGHNDs8f-TtQG0E4lO"
                           + "EU3ZPGofPNxa1E-HJvs7rsYbjCsgzw5sHaLuIZDIgpES_pVYntdUHK4RlY3jHCqsu8_asM7Gxsmo-RVGPuvg._FJDglnteTQWNFbunQ0aYg";
        String signatureSecret = "secretKey_ForTestingPurpose@12345#";
        Set<String> keysToExclude = new HashSet<>(asList("username", "roles", "iat", "exp", "jti"));
        Map<String, Object> expectedResultClaims = new HashMap<String, Object>() {{
            put("name", "name value");
            put("age", 23L);
        }};
        return Stream.of(
                //@formatter:off
                //            jwsToken,                  signatureSecret,          expectedException,                keysToExclude,   expectedResult
                Arguments.of( null,                      "ItDoesNotCare",          IllegalArgumentException.class,   keysToExclude,   null ),
                Arguments.of( "",                        "ItDoesNotCare",          IllegalArgumentException.class,   keysToExclude,   null ),
                Arguments.of( null,                      "ItDoesNotCare",          null,                             null,            new HashMap<>() ),
                Arguments.of( "ItDoesNotCare",           null,                     IllegalArgumentException.class,   keysToExclude,   null ),
                Arguments.of( "ItDoesNotCare",           null,                     null,                             null,            new HashMap<>() ),
                Arguments.of( "ItDoesNotCare",           null,                     IllegalArgumentException.class,   keysToExclude,   null ),
                Arguments.of( "NotValidToken",           "ItDoesNotCare",          null,                             null,            new HashMap<>() ),
                Arguments.of( "NotValidToken",           "ItDoesNotCare",          IllegalArgumentException.class,   keysToExclude,   null ),
                Arguments.of( expiredJwsToken,           signatureSecret + "V2",   TokenInvalidException.class,      keysToExclude,   null ),
                Arguments.of( notJwsToken,               signatureSecret,          TokenInvalidException.class,      keysToExclude,   null ),
                Arguments.of( expiredJwsToken,           signatureSecret,          TokenExpiredException.class,      keysToExclude,   null ),
                Arguments.of( notExpiredEmptyJwsToken,   signatureSecret,          null,                             keysToExclude,   new HashMap<>() ),
                Arguments.of( notExpiredJwsToken,        signatureSecret,          null,                             null,            new HashMap<>() ),
                Arguments.of( notExpiredJwsToken,        signatureSecret,          null,                             keysToExclude,   expectedResultClaims )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getPayloadExceptGivenKeysTestCases")
    @DisplayName("getPayloadExceptGivenKeys: test cases")
    public void getPayloadExceptGivenKeys_testCases(String jwsToken, String signatureSecret, Class<? extends Exception> expectedException,
                                                    Set<String> keysToExclude, Map<String, Object> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> jwsUtil.getPayloadExceptGivenKeys(jwsToken, signatureSecret, keysToExclude));
        }
        else {
            assertEquals(expectedResult, jwsUtil.getPayloadExceptGivenKeys(jwsToken, signatureSecret, keysToExclude));
        }
    }


    static Stream<Arguments> getRawPayloadTestCases() {
        String expiredJwsToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InVzZXJuYW1lIHZhbHVlIiwicm9sZXMiOlsiYWRtaW4iLCJ1c2VyIl0sIm5hbWUiOiJuYW1lI"
                               + "HZhbHVlIiwiYWdlIjoyMywiaWF0IjoxMDAwMDAwMDAwLCJleHAiOjEwMDAwMDAwMDB9.u1xbNQTk1Z_fq6FMK6qyKSmhwhU1MLvvBSMAfYM3FDs";
        String notExpiredJwsToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InVzZXJuYW1lIHZhbHVlIiwicm9sZXMiOlsiYWRtaW4iLCJ1c2VyIl0sIm5hbWUiOiJuYW1lI"
                                  + "HZhbHVlIiwiYWdlIjoyMywiaWF0Ijo1MDAwMDAwMDAwLCJleHAiOjUwMDAwMDAwMDB9.mLy5Kf1HX20YFiFpTCz6birHbDtmMXGGw3h9Q9xMHAs";
        String notJwsToken = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiZGlyIn0..B5boNIFOF9N3QKNEX8CPDA.Xd3_abfHI-5CWvQy9AiGI"
                           + "B6-1tZ_EUp5ZhrldrZrj49mX9IU7S09FXbPXTCW6r_E_DrhE1fVXoKBTbjEG2F-s-UcpGvpPOBJmQoK0qtAfuo8YlonXGHNDs8f-TtQG0E4lO"
                           + "EU3ZPGofPNxa1E-HJvs7rsYbjCsgzw5sHaLuIZDIgpES_pVYntdUHK4RlY3jHCqsu8_asM7Gxsmo-RVGPuvg._FJDglnteTQWNFbunQ0aYg";
        Map<String, Object> expectedExpiredTokenClaims = new HashMap<String, Object>() {{
            put("username", "username value");
            put("name", "name value");
            put("roles", new JSONArray().appendElement("admin").appendElement("user"));
            put("age", 23L);
            put("iat", new Date((long)1000000000*1000));
            put("exp", new Date((long)1000000000*1000));
        }};
        Map<String, Object> expectedNotExpiredTokenClaims = new HashMap<>(expectedExpiredTokenClaims);
        expectedNotExpiredTokenClaims.put("iat", new Date((long)5000000000L*1000));
        expectedNotExpiredTokenClaims.put("exp", new Date((long)5000000000L*1000));
        return Stream.of(
                //@formatter:off
                //            jwsToken,             expectedException,                expectedResult
                Arguments.of( null,                 IllegalArgumentException.class,   null ),
                Arguments.of( "",                   IllegalArgumentException.class,   null ),
                Arguments.of( "ItDoesNotCare",      IllegalArgumentException.class,   null ),
                Arguments.of( notJwsToken,          TokenInvalidException.class,      null ),
                Arguments.of( expiredJwsToken,      null,                             expectedExpiredTokenClaims ),
                Arguments.of( notExpiredJwsToken,   null,                             expectedNotExpiredTokenClaims )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getRawPayloadTestCases")
    @DisplayName("getRawPayload: test cases")
    public void getRawPayload_testCases(String jwsToken, Class<? extends Exception> expectedException, Map<String, Object> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> jwsUtil.getRawPayload(jwsToken));
        }
        else {
            assertEquals(expectedResult, jwsUtil.getRawPayload(jwsToken));
        }
    }


    static Stream<Arguments> isJwsTokenTestCases() {
        String expiredJwsToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InVzZXJuYW1lIHZhbHVlIiwicm9sZXMiOlsiYWRtaW4iLCJ1c2VyIl0sIm5hbWUiOiJuYW1lI"
                               + "HZhbHVlIiwiYWdlIjoyMywiaWF0IjoxMDAwMDAwMDAwLCJleHAiOjEwMDAwMDAwMDB9.u1xbNQTk1Z_fq6FMK6qyKSmhwhU1MLvvBSMAfYM3FDs";
        String notExpiredJwsToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InVzZXJuYW1lIHZhbHVlIiwicm9sZXMiOlsiYWRtaW4iLCJ1c2VyIl0sIm5hbWUiOiJuYW1lI"
                                  + "HZhbHVlIiwiYWdlIjoyMywiaWF0Ijo1MDAwMDAwMDAwLCJleHAiOjUwMDAwMDAwMDB9.mLy5Kf1HX20YFiFpTCz6birHbDtmMXGGw3h9Q9xMHAs";
        String notJwsToken = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiZGlyIn0..B5boNIFOF9N3QKNEX8CPDA.Xd3_abfHI-5CWvQy9AiGI"
                           + "B6-1tZ_EUp5ZhrldrZrj49mX9IU7S09FXbPXTCW6r_E_DrhE1fVXoKBTbjEG2F-s-UcpGvpPOBJmQoK0qtAfuo8YlonXGHNDs8f-TtQG0E4lO"
                           + "EU3ZPGofPNxa1E-HJvs7rsYbjCsgzw5sHaLuIZDIgpES_pVYntdUHK4RlY3jHCqsu8_asM7Gxsmo-RVGPuvg._FJDglnteTQWNFbunQ0aYg";
        return Stream.of(
                //@formatter:off
                //            jwsToken,             expectedException,                expectedResult
                Arguments.of( null,                 IllegalArgumentException.class,   null ),
                Arguments.of( "",                   IllegalArgumentException.class,   null ),
                Arguments.of( "ItDoesNotCare",      IllegalArgumentException.class,   null ),
                Arguments.of( notJwsToken,          null,                             false ),
                Arguments.of( expiredJwsToken,      null,                             true ),
                Arguments.of( notExpiredJwsToken,   null,                             true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("isJwsTokenTestCases")
    @DisplayName("isJwsToken: test cases")
    public void isJwsToken_testCases(String jwsToken, Class<? extends Exception> expectedException, Boolean expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> jwsUtil.isJwsToken(jwsToken));
        }
        else {
            assertEquals(expectedResult, jwsUtil.isJwsToken(jwsToken));
        }
    }

}
