package com.spring5microservices.common.interfaces.predicate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TriPredicateTest {

    static Stream<Arguments> testTestCases() {
        TriPredicate<Integer, Integer, Integer> allAreOdd = (t1, t2, t3) ->
                1 == t1 % 2 && 1 == t2 % 2 && 1 == t3 % 2;

        TriPredicate<String, String, String> allLongerThan3 = (t1, t2, t3) ->
                3 < t1.length() && 3 < t2.length() && 3 < t3.length();

        return Stream.of(
                //@formatter:off
                //            t1,       t2,       t3,       function,         expectedResult
                Arguments.of( 0,        5,        4,        allAreOdd,        false ),
                Arguments.of( 1,        3,        5,        allAreOdd,        true ),
                Arguments.of( "A",      "BbrT",   "C",      allLongerThan3,   false ),
                Arguments.of( "ABCD",   "BbrT",   "1234",   allLongerThan3,   true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("testTestCases")
    @DisplayName("test: test cases")
    public <T1, T2, T3> void test_testCases(T1 t1,
                                            T2 t2,
                                            T3 t3,
                                            TriPredicate<T1, T2, T3> predicate,
                                            boolean expectedResult) {
        assertEquals(expectedResult, predicate.test(t1, t2, t3));
    }


    static Stream<Arguments> andTestCases() {
        TriPredicate<Integer, Integer, Integer> allAreOdd = (t1, t2, t3) ->
                1 == t1 % 2 && 1 == t2 % 2 && 1 == t3 % 2;

        TriPredicate<Integer, Integer, Integer> allGreaterThan10 = (t1, t2, t3) ->
                10 < t1 && 10 < t2 && 10 < t3;

        return Stream.of(
                //@formatter:off
                //            t1,   t2,   t3,   function,    other,              expectedException,            expectedResult
                Arguments.of( 0,    5,    4,    null,        null,               NullPointerException.class,   null ),
                Arguments.of( 11,   15,   19,   allAreOdd,   null,               NullPointerException.class,   null ),
                Arguments.of( 1,    20,   19,   allAreOdd,   allGreaterThan10,   null,                         false ),
                Arguments.of( 9,    21,   15,   allAreOdd,   allGreaterThan10,   null,                         false ),
                Arguments.of( 19,   21,   15,   allAreOdd,   allGreaterThan10,   null,                         true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("andTestCases")
    @DisplayName("and: test cases")
    public <T1, T2, T3> void and_testCases(T1 t1,
                                           T2 t2,
                                           T3 t3,
                                           TriPredicate<T1, T2, T3> predicate,
                                           TriPredicate<T1, T2, T3> other,
                                           Class<? extends Exception> expectedException,
                                           Boolean expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> predicate.and(other).test(t1, t2, t3));
        } else {
            assertEquals(expectedResult, predicate.and(other).test(t1, t2, t3));
        }
    }


    static Stream<Arguments> negateTestCases() {
        TriPredicate<Integer, Integer, Integer> allAreOdd = (t1, t2, t3) ->
                1 == t1 % 2 && 1 == t2 % 2 && 1 == t3 % 2;

        TriPredicate<String, String, String> allLongerThan3 = (t1, t2, t3) ->
                3 < t1.length() && 3 < t2.length() && 3 < t3.length();

        return Stream.of(
                //@formatter:off
                //            t1,       t2,       t3,       function,         expectedResult
                Arguments.of( 0,        5,        4,        allAreOdd,        true ),
                Arguments.of( 1,        3,        5,        allAreOdd,        false ),
                Arguments.of( "A",      "BbrT",   "C",      allLongerThan3,   true ),
                Arguments.of( "ABCD",   "BbrT",   "1234",   allLongerThan3,   false )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("negateTestCases")
    @DisplayName("negate: test cases")
    public <T1, T2, T3> void negate_testCases(T1 t1,
                                              T2 t2,
                                              T3 t3,
                                              TriPredicate<T1, T2, T3> predicate,
                                              boolean expectedResult) {
        assertEquals(expectedResult, predicate.negate().test(t1, t2, t3));
    }


    static Stream<Arguments> orTestCases() {
        TriPredicate<Integer, Integer, Integer> allAreOdd = (t1, t2, t3) ->
                1 == t1 % 2 && 1 == t2 % 2 && 1 == t3 % 2;

        TriPredicate<Integer, Integer, Integer> allGreaterThan10 = (t1, t2, t3) ->
                10 < t1 && 10 < t2 && 10 < t3;

        return Stream.of(
                //@formatter:off
                //            t1,   t2,   t3,   function,    other,              expectedException,            expectedResult
                Arguments.of( 0,    5,    4,    null,        null,               NullPointerException.class,   null ),
                Arguments.of( 11,   15,   19,   allAreOdd,   null,               NullPointerException.class,   null ),
                Arguments.of( 1,    20,   19,   allAreOdd,   allGreaterThan10,   null,                         false ),
                Arguments.of( 9,    7,    13,   allAreOdd,   allGreaterThan10,   null,                         true ),
                Arguments.of( 12,   20,   16,   allAreOdd,   allGreaterThan10,   null,                         true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("orTestCases")
    @DisplayName("or: test cases")
    public <T1, T2, T3> void or_testCases(T1 t1,
                                          T2 t2,
                                          T3 t3,
                                          TriPredicate<T1, T2, T3> predicate,
                                          TriPredicate<T1, T2, T3> other,
                                          Class<? extends Exception> expectedException,
                                          Boolean expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> predicate.or(other).test(t1, t2, t3));
        } else {
            assertEquals(expectedResult, predicate.or(other).test(t1, t2, t3));
        }
    }

}
