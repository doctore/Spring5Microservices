package com.spring5microservices.common.util;

import com.spring5microservices.common.util.either.Either;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.stream.Stream;

import static com.spring5microservices.common.util.NumberUtil.fromString;
import static com.spring5microservices.common.util.either.Either.left;
import static com.spring5microservices.common.util.either.Either.right;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NumberUtilTest {

    static Stream<Arguments> compareBigDecimalDefaultRoundingModeTestCases() {
        BigDecimal bg1 = new BigDecimal(100);
        BigDecimal bg2 = new BigDecimal(111);
        BigDecimal bg3 = new BigDecimal("100.1241");
        BigDecimal bg4 = new BigDecimal("100.1251");
        BigDecimal bg5 = new BigDecimal("100.1242");
        return Stream.of(
                //@formatter:off
                //            one,    two,    numberOfDecimals,   expectedException,                expectedResult
                Arguments.of( null,   null,   -1,                 IllegalArgumentException.class,   null ),
                Arguments.of( bg1,    null,   -1,                 IllegalArgumentException.class,   null ),
                Arguments.of( bg1,    bg2,    -1,                 IllegalArgumentException.class,   null ),
                Arguments.of( null,   null,    0,                 null,                             CompareToResult.ZERO ),
                Arguments.of( bg1,    null,    0,                 null,                             CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( null,   bg1,     0,                 null,                             CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( bg1,    bg2,     0,                 null,                             CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( bg2,    bg1,     0,                 null,                             CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( bg3,    bg4,     3,                 null,                             CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( bg4,    bg3,     3,                 null,                             CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( bg3,    bg5,     3,                 null,                             CompareToResult.ZERO )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("compareBigDecimalDefaultRoundingModeTestCases")
    @DisplayName("compare: BigDecimal using default RoundingMode test cases")
    public void compareBigDecimalDefaultRoundingMode_testCases(BigDecimal one,
                                                               BigDecimal two,
                                                               int numberOfDecimals,
                                                               Class<? extends Exception> expectedException,
                                                               CompareToResult expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> NumberUtil.compare(one, two, numberOfDecimals));
        } else {
            int result = NumberUtil.compare(one, two, numberOfDecimals);
            switch (expectedResult) {
                case LESS_THAN_ZERO -> assertTrue(0 > result);
                case ZERO -> assertEquals(0, result);
                case GREATER_THAN_ZERO -> assertTrue(0 < result);
            }
        }
    }


    static Stream<Arguments> compareBigDecimalAllParametersTestCases() {
        BigDecimal bg1 = new BigDecimal(100);
        BigDecimal bg2 = new BigDecimal(111);
        BigDecimal bg3 = new BigDecimal("100.124");
        BigDecimal bg4 = new BigDecimal("100.125");
        BigDecimal bg5 = new BigDecimal("100.126");
        return Stream.of(
                //@formatter:off
                //            one,    two,    numberOfDecimals,   roundingMode,             expectedException,                expectedResult
                Arguments.of( null,   null,   -1,                 null,                     IllegalArgumentException.class,   null ),
                Arguments.of( bg1,    null,   -1,                 null,                     IllegalArgumentException.class,   null ),
                Arguments.of( bg1,    bg2,    -1,                 null,                     IllegalArgumentException.class,   null ),
                Arguments.of( null,   null,    0,                 null,                     null,                             CompareToResult.ZERO ),
                Arguments.of( bg1,    null,    0,                 null,                     null,                             CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( null,   bg1,     0,                 null,                     null,                             CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( bg1,    bg2,     0,                 null,                     null,                             CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( bg2,    bg1,     0,                 null,                     null,                             CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( bg3,    bg4,     3,                 null,                     null,                             CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( bg4,    bg3,     3,                 null,                     null,                             CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( bg3,    bg4,     2,                 RoundingMode.HALF_DOWN,   null,                             CompareToResult.ZERO ),
                Arguments.of( bg4,    bg5,     2,                 RoundingMode.HALF_DOWN,   null,                             CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( bg5,    bg4,     2,                 RoundingMode.HALF_DOWN,   null,                             CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( bg3,    bg5,     2,                 RoundingMode.DOWN,        null,                             CompareToResult.ZERO ),
                Arguments.of( bg5,    bg3,     2,                 RoundingMode.DOWN,        null,                             CompareToResult.ZERO ),
                Arguments.of( bg3,    bg5,     2,                 RoundingMode.UP,          null,                             CompareToResult.ZERO ),
                Arguments.of( bg5,    bg3,     2,                 RoundingMode.UP,          null,                             CompareToResult.ZERO )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("compareBigDecimalAllParametersTestCases")
    @DisplayName("compare: BigDecimal with all parameters test cases")
    public void compareBigDecimalAllParameters_testCases(BigDecimal one,
                                                         BigDecimal two,
                                                         int numberOfDecimals,
                                                         RoundingMode roundingMode,
                                                         Class<? extends Exception> expectedException,
                                                         CompareToResult expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> NumberUtil.compare(one, two, numberOfDecimals, roundingMode));
        } else {
            int result = NumberUtil.compare(one, two, numberOfDecimals, roundingMode);
            switch (expectedResult) {
                case LESS_THAN_ZERO -> assertTrue(0 > result);
                case ZERO -> assertEquals(0, result);
                case GREATER_THAN_ZERO -> assertTrue(0 < result);
            }
        }
    }


    static Stream<Arguments> fromStringTestCases() {
        String errorMessageNoNumber = "There was an error trying to convert the string: aa to an instance of: java.lang.Integer. "
                + "The cause was: java.lang.NumberFormatException with message: For input string: \"aa\"";
        String errorMessageNoInteger = "There was an error trying to convert the string: 12.1 to an instance of: java.lang.Integer. "
                + "The cause was: java.lang.NumberFormatException with message: For input string: \"12.1\"";
        return Stream.of(
                //@formatter:off
                //            potentialNumber,   expectedResult
                Arguments.of( null,              right(empty()) ),
                Arguments.of( "aa",              left(errorMessageNoNumber) ),
                Arguments.of( "12.1",            left(errorMessageNoInteger) ),
                Arguments.of( "12",              right(of(12)) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromStringTestCases")
    @DisplayName("fromString: test cases")
    public void fromString_testCases(String potentialNumber,
                                     Either<String, Optional<Integer>> expectedResult) {
        assertEquals(expectedResult, fromString(potentialNumber));
    }


    static Stream<Arguments> fromStringWithClazzTestCases() {
        String errorMessageNoNumber = "There was an error trying to convert the string: aa to an instance of: java.lang.Integer. "
                + "The cause was: java.lang.NumberFormatException with message: For input string: \"aa\"";
        String errorMessageNoInteger = "There was an error trying to convert the string: 12.1 to an instance of: java.lang.Integer. "
                + "The cause was: java.lang.NumberFormatException with message: For input string: \"12.1\"";
        return Stream.of(
                //@formatter:off
                //            potentialNumber,   clazzReturnedInstance,   expectedResult
                Arguments.of( null,              null,                    right(empty()) ),
                Arguments.of( "aa",              null,                    left(errorMessageNoNumber) ),
                Arguments.of( "aa",              Integer.class,           left(errorMessageNoNumber) ),
                Arguments.of( "12",              null,                    right(of(12)) ),
                Arguments.of( "12",              Integer.class,           right(of(12)) ),
                Arguments.of( "12",              Long.class,              right(of(12L)) ),
                Arguments.of( "12.1",            null,                    left(errorMessageNoInteger) ),
                Arguments.of( "12.1",            Integer.class,           left(errorMessageNoInteger) ),
                Arguments.of( "12.1",            Double.class,            right(of(12.1D)) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromStringWithClazzTestCases")
    @DisplayName("fromString: providing a result class test cases")
    public <T extends Number> void fromStringWithClazz_testCases(String potentialNumber,
                                                                 Class<T> clazzReturnedInstance,
                                                                 Either<String, Optional<Integer>> expectedResult) {
        assertEquals(expectedResult, fromString(potentialNumber, clazzReturnedInstance));
    }


    private enum CompareToResult {
        LESS_THAN_ZERO,
        ZERO,
        GREATER_THAN_ZERO
    }

}