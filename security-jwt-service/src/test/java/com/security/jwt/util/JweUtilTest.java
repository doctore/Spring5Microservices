package com.security.jwt.util;

import com.nimbusds.jose.JWSAlgorithm;
import com.security.jwt.exception.TokenInvalidException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class JweUtilTest {

    @Mock
    private JwsUtil mockJwsUtil;

    private JweUtil jweUtil;

    @BeforeEach
    public void init() {
        jweUtil = new JweUtil(mockJwsUtil);
    }


    static Stream<Arguments> generateTokenTestCases() {
        String encryptionSecret = "11111111111111111111111111111111";
        String validJwsToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InVzZXJuYW1lIHZhbHVlIiwicm9sZXMiOlsiYWRtaW4iLCJ1c2VyIl0sIm5hbWUiOiJuYW1lI"
                             + "HZhbHVlIiwiYWdlIjoyMywiaWF0Ijo1MDAwMDAwMDAwLCJleHAiOjUwMDAwMDAwMDB9.mLy5Kf1HX20YFiFpTCz6birHbDtmMXGGw3h9Q9xMHAs";
        return Stream.of(
                //@formatter:off
                //            encryptionSecret,   jwsTokenGenerated,  isValidJwsToken,   expectedException
                Arguments.of( null,               null,               false,             IllegalArgumentException.class ),
                Arguments.of( "",                 null,               false,             IllegalArgumentException.class ),
                Arguments.of( encryptionSecret,   "NotValidToken",    false,             TokenInvalidException.class ),
                Arguments.of( "NotValidSecret",   validJwsToken,      true,              IllegalArgumentException.class ),
                Arguments.of( encryptionSecret,   validJwsToken,      true,              null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("generateTokenTestCases")
    @DisplayName("generateToken: test cases")
    public void generateToken_testCases(String encryptionSecret, String jwsTokenGenerated, boolean isValidJwsToken,
                                        Class<? extends Exception> expectedException) {
        Map<String, Object> informationToInclude = new HashMap<>();
        JWSAlgorithm signatureAlgorithm = JWSAlgorithm.HS256;
        String signatureSecret = "Spring5Microservices_signatureSecret";
        long expirationTimeInSeconds = 90;

        when(mockJwsUtil.generateToken(informationToInclude, signatureAlgorithm, signatureSecret, expirationTimeInSeconds)).thenReturn(jwsTokenGenerated);
        when(mockJwsUtil.isJwsToken(jwsTokenGenerated)).thenReturn(isValidJwsToken);
        if (null != expectedException) {
            assertThrows(expectedException, () -> jweUtil.generateToken(informationToInclude, signatureAlgorithm, signatureSecret, encryptionSecret,
                    expirationTimeInSeconds));
        }
        else {
            assertNotNull(jweUtil.generateToken(informationToInclude, signatureAlgorithm, signatureSecret, encryptionSecret, expirationTimeInSeconds));
        }
    }


    static Stream<Arguments> getPayloadKeysTestCases() {
        String encryptionSecret = "11111111111111111111111111111111";
        String validJweToken = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiZGlyIn0..Y2rb2mouoXfNlhQhPc2gKQ.2M8XhDMp8"
                             + "GWau4f-h4J3FzHZ2Oo9HLS7YWShMgC1iGc0XsUwcgBXlB0KROBzHeD0CHjK7i7B47z9_KVED9lf94oSISQpz5dWfYU_aahKts"
                             + "IzpE5Z10vrLKZ0ngjqcJmo_1oSiKQxaovPJWecfBsvet_LEslwDVOj6xuMwRUwurJ7NELTVWXP746Uv_QfsF_0C4gyK9ZXjoQ"
                             + "exs61T4rR5eYzC4QZPQXimuS0RnxMPcG1qOCVFkKl0HUlJtlJx308Z5uOiSNd17GzlnkSon7yCA.plH9WSOZ2JOB8Xz8RJK4kQ";
        Map<String, Object> payloadFromJws = new HashMap<String, Object>() {{
            put("name", "name value");
            put("age", 23L);
        }};
        return Stream.of(
                //@formatter:off
                //            jweToken,          encryptionSecret,   expectedException,                payloadFromJws,   expectedResult
                Arguments.of( null,              null,               IllegalArgumentException.class,   null,             null ),
                Arguments.of( "",                null,               IllegalArgumentException.class,   null,             null ),
                Arguments.of( "NotValidToken",   null,               IllegalArgumentException.class,   null,             null ),
                Arguments.of( "NotValidToken",   encryptionSecret,   IllegalArgumentException.class,   null,             null ),
                Arguments.of( validJweToken,     "NotValidSecret",   IllegalArgumentException.class,   null,             null ),
                Arguments.of( validJweToken,     encryptionSecret,   null,                             null,             null ),
                Arguments.of( validJweToken,     encryptionSecret,   null,                             payloadFromJws,   payloadFromJws )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getPayloadKeysTestCases")
    @DisplayName("getPayloadKeys: test cases")
    public void getPayloadKeys_testCases(String jweToken, String encryptionSecret, Class<? extends Exception> expectedException,
                                         Map<String, Object> payloadFromJws, Map<String, Object> expectedResult) {
        String signatureSecret = "Spring5Microservices_signatureSecret";
        Set<String> keysToInclude = new HashSet<>();

        when(mockJwsUtil.getPayloadKeys(anyString(), eq(signatureSecret), eq(keysToInclude))).thenReturn(payloadFromJws);
        if (null != expectedException) {
            assertThrows(expectedException, () -> jweUtil.getPayloadKeys(jweToken, signatureSecret, encryptionSecret, keysToInclude));
        }
        else {
            assertEquals(expectedResult, jweUtil.getPayloadKeys(jweToken, signatureSecret, encryptionSecret, keysToInclude));
        }
    }


    static Stream<Arguments> getPayloadExceptGivenKeysTestCases() {
        String encryptionSecret = "11111111111111111111111111111111";
        String validJweToken = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiZGlyIn0..Y2rb2mouoXfNlhQhPc2gKQ.2M8XhDMp8"
                             + "GWau4f-h4J3FzHZ2Oo9HLS7YWShMgC1iGc0XsUwcgBXlB0KROBzHeD0CHjK7i7B47z9_KVED9lf94oSISQpz5dWfYU_aahKts"
                             + "IzpE5Z10vrLKZ0ngjqcJmo_1oSiKQxaovPJWecfBsvet_LEslwDVOj6xuMwRUwurJ7NELTVWXP746Uv_QfsF_0C4gyK9ZXjoQ"
                             + "exs61T4rR5eYzC4QZPQXimuS0RnxMPcG1qOCVFkKl0HUlJtlJx308Z5uOiSNd17GzlnkSon7yCA.plH9WSOZ2JOB8Xz8RJK4kQ";
        Map<String, Object> payloadFromJws = new HashMap<String, Object>() {{
            put("name", "name value");
            put("age", 23L);
        }};
        return Stream.of(
                //@formatter:off
                //            jweToken,          encryptionSecret,   expectedException,                payloadFromJws,   expectedResult
                Arguments.of( null,              null,               IllegalArgumentException.class,   null,             null ),
                Arguments.of( "",                null,               IllegalArgumentException.class,   null,             null ),
                Arguments.of( "NotValidToken",   null,               IllegalArgumentException.class,   null,             null ),
                Arguments.of( "NotValidToken",   encryptionSecret,   IllegalArgumentException.class,   null,             null ),
                Arguments.of( validJweToken,     "NotValidSecret",   IllegalArgumentException.class,   null,             null ),
                Arguments.of( validJweToken,     encryptionSecret,   null,                             null,             null ),
                Arguments.of( validJweToken,     encryptionSecret,   null,                             payloadFromJws,   payloadFromJws )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getPayloadExceptGivenKeysTestCases")
    @DisplayName("getPayloadExceptGivenKeys: test cases")
    public void getPayloadExceptGivenKeys_testCases(String jweToken, String encryptionSecret, Class<? extends Exception> expectedException,
                                                    Map<String, Object> payloadFromJws, Map<String, Object> expectedResult) {
        String signatureSecret = "Spring5Microservices_signatureSecret";
        Set<String> keysToExclude = new HashSet<>();

        when(mockJwsUtil.getPayloadExceptGivenKeys(anyString(), eq(signatureSecret), eq(keysToExclude))).thenReturn(payloadFromJws);
        if (null != expectedException) {
            assertThrows(expectedException, () -> jweUtil.getPayloadExceptGivenKeys(jweToken, signatureSecret, encryptionSecret, keysToExclude));
        }
        else {
            assertEquals(expectedResult, jweUtil.getPayloadExceptGivenKeys(jweToken, signatureSecret, encryptionSecret, keysToExclude));
        }
    }


    static Stream<Arguments> getRawPayloadTestCases() {
        String encryptionSecret = "11111111111111111111111111111111";
        String validJweToken = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiZGlyIn0..Y2rb2mouoXfNlhQhPc2gKQ.2M8XhDMp8"
                             + "GWau4f-h4J3FzHZ2Oo9HLS7YWShMgC1iGc0XsUwcgBXlB0KROBzHeD0CHjK7i7B47z9_KVED9lf94oSISQpz5dWfYU_aahKts"
                             + "IzpE5Z10vrLKZ0ngjqcJmo_1oSiKQxaovPJWecfBsvet_LEslwDVOj6xuMwRUwurJ7NELTVWXP746Uv_QfsF_0C4gyK9ZXjoQ"
                             + "exs61T4rR5eYzC4QZPQXimuS0RnxMPcG1qOCVFkKl0HUlJtlJx308Z5uOiSNd17GzlnkSon7yCA.plH9WSOZ2JOB8Xz8RJK4kQ";
        Map<String, Object> payloadFromJws = new HashMap<String, Object>() {{
            put("name", "name value");
            put("age", 23L);
        }};
        return Stream.of(
                //@formatter:off
                //            jweToken,          encryptionSecret,   expectedException,                payloadFromJws,   expectedResult
                Arguments.of( null,              null,               IllegalArgumentException.class,   null,             null ),
                Arguments.of( "",                null,               IllegalArgumentException.class,   null,             null ),
                Arguments.of( "NotValidToken",   null,               IllegalArgumentException.class,   null,             null ),
                Arguments.of( "NotValidToken",   encryptionSecret,   IllegalArgumentException.class,   null,             null ),
                Arguments.of( validJweToken,     "NotValidSecret",   IllegalArgumentException.class,   null,             null ),
                Arguments.of( validJweToken,     encryptionSecret,   null,                             null,             null ),
                Arguments.of( validJweToken,     encryptionSecret,   null,                             payloadFromJws,   payloadFromJws )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getRawPayloadTestCases")
    @DisplayName("getRawPayload: test cases")
    public void getRawPayload_testCases(String jweToken, String encryptionSecret, Class<? extends Exception> expectedException,
                                        Map<String, Object> payloadFromJws, Map<String, Object> expectedResult) {
        when(mockJwsUtil.getRawPayload(anyString())).thenReturn(payloadFromJws);
        if (null != expectedException) {
            assertThrows(expectedException, () -> jweUtil.getRawPayload(jweToken, encryptionSecret));
        }
        else {
            assertEquals(expectedResult, jweUtil.getRawPayload(jweToken, encryptionSecret));
        }
    }


    static Stream<Arguments> isJweTokenTestCases() {
        String validJweToken = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwiYWxnIjoiZGlyIn0..Y2rb2mouoXfNlhQhPc2gKQ.2M8XhDMp8"
                             + "GWau4f-h4J3FzHZ2Oo9HLS7YWShMgC1iGc0XsUwcgBXlB0KROBzHeD0CHjK7i7B47z9_KVED9lf94oSISQpz5dWfYU_aahKts"
                             + "IzpE5Z10vrLKZ0ngjqcJmo_1oSiKQxaovPJWecfBsvet_LEslwDVOj6xuMwRUwurJ7NELTVWXP746Uv_QfsF_0C4gyK9ZXjoQ"
                             + "exs61T4rR5eYzC4QZPQXimuS0RnxMPcG1qOCVFkKl0HUlJtlJx308Z5uOiSNd17GzlnkSon7yCA.plH9WSOZ2JOB8Xz8RJK4kQ";
        String notJweToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InVzZXJuYW1lIHZhbHVlIiwicm9sZXMiOlsiYWRtaW4iLCJ1c2VyIl0sIm5hbWUiOiJuYW1lI"
                           + "HZhbHVlIiwiYWdlIjoyMywiaWF0Ijo1MDAwMDAwMDAwLCJleHAiOjUwMDAwMDAwMDB9.mLy5Kf1HX20YFiFpTCz6birHbDtmMXGGw3h9Q9xMHAs";
        return Stream.of(
                //@formatter:off
                //            jweToken,             expectedException,                expectedResult
                Arguments.of( null,                 IllegalArgumentException.class,   null ),
                Arguments.of( "",                   IllegalArgumentException.class,   null ),
                Arguments.of( "ItDoesNotCare",      IllegalArgumentException.class,   null ),
                Arguments.of( notJweToken,          null,                             false ),
                Arguments.of( validJweToken,        null,                             true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("isJweTokenTestCases")
    @DisplayName("isJweToken: test cases")
    public void isJweToken_testCases(String jweToken, Class<? extends Exception> expectedException, Boolean expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> jweUtil.isJweToken(jweToken));
        }
        else {
            assertEquals(expectedResult, jweUtil.isJweToken(jweToken));
        }
    }

}
