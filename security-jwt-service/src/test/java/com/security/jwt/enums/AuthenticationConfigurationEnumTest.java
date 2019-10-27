package com.security.jwt.enums;

import com.security.jwt.exception.ClientNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.stream.Stream;

import static com.security.jwt.enums.AuthenticationConfigurationEnum.SPRING5_MICROSERVICES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
public class AuthenticationConfigurationEnumTest {

    static Stream<Arguments> getByClientIdTestCases() {
        return Stream.of(
                //@formatter:off
                //            clientId,                              expectedException,               expectedResult
                Arguments.of( null,                                  ClientNotFoundException.class,   null ),
                Arguments.of( "NotFoundClientId",                    ClientNotFoundException.class,   null ),
                Arguments.of( SPRING5_MICROSERVICES.getClientId(),   null,                            SPRING5_MICROSERVICES )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getByClientIdTestCases")
    @DisplayName("getByClientId: test cases")
    public void getByClientId_testCases(String clientId, Class<? extends Exception> expectedException, AuthenticationConfigurationEnum expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> AuthenticationConfigurationEnum.getByClientId(clientId));
        }
        else {
            assertEquals(expectedResult, AuthenticationConfigurationEnum.getByClientId(clientId));
        }
    }

}
