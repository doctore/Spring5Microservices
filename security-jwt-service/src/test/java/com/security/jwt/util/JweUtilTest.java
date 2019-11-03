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
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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


}
