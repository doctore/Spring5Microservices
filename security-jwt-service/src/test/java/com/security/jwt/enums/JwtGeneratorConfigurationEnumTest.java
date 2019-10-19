package com.security.jwt.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class JwtGeneratorConfigurationEnumTest {


    static Stream<Arguments> getByClientIdTestCases() {
        return Stream.of(
                //@formatter:off
                //            clientId,                                                            expectedResult
                Arguments.of( null,                                                                empty() ),
                Arguments.of( "NotFoundClientId",                                                  empty() ),
                Arguments.of( JwtGeneratorConfigurationEnum.SPRING5_MICROSERVICES.getClientId(),   of(JwtGeneratorConfigurationEnum.SPRING5_MICROSERVICES) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getByClientIdTestCases")
    @DisplayName("getByClientId: test cases")
    public void getByClientId_testCases(String clientId, Optional<JwtGeneratorConfigurationEnum> expectedResult) {
        assertEquals(expectedResult, JwtGeneratorConfigurationEnum.getByClientId(clientId));
    }

}
