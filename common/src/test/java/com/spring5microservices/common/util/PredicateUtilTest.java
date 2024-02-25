package com.spring5microservices.common.util;

import com.spring5microservices.common.PizzaDto;
import com.spring5microservices.common.interfaces.predicate.HeptaPredicate;
import com.spring5microservices.common.interfaces.predicate.HexaPredicate;
import com.spring5microservices.common.interfaces.predicate.PentaPredicate;
import com.spring5microservices.common.interfaces.predicate.QuadPredicate;
import com.spring5microservices.common.interfaces.predicate.TriPredicate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.spring5microservices.common.PizzaEnum.CARBONARA;
import static com.spring5microservices.common.PizzaEnum.MARGUERITA;
import static com.spring5microservices.common.util.PredicateUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PredicateUtilTest {

    static Stream<Arguments> allOfTestCases() {
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        PizzaDto marguerita = new PizzaDto(MARGUERITA.getInternalPropertyValue(), 15D);
        Predicate<PizzaDto> nameLongerThan5 = p -> 5 < p.getName().length();
        Predicate<PizzaDto> costMoreExpenseThan10 = p -> 0 < p.getCost().compareTo(10d);
        return Stream.of(
                //@formatter:off
                //            t,            predicate1,        predicate2,              expectedResult
                Arguments.of( carbonara,    null,              null,                    true ),
                Arguments.of( carbonara,    null,              costMoreExpenseThan10,   false ),
                Arguments.of( carbonara,    nameLongerThan5,   null,                    true ),
                Arguments.of( carbonara,    nameLongerThan5,   costMoreExpenseThan10,   false ),
                Arguments.of( marguerita,   null,              costMoreExpenseThan10,   true ),
                Arguments.of( marguerita,   nameLongerThan5,   null,                    true ),
                Arguments.of( marguerita,   nameLongerThan5,   costMoreExpenseThan10,   true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("allOfTestCases")
    @DisplayName("allOf: test cases")
    public <T> void allOf_testCases(T t,
                                    Predicate<? super T> predicate1,
                                    Predicate<? super T> predicate2,
                                    boolean expectedResult) {
        Predicate<T> finalPredicate =
                null == predicate1 && null == predicate2
                        ? allOf()
                        : allOf(predicate1, predicate2);

        assertEquals(expectedResult, finalPredicate.test(t));
    }


    static Stream<Arguments> alwaysFalseTestCases() {
        Integer nullInt = null;
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        return Stream.of(
                //@formatter:off
                //            t
                Arguments.of( nullInt ),
                Arguments.of( "noMatterString" ),
                Arguments.of( 12 ),
                Arguments.of( carbonara )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("alwaysFalseTestCases")
    @DisplayName("alwaysFalse: test cases")
    public <T> void alwaysFalse_testCases(T t) {
        assertFalse(alwaysFalse().test(t));
    }


    static Stream<Arguments> alwaysTrueTestCases() {
        Integer nullInt = null;
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        return Stream.of(
                //@formatter:off
                //            t
                Arguments.of( nullInt ),
                Arguments.of( "noMatterString" ),
                Arguments.of( 12 ),
                Arguments.of( carbonara )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("alwaysTrueTestCases")
    @DisplayName("alwaysTrue: test cases")
    public <T> void alwaysTrue_testCases(T t) {
        assertTrue(alwaysTrue().test(t));
    }


    static Stream<Arguments> anyOfTestCases() {
        Predicate<Integer> isGreaterThanTen = i -> 10 < i;
        Predicate<Integer> isGreaterThanTwenty = i -> 20 < i;
        return Stream.of(
                //@formatter:off
                //            t,    predicate1,         predicate2,            expectedResult
                Arguments.of( 1,    null,               null,                  false ),
                Arguments.of( 1,    null,               isGreaterThanTwenty,   false ),
                Arguments.of( 1,    isGreaterThanTen,   null,                  false ),
                Arguments.of( 1,    isGreaterThanTen,   isGreaterThanTwenty,   false ),
                Arguments.of( 11,   isGreaterThanTen,   isGreaterThanTwenty,   true ),
                Arguments.of( 21,   isGreaterThanTen,   isGreaterThanTwenty,   true )

        ); //@formatter:on
    }


    @ParameterizedTest
    @MethodSource("anyOfTestCases")
    @DisplayName("anyOf: test cases")
    public <T> void anyOf_testCases(T t,
                                    Predicate<? super T> predicate1,
                                    Predicate<? super T> predicate2,
                                    boolean expectedResult) {
        Predicate<T> finalPredicate =
                null == predicate1 && null == predicate2
                        ? anyOf()
                        : anyOf(predicate1, predicate2);

        assertEquals(expectedResult, finalPredicate.test(t));
    }


    static Stream<Arguments> biAllOfTestCases() {
        BiPredicate<Integer, String> isIntegerGreaterThanTenAndStringLongerThan2 = (i, s) -> (10 < i) && (2 < s.length());
        BiPredicate<Integer, String> isGreaterThanTwentyAndStringLongerThan5 = (i, s) -> (20 < i) && (5 < s.length());
        return Stream.of(
                //@formatter:off
                //            t1,   t2,          predicate1,                                    predicate2,                                expectedResult
                Arguments.of( 1,    "s",        null,                                          null,                                      true ),
                Arguments.of( 1,    "s",        null,                                          isGreaterThanTwentyAndStringLongerThan5,   false ),
                Arguments.of( 1,    "s",        isIntegerGreaterThanTenAndStringLongerThan2,   null,                                      false ),
                Arguments.of( 1,    "s",        isIntegerGreaterThanTenAndStringLongerThan2,   isGreaterThanTwentyAndStringLongerThan5,   false ),
                Arguments.of( 11,   "abc",      isIntegerGreaterThanTenAndStringLongerThan2,   isGreaterThanTwentyAndStringLongerThan5,   false ),
                Arguments.of( 21,   "abcdef",   isIntegerGreaterThanTenAndStringLongerThan2,   isGreaterThanTwentyAndStringLongerThan5,   true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("biAllOfTestCases")
    @DisplayName("biAllOf: test cases")
    public <T1, T2> void biAllOf_testCases(T1 t1,
                                           T2 t2,
                                           BiPredicate<? super T1, ? super T2> predicate1,
                                           BiPredicate<? super T1, ? super T2> predicate2,
                                           boolean expectedResult) {
        BiPredicate<? super T1, ? super T2> finalPredicate =
                null == predicate1 && null == predicate2
                        ? biAllOf()
                        : biAllOf(predicate1, predicate2);

        assertEquals(expectedResult, finalPredicate.test(t1, t2));
    }


    static Stream<Arguments> biAlwaysFalseTestCases() {
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        return Stream.of(
                //@formatter:off
                //            t1,                 t2,             expectedResult
                Arguments.of( null,               null,           false ),
                Arguments.of( "abc",              null,           false ),
                Arguments.of( "noMatterString",   11,             false ),
                Arguments.of( 12,                 54L,            false ),
                Arguments.of( carbonara,          Boolean.TRUE,   false )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("biAlwaysFalseTestCases")
    @DisplayName("biAlwaysFalse: test cases")
    public <T1, T2> void biAlwaysFalse_testCases(T1 t1,
                                                 T2 t2,
                                                 boolean expectedResult) {
        assertEquals(expectedResult, biAlwaysFalse().test(t1, t2));
    }


    static Stream<Arguments> biAlwaysTrueTestCases() {
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        return Stream.of(
                //@formatter:off
                //            t1,                 t2,             expectedResult
                Arguments.of( null,               null,           true ),
                Arguments.of( 4,                  null,           true ),
                Arguments.of( "noMatterString",   11,             true ),
                Arguments.of( 12,                 54L,            true ),
                Arguments.of( carbonara,          Boolean.TRUE,   true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("biAlwaysTrueTestCases")
    @DisplayName("biAlwaysTrue: test cases")
    public <T1, T2> void biAlwaysTrue_testCases(T1 t1,
                                                T2 t2,
                                                boolean expectedResult) {
        assertEquals(expectedResult, biAlwaysTrue().test(t1, t2));
    }


    static Stream<Arguments> biAnyOfTestCases() {
        BiPredicate<Integer, String> isIntegerGreaterThanTenAndStringLongerThan2 = (i, s) -> (10 < i) && (2 < s.length());
        BiPredicate<Integer, String> isLowerThanTwentyAndStringShorterThan5 = (i, s) -> (20 > i) && (5 > s.length());
        return Stream.of(
                //@formatter:off
                //            t1,   t2,         predicate1,                                    predicate2,                               expectedResult
                Arguments.of( 1,    "s",        null,                                          null,                                     false ),
                Arguments.of( 1,    "s",        null,                                          isLowerThanTwentyAndStringShorterThan5,   true ),
                Arguments.of( 1,    "s",        isIntegerGreaterThanTenAndStringLongerThan2,   null,                                     false ),
                Arguments.of( 11,   "abc",      isIntegerGreaterThanTenAndStringLongerThan2,   isLowerThanTwentyAndStringShorterThan5,   true ),
                Arguments.of( 8,    "abc",      isIntegerGreaterThanTenAndStringLongerThan2,   isLowerThanTwentyAndStringShorterThan5,   true ),
                Arguments.of( 5,    "abcdef",   isIntegerGreaterThanTenAndStringLongerThan2,   isLowerThanTwentyAndStringShorterThan5,   false )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("biAnyOfTestCases")
    @DisplayName("biAnyOf: test cases")
    public <T1, T2> void biAnyOf_testCases(T1 t1,
                                           T2 t2,
                                           BiPredicate<? super T1, ? super T2> predicate1,
                                           BiPredicate<? super T1, ? super T2> predicate2,
                                           boolean expectedResult) {
        BiPredicate<? super T1, ? super T2> finalPredicate =
                null == predicate1 && null == predicate2
                        ? biAnyOf()
                        : biAnyOf(predicate1, predicate2);

        assertEquals(expectedResult, finalPredicate.test(t1, t2));
    }


    static Stream<Arguments> biIsNullTestCases() {
        Integer nullInt = null;
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        return Stream.of(
                //@formatter:off
                //            t1,                 t2,                 expectedResult
                Arguments.of( null,               null,               true ),
                Arguments.of( nullInt,            null,               true),
                Arguments.of( null,               nullInt,            true),
                Arguments.of( "noMatterString",   null,               false ),
                Arguments.of( null,               "noMatterString",   false ),
                Arguments.of( 12,                 null,               false ),
                Arguments.of( null,               12,                 false ),
                Arguments.of( null,               carbonara,          false ),
                Arguments.of( carbonara,          null,               false ),
                Arguments.of( nullInt,            carbonara,          false ),
                Arguments.of( carbonara,          12,                 false ),
                Arguments.of( "noMatterString",   carbonara,          false )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("biIsNullTestCases")
    @DisplayName("biIsNull: test cases")
    public <T1, T2> void biIsNull_testCases(T1 t1,
                                            T2 t2,
                                            boolean expectedResult) {
        assertEquals(expectedResult, biIsNull().test(t1, t2));
    }


    static Stream<Arguments> biNonNullTestCases() {
        Integer nullInt = null;
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        return Stream.of(
                //@formatter:off
                //            t1,                 t2,                 expectedResult
                Arguments.of( null,               null,               false ),
                Arguments.of( nullInt,            null,               false),
                Arguments.of( null,               nullInt,            false),
                Arguments.of( "noMatterString",   null,               false ),
                Arguments.of( null,               "noMatterString",   false ),
                Arguments.of( 12,                 null,               false ),
                Arguments.of( null,               12,                 false ),
                Arguments.of( null,               carbonara,          false ),
                Arguments.of( carbonara,          null,               false ),
                Arguments.of( nullInt,            carbonara,          false ),
                Arguments.of( carbonara,          12,                 true ),
                Arguments.of( "noMatterString",   carbonara,          true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("biNonNullTestCases")
    @DisplayName("biNonNull: test cases")
    public <T1, T2> void biNonNull_testCases(T1 t1,
                                             T2 t2,
                                             boolean expectedResult) {
        assertEquals(expectedResult, biNonNull().test(t1, t2));
    }


    static Stream<Arguments> triAllOfTestCases() {
        TriPredicate<Integer, Integer, Integer> allAreOdd = (t1, t2, t3) ->
                1 == t1 % 2 && 1 == t2 % 2 && 1 == t3 % 2;

        TriPredicate<Integer, Integer, Integer> allGreaterThan10 = (t1, t2, t3) ->
                10 < t1 && 10 < t2 && 10 < t3;

        return Stream.of(
                //@formatter:off
                //            t1,   t2,   t3,   predicate1,   predicate2,         expectedResult
                Arguments.of( 0,    5,    4,    null,         null,               true ),
                Arguments.of( 0,    5,    4,    allAreOdd,    null,               false ),
                Arguments.of( 11,   12,   14,   null,         allGreaterThan10,   true ),
                Arguments.of( 0,    5,    4,    allAreOdd,    allGreaterThan10,   false ),
                Arguments.of( 10,   15,   14,   allAreOdd,    allGreaterThan10,   false ),
                Arguments.of( 11,   15,   19,   allAreOdd,    allGreaterThan10,   true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("triAllOfTestCases")
    @DisplayName("triAllOf: test cases")
    public <T1, T2, T3> void triAllOf_testCases(T1 t1,
                                                T2 t2,
                                                T3 t3,
                                                TriPredicate<? super T1, ? super T2, ? super T3> predicate1,
                                                TriPredicate<? super T1, ? super T2, ? super T3> predicate2,
                                                boolean expectedResult) {
        TriPredicate<? super T1, ? super T2, ? super T3> finalPredicate =
                null == predicate1 && null == predicate2
                        ? triAllOf()
                        : triAllOf(predicate1, predicate2);

        assertEquals(expectedResult, finalPredicate.test(t1, t2, t3));
    }


    static Stream<Arguments> triAlwaysFalseTestCases() {
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        return Stream.of(
                //@formatter:off
                //            t1,      t2,          t3,             expectedResult
                Arguments.of( null,    null,        null,           false ),
                Arguments.of( 1,       null,        null,           false ),
                Arguments.of( 10,      "rt",        null,           false ),
                Arguments.of( "abc",   carbonara,   Boolean.TRUE,   false )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("triAlwaysFalseTestCases")
    @DisplayName("triAlwaysFalse: test cases")
    public <T1, T2, T3> void triAlwaysFalse_testCases(T1 t1,
                                                      T2 t2,
                                                      T3 t3,
                                                      boolean expectedResult) {
        assertEquals(expectedResult, triAlwaysFalse().test(t1, t2, t3));
    }


    static Stream<Arguments> triAlwaysTrueTestCases() {
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        return Stream.of(
                //@formatter:off
                //            t1,      t2,          t3,             expectedResult
                Arguments.of( null,    null,        null,           true ),
                Arguments.of( 1,       null,        null,           true ),
                Arguments.of( 10,      "rt",        null,           true ),
                Arguments.of( "abc",   carbonara,   Boolean.TRUE,   true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("triAlwaysTrueTestCases")
    @DisplayName("triAlwaysTrue: test cases")
    public <T1, T2, T3> void triAlwaysTrue_testCases(T1 t1,
                                                     T2 t2,
                                                     T3 t3,
                                                     boolean expectedResult) {
        assertEquals(expectedResult, triAlwaysTrue().test(t1, t2, t3));
    }


    static Stream<Arguments> triAnyOfTestCases() {
        TriPredicate<Integer, Integer, Integer> allAreOdd = (t1, t2, t3) ->
                1 == t1 % 2 && 1 == t2 % 2 && 1 == t3 % 2;

        TriPredicate<Integer, Integer, Integer> allGreaterThan10 = (t1, t2, t3) ->
                10 < t1 && 10 < t2 && 10 < t3;

        return Stream.of(
                //@formatter:off
                //            t1,   t2,   t3,   predicate1,   predicate2,         expectedResult
                Arguments.of( 0,    5,    4,    null,         null,               false ),
                Arguments.of( 0,    5,    4,    allAreOdd,    null,               false ),
                Arguments.of( 11,   12,   14,   null,         allGreaterThan10,   true ),
                Arguments.of( 1,    5,    11,   allAreOdd,    allGreaterThan10,   true ),
                Arguments.of( 12,   15,   14,   allAreOdd,    allGreaterThan10,   true ),
                Arguments.of( 2,    15,   19,   allAreOdd,    allGreaterThan10,   false )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("triAnyOfTestCases")
    @DisplayName("triAnyOf: test cases")
    public <T1, T2, T3> void triAnyOf_testCases(T1 t1,
                                                T2 t2,
                                                T3 t3,
                                                TriPredicate<? super T1, ? super T2, ? super T3> predicate1,
                                                TriPredicate<? super T1, ? super T2, ? super T3> predicate2,
                                                boolean expectedResult) {
        TriPredicate<? super T1, ? super T2, ? super T3> finalPredicate =
                null == predicate1 && null == predicate2
                        ? triAnyOf()
                        : triAnyOf(predicate1, predicate2);

        assertEquals(expectedResult, finalPredicate.test(t1, t2, t3));
    }


    static Stream<Arguments> triIsNullTestCases() {
        Integer nullInt = null;
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        return Stream.of(
                //@formatter:off
                //            t1,        t2,        t3,          expectedResult
                Arguments.of( null,      null,      null,        true ),
                Arguments.of( nullInt,   null,      null,        true ),
                Arguments.of( null,      nullInt,   null,        true ),
                Arguments.of( null,      null,      nullInt,     true ),
                Arguments.of( "ab",      null,      null,        false ),
                Arguments.of( null,      "ab",      null,        false ),
                Arguments.of( null,      null,      "ab",        false ),
                Arguments.of( "ab",      nullInt,   carbonara,   false )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("triIsNullTestCases")
    @DisplayName("triIsNull: test cases")
    public <T1, T2, T3> void triIsNull_testCases(T1 t1,
                                                 T2 t2,
                                                 T3 t3,
                                                 boolean expectedResult) {
        assertEquals(expectedResult, triIsNull().test(t1, t2, t3));
    }


    static Stream<Arguments> triNonNullTestCases() {
        Integer nullInt = null;
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        return Stream.of(
                //@formatter:off
                //            t1,        t2,        t3,          expectedResult
                Arguments.of( null,      null,      null,        false ),
                Arguments.of( nullInt,   null,      null,        false ),
                Arguments.of( null,      nullInt,   null,        false ),
                Arguments.of( null,      null,      nullInt,     false ),
                Arguments.of( "ab",      null,      null,        false ),
                Arguments.of( null,      "ab",      null,        false ),
                Arguments.of( null,      null,      "ab",        false ),
                Arguments.of( "ab",      12,        carbonara,   true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("triNonNullTestCases")
    @DisplayName("triNonNull: test cases")
    public <T1, T2, T3> void triNonNull_testCases(T1 t1,
                                                  T2 t2,
                                                  T3 t3,
                                                  boolean expectedResult) {
        assertEquals(expectedResult, triNonNull().test(t1, t2, t3));
    }


    static Stream<Arguments> quadAllOfTestCases() {
        QuadPredicate<Integer, Integer, Integer, Integer> allAreOdd = (t1, t2, t3, t4) ->
                1 == t1 % 2 && 1 == t2 % 2 && 1 == t3 % 2 && 1 == t4 % 2;

        QuadPredicate<Integer, Integer, Integer, Integer> allGreaterThan10 = (t1, t2, t3, t4) ->
                10 < t1 && 10 < t2 && 10 < t3 && 10 < t4;

        return Stream.of(
                //@formatter:off
                //            t1,   t2,   t3,   t4,   predicate1,   predicate2,         expectedResult
                Arguments.of( 0,    5,    4,    9,    null,         null,               true ),
                Arguments.of( 0,    5,    4,    9,    allAreOdd,    null,               false ),
                Arguments.of( 11,   12,   14,   19,   null,         allGreaterThan10,   true ),
                Arguments.of( 0,    5,    4,    12,   allAreOdd,    allGreaterThan10,   false ),
                Arguments.of( 10,   15,   14,   9,    allAreOdd,    allGreaterThan10,   false ),
                Arguments.of( 11,   15,   19,   21,   allAreOdd,    allGreaterThan10,   true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("quadAllOfTestCases")
    @DisplayName("quadAllOf: test cases")
    public <T1, T2, T3, T4> void quadAllOf_testCases(T1 t1,
                                                     T2 t2,
                                                     T3 t3,
                                                     T4 t4,
                                                     QuadPredicate<? super T1, ? super T2, ? super T3, ? super T4> predicate1,
                                                     QuadPredicate<? super T1, ? super T2, ? super T3, ? super T4> predicate2,
                                                     boolean expectedResult) {
        QuadPredicate<? super T1, ? super T2, ? super T3, ? super T4> finalPredicate =
                null == predicate1 && null == predicate2
                        ? quadAllOf()
                        : quadAllOf(predicate1, predicate2);

        assertEquals(expectedResult, finalPredicate.test(t1, t2, t3, t4));
    }


    static Stream<Arguments> quadAlwaysFalseTestCases() {
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        return Stream.of(
                //@formatter:off
                //            t1,      t2,          t3,             t4,     expectedResult
                Arguments.of( null,    null,        null,           null,   false ),
                Arguments.of( 1,       null,        null,           null,   false ),
                Arguments.of( 10,      "rt",        null,           null,   false ),
                Arguments.of( 10,      "rt",        carbonara,      null,   false ),
                Arguments.of( "abc",   carbonara,   Boolean.TRUE,   10,     false )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("quadAlwaysFalseTestCases")
    @DisplayName("quadAlwaysFalse: test cases")
    public <T1, T2, T3, T4> void quadAlwaysFalse_testCases(T1 t1,
                                                           T2 t2,
                                                           T3 t3,
                                                           T4 t4,
                                                           boolean expectedResult) {
        assertEquals(expectedResult, quadAlwaysFalse().test(t1, t2, t3, t4));
    }


    static Stream<Arguments> quadAlwaysTrueTestCases() {
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        return Stream.of(
                //@formatter:off
                //            t1,      t2,          t3,             t4,     expectedResult
                Arguments.of( null,    null,        null,           null,   true ),
                Arguments.of( 1,       null,        null,           null,   true ),
                Arguments.of( 10,      "rt",        null,           null,   true ),
                Arguments.of( 10,      "rt",        carbonara,      null,   true ),
                Arguments.of( "abc",   carbonara,   Boolean.TRUE,   10,     true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("quadAlwaysTrueTestCases")
    @DisplayName("quadAlwaysTrue: test cases")
    public <T1, T2, T3, T4> void quadAlwaysTrue_testCases(T1 t1,
                                                          T2 t2,
                                                          T3 t3,
                                                          T4 t4,
                                                          boolean expectedResult) {
        assertEquals(expectedResult, quadAlwaysTrue().test(t1, t2, t3, t4));
    }


    static Stream<Arguments> quadAnyOfTestCases() {
        QuadPredicate<Integer, Integer, Integer, Integer> allAreOdd = (t1, t2, t3, t4) ->
                1 == t1 % 2 && 1 == t2 % 2 && 1 == t3 % 2 && 1 == t4 % 2;

        QuadPredicate<Integer, Integer, Integer, Integer> allGreaterThan10 = (t1, t2, t3, t4) ->
                10 < t1 && 10 < t2 && 10 < t3 && 10 < t4;

        return Stream.of(
                //@formatter:off
                //            t1,   t2,   t3,   t4,   predicate1,   predicate2,         expectedResult
                Arguments.of( 0,    5,    4,    9,    null,         null,               false ),
                Arguments.of( 0,    5,    4,    9,    allAreOdd,    null,               false ),
                Arguments.of( 11,   12,   14,   19,   null,         allGreaterThan10,   true ),
                Arguments.of( 1,    5,    11,   15,   allAreOdd,    allGreaterThan10,   true ),
                Arguments.of( 12,   15,   14,   18,   allAreOdd,    allGreaterThan10,   true ),
                Arguments.of( 2,    15,   19,   8,    allAreOdd,    allGreaterThan10,   false )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("quadAnyOfTestCases")
    @DisplayName("quadAnyOf: test cases")
    public <T1, T2, T3, T4> void quadAnyOf_testCases(T1 t1,
                                                     T2 t2,
                                                     T3 t3,
                                                     T4 t4,
                                                     QuadPredicate<? super T1, ? super T2, ? super T3, ? super T4> predicate1,
                                                     QuadPredicate<? super T1, ? super T2, ? super T3, ? super T4> predicate2,
                                                     boolean expectedResult) {
        QuadPredicate<? super T1, ? super T2, ? super T3, ? super T4> finalPredicate =
                null == predicate1 && null == predicate2
                        ? quadAnyOf()
                        : quadAnyOf(predicate1, predicate2);

        assertEquals(expectedResult, finalPredicate.test(t1, t2, t3, t4));
    }


    static Stream<Arguments> quadIsNullTestCases() {
        Integer nullInt = null;
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        return Stream.of(
                //@formatter:off
                //            t1,        t2,        t3,          t4,        expectedResult
                Arguments.of( null,      null,      null,        null,      true ),
                Arguments.of( nullInt,   null,      null,        null,      true ),
                Arguments.of( null,      nullInt,   null,        null,      true ),
                Arguments.of( null,      null,      nullInt,     null,      true ),
                Arguments.of( null,      null,      null,        nullInt,   true ),
                Arguments.of( "ab",      null,      null,        null,      false ),
                Arguments.of( null,      "ab",      null,        null,      false ),
                Arguments.of( null,      null,      "ab",        null,      false ),
                Arguments.of( null,      null,      null,        "a",       false ),
                Arguments.of( "ab",      nullInt,   carbonara,   12,        false )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("quadIsNullTestCases")
    @DisplayName("quadIsNull: test cases")
    public <T1, T2, T3, T4> void quadIsNull_testCases(T1 t1,
                                                      T2 t2,
                                                      T3 t3,
                                                      T4 t4,
                                                      boolean expectedResult) {
        assertEquals(expectedResult, quadIsNull().test(t1, t2, t3, t4));
    }


    static Stream<Arguments> quadNonNullTestCases() {
        Integer nullInt = null;
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        return Stream.of(
                //@formatter:off
                //            t1,        t2,        t3,          t4,        expectedResult
                Arguments.of( null,      null,      null,        null,      false ),
                Arguments.of( nullInt,   null,      null,        null,      false ),
                Arguments.of( null,      nullInt,   null,        null,      false ),
                Arguments.of( null,      null,      nullInt,     null,      false ),
                Arguments.of( null,      null,      null,        nullInt,   false ),
                Arguments.of( "ab",      null,      null,        null,      false ),
                Arguments.of( null,      "ab",      null,        null,      false ),
                Arguments.of( null,      null,      "ab",        null,      false ),
                Arguments.of( null,      null,      null,        "a",       false ),
                Arguments.of( "ab",      11,        carbonara,   12,        true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("quadNonNullTestCases")
    @DisplayName("quadNonNull: test cases")
    public <T1, T2, T3, T4> void quadNonNull_testCases(T1 t1,
                                                       T2 t2,
                                                       T3 t3,
                                                       T4 t4,
                                                       boolean expectedResult) {
        assertEquals(expectedResult, quadNonNull().test(t1, t2, t3, t4));
    }


    static Stream<Arguments> pentaAllOfTestCases() {
        PentaPredicate<Integer, Integer, Integer, Integer, Integer> allAreOdd = (t1, t2, t3, t4, t5) ->
                1 == t1 % 2 && 1 == t2 % 2 && 1 == t3 % 2 && 1 == t4 % 2 && 1 == t5 % 2;

        PentaPredicate<Integer, Integer, Integer, Integer, Integer> allGreaterThan10 = (t1, t2, t3, t4, t5) ->
                10 < t1 && 10 < t2 && 10 < t3 && 10 < t4 && 10 < t5;

        return Stream.of(
                //@formatter:off
                //            t1,   t2,   t3,   t4,   t5,   predicate1,   predicate2,         expectedResult
                Arguments.of( 0,    5,    4,    9,    12,   null,         null,               true ),
                Arguments.of( 0,    5,    4,    9,    10,   allAreOdd,    null,               false ),
                Arguments.of( 11,   12,   14,   19,   22,   null,         allGreaterThan10,   true ),
                Arguments.of( 0,    5,    4,    12,   9,    allAreOdd,    allGreaterThan10,   false ),
                Arguments.of( 10,   15,   14,   9,    7,    allAreOdd,    allGreaterThan10,   false ),
                Arguments.of( 11,   15,   19,   21,   33,   allAreOdd,    allGreaterThan10,   true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("pentaAllOfTestCases")
    @DisplayName("pentaAllOf: test cases")
    public <T1, T2, T3, T4, T5> void pentaAllOf_testCases(T1 t1,
                                                          T2 t2,
                                                          T3 t3,
                                                          T4 t4,
                                                          T5 t5,
                                                          PentaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5> predicate1,
                                                          PentaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5> predicate2,
                                                          boolean expectedResult) {
        PentaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5> finalPredicate =
                null == predicate1 && null == predicate2
                        ? pentaAllOf()
                        : pentaAllOf(predicate1, predicate2);

        assertEquals(expectedResult, finalPredicate.test(t1, t2, t3, t4, t5));
    }


    static Stream<Arguments> pentaAlwaysFalseTestCases() {
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        return Stream.of(
                //@formatter:off
                //            t1,      t2,          t3,             t4,     t5,     expectedResult
                Arguments.of( null,    null,        null,           null,   null,   false ),
                Arguments.of( 1,       null,        null,           null,   null,   false ),
                Arguments.of( 10,      "rt",        null,           null,   null,   false ),
                Arguments.of( 10,      "rt",        carbonara,      null,   null,   false ),
                Arguments.of( 10,      "rt",        carbonara,      5L,     null,   false ),
                Arguments.of( "abc",   carbonara,   Boolean.TRUE,   10,     4L,     false )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("pentaAlwaysFalseTestCases")
    @DisplayName("pentaAlwaysFalse: test cases")
    public <T1, T2, T3, T4, T5> void pentaAlwaysFalse_testCases(T1 t1,
                                                                T2 t2,
                                                                T3 t3,
                                                                T4 t4,
                                                                T5 t5,
                                                                boolean expectedResult) {
        assertEquals(expectedResult, pentaAlwaysFalse().test(t1, t2, t3, t4, t5));
    }


    static Stream<Arguments> pentaAlwaysTrueTestCases() {
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        return Stream.of(
                //@formatter:off
                //            t1,      t2,          t3,             t4,     t5,     expectedResult
                Arguments.of( null,    null,        null,           null,   null,   true ),
                Arguments.of( 1,       null,        null,           null,   null,   true ),
                Arguments.of( 10,      "rt",        null,           null,   null,   true ),
                Arguments.of( 10,      "rt",        carbonara,      null,   null,   true ),
                Arguments.of( 10,      "rt",        carbonara,      5L,     null,   true ),
                Arguments.of( "abc",   carbonara,   Boolean.TRUE,   10,     4L,     true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("pentaAlwaysTrueTestCases")
    @DisplayName("pentaAlwaysTrue: test cases")
    public <T1, T2, T3, T4, T5> void pentaAlwaysTrue_testCases(T1 t1,
                                                               T2 t2,
                                                               T3 t3,
                                                               T4 t4,
                                                               T5 t5,
                                                               boolean expectedResult) {
        assertEquals(expectedResult, pentaAlwaysTrue().test(t1, t2, t3, t4, t5));
    }


    static Stream<Arguments> pentaAnyOfTestCases() {
        PentaPredicate<Integer, Integer, Integer, Integer, Integer> allAreOdd = (t1, t2, t3, t4, t5) ->
                1 == t1 % 2 && 1 == t2 % 2 && 1 == t3 % 2 && 1 == t4 % 2 && 1 == t5 % 2;

        PentaPredicate<Integer, Integer, Integer, Integer, Integer> allGreaterThan10 = (t1, t2, t3, t4, t5) ->
                10 < t1 && 10 < t2 && 10 < t3 && 10 < t4 && 10 < t5;

        return Stream.of(
                //@formatter:off
                //            t1,   t2,   t3,   t4,   t5,   predicate1,   predicate2,         expectedResult
                Arguments.of( 0,    5,    4,    9,    12,   null,         null,               false ),
                Arguments.of( 0,    5,    4,    9,    11,   allAreOdd,    null,               false ),
                Arguments.of( 11,   12,   14,   19,   18,   null,         allGreaterThan10,   true ),
                Arguments.of( 1,    5,    11,   15,   7,    allAreOdd,    allGreaterThan10,   true ),
                Arguments.of( 12,   15,   14,   18,   20,   allAreOdd,    allGreaterThan10,   true ),
                Arguments.of( 2,    15,   19,   8,    11,   allAreOdd,    allGreaterThan10,   false )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("pentaAnyOfTestCases")
    @DisplayName("pentaAnyOf: test cases")
    public <T1, T2, T3, T4, T5> void pentaAnyOf_testCases(T1 t1,
                                                          T2 t2,
                                                          T3 t3,
                                                          T4 t4,
                                                          T5 t5,
                                                          PentaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5> predicate1,
                                                          PentaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5> predicate2,
                                                          boolean expectedResult) {
        PentaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5> finalPredicate =
                null == predicate1 && null == predicate2
                        ? pentaAnyOf()
                        : pentaAnyOf(predicate1, predicate2);

        assertEquals(expectedResult, finalPredicate.test(t1, t2, t3, t4, t5));
    }


    static Stream<Arguments> pentaIsNullTestCases() {
        Integer nullInt = null;
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        return Stream.of(
                //@formatter:off
                //            t1,        t2,        t3,          t4,        t5,        expectedResult
                Arguments.of( null,      null,      null,        null,      null,      true ),
                Arguments.of( nullInt,   null,      null,        null,      null,      true ),
                Arguments.of( null,      nullInt,   null,        null,      null,      true ),
                Arguments.of( null,      null,      nullInt,     null,      null,      true ),
                Arguments.of( null,      null,      null,        nullInt,   null,      true ),
                Arguments.of( null,      null,      null,        null,      nullInt,   true ),
                Arguments.of( "ab",      null,      null,        null,      null,      false ),
                Arguments.of( null,      "ab",      null,        null,      null,      false ),
                Arguments.of( null,      null,      "ab",        null,      null,      false ),
                Arguments.of( null,      null,      null,        "a",       null,      false ),
                Arguments.of( null,      null,      null,        null,      "a",       false ),
                Arguments.of( "ab",      nullInt,   carbonara,   12,        4L,        false )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("pentaIsNullTestCases")
    @DisplayName("pentaIsNull: test cases")
    public <T1, T2, T3, T4, T5> void pentaIsNull_testCases(T1 t1,
                                                           T2 t2,
                                                           T3 t3,
                                                           T4 t4,
                                                           T5 t5,
                                                           boolean expectedResult) {
        assertEquals(expectedResult, pentaIsNull().test(t1, t2, t3, t4, t5));
    }


    static Stream<Arguments> pentaNonNullTestCases() {
        Integer nullInt = null;
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        return Stream.of(
                //@formatter:off
                //            t1,        t2,        t3,          t4,        t5,        expectedResult
                Arguments.of( null,      null,      null,        null,      null,      false ),
                Arguments.of( nullInt,   null,      null,        null,      null,      false ),
                Arguments.of( null,      nullInt,   null,        null,      null,      false ),
                Arguments.of( null,      null,      nullInt,     null,      null,      false ),
                Arguments.of( null,      null,      null,        nullInt,   null,      false ),
                Arguments.of( null,      null,      null,        null,      nullInt,   false ),
                Arguments.of( "ab",      null,      null,        null,      null,      false ),
                Arguments.of( null,      "ab",      null,        null,      null,      false ),
                Arguments.of( null,      null,      "ab",        null,      null,      false ),
                Arguments.of( null,      null,      null,        "a",       null,      false ),
                Arguments.of( null,      null,      null,        null,      "a",       false ),
                Arguments.of( "ab",      "123",     carbonara,   12,        4L,        true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("pentaNonNullTestCases")
    @DisplayName("pentaNonNull: test cases")
    public <T1, T2, T3, T4, T5> void pentaNonNull_testCases(T1 t1,
                                                            T2 t2,
                                                            T3 t3,
                                                            T4 t4,
                                                            T5 t5,
                                                            boolean expectedResult) {
        assertEquals(expectedResult, pentaNonNull().test(t1, t2, t3, t4, t5));
    }


    static Stream<Arguments> hexaAllOfTestCases() {
        HexaPredicate<Integer, Integer, Integer, Integer, Integer, Integer> allAreOdd = (t1, t2, t3, t4, t5, t6) ->
                1 == t1 % 2 && 1 == t2 % 2 && 1 == t3 % 2 && 1 == t4 % 2 && 1 == t5 % 2 && 1 == t6 % 2;

        HexaPredicate<Integer, Integer, Integer, Integer, Integer, Integer> allGreaterThan10 = (t1, t2, t3, t4, t5, t6) ->
                10 < t1 && 10 < t2 && 10 < t3 && 10 < t4 && 10 < t5 && 10 < t6;

        return Stream.of(
                //@formatter:off
                //            t1,   t2,   t3,   t4,   t5,   t6,   predicate1,   predicate2,         expectedResult
                Arguments.of( 0,    5,    4,    9,    12,   19,   null,         null,               true ),
                Arguments.of( 0,    5,    4,    9,    10,   22,   allAreOdd,    null,               false ),
                Arguments.of( 11,   12,   14,   19,   22,   33,   null,         allGreaterThan10,   true ),
                Arguments.of( 0,    5,    4,    12,   9,    14,   allAreOdd,    allGreaterThan10,   false ),
                Arguments.of( 10,   15,   14,   9,    7,    4,    allAreOdd,    allGreaterThan10,   false ),
                Arguments.of( 11,   15,   19,   21,   33,   37,   allAreOdd,    allGreaterThan10,   true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("hexaAllOfTestCases")
    @DisplayName("hexaAllOf: test cases")
    public <T1, T2, T3, T4, T5, T6> void hexaAllOf_testCases(T1 t1,
                                                             T2 t2,
                                                             T3 t3,
                                                             T4 t4,
                                                             T5 t5,
                                                             T6 t6,
                                                             HexaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6> predicate1,
                                                             HexaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6> predicate2,
                                                             boolean expectedResult) {
        HexaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6> finalPredicate =
                null == predicate1 && null == predicate2
                        ? hexaAllOf()
                        : hexaAllOf(predicate1, predicate2);

        assertEquals(expectedResult, finalPredicate.test(t1, t2, t3, t4, t5, t6));
    }


    static Stream<Arguments> hexaAlwaysFalseTestCases() {
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        return Stream.of(
                //@formatter:off
                //            t1,      t2,          t3,             t4,     t5,     t6,      expectedResult
                Arguments.of( null,    null,        null,           null,   null,   null,    false ),
                Arguments.of( 1,       null,        null,           null,   null,   null,    false ),
                Arguments.of( 10,      "rt",        null,           null,   null,   null,    false ),
                Arguments.of( 10,      "rt",        carbonara,      null,   null,   null,    false ),
                Arguments.of( 10,      "rt",        carbonara,      5L,     null,   null,    false ),
                Arguments.of( 10,      "rt",        carbonara,      5L,     "123",  null,    false ),
                Arguments.of( "abc",   carbonara,   Boolean.TRUE,   10,     4L,     "123",   false )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("hexaAlwaysFalseTestCases")
    @DisplayName("hexaAlwaysFalse: test cases")
    public <T1, T2, T3, T4, T5, T6> void hexaAlwaysFalse_testCases(T1 t1,
                                                                   T2 t2,
                                                                   T3 t3,
                                                                   T4 t4,
                                                                   T5 t5,
                                                                   T6 t6,
                                                                   boolean expectedResult) {
        assertEquals(expectedResult, hexaAlwaysFalse().test(t1, t2, t3, t4, t5, t6));
    }


    static Stream<Arguments> hexaAlwaysTrueTestCases() {
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        return Stream.of(
                //@formatter:off
                //            t1,      t2,          t3,             t4,     t5,     t6,      expectedResult
                Arguments.of( null,    null,        null,           null,   null,   null,    true ),
                Arguments.of( 1,       null,        null,           null,   null,   null,    true ),
                Arguments.of( 10,      "rt",        null,           null,   null,   null,    true ),
                Arguments.of( 10,      "rt",        carbonara,      null,   null,   null,    true ),
                Arguments.of( 10,      "rt",        carbonara,      5L,     null,   null,    true ),
                Arguments.of( 10,      "rt",        carbonara,      5L,     "123",  null,    true ),
                Arguments.of( "abc",   carbonara,   Boolean.TRUE,   10,     4L,     "123",   true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("hexaAlwaysTrueTestCases")
    @DisplayName("hexaAlwaysTrue: test cases")
    public <T1, T2, T3, T4, T5, T6> void hexaAlwaysTrue_testCases(T1 t1,
                                                                  T2 t2,
                                                                  T3 t3,
                                                                  T4 t4,
                                                                  T5 t5,
                                                                  T6 t6,
                                                                  boolean expectedResult) {
        assertEquals(expectedResult, hexaAlwaysTrue().test(t1, t2, t3, t4, t5, t6));
    }


    static Stream<Arguments> hexaAnyOfTestCases() {
        HexaPredicate<Integer, Integer, Integer, Integer, Integer, Integer> allAreOdd = (t1, t2, t3, t4, t5, t6) ->
                1 == t1 % 2 && 1 == t2 % 2 && 1 == t3 % 2 && 1 == t4 % 2 && 1 == t5 % 2 && 1 == t6 % 2;

        HexaPredicate<Integer, Integer, Integer, Integer, Integer, Integer> allGreaterThan10 = (t1, t2, t3, t4, t5, t6) ->
                10 < t1 && 10 < t2 && 10 < t3 && 10 < t4 && 10 < t5 && 10 < t6;

        return Stream.of(
                //@formatter:off
                //            t1,   t2,   t3,   t4,   t5,   t6,   predicate1,   predicate2,         expectedResult
                Arguments.of( 0,    5,    4,    9,    12,   19,   null,         null,               false ),
                Arguments.of( 0,    5,    4,    9,    10,   22,   allAreOdd,    null,               false ),
                Arguments.of( 11,   12,   14,   19,   22,   33,   null,         allGreaterThan10,   true ),
                Arguments.of( 1,    5,    11,   15,   7,    17,   allAreOdd,    allGreaterThan10,   true ),
                Arguments.of( 12,   15,   14,   18,   20,   32,   allAreOdd,    allGreaterThan10,   true ),
                Arguments.of( 2,    15,   19,   8,    11,   37,   allAreOdd,    allGreaterThan10,   false )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("hexaAnyOfTestCases")
    @DisplayName("hexaAnyOf: test cases")
    public <T1, T2, T3, T4, T5, T6> void hexaAnyOf_testCases(T1 t1,
                                                             T2 t2,
                                                             T3 t3,
                                                             T4 t4,
                                                             T5 t5,
                                                             T6 t6,
                                                             HexaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6> predicate1,
                                                             HexaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6> predicate2,
                                                             boolean expectedResult) {
        HexaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6> finalPredicate =
                null == predicate1 && null == predicate2
                        ? hexaAnyOf()
                        : hexaAnyOf(predicate1, predicate2);

        assertEquals(expectedResult, finalPredicate.test(t1, t2, t3, t4, t5, t6));
    }


    static Stream<Arguments> hexaIsNullTestCases() {
        Integer nullInt = null;
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        return Stream.of(
                //@formatter:off
                //            t1,        t2,        t3,          t4,        t5,        t6,        expectedResult
                Arguments.of( null,      null,      null,        null,      null,      null,      true ),
                Arguments.of( nullInt,   null,      null,        null,      null,      null,      true ),
                Arguments.of( null,      nullInt,   null,        null,      null,      null,      true ),
                Arguments.of( null,      null,      nullInt,     null,      null,      null,      true ),
                Arguments.of( null,      null,      null,        nullInt,   null,      null,      true ),
                Arguments.of( null,      null,      null,        null,      nullInt,   null,      true ),
                Arguments.of( null,      null,      null,        null,      null,      nullInt,   true ),
                Arguments.of( "ab",      null,      null,        null,      null,      null,      false ),
                Arguments.of( null,      "ab",      null,        null,      null,      null,      false ),
                Arguments.of( null,      null,      "ab",        null,      null,      null,      false ),
                Arguments.of( null,      null,      null,        "a",       null,      null,      false ),
                Arguments.of( null,      null,      null,        null,      "a",       null,      false ),
                Arguments.of( null,      null,      null,        null,      null,      "a",       false ),
                Arguments.of( "ab",      nullInt,   carbonara,   12,        4L,        "121",     false )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("hexaIsNullTestCases")
    @DisplayName("hexaIsNull: test cases")
    public <T1, T2, T3, T4, T5, T6> void hexaIsNull_testCases(T1 t1,
                                                              T2 t2,
                                                              T3 t3,
                                                              T4 t4,
                                                              T5 t5,
                                                              T6 t6,
                                                              boolean expectedResult) {
        assertEquals(expectedResult, hexaIsNull().test(t1, t2, t3, t4, t5, t6));
    }


    static Stream<Arguments> hexaNonNullTestCases() {
        Integer nullInt = null;
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        return Stream.of(
                //@formatter:off
                //            t1,        t2,        t3,          t4,        t5,        t6,        expectedResult
                Arguments.of( null,      null,      null,        null,      null,      null,      false ),
                Arguments.of( nullInt,   null,      null,        null,      null,      null,      false ),
                Arguments.of( null,      nullInt,   null,        null,      null,      null,      false ),
                Arguments.of( null,      null,      nullInt,     null,      null,      null,      false ),
                Arguments.of( null,      null,      null,        nullInt,   null,      null,      false ),
                Arguments.of( null,      null,      null,        null,      nullInt,   null,      false ),
                Arguments.of( null,      null,      null,        null,      null,      nullInt,   false ),
                Arguments.of( "ab",      null,      null,        null,      null,      null,      false ),
                Arguments.of( null,      "ab",      null,        null,      null,      null,      false ),
                Arguments.of( null,      null,      "ab",        null,      null,      null,      false ),
                Arguments.of( null,      null,      null,        "a",       null,      null,      false ),
                Arguments.of( null,      null,      null,        null,      "a",       null,      false ),
                Arguments.of( null,      null,      null,        null,      null,      "a",       false ),
                Arguments.of( "ab",      32F,       carbonara,   12,        4L,        "121",     true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("hexaNonNullTestCases")
    @DisplayName("hexaNonNull: test cases")
    public <T1, T2, T3, T4, T5, T6> void hexaNonNull_testCases(T1 t1,
                                                               T2 t2,
                                                               T3 t3,
                                                               T4 t4,
                                                               T5 t5,
                                                               T6 t6,
                                                               boolean expectedResult) {
        assertEquals(expectedResult, hexaNonNull().test(t1, t2, t3, t4, t5, t6));
    }


    static Stream<Arguments> heptaAllOfTestCases() {
        HeptaPredicate<Integer, Integer, Integer, Integer, Integer, Integer, Integer> allAreOdd = (t1, t2, t3, t4, t5, t6, t7) ->
                1 == t1 % 2 && 1 == t2 % 2 && 1 == t3 % 2 && 1 == t4 % 2 && 1 == t5 % 2 && 1 == t6 % 2 && 1 == t7 % 2;

        HeptaPredicate<Integer, Integer, Integer, Integer, Integer, Integer, Integer> allGreaterThan10 = (t1, t2, t3, t4, t5, t6, t7) ->
                10 < t1 && 10 < t2 && 10 < t3 && 10 < t4 && 10 < t5 && 10 < t6 && 10 < t7;

        return Stream.of(
                //@formatter:off
                //            t1,   t2,   t3,   t4,   t5,   t6,   t7,   predicate1,   predicate2,         expectedResult
                Arguments.of( 0,    5,    4,    9,    12,   19,   11,   null,         null,               true ),
                Arguments.of( 0,    5,    4,    9,    10,   22,   10,   allAreOdd,    null,               false ),
                Arguments.of( 11,   12,   14,   19,   22,   33,   20,   null,         allGreaterThan10,   true ),
                Arguments.of( 0,    5,    4,    12,   9,    14,   11,   allAreOdd,    allGreaterThan10,   false ),
                Arguments.of( 10,   15,   14,   9,    7,    4,    0,    allAreOdd,    allGreaterThan10,   false ),
                Arguments.of( 11,   15,   19,   21,   33,   37,   43,   allAreOdd,    allGreaterThan10,   true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("heptaAllOfTestCases")
    @DisplayName("heptaAllOf: test cases")
    public <T1, T2, T3, T4, T5, T6, T7> void heptaAllOf_testCases(T1 t1,
                                                                  T2 t2,
                                                                  T3 t3,
                                                                  T4 t4,
                                                                  T5 t5,
                                                                  T6 t6,
                                                                  T7 t7,
                                                                  HeptaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7> predicate1,
                                                                  HeptaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7> predicate2,
                                                                  boolean expectedResult) {
        HeptaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7> finalPredicate =
                null == predicate1 && null == predicate2
                        ? heptaAllOf()
                        : heptaAllOf(predicate1, predicate2);

        assertEquals(expectedResult, finalPredicate.test(t1, t2, t3, t4, t5, t6, t7));
    }


    static Stream<Arguments> heptaAlwaysFalseTestCases() {
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        return Stream.of(
                //@formatter:off
                //            t1,      t2,          t3,             t4,     t5,     t6,      t7,     expectedResult
                Arguments.of( null,    null,        null,           null,   null,   null,    null,   false ),
                Arguments.of( 1,       null,        null,           null,   null,   null,    null,   false ),
                Arguments.of( 10,      "rt",        null,           null,   null,   null,    null,   false ),
                Arguments.of( 10,      "rt",        carbonara,      null,   null,   null,    null,   false ),
                Arguments.of( 10,      "rt",        carbonara,      5L,     null,   null,    null,   false ),
                Arguments.of( 10,      "rt",        carbonara,      5L,     "123",  null,    null,   false ),
                Arguments.of( 10,      "rt",        carbonara,      5L,     "123",  5L,      null,   false ),
                Arguments.of( "abc",   carbonara,   Boolean.TRUE,   10,     4L,     "123",   12F,    false )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("heptaAlwaysFalseTestCases")
    @DisplayName("heptaAlwaysFalse: test cases")
    public <T1, T2, T3, T4, T5, T6, T7> void heptaAlwaysFalse_testCases(T1 t1,
                                                                        T2 t2,
                                                                        T3 t3,
                                                                        T4 t4,
                                                                        T5 t5,
                                                                        T6 t6,
                                                                        T7 t7,
                                                                        boolean expectedResult) {
        assertEquals(expectedResult, heptaAlwaysFalse().test(t1, t2, t3, t4, t5, t6, t7));
    }


    static Stream<Arguments> heptaAlwaysTrueTestCases() {
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        return Stream.of(
                //@formatter:off
                //            t1,      t2,          t3,             t4,     t5,     t6,      t7,     expectedResult
                Arguments.of( null,    null,        null,           null,   null,   null,    null,   true ),
                Arguments.of( 1,       null,        null,           null,   null,   null,    null,   true ),
                Arguments.of( 10,      "rt",        null,           null,   null,   null,    null,   true ),
                Arguments.of( 10,      "rt",        carbonara,      null,   null,   null,    null,   true ),
                Arguments.of( 10,      "rt",        carbonara,      5L,     null,   null,    null,   true ),
                Arguments.of( 10,      "rt",        carbonara,      5L,     "123",  null,    null,   true ),
                Arguments.of( 10,      "rt",        carbonara,      5L,     "123",  5L,      null,   true ),
                Arguments.of( "abc",   carbonara,   Boolean.TRUE,   10,     4L,     "123",   12F,    true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("heptaAlwaysTrueTestCases")
    @DisplayName("heptaAlwaysTrue: test cases")
    public <T1, T2, T3, T4, T5, T6, T7> void heptaAlwaysTrue_testCases(T1 t1,
                                                                       T2 t2,
                                                                       T3 t3,
                                                                       T4 t4,
                                                                       T5 t5,
                                                                       T6 t6,
                                                                       T7 t7,
                                                                       boolean expectedResult) {
        assertEquals(expectedResult, heptaAlwaysTrue().test(t1, t2, t3, t4, t5, t6, t7));
    }


    static Stream<Arguments> heptaAnyOfTestCases() {
        HeptaPredicate<Integer, Integer, Integer, Integer, Integer, Integer, Integer> allAreOdd = (t1, t2, t3, t4, t5, t6, t7) ->
                1 == t1 % 2 && 1 == t2 % 2 && 1 == t3 % 2 && 1 == t4 % 2 && 1 == t5 % 2 && 1 == t6 % 2 && 1 == t7 % 2;

        HeptaPredicate<Integer, Integer, Integer, Integer, Integer, Integer, Integer> allGreaterThan10 = (t1, t2, t3, t4, t5, t6, t7) ->
                10 < t1 && 10 < t2 && 10 < t3 && 10 < t4 && 10 < t5 && 10 < t6 && 10 < t7;

        return Stream.of(
                //@formatter:off
                //            t1,   t2,   t3,   t4,   t5,   t6,   t7,   predicate1,   predicate2,         expectedResult
                Arguments.of( 0,    5,    4,    9,    12,   19,   10,   null,         null,               false ),
                Arguments.of( 0,    5,    4,    9,    10,   22,   11,   allAreOdd,    null,               false ),
                Arguments.of( 11,   12,   14,   19,   22,   33,   15,   null,         allGreaterThan10,   true ),
                Arguments.of( 1,    5,    11,   15,   7,    17,   9,    allAreOdd,    allGreaterThan10,   true ),
                Arguments.of( 12,   15,   14,   18,   20,   32,   11,   allAreOdd,    allGreaterThan10,   true ),
                Arguments.of( 2,    15,   19,   8,    11,   37,   4,    allAreOdd,    allGreaterThan10,   false )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("heptaAnyOfTestCases")
    @DisplayName("heptaAnyOf: test cases")
    public <T1, T2, T3, T4, T5, T6, T7> void heptaAnyOf_testCases(T1 t1,
                                                                  T2 t2,
                                                                  T3 t3,
                                                                  T4 t4,
                                                                  T5 t5,
                                                                  T6 t6,
                                                                  T7 t7,
                                                                  HeptaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7> predicate1,
                                                                  HeptaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7> predicate2,
                                                                  boolean expectedResult) {
        HeptaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7> finalPredicate =
                null == predicate1 && null == predicate2
                        ? heptaAnyOf()
                        : heptaAnyOf(predicate1, predicate2);

        assertEquals(expectedResult, finalPredicate.test(t1, t2, t3, t4, t5, t6, t7));
    }


    static Stream<Arguments> heptaIsNullTestCases() {
        Integer nullInt = null;
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        return Stream.of(
                //@formatter:off
                //            t1,        t2,        t3,          t4,        t5,        t6,        t7,        expectedResult
                Arguments.of( null,      null,      null,        null,      null,      null,      null,      true ),
                Arguments.of( nullInt,   null,      null,        null,      null,      null,      null,      true ),
                Arguments.of( null,      nullInt,   null,        null,      null,      null,      null,      true ),
                Arguments.of( null,      null,      nullInt,     null,      null,      null,      null,      true ),
                Arguments.of( null,      null,      null,        nullInt,   null,      null,      null,      true ),
                Arguments.of( null,      null,      null,        null,      nullInt,   null,      null,      true ),
                Arguments.of( null,      null,      null,        null,      null,      nullInt,   null,      true ),
                Arguments.of( null,      null,      null,        null,      null,      null,      nullInt,   true ),
                Arguments.of( "ab",      null,      null,        null,      null,      null,      null,      false ),
                Arguments.of( null,      "ab",      null,        null,      null,      null,      null,      false ),
                Arguments.of( null,      null,      "ab",        null,      null,      null,      null,      false ),
                Arguments.of( null,      null,      null,        "a",       null,      null,      null,      false ),
                Arguments.of( null,      null,      null,        null,      "a",       null,      null,      false ),
                Arguments.of( null,      null,      null,        null,      null,      "a",       null,      false ),
                Arguments.of( null,      null,      null,        null,      null,      null,      "a",       false ),
                Arguments.of( "ab",      nullInt,   carbonara,   12,        4L,        "121",     51F,       false )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("heptaIsNullTestCases")
    @DisplayName("heptaIsNull: test cases")
    public <T1, T2, T3, T4, T5, T6, T7> void heptaIsNull_testCases(T1 t1,
                                                                   T2 t2,
                                                                   T3 t3,
                                                                   T4 t4,
                                                                   T5 t5,
                                                                   T6 t6,
                                                                   T7 t7,
                                                                   boolean expectedResult) {
        assertEquals(expectedResult, heptaIsNull().test(t1, t2, t3, t4, t5, t6, t7));
    }


    static Stream<Arguments> heptaNonNullTestCases() {
        Integer nullInt = null;
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        return Stream.of(
                //@formatter:off
                //            t1,        t2,        t3,          t4,        t5,        t6,        t7,        expectedResult
                Arguments.of( null,      null,      null,        null,      null,      null,      null,      false ),
                Arguments.of( nullInt,   null,      null,        null,      null,      null,      null,      false ),
                Arguments.of( null,      nullInt,   null,        null,      null,      null,      null,      false ),
                Arguments.of( null,      null,      nullInt,     null,      null,      null,      null,      false ),
                Arguments.of( null,      null,      null,        nullInt,   null,      null,      null,      false ),
                Arguments.of( null,      null,      null,        null,      nullInt,   null,      null,      false ),
                Arguments.of( null,      null,      null,        null,      null,      nullInt,   null,      false ),
                Arguments.of( null,      null,      null,        null,      null,      null,      nullInt,   false ),
                Arguments.of( "ab",      null,      null,        null,      null,      null,      null,      false ),
                Arguments.of( null,      "ab",      null,        null,      null,      null,      null,      false ),
                Arguments.of( null,      null,      "ab",        null,      null,      null,      null,      false ),
                Arguments.of( null,      null,      null,        "a",       null,      null,      null,      false ),
                Arguments.of( null,      null,      null,        null,      "a",       null,      null,      false ),
                Arguments.of( null,      null,      null,        null,      null,      "a",       null,      false ),
                Arguments.of( null,      null,      null,        null,      null,      null,      "a",       false ),
                Arguments.of( "ab",      32F,       carbonara,   12,        4L,        "121",     51F,       true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("heptaNonNullTestCases")
    @DisplayName("heptaNonNull: test cases")
    public <T1, T2, T3, T4, T5, T6, T7> void heptaNonNull_testCases(T1 t1,
                                                                    T2 t2,
                                                                    T3 t3,
                                                                    T4 t4,
                                                                    T5 t5,
                                                                    T6 t6,
                                                                    T7 t7,
                                                                    boolean expectedResult) {
        assertEquals(expectedResult, heptaNonNull().test(t1, t2, t3, t4, t5, t6, t7));
    }


    static Stream<Arguments> distinctByKeyTestCases() {
        PizzaDto carbonaraCheap = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        PizzaDto carbonaraExpense = new PizzaDto(CARBONARA.getInternalPropertyValue(), 10D);
        PizzaDto margheritaCheap = new PizzaDto(MARGUERITA.getInternalPropertyValue(), 5D);
        PizzaDto margheritaExpense = new PizzaDto(MARGUERITA.getInternalPropertyValue(), 10D);
        Function<PizzaDto, String> getName = PizzaDto::getName;
        Function<PizzaDto, Double> getCost = PizzaDto::getCost;
        return Stream.of(
                //@formatter:off
                //            initialCollection,                             keyExtractor,   expectedResult
                Arguments.of( List.of(),                                     getName,        List.of() ),
                Arguments.of( List.of(carbonaraCheap, carbonaraExpense),     getName,        List.of(carbonaraCheap) ),
                Arguments.of( List.of(carbonaraCheap, margheritaCheap),      getName,        List.of(carbonaraCheap, margheritaCheap) ),
                Arguments.of( List.of(margheritaCheap, margheritaExpense),   getCost,        List.of(margheritaCheap, margheritaExpense) ),
                Arguments.of( List.of(carbonaraCheap, margheritaCheap),      getCost,        List.of(carbonaraCheap) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("distinctByKeyTestCases")
    @DisplayName("distinctByKey: test cases")
    public void distinctByKey_testCases(List<PizzaDto> initialCollection,
                                        Function<PizzaDto, String> keyExtractor,
                                        List<PizzaDto> expectedResult) {
        List<PizzaDto> distinctCollection = initialCollection.stream()
                .filter(distinctByKey(keyExtractor))
                .toList();
        assertEquals(expectedResult, distinctCollection);
    }


    static Stream<Arguments> fromBiPredicateToMapEntryPredicateTestCases() {
        Map.Entry<String, Integer> emptyEntry = new AbstractMap.SimpleEntry<>(
                null,
                null
        );
        BiPredicate<Integer, String> predicate =
                (i, s) ->
                        null != i &&
                                0 == i % 2;
        return Stream.of(
                //@formatter:off
                //            entry,              predicate,   expectedResult
                Arguments.of( null,               null,        true ),
                Arguments.of( null,               predicate,   true ),
                Arguments.of( emptyEntry,         null,        true ),
                Arguments.of( emptyEntry,         predicate,   false ),
                Arguments.of( Map.entry(1, ""),   predicate,   false ),
                Arguments.of( Map.entry(2, ""),   predicate,   true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromBiPredicateToMapEntryPredicateTestCases")
    @DisplayName("fromBiPredicateToMapEntryPredicate: test cases")
    public <K, V> void fromBiPredicateToMapEntryPredicate_testCases(Map.Entry<? super K, ? super V> entry,
                                                                    BiPredicate<? super K, ? super V> predicate,
                                                                    boolean expectedResult) {
        // Required because sometimes the Java compiler is stupid
        Predicate<Map.Entry<K, V>> predicateToApply = fromBiPredicateToMapEntryPredicate(
                predicate
        );
        assertEquals(expectedResult, predicateToApply.test((Map.Entry<K, V>) entry));
    }


    static Stream<Arguments> getOrAlwaysFalsePredicateTestCases() {
        Predicate<Integer> greaterThan10 = i -> 0 < i.compareTo(10);
        return Stream.of(
                //@formatter:off
                //            predicate,       t,      expectedResult
                Arguments.of( null,            null,   false ),
                Arguments.of( null,            12,     false ),
                Arguments.of( greaterThan10,   9,      false ),
                Arguments.of( greaterThan10,   12,     true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrAlwaysFalsePredicateTestCases")
    @DisplayName("getOrAlwaysFalse: with Predicate test cases")
    public <T> void getOrAlwaysFalsePredicate_testCases(Predicate<? super T> predicate,
                                                        T t,
                                                        boolean expectedResult) {
        Predicate<T> predicateToApply = getOrAlwaysFalse(
                predicate
        );
        assertNotNull(predicateToApply);
        assertEquals(expectedResult, predicateToApply.test(t));
    }


    static Stream<Arguments> getOrAlwaysFalseBiPredicateTestCases() {
        BiPredicate<Integer, String> isIntegerGreaterThanTenAndStringLongerThan2 = (i, s) -> (10 < i) && (2 < s.length());
        return Stream.of(
                //@formatter:off
                //            predicate,                                     t,      e,       expectedResult
                Arguments.of( null,                                          null,   null,    false ),
                Arguments.of( null,                                          11,     null,    false ),
                Arguments.of( null,                                          null,   "ab",    false ),
                Arguments.of( null,                                          9,      "ab",    false ),
                Arguments.of( isIntegerGreaterThanTenAndStringLongerThan2,   9,      "abc",   false ),
                Arguments.of( isIntegerGreaterThanTenAndStringLongerThan2,   11,     "ab",    false ),
                Arguments.of( isIntegerGreaterThanTenAndStringLongerThan2,   11,     "abc",   true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrAlwaysFalseBiPredicateTestCases")
    @DisplayName("getOrAlwaysFalse: with BiPredicate test cases")
    public <T, E> void getOrAlwaysFalseBiPredicate_testCases(BiPredicate<? super T, ? super E> predicate,
                                                             T t,
                                                             E e,
                                                             boolean expectedResult) {
        BiPredicate<T, E> predicateToApply = getOrAlwaysFalse(
                predicate
        );
        assertNotNull(predicateToApply);
        assertEquals(expectedResult, predicateToApply.test(t, e));
    }


    static Stream<Arguments> getOrAlwaysTruePredicateTestCases() {
        Predicate<Integer> greaterThan10 = i -> 0 < i.compareTo(10);
        return Stream.of(
                //@formatter:off
                //            predicate,       t,      expectedResult
                Arguments.of( null,            null,   true ),
                Arguments.of( null,            12,     true ),
                Arguments.of( greaterThan10,   9,      false ),
                Arguments.of( greaterThan10,   12,     true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrAlwaysTruePredicateTestCases")
    @DisplayName("getOrAlwaysTrue: with Predicate test cases")
    public <T> void getOrAlwaysTruePredicate_testCases(Predicate<? super T> predicate,
                                                       T t,
                                                       boolean expectedResult) {
        Predicate<T> predicateToApply = getOrAlwaysTrue(
                predicate
        );
        assertNotNull(predicateToApply);
        assertEquals(expectedResult, predicateToApply.test(t));
    }


    static Stream<Arguments> getOrAlwaysTrueBiPredicateTestCases() {
        BiPredicate<Integer, String> isIntegerGreaterThanTenAndStringLongerThan2 = (i, s) -> (10 < i) && (2 < s.length());
        return Stream.of(
                //@formatter:off
                //            predicate,                                     t,      e,       expectedResult
                Arguments.of( null,                                          null,   null,    true ),
                Arguments.of( null,                                          11,     null,    true ),
                Arguments.of( null,                                          null,   "ab",    true ),
                Arguments.of( null,                                          9,      "ab",    true ),
                Arguments.of( isIntegerGreaterThanTenAndStringLongerThan2,   9,      "abc",   false ),
                Arguments.of( isIntegerGreaterThanTenAndStringLongerThan2,   11,     "ab",    false ),
                Arguments.of( isIntegerGreaterThanTenAndStringLongerThan2,   11,     "abc",   true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrAlwaysTrueBiPredicateTestCases")
    @DisplayName("getOrAlwaysTrue: with BiPredicate test cases")
    public <T, E> void getOrAlwaysTrueBiPredicate_testCases(BiPredicate<? super T, ? super E> predicate,
                                                            T t,
                                                            E e,
                                                            boolean expectedResult) {
        BiPredicate<T, E> predicateToApply = getOrAlwaysTrue(
                predicate
        );
        assertNotNull(predicateToApply);
        assertEquals(expectedResult, predicateToApply.test(t, e));
    }


    static Stream<Arguments> isNullTestCases() {
        Integer nullInt = null;
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        return Stream.of(
                //@formatter:off
                //            t,                  expectedResult
                Arguments.of( null,               true ),
                Arguments.of( nullInt,            true),
                Arguments.of( "noMatterString",   false ),
                Arguments.of( 12,                 false ),
                Arguments.of( carbonara,          false )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("isNullTestCases")
    @DisplayName("isNull: test cases")
    public <T> void isNull_testCases(T t,
                                     boolean expectedResult) {
        assertEquals(expectedResult, isNull().test(t));
    }


    static Stream<Arguments> nonNullTestCases() {
        Integer nullInt = null;
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        return Stream.of(
                //@formatter:off
                //            t,                  expectedResult
                Arguments.of( null,               false ),
                Arguments.of( nullInt,            false),
                Arguments.of( "noMatterString",   true ),
                Arguments.of( 12,                 true ),
                Arguments.of( carbonara,          true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("nonNullTestCases")
    @DisplayName("nonNull: test cases")
    public <T> void nonNull_testCases(T t,
                                      boolean expectedResult) {
        assertEquals(expectedResult, nonNull().test(t));
    }

}
