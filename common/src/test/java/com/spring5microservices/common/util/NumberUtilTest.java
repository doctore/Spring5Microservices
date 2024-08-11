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
import static com.spring5microservices.common.util.NumberUtil.toByte;
import static com.spring5microservices.common.util.NumberUtil.toDouble;
import static com.spring5microservices.common.util.NumberUtil.toFloat;
import static com.spring5microservices.common.util.NumberUtil.toInteger;
import static com.spring5microservices.common.util.NumberUtil.toLong;
import static com.spring5microservices.common.util.NumberUtil.toShort;
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
            assertThrows(
                    expectedException,
                    () -> NumberUtil.compare(one, two, numberOfDecimals)
            );
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
            assertThrows(
                    expectedException,
                    () -> NumberUtil.compare(one, two, numberOfDecimals, roundingMode)
            );
        } else {
            int result = NumberUtil.compare(one, two, numberOfDecimals, roundingMode);
            switch (expectedResult) {
                case LESS_THAN_ZERO -> assertTrue(0 > result);
                case ZERO -> assertEquals(0, result);
                case GREATER_THAN_ZERO -> assertTrue(0 < result);
            }
        }
    }


    static Stream<Arguments> fromStringWithoutClassInstanceTestCases() {
        String errorMessageNoNumber = "There was an error trying to convert the string: aa to an instance of: java.lang.Integer. "
                + "The cause was: java.lang.NumberFormatException with message: For input string: \"aa\"";
        String errorMessageNoInteger = "There was an error trying to convert the string: 12.1 to an instance of: java.lang.Integer. "
                + "The cause was: java.lang.NumberFormatException with message: For input string: \"12.1\"";
        return Stream.of(
                //@formatter:off
                //            potentialNumber,   expectedResult
                Arguments.of( null,              right(empty()) ),
                Arguments.of( "",                right(empty()) ),
                Arguments.of( "  ",              right(empty()) ),
                Arguments.of( "aa",              left(errorMessageNoNumber) ),
                Arguments.of( "12.1",            left(errorMessageNoInteger) ),
                Arguments.of( "12",              right(of(12)) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromStringWithoutClassInstanceTestCases")
    @DisplayName("fromString: without class instance test cases")
    public void fromStringWithoutClassInstanceToReturn_testCases(String potentialNumber,
                                                                 Either<String, Optional<Integer>> expectedResult) {
        assertEquals(
                expectedResult,
                fromString(potentialNumber)
        );
    }


    static Stream<Arguments> fromStringAllParametersTestCases() {
        String errorMessageNoNumber = "There was an error trying to convert the string: aa to an instance of: java.lang.Integer. "
                + "The cause was: java.lang.NumberFormatException with message: For input string: \"aa\"";
        String errorMessageNoInteger = "There was an error trying to convert the string: 12.1 to an instance of: java.lang.Integer. "
                + "The cause was: java.lang.NumberFormatException with message: For input string: \"12.1\"";
        return Stream.of(
                //@formatter:off
                //            potentialNumber,   clazzReturnedInstance,   expectedResult
                Arguments.of( null,              null,                    right(empty()) ),
                Arguments.of( null,              Integer.class,           right(empty()) ),
                Arguments.of( "",                null,                    right(empty()) ),
                Arguments.of( "",                Long.class,              right(empty()) ),
                Arguments.of( "  ",              null,                    right(empty()) ),
                Arguments.of( "  ",              Double.class,            right(empty()) ),
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
    @MethodSource("fromStringAllParametersTestCases")
    @DisplayName("fromString: with all parameters test cases")
    public <T extends Number> void fromStringAllParameters_testCases(String potentialNumber,
                                                                     Class<T> clazzReturnedInstance,
                                                                     Either<String, Optional<Integer>> expectedResult) {
        assertEquals(
                expectedResult,
                fromString(potentialNumber, clazzReturnedInstance)
        );
    }


    static Stream<Arguments> toByteWithoutDefaultValueTestCases() {
        Byte expectedIfNoConversion = (byte) 0;
        return Stream.of(
                //@formatter:off
                //            potentialNumber,   expectedResult
                Arguments.of( null,              expectedIfNoConversion ),
                Arguments.of( "",                expectedIfNoConversion ),
                Arguments.of( "  ",              expectedIfNoConversion ),
                Arguments.of( "aa",              expectedIfNoConversion ),
                Arguments.of( "21",              (byte) 21 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toByteWithoutDefaultValueTestCases")
    @DisplayName("toByte: without default value test cases")
    public void toByteWithoutDefaultValue_testCases(String potentialNumber,
                                                    Byte expectedResult) {
        assertEquals(
                expectedResult,
                toByte(potentialNumber)
        );
    }


    static Stream<Arguments> toByteAllParametersTestCases() {
        byte defaultValue = (byte) 11;
        return Stream.of(
                //@formatter:off
                //            potentialNumber,   defaultValue,   expectedResult
                Arguments.of( null,              defaultValue,   defaultValue ),
                Arguments.of( "",                defaultValue,   defaultValue ),
                Arguments.of( "  ",              defaultValue,   defaultValue ),
                Arguments.of( "aa",              defaultValue,   defaultValue ),
                Arguments.of( "50",              defaultValue,   (byte) 50 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toByteAllParametersTestCases")
    @DisplayName("toByte: with all parameters test cases")
    public void toByteAllParameters_testCases(String potentialNumber,
                                              byte defaultValue,
                                              Byte expectedResult) {
        assertEquals(
                expectedResult,
                toByte(potentialNumber, defaultValue)
        );
    }


    static Stream<Arguments> toDoubleWithoutDefaultValueTestCases() {
        Double expectedIfNoConversion = 0.0d;
        return Stream.of(
                //@formatter:off
                //            potentialNumber,   expectedResult
                Arguments.of( null,              expectedIfNoConversion ),
                Arguments.of( "",                expectedIfNoConversion ),
                Arguments.of( "  ",              expectedIfNoConversion ),
                Arguments.of( "aa",              expectedIfNoConversion ),
                Arguments.of( "321.3",           321.3d )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toDoubleWithoutDefaultValueTestCases")
    @DisplayName("toDouble: without default value test cases")
    public void toDoubleWithoutDefaultValue_testCases(String potentialNumber,
                                                      Double expectedResult) {
        assertEquals(
                expectedResult,
                toDouble(potentialNumber)
        );
    }


    static Stream<Arguments> toDoubleAllParametersTestCases() {
        double defaultValue = 11.1d;
        return Stream.of(
                //@formatter:off
                //            potentialNumber,   defaultValue,   expectedResult
                Arguments.of( null,              defaultValue,   defaultValue ),
                Arguments.of( "",                defaultValue,   defaultValue ),
                Arguments.of( "  ",              defaultValue,   defaultValue ),
                Arguments.of( "aa",              defaultValue,   defaultValue ),
                Arguments.of( "50.2",            defaultValue,   50.2d )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toDoubleAllParametersTestCases")
    @DisplayName("toDouble: with all parameters test cases")
    public void toDoubleAllParameters_testCases(String potentialNumber,
                                                double defaultValue,
                                                Double expectedResult) {
        assertEquals(
                expectedResult,
                toDouble(potentialNumber, defaultValue)
        );
    }


    static Stream<Arguments> toFloatWithoutDefaultValueTestCases() {
        Float expectedIfNoConversion = 0.0f;
        return Stream.of(
                //@formatter:off
                //            potentialNumber,   expectedResult
                Arguments.of( null,              expectedIfNoConversion ),
                Arguments.of( "",                expectedIfNoConversion ),
                Arguments.of( "  ",              expectedIfNoConversion ),
                Arguments.of( "aa",              expectedIfNoConversion ),
                Arguments.of( "321.9",           321.9f )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toFloatWithoutDefaultValueTestCases")
    @DisplayName("toFloat: without default value test cases")
    public void toFloatWithoutDefaultValue_testCases(String potentialNumber,
                                                     Float expectedResult) {
        assertEquals(
                expectedResult,
                toFloat(potentialNumber)
        );
    }


    static Stream<Arguments> toFloatAllParametersTestCases() {
        float defaultValue = 11.1f;
        return Stream.of(
                //@formatter:off
                //            potentialNumber,   defaultValue,   expectedResult
                Arguments.of( null,              defaultValue,   defaultValue ),
                Arguments.of( "",                defaultValue,   defaultValue ),
                Arguments.of( "  ",              defaultValue,   defaultValue ),
                Arguments.of( "aa",              defaultValue,   defaultValue ),
                Arguments.of( "50.4",            defaultValue,   50.4f )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toFloatAllParametersTestCases")
    @DisplayName("toFloat: with all parameters test cases")
    public void toFloatAllParameters_testCases(String potentialNumber,
                                               float defaultValue,
                                               Float expectedResult) {
        assertEquals(
                expectedResult,
                toFloat(potentialNumber, defaultValue)
        );
    }


    static Stream<Arguments> toIntegerWithoutDefaultValueTestCases() {
        Integer expectedIfNoConversion = 0;
        return Stream.of(
                //@formatter:off
                //            potentialNumber,   expectedResult
                Arguments.of( null,              expectedIfNoConversion ),
                Arguments.of( "",                expectedIfNoConversion ),
                Arguments.of( "  ",              expectedIfNoConversion ),
                Arguments.of( "aa",              expectedIfNoConversion ),
                Arguments.of( "321",             321 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toIntegerWithoutDefaultValueTestCases")
    @DisplayName("toInteger: without default value test cases")
    public void toIntegerWithoutDefaultValue_testCases(String potentialNumber,
                                                       Integer expectedResult) {
        assertEquals(
                expectedResult,
                toInteger(potentialNumber)
        );
    }


    static Stream<Arguments> toIntegerAllParametersTestCases() {
        int defaultValue = 11;
        return Stream.of(
                //@formatter:off
                //            potentialNumber,   defaultValue,   expectedResult
                Arguments.of( null,              defaultValue,   defaultValue ),
                Arguments.of( "",                defaultValue,   defaultValue ),
                Arguments.of( "  ",              defaultValue,   defaultValue ),
                Arguments.of( "aa",              defaultValue,   defaultValue ),
                Arguments.of( "50",              defaultValue,   50 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toIntegerAllParametersTestCases")
    @DisplayName("toInteger: with all parameters test cases")
    public void toIntegerAllParameters_testCases(String potentialNumber,
                                                 int defaultValue,
                                                 Integer expectedResult) {
        assertEquals(
                expectedResult,
                toInteger(potentialNumber, defaultValue)
        );
    }


    static Stream<Arguments> toLongWithoutDefaultValueTestCases() {
        Long expectedIfNoConversion = 0L;
        return Stream.of(
                //@formatter:off
                //            potentialNumber,   expectedResult
                Arguments.of( null,              expectedIfNoConversion ),
                Arguments.of( "",                expectedIfNoConversion ),
                Arguments.of( "  ",              expectedIfNoConversion ),
                Arguments.of( "aa",              expectedIfNoConversion ),
                Arguments.of( "321",             321L )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toLongWithoutDefaultValueTestCases")
    @DisplayName("toLong: without default value test cases")
    public void toLongWithoutDefaultValue_testCases(String potentialNumber,
                                                    Long expectedResult) {
        assertEquals(
                expectedResult,
                toLong(potentialNumber)
        );
    }


    static Stream<Arguments> toLongAllParametersTestCases() {
        long defaultValue = 11;
        return Stream.of(
                //@formatter:off
                //            potentialNumber,   defaultValue,   expectedResult
                Arguments.of( null,              defaultValue,   defaultValue ),
                Arguments.of( "",                defaultValue,   defaultValue ),
                Arguments.of( "  ",              defaultValue,   defaultValue ),
                Arguments.of( "aa",              defaultValue,   defaultValue ),
                Arguments.of( "50",              defaultValue,   50L )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toLongAllParametersTestCases")
    @DisplayName("toLong: with all parameters test cases")
    public void toLongAllParameters_testCases(String potentialNumber,
                                              long defaultValue,
                                              Long expectedResult) {
        assertEquals(
                expectedResult,
                toLong(potentialNumber, defaultValue)
        );
    }


    static Stream<Arguments> toShortWithoutDefaultValueTestCases() {
        Short expectedIfNoConversion = (short) 0;
        return Stream.of(
                //@formatter:off
                //            potentialNumber,   expectedResult
                Arguments.of( null,              expectedIfNoConversion ),
                Arguments.of( "",                expectedIfNoConversion ),
                Arguments.of( "  ",              expectedIfNoConversion ),
                Arguments.of( "aa",              expectedIfNoConversion ),
                Arguments.of( "88",              (short) 88 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toShortWithoutDefaultValueTestCases")
    @DisplayName("toShort: without default value test cases")
    public void toShortWithoutDefaultValue_testCases(String potentialNumber,
                                                     Short expectedResult) {
        assertEquals(
                expectedResult,
                toShort(potentialNumber)
        );
    }


    static Stream<Arguments> toShortAllParametersTestCases() {
        short defaultValue = (short) 11;
        return Stream.of(
                //@formatter:off
                //            potentialNumber,   defaultValue,   expectedResult
                Arguments.of( null,              defaultValue,   defaultValue ),
                Arguments.of( "",                defaultValue,   defaultValue ),
                Arguments.of( "  ",              defaultValue,   defaultValue ),
                Arguments.of( "aa",              defaultValue,   defaultValue ),
                Arguments.of( "50",              defaultValue,   (short) 50 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toShortAllParametersTestCases")
    @DisplayName("toShort: with all parameters test cases")
    public void toShortAllParameters_testCases(String potentialNumber,
                                               short defaultValue,
                                               Short expectedResult) {
        assertEquals(
                expectedResult,
                toShort(potentialNumber, defaultValue)
        );
    }


    private enum CompareToResult {
        LESS_THAN_ZERO,
        ZERO,
        GREATER_THAN_ZERO
    }

}