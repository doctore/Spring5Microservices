package com.spring5microservices.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DateTimeUtilTest {

    static Stream<Arguments> compareLocalDateTimeTestCases() {
        LocalDateTime ldt1 = LocalDateTime.of(2020, 11, 11, 12, 31, 00);
        LocalDateTime ldt2 = LocalDateTime.of(2020, 11, 11, 12, 31, 30);
        LocalDateTime ldt3 = LocalDateTime.of(2020, 11, 11, 12, 33, 00);

        return Stream.of(
                //@formatter:off
                //            one,    two,    epsilon,   timeUnit,             expectedException,                expectedResult
                Arguments.of( null,   null,   -1,        null,                 IllegalArgumentException.class,   null ),
                Arguments.of( null,   null,   1,         null,                 null,                             CompareToResult.ZERO ),
                Arguments.of( ldt1,   null,   1,         null,                 null,                             CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( null,   ldt1,   1,         null,                 null,                             CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( ldt1,   ldt2,   0,         null,                 null,                             CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( ldt2,   ldt1,   0,         null,                 null,                             CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( ldt1,   ldt2,   29,        ChronoUnit.SECONDS,   null,                             CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( ldt1,   ldt2,   30,        ChronoUnit.SECONDS,   null,                             CompareToResult.ZERO ),
                Arguments.of( ldt1,   ldt2,   31,        ChronoUnit.SECONDS,   null,                             CompareToResult.ZERO ),
                Arguments.of( ldt2,   ldt1,   29,        ChronoUnit.SECONDS,   null,                             CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( ldt2,   ldt1,   30,        ChronoUnit.SECONDS,   null,                             CompareToResult.ZERO ),
                Arguments.of( ldt2,   ldt1,   31,        ChronoUnit.SECONDS,   null,                             CompareToResult.ZERO ),
                Arguments.of( ldt1,   ldt3,   1,         ChronoUnit.MINUTES,   null,                             CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( ldt1,   ldt3,   2,         ChronoUnit.MINUTES,   null,                             CompareToResult.ZERO ),
                Arguments.of( ldt3,   ldt1,   1,         ChronoUnit.MINUTES,   null,                             CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( ldt3,   ldt1,   2,         ChronoUnit.MINUTES,   null,                             CompareToResult.ZERO )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("compareLocalDateTimeTestCases")
    @DisplayName("compare: LocalDateTime test cases")
    public void compareLocalDateTime_testCases(LocalDateTime one, LocalDateTime two, long epsilon, ChronoUnit timeUnit,
                                               Class<? extends Exception> expectedException, CompareToResult expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> DateTimeUtil.compare(one, two, epsilon, timeUnit));
        }
        else {
            int result = DateTimeUtil.compare(one, two, epsilon, timeUnit);
            switch (expectedResult) {
                case LESS_THAN_ZERO: assertTrue(0 > result); break;
                case ZERO: assertTrue(0 == result); break;
                case GREATER_THAN_ZERO: assertTrue(0 < result); break;
            }
        }
    }


    private enum CompareToResult {
        LESS_THAN_ZERO,
        ZERO,
        GREATER_THAN_ZERO
    }

}
