package com.spring5microservices.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NumberUtilTest {

    static Stream<Arguments> compareToBigDecimalTestCases() {
        BigDecimal bg1 = new BigDecimal(100);
        BigDecimal bg2 = new BigDecimal(111);
        BigDecimal bg3 = new BigDecimal(100.1241);
        BigDecimal bg4 = new BigDecimal(100.1251);
        BigDecimal bg5 = new BigDecimal(100.1242);
        return Stream.of(
                //@formatter:off
                //            one,    two,    numberOfDecimals,   expectedException,                expectedResult
                Arguments.of( null,   null,   -1,                 IllegalArgumentException.class,   CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( null,   null,   0,                  null,                             CompareToResult.ZERO ),
                Arguments.of( bg1,    null,   0,                  null,                             CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( null,   bg1,    0,                  null,                             CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( bg1,    bg2,    0,                  null,                             CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( bg2,    bg1,    0,                  null,                             CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( bg3,    bg4,    3,                  null,                             CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( bg4,    bg3,    3,                  null,                             CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( bg3,    bg5,    3,                  null,                             CompareToResult.ZERO )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("compareToBigDecimalTestCases")
    @DisplayName("compareTo: BigDecimal test cases")
    public void compareToBigDecimal_testCases(BigDecimal one, BigDecimal two, int numberOfDecimals,
                                              Class<? extends Exception> expectedException, CompareToResult expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> NumberUtil.compareTo(one, two, numberOfDecimals));
        }
        else {
            int result = NumberUtil.compareTo(one, two, numberOfDecimals);
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