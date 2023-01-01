package com.spring5microservices.common.util;

import com.spring5microservices.common.collection.tuple.Tuple;
import com.spring5microservices.common.collection.tuple.Tuple2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.spring5microservices.common.util.HttpUtil.decodeBasicAuthentication;
import static com.spring5microservices.common.util.HttpUtil.encodeBasicAuthentication;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HttpUtilTest {

    static Stream<Arguments> encodeBasicAuthenticationTestCases() {
        String username = "user1";
        String password = "pass1";

        String expectedResultEmptyUserPass = "Basic Og==";
        String expectedResultNotEmptyUserPass = "Basic dXNlcjE6cGFzczE=";
        return Stream.of(
                //@formatter:off
                //            username,   password,   expectedException,                expectedResult
                Arguments.of( ":",        null,       IllegalArgumentException.class,   null ),
                Arguments.of( ":",        password,   IllegalArgumentException.class,   null ),
                Arguments.of( null,       null,       null,                             expectedResultEmptyUserPass ),
                Arguments.of( "",         "",         null,                             expectedResultEmptyUserPass ),
                Arguments.of( username,   password,   null,                             expectedResultNotEmptyUserPass )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("encodeBasicAuthenticationTestCases")
    @DisplayName("encodeBasicAuthentication: test cases")
    public void encodeBasicAuthentication_testCases(String username,
                                                    String password,
                                                    Class<? extends Exception> expectedException,
                                                    String expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> encodeBasicAuthentication(username, password));
        } else {
            assertEquals(expectedResult, encodeBasicAuthentication(username, password));
        }
    }


    static Stream<Arguments> decodeBasicAuthenticationTestCases() {
        String notValidPrefixEncodeBasicAuth = "Bvassic Og==";
        String notValidBase64EncodeBasicAuth = "Basic ...";
        String notFoundSeparatorEncodeBasicAuth = "Basic dGVzdA==";
        String emptyValidEncodeBasicAuth = "Basic Og==";
        String notEmptyValidEncodeBasicAuth = "Basic dXNlcjE6cGFzczE=";

        Tuple2<String, String> expectedResultEmptyUserPass = Tuple.of("", "");
        Tuple2<String, String> expectedResultNotEmptyUserPass = Tuple.of("user1", "pass1");
        return Stream.of(
                //@formatter:off
                //            encodeBasicAuth,                    expectedException,                expectedResult
                Arguments.of( null,                               IllegalArgumentException.class,   null ),
                Arguments.of( notValidPrefixEncodeBasicAuth,      IllegalArgumentException.class,   null ),
                Arguments.of( notValidBase64EncodeBasicAuth,      IllegalArgumentException.class,   null ),
                Arguments.of( notFoundSeparatorEncodeBasicAuth,   IllegalArgumentException.class,   null ),
                Arguments.of( emptyValidEncodeBasicAuth,          null,                             expectedResultEmptyUserPass ),
                Arguments.of( notEmptyValidEncodeBasicAuth,       null,                             expectedResultNotEmptyUserPass )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("decodeBasicAuthenticationTestCases")
    @DisplayName("decodeBasicAuthentication: test cases")
    public void decodeBasicAuthentication_testCases(String encodeBasicAuth,
                                                    Class<? extends Exception> expectedException,
                                                    Tuple2<String, String> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> decodeBasicAuthentication(encodeBasicAuth));
        } else {
            assertEquals(expectedResult, decodeBasicAuthentication(encodeBasicAuth));
        }
    }

}
