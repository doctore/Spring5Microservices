package com.spring5microservices.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Comparator;
import java.util.stream.Stream;

import static com.spring5microservices.common.util.ComparatorUtil.safeNaturalOrderNullFirst;
import static com.spring5microservices.common.util.ComparatorUtil.safeNaturalOrderNullLast;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ComparatorUtilTest {

    static Stream<Arguments> safeNaturalOrderNullFirstTestCases() {
        Integer int1 = 11;
        Integer int2 = 12;
        String string1 = "AB";
        String string2 = "CD";
        return Stream.of(
                //@formatter:off
                //            element1,   element2,   expectedResult
                Arguments.of( null,       null,       CompareToResult.ZERO ),
                Arguments.of( int1,       int1,       CompareToResult.ZERO ),
                Arguments.of( string1,    string1,    CompareToResult.ZERO ),
                Arguments.of( null,       int1,       CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( null,       string1,    CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( int1,       int2,       CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( string1,    string2,    CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( int1,       null,       CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( string1,    null,       CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( int2,       int1,       CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( string2,    string1,    CompareToResult.GREATER_THAN_ZERO )

        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("safeNaturalOrderNullFirstTestCases")
    @DisplayName("safeNaturalOrderNullFirst: test cases")
    public <T extends Comparable<? super T>> void safeNaturalOrderNullFirst_testCases(T element1,
                                                                                      T element2,
                                                                                      CompareToResult expectedResult) {
        // Required because the sometimes stupid Java compiler
        Comparator<T> comparator = safeNaturalOrderNullFirst();

        int result = comparator.compare(element1, element2);
        verifyCompareToResult(result, expectedResult);
    }


    static Stream<Arguments> safeNaturalOrderNullLastTestCases() {
        Integer int1 = 11;
        Integer int2 = 12;
        String string1 = "AB";
        String string2 = "CD";
        return Stream.of(
                //@formatter:off
                //            element1,   element2,   expectedResult
                Arguments.of( null,       null,       CompareToResult.ZERO ),
                Arguments.of( int1,       int1,       CompareToResult.ZERO ),
                Arguments.of( string1,    string1,    CompareToResult.ZERO ),
                Arguments.of( int1,       null,       CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( string1,    null,       CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( int1,       int2,       CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( string1,    string2,    CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( null,       int1,       CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( null,       string1,    CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( int2,       int1,       CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( string2,    string1,    CompareToResult.GREATER_THAN_ZERO )

        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("safeNaturalOrderNullLastTestCases")
    @DisplayName("safeNaturalOrderNullLast: test cases")
    public <T extends Comparable<? super T>> void safeNaturalOrderNullLast_testCases(T element1,
                                                                                      T element2,
                                                                                      CompareToResult expectedResult) {
        // Required because the sometimes stupid Java compiler
        Comparator<T> comparator = safeNaturalOrderNullLast();

        int result = comparator.compare(element1, element2);
        verifyCompareToResult(result, expectedResult);
    }


    private void verifyCompareToResult(int actualResult,
                                       CompareToResult expectedResult) {
        switch (expectedResult) {
            case LESS_THAN_ZERO -> assertTrue(0 > actualResult);
            case ZERO -> assertEquals(0, actualResult);
            case GREATER_THAN_ZERO -> assertTrue(0 < actualResult);
        }
    }


    private enum CompareToResult {
        LESS_THAN_ZERO,
        ZERO,
        GREATER_THAN_ZERO
    }

}
