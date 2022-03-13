package com.spring5microservices.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Stream;

import static com.spring5microservices.common.util.NumberUtil.fromString;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NumberUtilTest {

    static Stream<Arguments> compareBigDecimalTestCases() {
        BigDecimal bg1 = new BigDecimal(100);
        BigDecimal bg2 = new BigDecimal(111);
        BigDecimal bg3 = new BigDecimal("100.1241");
        BigDecimal bg4 = new BigDecimal("100.1251");
        BigDecimal bg5 = new BigDecimal("100.1242");
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
    @MethodSource("compareBigDecimalTestCases")
    @DisplayName("compare: BigDecimal test cases")
    public void compareBigDecimal_testCases(BigDecimal one, BigDecimal two, int numberOfDecimals,
                                            Class<? extends Exception> expectedException, CompareToResult expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> NumberUtil.compare(one, two, numberOfDecimals));
        }
        else {
            int result = NumberUtil.compare(one, two, numberOfDecimals);
            switch (expectedResult) {
                case LESS_THAN_ZERO: assertTrue(0 > result); break;
                case ZERO: assertEquals(0, result); break;
                case GREATER_THAN_ZERO: assertTrue(0 < result); break;
            }
        }
    }


    static Stream<Arguments> fromStringWithClazzTestCases() {
        return Stream.of(
                //@formatter:off
                //            potentialNumber,   clazzReturnedInstance,   expectedException,                expectedResult
                Arguments.of( null,              null,                    IllegalArgumentException.class,   null ),
                Arguments.of( "12",              null,                    IllegalArgumentException.class,   null ),
                Arguments.of( "aa",              Integer.class,           null,                             empty() ),
                Arguments.of( "12",              Integer.class,           null,                             of(12) ),
                Arguments.of( "aa",              Long.class,              null,                             empty() ),
                Arguments.of( "12",              Long.class,              null,                             of(12L) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromStringWithClazzTestCases")
    @DisplayName("fromString: providing a result class test cases")
    public <T extends Number> void fromStringWithClazz_testCases(String potentialNumber, Class<T> clazzReturnedInstance,
                                                                 Class<? extends Exception> expectedException,
                                                                 Optional<T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> fromString(potentialNumber, clazzReturnedInstance));
        }
        else {
            assertEquals(expectedResult, fromString(potentialNumber, clazzReturnedInstance));
        }
    }

    static Stream<Arguments> fromStringTestCases() {
        return Stream.of(
                //@formatter:off
                //            potentialNumber,   expectedResult
                Arguments.of( null,              empty() ),
                Arguments.of( "aa",              empty() ),
                Arguments.of( "12.1",            empty() ),
                Arguments.of( "12",              of(12) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromStringTestCases")
    @DisplayName("fromString: test cases")
    public void fromString_testCases(String potentialNumber, Optional<Integer> expectedResult) {
        assertEquals(expectedResult, fromString(potentialNumber));
    }


    private enum CompareToResult {
        LESS_THAN_ZERO,
        ZERO,
        GREATER_THAN_ZERO
    }

}