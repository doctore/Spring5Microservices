package com.security.jwt.enums;

import com.security.jwt.exception.TokenExpiredException;
import com.security.jwt.exception.UnAuthorizedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.stream.Stream;

import static com.security.jwt.enums.TokenVerificationEnum.CORRECT_TOKEN;
import static com.security.jwt.enums.TokenVerificationEnum.EXPIRED_TOKEN;
import static com.security.jwt.enums.TokenVerificationEnum.INVALID_SECRET_KEY;
import static com.security.jwt.enums.TokenVerificationEnum.INVALID_TOKEN;
import static com.security.jwt.enums.TokenVerificationEnum.UNKNOWN_ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
public class TokenVerificationEnumTest {

    static Stream<Arguments> throwRelatedExceptionIfRequiredTestCases() {
        return Stream.of(
                //@formatter:off
                //            tokenVerificationEnum,   errorMessage,       expectedException
                Arguments.of( CORRECT_TOKEN,           null,               null ),
                Arguments.of( EXPIRED_TOKEN,           "Token expired",    TokenExpiredException.class ),
                Arguments.of( INVALID_SECRET_KEY,      "Invalid secret",   UnAuthorizedException.class ),
                Arguments.of( INVALID_TOKEN,           "Invalid token",    UnAuthorizedException.class ),
                Arguments.of( UNKNOWN_ERROR,           "Unknown error",    UnAuthorizedException.class )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("throwRelatedExceptionIfRequiredTestCases")
    @DisplayName("throwRelatedExceptionIfRequired: test cases")
    public void getByClientId_testCases(TokenVerificationEnum tokenVerificationEnum, String errorMessage, Class<? extends Exception> expectedException) {
        if (null != expectedException) {
            Exception launchedException = assertThrows(expectedException, () -> tokenVerificationEnum.throwRelatedExceptionIfRequired(errorMessage));
            assertEquals(errorMessage, launchedException.getMessage());
        }
        else {
            tokenVerificationEnum.throwRelatedExceptionIfRequired(errorMessage);
        }
    }

}
